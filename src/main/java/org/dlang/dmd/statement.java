package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.astcodegen.*;
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.canthrow.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dinterpret.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.gluelayer.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.parse.*;
import static org.dlang.dmd.sapply.*;
import static org.dlang.dmd.sideeffect.*;
import static org.dlang.dmd.statementsem.*;
import static org.dlang.dmd.staticassert.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.visitor.*;

public class statement {
    private static class UsesEH extends StoppableVisitor
    {
        // Erasure: visit<Statement>
        public  void visit(Statement s) {
        }

        // Erasure: visit<TryCatchStatement>
        public  void visit(TryCatchStatement s) {
            this.stop = true;
        }

        // Erasure: visit<TryFinallyStatement>
        public  void visit(TryFinallyStatement s) {
            this.stop = true;
        }

        // Erasure: visit<ScopeGuardStatement>
        public  void visit(ScopeGuardStatement s) {
            this.stop = true;
        }

        // Erasure: visit<SynchronizedStatement>
        public  void visit(SynchronizedStatement s) {
            this.stop = true;
        }

        // Erasure: __ctor<>
        public  UsesEH() {
            super();
        }

    }
    private static class ComeFrom extends StoppableVisitor
    {
        // Erasure: visit<Statement>
        public  void visit(Statement s) {
        }

        // Erasure: visit<CaseStatement>
        public  void visit(CaseStatement s) {
            this.stop = true;
        }

        // Erasure: visit<DefaultStatement>
        public  void visit(DefaultStatement s) {
            this.stop = true;
        }

        // Erasure: visit<LabelStatement>
        public  void visit(LabelStatement s) {
            this.stop = true;
        }

        // Erasure: visit<AsmStatement>
        public  void visit(AsmStatement s) {
            this.stop = true;
        }

        // Erasure: __ctor<>
        public  ComeFrom() {
            super();
        }

    }
    private static class HasCode extends StoppableVisitor
    {
        // Erasure: visit<Statement>
        public  void visit(Statement s) {
            this.stop = true;
        }

        // Erasure: visit<ExpStatement>
        public  void visit(ExpStatement s) {
            if ((s.exp != null))
            {
                this.stop = s.exp.hasCode();
            }
        }

        // Erasure: visit<CompoundStatement>
        public  void visit(CompoundStatement s) {
        }

        // Erasure: visit<ScopeStatement>
        public  void visit(ScopeStatement s) {
        }

        // Erasure: visit<ImportStatement>
        public  void visit(ImportStatement s) {
        }

        // Erasure: __ctor<>
        public  HasCode() {
            super();
        }

    }
    private static class ToStmt extends Visitor
    {
        private Statement result = null;
        // Erasure: visitMembers<Loc, Ptr>
        public  Statement visitMembers(Loc loc, DArray<Dsymbol> a) {
            if (a == null)
            {
                return null;
            }
            DArray<Statement> statements = new DArray<Statement>();
            {
                Slice<Dsymbol> __r1589 = (a).opSlice().copy();
                int __key1590 = 0;
                for (; (__key1590 < __r1589.getLength());__key1590 += 1) {
                    Dsymbol s = __r1589.get(__key1590);
                    (statements).push(toStatement(s));
                }
            }
            return new CompoundStatement(loc, statements);
        }

        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol s) {
            error(Loc.initial, new BytePtr("Internal Compiler Error: cannot mixin %s `%s`\n"), s.kind(), s.toChars());
            this.result = new ErrorStatement();
        }

        // Erasure: visit<TemplateMixin>
        public  void visit(TemplateMixin tm) {
            DArray<Statement> a = new DArray<Statement>();
            {
                Slice<Dsymbol> __r1591 = (tm.members).opSlice().copy();
                int __key1592 = 0;
                for (; (__key1592 < __r1591.getLength());__key1592 += 1) {
                    Dsymbol m = __r1591.get(__key1592);
                    Statement s = toStatement(m);
                    if (s != null)
                    {
                        (a).push(s);
                    }
                }
            }
            this.result = new CompoundStatement(tm.loc, a);
        }

        // Erasure: declStmt<Dsymbol>
        public  Statement declStmt(Dsymbol s) {
            DeclarationExp de = new DeclarationExp(s.loc, s);
            de.type.value = Type.tvoid;
            return new ExpStatement(s.loc, de);
        }

        // Erasure: visit<VarDeclaration>
        public  void visit(VarDeclaration d) {
            this.result = this.declStmt(d);
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(AggregateDeclaration d) {
            this.result = this.declStmt(d);
        }

        // Erasure: visit<FuncDeclaration>
        public  void visit(FuncDeclaration d) {
            this.result = this.declStmt(d);
        }

        // Erasure: visit<EnumDeclaration>
        public  void visit(EnumDeclaration d) {
            this.result = this.declStmt(d);
        }

        // Erasure: visit<AliasDeclaration>
        public  void visit(AliasDeclaration d) {
            this.result = this.declStmt(d);
        }

        // Erasure: visit<TemplateDeclaration>
        public  void visit(TemplateDeclaration d) {
            this.result = this.declStmt(d);
        }

        // Erasure: visit<StorageClassDeclaration>
        public  void visit(StorageClassDeclaration d) {
            this.result = this.visitMembers(d.loc, d.decl);
        }

        // Erasure: visit<DeprecatedDeclaration>
        public  void visit(DeprecatedDeclaration d) {
            this.result = this.visitMembers(d.loc, d.decl);
        }

        // Erasure: visit<LinkDeclaration>
        public  void visit(LinkDeclaration d) {
            this.result = this.visitMembers(d.loc, d.decl);
        }

        // Erasure: visit<ProtDeclaration>
        public  void visit(ProtDeclaration d) {
            this.result = this.visitMembers(d.loc, d.decl);
        }

        // Erasure: visit<AlignDeclaration>
        public  void visit(AlignDeclaration d) {
            this.result = this.visitMembers(d.loc, d.decl);
        }

        // Erasure: visit<UserAttributeDeclaration>
        public  void visit(UserAttributeDeclaration d) {
            this.result = this.visitMembers(d.loc, d.decl);
        }

        // Erasure: visit<StaticAssert>
        public  void visit(StaticAssert s) {
        }

        // Erasure: visit<Import>
        public  void visit(Import s) {
        }

        // Erasure: visit<PragmaDeclaration>
        public  void visit(PragmaDeclaration d) {
        }

        // Erasure: visit<ConditionalDeclaration>
        public  void visit(ConditionalDeclaration d) {
            this.result = this.visitMembers(d.loc, d.include(null));
        }

        // Erasure: visit<StaticForeachDeclaration>
        public  void visit(StaticForeachDeclaration d) {
            assert((d.sfe != null) && d.sfe.aggrfe != null ^ d.sfe.rangefe != null);
            (d.sfe.aggrfe != null ? ptr(d.sfe.aggrfe._body) : ptr(d.sfe.rangefe._body)).set(0, this.visitMembers(d.loc, d.decl));
            this.result = new StaticForeachStatement(d.loc, d.sfe);
        }

        // Erasure: visit<CompileDeclaration>
        public  void visit(CompileDeclaration d) {
            this.result = this.visitMembers(d.loc, d.include(null));
        }


        public ToStmt() {}
    }

    // Erasure: getThrowable<>
    public static TypeIdentifier getThrowable() {
        TypeIdentifier tid = new TypeIdentifier(Loc.initial, Id.empty);
        tid.addIdent(Id.object);
        tid.addIdent(Id.Throwable);
        return tid;
    }

    // Erasure: getException<>
    public static TypeIdentifier getException() {
        TypeIdentifier tid = new TypeIdentifier(Loc.initial, Id.empty);
        tid.addIdent(Id.object);
        tid.addIdent(Id.Exception);
        return tid;
    }

