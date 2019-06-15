module visitors.expression;

import core.stdc.ctype;
import core.stdc.stdio;
import core.stdc.string;

import std.string;

import dmd.aggregate;
import dmd.apply;
import dmd.aliasthis;
import dmd.arraytypes;
import dmd.cond;
import dmd.ctfeexpr;
import dmd.dclass;
import dmd.declaration;
import dmd.denum;
import dmd.dimport;
import dmd.dmodule;
import dmd.dtemplate;
import dmd.doc;
import dmd.dstruct;
import dmd.dsymbol;
import dmd.expression;
import dmd.func;
import dmd.globals;
import dmd.id;
import dmd.identifier;
import dmd.init;
import dmd.mtype;
import dmd.nspace;
import dmd.parse;
import dmd.root.outbuffer;
import dmd.root.rootobject;
import dmd.statement;
import dmd.target;
import dmd.tokens;
import dmd.utils;
import dmd.visitor;

struct ExprOpts {
    bool wantCharPtr = false;
    bool reverseIntPromotion = false;
    EnumDeclaration inEnumDecl = null;
    FuncDeclaration inFuncDecl = null;
    Expression dollarValue = null;
    bool[void*] refParams; //out and ref params, they must be boxed
}

///
const(char)[] symbol(const(char)[] s) {
    if (s == "native") return "native_";
    else if(s == "toString") return "asString";
    else if (s == "Array") return "DArray";
    else return s;
}

///
const(char)[] symbol(const(char)* s) {
    return symbol(s[0..strlen(s)]);
}

///
const(char)[] symbol(Identifier s) {
    return symbol(s.toString);
}

///
string toJavaFunc(TypeFunction t)
{
    scope OutBuffer* buf = new OutBuffer();
    visitFuncIdentWithPostfix(t, null, buf);
    buf.writeByte(0);
    char* p = buf.extractData;
    return cast(string)p[0..strlen(p)];
}

///
string toJava(Type t, Identifier id = null, Boxing boxing = Boxing.no) {
    scope OutBuffer* buf = new OutBuffer();
    typeToBuffer(t, id, buf, boxing);
    buf.writeByte(0);
    char* p = buf.extractData;
    auto type = cast(string)p[0..strlen(p)];
    return type;
}

///
string toJava(Expression e, ExprOpts opts) {
    scope OutBuffer* buf = new OutBuffer();
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
    buf.writeByte(0);
    char* p = v.buf.extractData;
    return cast(string)p[0..strlen(p)];
}

///
string toJavaBool(Expression e, ExprOpts opts) {
    scope OutBuffer* buf = new OutBuffer();
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
    if (e.type && e.type.ty != Tbool) switch(e.type.ty){
        case Tpointer:
        case Tclass:
            buf.writestring(" != null");
            break;
        case Tarray:
            buf.writestring(".getLength() != 0");
            break;
        default:
            buf.prependbyte('(');
            buf.writestring(") != 0");
    }
    buf.writeByte(0);
    char* p = v.buf.extractData;
    return cast(string)p[0..strlen(p)];
}

private bool isJavaByte(Type t) {
    return t.ty == Tchar || t.ty == Tint8 || t.ty == Tuns8;
}


private extern(C++) class ByteSizedVisitor : Visitor {
    bool isByteSized = false;
    alias visit = typeof(super).visit;
    
    override void visit(Expression ) {}

    override void visit(UnaExp u) {
        u.e1.accept(this);
    }

    override void visit(BinExp b) {
        b.e1.accept(this);
        b.e2.accept(this);
    }

    override void visit(CastExp e) {
        auto t = e.e1.type;
        if (isJavaByte(t)) {
            isByteSized = true;
        }
        else if(auto et = t.isTypeEnum()) {
            isByteSized = isJavaByte(et.memType);
        }
    }
}

public bool isByteSized(Expression e) {
    scope v = new ByteSizedVisitor();
    e.accept(v);
    return v.isByteSized;
}

