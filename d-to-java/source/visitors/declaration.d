module visitors.declaration;

import core.stdc.string;

import ds.buffer, ds.stack, ds.identity_map;

import dmd.aggregate;
import dmd.arraytypes;
import dmd.attrib;
import dmd.cond;
import dmd.dclass;
import dmd.denum;
import dmd.dimport;
import dmd.dmodule;
import dmd.dstruct;
import dmd.dtemplate;
import dmd.expression;
import dmd.declaration;
import dmd.dsymbol;
import dmd.func;
import dmd.id;
import dmd.identifier;
import dmd.init;
import dmd.mtype;
import dmd.statement;
import dmd.staticassert;
import dmd.tokens;
import dmd.visitor : Visitor, SemanticTimeTransitiveVisitor;
import dmd.root.array, dmd.root.rootobject;

import std.array, std.algorithm, std.format, std.string, std.range, std.stdio;

import visitors.expression, visitors.members, visitors.passed_by_ref, visitors.templates;

alias toJava = visitors.expression.toJava; 

///
string toJava(Module mod, ToJavaModuleVisitor v) {
    auto id = mod.ident.toString.idup;
    v.moduleName = id.endsWith(".d") ? id[0..$-2] : id;
    v.onModuleStart(mod);
    mod.accept(v);
    v.onModuleEnd();
    return v.result;
}

struct Goto {
    Expression case_;
    bool default_;
    LabelDsymbol label;
    bool local;
}

Goto[] collectGotos(Statement s) {
    extern(C++) static class Collector : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        Goto[] gotos;

        override void visit(ConditionalDeclaration ver) {
            if (ver.condition.inc == Include.yes) {
                if (ver.decl) {
                    foreach(d; *ver.decl){
                        d.accept(this);
                    }
                }
            }
            else if(ver.elsedecl) {
                foreach(d; *ver.elsedecl){
                    d.accept(this);
                }
            }
        }

        override void visit(GotoDefaultStatement ){
            gotos ~= Goto(null, true, null);
        }

        override void visit(GotoCaseStatement case_) {
            if (case_.exp && !gotos.canFind!(c => c.case_ is case_.exp)) {
                gotos ~= Goto(case_.exp, false, null);
            }
        }

        override void visit(ExpStatement e){}

        override void visit(GotoStatement goto_) {
            if (!gotos.canFind!(g => g.label is goto_.label))
                gotos ~= Goto(null,false,goto_.label);
        }
    }
    extern(C++) class MarkLocals : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        Goto[] gotos;
        override void visit(LabelStatement label){
            foreach(ref g; gotos) {
                if (g.label  && g.label.ident == label.ident)
                    g.local = true;
            }
            //stderr.writefln("%s %s", label.ident.toString, r);
            //if (!r.empty) r[0].local = true;
        }
    }
    scope v = new Collector();
    s.accept(v);
    scope v2 = new MarkLocals();
    v2.gotos = v.gotos;
    s.accept(v2);
    v2.gotos.sort!((a,b) => cast(int)a.local > cast(int)b.local);
    return v2.gotos;
}

bool hasCtor(AggregateDeclaration agg) {
    extern(C++) static class HasCtorVisitor : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        bool hasCtor = false;

        override void visit(CtorDeclaration) {
            hasCtor = true;
        }

        override void visit(Statement) {} // do shallow visit
    }
    scope v = new HasCtorVisitor();
    agg.accept(v);
    return v.hasCtor;
}

FuncExp[] collectLambdas(Statement s) {
    extern(C++) static class Lambdas : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        IdentityMap!bool exps;

        override void visit(FuncExp e) {
            exps[e] = true;
        }
    }
    scope v = new Lambdas();
    s.accept(v);
    return cast(FuncExp[])v.exps.keys();
}

AggregateDeclaration[] collectNestedAggregates(FuncDeclaration f) {
    extern(C++) static class Aggregates : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        AggregateDeclaration[] decls;

        override void visit(ClassDeclaration cd) {
            decls ~= cd;
        }

        override void visit(StructDeclaration sd) {
            decls ~= sd;
        }
    }
    scope v = new Aggregates();
    f.accept(v);
    return v.decls;
}

extern(C) void foobar(int) {}

VarDeclaration varargVarDecl(FuncDeclaration decl) {
    extern(C++) static class VarArg : SemanticTimeTransitiveVisitor {
        alias visit = typeof(super).visit;
        VarDeclaration var;

        override void visit(CallExp e) {
            if (e.f && e.f.ident.symbol == "va_start") {
                auto ve = (*e.arguments)[0].isVarExp();
                var = ve.var.isVarDeclaration();
            }
        }
    }
    scope v = new VarArg();
    decl.accept(v);
    return v.var;
}