    public static abstract class Statement extends ASTNode
    {
        public Loc loc = new Loc();
        // Erasure: dyncast<>
        public  int dyncast() {
            return DYNCAST.statement;
        }

        // Erasure: __ctor<Loc>
        public  Statement(Loc loc) {
            super();
            this.loc.opAssign(loc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            throw new AssertionError("Unreachable code!");
        }

        // Erasure: arraySyntaxCopy<Ptr>
        public static DArray<Statement> arraySyntaxCopy(DArray<Statement> a) {
            DArray<Statement> b = null;
            if (a != null)
            {
                b = pcopy((a).copy());
                {
                    Slice<Statement> __r1588 = (a).opSlice().copy();
                    int __key1587 = 0;
                    for (; (__key1587 < __r1588.getLength());__key1587 += 1) {
                        Statement s = __r1588.get(__key1587);
                        int i = __key1587;
                        b.set(i, s != null ? s.syntaxCopy() : null);
                    }
                }
            }
            return b;
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                toCBuffer(this, buf.value, ptr(hgs));
                return buf.value.extractChars();
            }
            finally {
            }
        }

        // Erasure: error<Ptr>
        public  void error(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            verror(this.loc, format_ref.value, new RawSlice<>(ap), null, null, new BytePtr("Error: "));
        }

        // Erasure: warning<Ptr>
        public  void warning(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            vwarning(this.loc, format_ref.value, new RawSlice<>(ap));
        }

        // Erasure: deprecation<Ptr>
        public  void deprecation(BytePtr format, Object... ap) {
            Ref<BytePtr> format_ref = ref(format);
            vdeprecation(this.loc, format_ref.value, new RawSlice<>(ap), null, null);
        }

        // Erasure: getRelatedLabeled<>
        public  Statement getRelatedLabeled() {
            return this;
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return false;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return false;
        }

        // Erasure: usesEH<>
        public  boolean usesEH() {
            Statement __self = this;
            // skipping duplicate class UsesEH
            UsesEH ueh = new UsesEH();
            return walkPostorder(this, ueh);
        }

        // Erasure: comeFrom<>
        public  boolean comeFrom() {
            Statement __self = this;
            // skipping duplicate class ComeFrom
            ComeFrom cf = new ComeFrom();
            return walkPostorder(this, cf);
        }

        // Erasure: hasCode<>
        public  boolean hasCode() {
            Statement __self = this;
            // skipping duplicate class HasCode
            HasCode hc = new HasCode();
            return walkPostorder(this, hc);
        }

        // Erasure: scopeCode<Ptr, Ptr, Ptr, Ptr>
        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            sentry.set(0, null);
            sexception.set(0, null);
            sfinally.set(0, null);
            return this;
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            return null;
        }

        // Erasure: last<>
        public  Statement last() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: isErrorStatement<>
        public  ErrorStatement isErrorStatement() {
            return null;
        }

        // Erasure: isScopeStatement<>
        public  ScopeStatement isScopeStatement() {
            return null;
        }

        // Erasure: isExpStatement<>
        public  ExpStatement isExpStatement() {
            return null;
        }

        // Erasure: isCompoundStatement<>
        public  CompoundStatement isCompoundStatement() {
            return null;
        }

        // Erasure: isReturnStatement<>
        public  ReturnStatement isReturnStatement() {
            return null;
        }

        // Erasure: isIfStatement<>
        public  IfStatement isIfStatement() {
            return null;
        }

        // Erasure: isCaseStatement<>
        public  CaseStatement isCaseStatement() {
            return null;
        }

        // Erasure: isDefaultStatement<>
        public  DefaultStatement isDefaultStatement() {
            return null;
        }

        // Erasure: isLabelStatement<>
        public  LabelStatement isLabelStatement() {
            return null;
        }

        // Erasure: isGotoDefaultStatement<>
        public  GotoDefaultStatement isGotoDefaultStatement() {
            return null;
        }

        // Erasure: isGotoCaseStatement<>
        public  GotoCaseStatement isGotoCaseStatement() {
            return null;
        }

        // Erasure: isBreakStatement<>
        public  BreakStatement isBreakStatement() {
            return null;
        }

        // Erasure: isDtorExpStatement<>
        public  DtorExpStatement isDtorExpStatement() {
            return null;
        }

        // Erasure: isForwardingStatement<>
        public  ForwardingStatement isForwardingStatement() {
            return null;
        }


        public Statement() {}