///
extern (C++) final class toJavaExpressionVisitor : Visitor
{
    alias visit = Visitor.visit;
    
public:
    OutBuffer* buf;
    private ExprOpts opts;


    extern (D) this(OutBuffer* buf, ExprOpts opts)
    {
        this.buf = buf;
        this.opts  = opts;
    }

    ////////////////////////////////////////////////////////////////////////////
    override void visit(Expression e)
    {
        buf.writestring(Token.toString(e.op));
    }

    override void visit(IntegerExp e)
    {
        const dinteger_t v = e.toInteger();
        if (e.type)
        {
            Type t = e.type;
            string castTarget = "";
        L1:
            switch (t.ty)
            {
            case Tenum:
                {
                    TypeEnum te = cast(TypeEnum)t;
                    auto sym = te.sym;
                    if (opts.inEnumDecl != sym)
                        foreach(i;0 .. sym.members.dim)
                        {
                            EnumMember em = cast(EnumMember) (*sym.members)[i];
                            if (em.value.toInteger == v)
                            {
                                auto s = symbol(sym.ident);
                                buf.printf("%.*s.%s", s.length, s.ptr, em.ident.toChars());
                                return ;
                            }
                        }
                    t = sym.memtype;
                    goto L1;
                }
            case Twchar:
            case Tdchar:
                buf.printf("'\\u%04x'", v);
                break;
            case Tchar:
                {
                    buf.printf("(byte)%d", cast(int)v);
                    break;
                }
            case Tint8:
                castTarget = "byte";
                goto L2;
            case Tint16:
                castTarget = "short";
                goto L2;
            case Tuns8:
                castTarget = "byte";
                goto L2;
            case Tuns16:
                castTarget = "short";
                goto L2;
            case Tint32:
            case Tuns32:
            L2:
                if (castTarget.length)
                    buf.printf("(%.*s)%d", castTarget.length, castTarget.ptr, cast(int)v);
                else if(opts.reverseIntPromotion)
                    buf.printf("(byte)%d", cast(int)v);
                else
                    buf.printf("%d", cast(int)v);
                break;
            case Tint64:
            case Tuns64:
                buf.printf("%lldL", v);
                break;
            case Tbool:
                buf.writestring(v ? "true" : "false");
                break;
            case Tclass:
            case Tpointer:
                if (v == 0) buf.writestring("null");
                else assert(false);
                break;
            default:
                /* This can happen if errors, such as
                 * the type is painted on like in fromConstInitializer().
                 */
                if (!global.errors)
                {
                    assert(0);
                }
                break;
            }
        }
        else if (v & 0x8000000000000000L)
            buf.printf("0x%llx", v);
        else
            buf.print(v);
    }

    override void visit(ErrorExp e)
    {
        buf.writestring("__error");
    }

    override void visit(DollarExp e)
    {
        fprintf(stderr, "D %s\n", e.toChars());
        buf.writestring(e.ident.toString());
    }

    override void visit(IdentifierExp e)
    {
        fprintf(stderr, "ID %s\n", e.toChars());
        buf.writestring(e.ident.toString());
    }

    override void visit(DsymbolExp e)
    {
        fprintf(stderr, "DSymbol %s\n", e.toChars());
        buf.writestring(e.s.toChars());
    }

    override void visit(ThisExp e)
    {
        buf.writestring("this");
    }

    override void visit(SuperExp e)
    {
        buf.writestring("super");
    }

    override void visit(NullExp e)
    {
        auto t = e.type.toJava();
        if (e.type.ty == Tarray) buf.printf("new %.*s()", t.length, t.ptr);
        else buf.writestring("null");
    }

    override void visit(StringExp e)
    {
        if (opts.wantCharPtr) buf.writestring("new BytePtr(");
        else buf.writestring(" new ByteSlice(");
        buf.writeByte('"');
        const o = buf.offset;
        for (size_t i = 0; i < e.len; i++)
        {
            const c = e.charAt(i);
            switch (c)
            {
            case '"':
            case '\\':
                buf.writeByte('\\');
                goto default;
            default:
                if (c <= 0x7F && isprint(c))
                    buf.writeByte(c);
                else if(c == '\n')
                    buf.writestring("\\n");
                else if(c == '\r')
                    buf.writestring("\\r");
                else
                    buf.printf("\\u%04x", c);
                break;
            }
        }
        buf.writeByte('"');
        buf.writeByte(')');
    }

    override void visit(ArrayLiteralExp e)
    {
        buf.writeByte('{');
        argsToBuffer(e.elements, buf, opts, e.basis);
        buf.writeByte('}');
    }

    override void visit(AssocArrayLiteralExp e)
    {
        buf.writeByte('[');
        foreach (i, key; *e.keys)
        {
            if (i)
                buf.writestring(", ");
            expToBuffer(key, PREC.assign, buf, opts);
            buf.writeByte(':');
            auto value = (*e.values)[i];
            expToBuffer(value, PREC.assign, buf, opts);
        }
        buf.writeByte(']');
    }

    override void visit(StructLiteralExp e)
    {
        buf.writestring("new ");
        buf.writestring(e.type.toJava);
        buf.writeByte('(');
        if (e.type.toString.indexOf("Array!") < 0) {
            // CTFE can generate struct literals that contain an AddrExp pointing
            // to themselves, need to avoid infinite recursion:
            // struct S { this(int){ this.s = &this; } S* s; }
            // const foo = new S(0);
            if (e.stageflags & stageToCBuffer)
                buf.writestring("<recursion>");
            else
            {
                const old = e.stageflags;
                e.stageflags |= stageToCBuffer;
                argsToBuffer(e.elements, buf, opts);
                e.stageflags = old;
            }
        }
        buf.writeByte(')');
    }

    override void visit(TypeExp e)
    {
        typeToBuffer(e.type, null, buf);
    }

    override void visit(ScopeExp e)
    {
        if (e.sds.isTemplateInstance())
        {
            e.sds.dsymbolToBuffer(buf);
        }
        else
        {
            buf.writestring(e.sds.kind());
            buf.writeByte(' ');
            buf.writestring(e.sds.toChars());
        }
    }

    override void visit(TemplateExp e)
    {
        buf.writestring(e.td.toChars());
    }

    override void visit(NewExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, opts);
            buf.writeByte('.');
        }
        buf.writestring("new ");
        typeToBuffer(e.newtype, null, buf);
        buf.writeByte('(');
        if (e.type.toString.indexOf("Array!") < 0) { 
            if (e.arguments && e.arguments.dim)
            {
                argsToBuffer(e.arguments, buf, opts);
            }
        }
        buf.writeByte(')');
    }

    override void visit(NewAnonClassExp e)
    {
        if (e.thisexp)
        {
            expToBuffer(e.thisexp, PREC.primary, buf, opts);
            buf.writeByte('.');
        }
        buf.writestring("new");
        if (e.newargs && e.newargs.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.newargs, buf, opts);
            buf.writeByte(')');
        }
        buf.writestring(" class ");
        if (e.arguments && e.arguments.dim)
        {
            buf.writeByte('(');
            argsToBuffer(e.arguments, buf, opts);
            buf.writeByte(')');
        }
        if (e.cd)
            e.cd.dsymbolToBuffer(buf);
    }

    override void visit(SymOffExp e)
    {
        if (e.offset)
            buf.printf("(%s.ptr().plus(%u))", e.var.toChars(), e.offset);
        else if (e.var.isTypeInfoDeclaration())
            buf.writestring(e.var.toChars());
        else
            buf.printf("%s.ptr()", e.var.toChars());
    }

    override void visit(VarExp e)
    {
        if(e.var.ident.symbol == "__dollar") {
            opts.dollarValue.accept(this);
            buf.writestring(".getLength()");
        }
        else {
            if (e.var.isMember() && e.var.isStatic())
                buf.printf("%s.", e.var.parent.ident.toChars());
            buf.writestring(symbol(e.var.toChars()));
            if (cast(void*)e.var in opts.refParams) 
                buf.writestring(".value");
        }
    }

    override void visit(OverExp e)
    {
        buf.writestring(e.vars.ident.toString());
    }

    override void visit(TupleExp e)
    {
        if (e.e0)
        {
            buf.writeByte('(');
            e.e0.accept(this);
            buf.writestring(", tuple(");
            argsToBuffer(e.exps, buf, opts);
            buf.writestring("))");
        }
        else
        {
            buf.writestring("tuple(");
            argsToBuffer(e.exps, buf, opts);
            buf.writeByte(')');
        }
    }

    override void visit(FuncExp e)
    {
        fprintf(stderr, "Func exp %s\n", e.toChars());
        e.fd.dsymbolToBuffer(buf);
        //buf.writestring(e.fd.toChars());
    }
    
    override void visit(CommaExp c)
    {
        /*buf.writestring("comma(");
        buf.writestring(c.e1.toJava(opts));
        buf.writestring(", ");
        buf.writestring(c.e2.toJava(opts));
        buf.writestring(")");*/
    }

    override void visit(DeclarationExp e)
    {
        /* Normal dmd execution won't reach here - regular variable declarations
         * are handled in visit(ExpStatement), so here would be used only when
         * we'll directly call Expression.toChars() for debugging.
         */
        assert(0, "DeclarationExp");
    }

    override void visit(TypeidExp e)
    {
        //TODO: 
        buf.writestring(e.toChars);
        //assert(false);
        // not used in DMD sources
    }

    override void visit(TraitsExp e)
    {
        // must handle TraitsExp by pattern-matching on 
        // the full expression
        assert(0);
        version(none) {
            buf.writestring("__traits(");
            if (e.ident)
                buf.writestring(e.ident.toString());
            if (e.args)
            {
                foreach (arg; *e.args)
                {
                    buf.writestring(", ");
                    objectToBuffer(arg, buf);
                }
            }
            buf.writeByte(')');
        }
    }

    override void visit(HaltExp e)
    {
        buf.writestring("halt");
    }

    override void visit(IsExp e)
    {
        assert(false);
        version(none) {
            buf.writestring("is(");
            typeToBuffer(e.targ, e.id, buf);
            if (e.tok2 != TOK.reserved)
            {
                buf.printf(" %s %s", Token.toChars(e.tok), Token.toChars(e.tok2));
            }
            else if (e.tspec)
            {
                if (e.tok == TOK.colon)
                    buf.writestring(" : ");
                else
                    buf.writestring(" == ");
                typeToBuffer(e.tspec, null, buf);
            }
            if (e.parameters && e.parameters.dim)
            {
                buf.writestring(", ");
                scope v = new DsymbolPrettyPrintVisitor(buf);
                v.visitTemplateParameters(e.parameters);
            }
            buf.writeByte(')');
        }
    }

    override void visit(UnaExp e)
    {
        if (e.e1.type.ty == Tpointer) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            if(e.op == TOK.not) {
                buf.writestring(" == null");
            }
            else {    
                assert(0, "Unsupported unary pointer arithmetic");
            }
        }
        else if(e.op == TOK.not) {
            buf.writestring(Token.toString(e.op));
            buf.writeByte('(');
            buf.writestring(e.e1.toJavaBool(opts));
            buf.writeByte(')');
        }
        else {
            buf.writestring(Token.toString(e.op));
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(BinExp e)
    {
        bool oldIntPromotion = opts.reverseIntPromotion;
        if (e.e1.type && (e.op == TOK.equal || e.op == TOK.notEqual) && !e.e1.type.isTypeBasic) {
            expToBuffer(e.e1, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(".equals(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(")");
            return;
        }
        else if(e.op == TOK.andAnd || e.op == TOK.orOr) {
            buf.writestring(e.e1.toJavaBool(opts));
            buf.writeByte(' ');
            buf.writestring(Token.toString(e.op));
            buf.writeByte(' ');
            buf.writestring(e.e2.toJavaBool(opts));
            return;
        }
        else if(e.op == TOK.concatenate) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(".concat(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(")");
            return;
        }
        else if(e.op == TOK.concatenateAssign || e.op == TOK.concatenateElemAssign) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(".append(");
            expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
            buf.writestring(")");
            return;
        }
        else if (isByteSized(e.e1) && !isByteSized(e.e2)) {
            oldIntPromotion = opts.reverseIntPromotion;
            opts.reverseIntPromotion = true;
        }
        else if(!isByteSized(e.e1) && isByteSized(e.e2)){
            oldIntPromotion = opts.reverseIntPromotion;
            opts.reverseIntPromotion = true;
        }
        else if(e.e1.type.ty == Tpointer && (e.e2.type.isTypeBasic() || e.e2.type.ty == Tpointer)) {
            string opName = "";
            switch(e.op) {
                case TOK.add:
                    opName = "plus";
                    break;
                case TOK.addAssign: 
                    opName = "plusAssign";
                    break;
                case TOK.min:
                    opName = "minus";
                    break;
                case TOK.minAssign:
                    opName = "minusAssign";
                    break;
                default:
            } 
            if (opName != "") {
                expToBuffer(e.e1, precedence[e.op], buf, opts);
                buf.printf(".%.*s(", opName.length, opName.ptr);
                expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
                buf.writestring(")");
                return;
            }
        }
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        buf.writeByte(' ');
        if (e.op == TOK.identity) buf.writestring("==");
        else if (e.op == TOK.notIdentity)  buf.writestring("!=");
        else buf.writestring(Token.toString(e.op));
        buf.writeByte(' ');
        expToBuffer(e.e2, cast(PREC)(precedence[e.op] + 1), buf, opts);
        opts.reverseIntPromotion = oldIntPromotion;
    }

    override void visit(CompileExp e)
    {
        buf.writestring("mixin(");
        argsToBuffer(e.exps, buf, opts, null);
        buf.writeByte(')');
    }

    override void visit(ImportExp e)
    {
        buf.writestring("import(");
        expToBuffer(e.e1, PREC.assign, buf, opts);
        buf.writeByte(')');
    }

    override void visit(AssertExp e)
    {
        if (e.e1.isIntegerExp()) {
            auto i = e.e1.isIntegerExp();
            if (i.toInteger() == 0) {
                buf.writestring("throw new AssertionError(\"Unreachable code!\")");
                return;
            }
        }
        buf.writestring("assert(");
        expToBuffer(e.e1, PREC.assign, buf, opts);
        if (e.e1.type.ty == Tint32) buf.writestring(" != 0");
        if (e.msg)
        {
            buf.writestring(", "); 
            expToBuffer(e.msg, PREC.assign, buf, opts);
        }
        buf.writeByte(')');
    }

    override void visit(DotIdExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.ident.toString());
    }

    override void visit(DotTemplateExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.td.toChars());
    }

    override void visit(DotVarExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.var.ident.symbol);
    }

    override void visit(DotTemplateInstanceExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        e.ti.dsymbolToBuffer(buf);
    }

    override void visit(DelegateExp e)
    {
        //fprintf(stderr, "Delegate exp %s\n", e.toChars());
        if (!e.func.isNested() || e.func.needThis())
        {
            expToBuffer(e.e1, PREC.primary, buf, opts);
            buf.writeByte('.');
        }
        buf.writestring(e.func.toChars());
    }

    override void visit(DotTypeExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        buf.writestring(e.sym.toChars());
    }

    override void visit(CallExp e)
    {
        //fprintf(stderr, "call exp %s\n", e.e1.toChars());
        if (e.e1.op == TOK.type)
        {
            /* Avoid parens around type to prevent forbidden cast syntax:
             *   (sometype)(arg1)
             * This is ok since types in constructor calls
             * can never depend on parens anyway
             */
            e.e1.accept(this);
        }
        else {
            if (e.f && e.f.isDtorDeclaration()) return;
            if (e.f && e.f.isCtorDeclaration()) {
                fprintf(stderr, "CTOR: %s\n", e.toChars());
            }
            if (e.f && e.f.ident.symbol == "opIndex") {
                auto var = e.e1.isDotVarExp();
                // fprintf(stderr, "DOT VAR: %x\n", var);
                if (var) expToBuffer(var.e1, PREC.primary, buf, opts);
                else expToBuffer(e.e1, PREC.primary, buf, opts);
                if (e.f.parameters.length == 1)
                    buf.writestring(".get");
                else
                    buf.writestring(".set");
            }
            else 
                expToBuffer(e.e1, precedence[e.op], buf, opts);
            if (e.e1.type.ty == Tpointer) {
                buf.writestring(".invoke");
            }
        }
        buf.writeByte('(');
        argsToBuffer(e.arguments, buf, opts);
        buf.writeByte(')');
    }

    override void visit(PtrExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.e1.type.nextOf.ty != Tstruct && e.e1.type.nextOf.ty != Tfunction) 
            buf.writestring(".get(0)");
    }

    override void visit(DeleteExp e)
    {
        // no-op
    }

    override void visit(CastExp e)
    {
        if (!e.to) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            return; 
        }
        bool intTo, wasInt;
        bool complexTarget = false;
        bool fromBool = false;
        bool fromEnum = false;
        bool fromClass = false;
        bool toVoidPtr = false;
        bool toVoid = false;
        if (e.to) switch(e.to.ty) {
            case Tvoid:
                toVoid = true;
                break;
            case Tpointer:
                if (e.to.nextOf.ty == Tvoid)
                    toVoidPtr = true;
                goto case;
            case Tarray:
                complexTarget = true;
                break;
            case Tint32:
            case Tuns32:
            case Tdchar:
                intTo = true;
                break;
            default:
        }
        if (e.e1.type) switch(e.e1.type.ty) {
            case Tbool:
                fromBool = true;
                break;
            case Tclass:
                fromClass = true;
                break;
            case Tint32:
            case Tuns32:
            case Tdchar:
                wasInt = true;
                break;
            case Tenum:
                fromEnum = true;
                break;
            default:
        }
        if (toVoid || toVoidPtr || fromClass) expToBuffer(e.e1, precedence[e.op], buf, opts);
        else if(fromBool && intTo) {
            buf.writestring("(");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(" ? 1 : 0)");
        }
        else if (wasInt && intTo) expToBuffer(e.e1, precedence[e.op], buf, opts);
        else if(opts.reverseIntPromotion && intTo)
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        else if(fromEnum) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(".value");
        }
        else if (complexTarget) { // rely on .toTypeName
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            buf.writestring(".to");
            typeToBuffer(e.to, null, buf);
            buf.writestring("()");
        }
        else { // simple casts
            buf.writestring("(");
            typeToBuffer(e.to, null, buf);
            buf.writestring(")");
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(VectorExp e)
    {
        assert(false);
    }

    override void visit(VectorArrayExp e)
    {
        assert(false);
    }

    override void visit(SliceExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.upr || e.lwr)
        {
            buf.writestring(".slice(");
            if (e.lwr)
                sizeToBuffer(e.lwr, buf, opts);
            else
                buf.writeByte('0');
            
            if (e.upr) {
                auto old = opts.dollarValue;
                scope(exit) opts.dollarValue = old;
                opts.dollarValue = e.e1;
                buf.writestring(",");
                sizeToBuffer(e.upr, buf, opts);
            }
            buf.writeByte(')');
        }
    }

    override void visit(ArrayLengthExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".getLength()");
    }

    override void visit(IntervalExp e)
    {
        expToBuffer(e.lwr, PREC.assign, buf, opts);
        buf.writestring("..");
        expToBuffer(e.upr, PREC.assign, buf, opts);
    }

    override void visit(DelegatePtrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".ptr");
    }

    override void visit(DelegateFuncptrExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".funcptr");
    }

    override void visit(ArrayExp e)
    {
        buf.writestring("new ");
        typeToBuffer(e.type, null, buf);
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('{');
        argsToBuffer(e.arguments, buf, opts);
        buf.writeByte('}');
    }

    override void visit(DotExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writeByte('.');
        expToBuffer(e.e2, PREC.primary, buf, opts);
    }

    override void visit(AssignExp e)
    {
        if (auto assign = e.e1.isIndexExp()) {
            auto oldDollar = opts.dollarValue;
            scope(exit) opts.dollarValue = oldDollar;
            opts.dollarValue = e.e1;
            expToBuffer(assign.e1, PREC.primary, buf, opts);
            buf.writestring(".set(");
            expToBuffer(assign.e2, PREC.primary, buf, opts);
            buf.writestring(", ");
            sizeToBuffer(e.e2, buf, opts);
            buf.writeByte(')');
        }
        else if(auto pt = e.e1.isPtrExp()) {
            expToBuffer(pt.e1, PREC.primary, buf, opts);
            buf.writestring(".set(0, ");
            expToBuffer(e.e2, PREC.primary, buf, opts);
            buf.writeByte(')');
        }
        else if(e.e1.type.ty == Tpointer && e.e1.type.nextOf.ty == Tchar) {
            auto old = opts.wantCharPtr;
            opts.wantCharPtr = true;
            scope(exit) opts.wantCharPtr = old;
            visit(cast(BinExp)e);
        }
        else
            visit(cast(BinExp)e);
    }

    override void visit(IndexExp e)
    {
        auto oldDollar = opts.dollarValue;
        scope(exit) opts.dollarValue = oldDollar;
        opts.dollarValue = e.e1;
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".get(");
        sizeToBuffer(e.e2, buf, opts);
        buf.writeByte(')');
    }

    override void visit(PostExp e)
    {
        expToBuffer(e.e1, precedence[e.op], buf, opts);
        if (e.e1.type.ty == Tpointer) {
            if(e.op == TOK.plusPlus) {
                buf.writestring(".postInc()");
            }
            else if(e.op == TOK.minusMinus) {
                buf.writestring(".postDec()");
            }
            else 
                fprintf(stderr, "Pointer arithmetic: %s\n", e.toChars());
        }
        else 
            buf.writestring(Token.toString(e.op));
    }

    override void visit(PreExp e)
    {
        if (e.e1.type.ty == Tpointer) {
            expToBuffer(e.e1, precedence[e.op], buf, opts);
            if(e.op == TOK.prePlusPlus) {
                buf.writestring(".inc()");
            }
            else if(e.op == TOK.preMinusMinus) {
                buf.writestring(".dec()");
            }
            else 
                fprintf(stderr, "Pointer arithmetic: %s\n", e.toChars());
        }
        else {
            buf.writestring(Token.toString(e.op));
            expToBuffer(e.e1, precedence[e.op], buf, opts);
        }
    }

    override void visit(RemoveExp e)
    {
        expToBuffer(e.e1, PREC.primary, buf, opts);
        buf.writestring(".remove(");
        expToBuffer(e.e2, PREC.assign, buf, opts);
        buf.writeByte(')');
    }

    override void visit(CondExp e)
    {
        buf.writestring(e.econd.toJavaBool(opts));
        buf.writestring(" ? ");
        expToBuffer(e.e1, PREC.expr, buf, opts);
        buf.writestring(" : ");
        expToBuffer(e.e2, PREC.cond, buf, opts);
    }

    override void visit(DefaultInitExp e)
    {
        buf.writestring(Token.toString(e.subop));
    }

    override void visit(ClassReferenceExp e)
    {
        buf.writestring(e.value.toChars());
    }
}