extern (C++) class ToJavaModuleVisitor : SemanticTimeTransitiveVisitor {
    alias visit = typeof(super).visit;
    TextBuffer buf;
    TextBuffer header;
    string defAccess = "public";
    Stack!(bool[string]) generatedFunctions;
    bool[string] generatedClasses;
    string moduleName;
    string[] constants; // all local static vars are collected here
    bool[string] imports; // all imports for deduplication

    ExprOpts opts; // packs all information required for exp visitor

    int testCounter;
    
    Stack!int dispatch;
    int dispatchCount;

    Stack!int forLoop;
    int forCount;

    bool hasEmptyCtor;

    Stack!(Goto[]) gotos;
    Stack!(IdentityMap!Statement) unrolledGotos; // goto that may be unrolled
    IdentityMap!bool unrolling; // keep track of unrolling process to avoid infinite recursion
    Stack!(IdentityMap!int) labelGotoNums;
    
    int inInitializer; // to avoid recursive decomposition of arrays
    string[] arrayInitializers;

    bool noTiargs; // used for mixin templates
    Stack!TemplateInstance currentInst;

    string nameOf(AggregateDeclaration agg) {
        auto tmpl = agg in opts.templates;
        return format("%s%s", agg.ident.symbol, tmpl ? tmpl.str : "");
    }

    Template tiArgs() {
        return currentInst.length && !noTiargs ? .tiArgs(currentInst.top, opts) : Template.init;
    }

    string typeOf(Type t, Boxing boxing = Boxing.no) {
        addImportForType(t);
        return toJava(t, opts, boxing);
    }

    string typeCons(Type type) {
        auto tc = typeOf(type);
        return tc.startsWith("Slice") ? "Raw"~tc : tc;
    }

    string refTypeOf(Type t) {
        addImportForType(t);
        return refType(t, opts);
    }

    void addImport(Array!Identifier* packages, Identifier id) {
        if (id == Id.object)
            return; // object is imported by default
        scope temp = new TextBuffer();
        if (packages && packages.dim)
        {
            foreach (const pid; *packages) 
            {
                if (pid.toString() == "root") return;
            }
            if((*packages)[0].toString() != "dmd") return;
            temp.put("import static org.dlang.");
            foreach (const pid; *packages)
            {
                temp.fmt("%s.", pid.toString());
            }
        }
        temp.fmt("%s.*;\n", id.toString(), id.toString());
        imports[temp.data.idup] = true;
    }

    string funcSig(FuncDeclaration func) {
        auto b = new TextBuffer();
        b.put(typeOf(func.type.nextOf));
        b.put(" ");
        b.put(func.ident.symbol);
        auto tf = func.type.isTypeFunction();
        if(tf.parameterList)
            foreach(i, p; *tf.parameterList){
                if (i) b.put(", ");
                auto box = p.storageClass & (STC.ref_ | STC.out_);
                if (box && !p.type.isTypeStruct) {
                    b.fmt("%s", refTypeOf(p.type));
                }
                else b.fmt("%s", typeOf(p.type));
            }
        if (auto ti = func in opts.templates) b.put(ti.str);
        return b.data.dup;
    }

    VarDeclaration hoistVarFromIf(Statement st) {
        if (auto ifs = st.isIfStatement()) {
            if (auto c = ifs.condition.isCommaExp()) {
                auto var = c.e1.isDeclarationExp().declaration.isVarDeclaration();
                return var;
            }
        }
        return null;
    }

    void onModuleStart(Module mod){
        buf = new TextBuffer();
        header = new TextBuffer();
        opts.currentMod = mod;
        constants = null;
        arrayInitializers = null;
        imports = null;
        testCounter = 0;
        forCount = 0;
        dispatchCount = 0;
        if (generatedFunctions.length != 1) {
            generatedFunctions.clear;
            generatedFunctions.push(null);
        }
        generatedClasses = null;
        if (gotos.length != 1) gotos.push(null);
        if (opts.erasuresCount.length != 1) opts.erasuresCount.push(null);

        header.put("package org.dlang.dmd;\n");
        header.put("import kotlin.jvm.functions.*;\n");
        header.put("\nimport org.dlang.dmd.root.*;\n");
        header.put("import static org.dlang.dmd.root.filename.*;\n");
        header.put("import static org.dlang.dmd.root.File.*;\n");
        header.put("import static org.dlang.dmd.root.ShimsKt.*;\n");
        header.put("import static org.dlang.dmd.root.SliceKt.*;\n");
        header.put("import static org.dlang.dmd.root.DArrayKt.*;\n");
        buf.indent;
        buf.put("\n");
    }

    ///
    void onModuleEnd() {
        auto imps = sort(imports.keys).array;
        foreach(imp; imps) {
            header.put(imp);
        }
        header.fmt("\npublic class %s {\n", moduleName);
        header.indent;
        foreach (i, v; arrayInitializers) {
            header.fmt("%s;\n", v);
        }
        if (constants.length)  {
            foreach(var; constants) {
                header.put(var);
            }
        }
        header.outdent;
        buf.outdent;
        buf.fmt("}\n");
    }

    void processLambdas(Statement st) {
        auto lambdas = collectLambdas(st);
        schwartzSort!(x => x.fd.funcName)(lambdas);
        foreach (i, v; lambdas)  {
            auto sig = funcSig(v.fd);
            opts.localFuncs[v.fd] = true;
            if (sig !in generatedFunctions.top) {
                auto _ = pushed(opts.funcs, v.fd);
                printLocalFunction(v.fd, true);
                generatedFunctions.top[sig] = true;
            }
        }
    }

    override void visit(ConditionalDeclaration ver) {
        if (ver.condition.inc == Include.yes) {
            if (ver.decl) {
                buf.put("\n");
                foreach(d; *ver.decl){
                    d.accept(this);
                }
            }
        }
        else if(ver.elsedecl) {
            foreach(d; *ver.elsedecl){
                d.accept(this);
            }
        }
    }

    void printSArray(Type type, TextBuffer sink) {
        auto st = cast(TypeSArray)type;
        auto tc = typeCons(type);
        sink.fmt("new %s(new %s[%s])", tc, typeOf(type.nextOf), st.dim.toJava(opts));
    }
   
    extern(D) private void printVar(VarDeclaration var, const(char)[] ident, TextBuffer sink) {
        // remove var-args decls
        if (opts.funcs.length && opts.vararg) {
            if (var.ident.symbol == "_arguments") return;
            if (var is opts.vararg) return;
        }
        bool staticInit = var.isStatic() || (var.storage_class & STC.gshared) || (opts.funcs.empty && opts.aggregates.empty);
        bool refVar = (var in opts.refParams.top) != null;
        Type t = var.type;
        if(var._init && var._init.kind == InitKind.array && var.type.ty == Tpointer)
            t = var.type.nextOf.arrayOf;
        auto type = refVar ? refTypeOf(var.type) : typeOf(t);
        auto access = "";
        if (opts.aggregates.length && !opts.funcs.length) access = defAccess ~ " ";
        auto ti = opts.funcs.length ? "" : tiArgs.str;
        sink.fmt("%s%s%s %s%s",  access, staticInit ? "static " : "", type, ident, ti);
        sink.fmt(" = ");
        if (refVar) sink.fmt("ref(");
        if (var._init) {
            ExpInitializer ie = var._init.isExpInitializer();
            if (ie && (ie.exp.op == TOK.construct || ie.exp.op == TOK.blit)) {
                auto assign = (cast(AssignExp)ie.exp);
                auto integer = assign.e2.isIntegerExp();
                auto isNull = assign.e2.isNullExp();
                bool needPCopy(Expression e) {
                    return e.type.ty == Tpointer && !e.isNullExp && e.type.nextOf.ty != Tstruct;
                }
                bool needCopy(Expression e) {
                    return e.type.ty == Tstruct || e.type.ty == Tarray;
                }
                //fprintf(stderr, "init %s with %s\n", var.toChars, assign.e2.toChars);
                if (needPCopy(assign.e2)) sink.put("pcopy(");
                //stderr.writefln("Init1 %s integer = %s null = %s", var, integer, isNull);
                if (integer && integer.toInteger() == 0 && var.type.ty == Tstruct){
                    sink.fmt("new %s()", typeOf(var.type));
                }
                else if(integer && integer.toInteger() == 0 && var.type.ty == Tsarray) {
                    printSArray(var.type, sink);
                }
                else if(var.type.ty == Tarray && isNull) {
                    sink.fmt("new %s()", typeCons(var.type));
                }
                else {
                    sink.put(assign.e2.toJava(opts));
                }
                if (needCopy(assign.e2))  sink.put(".copy()");
                if (needPCopy(assign.e2)) sink.put(")");
                
            }
            else {
                //stderr.writefln( "Init2 %s", var);
                initializerToBuffer(var._init, sink, opts);
            }
        }
        else if (var.type.ty == Tpointer || var.type.ty == Tclass) {
            sink.fmt("null");
        }
        else if(var.type.ty == Tsarray) {
            printSArray(var.type, sink);
        }
        else if (var.type.ty == Tstruct || var.type.ty == Taarray || var.type.ty == Tarray) {
            sink.fmt("new %s()", typeCons(var.type));
        }
        else if (var.type.ty == Tbool) {
            sink.fmt("false");
        }
        else if (var.type.ty == Tint64 || var.type.ty == Tuns64) {
            sink.fmt("0L");
        }
        else if(var.type.isintegral) {
            sink.fmt("0");
        }
        else if(var.type.isfloating) {
            sink.fmt("0.0");
        }
        if (refVar) sink.fmt(")");
        sink.fmt(";\n");
    }

    override void visit(AnonDeclaration anon)
    {
        auto members = collectMembers(anon);
        VarDeclaration[string] visited;
        //stderr.writefln("UNION members: %s", members.all);
        foreach (m; members.all) {
            if (auto root = typeOf(m.type) in visited) {
                opts.aliasedUnion[m] = *root;
            }
            else {
                visited[typeOf(m.type)] = m;
            }
        }
        foreach (m; members.all) {
            if (m !in opts.aliasedUnion)
                (cast(VarDeclaration)m).accept(this);
        }
    }

    override void visit(VarDeclaration var) {
        if (var.type is null) {
            stderr.writefln("NULL TYPE VAR: %s", var.ident.symbol);
            return;
        }
        addImportForType(var.type);
        if (var.type.toJava(opts).startsWith("TypeInfo_")) return;
        bool pushToGlobal = (var.isStatic() || (var.storage_class & STC.gshared)) && !opts.funcs.empty;
        if (pushToGlobal) {
            auto temp = new TextBuffer();
            const(char)[] id = opts.funcs.top.funcName ~ var.ident.symbol;
            printVar(var, id, temp);
            constants ~= temp.data.idup;
            opts.renamed[var] = format("%s.%s", moduleName, id);
        }
        else {
            if (auto name = var in opts.renamed)
                printVar(var, *name, buf);
            else 
                printVar(var, var.ident.symbol, buf);
        }
    }

    override void visit(ExpStatement s)
    {
        processLambdas(s);
        if (s.exp && s.exp.op == TOK.declaration &&
            (cast(DeclarationExp)s.exp).declaration)
        {
            (cast(DeclarationExp)s.exp).declaration.accept(this);
        }
        else if (s.exp) {
            auto text = s.exp.toJava(opts);
            if (text.length) {
                bool needsWrap = (s.exp.type.ty == Tbool && s.exp.op == TOK.orOr) || s.exp.op == TOK.question;
                if (needsWrap) buf.put("expr(");
                buf.put(text);
                if (needsWrap) buf.put(")");
                buf.put(";\n");
            }
        }
    }

    override void visit(ScopeStatement s)
    {
        if (s.statement)  {
            buf.put("{\n");
            buf.indent;
                s.statement.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(CompoundStatement s)
    {
        static struct Range {
            long first = int.max;
            long last = int.min;
            bool reversed = false;
        }
        auto labels = s.statements ? 
            (*s.statements)[]
                .map!(s => s ? s.isLabelStatement() : null)
                .filter!(x => x).array 
            : null;
        auto range = new Range[labels.length];
        if (gotos.top.length == 0) { // do not use try/catch mechanism inside of switches
            if (s.statements) {
                // top-level non-local gotos
                foreach(k, lbl; labels) {
                    labelGotoNums.top[lbl.ident] = cast(int)k;
                    foreach (i, st; *s.statements) if (st) {
                        auto gs = collectGotos(st);
                        auto nonLocalGotos = gs.filter!(g => !g.local);
                        if (!nonLocalGotos.empty){
                            if (nonLocalGotos.canFind!(g => g.label && g.label.ident == lbl.ident)) {
                                auto target = (*s.statements)[].countUntil!(x => x is lbl);
                                if (range[k].first > i) range[k].first = i;
                                if (range[k].last < target) range[k].last = target;
                            }
                        }
                    }
                    if (range[k].first > range[k].last) range[k].reversed = true;
                }
            }
        }
        // unroll gotos inside of if/else chains - a typical pattern s
        {
            Goto[] allGotos = collectGotos(s).filter!(g => g.label).array;
            auto labeled = allGotos.map!(x => cast(Statement)x.label.statement.statement).enumerate.array;
            void recurse(Statement st) {
                if (auto ifst = st.isIfStatement) {
                    auto cnt = labeled.filter!(lbl => ifst.containsStatement(lbl[1]));
                    foreach(i, c; cnt) {
                        // stderr.writefln("FOUND %s", i);
                        unrolledGotos.top[allGotos[i].label.ident] = ifst;
                    }
                    if (ifst.elsebody) {
                        recurse(ifst.elsebody);
                    }
                }
            }
            foreach (st; *s.statements) {
                if (st)
                    recurse(st);
            }
        }
        bool addedStart = false;
        if (s.statements) {
            ptrdiff_t[] totalRevs = [];
            foreach (idx, st; *s.statements) if (st) {
                auto starts = range.filter!(x => x.first == idx && !x.reversed);
                if(!starts.empty && !addedStart) {
                    addedStart = true;
                    foreach (start; range.filter!(x => !x.reversed)) {
                        buf.put("try {\n");
                        buf.indent;
                    }
                }
                auto revEnds = range.filter!(x => x.last == idx && x.reversed);
                foreach (rev; revEnds) {
                    buf.put("while(true) try {\n");
                    buf.indent;
                    totalRevs ~= range.countUntil(rev);
                }
                // try to find target on this level, if fails we are too deep
                // some other (upper) check will eventually succeed
                auto end = range.countUntil!(x => x.last == idx && !x.reversed);
                if (end >= 0) {
                    buf.outdent;
                    buf.fmt("}\ncatch(Dispatch%d __d){}\n", end);
                }
                if (idx == s.statements.length-1 && st.isBreakStatement) {
                    foreach (rev; totalRevs) {
                        buf.put("break;\n");
                        buf.outdent;
                        buf.fmt("} catch(Dispatch%d __d){}\n", rev);
                    }
                }
                st.accept(this);
                if (idx == s.statements.length-1 && !st.isBreakStatement) 
                    foreach (rev; totalRevs) {
                        buf.put("break;\n");
                        buf.outdent;
                        buf.fmt("} catch(Dispatch%d __d){}\n", rev);
                    }
                //TODO: for?
            }
        }
    }

    override void visit(WhileStatement s)
    {
        assert(0);
        /*buf.put("while (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(")\n");
        if (s._body)
            s._body.accept(this);*/
    }

    override void visit(DoStatement s)
    {
        if (collectGotos(s._body).length > 0) {
            buf.outdent;
            forLoop.push(++forCount);
            buf.fmt("L_outer%d:\n", forCount);
            buf.indent;
        }
        else 
            forLoop.push(0); // no gotos, let it continue this inner for
        scope(exit) forLoop.pop();
        buf.put("do {\n");
        buf.indent;
        if (s._body)
            s._body.accept(this);
        buf.outdent;
        buf.put("} while (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(");\n");
    }

    override void visit(ForStatement s)
    {
        if (collectGotos(s._body).length > 0) {
            buf.outdent;
            forLoop.push(++forCount);
            buf.fmt("L_outer%d:\n", forCount);
            buf.indent;
        }
        else 
            forLoop.push(0); // no gotos, let it continue this inner for
        scope(exit) forLoop.pop();
        buf.put("for (");
        if (s._init)
        {
            s._init.accept(this);
        }
        buf.put("; ");
        if (s.condition)
        {
            buf.put(s.condition.toJavaBool(opts));
        }
        buf.put(";");
        if (s.increment)
        {
            buf.put(s.increment.toJava(opts));
        }
        buf.put(')');
        if (s._body && !s._body.isScopeStatement())
        {
            buf.put(" {\n");
            buf.indent;
            if (s._body) {
                s._body.accept(this);
            }
            buf.outdent;
            buf.put("}\n");
        }
        else if (s._body) {
            s._body.accept(this);
        }
    }

    override void visit(ForeachStatement s)
    {
        assert(false); // has been lowered
    }

    override void visit(IfStatement s)
    {
        auto var = hoistVarFromIf(s);
        if (var) {
            buf.put("{\n");
            buf.indent;
            var.accept(this);
        }
        scope(exit) if (var) {
            buf.outdent;
            buf.put("}\n");
        }
        buf.put("if (");
        buf.put(s.condition.toJavaBool(opts));
        buf.put(")\n");
        if (s.ifbody.isScopeStatement())
        {
            s.ifbody.accept(this);
        }
        else
        {
            buf.put("{\n");
            buf.indent;
            s.ifbody.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
        if (s.elsebody)
        {
            buf.put("else");
            if (!s.elsebody.isIfStatement())
            {
                buf.put('\n');
            }
            else
            {
                buf.put(' ');
            }
            if (s.elsebody.isScopeStatement() || s.elsebody.isIfStatement())
            {
                s.elsebody.accept(this);
            }
            else
            {
                buf.put("{\n");
                buf.indent;
                s.elsebody.accept(this);
                buf.outdent;
                buf.put("}\n");
            }
        }
    }

    override void visit(TemplateMixin mix) {
        buf.fmt("// from template mixin %s\n", mix.toString);
        noTiargs = true;
        scope(exit) noTiargs = false;
        auto _ = pushed(currentInst, mix);
        visit(cast(TemplateInstance)mix);
    }

    override void visit(TemplateDeclaration td) {
        if (td.ident.symbol.startsWith("RTInfo")) return;
        auto instances = td.instances.values;
        schwartzSort!(x => (x.ident ? x.ident.symbol : "") ~ .tiArgs(x, opts).str)(instances);
        foreach(inst; instances) {
            auto _ = pushed(currentInst, inst);
            inst.accept(this);
        }
    }

    override void visit(TemplateInstance ti) {
        if (currentInst.empty || !currentInst.top) return;
        if (ti.tiargs) {
            auto decl = ti.tempdecl.isTemplateDeclaration();
            foreach(m; *ti.members) {
                buf.fmt("// from template %s!(%s)\n", ti.name.symbol, .tiArgs(ti, opts).str);
                m.accept(this);
                buf.put("\n");
            }
        }
    }

    override void visit(StaticAssert s)
    {
        // stderr.writefln("StaticAssert: %s\n", s.toString());
        // ignore and do not recurse into
    }

    override void visit(StaticCtorDeclaration ctor)
    {
        buf.put("static {\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
    }

    override void visit(SharedStaticCtorDeclaration ctor)
    {
        auto _ = pushed(opts.funcs, ctor);
        buf.put("static {\n");
        buf.indent;
        if (ctor.fbody)
            ctor.fbody.accept(this);
        buf.outdent;
        buf.put("}\n");
    }

    override void visit(Import imp)
    {
        addImport(imp.packages, imp.id);
    }

    override void visit(CompileDeclaration compile)
    {
        foreach (e; *compile.exps) {
            auto se = cast(StringExp)e.ctfeInterpret();
            auto s = se.string[0..se.len];
            s = s.replace("Identifier", "static Identifier"); //hack
            buf.put(s);
            buf.put("\n");
        }
    }

    override void visit(EnumDeclaration d)
    {
        auto old = buf;
        auto oldAccess = defAccess;
        if (opts.funcs.length) {
            buf = new TextBuffer();
            defAccess = "private";
        }
        auto oldInEnumDecl = opts.inEnumDecl;
        scope(exit) opts.inEnumDecl = oldInEnumDecl;
        opts.inEnumDecl = d;
        if (d.ident)
        {
            buf.fmt("\n%s static class ", defAccess);
            buf.put(symbol(d.ident));
            buf.put(' ');
        }
        if (!d.members)
        {
            buf.put(';');
            buf.put('\n');
            return;
        }
        buf.put('\n');
        if (d.ident) {
            buf.put('{');
            buf.put('\n');
            buf.indent;
        }
        foreach (em; *d.members)
        {
            if (!em)
                continue;
            em.accept(this);
        }
        if (d.ident) {
            buf.outdent;
            buf.put("}\n\n");
        }
        if (opts.funcs.length) {
            constants ~= buf.data.dup;
            buf = old;
            defAccess = oldAccess;
        }
    }

    override void visit(EnumMember em)
    {
        if (em.value)
        {
            buf.fmt("public static final %s %s = %s;\n", 
                typeOf(em.type), em.ident.symbol, em.value.toJava(opts));
        }
    }

    override void visit(SwitchStatement s)
    {
        auto gt = pushed(gotos, collectGotos(s));
        //if (gotos.length) stderr.writefln("GOTOS: %s", gotos);
        auto _d = pushed(dispatch, dispatchCount++);
        if (gotos.top.length) {
            buf.put("{\n");
            buf.indent;
            buf.fmt("int __dispatch%d = 0;\n", dispatch.top);
            buf.fmt("dispatched_%d:\n", dispatch.top);
            buf.put("do {\n");
            buf.indent;
        }
        disambiguateVars(s, opts.renamed);
        auto cond = s.condition.toJava(opts);
        if (s.condition.type.toJava(opts) == "byte") {
            cond = "(" ~ cond ~" & 0xFF)";
        }
        buf.put("switch (");
        if (gotos.top.length) {
            buf.fmt("__dispatch%d != 0 ? __dispatch%d : %s", 
                dispatch.top, dispatch.top, cond);
        }
        else {
            if (s.condition.type.ty == Tint64 || s.condition.type.ty == Tuns64)
                buf.put("(int)");
            buf.put(cond);
        }
        buf.put(')');
        buf.put('\n');
        if (s._body)
        {
            if (!s._body.isScopeStatement())
            {
                //stderr.writefln("SWITCH2 %s \n", cond);
                buf.put('{');
                buf.put('\n');
                buf.indent;
                if (auto comp = s._body.isCompoundStatement()) {
                    foreach(st; *comp.statements) {
                        if (auto scst = st.isScopeStatement()) scst.statement.accept(this);
                        else st.accept(this);
                    }
                }
                else
                    s._body.accept(this);
                buf.outdent;
                buf.put('}');
                buf.put('\n');
            }
            else
            {
                //stderr.writefln("SWITCH %s \n", cond);
                s._body.accept(this);
            }
        }
        if (gotos.top.length) {
            buf.outdent;
            buf.fmt("} while(__dispatch%d != 0);\n", dispatch.top);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(WithStatement w)
    {
        if (auto ss = w._body.isScopeStatement) {
            if (dispatch.length > 0 && ss.statement)
                return ss.statement.accept(this);
        }
        w._body.accept(this);
    }

    override void visit(CaseStatement s)
    {
        buf.put("case ");
        if (s.exp.type.ty == Tuns64 || s.exp.type.ty == Tint64)
        buf.put("(int)");
        buf.put(s.exp.toJava(opts));
        buf.put(':');
        buf.put('\n');
        buf.indent;
        Statement st = s.statement;
        ScopeStatement ss;
        if (gotos.top.canFind!(g => g.case_ && g.case_.toInteger == s.exp.toInteger)) {
            buf.fmt("__dispatch%d = 0;\n", dispatch.top);
        }
        while (st && ((ss = st.isScopeStatement()) !is null)) {
            st = ss.statement;
        }
        if (st) st.accept(this);
        buf.outdent;
    }

    override void visit(CaseRangeStatement s)
    {
        buf.put("case ");
        buf.put(s.first.toJava(opts));
        buf.put(": .. case ");
        buf.put(s.last.toJava(opts));
        buf.put(':');
        buf.put('\n');
        s.statement.accept(this);
    }

    override void visit(DefaultStatement s)
    {
        buf.put("default:\n");
        if (gotos.top.canFind!(g => g.default_)) {
            buf.fmt("__dispatch%d = 0;\n", dispatch.top);
        }
        Statement st = s.statement;
        ScopeStatement ss;
        while (st && ((ss = st.isScopeStatement()) !is null)) {
            st = ss.statement;
        }
        if (st) st.accept(this);
    }

    override void visit(LabelStatement label) {
        buf.outdent;
        buf.fmt("/*%s:*/\n", label.ident.symbol);
        long myIndex = gotos.top.countUntil!(c => c.label && c.label.ident == label.ident);
        if (myIndex >= 0 && gotos.top[myIndex].local && dispatch.top > 0) {
            buf.fmt("case %d:\n__dispatch%d = 0;\n", -1-myIndex, dispatch.top);
        }
        buf.indent;
        super.visit(label);
    }

    override void visit(GotoStatement g) {
        buf.fmt("/*goto %s*/", g.label.toString);
        auto myIndex = map!(gs => gs.countUntil!(c => c.label is g.label))(gotos[]);
        auto stackIndex = myIndex.countUntil!(x => x >= 0);
        auto idx = stackIndex >= 0 ? myIndex[stackIndex] : -1;
        if (idx >= 0 && gotos[][stackIndex][idx].local) {
            //stderr.writefln("StackIdx = %d idx = %d dispatch = %s gotos = %s", stackIndex, idx, dispatch[], gotos[]);
            // gotos have empty array added at start so -1
            buf.fmt("{ __dispatch%d = %d; continue dispatched_%d; }\n",
                dispatch[][stackIndex - 1], -1-idx, dispatch[][stackIndex - 1]);
        }
        else if (auto count = g.label.ident in labelGotoNums.top){
            buf.fmt("throw Dispatch%d.INSTANCE;\n", *count);
        }
        else if (auto ifs = g.label.ident in unrolledGotos.top) {
            // if we are already unrolling this goto...
            if (auto mark = g.label.ident in unrolling) {
                if (*mark) {
                    unrolling[g.label.ident] = false;
                    return;
                }
            }
            auto saved = buf.copy;
            // attempt to unroll, restore buffer on failure
            unrolling[g.label.ident] = true;
            scope(exit) unrolling.remove(g.label.ident);
            buf.fmt("/*unrolled goto*/\n");
            Statement st = ifs.isIfStatement ? ifs.isIfStatement.ifbody : *ifs;
            st = st.isScopeStatement ? st.isScopeStatement.statement : st;
            if (auto comp = st.isCompoundStatement){
                bool generate = false;
                foreach (s; *comp.statements) {
                    if (s == g.label.statement) {
                        generate = true;
                    }
                    if (generate) s.accept(this);

                }
            }
            else 
                st.accept(this);
            if (!unrolling[g.label.ident]) {
                buf.put("/* failed to unroll*/");
                buf.put("throw Dispatch.INSTANCE;\n");
                // rollback changes
                buf = saved;
            }
        }
        else {
            buf.put("throw Dispatch.INSTANCE;\n");
        }
    }

    override void visit(GotoDefaultStatement s)
    {
        long myIndex = gotos.top.countUntil!(c => c.default_);
        buf.put("/*goto default*/ ");
        if (myIndex >= 0) {
            buf.fmt("{ __dispatch%d = %d; continue dispatched_%d; }\n", 
                dispatch.top, -1-myIndex, dispatch.top);
        }
        else {
            buf.put("throw Dispatch.INSTANCE;\n");
        }
    }

    override void visit(GotoCaseStatement s)
    {
        if (!s.exp) {
            // fallthrough
        }
        else {
            buf.put("/*goto case*/");
            buf.fmt("{ __dispatch%d = %s; continue dispatched_%d; }\n",
                dispatch.top, s.exp.toJava(opts), dispatch.top);
        }
    }

    override void visit(SwitchErrorStatement s)
    {
        buf.fmt("throw SwitchError.INSTANCE;\n");
    }
    
    override void visit(BreakStatement s)
    {
        buf.put("break");
        if (s.ident)
        {
            buf.put(' ');
            buf.put(s.ident.toString());
        }
        buf.put(';');
        buf.put('\n');
    }

    override void visit(ContinueStatement s)
    {
        buf.put("continue");
        if (s.ident)
        {
            buf.put(' ');
            buf.put(s.ident.toString());
        }
        else if(forLoop.top != 0) {
            buf.put(' ');
            buf.fmt("L_outer%d", forLoop.top);
        }
        buf.put(';');
        buf.put('\n');
    }

    override void visit(SynchronizedStatement s)
    {
        buf.put("synchronized");
        if (s.exp)
        {
            buf.put('(');
            buf.put(s.exp.toJava(opts));
            buf.put(')');
        }
        if (s._body)
        {
            buf.put(' ');
            s._body.accept(this);
        }
    }

    override void visit(ReturnStatement s)
    {
        processLambdas(s);
        if (opts.funcs.length && !opts.funcs.top.isCtorDeclaration()) {
            if(opts.funcs.top.ident.symbol == "main" && opts.aggregates.empty) {
                buf.put("exit(");
                if (s.exp) {
                    buf.put(s.exp.toJava(opts));
                }
                buf.put(");\n");
            }
            else if (opts.funcs.length > 1 && opts.funcs.top.type.nextOf.ty == Tvoid) {
                buf.put("return null;\n");
            }
            else {
                buf.put("return ");
                if (s.exp) {
                    auto retType = opts.funcs.top.type.nextOf();
                    buf.put(s.exp.toJava(opts));
                }
                buf.put(";\n");
            }
        }
    }

    void addImportForType(Type t) {
        if (auto p = t.isTypePointer) return addImportForType(p.next);
        // stderr.writefln("import for %s %s\n", t.toString, t.kind[0..strlen(t.kind)]);
        auto tc = t.isTypeClass();
        // if (tc) 
        if (tc && tc.sym.getModule() !is opts.currentMod) {
            if (auto tmpl = tc.sym in opts.templates) {
                foreach (tt; tmpl.types) addImportForType(tt);
            }
            addImport(tc.sym.getModule.md.packages, tc.sym.getModule.ident);
        }
        auto ts = t.isTypeStruct();
        if (ts && ts.sym.getModule() !is opts.currentMod) {
            if (auto tmpl = ts.sym in opts.templates) {
                foreach (tt; tmpl.types) addImportForType(tt);
            }
            else if (ts.sym.parent) {
                if (auto ti = ts.sym.parent.isTemplateInstance) {
                    if (ti.tiargs) {
                        foreach (arg; *ti.tiargs) {
                            if (auto tt = arg.isType()) {
                                addImportForType(tt);
                            }
                        }   
                    }
                }
            }
            addImport(ts.sym.getModule.md.packages, ts.sym.getModule.ident);
        }
    }

    void addNestedImports(Type t) {
        // stderr.writefln("import for %s %s\n", t.toString, t.kind[0..strlen(t.kind)]);
        Import[] imps;
        auto tc = t.isTypeClass();
        // if (tc) 
        if (tc) {
            imps = collectImports(tc.sym);
        }
        auto ts = t.isTypeStruct();
        if (ts) {
            imps = collectImports(ts.sym);
        }
        foreach(imp; imps)
            addImport(imp.packages, imp.id);
    }

    auto handleTiAggregate(AggregateDeclaration d) { 
        if (!currentInst.empty) {
            auto tiargs = currentInst.top ? tiArgs().str : "";
            if (currentInst.top) {
                if (currentInst.top.tiargs)
                    foreach (arg; *currentInst.top.tiargs) {
                        if (auto t = arg.isType()) {
                            addImportForType(t);
                            // addNestedImports(t);
                        }
                    }
            }
        }
        return pushed(currentInst, null);
    }

    override void visit(StructDeclaration d)
    {
        if (nameOf(d) == "FPTypePropertiesDouble" && (d in opts.templates).types[0].ty == Tfloat80) return;
        if (nameOf(d) == "UnionExp") return;
        if (opts.funcs.length) return; // inner structs are done separately
        if (nameOf(d) in generatedClasses) {
            buf.fmt("// skipping duplicate class %s\n", nameOf(d));
            return;
        }
        generatedClasses[nameOf(d)] = true;
        auto _ = pushed(opts.aggregates, d);
        auto gf = pushed(generatedFunctions, null);
        auto guard = handleTiAggregate(d);
        auto eg = pushed(opts.erasuresCount, null);

        stderr.writefln("Struct %s", d);
        auto members = collectMembers(d);
        buf.fmt("%s static class ", defAccess);
        if (!d.isAnonymous()) {
            buf.put(nameOf(d));
        }
        if (!d.members)
        {
            buf.put(';');
            buf.put('\n');
            return;
        }
        buf.put('\n');
        buf.put('{');
        buf.put('\n');
        buf.indent;
        foreach (s; *d.members) {
            if (!s.isThisDeclaration) // hidden this parameter for inner function?
                s.accept(this);
        }
        // .init ctor
        buf.fmt("public %s(){ }\n", nameOf(d));
        // default shallow copy for structs
        buf.fmt("public %s copy(){\n", nameOf(d));
        buf.indent;
        buf.fmt("%s r = new %s();\n", nameOf(d), nameOf(d));
        foreach(m; members.all) {
            if (m !in opts.aliasedUnion) {
                bool refness = (m in opts.refParams.top) !is null;
                if (m.type.ty == Tstruct || m.type.ty == Tarray || m in opts.refParams.top) {
                    buf.fmt("r.%s = %s.copy();\n", m.ident.symbol, m.ident.symbol);
                }
                else
                    buf.fmt("r.%s = %s;\n", m.ident.symbol, m.ident.symbol);
            }
        }
        buf.put("return r;\n");
        buf.outdent;
        buf.put("}\n");
        bool hasCtor = hasCtor(d);
        if (!hasCtor) {
            if (members.all.length) {
                //Generate ctors
                // all fields ctor
                if (!members.hasUnion) {
                    buf.fmt("public %s(", nameOf(d));
                    foreach(i, m; members.all) {
                        if(i) buf.put(", ");
                        buf.fmt("%s %s", typeOf(m.type), m.ident.toString);
                    }
                    buf.put(") {\n");
                    buf.indent;
                    foreach(i,m; members.all){
                        if (m !in opts.aliasedUnion) {
                            if (m in opts.refParams.top)
                                buf.fmt("this.%s = ref(%s);\n", m.ident.toString, m.ident.toString);
                            else
                                buf.fmt("this.%s = %s;\n", m.ident.toString, m.ident.toString);
                        }
                    }
                    buf.outdent;
                    buf.put("}\n\n");
                }
            }
        }
        // generate opAssign
        buf.fmt("public %s opAssign(%s that) {\n", nameOf(d), nameOf(d));
        buf.indent;
        foreach(i,m; members.all){
            if (m !in opts.aliasedUnion)
                buf.fmt("this.%s = that.%s;\n", m.ident.toString, m.ident.toString);
        }
        buf.put("return this;\n");
        buf.outdent;
        buf.put("}\n");
        buf.outdent;
        buf.put('}');
        buf.put('\n');
    }
    
    override void visit(ClassDeclaration d)
    {
        if (nameOf(d) in generatedClasses) {
            buf.fmt("// skipping duplicate class %s\n", nameOf(d));
            return;
        }
        generatedClasses[nameOf(d)] = true;
        if (opts.funcs.length) return; // inner classes are done separately
        auto gf = pushed(generatedFunctions, null);
        auto agg = pushed(opts.aggregates, d);
        auto guard = handleTiAggregate(d);
        auto eg = pushed(opts.erasuresCount, null);

        stderr.writefln("Class %s", d);
        auto members = collectMembers(d, true);
        if (!d.isAnonymous())
        {
            auto abs =  d.isAbstract ? "abstract " : "";
            buf.fmt("%s static %sclass %s", defAccess, abs, nameOf(d));
        }
        visitBase(d);
        if (d.members)
        {
            buf.put("\n{\n");
            buf.indent;

            auto oldHasEmptyCtor = hasEmptyCtor;
            scope(exit) hasEmptyCtor = oldHasEmptyCtor;
            hasEmptyCtor = false;
            
            foreach (s; *d.members) {
                if (!s.isThisDeclaration) // hidden this parameter for inner function?
                    s.accept(this);
            }
            if (!hasEmptyCtor) buf.fmt("\npublic %s() {}\n", nameOf(d));

            // generate copy
            if (!d.isNested) {
                buf.fmt("\npublic %s%s copy()", d.isAbstract ? "abstract " : "", nameOf(d));
                if (d.isAbstract) buf.put(";\n");
                else {
                    buf.put(" {\n");
                    buf.indent;
                    buf.fmt("%s that = new %s();\n", nameOf(d), nameOf(d));
                    foreach(m; members.all) {
                        buf.fmt("that.%s = this.%s;\n", m.ident.symbol, m.ident.symbol);
                    }
                    buf.put("return that;\n");
                    buf.outdent;
                    buf.fmt("}\n");
                }
            }

            buf.outdent;
            buf.put('}');
            
        }
        else
            buf.put(';');
        buf.put('\n');
    }

    private void visitBase(ClassDeclaration d)
    {
        if (!d || !d.baseclasses.dim)
            return;
        if (!d.isAnonymous())
            buf.put(" extends ");
        foreach (i, b; *d.baseclasses)
        {
            addNestedImports(b.type);
            if (i) buf.put(", ");
            buf.put(typeOf(b.type));
        }
    }

    override void visit(UnitTestDeclaration func)  {
        hoistLocalAggregates(func);
        auto _ = pushed(opts.funcs, func);
        if (func.fbody) {
            buf.fmt("public static void test_%d() {\n", testCounter++);
            buf.indent;    
            func.fbody.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
    }

    extern(D) void printFunctionBody(FuncDeclaration func, VarDeclaration[] renamedVars, bool needsSelf = false) {
        if (func.fbody is null) 
            buf.put(";\n");
        else {
            buf.put(" {\n");
            buf.indent;
            if (func.vresult) visit(func.vresult);
            foreach (var; renamedVars) {
                buf.fmt("%s %s = ref(%s);\n", refType(var.type, opts), opts.renamed[var], var.ident.symbol);
            }
            if (needsSelf) buf.fmt("%s __self = this;\n", func.isThis.type.toJava(opts));
            func.fbody.accept(this);
            if (!func.hasReturnExp && opts.funcs.length > 1 && func.type.nextOf.ty == Tvoid)
                buf.put("return null;\n");
            buf.outdent;
            buf.put('}');
        }
    }

    VarDeclaration[] printParameters(FuncDeclaration func, Boxing boxing, bool isLambda, int numArgs = -1) {
        VarDeclaration[] renamedVars;
        if (func.parameters) {
            numArgs = numArgs < 0 ? cast(int)func.parameters.length : numArgs;
            foreach(i, p; (*func.parameters)[0..numArgs]) {
                if (i != 0) buf.fmt(", ");
                auto box = p.isRef || p.isOut;
                if (box && !isLambda && !p.type.isTypeStruct && p.type.ty != Tarray && !p.type.isConst) {
                    opts.refParams.top[p] = true;
                    buf.fmt("%s %s", refTypeOf(p.type), p.ident.symbol);
                }
                else {
                    buf.fmt("%s %s", typeOf(p.type, boxing), p.ident.symbol);
                    if (p in opts.refParams.top) {
                        renamedVars ~= p;
                        opts.renamed[p] = (p.ident.symbol ~ "_ref").dup;
                    }
                }
            }
            if (auto var = varargVarDecl(func)) {
                opts.vararg = var;
                buf.fmt(", Object... %s", var.ident.symbol);
            }
        }
        else if (auto ft = func.type.isTypeFunction()){
            if (ft.parameterList)
                foreach(i, p; *ft.parameterList) {
                   if (i != 0) buf.fmt(", ");
                    auto box = p.storageClass & (STC.ref_ | STC.out_);
                    auto name = p.ident ? p.ident.toString : format("arg%d",i);
                    if (box && !isLambda && !p.type.isTypeStruct && p.type.ty != Tarray && !p.type.isConst) {
                        opts.refParams.top[p] = true;
                        buf.fmt("%s %s", refTypeOf(p.type), name);
                    }
                    else buf.fmt("%s %s", typeOf(p.type), name);
                }
        }
        return renamedVars;
    }

    VarDeclaration[] printGlobalFunctionHead(FuncDeclaration func, int numArgs = -1) {
        auto storage = (func.isStatic()  || opts.aggregates.length == 0) ? "static" : "";
        if (func.isAbstract && func.fbody is null) storage = "abstract";
        if (auto ctor = func.isCtorDeclaration())
            buf.fmt("public %s %s%s(", storage, typeOf(func.type.nextOf()), tiArgs.str);
        else if(func.funcName == "main" && opts.aggregates.length == 0) {
            buf.fmt("public %s void %s%s(", storage, func.funcName, tiArgs.str);
        }
        else
            buf.fmt("public %s %s %s%s(", storage, typeOf(func.type.nextOf()), 
                func.funcName == "opIndex" ? "get" : func.funcName, tiArgs.str);
        VarDeclaration[] renamedVars = printParameters(func, Boxing.no, false, numArgs);
        if (auto e = func in opts.erasures) {
            if (e.n != 0) buf.fmt(", ETag%d __tag", e.n);
        }
        buf.put(")");
        return renamedVars;
    }

    void printLocalFunction(FuncDeclaration func, bool isLambda = false) {
        auto t = func.type.isTypeFunction();
        //buf.fmt("// Local function %s is lambda=%s\n", func.ident.toString, isLambda);
        buf.fmt("%s %s%s = new %s() {\n", t.toJavaFunc(opts, isLambda), 
            func.funcName, tiArgs.str, t.toJavaFunc(opts, isLambda));
        buf.indent;
        buf.fmt("public %s invoke(", t.nextOf.toJava(opts, Boxing.yes));
        VarDeclaration[] renamedVars = printParameters(func, Boxing.yes, isLambda);
        buf.fmt(") {\n");
        printFunctionBody(func, renamedVars);
        buf.fmt("}\n");
        buf.outdent;
        buf.put("\n};\n");
    }

    
    void printGlobalFunction(FuncDeclaration func) {
        opts.vararg = null;
        if (func.fbody is null && !func.isAbstract) return;
        //stderr.writefln("\tFunction %s", func.ident.toString);
        buf.fmt("// Erasure: %s\n", erasureOf(func, opts));
        VarDeclaration[] renamedVars = printGlobalFunctionHead(func);
        bool needsSelf = func.isThis !is null && hasLocalFunctions(func);
        printFunctionBody(func, renamedVars, needsSelf);
        buf.put("\n\n");
        void printDefaulted(size_t split, Parameters* params) {
            foreach (j; 0..params.length) {
                auto p = (*params)[j];
                if (j) buf.put(", ");
                if (j < split) buf.put(p.ident.symbol);
                else if (p.defaultArg.isNullExp) buf.fmt("(%s)null", p.defaultArg.type.toJava(opts));
                else buf.put(p.defaultArg.toJava(opts));
            }
        }
        if (func.parameters) {
            auto params = func.type.isTypeFunction.parameterList;
            foreach_reverse (i, p; *params) {
                if (p.defaultArg) {
                    buf.fmt("// defaulted all parameters starting with #%d\n", i+1);
                    printGlobalFunctionHead(func, cast(int)i);
                    buf.put(" {\n");
                    buf.indent;
                    if (func.isCtorDeclaration) {
                        buf.fmt("this(");
                        printDefaulted(i, params);
                        buf.fmt(");\n");
                    }
                    else {
                        if (func.type.nextOf.ty != Tvoid) buf.put("return ");
                        buf.fmt("%s(", func.funcName);
                        printDefaulted(i, params);
                        buf.fmt(");\n");
                    }
                    buf.outdent;
                    buf.put("}\n\n");
                }
            }
        }
    }

    void hoistLocalAggregates(FuncDeclaration func) {
        auto nested = collectNestedAggregates(func);
        auto save = buf;
        buf = new TextBuffer();
        auto oldDefAccess = defAccess;
        defAccess = "private";
        scope(exit) defAccess = oldDefAccess;
        foreach(agg; nested) agg.accept(this);
        constants ~= buf.data.dup;
        buf = save;
    }

    override void visit(TryFinallyStatement statement) {
        buf.put("try {\n");
        buf.indent;
        if (statement._body) statement._body.accept(this);
        buf.outdent;
        buf.put("}\n");
        if (statement.finalbody) {
            buf.put("finally {\n");
            buf.indent;
            statement.finalbody.accept(this);
            buf.outdent;
            buf.put("}\n");
        }
    }

    override void visit(DtorDeclaration ) { }

    override void visit(AliasDeclaration d) { 
        if (d.aliassym) {
            auto f = d.aliassym.isFuncDeclaration;
            if (f && f.getModule !is opts.currentMod)
                addImport(f.getModule.md.packages, f.getModule.ident);
        }
    }

    override void visit(FuncDeclaration func)  {
        if (func.funcName == "destroy") return;
        if (func.funcName == "opAssign") return;
        if (func.funcName == "emplaceExp") return;
        if (func.funcName == "copy" && opts.aggregates.length > 0) return;
        if (func.isCtorDeclaration() && !func.parameters) hasEmptyCtor = true;
        if (opts.funcs.length > 0) opts.localFuncs[func] = true;
        // check for duplicates
        auto sig = funcSig(func);
        if (sig in generatedFunctions.top) {
            buf.fmt("// removed duplicate function, [%s] signature: %s\n", generatedFunctions.top.keys, sig);
            return;
        }
        auto unrolled = pushed(unrolledGotos, IdentityMap!Statement());
        generatedFunctions.top[sig] = true;
        auto lgn = pushed(labelGotoNums);
        auto erasure = erasureOf(func, opts);
        if (int* count = erasure in opts.erasuresCount.top) {
            opts.erasures[func] = Erasure(erasure, *count);
            ++*count;
        }
        else  {
            opts.erasures[func] = Erasure(erasure, 0);
            opts.erasuresCount.top[erasure] = 1;
        }
        
        // hoist nested structs/classes to top level, mark them private
        if (opts.funcs.length == 0) {
            hoistLocalAggregates(func);
        }
        auto _ = pushed(opts.funcs, func);

        auto local = passedByRef(func);
        foreach (k; opts.refParams.top.keys)
            local[k] = opts.refParams.top[k];

        auto refs = pushed(opts.refParams, local);

        if (opts.funcs.length > 1)
            printLocalFunction(func);
        else {
            auto gf = pushed(generatedFunctions);
            printGlobalFunction(func);
        }
    }

    private void initializerToBuffer(Initializer inx, TextBuffer buf, ExprOpts opts)
    {
        void visitError(ErrorInitializer iz)
        {
            buf.fmt("__error__");
        }

        void visitVoid(VoidInitializer iz)
        {
            if (iz.type.ty == Tsarray) printSArray(iz.type, buf);
            else if(iz.type.ty == Tint32 || iz.type.ty == Tuns32) {
                buf.put("0");
            }
            else buf.fmt("null");
        }

        void visitStruct(StructInitializer si)
        {
            //printf("StructInitializer::toCBuffer()\n");
            buf.put('{');
            foreach (i, const id; si.field)
            {
                if (i)
                    buf.put(", ");
                if (id)
                {
                    buf.put(id.toString());
                    buf.put(':');
                }
                if (auto iz = si.value[i])
                    initializerToBuffer(iz, buf, opts);
            }
            buf.put('}');
        }

        void visitArray(ArrayInitializer ai)
        {
            TextBuffer tmp = buf;
            inInitializer++;
            Initializer[] arr = new Initializer[ai.index.length];
            bool strings = true;
            foreach (i, ex; ai.index)
            {
                if (ex)
                {
                    auto ie = ex.isIntegerExp();
                    if (arr.length <= ie.toInteger) arr.length = ie.toInteger + 1;
                    arr[ie.toInteger] = ai.value[i];
                }
                else {
                    arr[i] = ai.value[i];
                    if (auto e = arr[i].isArrayInitializer()) {
                        strings = false;
                    }
                }
            }
            if (inInitializer == 1) {
                tmp = new TextBuffer();
                auto t = ai.type;
                string suffix = "";
                opts.rawArrayLiterals = true;
                // string literals are byte arrays, exclude them
                while((t.ty == Tarray || t.ty == Tsarray || t.ty == Tpointer) && (t.nextOf.ty != Tchar || !strings)) {
                    suffix ~= "[]";
                    t = t.nextOf();
                }
                tmp.fmt("private static final %s%s initializer_%d = ", typeOf(t), suffix, arrayInitializers.length);
            }
            
            tmp.put("{");
            foreach (i, iz; arr[])
            {
                if (i)
                    tmp.put(", ");
                if (iz) initializerToBuffer(iz, tmp, opts);
                else if (ai.type.nextOf().ty == Tenum) tmp.put("0");
                else tmp.put("null");
            }
            tmp.put("}");
            if (inInitializer == 1) {
                opts.rawArrayLiterals = false;
                arrayInitializers ~= tmp.data.idup;
                buf.fmt("slice(initializer_%d)", arrayInitializers.length-1);
            }
            inInitializer--;
        }

        void visitExp(ExpInitializer ei)
        {
            //stderr.writefln("Initializer is %s %s\n", ei.exp, ei.exp.type);
            buf.put(ei.exp.toJava(opts));
        }

        final switch (inx.kind)
        {
            case InitKind.error:   return visitError (inx.isErrorInitializer ());
            case InitKind.void_:   return visitVoid  (inx.isVoidInitializer  ());
            case InitKind.struct_: return visitStruct(inx.isStructInitializer());
            case InitKind.array:   return visitArray (inx.isArrayInitializer ());
            case InitKind.exp:     return visitExp   (inx.isExpInitializer   ());
        }
    }

    string result() { return cast(string)header.data ~ cast(string)buf.data; }
}