        public abstract Statement copy();
    }
    public static class ErrorStatement extends Statement
    {
        // Erasure: __ctor<>
        public  ErrorStatement() {
            super(Loc.initial);
            assert((global.gaggedErrors != 0) || (global.errors != 0));
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return this;
        }

        // Erasure: isErrorStatement<>
        public  ErrorStatement isErrorStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ErrorStatement copy() {
            ErrorStatement that = new ErrorStatement();
            that.loc = this.loc;
            return that;
        }
    }
    public static class PeelStatement extends Statement
    {
        public Ref<Statement> s = ref(null);
        // Erasure: __ctor<Statement>
        public  PeelStatement(Statement s) {
            super(s.loc);
            this.s.value = s;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PeelStatement() {}

        public PeelStatement copy() {
            PeelStatement that = new PeelStatement();
            that.s = this.s;
            that.loc = this.loc;
            return that;
        }
    }
    // Erasure: toStatement<Dsymbol>
    public static Statement toStatement(Dsymbol s) {
        // skipping duplicate class ToStmt
        if (s == null)
        {
            return null;
        }
        ToStmt v = new ToStmt();
        s.accept(v);
        return v.result;
    }

    public static class ExpStatement extends Statement
    {
        public Expression exp = null;
        // Erasure: __ctor<Loc, Expression>
        public  ExpStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        // Erasure: __ctor<Loc, Dsymbol>
        public  ExpStatement(Loc loc, Dsymbol declaration) {
            super(loc);
            this.exp = new DeclarationExp(loc, declaration);
        }

        // Erasure: create<Loc, Expression>
        public static ExpStatement create(Loc loc, Expression exp) {
            return new ExpStatement(loc, exp);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ExpStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null);
        }

        // Erasure: scopeCode<Ptr, Ptr, Ptr, Ptr>
        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            sentry.set(0, null);
            sexception.set(0, null);
            sfinally.set(0, null);
            if ((this.exp != null) && ((this.exp.op & 0xFF) == 38))
            {
                DeclarationExp de = ((DeclarationExp)this.exp);
                VarDeclaration v = de.declaration.isVarDeclaration();
                if ((v != null) && !v.isDataseg())
                {
                    if (v.needsScopeDtor())
                    {
                        sfinally.set(0, new DtorExpStatement(this.loc, v.edtor, v));
                        v.storage_class |= 16777216L;
                    }
                }
            }
            return this;
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            if ((this.exp != null) && ((this.exp.op & 0xFF) == 38))
            {
                Dsymbol d = (((DeclarationExp)this.exp)).declaration;
                {
                    TemplateMixin tm = d.isTemplateMixin();
                    if ((tm) != null)
                    {
                        Expression e = expressionSemantic(this.exp, sc);
                        if (((e.op & 0xFF) == 127) || tm.errors)
                        {
                            DArray<Statement> a = new DArray<Statement>();
                            (a).push(new ErrorStatement());
                            return a;
                        }
                        assert(tm.members != null);
                        Statement s = toStatement(tm);
                        DArray<Statement> a = new DArray<Statement>();
                        (a).push(s);
                        return a;
                    }
                }
            }
            return null;
        }

        // Erasure: isExpStatement<>
        public  ExpStatement isExpStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ExpStatement() {}

        public ExpStatement copy() {
            ExpStatement that = new ExpStatement();
            that.exp = this.exp;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DtorExpStatement extends ExpStatement
    {
        public VarDeclaration var = null;
        // Erasure: __ctor<Loc, Expression, VarDeclaration>
        public  DtorExpStatement(Loc loc, Expression exp, VarDeclaration var) {
            super(loc, exp);
            this.var = var;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new DtorExpStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null, this.var);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: isDtorExpStatement<>
        public  DtorExpStatement isDtorExpStatement() {
            return this;
        }


        public DtorExpStatement() {}

        public DtorExpStatement copy() {
            DtorExpStatement that = new DtorExpStatement();
            that.var = this.var;
            that.exp = this.exp;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompileStatement extends Statement
    {
        public DArray<Expression> exps = null;
        // Erasure: __ctor<Loc, Expression>
        public  CompileStatement(Loc loc, Expression exp) {
            DArray<Expression> exps = new DArray<Expression>();
            (exps).push(exp);
            this(loc, exps);
        }

        // Erasure: __ctor<Loc, Ptr>
        public  CompileStatement(Loc loc, DArray<Expression> exps) {
            super(loc);
            this.exps = pcopy(exps);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new CompileStatement(this.loc, Expression.arraySyntaxCopy(this.exps));
        }

        // Erasure: compileIt<Ptr>
        public  DArray<Statement> compileIt(Ptr<Scope> sc) {
            CompileStatement __self = this;
            Function0<DArray<Statement>> errorStatements = new Function0<DArray<Statement>>() {
                public DArray<Statement> invoke() {
                 {
                    DArray<Statement> a = new DArray<Statement>();
                    (a).push(new ErrorStatement());
                    return a;
                }}

            };
            Ref<OutBuffer> buf = ref(new OutBuffer());
            try {
                if (expressionsToString(buf, sc, this.exps))
                {
                    return errorStatements.invoke();
                }
                int errors = global.errors;
                int len = buf.value.offset;
                ByteSlice str = buf.value.extractChars().slice(0,len).copy();
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                try {
                    ParserASTCodegen p = new ParserASTCodegen(this.loc, (sc.get())._module, str, false, diagnosticReporter);
                    try {
                        p.nextToken();
                        DArray<Statement> a = new DArray<Statement>();
                        for (; ((p.token.value.value & 0xFF) != 11);){
                            Statement s = p.parseStatement(9, null, null);
                            if ((s == null) || p.errors())
                            {
                                assert(!p.errors() || (global.errors != errors));
                                return errorStatements.invoke();
                            }
                            (a).push(s);
                        }
                        return a;
                    }
                    finally {
                    }
                }
                finally {
                }
            }
            finally {
            }
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            return this.compileIt(sc);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompileStatement() {}

        public CompileStatement copy() {
            CompileStatement that = new CompileStatement();
            that.exps = this.exps;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompoundStatement extends Statement
    {
        public DArray<Statement> statements = null;
        // Erasure: __ctor<Loc, Ptr>
        public  CompoundStatement(Loc loc, DArray<Statement> statements) {
            super(loc);
            this.statements = pcopy(statements);
        }

        // Erasure: __ctor<Loc, Array>
        public  CompoundStatement(Loc loc, Slice<Statement> sts) {
            super(loc);
            this.statements = pcopy(new DArray<Statement>());
            (this.statements).reserve(sts.getLength());
            {
                Slice<Statement> __r1593 = sts.copy();
                int __key1594 = 0;
                for (; (__key1594 < __r1593.getLength());__key1594 += 1) {
                    Statement s = __r1593.get(__key1594);
                    (this.statements).push(s);
                }
            }
        }

        // Erasure: create<Loc, Statement, Statement>
        public static CompoundStatement create(Loc loc, Statement s1, Statement s2) {
            return new CompoundStatement(loc, slice(new Statement[]{s1, s2}));
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new CompoundStatement(this.loc, Statement.arraySyntaxCopy(this.statements));
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            return this.statements;
        }

        // Erasure: isReturnStatement<>
        public  ReturnStatement isReturnStatement() {
            ReturnStatement rs = null;
            {
                Slice<Statement> __r1595 = (this.statements).opSlice().copy();
                int __key1596 = 0;
                for (; (__key1596 < __r1595.getLength());__key1596 += 1) {
                    Statement s = __r1595.get(__key1596);
                    if (s != null)
                    {
                        rs = s.isReturnStatement();
                        if (rs != null)
                        {
                            break;
                        }
                    }
                }
            }
            return rs;
        }

        // Erasure: last<>
        public  Statement last() {
            Statement s = null;
            {
                int i = (this.statements).length;
                for (; i != 0;i -= 1){
                    s = (this.statements).get(i - 1);
                    if (s != null)
                    {
                        s = s.last();
                        if (s != null)
                        {
                            break;
                        }
                    }
                }
            }
            return s;
        }

        // Erasure: isCompoundStatement<>
        public  CompoundStatement isCompoundStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompoundStatement() {}

        public CompoundStatement copy() {
            CompoundStatement that = new CompoundStatement();
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompoundDeclarationStatement extends CompoundStatement
    {
        // Erasure: __ctor<Loc, Ptr>
        public  CompoundDeclarationStatement(Loc loc, DArray<Statement> statements) {
            super(loc, statements);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            DArray<Statement> a = new DArray<Statement>((this.statements).length);
            {
                Slice<Statement> __r1598 = (this.statements).opSlice().copy();
                int __key1597 = 0;
                for (; (__key1597 < __r1598.getLength());__key1597 += 1) {
                    Statement s = __r1598.get(__key1597);
                    int i = __key1597;
                    a.set(i, s != null ? s.syntaxCopy() : null);
                }
            }
            return new CompoundDeclarationStatement(this.loc, a);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompoundDeclarationStatement() {}

        public CompoundDeclarationStatement copy() {
            CompoundDeclarationStatement that = new CompoundDeclarationStatement();
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class UnrolledLoopStatement extends Statement
    {
        public DArray<Statement> statements = null;
        // Erasure: __ctor<Loc, Ptr>
        public  UnrolledLoopStatement(Loc loc, DArray<Statement> statements) {
            super(loc);
            this.statements = pcopy(statements);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            DArray<Statement> a = new DArray<Statement>((this.statements).length);
            {
                Slice<Statement> __r1600 = (this.statements).opSlice().copy();
                int __key1599 = 0;
                for (; (__key1599 < __r1600.getLength());__key1599 += 1) {
                    Statement s = __r1600.get(__key1599);
                    int i = __key1599;
                    a.set(i, s != null ? s.syntaxCopy() : null);
                }
            }
            return new UnrolledLoopStatement(this.loc, a);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public UnrolledLoopStatement() {}

        public UnrolledLoopStatement copy() {
            UnrolledLoopStatement that = new UnrolledLoopStatement();
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ScopeStatement extends Statement
    {
        public Ref<Statement> statement = ref(null);
        public Loc endloc = new Loc();
        // Erasure: __ctor<Loc, Statement, Loc>
        public  ScopeStatement(Loc loc, Statement statement, Loc endloc) {
            super(loc);
            this.statement.value = statement;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ScopeStatement(this.loc, this.statement.value != null ? this.statement.value.syntaxCopy() : null, this.endloc);
        }

        // Erasure: isScopeStatement<>
        public  ScopeStatement isScopeStatement() {
            return this;
        }

        // Erasure: isReturnStatement<>
        public  ReturnStatement isReturnStatement() {
            if (this.statement.value != null)
            {
                return this.statement.value.isReturnStatement();
            }
            return null;
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return this.statement.value != null ? this.statement.value.hasBreak() : false;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return this.statement.value != null ? this.statement.value.hasContinue() : false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeStatement() {}

        public ScopeStatement copy() {
            ScopeStatement that = new ScopeStatement();
            that.statement = this.statement;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForwardingStatement extends Statement
    {
        public ForwardingScopeDsymbol sym = null;
        public Statement statement = null;
        // Erasure: __ctor<Loc, ForwardingScopeDsymbol, Statement>
        public  ForwardingStatement(Loc loc, ForwardingScopeDsymbol sym, Statement statement) {
            super(loc);
            this.sym = sym;
            assert(statement != null);
            this.statement = statement;
        }

        // Erasure: __ctor<Loc, Statement>
        public  ForwardingStatement(Loc loc, Statement statement) {
            ForwardingScopeDsymbol sym = new ForwardingScopeDsymbol(null);
            sym.symtab = new DsymbolTable();
            this(loc, sym, statement);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ForwardingStatement(this.loc, this.statement.syntaxCopy());
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            if (this.statement == null)
            {
                return null;
            }
            sc = pcopy((sc.get()).push(this.sym));
            DArray<Statement> a = this.statement.flatten(sc);
            sc = pcopy((sc.get()).pop());
            if (a == null)
            {
                return a;
            }
            DArray<Statement> b = new DArray<Statement>((a).length);
            {
                Slice<Statement> __r1602 = (a).opSlice().copy();
                int __key1601 = 0;
                for (; (__key1601 < __r1602.getLength());__key1601 += 1) {
                    Statement s = __r1602.get(__key1601);
                    int i = __key1601;
                    b.set(i, s != null ? new ForwardingStatement(s.loc, this.sym, s) : null);
                }
            }
            return b;
        }

        // Erasure: isForwardingStatement<>
        public  ForwardingStatement isForwardingStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForwardingStatement() {}

        public ForwardingStatement copy() {
            ForwardingStatement that = new ForwardingStatement();
            that.sym = this.sym;
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class WhileStatement extends Statement
    {
        public Expression condition = null;
        public Ref<Statement> _body = ref(null);
        public Loc endloc = new Loc();
        // Erasure: __ctor<Loc, Expression, Statement, Loc>
        public  WhileStatement(Loc loc, Expression condition, Statement _body, Loc endloc) {
            super(loc);
            this.condition = condition;
            this._body.value = _body;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new WhileStatement(this.loc, this.condition.syntaxCopy(), this._body.value != null ? this._body.value.syntaxCopy() : null, this.endloc);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public WhileStatement() {}

        public WhileStatement copy() {
            WhileStatement that = new WhileStatement();
            that.condition = this.condition;
            that._body = this._body;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DoStatement extends Statement
    {
        public Ref<Statement> _body = ref(null);
        public Expression condition = null;
        public Loc endloc = new Loc();
        // Erasure: __ctor<Loc, Statement, Expression, Loc>
        public  DoStatement(Loc loc, Statement _body, Expression condition, Loc endloc) {
            super(loc);
            this._body.value = _body;
            this.condition = condition;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new DoStatement(this.loc, this._body.value != null ? this._body.value.syntaxCopy() : null, this.condition.syntaxCopy(), this.endloc);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DoStatement() {}

        public DoStatement copy() {
            DoStatement that = new DoStatement();
            that._body = this._body;
            that.condition = this.condition;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForStatement extends Statement
    {
        public Ref<Statement> _init = ref(null);
        public Expression condition = null;
        public Expression increment = null;
        public Ref<Statement> _body = ref(null);
        public Loc endloc = new Loc();
        public Statement relatedLabeled = null;
        // Erasure: __ctor<Loc, Statement, Expression, Expression, Statement, Loc>
        public  ForStatement(Loc loc, Statement _init, Expression condition, Expression increment, Statement _body, Loc endloc) {
            super(loc);
            this._init.value = _init;
            this.condition = condition;
            this.increment = increment;
            this._body.value = _body;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ForStatement(this.loc, this._init.value != null ? this._init.value.syntaxCopy() : null, this.condition != null ? this.condition.syntaxCopy() : null, this.increment != null ? this.increment.syntaxCopy() : null, this._body.value.syntaxCopy(), this.endloc);
        }

        // Erasure: scopeCode<Ptr, Ptr, Ptr, Ptr>
        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            this.scopeCode(sc, sentry, sexception, sfinally);
            return this;
        }

        // Erasure: getRelatedLabeled<>
        public  Statement getRelatedLabeled() {
            return this.relatedLabeled != null ? this.relatedLabeled : this;
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForStatement() {}

        public ForStatement copy() {
            ForStatement that = new ForStatement();
            that._init = this._init;
            that.condition = this.condition;
            that.increment = this.increment;
            that._body = this._body;
            that.endloc = this.endloc;
            that.relatedLabeled = this.relatedLabeled;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForeachStatement extends Statement
    {
        public byte op = 0;
        public DArray<Parameter> parameters = null;
        public Ref<Expression> aggr = ref(null);
        public Ref<Statement> _body = ref(null);
        public Loc endloc = new Loc();
        public VarDeclaration key = null;
        public VarDeclaration value = null;
        public FuncDeclaration func = null;
        public DArray<Statement> cases = null;
        public DArray<ScopeStatement> gotos = null;
        // Erasure: __ctor<Loc, byte, Ptr, Expression, Statement, Loc>
        public  ForeachStatement(Loc loc, byte op, DArray<Parameter> parameters, Expression aggr, Statement _body, Loc endloc) {
            super(loc);
            this.op = op;
            this.parameters = pcopy(parameters);
            this.aggr.value = aggr;
            this._body.value = _body;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ForeachStatement(this.loc, this.op, Parameter.arraySyntaxCopy(this.parameters), this.aggr.value.syntaxCopy(), this._body.value != null ? this._body.value.syntaxCopy() : null, this.endloc);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForeachStatement() {}

        public ForeachStatement copy() {
            ForeachStatement that = new ForeachStatement();
            that.op = this.op;
            that.parameters = this.parameters;
            that.aggr = this.aggr;
            that._body = this._body;
            that.endloc = this.endloc;
            that.key = this.key;
            that.value = this.value;
            that.func = this.func;
            that.cases = this.cases;
            that.gotos = this.gotos;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ForeachRangeStatement extends Statement
    {
        public byte op = 0;
        public Parameter prm = null;
        public Expression lwr = null;
        public Expression upr = null;
        public Ref<Statement> _body = ref(null);
        public Loc endloc = new Loc();
        public VarDeclaration key = null;
        // Erasure: __ctor<Loc, byte, Parameter, Expression, Expression, Statement, Loc>
        public  ForeachRangeStatement(Loc loc, byte op, Parameter prm, Expression lwr, Expression upr, Statement _body, Loc endloc) {
            super(loc);
            this.op = op;
            this.prm = prm;
            this.lwr = lwr;
            this.upr = upr;
            this._body.value = _body;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ForeachRangeStatement(this.loc, this.op, this.prm.syntaxCopy(), this.lwr.syntaxCopy(), this.upr.syntaxCopy(), this._body.value != null ? this._body.value.syntaxCopy() : null, this.endloc);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return true;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ForeachRangeStatement() {}

        public ForeachRangeStatement copy() {
            ForeachRangeStatement that = new ForeachRangeStatement();
            that.op = this.op;
            that.prm = this.prm;
            that.lwr = this.lwr;
            that.upr = this.upr;
            that._body = this._body;
            that.endloc = this.endloc;
            that.key = this.key;
            that.loc = this.loc;
            return that;
        }
    }
    public static class IfStatement extends Statement
    {
        public Parameter prm = null;
        public Expression condition = null;
        public Ref<Statement> ifbody = ref(null);
        public Ref<Statement> elsebody = ref(null);
        public VarDeclaration match = null;
        public Loc endloc = new Loc();
        // Erasure: __ctor<Loc, Parameter, Expression, Statement, Statement, Loc>
        public  IfStatement(Loc loc, Parameter prm, Expression condition, Statement ifbody, Statement elsebody, Loc endloc) {
            super(loc);
            this.prm = prm;
            this.condition = condition;
            this.ifbody.value = ifbody;
            this.elsebody.value = elsebody;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new IfStatement(this.loc, this.prm != null ? this.prm.syntaxCopy() : null, this.condition.syntaxCopy(), this.ifbody.value != null ? this.ifbody.value.syntaxCopy() : null, this.elsebody.value != null ? this.elsebody.value.syntaxCopy() : null, this.endloc);
        }

        // Erasure: isIfStatement<>
        public  IfStatement isIfStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public IfStatement() {}

        public IfStatement copy() {
            IfStatement that = new IfStatement();
            that.prm = this.prm;
            that.condition = this.condition;
            that.ifbody = this.ifbody;
            that.elsebody = this.elsebody;
            that.match = this.match;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ConditionalStatement extends Statement
    {
        public Condition condition = null;
        public Statement ifbody = null;
        public Statement elsebody = null;
        // Erasure: __ctor<Loc, Condition, Statement, Statement>
        public  ConditionalStatement(Loc loc, Condition condition, Statement ifbody, Statement elsebody) {
            super(loc);
            this.condition = condition;
            this.ifbody = ifbody;
            this.elsebody = elsebody;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ConditionalStatement(this.loc, this.condition.syntaxCopy(), this.ifbody.syntaxCopy(), this.elsebody != null ? this.elsebody.syntaxCopy() : null);
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            Statement s = null;
            if (this.condition.include(sc) != 0)
            {
                DebugCondition dc = this.condition.isDebugCondition();
                if (dc != null)
                {
                    s = new DebugStatement(this.loc, this.ifbody);
                }
                else
                {
                    s = this.ifbody;
                }
            }
            else
            {
                s = this.elsebody;
            }
            DArray<Statement> a = new DArray<Statement>();
            (a).push(s);
            return a;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ConditionalStatement() {}

        public ConditionalStatement copy() {
            ConditionalStatement that = new ConditionalStatement();
            that.condition = this.condition;
            that.ifbody = this.ifbody;
            that.elsebody = this.elsebody;
            that.loc = this.loc;
            return that;
        }
    }
    public static class StaticForeachStatement extends Statement
    {
        public StaticForeach sfe = null;
        // Erasure: __ctor<Loc, StaticForeach>
        public  StaticForeachStatement(Loc loc, StaticForeach sfe) {
            super(loc);
            this.sfe = sfe;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new StaticForeachStatement(this.loc, this.sfe.syntaxCopy());
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            this.sfe.prepare(sc);
            if (this.sfe.ready())
            {
                Statement s = makeTupleForeach10(sc, this.sfe.aggrfe, this.sfe.needExpansion);
                DArray<Statement> result = s.flatten(sc);
                if (result != null)
                {
                    return result;
                }
                result = pcopy(new DArray<Statement>());
                (result).push(s);
                return result;
            }
            else
            {
                DArray<Statement> result = new DArray<Statement>();
                (result).push(new ErrorStatement());
                return result;
            }
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticForeachStatement() {}

        public StaticForeachStatement copy() {
            StaticForeachStatement that = new StaticForeachStatement();
            that.sfe = this.sfe;
            that.loc = this.loc;
            return that;
        }
    }
    public static class PragmaStatement extends Statement
    {
        public Identifier ident = null;
        public DArray<Expression> args = null;
        public Statement _body = null;
        // Erasure: __ctor<Loc, Identifier, Ptr, Statement>
        public  PragmaStatement(Loc loc, Identifier ident, DArray<Expression> args, Statement _body) {
            super(loc);
            this.ident = ident;
            this.args = pcopy(args);
            this._body = _body;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new PragmaStatement(this.loc, this.ident, Expression.arraySyntaxCopy(this.args), this._body != null ? this._body.syntaxCopy() : null);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public PragmaStatement() {}

        public PragmaStatement copy() {
            PragmaStatement that = new PragmaStatement();
            that.ident = this.ident;
            that.args = this.args;
            that._body = this._body;
            that.loc = this.loc;
            return that;
        }
    }
    public static class StaticAssertStatement extends Statement
    {
        public StaticAssert sa = null;
        // Erasure: __ctor<StaticAssert>
        public  StaticAssertStatement(StaticAssert sa) {
            super(sa.loc);
            this.sa = sa;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new StaticAssertStatement(((StaticAssert)this.sa.syntaxCopy(null)));
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public StaticAssertStatement() {}

        public StaticAssertStatement copy() {
            StaticAssertStatement that = new StaticAssertStatement();
            that.sa = this.sa;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SwitchStatement extends Statement
    {
        public Expression condition = null;
        public Ref<Statement> _body = ref(null);
        public boolean isFinal = false;
        public DefaultStatement sdefault = null;
        public TryFinallyStatement tf = null;
        public DArray<GotoCaseStatement> gotoCases = new DArray<GotoCaseStatement>();
        public DArray<CaseStatement> cases = null;
        public int hasNoDefault = 0;
        public int hasVars = 0;
        public VarDeclaration lastVar = null;
        // Erasure: __ctor<Loc, Expression, Statement, boolean>
        public  SwitchStatement(Loc loc, Expression condition, Statement _body, boolean isFinal) {
            super(loc);
            this.condition = condition;
            this._body.value = _body;
            this.isFinal = isFinal;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new SwitchStatement(this.loc, this.condition.syntaxCopy(), this._body.value.syntaxCopy(), this.isFinal);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return true;
        }

        // Erasure: checkLabel<>
        public  boolean checkLabel() {
            SwitchStatement __self = this;
            Function1<VarDeclaration,Boolean> checkVar = new Function1<VarDeclaration,Boolean>() {
                public Boolean invoke(VarDeclaration vd) {
                 {
                    {
                        Ref<VarDeclaration> v = ref(vd);
                        for (; (v.value != null) && (!pequals(v.value, lastVar));v.value = v.value.lastVar){
                            if (v.value.isDataseg() || ((v.value.storage_class & 1099520016384L) != 0) || (v.value._init.isVoidInitializer() != null))
                            {
                                continue;
                            }
                            if ((pequals(vd.ident, Id.withSym)))
                            {
                                error(new BytePtr("`switch` skips declaration of `with` temporary at %s"), v.value.loc.toChars(global.params.showColumns));
                            }
                            else
                            {
                                error(new BytePtr("`switch` skips declaration of variable `%s` at %s"), v.value.toPrettyChars(false), v.value.loc.toChars(global.params.showColumns));
                            }
                            return true;
                        }
                    }
                    return false;
                }}

            };
            boolean error = true;
            if ((this.sdefault != null) && checkVar.invoke(this.sdefault.lastVar))
            {
                return false;
            }
            {
                Slice<CaseStatement> __r1607 = (this.cases).opSlice().copy();
                int __key1608 = 0;
                for (; (__key1608 < __r1607.getLength());__key1608 += 1) {
                    CaseStatement scase = __r1607.get(__key1608);
                    if ((scase != null) && checkVar.invoke(scase.lastVar))
                    {
                        return false;
                    }
                }
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SwitchStatement() {}

        public SwitchStatement copy() {
            SwitchStatement that = new SwitchStatement();
            that.condition = this.condition;
            that._body = this._body;
            that.isFinal = this.isFinal;
            that.sdefault = this.sdefault;
            that.tf = this.tf;
            that.gotoCases = this.gotoCases;
            that.cases = this.cases;
            that.hasNoDefault = this.hasNoDefault;
            that.hasVars = this.hasVars;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CaseStatement extends Statement
    {
        public Expression exp = null;
        public Ref<Statement> statement = ref(null);
        public int index = 0;
        public VarDeclaration lastVar = null;
        // Erasure: __ctor<Loc, Expression, Statement>
        public  CaseStatement(Loc loc, Expression exp, Statement statement) {
            super(loc);
            this.exp = exp;
            this.statement.value = statement;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new CaseStatement(this.loc, this.exp.syntaxCopy(), this.statement.value.syntaxCopy());
        }

        // Erasure: isCaseStatement<>
        public  CaseStatement isCaseStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CaseStatement() {}

        public CaseStatement copy() {
            CaseStatement that = new CaseStatement();
            that.exp = this.exp;
            that.statement = this.statement;
            that.index = this.index;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CaseRangeStatement extends Statement
    {
        public Expression first = null;
        public Expression last = null;
        public Ref<Statement> statement = ref(null);
        // Erasure: __ctor<Loc, Expression, Expression, Statement>
        public  CaseRangeStatement(Loc loc, Expression first, Expression last, Statement statement) {
            super(loc);
            this.first = first;
            this.last = last;
            this.statement.value = statement;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new CaseRangeStatement(this.loc, this.first.syntaxCopy(), this.last.syntaxCopy(), this.statement.value.syntaxCopy());
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CaseRangeStatement() {}

        public CaseRangeStatement copy() {
            CaseRangeStatement that = new CaseRangeStatement();
            that.first = this.first;
            that.last = this.last;
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DefaultStatement extends Statement
    {
        public Ref<Statement> statement = ref(null);
        public VarDeclaration lastVar = null;
        // Erasure: __ctor<Loc, Statement>
        public  DefaultStatement(Loc loc, Statement statement) {
            super(loc);
            this.statement.value = statement;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new DefaultStatement(this.loc, this.statement.value.syntaxCopy());
        }

        // Erasure: isDefaultStatement<>
        public  DefaultStatement isDefaultStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DefaultStatement() {}

        public DefaultStatement copy() {
            DefaultStatement that = new DefaultStatement();
            that.statement = this.statement;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GotoDefaultStatement extends Statement
    {
        public SwitchStatement sw = null;
        // Erasure: __ctor<Loc>
        public  GotoDefaultStatement(Loc loc) {
            super(loc);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new GotoDefaultStatement(this.loc);
        }

        // Erasure: isGotoDefaultStatement<>
        public  GotoDefaultStatement isGotoDefaultStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GotoDefaultStatement() {}

        public GotoDefaultStatement copy() {
            GotoDefaultStatement that = new GotoDefaultStatement();
            that.sw = this.sw;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GotoCaseStatement extends Statement
    {
        public Expression exp = null;
        public CaseStatement cs = null;
        // Erasure: __ctor<Loc, Expression>
        public  GotoCaseStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new GotoCaseStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null);
        }

        // Erasure: isGotoCaseStatement<>
        public  GotoCaseStatement isGotoCaseStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GotoCaseStatement() {}

        public GotoCaseStatement copy() {
            GotoCaseStatement that = new GotoCaseStatement();
            that.exp = this.exp;
            that.cs = this.cs;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SwitchErrorStatement extends Statement
    {
        public Expression exp = null;
        // Erasure: __ctor<Loc>
        public  SwitchErrorStatement(Loc loc) {
            super(loc);
        }

        // Erasure: __ctor<Loc, Expression>
        public  SwitchErrorStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SwitchErrorStatement() {}

        public SwitchErrorStatement copy() {
            SwitchErrorStatement that = new SwitchErrorStatement();
            that.exp = this.exp;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ReturnStatement extends Statement
    {
        public Expression exp = null;
        public int caseDim = 0;
        // Erasure: __ctor<Loc, Expression>
        public  ReturnStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ReturnStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null);
        }

        // Erasure: isReturnStatement<>
        public  ReturnStatement isReturnStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ReturnStatement() {}

        public ReturnStatement copy() {
            ReturnStatement that = new ReturnStatement();
            that.exp = this.exp;
            that.caseDim = this.caseDim;
            that.loc = this.loc;
            return that;
        }
    }
    public static class BreakStatement extends Statement
    {
        public Identifier ident = null;
        // Erasure: __ctor<Loc, Identifier>
        public  BreakStatement(Loc loc, Identifier ident) {
            super(loc);
            this.ident = ident;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new BreakStatement(this.loc, this.ident);
        }

        // Erasure: isBreakStatement<>
        public  BreakStatement isBreakStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public BreakStatement() {}

        public BreakStatement copy() {
            BreakStatement that = new BreakStatement();
            that.ident = this.ident;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ContinueStatement extends Statement
    {
        public Identifier ident = null;
        // Erasure: __ctor<Loc, Identifier>
        public  ContinueStatement(Loc loc, Identifier ident) {
            super(loc);
            this.ident = ident;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ContinueStatement(this.loc, this.ident);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ContinueStatement() {}

        public ContinueStatement copy() {
            ContinueStatement that = new ContinueStatement();
            that.ident = this.ident;
            that.loc = this.loc;
            return that;
        }
    }
    public static class SynchronizedStatement extends Statement
    {
        public Expression exp = null;
        public Ref<Statement> _body = ref(null);
        // Erasure: __ctor<Loc, Expression, Statement>
        public  SynchronizedStatement(Loc loc, Expression exp, Statement _body) {
            super(loc);
            this.exp = exp;
            this._body.value = _body;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new SynchronizedStatement(this.loc, this.exp != null ? this.exp.syntaxCopy() : null, this._body.value != null ? this._body.value.syntaxCopy() : null);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return false;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public SynchronizedStatement() {}

        public SynchronizedStatement copy() {
            SynchronizedStatement that = new SynchronizedStatement();
            that.exp = this.exp;
            that._body = this._body;
            that.loc = this.loc;
            return that;
        }
    }
    public static class WithStatement extends Statement
    {
        public Expression exp = null;
        public Ref<Statement> _body = ref(null);
        public VarDeclaration wthis = null;
        public Loc endloc = new Loc();
        // Erasure: __ctor<Loc, Expression, Statement, Loc>
        public  WithStatement(Loc loc, Expression exp, Statement _body, Loc endloc) {
            super(loc);
            this.exp = exp;
            this._body.value = _body;
            this.endloc.opAssign(endloc.copy());
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new WithStatement(this.loc, this.exp.syntaxCopy(), this._body.value != null ? this._body.value.syntaxCopy() : null, this.endloc);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public WithStatement() {}

        public WithStatement copy() {
            WithStatement that = new WithStatement();
            that.exp = this.exp;
            that._body = this._body;
            that.wthis = this.wthis;
            that.endloc = this.endloc;
            that.loc = this.loc;
            return that;
        }
    }
    public static class TryCatchStatement extends Statement
    {
        public Ref<Statement> _body = ref(null);
        public DArray<Catch> catches = null;
        // Erasure: __ctor<Loc, Statement, Ptr>
        public  TryCatchStatement(Loc loc, Statement _body, DArray<Catch> catches) {
            super(loc);
            this._body.value = _body;
            this.catches = pcopy(catches);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            DArray<Catch> a = new DArray<Catch>((this.catches).length);
            {
                Slice<Catch> __r1610 = (this.catches).opSlice().copy();
                int __key1609 = 0;
                for (; (__key1609 < __r1610.getLength());__key1609 += 1) {
                    Catch c = __r1610.get(__key1609);
                    int i = __key1609;
                    a.set(i, c.syntaxCopy());
                }
            }
            return new TryCatchStatement(this.loc, this._body.value.syntaxCopy(), a);
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TryCatchStatement() {}

        public TryCatchStatement copy() {
            TryCatchStatement that = new TryCatchStatement();
            that._body = this._body;
            that.catches = this.catches;
            that.loc = this.loc;
            return that;
        }
    }
    public static class Catch extends RootObject
    {
        public Loc loc = new Loc();
        public Type type = null;
        public Identifier ident = null;
        public Ref<Statement> handler = ref(null);
        public VarDeclaration var = null;
        public boolean errors = false;
        public boolean internalCatch = false;
        // Erasure: __ctor<Loc, Type, Identifier, Statement>
        public  Catch(Loc loc, Type type, Identifier ident, Statement handler) {
            super();
            this.loc.opAssign(loc.copy());
            this.type = type;
            this.ident = ident;
            this.handler.value = handler;
        }

        // Erasure: syntaxCopy<>
        public  Catch syntaxCopy() {
            Catch c = new Catch(this.loc, this.type != null ? this.type.syntaxCopy() : getThrowable(), this.ident, this.handler.value != null ? this.handler.value.syntaxCopy() : null);
            c.internalCatch = this.internalCatch;
            return c;
        }


        public Catch() {}

        public Catch copy() {
            Catch that = new Catch();
            that.loc = this.loc;
            that.type = this.type;
            that.ident = this.ident;
            that.handler = this.handler;
            that.var = this.var;
            that.errors = this.errors;
            that.internalCatch = this.internalCatch;
            return that;
        }
    }
    public static class TryFinallyStatement extends Statement
    {
        public Ref<Statement> _body = ref(null);
        public Ref<Statement> finalbody = ref(null);
        public boolean bodyFallsThru = false;
        // Erasure: __ctor<Loc, Statement, Statement>
        public  TryFinallyStatement(Loc loc, Statement _body, Statement finalbody) {
            super(loc);
            this._body.value = _body;
            this.finalbody.value = finalbody;
            this.bodyFallsThru = true;
        }

        // Erasure: create<Loc, Statement, Statement>
        public static TryFinallyStatement create(Loc loc, Statement _body, Statement finalbody) {
            return new TryFinallyStatement(loc, _body, finalbody);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new TryFinallyStatement(this.loc, this._body.value.syntaxCopy(), this.finalbody.value.syntaxCopy());
        }

        // Erasure: hasBreak<>
        public  boolean hasBreak() {
            return false;
        }

        // Erasure: hasContinue<>
        public  boolean hasContinue() {
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TryFinallyStatement() {}

        public TryFinallyStatement copy() {
            TryFinallyStatement that = new TryFinallyStatement();
            that._body = this._body;
            that.finalbody = this.finalbody;
            that.bodyFallsThru = this.bodyFallsThru;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ScopeGuardStatement extends Statement
    {
        public byte tok = 0;
        public Statement statement = null;
        // Erasure: __ctor<Loc, byte, Statement>
        public  ScopeGuardStatement(Loc loc, byte tok, Statement statement) {
            super(loc);
            this.tok = tok;
            this.statement = statement;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new ScopeGuardStatement(this.loc, this.tok, this.statement.syntaxCopy());
        }

        // Erasure: scopeCode<Ptr, Ptr, Ptr, Ptr>
        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexception, Ptr<Statement> sfinally) {
            sentry.set(0, null);
            sexception.set(0, null);
            sfinally.set(0, null);
            Statement s = new PeelStatement(this.statement);
            switch ((this.tok & 0xFF))
            {
                case 204:
                    sfinally.set(0, s);
                    break;
                case 205:
                    sexception.set(0, s);
                    break;
                case 206:
                    VarDeclaration v = copyToTemp(0L, new BytePtr("__os"), new IntegerExp(Loc.initial, 0L, Type.tbool));
                    dsymbolSemantic(v, sc);
                    sentry.set(0, new ExpStatement(this.loc, v));
                    Expression e = new IntegerExp(Loc.initial, 1L, Type.tbool);
                    e = new AssignExp(Loc.initial, new VarExp(Loc.initial, v, true), e);
                    sexception.set(0, new ExpStatement(Loc.initial, e));
                    e = new VarExp(Loc.initial, v, true);
                    e = new NotExp(Loc.initial, e);
                    sfinally.set(0, new IfStatement(Loc.initial, null, e, s, null, Loc.initial));
                    break;
                default:
                throw new AssertionError("Unreachable code!");
            }
            return null;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ScopeGuardStatement() {}

        public ScopeGuardStatement copy() {
            ScopeGuardStatement that = new ScopeGuardStatement();
            that.tok = this.tok;
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ThrowStatement extends Statement
    {
        public Expression exp = null;
        public boolean internalThrow = false;
        // Erasure: __ctor<Loc, Expression>
        public  ThrowStatement(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            ThrowStatement s = new ThrowStatement(this.loc, this.exp.syntaxCopy());
            s.internalThrow = this.internalThrow;
            return s;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ThrowStatement() {}

        public ThrowStatement copy() {
            ThrowStatement that = new ThrowStatement();
            that.exp = this.exp;
            that.internalThrow = this.internalThrow;
            that.loc = this.loc;
            return that;
        }
    }
    public static class DebugStatement extends Statement
    {
        public Ref<Statement> statement = ref(null);
        // Erasure: __ctor<Loc, Statement>
        public  DebugStatement(Loc loc, Statement statement) {
            super(loc);
            this.statement.value = statement;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new DebugStatement(this.loc, this.statement.value != null ? this.statement.value.syntaxCopy() : null);
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            DArray<Statement> a = this.statement.value != null ? this.statement.value.flatten(sc) : null;
            if (a != null)
            {
                {
                    Slice<Statement> __r1611 = (a).opSlice().copy();
                    int __key1612 = 0;
                    for (; (__key1612 < __r1611.getLength());__key1612 += 1) {
                        Statement s = __r1611.get(__key1612);
                        s = new DebugStatement(this.loc, s);
                    }
                }
            }
            return a;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DebugStatement() {}

        public DebugStatement copy() {
            DebugStatement that = new DebugStatement();
            that.statement = this.statement;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GotoStatement extends Statement
    {
        public Identifier ident = null;
        public LabelDsymbol label = null;
        public TryFinallyStatement tf = null;
        public ScopeGuardStatement os = null;
        public VarDeclaration lastVar = null;
        // Erasure: __ctor<Loc, Identifier>
        public  GotoStatement(Loc loc, Identifier ident) {
            super(loc);
            this.ident = ident;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new GotoStatement(this.loc, this.ident);
        }

        // Erasure: checkLabel<>
        public  boolean checkLabel() {
            if (this.label.statement == null)
            {
                this.error(new BytePtr("label `%s` is undefined"), this.label.toChars());
                return true;
            }
            if ((!pequals(this.label.statement.os, this.os)))
            {
                if ((this.os != null) && ((this.os.tok & 0xFF) == 205) && (this.label.statement.os == null))
                {
                }
                else
                {
                    if (this.label.statement.os != null)
                    {
                        this.error(new BytePtr("cannot `goto` in to `%s` block"), Token.toChars(this.label.statement.os.tok));
                    }
                    else
                    {
                        this.error(new BytePtr("cannot `goto` out of `%s` block"), Token.toChars(this.os.tok));
                    }
                    return true;
                }
            }
            if ((!pequals(this.label.statement.tf, this.tf)))
            {
                this.error(new BytePtr("cannot `goto` in or out of `finally` block"));
                return true;
            }
            VarDeclaration vd = this.label.statement.lastVar;
            if ((vd == null) || vd.isDataseg() || ((vd.storage_class & 8388608L) != 0))
            {
                return false;
            }
            VarDeclaration last = this.lastVar;
            for (; (last != null) && (!pequals(last, vd));) {
                last = last.lastVar;
            }
            if ((pequals(last, vd)))
            {
            }
            else if ((vd.storage_class & 140737488355328L) != 0)
            {
            }
            else if ((pequals(vd.ident, Id.withSym)))
            {
                this.error(new BytePtr("`goto` skips declaration of `with` temporary at %s"), vd.loc.toChars(global.params.showColumns));
                return true;
            }
            else
            {
                this.error(new BytePtr("`goto` skips declaration of variable `%s` at %s"), vd.toPrettyChars(false), vd.loc.toChars(global.params.showColumns));
                return true;
            }
            return false;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GotoStatement() {}

        public GotoStatement copy() {
            GotoStatement that = new GotoStatement();
            that.ident = this.ident;
            that.label = this.label;
            that.tf = this.tf;
            that.os = this.os;
            that.lastVar = this.lastVar;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LabelStatement extends Statement
    {
        public Identifier ident = null;
        public Ref<Statement> statement = ref(null);
        public TryFinallyStatement tf = null;
        public ScopeGuardStatement os = null;
        public VarDeclaration lastVar = null;
        public Statement gotoTarget = null;
        public boolean breaks = false;
        // Erasure: __ctor<Loc, Identifier, Statement>
        public  LabelStatement(Loc loc, Identifier ident, Statement statement) {
            super(loc);
            this.ident = ident;
            this.statement.value = statement;
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new LabelStatement(this.loc, this.ident, this.statement.value != null ? this.statement.value.syntaxCopy() : null);
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            DArray<Statement> a = null;
            if (this.statement.value != null)
            {
                a = pcopy(this.statement.value.flatten(sc));
                if (a != null)
                {
                    if ((a).length == 0)
                    {
                        (a).push(new ExpStatement(this.loc, null));
                    }
                    this.statement.value = (a).get(0);
                    a.set(0, this);
                }
            }
            return a;
        }

        // Erasure: scopeCode<Ptr, Ptr, Ptr, Ptr>
        public  Statement scopeCode(Ptr<Scope> sc, Ptr<Statement> sentry, Ptr<Statement> sexit, Ptr<Statement> sfinally) {
            if (this.statement.value != null)
            {
                this.statement.value = this.statement.value.scopeCode(sc, sentry, sexit, sfinally);
            }
            else
            {
                sentry.set(0, null);
                sexit.set(0, null);
                sfinally.set(0, null);
            }
            return this;
        }

        // Erasure: isLabelStatement<>
        public  LabelStatement isLabelStatement() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LabelStatement() {}

        public LabelStatement copy() {
            LabelStatement that = new LabelStatement();
            that.ident = this.ident;
            that.statement = this.statement;
            that.tf = this.tf;
            that.os = this.os;
            that.lastVar = this.lastVar;
            that.gotoTarget = this.gotoTarget;
            that.breaks = this.breaks;
            that.loc = this.loc;
            return that;
        }
    }
    public static class LabelDsymbol extends Dsymbol
    {
        public LabelStatement statement = null;
        // Erasure: __ctor<Identifier>
        public  LabelDsymbol(Identifier ident) {
            super(ident);
        }

        // Erasure: create<Identifier>
        public static LabelDsymbol create(Identifier ident) {
            return new LabelDsymbol(ident);
        }

        // Erasure: isLabel<>
        public  LabelDsymbol isLabel() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public LabelDsymbol() {}

        public LabelDsymbol copy() {
            LabelDsymbol that = new LabelDsymbol();
            that.statement = this.statement;
            that.ident = this.ident;
            that.parent = this.parent;
            that.namespace = this.namespace;
            that.csym = this.csym;
            that.isym = this.isym;
            that.comment = this.comment;
            that.loc = this.loc;
            that._scope = this._scope;
            that.prettystring = this.prettystring;
            that.errors = this.errors;
            that.semanticRun = this.semanticRun;
            that.depdecl = this.depdecl;
            that.userAttribDecl = this.userAttribDecl;
            that.ddocUnittest = this.ddocUnittest;
            return that;
        }
    }
    public static class AsmStatement extends Statement
    {
        public Ptr<Token> tokens = null;
        // Erasure: __ctor<Loc, Ptr>
        public  AsmStatement(Loc loc, Ptr<Token> tokens) {
            super(loc);
            this.tokens = pcopy(tokens);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new AsmStatement(this.loc, this.tokens);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public AsmStatement() {}

        public AsmStatement copy() {
            AsmStatement that = new AsmStatement();
            that.tokens = this.tokens;
            that.loc = this.loc;
            return that;
        }
    }
    public static class InlineAsmStatement extends AsmStatement
    {
        public Ptr<code> asmcode = null;
        public int asmalign = 0;
        public int regs = 0;
        public boolean refparam = false;
        public boolean naked = false;
        // Erasure: __ctor<Loc, Ptr>
        public  InlineAsmStatement(Loc loc, Ptr<Token> tokens) {
            super(loc, tokens);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new InlineAsmStatement(this.loc, this.tokens);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public InlineAsmStatement() {}

        public InlineAsmStatement copy() {
            InlineAsmStatement that = new InlineAsmStatement();
            that.asmcode = this.asmcode;
            that.asmalign = this.asmalign;
            that.regs = this.regs;
            that.refparam = this.refparam;
            that.naked = this.naked;
            that.tokens = this.tokens;
            that.loc = this.loc;
            return that;
        }
    }
    public static class GccAsmStatement extends AsmStatement
    {
        public long stc = 0L;
        public Expression insn = null;
        public DArray<Expression> args = null;
        public int outputargs = 0;
        public DArray<Identifier> names = null;
        public DArray<Expression> constraints = null;
        public DArray<Expression> clobbers = null;
        public DArray<Identifier> labels = null;
        public DArray<GotoStatement> gotos = null;
        // Erasure: __ctor<Loc, Ptr>
        public  GccAsmStatement(Loc loc, Ptr<Token> tokens) {
            super(loc, tokens);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            return new GccAsmStatement(this.loc, this.tokens);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public GccAsmStatement() {}

        public GccAsmStatement copy() {
            GccAsmStatement that = new GccAsmStatement();
            that.stc = this.stc;
            that.insn = this.insn;
            that.args = this.args;
            that.outputargs = this.outputargs;
            that.names = this.names;
            that.constraints = this.constraints;
            that.clobbers = this.clobbers;
            that.labels = this.labels;
            that.gotos = this.gotos;
            that.tokens = this.tokens;
            that.loc = this.loc;
            return that;
        }
    }
    public static class CompoundAsmStatement extends CompoundStatement
    {
        public long stc = 0L;
        // Erasure: __ctor<Loc, Ptr, long>
        public  CompoundAsmStatement(Loc loc, DArray<Statement> statements, long stc) {
            super(loc, statements);
            this.stc = stc;
        }

        // Erasure: syntaxCopy<>
        public  CompoundAsmStatement syntaxCopy() {
            DArray<Statement> a = new DArray<Statement>((this.statements).length);
            {
                Slice<Statement> __r1614 = (this.statements).opSlice().copy();
                int __key1613 = 0;
                for (; (__key1613 < __r1614.getLength());__key1613 += 1) {
                    Statement s = __r1614.get(__key1613);
                    int i = __key1613;
                    a.set(i, s != null ? s.syntaxCopy() : null);
                }
            }
            return ((Statement)new CompoundAsmStatement(this.loc, a, this.stc));
        }

        // Erasure: flatten<Ptr>
        public  DArray<Statement> flatten(Ptr<Scope> sc) {
            return null;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public CompoundAsmStatement() {}

        public CompoundAsmStatement copy() {
            CompoundAsmStatement that = new CompoundAsmStatement();
            that.stc = this.stc;
            that.statements = this.statements;
            that.loc = this.loc;
            return that;
        }
    }
    public static class ImportStatement extends Statement
    {
        public DArray<Dsymbol> imports = null;
        // Erasure: __ctor<Loc, Ptr>
        public  ImportStatement(Loc loc, DArray<Dsymbol> imports) {
            super(loc);
            this.imports = pcopy(imports);
        }

        // Erasure: syntaxCopy<>
        public  Statement syntaxCopy() {
            DArray<Dsymbol> m = new DArray<Dsymbol>((this.imports).length);
            {
                Slice<Dsymbol> __r1616 = (this.imports).opSlice().copy();
                int __key1615 = 0;
                for (; (__key1615 < __r1616.getLength());__key1615 += 1) {
                    Dsymbol s = __r1616.get(__key1615);
                    int i = __key1615;
                    m.set(i, s.syntaxCopy(null));
                }
            }
            return new ImportStatement(this.loc, m);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public ImportStatement() {}

        public ImportStatement copy() {
            ImportStatement that = new ImportStatement();
            that.imports = this.imports;
            that.loc = this.loc;
            return that;
        }
    }
}