private void expressionToBuffer(Expression e, OutBuffer* buf, ExprOpts opts)
{
    scope v = new toJavaExpressionVisitor(buf, opts);
    e.accept(v);
}

/**************************************************
 * Write expression out to buf, but wrap it
 * in ( ) if its precedence is less than pr.
 */
private void expToBuffer(Expression e, PREC pr, OutBuffer* buf, ExprOpts opts)
{
    debug
    {
        if (precedence[e.op] == PREC.zero)
            printf("precedence not defined for token '%s'\n", Token.toChars(e.op));
    }
    assert(precedence[e.op] != PREC.zero);
    assert(pr != PREC.zero);
    /* Despite precedence, we don't allow a<b<c expressions.
     * They must be parenthesized.
     */
    if (precedence[e.op] < pr || (pr == PREC.rel && precedence[e.op] == pr)
        || (pr >= PREC.or && pr <= PREC.and && precedence[e.op] == PREC.rel))
    {
        buf.writeByte('(');
        e.expressionToBuffer(buf, opts);
        buf.writeByte(')');
    }
    else
    {
        e.expressionToBuffer(buf, opts);
    }
}

/**************************************************
 * Write out argument list to buf.
 */
private void argsToBuffer(Expressions* expressions, OutBuffer* buf, ExprOpts opts, Expression basis = null)
{
    if (!expressions || !expressions.dim)
        return;
    foreach (i, el; *expressions)
    {
        if (i)
            buf.writestring(", ");
        if (!el)
            el = basis;
        if (el) {
            auto var = el.isVarExp();
            auto n = el.isNullExp();
            if (var && (cast(void*)var.var in opts.refParams)) {
                buf.writestring(var.var.ident.toString);
            }
            else if(n && n.type.ty == Tarray) {
                buf.writestring("new ");
                buf.writestring(n.type.toJava);
                buf.writestring("()");
            }
            else
                expToBuffer(el, PREC.assign, buf, opts);
        }
    }
}

