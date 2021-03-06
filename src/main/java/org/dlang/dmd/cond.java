package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.staticcond.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class cond {


    public static class Include 
    {
        public static final int notComputed = 0;
        public static final int yes = 1;
        public static final int no = 2;
    }

    public static abstract class Condition extends ASTNode
    {
        public Loc loc = new Loc();
        public int inc = 0;
        // Erasure: dyncast<>
        public  int dyncast() {
            return DYNCAST.condition;
        }

        // Erasure: __ctor<Loc>
        public  Condition(Loc loc) {
            super();
            this.loc.opAssign(loc.copy());
        }

        // Erasure: syntaxCopy<>
        public abstract Condition syntaxCopy();


        // Erasure: include<>
        public abstract int include(Ptr<Scope> sc);


        // Erasure: isDebugCondition<>
        public  DebugCondition isDebugCondition() {
            return null;
        }

        // Erasure: isVersionCondition<>
        public  VersionCondition isVersionCondition() {
            return null;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public Condition() {}

        public abstract Condition copy();
    }
    public static class StaticForeach extends RootObject
    {
        public static ByteSlice tupleFieldName = new ByteSlice("tuple");
        public Loc loc = new Loc();
        public ForeachStatement aggrfe = null;
        public ForeachRangeStatement rangefe = null;
        public boolean needExpansion = false;
        // Erasure: __ctor<Loc, ForeachStatement, ForeachRangeStatement>
        public  StaticForeach(Loc loc, ForeachStatement aggrfe, ForeachRangeStatement rangefe) {
            super();
            assert(aggrfe != null ^ rangefe != null);
            this.loc.opAssign(loc.copy());
            this.aggrfe = aggrfe;
            this.rangefe = rangefe;
        }

        // Erasure: syntaxCopy<>
        public  StaticForeach syntaxCopy() {
            return new StaticForeach(this.loc, this.aggrfe != null ? ((ForeachStatement)this.aggrfe.syntaxCopy()) : null, this.rangefe != null ? ((ForeachRangeStatement)this.rangefe.syntaxCopy()) : null);
        }

        // Erasure: lowerArrayAggregate<Ptr>
        public  void lowerArrayAggregate(Ptr<Scope> sc) {
            Expression aggr = this.aggrfe.aggr.value;
            Expression el = new ArrayLengthExp(aggr.loc, aggr);
            sc = pcopy((sc.get()).startCTFE());
            el = expressionSemantic(el, sc);
            sc = pcopy((sc.get()).endCTFE());
            el = el.optimize(0, false);
            el = el.ctfeInterpret();
            if (((el.op & 0xFF) == 135))
            {
                long length = el.toInteger();
                DArray<Expression> es = new DArray<Expression>();
                {
                    long __key815 = 0L;
                    long __limit816 = length;
                    for (; (__key815 < __limit816);__key815 += 1L) {
                        long i = __key815;
                        IntegerExp index = new IntegerExp(this.loc, i, Type.tsize_t);
                        IndexExp value = new IndexExp(aggr.loc, aggr, index);
                        (es).push(value);
                    }
                }
                this.aggrfe.aggr.value = new TupleExp(aggr.loc, es);
                this.aggrfe.aggr.value = expressionSemantic(this.aggrfe.aggr.value, sc);
                this.aggrfe.aggr.value = this.aggrfe.aggr.value.optimize(0, false);
            }
            else
            {
                this.aggrfe.aggr.value = new ErrorExp();
            }
        }

        // Erasure: wrapAndCall<Loc, Statement>
        public  Expression wrapAndCall(Loc loc, Statement s) {
            TypeFunction tf = new TypeFunction(new ParameterList(null, VarArg.none), null, LINK.default_, 0L);
            FuncLiteralDeclaration fd = new FuncLiteralDeclaration(loc, loc, tf, TOK.reserved, null, null);
            fd.fbody.value = s;
            FuncExp fe = new FuncExp(loc, fd);
            CallExp ce = new CallExp(loc, fe, new DArray<Expression>());
            return ce;
        }

        // Erasure: createForeach<Loc, Ptr, Statement>
        public  Statement createForeach(Loc loc, DArray<Parameter> parameters, Statement s) {
            if (this.aggrfe != null)
            {
                return new ForeachStatement(loc, this.aggrfe.op, parameters, this.aggrfe.aggr.value.syntaxCopy(), s, loc);
            }
            else
            {
                assert((this.rangefe != null) && ((parameters).length == 1));
                return new ForeachRangeStatement(loc, this.rangefe.op, (parameters).get(0), this.rangefe.lwr.syntaxCopy(), this.rangefe.upr.syntaxCopy(), s, loc);
            }
        }

        // Erasure: createTupleType<Loc, Ptr, Ptr>
        public  TypeStruct createTupleType(Loc loc, DArray<Expression> e, Ptr<Scope> sc) {
            Identifier sid = Identifier.generateId(new BytePtr("Tuple"));
            StructDeclaration sdecl = new StructDeclaration(loc, sid, false);
            sdecl.storage_class |= 1L;
            sdecl.members = pcopy(new DArray<Dsymbol>());
            Identifier fid = Identifier.idPool(tupleFieldName.getPtr(0), 5);
            TypeTypeof ty = new TypeTypeof(loc, new TupleExp(loc, e));
            (sdecl.members).push(new VarDeclaration(loc, ty, fid, null, 0L));
            TypeStruct r = ((TypeStruct)sdecl.type);
            r.vtinfo = TypeInfoStructDeclaration.create(r);
            return r;
        }

        // Erasure: createTuple<Loc, TypeStruct, Ptr>
        public  Expression createTuple(Loc loc, TypeStruct type, DArray<Expression> e) {
            return new CallExp(loc, new TypeExp(loc, type), e);
        }

        // Erasure: lowerNonArrayAggregate<Ptr>
        public  void lowerNonArrayAggregate(Ptr<Scope> sc) {
            int nvars = this.aggrfe != null ? (this.aggrfe.parameters).length : 1;
            Loc aloc = this.aggrfe != null ? this.aggrfe.aggr.value.loc : this.rangefe.lwr.loc.copy();
            Slice<DArray<Parameter>> pparams = slice(new DArray<Parameter>[]{new DArray<Parameter>(), new DArray<Parameter>(), new DArray<Parameter>()});
            {
                int __key817 = 0;
                int __limit818 = nvars;
                for (; (__key817 < __limit818);__key817 += 1) {
                    int i = __key817;
                    {
                        Slice<DArray<Parameter>> __r819 = pparams.copy();
                        int __key820 = 0;
                        for (; (__key820 < __r819.getLength());__key820 += 1) {
                            DArray<Parameter> params = __r819.get(__key820);
                            Parameter p = this.aggrfe != null ? (this.aggrfe.parameters).get(i) : this.rangefe.prm;
                            (params).push(new Parameter(p.storageClass, p.type, p.ident, null, null));
                        }
                    }
                }
            }
            Slice<Expression> res = null;
            TypeStruct tplty = null;
            if ((nvars == 1))
            {
                {
                    int __key821 = 0;
                    int __limit822 = 2;
                    for (; (__key821 < __limit822);__key821 += 1) {
                        int i = __key821;
                        res.set((i), new IdentifierExp(aloc, (pparams.get(i)).get(0).ident));
                    }
                }
            }
            else
            {
                {
                    int __key823 = 0;
                    int __limit824 = 2;
                    for (; (__key823 < __limit824);__key823 += 1) {
                        int i = __key823;
                        DArray<Expression> e = new DArray<Expression>((pparams.get(0)).length);
                        {
                            Slice<Expression> __r826 = (e).opSlice().copy();
                            int __key825 = 0;
                            for (; (__key825 < __r826.getLength());__key825 += 1) {
                                Expression elem = __r826.get(__key825);
                                int j = __key825;
                                Parameter p = (pparams.get(i)).get(j);
                                elem = new IdentifierExp(aloc, p.ident);
                            }
                        }
                        if (tplty == null)
                        {
                            tplty = this.createTupleType(aloc, e, sc);
                        }
                        res.set((i), this.createTuple(aloc, tplty, e));
                    }
                }
                this.needExpansion = true;
            }
            if (this.rangefe != null)
            {
                sc = pcopy((sc.get()).startCTFE());
                this.rangefe.lwr = expressionSemantic(this.rangefe.lwr, sc);
                this.rangefe.lwr = resolveProperties(sc, this.rangefe.lwr);
                this.rangefe.upr = expressionSemantic(this.rangefe.upr, sc);
                this.rangefe.upr = resolveProperties(sc, this.rangefe.upr);
                sc = pcopy((sc.get()).endCTFE());
                this.rangefe.lwr = this.rangefe.lwr.optimize(0, false);
                this.rangefe.lwr = this.rangefe.lwr.ctfeInterpret();
                this.rangefe.upr = this.rangefe.upr.optimize(0, false);
                this.rangefe.upr = this.rangefe.upr.ctfeInterpret();
            }
            DArray<Statement> s1 = new DArray<Statement>();
            DArray<Statement> sfe = new DArray<Statement>();
            if (tplty != null)
            {
                (sfe).push(new ExpStatement(this.loc, tplty.sym));
            }
            (sfe).push(new ReturnStatement(aloc, res.get(0)));
            (s1).push(this.createForeach(aloc, pparams.get(0), new CompoundStatement(aloc, sfe)));
            (s1).push(new ExpStatement(aloc, new AssertExp(aloc, new IntegerExp(aloc, 0L, Type.tint32), null)));
            TypeTypeof ety = new TypeTypeof(aloc, this.wrapAndCall(aloc, new CompoundStatement(aloc, s1)));
            Type aty = ety.arrayOf();
            Identifier idres = Identifier.generateId(new BytePtr("__res"));
            VarDeclaration vard = new VarDeclaration(aloc, aty, idres, null, 0L);
            DArray<Statement> s2 = new DArray<Statement>();
            (s2).push(new ExpStatement(aloc, vard));
            CatAssignExp catass = new CatAssignExp(aloc, new IdentifierExp(aloc, idres), res.get(1));
            (s2).push(this.createForeach(aloc, pparams.get(1), new ExpStatement(aloc, catass)));
            (s2).push(new ReturnStatement(aloc, new IdentifierExp(aloc, idres)));
            Expression aggr = this.wrapAndCall(aloc, new CompoundStatement(aloc, s2));
            sc = pcopy((sc.get()).startCTFE());
            aggr = expressionSemantic(aggr, sc);
            aggr = resolveProperties(sc, aggr);
            sc = pcopy((sc.get()).endCTFE());
            aggr = aggr.optimize(0, false);
            aggr = aggr.ctfeInterpret();
            assert(this.aggrfe != null ^ this.rangefe != null);
            this.aggrfe = new ForeachStatement(this.loc, TOK.foreach_, pparams.get(2), aggr, this.aggrfe != null ? this.aggrfe._body.value : this.rangefe._body.value, this.aggrfe != null ? this.aggrfe.endloc : this.rangefe.endloc);
            this.rangefe = null;
            this.lowerArrayAggregate(sc);
        }

        // Erasure: prepare<Ptr>
        public  void prepare(Ptr<Scope> sc) {
            assert(sc != null);
            if (this.aggrfe != null)
            {
                sc = pcopy((sc.get()).startCTFE());
                this.aggrfe.aggr.value = expressionSemantic(this.aggrfe.aggr.value, sc);
                sc = pcopy((sc.get()).endCTFE());
                this.aggrfe.aggr.value = this.aggrfe.aggr.value.optimize(0, false);
                Type tab = this.aggrfe.aggr.value.type.value.toBasetype();
                if (((tab.ty & 0xFF) != ENUMTY.Ttuple))
                {
                    this.aggrfe.aggr.value = this.aggrfe.aggr.value.ctfeInterpret();
                }
            }
            if ((this.aggrfe != null) && ((this.aggrfe.aggr.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Terror))
            {
                return ;
            }
            if (!this.ready())
            {
                if ((this.aggrfe != null) && ((this.aggrfe.aggr.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Tarray))
                {
                    this.lowerArrayAggregate(sc);
                }
                else
                {
                    this.lowerNonArrayAggregate(sc);
                }
            }
        }

        // Erasure: ready<>
        public  boolean ready() {
            return (this.aggrfe != null) && (this.aggrfe.aggr.value != null) && (this.aggrfe.aggr.value.type.value != null) && ((this.aggrfe.aggr.value.type.value.toBasetype().ty & 0xFF) == ENUMTY.Ttuple);
        }


        public StaticForeach() {}

        public StaticForeach copy() {
            StaticForeach that = new StaticForeach();
            that.loc = this.loc;
            that.aggrfe = this.aggrfe;
            that.rangefe = this.rangefe;
            that.needExpansion = this.needExpansion;
            return that;
        }
    }
    public static abstract class DVCondition extends Condition
    {
        public int level = 0;
        public Identifier ident = null;
        public dmodule.Module mod = null;
        // Erasure: __ctor<Module, int, Identifier>
        public  DVCondition(dmodule.Module mod, int level, Identifier ident) {
            super(Loc.initial);
            this.mod = mod;
            this.level = level;
            this.ident = ident;
        }

        // Erasure: syntaxCopy<>
        public  Condition syntaxCopy() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public DVCondition() {}

        public abstract DVCondition copy();
    }
    public static class DebugCondition extends DVCondition
    {
        // Erasure: addGlobalIdent<Ptr>
        public static void addGlobalIdent(BytePtr ident) {
            addGlobalIdent(ident.slice(0,strlen(ident)));
        }

        // Erasure: addGlobalIdent<Array>
        public static void addGlobalIdent(ByteSlice ident) {
            addGlobalIdent(toByteSlice(ident));
        }

        // removed duplicate function, [["void addGlobalIdentByteSlice", "void addGlobalIdentBytePtr"]] signature: void addGlobalIdentByteSlice
        // Erasure: __ctor<Module, int, Identifier>
        public  DebugCondition(dmodule.Module mod, int level, Identifier ident) {
            super(mod, level, ident);
        }

        // Erasure: include<Ptr>
        public  int include(Ptr<Scope> sc) {
            if ((this.inc == Include.notComputed))
            {
                this.inc = Include.no;
                boolean definedInModule = false;
                if (this.ident != null)
                {
                    if (findCondition(this.mod.debugids, this.ident))
                    {
                        this.inc = Include.yes;
                        definedInModule = true;
                    }
                    else if (findCondition(global.debugids, this.ident))
                    {
                        this.inc = Include.yes;
                    }
                    else
                    {
                        if (this.mod.debugidsNot == null)
                        {
                            this.mod.debugidsNot = pcopy(new DArray<Identifier>());
                        }
                        (this.mod.debugidsNot).push(this.ident);
                    }
                }
                else if ((this.level <= global.params.debuglevel) || (this.level <= this.mod.debuglevel))
                {
                    this.inc = Include.yes;
                }
                if (!definedInModule)
                {
                    printDepsConditional(sc, this, new ByteSlice("depsDebug "));
                }
            }
            return ((this.inc == Include.yes) ? 1 : 0);
        }

        // Erasure: isDebugCondition<>
        public  DebugCondition isDebugCondition() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.ident != null ? this.ident.toChars() : new BytePtr("debug");
        }


        public DebugCondition() {}

        public DebugCondition copy() {
            DebugCondition that = new DebugCondition();
            that.level = this.level;
            that.ident = this.ident;
            that.mod = this.mod;
            that.loc = this.loc;
            that.inc = this.inc;
            return that;
        }
    }
    public static class VersionCondition extends DVCondition
    {
        // Erasure: isReserved<Array>
        public static boolean isReserved(ByteSlice ident) {
            switch (__switch(ident))
            {
                case 66:
                case 4:
                case 5:
                case 8:
                case 54:
                case 32:
                case 33:
                case 34:
                case 6:
                case 11:
                case 16:
                case 53:
                case 45:
                case 47:
                case 40:
                case 69:
                case 3:
                case 51:
                case 26:
                case 1:
                case 22:
                case 29:
                case 30:
                case 31:
                case 13:
                case 44:
                case 65:
                case 67:
                case 72:
                case 35:
                case 23:
                case 70:
                case 9:
                case 41:
                case 2:
                case 61:
                case 75:
                case 64:
                case 74:
                case 43:
                case 19:
                case 55:
                case 7:
                case 78:
                case 77:
                case 25:
                case 14:
                case 37:
                case 38:
                case 58:
                case 56:
                case 59:
                case 57:
                case 63:
                case 83:
                case 82:
                case 39:
                case 24:
                case 46:
                case 48:
                case 49:
                case 28:
                case 73:
                case 89:
                case 88:
                case 50:
                case 15:
                case 27:
                case 52:
                case 12:
                case 36:
                case 0:
                case 68:
                case 18:
                case 85:
                case 84:
                case 71:
                case 62:
                case 20:
                case 21:
                case 86:
                case 92:
                case 79:
                case 91:
                case 76:
                case 87:
                case 90:
                case 94:
                case 80:
                case 93:
                case 81:
                case 60:
                case 42:
                case 10:
                case 17:
                    return true;
                default:
                return (ident.getLength() >= 2) && __equals(ident.slice(0,2), new ByteSlice("D_"));
            }
        }

        // Erasure: checkReserved<Loc, Array>
        public static void checkReserved(Loc loc, ByteSlice ident) {
            if (isReserved(ident))
            {
                error(loc, new BytePtr("version identifier `%s` is reserved and cannot be set"), ident.getPtr(0));
            }
        }

        // Erasure: addGlobalIdent<Ptr>
        public static void addGlobalIdent(BytePtr ident) {
            addGlobalIdent(ident.slice(0,strlen(ident)));
        }

        // Erasure: addGlobalIdent<Array>
        public static void addGlobalIdent(ByteSlice ident) {
            addGlobalIdent(toByteSlice(ident));
        }

        // removed duplicate function, [["void checkReservedLoc, ByteSlice", "void addGlobalIdentByteSlice", "void addGlobalIdentBytePtr", "boolean isReservedByteSlice"]] signature: void addGlobalIdentByteSlice
        // Erasure: addPredefinedGlobalIdent<Ptr>
        public static void addPredefinedGlobalIdent(BytePtr ident) {
            addPredefinedGlobalIdent(ident.slice(0,strlen(ident)));
        }

        // Erasure: addPredefinedGlobalIdent<Array>
        public static void addPredefinedGlobalIdent(ByteSlice ident) {
            addPredefinedGlobalIdent(toByteSlice(ident));
        }

        // removed duplicate function, [["void checkReservedLoc, ByteSlice", "void addGlobalIdentByteSlice", "void addGlobalIdentBytePtr", "boolean isReservedByteSlice", "void addPredefinedGlobalIdentByteSlice", "void addPredefinedGlobalIdentBytePtr"]] signature: void addPredefinedGlobalIdentByteSlice
        // Erasure: __ctor<Module, int, Identifier>
        public  VersionCondition(dmodule.Module mod, int level, Identifier ident) {
            super(mod, level, ident);
        }

        // Erasure: include<Ptr>
        public  int include(Ptr<Scope> sc) {
            if ((this.inc == Include.notComputed))
            {
                this.inc = Include.no;
                boolean definedInModule = false;
                if (this.ident != null)
                {
                    if (findCondition(this.mod.versionids, this.ident))
                    {
                        this.inc = Include.yes;
                        definedInModule = true;
                    }
                    else if (findCondition(global.versionids, this.ident))
                    {
                        this.inc = Include.yes;
                    }
                    else
                    {
                        if (this.mod.versionidsNot == null)
                        {
                            this.mod.versionidsNot = pcopy(new DArray<Identifier>());
                        }
                        (this.mod.versionidsNot).push(this.ident);
                    }
                }
                else if ((this.level <= global.params.versionlevel) || (this.level <= this.mod.versionlevel))
                {
                    this.inc = Include.yes;
                }
                if (!definedInModule && (this.ident == null) || !isReserved(this.ident.asString()) && (!pequals(this.ident, Id._unittest)) && (!pequals(this.ident, Id._assert)))
                {
                    printDepsConditional(sc, this, new ByteSlice("depsVersion "));
                }
            }
            return ((this.inc == Include.yes) ? 1 : 0);
        }

        // Erasure: isVersionCondition<>
        public  VersionCondition isVersionCondition() {
            return this;
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.ident != null ? this.ident.toChars() : new BytePtr("version");
        }


        public VersionCondition() {}

        public VersionCondition copy() {
            VersionCondition that = new VersionCondition();
            that.level = this.level;
            that.ident = this.ident;
            that.mod = this.mod;
            that.loc = this.loc;
            that.inc = this.inc;
            return that;
        }
    }
    public static class StaticIfCondition extends Condition
    {
        public Expression exp = null;
        // Erasure: __ctor<Loc, Expression>
        public  StaticIfCondition(Loc loc, Expression exp) {
            super(loc);
            this.exp = exp;
        }

        // Erasure: syntaxCopy<>
        public  Condition syntaxCopy() {
            return new StaticIfCondition(this.loc, this.exp.syntaxCopy());
        }

        // Erasure: include<Ptr>
        public  int include(Ptr<Scope> sc) {
            StaticIfCondition __self = this;
            Function0<Integer> errorReturn = new Function0<Integer>() {
                public Integer invoke() {
                 {
                    if (global.gag == 0)
                    {
                        inc = Include.no;
                    }
                    return 0;
                }}

            };
            if ((this.inc == Include.notComputed))
            {
                if (sc == null)
                {
                    error(this.loc, new BytePtr("`static if` conditional cannot be at global scope"));
                    this.inc = Include.no;
                    return 0;
                }
                Ref<Boolean> errors = ref(false);
                boolean result = evalStaticCondition(sc, this.exp, this.exp, errors);
                if ((this.inc != Include.notComputed))
                {
                    return ((this.inc == Include.yes) ? 1 : 0);
                }
                if (errors.value)
                {
                    return errorReturn.invoke();
                }
                if (result)
                {
                    this.inc = Include.yes;
                }
                else
                {
                    this.inc = Include.no;
                }
            }
            return ((this.inc == Include.yes) ? 1 : 0);
        }

        // Erasure: accept<Visitor>
        public  void accept(Visitor v) {
            v.visit(this);
        }

        // Erasure: toChars<>
        public  BytePtr toChars() {
            return this.exp != null ? this.exp.toChars() : new BytePtr("static if");
        }


        public StaticIfCondition() {}

        public StaticIfCondition copy() {
            StaticIfCondition that = new StaticIfCondition();
            that.exp = this.exp;
            that.loc = this.loc;
            that.inc = this.inc;
            return that;
        }
    }
    // Erasure: findCondition<Ptr, Identifier>
    public static boolean findCondition(DArray<Identifier> ids, Identifier ident) {
        if (ids != null)
        {
            {
                Slice<Identifier> __r829 = (ids).opSlice().copy();
                int __key830 = 0;
                for (; (__key830 < __r829.getLength());__key830 += 1) {
                    Identifier id = __r829.get(__key830);
                    if ((pequals(id, ident)))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // Erasure: printDepsConditional<Ptr, DVCondition, Array>
    public static void printDepsConditional(Ptr<Scope> sc, DVCondition condition, ByteSlice depType) {
        if ((global.params.moduleDeps == null) || (global.params.moduleDepsFile.getLength() != 0))
        {
            return ;
        }
        OutBuffer ob = global.params.moduleDeps;
        dmodule.Module imod = sc != null ? (sc.get()).instantiatingModule() : condition.mod;
        if (imod == null)
        {
            return ;
        }
        (ob).writestring(depType);
        (ob).writestring(imod.toPrettyChars(false));
        (ob).writestring(new ByteSlice(" ("));
        escapePath(ob, imod.srcfile.toChars());
        (ob).writestring(new ByteSlice(") : "));
        if (condition.ident != null)
        {
            (ob).writestring(condition.ident.asString());
        }
        else
        {
            (ob).print((long)condition.level);
        }
        (ob).writeByte(10);
    }

}