private void sizeToBuffer(Expression e, OutBuffer* buf, ExprOpts opts)
{
    if (e.type == Type.tsize_t)
    {
        Expression ex = (e.op == TOK.cast_ ? (cast(CastExp)e).e1 : e);
        ex = ex.optimize(WANTvalue);
        const dinteger_t uval = ex.op == TOK.int64 ? ex.toInteger() : cast(dinteger_t)-1;
        if (cast(sinteger_t)uval >= 0)
        {
            dinteger_t sizemax;
            if (target.ptrsize == 4)
                sizemax = 0xFFFFFFFFU;
            else if (target.ptrsize == 8)
                sizemax = 0xFFFFFFFFFFFFFFFFUL;
            else
                assert(0);
            if (uval <= sizemax && uval <= 0x7FFFFFFFFFFFFFFFUL)
            {
                buf.print(uval);
                return;
            }
        }
    }
    expToBuffer(e, PREC.assign, buf, opts);
}

/**************************************************
 * An entry point to pretty-print type.
 */
private void typeToBuffer(Type t, const Identifier ident, OutBuffer* buf, Boxing boxing = Boxing.no)
{
    if (t is null) {
        buf.writestring("nothing");
        return;
    }
    if (auto tf = t.isTypeFunction())
    {
        visitFuncIdentWithPrefix(tf, ident, null, buf);
        return;
    }
    typeToBufferx(t, buf, boxing);
    if (ident)
    {
        buf.writeByte(' ');
        buf.writestring(ident.toString());
    }
}

enum Boxing {
    no = 0,
    yes
}

private void typeToBufferx(Type t, OutBuffer* buf, Boxing boxing = Boxing.no)
{
    void visitType(Type t)
    {
        printf("t = %p, ty = %d\n", t, t.ty);
        assert(0);
    }

    void visitError(TypeError t)
    {
        buf.writestring("_error_");
    }

    void visitBasic(TypeBasic t)
    {
        //printf("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        switch (t.ty) with(Boxing)
        {
        case Tvoid:
            buf.writestring("void");
            break;

        case Tint8:
        case Tuns8:
            if (boxing == yes) buf.writestring("Byte");
            else buf.writestring("byte");
            break;

        case Tint16:
        case Tuns16:
            if (boxing == yes) buf.writestring("Short");
            else buf.writestring("short");
            break;

        case Tint32:
        case Tuns32:
            if (boxing == yes) buf.writestring("Integer");
            else buf.writestring("int");
            break;

        case Tfloat32:
            if (boxing == yes) buf.writestring("Float");
            else buf.writestring("float");
            break;

        case Tfloat64:
        case Tfloat80:
            if (boxing == yes) buf.writestring("Double");
            else  buf.writestring("double");
            break;

        case Tint64:
        case Tuns64:
            if (boxing == yes) buf.writestring("Long");
            else buf.writestring("long");
            break;

        case Tbool:
            buf.writestring("boolean");
            break;

        case Tchar:
            buf.writestring("byte");
            break;

        case Twchar:
            buf.writestring("char");
            break;

        case Tdchar:
            if (boxing == yes) buf.writestring("Integer");
            else buf.writestring("int");
            break;

        default:
            import core.stdc.stdio;
            fprintf(stderr, "%s\n", t.toChars());
            assert(0, "Unexpected type in type-conversion ");
        }
    }

    void visitTraits(TypeTraits t)
    {
        //printf("TypeBasic::toCBuffer2(t.mod = %d)\n", t.mod);
        t.exp.expressionToBuffer(buf, ExprOpts.init);
    }

    void visitVector(TypeVector t)
    {
        //printf("TypeVector::toCBuffer2(t.mod = %d)\n", t.mod);
        assert(0);
        version(none) {
            buf.writestring("__vector(");
            typeToBufferx(t.basetype, buf);
            buf.writestring(")");
        }
    }

    void visitSArray(TypeSArray t)
    {
        if (t.next.ty == Tchar)
            buf.writestring("ByteSlice");
        else if (t.next.ty == Twchar)
            buf.writestring("CharSlice");
        else if (t.next.ty == Tdchar)
            buf.writestring("IntSlice");
        else if(t.next.ty == Tint32 || t.next.ty == Tuns32)
            buf.writestring("IntSlice");
        else {
            buf.writestring("Slice<");
            typeToBufferx(t.next, buf, Boxing.yes);
            buf.writestring(">");
        }
    }

    void visitDArray(TypeDArray t)
    {
        Type ut = t.castMod(0);
        if (ut.ty == Tarray && t.next.ty == Tchar || t.next.ty == Tvoid || t.next.ty == Tuns8 || t.next.ty == Tint8)
            buf.writestring("ByteSlice");
        else if (ut.ty == Tarray && t.next.ty == Twchar)
            buf.writestring("CharSlice");
        else if (ut.ty == Tarray && t.next.ty == Tdchar)
            buf.writestring("IntSlice");
        else if(t.next.ty == Tint32 || t.next.ty == Tuns32)
            buf.writestring("IntSlice");
        else
        {
        L1:
            buf.writestring("Slice<");
            typeToBufferx(t.next, buf, Boxing.yes);
            buf.writestring(">");
        }
    }

    void visitAArray(TypeAArray t)
    {
        buf.writestring("AA<");
        typeToBufferx(t.next, buf, Boxing.yes);
        buf.writeByte(',');
        typeToBufferx(t.index, buf, Boxing.yes);
        buf.writeByte('>');
    }

    void visitPointer(TypePointer t)
    {
        //printf("TypePointer::toCBuffer2() next = %d\n", t.next.ty);
        if (t.next.ty == Tfunction)
            visitFuncIdentWithPostfix(cast(TypeFunction)t.next, "function", buf);
        else
        {
            if (t.next.ty == Tchar || t.next.ty == Tvoid || t.next.ty == Tuns8 || t.next.ty == Tint8)
                buf.writestring("BytePtr");
            else if (t.next.ty == Twchar)
                buf.writestring("CharPtr");
            else if (t.next.ty == Tdchar) 
                buf.writestring("IntPtr");
            else if (t.next.ty == Tint32 || t.next.ty == Tuns32)
                buf.writestring("IntPtr");
            else if (t.next.ty == Tstruct || t.next.ty == Tclass)
                typeToBufferx(t.next, buf, Boxing.yes);
            else {
                buf.writestring("Ptr<");
                typeToBufferx(t.next, buf, Boxing.yes);
                buf.writestring(">");
            }
        }
    }

    void visitReference(TypeReference t)
    {
        assert(0);
        version(none) {
            typeToBufferx(t.next, buf);
            buf.writeByte('&');
        }
    }

    void visitFunction(TypeFunction t)
    {
        //printf("TypeFunction::toCBuffer2() t = %p, ref = %d\n", t, t.isref);
        visitFuncIdentWithPostfix(t, null, buf);
    }

    void visitDelegate(TypeDelegate t)
    {
        visitFuncIdentWithPostfix(cast(TypeFunction)t.next, "delegate", buf);
    }

    void visitTypeQualifiedHelper(TypeQualified t)
    {
        foreach (id; t.idents)
        {
            if (id.dyncast() == DYNCAST.dsymbol)
            {
                assert(false);
                version(none) {
                    buf.writeByte('.');
                    TemplateInstance ti = cast(TemplateInstance)id;
                    ti.dsymbolToBuffer(buf);
                }
            }
            else if (id.dyncast() == DYNCAST.expression)
            {
                buf.writeByte('[');
                (cast(Expression)id).expressionToBuffer(buf, ExprOpts.init);
                buf.writeByte(']');
            }
            else if (id.dyncast() == DYNCAST.type)
            {
                buf.writeByte('[');
                typeToBufferx(cast(Type)id, buf);
                buf.writeByte(']');
            }
            else
            {
                buf.writeByte('.');
                buf.writestring(id.toString());
            }
        }
    }

    void visitIdentifier(TypeIdentifier t)
    {
        buf.writestring(t.ident.toString());
        visitTypeQualifiedHelper(t);
    }

    void visitInstance(TypeInstance t)
    {
        t.tempinst.dsymbolToBuffer(buf);
        visitTypeQualifiedHelper(t);
    }

    void visitTypeof(TypeTypeof t)
    {
        buf.writestring("typeof(");
        t.exp.expressionToBuffer(buf, ExprOpts.init);
        buf.writeByte(')');
        visitTypeQualifiedHelper(t);
    }

    void visitReturn(TypeReturn t)
    {
        buf.writestring("typeof(return)");
        visitTypeQualifiedHelper(t);
    }

    void visitEnum(TypeEnum t)
    {
        buf.writestring(t.sym.toChars());
    }

    void visitStruct(TypeStruct t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent ? t.sym.parent.isTemplateInstance() : null;
        if (ti && ti.aliasdecl == t.sym) {
            buf.writestring(ti.name.symbol);
            buf.writestring("<");
            foreach(i, arg; (*ti.tiargs)[]) {
                if(i) buf.writestring(",");
                if (auto atype = isType(arg)) buf.writestring(atype.toJava);
                else buf.writestring(arg.toChars());
            }
            buf.writestring(">");
        }
        else
            buf.writestring(t.sym.ident.symbol);
    }

    void visitClass(TypeClass t)
    {
        // https://issues.dlang.org/show_bug.cgi?id=13776
        // Don't use ti.toAlias() to avoid forward reference error
        // while printing messages.
        TemplateInstance ti = t.sym.parent.isTemplateInstance();
        if (ti && ti.aliasdecl == t.sym)
            buf.writestring(ti.toChars());
        else
            buf.writestring(t.sym.toChars());
    }

    void visitTuple(TypeTuple t)
    {
        parametersToBuffer(ParameterList(t.arguments, VarArg.none), buf);
    }

    void visitSlice(TypeSlice t)
    {
        assert(false);
    }

    void visitNull(TypeNull t)
    {
        buf.writestring("Any");
    }

    switch (t.ty)
    {
        default:        return t.isTypeBasic() ?
                                visitBasic(cast(TypeBasic)t) :
                                visitType(t);

        case Terror:     return visitError(cast(TypeError)t);
        case Ttraits:    return visitTraits(cast(TypeTraits)t);
        case Tvector:    return visitVector(cast(TypeVector)t);
        case Tsarray:    return visitSArray(cast(TypeSArray)t);
        case Tarray:     return visitDArray(cast(TypeDArray)t);
        case Taarray:    return visitAArray(cast(TypeAArray)t);
        case Tpointer:   return visitPointer(cast(TypePointer)t);
        case Treference: return visitReference(cast(TypeReference)t);
        case Tfunction:  return visitFunction(cast(TypeFunction)t);
        case Tdelegate:  return visitDelegate(cast(TypeDelegate)t);
        case Tident:     return visitIdentifier(cast(TypeIdentifier)t);
        case Tinstance:  return visitInstance(cast(TypeInstance)t);
        case Ttypeof:    return visitTypeof(cast(TypeTypeof)t);
        case Treturn:    return visitReturn(cast(TypeReturn)t);
        case Tenum:      return visitEnum(cast(TypeEnum)t);
        case Tstruct:    return visitStruct(cast(TypeStruct)t);
        case Tclass:     return visitClass(cast(TypeClass)t);
        case Ttuple:     return visitTuple (cast(TypeTuple)t);
        case Tslice:     return visitSlice(cast(TypeSlice)t);
        case Tnull:      return visitNull(cast(TypeNull)t);
    }
}

private void parametersToBuffer(ParameterList pl, OutBuffer* buf, Boxing boxing = Boxing.no)
{
    foreach (i; 0 .. pl.length)
    {
        if (i)
            buf.writestring(", ");
        pl[i].parameterToBuffer(buf, boxing);
    }
    final switch (pl.varargs)
    {
        case VarArg.none:
            break;

        case VarArg.variadic:
            if (pl.length == 0)
                goto case VarArg.typesafe;
            buf.writestring(", ...");
            break;

        case VarArg.typesafe:
            buf.writestring("...");
            break;
    }
}


private void visitFuncIdentWithPostfix(TypeFunction t, const char[] ident, OutBuffer* buf)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;
    if (t.parameterList.length > 0)
        buf.printf("Function%d<", t.parameterList.length);
    else
        buf.writestring("Function<");
    foreach(i, p; *t.parameterList) {
        if (i) buf.writestring(",");
        typeToBuffer(p.type, null, buf, Boxing.yes);
    }
    buf.writestring(",");
    if (t.next)
    {
        typeToBuffer(t.next, null, buf, Boxing.yes);
    }
    else 
        buf.writestring("Void");
    buf.writestring(">");
    t.inuse--;
}


private void visitFuncIdentWithPrefix(TypeFunction t, const Identifier ident, TemplateDeclaration td, OutBuffer* buf)
{
    if (t.inuse)
    {
        t.inuse = 2; // flag error to caller
        return;
    }
    t.inuse++;

    if (ident)
        buf.writestring(ident.toHChars2());
    if (ident && ident.toHChars2() != ident.toChars())
    {
        // Don't print return type for ctor, dtor, unittest, etc
    }
    else if (t.next)
    {
        if (ident)
            buf.writestring(": ");
        typeToBuffer(t.next, null, buf);
    }
    if (td)
    {
        assert(false);
        version(none) {
            buf.writeByte('(');
            foreach (i, p; *td.origParameters)
            {
                if (i)
                    buf.writestring(", ");
                p.templateParameterToBuffer(buf, hgs);
            }
            buf.writeByte(')');
        }
    }
    parametersToBuffer(t.parameterList, buf);
    t.inuse--;
}

private void dsymbolToBuffer(Dsymbol s, OutBuffer* buf) {
    buf.writestring(s.toChars());
}


/***********************************************************
 * Write parameter `p` to buffer `buf`.
 * Params:
 *      p = parameter to serialize
 *      buf = buffer to write it to
 *      hgs = context
 */
private void parameterToBuffer(Parameter p, OutBuffer* buf, Boxing boxing = Boxing.no)
{
    if (p.type.ty == Tident &&
             (cast(TypeIdentifier)p.type).ident.toString().length > 3 &&
             strncmp((cast(TypeIdentifier)p.type).ident.toChars(), "__T", 3) == 0)
    {
        // print parameter name, instead of undetermined type parameter
        buf.writestring(p.ident.toString());
    }
    else
    {
        typeToBuffer(p.type, p.ident, buf, boxing);
    }
    auto opts = ExprOpts(p.type.ty == Tpointer && p.type.nextOf().ty == Tchar);

    if (p.defaultArg)
    {
        buf.writestring(" = ");
        p.defaultArg.expToBuffer(PREC.assign, buf, opts);
    }
}
