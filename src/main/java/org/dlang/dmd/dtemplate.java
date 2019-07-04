package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.aggregate.*;
import static org.dlang.dmd.aliasthis.*;
import static org.dlang.dmd.arraytypes.*;
import static org.dlang.dmd.ast_node.*;
import static org.dlang.dmd.dcast.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.dmangle.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.expression.*;
import static org.dlang.dmd.expressionsem.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.impcnvtab.*;
import static org.dlang.dmd.init.*;
import static org.dlang.dmd.initsem.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.opover.*;
import static org.dlang.dmd.semantic2.*;
import static org.dlang.dmd.semantic3.*;
import static org.dlang.dmd.staticcond.*;
import static org.dlang.dmd.templateparamsem.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.typesem.*;
import static org.dlang.dmd.visitor.*;

public class dtemplate {
    private static class DeduceType extends Visitor
    {
        private Scope sc;
        private Type tparam;
        private DArray<TemplateParameter> parameters;
        private DArray<RootObject> dedtypes;
        private IntPtr wm;
        private int inferStart;
        private boolean ignoreAliasThis;
        private int result;
        public  DeduceType(Scope sc, Type tparam, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, IntPtr wm, int inferStart, boolean ignoreAliasThis) {
            this.sc = sc;
            this.tparam = tparam;
            this.parameters = parameters;
            this.dedtypes = dedtypes;
            this.wm = pcopy(wm);
            this.inferStart = inferStart;
            this.ignoreAliasThis = ignoreAliasThis;
            this.result = MATCH.nomatch;
        }

        public  void visit(Type t) {
            try {
                if (!(this.tparam != null))
                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                try {
                    if (pequals(t, this.tparam))
                        /*goto Lexact*/throw Dispatch0.INSTANCE;
                    try {
                        if ((this.tparam.ty & 0xFF) == ENUMTY.Tident)
                        {
                            int i = templateParameterLookup(this.tparam, this.parameters);
                            if (i == 305419896)
                            {
                                if (this.sc == null)
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                Loc loc = new Loc();
                                if (((this.parameters).length) != 0)
                                {
                                    TemplateParameter tp = (this.parameters).get(0);
                                    loc = tp.loc.copy();
                                }
                                this.tparam = typeSemantic(this.tparam, loc, this.sc);
                                assert((this.tparam.ty & 0xFF) != ENUMTY.Tident);
                                this.result = deduceType(t, this.sc, this.tparam, this.parameters, this.dedtypes, this.wm, 0, false);
                                return ;
                            }
                            TemplateParameter tp = (this.parameters).get(i);
                            TypeIdentifier tident = (TypeIdentifier)this.tparam;
                            if (tident.idents.length > 0)
                            {
                                Dsymbol s = t.toDsymbol(this.sc);
                                {
                                    int j = tident.idents.length;
                                L_outer15:
                                    for (; j-- > 0;){
                                        RootObject id = tident.idents.get(j);
                                        if (id.dyncast() == DYNCAST.identifier)
                                        {
                                            if ((!(s != null) || !(s.parent != null)))
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            Dsymbol s2 = s.parent.search(Loc.initial, (Identifier)id, 0);
                                            if (!(s2 != null))
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            s2 = s2.toAlias();
                                            if (!pequals(s, s2))
                                            {
                                                {
                                                    Type tx = s2.getType();
                                                    if (tx != null)
                                                    {
                                                        if (!pequals(s, tx.toDsymbol(this.sc)))
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    else
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                }
                                            }
                                            s = s.parent;
                                        }
                                        else
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                }
                                if (tp.isTemplateTypeParameter() != null)
                                {
                                    Type tt = s.getType();
                                    if (!(tt != null))
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    Type at = (Type)(this.dedtypes).get(i);
                                    if ((at != null && (at.ty & 0xFF) == ENUMTY.Tnone))
                                        at = ((TypeDeduced)at).tded;
                                    if ((!(at != null) || tt.equals(at)))
                                    {
                                        this.dedtypes.set(i, tt);
                                        /*goto Lexact*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                if (tp.isTemplateAliasParameter() != null)
                                {
                                    Dsymbol s2 = (Dsymbol)(this.dedtypes).get(i);
                                    if ((!(s2 != null) || pequals(s, s2)))
                                    {
                                        this.dedtypes.set(i, s);
                                        /*goto Lexact*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            }
                            if (!(tp.isTemplateTypeParameter() != null))
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            Type at = (Type)(this.dedtypes).get(i);
                            Ref<Type> tt = ref(null);
                            {
                                byte wx = this.wm != null ? (byte)(deduceWildHelper(t, ptr(tt), this.tparam) & 0xFF) : (byte)0;
                                if ((wx) != 0)
                                {
                                    if (!(at != null))
                                    {
                                        this.dedtypes.set(i, tt.value);
                                        this.wm.set(0, this.wm.get() | (wx & 0xFF));
                                        this.result = MATCH.constant;
                                        return ;
                                    }
                                    if ((at.ty & 0xFF) == ENUMTY.Tnone)
                                    {
                                        TypeDeduced xt = (TypeDeduced)at;
                                        this.result = xt.matchAll(tt.value);
                                        if (this.result > MATCH.nomatch)
                                        {
                                            this.dedtypes.set(i, tt.value);
                                            if (this.result > MATCH.constant)
                                                this.result = MATCH.constant;
                                        }
                                        return ;
                                    }
                                    if (tt.value.equals(at))
                                    {
                                        this.dedtypes.set(i, tt.value);
                                        /*goto Lconst*/throw Dispatch2.INSTANCE;
                                    }
                                    if ((tt.value.implicitConvTo(at.constOf())) != 0)
                                    {
                                        this.dedtypes.set(i, at.constOf().mutableOf());
                                        this.wm.set(0, this.wm.get() | 1);
                                        /*goto Lconst*/throw Dispatch2.INSTANCE;
                                    }
                                    if ((at.implicitConvTo(tt.value.constOf())) != 0)
                                    {
                                        this.dedtypes.set(i, tt.value.constOf().mutableOf());
                                        this.wm.set(0, this.wm.get() | 1);
                                        /*goto Lconst*/throw Dispatch2.INSTANCE;
                                    }
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                }
                                else {
                                    int m = deduceTypeHelper(t, ptr(tt), this.tparam);
                                    if ((m) != 0)
                                    {
                                        if (!(at != null))
                                        {
                                            this.dedtypes.set(i, tt.value);
                                            this.result = m;
                                            return ;
                                        }
                                        if ((at.ty & 0xFF) == ENUMTY.Tnone)
                                        {
                                            TypeDeduced xt = (TypeDeduced)at;
                                            this.result = xt.matchAll(tt.value);
                                            if (this.result > MATCH.nomatch)
                                            {
                                                this.dedtypes.set(i, tt.value);
                                            }
                                            return ;
                                        }
                                        if (tt.value.equals(at))
                                        {
                                            /*goto Lexact*/throw Dispatch0.INSTANCE;
                                        }
                                        if (((tt.value.ty & 0xFF) == ENUMTY.Tclass && (at.ty & 0xFF) == ENUMTY.Tclass))
                                        {
                                            this.result = tt.value.implicitConvTo(at);
                                            return ;
                                        }
                                        if ((((tt.value.ty & 0xFF) == ENUMTY.Tsarray && (at.ty & 0xFF) == ENUMTY.Tarray) && tt.value.nextOf().implicitConvTo(at.nextOf()) >= MATCH.constant))
                                        {
                                            /*goto Lexact*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                            }
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                        if ((this.tparam.ty & 0xFF) == ENUMTY.Ttypeof)
                        {
                            Loc loc = new Loc();
                            if (((this.parameters).length) != 0)
                            {
                                TemplateParameter tp = (this.parameters).get(0);
                                loc = tp.loc.copy();
                            }
                            this.tparam = typeSemantic(this.tparam, loc, this.sc);
                        }
                        if ((t.ty & 0xFF) != (this.tparam.ty & 0xFF))
                        {
                            {
                                Dsymbol sym = t.toDsymbol(this.sc);
                                if (sym != null)
                                {
                                    if ((sym.isforwardRef() && this.tparam.deco == null))
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                }
                            }
                            int m = t.implicitConvTo(this.tparam);
                            if ((m == MATCH.nomatch && !(this.ignoreAliasThis)))
                            {
                                if ((t.ty & 0xFF) == ENUMTY.Tclass)
                                {
                                    TypeClass tc = (TypeClass)t;
                                    if ((tc.sym.aliasthis != null && !((tc.att & AliasThisRec.tracingDT) != 0)))
                                    {
                                        {
                                            Type ato = t.aliasthisOf();
                                            if (ato != null)
                                            {
                                                tc.att = tc.att | AliasThisRec.tracingDT;
                                                m = deduceType(ato, this.sc, this.tparam, this.parameters, this.dedtypes, this.wm, 0, false);
                                                tc.att = tc.att & -9;
                                            }
                                        }
                                    }
                                }
                                else if ((t.ty & 0xFF) == ENUMTY.Tstruct)
                                {
                                    TypeStruct ts = (TypeStruct)t;
                                    if ((ts.sym.aliasthis != null && !((ts.att & AliasThisRec.tracingDT) != 0)))
                                    {
                                        {
                                            Type ato = t.aliasthisOf();
                                            if (ato != null)
                                            {
                                                ts.att = ts.att | AliasThisRec.tracingDT;
                                                m = deduceType(ato, this.sc, this.tparam, this.parameters, this.dedtypes, this.wm, 0, false);
                                                ts.att = ts.att & -9;
                                            }
                                        }
                                    }
                                }
                            }
                            this.result = m;
                            return ;
                        }
                        if (t.nextOf() != null)
                        {
                            if ((this.tparam.deco != null && !((this.tparam.hasWild()) != 0)))
                            {
                                this.result = t.implicitConvTo(this.tparam);
                                return ;
                            }
                            Type tpn = this.tparam.nextOf();
                            if (((this.wm != null && (t.ty & 0xFF) == ENUMTY.Taarray) && this.tparam.isWild()))
                            {
                                tpn = tpn.substWildTo(16);
                            }
                            this.result = deduceType(t.nextOf(), this.sc, tpn, this.parameters, this.dedtypes, this.wm, 0, false);
                            return ;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*Lexact:*/
                    this.result = MATCH.exact;
                    return ;
                }
                catch(Dispatch1 __d){}
            /*Lnomatch:*/
                this.result = MATCH.nomatch;
                return ;
            }
            catch(Dispatch2 __d){}
        /*Lconst:*/
            this.result = MATCH.constant;
        }

        public  void visit(TypeVector t) {
            if ((this.tparam.ty & 0xFF) == ENUMTY.Tvector)
            {
                TypeVector tp = (TypeVector)this.tparam;
                this.result = deduceType(t.basetype, this.sc, tp.basetype, this.parameters, this.dedtypes, this.wm, 0, false);
                return ;
            }
            this.visit((Type)t);
        }

        public  void visit(TypeDArray t) {
            this.visit((Type)t);
        }

        public  void visit(TypeSArray t) {
            if (this.tparam != null)
            {
                if ((this.tparam.ty & 0xFF) == ENUMTY.Tarray)
                {
                    int m = deduceType(t.next, this.sc, this.tparam.nextOf(), this.parameters, this.dedtypes, this.wm, 0, false);
                    this.result = m >= MATCH.constant ? MATCH.convert : MATCH.nomatch;
                    return ;
                }
                TemplateParameter tp = null;
                Expression edim = null;
                int i = 0;
                if ((this.tparam.ty & 0xFF) == ENUMTY.Tsarray)
                {
                    TypeSArray tsa = (TypeSArray)this.tparam;
                    if (((tsa.dim.op & 0xFF) == 26 && (((VarExp)tsa.dim).var.storage_class & 262144L) != 0))
                    {
                        Identifier id = ((VarExp)tsa.dim).var.ident;
                        i = templateIdentifierLookup(id, this.parameters);
                        assert(i != 305419896);
                        tp = (this.parameters).get(i);
                    }
                    else
                        edim = tsa.dim;
                }
                else if ((this.tparam.ty & 0xFF) == ENUMTY.Taarray)
                {
                    TypeAArray taa = (TypeAArray)this.tparam;
                    i = templateParameterLookup(taa.index, this.parameters);
                    if (i != 305419896)
                        tp = (this.parameters).get(i);
                    else
                    {
                        Ref<Expression> e = ref(null);
                        Ref<Type> tx = ref(null);
                        Ref<Dsymbol> s = ref(null);
                        resolve(taa.index, Loc.initial, this.sc, ptr(e), ptr(tx), ptr(s), false);
                        edim = s.value != null ? getValue(s) : getValue(e.value);
                    }
                }
                if (((tp != null && (tp.matchArg(this.sc, t.dim, i, this.parameters, this.dedtypes, null)) != 0) || (edim != null && edim.toInteger() == t.dim.toInteger())))
                {
                    this.result = deduceType(t.next, this.sc, this.tparam.nextOf(), this.parameters, this.dedtypes, this.wm, 0, false);
                    return ;
                }
            }
            this.visit((Type)t);
        }

        public  void visit(TypeAArray t) {
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Taarray))
            {
                TypeAArray tp = (TypeAArray)this.tparam;
                if (!((deduceType(t.index, this.sc, tp.index, this.parameters, this.dedtypes, null, 0, false)) != 0))
                {
                    this.result = MATCH.nomatch;
                    return ;
                }
            }
            this.visit((Type)t);
        }

        public  void visit(TypeFunction t) {
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tfunction))
            {
                TypeFunction tp = (TypeFunction)this.tparam;
                if ((t.parameterList.varargs != tp.parameterList.varargs || t.linkage != tp.linkage))
                {
                    this.result = MATCH.nomatch;
                    return ;
                }
                {
                    Slice<Parameter> __r1203 = (tp.parameterList.parameters).opSlice().copy();
                    int __key1204 = 0;
                    for (; __key1204 < __r1203.getLength();__key1204 += 1) {
                        Parameter fparam = __r1203.get(__key1204);
                        fparam.type = fparam.type.addStorageClass(fparam.storageClass);
                        fparam.storageClass &= -2685405189L;
                        if (!(reliesOnTemplateParameters(fparam.type, (this.parameters).opSlice(this.inferStart, (this.parameters).length))))
                        {
                            Type tx = typeSemantic(fparam.type, Loc.initial, this.sc);
                            if ((tx.ty & 0xFF) == ENUMTY.Terror)
                            {
                                this.result = MATCH.nomatch;
                                return ;
                            }
                            fparam.type = tx;
                        }
                    }
                }
                int nfargs = t.parameterList.length();
                int nfparams = tp.parameterList.length();
                try {
                    try {
                        if ((nfparams > 0 && nfargs >= nfparams - 1))
                        {
                            Parameter fparam = tp.parameterList.get(nfparams - 1);
                            assert(fparam != null);
                            assert(fparam.type != null);
                            if ((fparam.type.ty & 0xFF) != ENUMTY.Tident)
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            TypeIdentifier tid = (TypeIdentifier)fparam.type;
                            if ((tid.idents.length) != 0)
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            int tupi = 0;
                        L_outer16:
                            for (; (1) != 0;tupi++){
                                if (tupi == (this.parameters).length)
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                TemplateParameter tx = (this.parameters).get(tupi);
                                TemplateTupleParameter tup = tx.isTemplateTupleParameter();
                                if ((tup != null && tup.ident.equals(tid.ident)))
                                    break;
                            }
                            int tuple_dim = nfargs - (nfparams - 1);
                            RootObject o = (this.dedtypes).get(tupi);
                            if (o != null)
                            {
                                Tuple tup = isTuple(o);
                                if ((!(tup != null) || tup.objects.length != tuple_dim))
                                {
                                    this.result = MATCH.nomatch;
                                    return ;
                                }
                                {
                                    int i = 0;
                                    for (; i < tuple_dim;i++){
                                        Parameter arg = t.parameterList.get(nfparams - 1 + i);
                                        if (!(arg.type.equals(tup.objects.get(i))))
                                        {
                                            this.result = MATCH.nomatch;
                                            return ;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                Tuple tup = new Tuple(tuple_dim);
                                {
                                    int i = 0;
                                    for (; i < tuple_dim;i++){
                                        Parameter arg = t.parameterList.get(nfparams - 1 + i);
                                        tup.objects.set(i, arg.type);
                                    }
                                }
                                this.dedtypes.set(tupi, tup);
                            }
                            nfparams--;
                            /*goto L2*/throw Dispatch1.INSTANCE;
                        }
                    }
                    catch(Dispatch0 __d){}
                /*L1:*/
                    if (nfargs != nfparams)
                    {
                        this.result = MATCH.nomatch;
                        return ;
                    }
                }
                catch(Dispatch1 __d){}
            /*L2:*/
                {
                    int i = 0;
                    for (; i < nfparams;i++){
                        Parameter a = t.parameterList.get(i);
                        Parameter ap = tp.parameterList.get(i);
                        if ((!(a.isCovariant(t.isref, ap)) || !((deduceType(a.type, this.sc, ap.type, this.parameters, this.dedtypes, null, 0, false)) != 0)))
                        {
                            this.result = MATCH.nomatch;
                            return ;
                        }
                    }
                }
            }
            this.visit((Type)t);
        }

        public  void visit(TypeIdentifier t) {
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tident))
            {
                TypeIdentifier tp = (TypeIdentifier)this.tparam;
                {
                    int i = 0;
                    for (; i < t.idents.length;i++){
                        RootObject id1 = t.idents.get(i);
                        RootObject id2 = tp.idents.get(i);
                        if (!(id1.equals(id2)))
                        {
                            this.result = MATCH.nomatch;
                            return ;
                        }
                    }
                }
            }
            this.visit((Type)t);
        }

        public  void visit(TypeInstance t) {
            try {
                if (((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tinstance) && t.tempinst.tempdecl != null))
                {
                    TemplateDeclaration tempdecl = t.tempinst.tempdecl.isTemplateDeclaration();
                    assert(tempdecl != null);
                    TypeInstance tp = (TypeInstance)this.tparam;
                    try {
                        if (!(tp.tempinst.tempdecl != null))
                        {
                            int i = templateIdentifierLookup(tp.tempinst.name, this.parameters);
                            if (i == 305419896)
                            {
                                TypeIdentifier tid = new TypeIdentifier(tp.loc, tp.tempinst.name);
                                Ref<Type> tx = ref(null);
                                Ref<Expression> e = ref(null);
                                Ref<Dsymbol> s = ref(null);
                                resolve(tid, tp.loc, this.sc, ptr(e), ptr(tx), ptr(s), false);
                                if (tx.value != null)
                                {
                                    s.value = tx.value.toDsymbol(this.sc);
                                    {
                                        TemplateInstance ti = s.value != null ? s.value.parent.isTemplateInstance() : null;
                                        if (ti != null)
                                        {
                                            Dsymbol p = (this.sc).parent;
                                            for (; (p != null && !pequals(p, ti));) {
                                                p = p.parent;
                                            }
                                            if (p != null)
                                                s.value = ti.tempdecl;
                                        }
                                    }
                                }
                                if (s.value != null)
                                {
                                    s.value = s.value.toAlias();
                                    TemplateDeclaration td = s.value.isTemplateDeclaration();
                                    if (td != null)
                                    {
                                        if (td.overroot != null)
                                            td = td.overroot;
                                    L_outer17:
                                        for (; td != null;td = td.overnext){
                                            if (pequals(td, tempdecl))
                                                /*goto L2*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            TemplateParameter tpx = (this.parameters).get(i);
                            if (!((tpx.matchArg(this.sc, tempdecl, i, this.parameters, this.dedtypes, null)) != 0))
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        else if (!pequals(tempdecl, tp.tempinst.tempdecl))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    catch(Dispatch0 __d){}
                /*L2:*/
                    {
                        int i = 0;
                    L_outer18:
                        for (; (1) != 0;i++){
                            RootObject o1 = null;
                            if (i < (t.tempinst.tiargs).length)
                                o1 = (t.tempinst.tiargs).get(i);
                            else if ((i < t.tempinst.tdtypes.length && i < (tp.tempinst.tiargs).length))
                            {
                                o1 = t.tempinst.tdtypes.get(i);
                            }
                            else if (i >= (tp.tempinst.tiargs).length)
                                break;
                            if (i >= (tp.tempinst.tiargs).length)
                            {
                                int dim = (tempdecl.parameters).length - (tempdecl.isVariadic() != null ? 1 : 0);
                                for (; (i < dim && ((tempdecl.parameters).get(i).dependent || (tempdecl.parameters).get(i).hasDefaultArg()));){
                                    i++;
                                }
                                if (i >= dim)
                                    break;
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            RootObject o2 = (tp.tempinst.tiargs).get(i);
                            Ref<Type> t2 = ref(isType(o2));
                            int j = ((t2.value != null && (t2.value.ty & 0xFF) == ENUMTY.Tident) && i == (tp.tempinst.tiargs).length - 1) ? templateParameterLookup(t2.value, this.parameters) : 305419896;
                            if (((j != 305419896 && j == (this.parameters).length - 1) && (this.parameters).get(j).isTemplateTupleParameter() != null))
                            {
                                int vtdim = (tempdecl.isVariadic() != null ? (t.tempinst.tiargs).length : t.tempinst.tdtypes.length) - i;
                                Tuple vt = new Tuple(vtdim);
                                {
                                    int k = 0;
                                    for (; k < vtdim;k++){
                                        RootObject o = null;
                                        if (k < (t.tempinst.tiargs).length)
                                            o = (t.tempinst.tiargs).get(i + k);
                                        else
                                            o = t.tempinst.tdtypes.get(i + k);
                                        vt.objects.set(k, o);
                                    }
                                }
                                Tuple v = (Tuple)(this.dedtypes).get(j);
                                if (v != null)
                                {
                                    if (!(match(v, vt)))
                                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                                else
                                    this.dedtypes.set(j, vt);
                                break;
                            }
                            else if (!(o1 != null))
                                break;
                            Type t1 = isType(o1);
                            Ref<Dsymbol> s1 = ref(isDsymbol(o1));
                            Ref<Dsymbol> s2 = ref(isDsymbol(o2));
                            Expression e1 = s1.value != null ? getValue(s1) : getValue(isExpression(o1));
                            Ref<Expression> e2 = ref(isExpression(o2));
                            if ((t1 != null && t2.value != null))
                            {
                                if (!((deduceType(t1, this.sc, t2.value, this.parameters, this.dedtypes, null, 0, false)) != 0))
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            else if ((e1 != null && e2.value != null))
                            {
                            /*Le:*/
                                e1 = e1.ctfeInterpret();
                                if (((e2.value.op & 0xFF) == 26 && (((VarExp)e2.value).var.storage_class & 262144L) != 0))
                                {
                                    j = templateIdentifierLookup(((VarExp)e2.value).var.ident, this.parameters);
                                    if (j != 305419896)
                                        /*goto L1*/throw Dispatch.INSTANCE;
                                }
                                e2.value = expressionSemantic(e2.value, this.sc);
                                e2.value = e2.value.ctfeInterpret();
                                if (!(e1.equals(e2.value)))
                                {
                                    if (!((e2.value.implicitConvTo(e1.type)) != 0))
                                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                    e2.value = e2.value.implicitCastTo(this.sc, e1.type);
                                    e2.value = e2.value.ctfeInterpret();
                                    if (!(e1.equals(e2.value)))
                                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                            }
                            else if (((e1 != null && t2.value != null) && (t2.value.ty & 0xFF) == ENUMTY.Tident))
                            {
                                j = templateParameterLookup(t2.value, this.parameters);
                            /*L1:*/
                                if (j == 305419896)
                                {
                                    resolve(t2.value, ((TypeIdentifier)t2.value).loc, this.sc, ptr(e2), ptr(t2), ptr(s2), false);
                                    if (e2.value != null)
                                        /*goto Le*/throw Dispatch0.INSTANCE;
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                                if (!(((this.parameters).get(j).matchArg(this.sc, e1, j, this.parameters, this.dedtypes, null)) != 0))
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            else if ((s1.value != null && s2.value != null))
                            {
                            /*Ls:*/
                                if (!(s1.value.equals(s2.value)))
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            else if (((s1.value != null && t2.value != null) && (t2.value.ty & 0xFF) == ENUMTY.Tident))
                            {
                                j = templateParameterLookup(t2.value, this.parameters);
                                if (j == 305419896)
                                {
                                    resolve(t2.value, ((TypeIdentifier)t2.value).loc, this.sc, ptr(e2), ptr(t2), ptr(s2), false);
                                    if (s2.value != null)
                                        /*goto Ls*/throw Dispatch.INSTANCE;
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                                }
                                if (!(((this.parameters).get(j).matchArg(this.sc, s1.value, j, this.parameters, this.dedtypes, null)) != 0))
                                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                            }
                            else
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                this.visit((Type)t);
                return ;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            this.result = MATCH.nomatch;
        }

        public  void visit(TypeStruct t) {
            TemplateInstance ti = t.sym.parent.isTemplateInstance();
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tinstance))
            {
                if ((ti != null && pequals(ti.toAlias(), t.sym)))
                {
                    TypeInstance tx = new TypeInstance(Loc.initial, ti);
                    this.result = deduceType(tx, this.sc, this.tparam, this.parameters, this.dedtypes, this.wm, 0, false);
                    return ;
                }
                TypeInstance tpi = (TypeInstance)this.tparam;
                if ((tpi.idents.length) != 0)
                {
                    RootObject id = tpi.idents.get(tpi.idents.length - 1);
                    if ((id.dyncast() == DYNCAST.identifier && t.sym.ident.equals((Identifier)id)))
                    {
                        Type tparent = t.sym.parent.getType();
                        if (tparent != null)
                        {
                            tpi.idents.length--;
                            this.result = deduceType(tparent, this.sc, tpi, this.parameters, this.dedtypes, this.wm, 0, false);
                            tpi.idents.length++;
                            return ;
                        }
                    }
                }
            }
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tstruct))
            {
                TypeStruct tp = (TypeStruct)this.tparam;
                if ((this.wm != null && (t.deduceWild(this.tparam, false)) != 0))
                {
                    this.result = MATCH.constant;
                    return ;
                }
                this.result = t.implicitConvTo(tp);
                return ;
            }
            this.visit((Type)t);
        }

        public  void visit(TypeEnum t) {
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tenum))
            {
                TypeEnum tp = (TypeEnum)this.tparam;
                if (pequals(t.sym, tp.sym))
                    this.visit((Type)t);
                else
                    this.result = MATCH.nomatch;
                return ;
            }
            Type tb = t.toBasetype();
            if (((tb.ty & 0xFF) == (this.tparam.ty & 0xFF) || ((tb.ty & 0xFF) == ENUMTY.Tsarray && (this.tparam.ty & 0xFF) == ENUMTY.Taarray)))
            {
                this.result = deduceType(tb, this.sc, this.tparam, this.parameters, this.dedtypes, this.wm, 0, false);
                return ;
            }
            this.visit((Type)t);
        }

        public static void deduceBaseClassParameters(BaseClass b, Scope sc, Type tparam, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, DArray<RootObject> best, IntRef numBaseClassMatches) {
            TemplateInstance parti = b.sym != null ? b.sym.parent.isTemplateInstance() : null;
            if (parti != null)
            {
                DArray<RootObject> tmpdedtypes = new DArray<RootObject>((dedtypes).length);
                memcpy((BytePtr)((tmpdedtypes).tdata()), ((dedtypes).tdata()), ((dedtypes).length * 4));
                TypeInstance t = new TypeInstance(Loc.initial, parti);
                int m = deduceType(t, sc, tparam, parameters, tmpdedtypes, null, 0, false);
                if (m > MATCH.nomatch)
                {
                    if (numBaseClassMatches.value == 0)
                        memcpy((BytePtr)((best).tdata()), ((tmpdedtypes).tdata()), ((tmpdedtypes).length * 4));
                    else
                    {
                        int k = 0;
                        for (; k < (tmpdedtypes).length;k += 1){
                            if (!pequals((tmpdedtypes).get(k), (best).get(k)))
                                best.set(k, (dedtypes).get(k));
                        }
                    }
                    numBaseClassMatches.value += 1;
                }
            }
            {
                Slice<BaseClass> __r1205 = b.baseInterfaces.copy();
                int __key1206 = 0;
                for (; __key1206 < __r1205.getLength();__key1206 += 1) {
                    BaseClass bi = __r1205.get(__key1206).copy();
                    deduceBaseClassParameters(bi, sc, tparam, parameters, dedtypes, best, numBaseClassMatches);
                }
            }
        }

        public  void visit(TypeClass t) {
            TemplateInstance ti = t.sym.parent.isTemplateInstance();
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tinstance))
            {
                if ((ti != null && pequals(ti.toAlias(), t.sym)))
                {
                    TypeInstance tx = new TypeInstance(Loc.initial, ti);
                    int m = deduceType(tx, this.sc, this.tparam, this.parameters, this.dedtypes, this.wm, 0, false);
                    if (m != MATCH.nomatch)
                    {
                        this.result = m;
                        return ;
                    }
                }
                TypeInstance tpi = (TypeInstance)this.tparam;
                if ((tpi.idents.length) != 0)
                {
                    RootObject id = tpi.idents.get(tpi.idents.length - 1);
                    if ((id.dyncast() == DYNCAST.identifier && t.sym.ident.equals((Identifier)id)))
                    {
                        Type tparent = t.sym.parent.getType();
                        if (tparent != null)
                        {
                            tpi.idents.length--;
                            this.result = deduceType(tparent, this.sc, tpi, this.parameters, this.dedtypes, this.wm, 0, false);
                            tpi.idents.length++;
                            return ;
                        }
                    }
                }
                this.visit((Type)t);
                if (this.result != MATCH.nomatch)
                    return ;
                IntRef numBaseClassMatches = ref(0);
                DArray<RootObject> best = new DArray<RootObject>((this.dedtypes).length);
                ClassDeclaration s = t.sym;
                for (; (s != null && (s.baseclasses).length > 0);){
                    deduceBaseClassParameters((s.baseclasses).get(0), this.sc, this.tparam, this.parameters, this.dedtypes, best, numBaseClassMatches);
                    {
                        Slice<BaseClass> __r1207 = s.interfaces.copy();
                        int __key1208 = 0;
                        for (; __key1208 < __r1207.getLength();__key1208 += 1) {
                            BaseClass b = __r1207.get(__key1208);
                            deduceBaseClassParameters(b, this.sc, this.tparam, this.parameters, this.dedtypes, best, numBaseClassMatches);
                        }
                    }
                    s = ((s.baseclasses).get(0)).sym;
                }
                if (numBaseClassMatches.value == 0)
                {
                    this.result = MATCH.nomatch;
                    return ;
                }
                memcpy((BytePtr)((this.dedtypes).tdata()), ((best).tdata()), ((best).length * 4));
                this.result = MATCH.convert;
                return ;
            }
            if ((this.tparam != null && (this.tparam.ty & 0xFF) == ENUMTY.Tclass))
            {
                TypeClass tp = (TypeClass)this.tparam;
                if ((this.wm != null && (t.deduceWild(this.tparam, false)) != 0))
                {
                    this.result = MATCH.constant;
                    return ;
                }
                this.result = t.implicitConvTo(tp);
                return ;
            }
            this.visit((Type)t);
        }

        public  void visit(Expression e) {
            int i = templateParameterLookup(this.tparam, this.parameters);
            if ((i == 305419896 || ((TypeIdentifier)this.tparam).idents.length > 0))
            {
                if ((pequals(e, emptyArrayElement) && (this.tparam.ty & 0xFF) == ENUMTY.Tarray))
                {
                    Type tn = ((TypeNext)this.tparam).next;
                    this.result = deduceType(emptyArrayElement, this.sc, tn, this.parameters, this.dedtypes, this.wm, 0, false);
                    return ;
                }
                e.type.accept(this);
                return ;
            }
            TemplateTypeParameter tp = (this.parameters).get(i).isTemplateTypeParameter();
            if (!(tp != null))
                return ;
            if (pequals(e, emptyArrayElement))
            {
                if ((this.dedtypes).get(i) != null)
                {
                    this.result = MATCH.exact;
                    return ;
                }
                if (tp.defaultType != null)
                {
                    tp.defaultType.accept(this);
                    return ;
                }
            }
            Function1<Type,Boolean> isTopRef = new Function1<Type,Boolean>(){
                public Boolean invoke(Type t){
                    Type tb = t.baseElemOf();
                    return (((tb.ty & 0xFF) == ENUMTY.Tclass || (tb.ty & 0xFF) == ENUMTY.Taarray) || ((tb.ty & 0xFF) == ENUMTY.Tstruct && tb.hasPointers()));
                }
            };
            Type at = (Type)(this.dedtypes).get(i);
            Ref<Type> tt = ref(null);
            {
                byte wx = deduceWildHelper(e.type, ptr(tt), this.tparam);
                if ((wx) != 0)
                {
                    this.wm.set(0, this.wm.get() | (wx & 0xFF));
                    this.result = MATCH.constant;
                }
                else {
                    int m = deduceTypeHelper(e.type, ptr(tt), this.tparam);
                    if ((m) != 0)
                    {
                        this.result = m;
                    }
                    else if (!(isTopRef.invoke(e.type)))
                    {
                        tt.value = e.type.mutableOf();
                        this.result = MATCH.convert;
                    }
                    else
                        return ;
                }
            }
            if (!(at != null))
            {
                this.dedtypes.set(i, new TypeDeduced(tt.value, e, this.tparam));
                return ;
            }
            TypeDeduced xt = null;
            if ((at.ty & 0xFF) == ENUMTY.Tnone)
            {
                xt = (TypeDeduced)at;
                at = xt.tded;
            }
            int match1 = xt != null ? xt.matchAll(tt.value) : MATCH.nomatch;
            Type pt = at.addMod(this.tparam.mod);
            if ((this.wm.get()) != 0)
                pt = pt.substWildTo(this.wm.get());
            int match2 = e.implicitConvTo(pt);
            if ((match1 > MATCH.nomatch && match2 > MATCH.nomatch))
            {
                if (at.implicitConvTo(tt.value) <= MATCH.nomatch)
                    match1 = MATCH.nomatch;
                else if (tt.value.implicitConvTo(at) <= MATCH.nomatch)
                    match2 = MATCH.nomatch;
                else if (((tt.value.isTypeBasic() != null && (tt.value.ty & 0xFF) == (at.ty & 0xFF)) && (tt.value.mod & 0xFF) != (at.mod & 0xFF)))
                {
                    if ((!(tt.value.isMutable()) && !(at.isMutable())))
                        tt.value = tt.value.mutableOf().addMod(MODmerge(tt.value.mod, at.mod));
                    else if (tt.value.isMutable())
                    {
                        if ((at.mod & 0xFF) == 0)
                            match1 = MATCH.nomatch;
                        else
                            match2 = MATCH.nomatch;
                    }
                    else if (at.isMutable())
                    {
                        if ((tt.value.mod & 0xFF) == 0)
                            match2 = MATCH.nomatch;
                        else
                            match1 = MATCH.nomatch;
                    }
                }
                else
                {
                    match1 = MATCH.nomatch;
                    match2 = MATCH.nomatch;
                }
            }
            if (match1 > MATCH.nomatch)
            {
                if (xt != null)
                    xt.update(tt.value, e, this.tparam);
                else
                    this.dedtypes.set(i, tt.value);
                this.result = match1;
                return ;
            }
            if (match2 > MATCH.nomatch)
            {
                if (xt != null)
                    xt.update(e, this.tparam);
                this.result = match2;
                return ;
            }
            {
                Type t = rawTypeMerge(at, tt.value);
                if (t != null)
                {
                    if (xt != null)
                        xt.update(t, e, this.tparam);
                    else
                        this.dedtypes.set(i, t);
                    pt = tt.value.addMod(this.tparam.mod);
                    if ((this.wm.get()) != 0)
                        pt = pt.substWildTo(this.wm.get());
                    this.result = e.implicitConvTo(pt);
                    return ;
                }
            }
            this.result = MATCH.nomatch;
        }

        public  int deduceEmptyArrayElement() {
            if (!(emptyArrayElement != null))
            {
                emptyArrayElement = new IdentifierExp(Loc.initial, Id.p);
                emptyArrayElement.type = Type.tvoid;
            }
            assert((this.tparam.ty & 0xFF) == ENUMTY.Tarray);
            Type tn = ((TypeNext)this.tparam).next;
            return deduceType(emptyArrayElement, this.sc, tn, this.parameters, this.dedtypes, this.wm, 0, false);
        }

        public  void visit(NullExp e) {
            if (((this.tparam.ty & 0xFF) == ENUMTY.Tarray && (e.type.ty & 0xFF) == ENUMTY.Tnull))
            {
                this.result = this.deduceEmptyArrayElement();
                return ;
            }
            this.visit((Expression)e);
        }

        public  void visit(StringExp e) {
            Type taai = null;
            if (((e.type.ty & 0xFF) == ENUMTY.Tarray && ((this.tparam.ty & 0xFF) == ENUMTY.Tsarray || (((this.tparam.ty & 0xFF) == ENUMTY.Taarray && ((taai = ((TypeAArray)this.tparam).index).ty & 0xFF) == ENUMTY.Tident) && ((TypeIdentifier)taai).idents.length == 0))))
            {
                e.type.nextOf().sarrayOf((long)e.len).accept(this);
                return ;
            }
            this.visit((Expression)e);
        }

        public  void visit(ArrayLiteralExp e) {
            if ((((e.elements == null || !(((e.elements).length) != 0)) && (e.type.toBasetype().nextOf().ty & 0xFF) == ENUMTY.Tvoid) && (this.tparam.ty & 0xFF) == ENUMTY.Tarray))
            {
                this.result = this.deduceEmptyArrayElement();
                return ;
            }
            if ((((this.tparam.ty & 0xFF) == ENUMTY.Tarray && e.elements != null) && ((e.elements).length) != 0))
            {
                Type tn = ((TypeDArray)this.tparam).next;
                this.result = MATCH.exact;
                if (e.basis != null)
                {
                    int m = deduceType(e.basis, this.sc, tn, this.parameters, this.dedtypes, this.wm, 0, false);
                    if (m < this.result)
                        this.result = m;
                }
                {
                    int i = 0;
                    for (; i < (e.elements).length;i++){
                        if (this.result <= MATCH.nomatch)
                            break;
                        Expression el = (e.elements).get(i);
                        if (!(el != null))
                            continue;
                        int m = deduceType(el, this.sc, tn, this.parameters, this.dedtypes, this.wm, 0, false);
                        if (m < this.result)
                            this.result = m;
                    }
                }
                return ;
            }
            Type taai = null;
            if (((e.type.ty & 0xFF) == ENUMTY.Tarray && ((this.tparam.ty & 0xFF) == ENUMTY.Tsarray || (((this.tparam.ty & 0xFF) == ENUMTY.Taarray && ((taai = ((TypeAArray)this.tparam).index).ty & 0xFF) == ENUMTY.Tident) && ((TypeIdentifier)taai).idents.length == 0))))
            {
                e.type.nextOf().sarrayOf((long)(e.elements).length).accept(this);
                return ;
            }
            this.visit((Expression)e);
        }

        public  void visit(AssocArrayLiteralExp e) {
            if ((((this.tparam.ty & 0xFF) == ENUMTY.Taarray && e.keys != null) && ((e.keys).length) != 0))
            {
                TypeAArray taa = (TypeAArray)this.tparam;
                this.result = MATCH.exact;
                {
                    int i = 0;
                    for (; i < (e.keys).length;i++){
                        int m1 = deduceType((e.keys).get(i), this.sc, taa.index, this.parameters, this.dedtypes, this.wm, 0, false);
                        if (m1 < this.result)
                            this.result = m1;
                        if (this.result <= MATCH.nomatch)
                            break;
                        int m2 = deduceType((e.values).get(i), this.sc, taa.next, this.parameters, this.dedtypes, this.wm, 0, false);
                        if (m2 < this.result)
                            this.result = m2;
                        if (this.result <= MATCH.nomatch)
                            break;
                    }
                }
                return ;
            }
            this.visit((Expression)e);
        }

        public  void visit(FuncExp e) {
            if (e.td != null)
            {
                Type to = this.tparam;
                if ((!(to.nextOf() != null) || (to.nextOf().ty & 0xFF) != ENUMTY.Tfunction))
                    return ;
                TypeFunction tof = (TypeFunction)to.nextOf();
                assert(e.td._scope != null);
                TypeFunction tf = (TypeFunction)e.fd.type;
                int dim = tf.parameterList.length();
                if ((tof.parameterList.length() != dim || tof.parameterList.varargs != tf.parameterList.varargs))
                    return ;
                DArray<RootObject> tiargs = new DArray<RootObject>();
                (tiargs).reserve((e.td.parameters).length);
                {
                    int i = 0;
                    for (; i < (e.td.parameters).length;i++){
                        TemplateParameter tp = (e.td.parameters).get(i);
                        int u = 0;
                        for (; u < dim;u++){
                            Parameter p = tf.parameterList.get(u);
                            if (((p.type.ty & 0xFF) == ENUMTY.Tident && pequals(((TypeIdentifier)p.type).ident, tp.ident)))
                            {
                                break;
                            }
                        }
                        assert(u < dim);
                        Parameter pto = tof.parameterList.get(u);
                        if (!(pto != null))
                            break;
                        Type t = pto.type.syntaxCopy();
                        if (reliesOnTemplateParameters(t, (this.parameters).opSlice(this.inferStart, (this.parameters).length)))
                            return ;
                        t = typeSemantic(t, e.loc, this.sc);
                        if ((t.ty & 0xFF) == ENUMTY.Terror)
                            return ;
                        (tiargs).push(t);
                    }
                }
                if ((!(tf.next != null) && tof.next != null))
                    e.fd.treq = this.tparam;
                TemplateInstance ti = new TemplateInstance(e.loc, e.td, tiargs);
                Expression ex = expressionSemantic(new ScopeExp(e.loc, ti), e.td._scope);
                e.fd.treq = null;
                if ((ex.op & 0xFF) == 127)
                    return ;
                if ((ex.op & 0xFF) != 161)
                    return ;
                this.visit(ex.type);
                return ;
            }
            Type t = e.type;
            if (((t.ty & 0xFF) == ENUMTY.Tdelegate && (this.tparam.ty & 0xFF) == ENUMTY.Tpointer))
                return ;
            if ((((e.tok & 0xFF) == 0 && (t.ty & 0xFF) == ENUMTY.Tpointer) && (this.tparam.ty & 0xFF) == ENUMTY.Tdelegate))
            {
                TypeFunction tf = (TypeFunction)t.nextOf();
                t = merge(new TypeDelegate(tf));
            }
            this.visit(t);
        }

        public  void visit(SliceExp e) {
            Type taai = null;
            if (((e.type.ty & 0xFF) == ENUMTY.Tarray && ((this.tparam.ty & 0xFF) == ENUMTY.Tsarray || (((this.tparam.ty & 0xFF) == ENUMTY.Taarray && ((taai = ((TypeAArray)this.tparam).index).ty & 0xFF) == ENUMTY.Tident) && ((TypeIdentifier)taai).idents.length == 0))))
            {
                {
                    Type tsa = toStaticArrayType(e);
                    if (tsa != null)
                    {
                        tsa.accept(this);
                        return ;
                    }
                }
            }
            this.visit((Expression)e);
        }

        public  void visit(CommaExp e) {
            e.e2.accept(this);
        }

        private Object this;

        public DeduceType() {}

        public DeduceType copy() {
            DeduceType that = new DeduceType();
            that.sc = this.sc;
            that.tparam = this.tparam;
            that.parameters = this.parameters;
            that.dedtypes = this.dedtypes;
            that.wm = this.wm;
            that.inferStart = this.inferStart;
            that.ignoreAliasThis = this.ignoreAliasThis;
            that.result = this.result;
            that.this = this.this;
            return that;
        }
    }
    private static class ReliesOnTemplateParameters extends Visitor
    {
        private Slice<TemplateParameter> tparams;
        private boolean result;
        public  ReliesOnTemplateParameters(Slice<TemplateParameter> tparams) {
            this.tparams = tparams.copy();
        }

        public  void visit(Expression e) {
        }

        public  void visit(IdentifierExp e) {
            {
                Slice<TemplateParameter> __r1219 = this.tparams.copy();
                int __key1220 = 0;
                for (; __key1220 < __r1219.getLength();__key1220 += 1) {
                    TemplateParameter tp = __r1219.get(__key1220);
                    if (pequals(e.ident, tp.ident))
                    {
                        this.result = true;
                        return ;
                    }
                }
            }
        }

        public  void visit(TupleExp e) {
            if (e.exps != null)
            {
                {
                    Slice<Expression> __r1221 = (e.exps).opSlice().copy();
                    int __key1222 = 0;
                    for (; __key1222 < __r1221.getLength();__key1222 += 1) {
                        Expression ea = __r1221.get(__key1222);
                        ea.accept(this);
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(ArrayLiteralExp e) {
            if (e.elements != null)
            {
                {
                    Slice<Expression> __r1223 = (e.elements).opSlice().copy();
                    int __key1224 = 0;
                    for (; __key1224 < __r1223.getLength();__key1224 += 1) {
                        Expression el = __r1223.get(__key1224);
                        el.accept(this);
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(AssocArrayLiteralExp e) {
            {
                Slice<Expression> __r1225 = (e.keys).opSlice().copy();
                int __key1226 = 0;
                for (; __key1226 < __r1225.getLength();__key1226 += 1) {
                    Expression ek = __r1225.get(__key1226);
                    ek.accept(this);
                    if (this.result)
                        return ;
                }
            }
            {
                Slice<Expression> __r1227 = (e.values).opSlice().copy();
                int __key1228 = 0;
                for (; __key1228 < __r1227.getLength();__key1228 += 1) {
                    Expression ev = __r1227.get(__key1228);
                    ev.accept(this);
                    if (this.result)
                        return ;
                }
            }
        }

        public  void visit(StructLiteralExp e) {
            if (e.elements != null)
            {
                {
                    Slice<Expression> __r1229 = (e.elements).opSlice().copy();
                    int __key1230 = 0;
                    for (; __key1230 < __r1229.getLength();__key1230 += 1) {
                        Expression ea = __r1229.get(__key1230);
                        ea.accept(this);
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(TypeExp e) {
            this.result = reliesOnTemplateParameters(e.type, this.tparams);
        }

        public  void visit(NewExp e) {
            if (e.thisexp != null)
                e.thisexp.accept(this);
            if ((!(this.result) && e.newargs != null))
            {
                {
                    Slice<Expression> __r1231 = (e.newargs).opSlice().copy();
                    int __key1232 = 0;
                    for (; __key1232 < __r1231.getLength();__key1232 += 1) {
                        Expression ea = __r1231.get(__key1232);
                        ea.accept(this);
                        if (this.result)
                            return ;
                    }
                }
            }
            this.result = reliesOnTemplateParameters(e.newtype, this.tparams);
            if ((!(this.result) && e.arguments != null))
            {
                {
                    Slice<Expression> __r1233 = (e.arguments).opSlice().copy();
                    int __key1234 = 0;
                    for (; __key1234 < __r1233.getLength();__key1234 += 1) {
                        Expression ea = __r1233.get(__key1234);
                        ea.accept(this);
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(NewAnonClassExp e) {
            this.result = true;
        }

        public  void visit(FuncExp e) {
            this.result = true;
        }

        public  void visit(TypeidExp e) {
            {
                Expression ea = isExpression(e.obj);
                if (ea != null)
                    ea.accept(this);
                else {
                    Type ta = isType(e.obj);
                    if (ta != null)
                        this.result = reliesOnTemplateParameters(ta, this.tparams);
                }
            }
        }

        public  void visit(TraitsExp e) {
            if (e.args != null)
            {
                {
                    Slice<RootObject> __r1235 = (e.args).opSlice().copy();
                    int __key1236 = 0;
                    for (; __key1236 < __r1235.getLength();__key1236 += 1) {
                        RootObject oa = __r1235.get(__key1236);
                        {
                            Expression ea = isExpression(oa);
                            if (ea != null)
                                ea.accept(this);
                            else {
                                Type ta = isType(oa);
                                if (ta != null)
                                    this.result = reliesOnTemplateParameters(ta, this.tparams);
                            }
                        }
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(IsExp e) {
            this.result = reliesOnTemplateParameters(e.targ, this.tparams);
        }

        public  void visit(UnaExp e) {
            e.e1.accept(this);
        }

        public  void visit(DotTemplateInstanceExp e) {
            this.visit((UnaExp)e);
            if ((!(this.result) && e.ti.tiargs != null))
            {
                {
                    Slice<RootObject> __r1237 = (e.ti.tiargs).opSlice().copy();
                    int __key1238 = 0;
                    for (; __key1238 < __r1237.getLength();__key1238 += 1) {
                        RootObject oa = __r1237.get(__key1238);
                        {
                            Expression ea = isExpression(oa);
                            if (ea != null)
                                ea.accept(this);
                            else {
                                Type ta = isType(oa);
                                if (ta != null)
                                    this.result = reliesOnTemplateParameters(ta, this.tparams);
                            }
                        }
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(CallExp e) {
            this.visit((UnaExp)e);
            if ((!(this.result) && e.arguments != null))
            {
                {
                    Slice<Expression> __r1239 = (e.arguments).opSlice().copy();
                    int __key1240 = 0;
                    for (; __key1240 < __r1239.getLength();__key1240 += 1) {
                        Expression ea = __r1239.get(__key1240);
                        ea.accept(this);
                        if (this.result)
                            return ;
                    }
                }
            }
        }

        public  void visit(CastExp e) {
            this.visit((UnaExp)e);
            if ((!(this.result) && e.to != null))
                this.result = reliesOnTemplateParameters(e.to, this.tparams);
        }

        public  void visit(SliceExp e) {
            this.visit((UnaExp)e);
            if ((!(this.result) && e.lwr != null))
                e.lwr.accept(this);
            if ((!(this.result) && e.upr != null))
                e.upr.accept(this);
        }

        public  void visit(IntervalExp e) {
            e.lwr.accept(this);
            if (!(this.result))
                e.upr.accept(this);
        }

        public  void visit(ArrayExp e) {
            this.visit((UnaExp)e);
            if ((!(this.result) && e.arguments != null))
            {
                {
                    Slice<Expression> __r1241 = (e.arguments).opSlice().copy();
                    int __key1242 = 0;
                    for (; __key1242 < __r1241.getLength();__key1242 += 1) {
                        Expression ea = __r1241.get(__key1242);
                        ea.accept(this);
                    }
                }
            }
        }

        public  void visit(BinExp e) {
            e.e1.accept(this);
            if (!(this.result))
                e.e2.accept(this);
        }

        public  void visit(CondExp e) {
            e.econd.accept(this);
            if (!(this.result))
                this.visit((BinExp)e);
        }

        private Object this;

        public ReliesOnTemplateParameters() {}

        public ReliesOnTemplateParameters copy() {
            ReliesOnTemplateParameters that = new ReliesOnTemplateParameters();
            that.tparams = this.tparams;
            that.result = this.result;
            that.this = this.this;
            return that;
        }
    }
    static int tryExpandMembersnest;
    static int trySemantic3nest;

    static boolean LOG = false;
    static int IDX_NOTFOUND = 305419896;
    public static Expression isExpression(RootObject o) {
        if ((!(o != null) || o.dyncast() != DYNCAST.expression))
            return null;
        return (Expression)o;
    }

    public static Dsymbol isDsymbol(RootObject o) {
        if ((!(o != null) || o.dyncast() != DYNCAST.dsymbol))
            return null;
        return (Dsymbol)o;
    }

    public static Type isType(RootObject o) {
        if ((!(o != null) || o.dyncast() != DYNCAST.type))
            return null;
        return (Type)o;
    }

    public static Tuple isTuple(RootObject o) {
        if ((!(o != null) || o.dyncast() != DYNCAST.tuple))
            return null;
        return (Tuple)o;
    }

    public static Parameter isParameter(RootObject o) {
        if ((!(o != null) || o.dyncast() != DYNCAST.parameter))
            return null;
        return (Parameter)o;
    }

    public static TemplateParameter isTemplateParameter(RootObject o) {
        if ((!(o != null) || o.dyncast() != DYNCAST.templateparameter))
            return null;
        return (TemplateParameter)o;
    }

    public static boolean isError(RootObject o) {
        {
            Type t = isType(o);
            if (t != null)
                return (t.ty & 0xFF) == ENUMTY.Terror;
        }
        {
            Expression e = isExpression(o);
            if (e != null)
                return (((e.op & 0xFF) == 127 || !(e.type != null)) || (e.type.ty & 0xFF) == ENUMTY.Terror);
        }
        {
            Tuple v = isTuple(o);
            if (v != null)
                return arrayObjectIsError(v.objects);
        }
        Dsymbol s = isDsymbol(o);
        assert(s != null);
        if (s.errors)
            return true;
        return s.parent != null ? isError(s.parent) : false;
    }

    public static boolean arrayObjectIsError(DArray<RootObject> args) {
        {
            Slice<RootObject> __r1186 = (args).opSlice().copy();
            int __key1187 = 0;
            for (; __key1187 < __r1186.getLength();__key1187 += 1) {
                RootObject o = __r1186.get(__key1187);
                if (isError(o))
                    return true;
            }
        }
        return false;
    }

    public static Type getType(RootObject o) {
        Type t = isType(o);
        if (!(t != null))
        {
            {
                Expression e = isExpression(o);
                if (e != null)
                    return e.type;
            }
        }
        return t;
    }

    public static Dsymbol getDsymbol(RootObject oarg) {
        {
            Expression ea = isExpression(oarg);
            if (ea != null)
            {
                {
                    VarExp ve = ea.isVarExp();
                    if (ve != null)
                        return ve.var;
                    else {
                        FuncExp fe = ea.isFuncExp();
                        if (fe != null)
                            return fe.td != null ? fe.td : fe.fd;
                        else {
                            TemplateExp te = ea.isTemplateExp();
                            if (te != null)
                                return te.td;
                            else
                                return null;
                        }
                    }
                }
            }
            else
            {
                {
                    Type ta = isType(oarg);
                    if (ta != null)
                        return ta.toDsymbol(null);
                    else
                        return isDsymbol(oarg);
                }
            }
        }
    }

    public static Expression getValue(Ref<Dsymbol> s) {
        if (s.value != null)
        {
            {
                VarDeclaration v = s.value.isVarDeclaration();
                if (v != null)
                {
                    if ((v.storage_class & 8388608L) != 0)
                        return v.getConstInitializer(true);
                }
            }
        }
        return null;
    }

    public static Expression getValue(Expression e) {
        if ((e != null && (e.op & 0xFF) == 26))
        {
            VarDeclaration v = ((VarExp)e).var.isVarDeclaration();
            if ((v != null && (v.storage_class & 8388608L) != 0))
            {
                e = v.getConstInitializer(true);
            }
        }
        return e;
    }

    public static Expression getExpression(RootObject o) {
        Ref<Dsymbol> s = ref(isDsymbol(o));
        return s.value != null ? getValue(s) : getValue(isExpression(o));
    }

    public static boolean match(RootObject o1, RootObject o2) {
        boolean log = false;
        try {
            try {
                {
                    Type t1 = isType(o1);
                    if (t1 != null)
                    {
                        Type t2 = isType(o2);
                        if (!(t2 != null))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        if (!(t1.equals(t2)))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Expression e1 = getExpression(o1);
                    if (e1 != null)
                    {
                        Expression e2 = getExpression(o2);
                        if (!(e2 != null))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        if ((!(e1.type.equals(e2.type)) || !(e1.equals(e2))))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Dsymbol s1 = isDsymbol(o1);
                    if (s1 != null)
                    {
                        Dsymbol s2 = isDsymbol(o2);
                        if (!(s2 != null))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        if (!(s1.equals(s2)))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        if (((!pequals(s1.parent, s2.parent) && !(s1.isFuncDeclaration() != null)) && !(s2.isFuncDeclaration() != null)))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
                {
                    Tuple u1 = isTuple(o1);
                    if (u1 != null)
                    {
                        Tuple u2 = isTuple(o2);
                        if (!(u2 != null))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        if (!(arrayObjectMatch(u1.objects, u2.objects)))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        /*goto Lmatch*/throw Dispatch0.INSTANCE;
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Lmatch:*/
            return true;
        }
        catch(Dispatch1 __d){}
    /*Lnomatch:*/
        return false;
    }

    public static boolean arrayObjectMatch(DArray<RootObject> oa1, DArray<RootObject> oa2) {
        if (oa1 == oa2)
            return true;
        if ((oa1).length != (oa2).length)
            return false;
        int oa1dim = (oa1).length;
        Ptr<RootObject> oa1d = pcopy((oa1).data);
        Ptr<RootObject> oa2d = pcopy((oa2).data);
        {
            int __key1188 = 0;
            int __limit1189 = oa1dim;
            for (; __key1188 < __limit1189;__key1188 += 1) {
                int j = __key1188;
                RootObject o1 = oa1d.get(j);
                RootObject o2 = oa2d.get(j);
                if (!(match(o1, o2)))
                {
                    return false;
                }
            }
        }
        return true;
    }

    public static int arrayObjectHash(DArray<RootObject> oa1) {
        int hash = 0;
        {
            Slice<RootObject> __r1190 = (oa1).opSlice().copy();
            int __key1191 = 0;
            for (; __key1191 < __r1190.getLength();__key1191 += 1) {
                RootObject o1 = __r1190.get(__key1191);
                {
                    Type t1 = isType(o1);
                    if (t1 != null)
                        hash = mixHash(hash, (int)t1.deco);
                    else {
                        Expression e1 = getExpression(o1);
                        if (e1 != null)
                            hash = mixHash(hash, expressionHash(e1));
                        else {
                            Dsymbol s1 = isDsymbol(o1);
                            if (s1 != null)
                            {
                                FuncAliasDeclaration fa1 = s1.isFuncAliasDeclaration();
                                if (fa1 != null)
                                    s1 = fa1.toAliasFunc();
                                hash = mixHash(hash, mixHash((int)s1.getIdent(), (int)s1.parent));
                            }
                            else {
                                Tuple u1 = isTuple(o1);
                                if (u1 != null)
                                    hash = mixHash(hash, arrayObjectHash(u1.objects));
                            }
                        }
                    }
                }
            }
        }
        return hash;
    }

    public static int expressionHash(Expression e) {
        switch ((e.op & 0xFF))
        {
            case 135:
                return (int)((IntegerExp)e).getInteger();
            case 140:
                return CTFloat.hash(((RealExp)e).value);
            case 147:
                ComplexExp ce = (ComplexExp)e;
                return mixHash(CTFloat.hash(ce.toReal()), CTFloat.hash(ce.toImaginary()));
            case 120:
                return (int)((IdentifierExp)e).ident;
            case 13:
                return (int)((NullExp)e).type;
            case 121:
                StringExp se = (StringExp)e;
                return calcHash(se.string, se.len * (se.sz & 0xFF));
            case 126:
                TupleExp te = (TupleExp)e;
                int hash = 0;
                hash += te.e0 != null ? expressionHash(te.e0) : 0;
                {
                    Slice<Expression> __r1192 = (te.exps).opSlice().copy();
                    int __key1193 = 0;
                    for (; __key1193 < __r1192.getLength();__key1193 += 1) {
                        Expression elem = __r1192.get(__key1193);
                        hash = mixHash(hash, expressionHash(elem));
                    }
                }
                return hash;
            case 47:
                ArrayLiteralExp ae = (ArrayLiteralExp)e;
                int hash_1 = 0;
                {
                    int __key1194 = 0;
                    int __limit1195 = (ae.elements).length;
                    for (; __key1194 < __limit1195;__key1194 += 1) {
                        int i = __key1194;
                        hash_1 = mixHash(hash_1, expressionHash(ae.getElement(i)));
                    }
                }
                return hash_1;
            case 48:
                AssocArrayLiteralExp ae_1 = (AssocArrayLiteralExp)e;
                int hash_2 = 0;
                {
                    int __key1196 = 0;
                    int __limit1197 = (ae_1.keys).length;
                    for (; __key1196 < __limit1197;__key1196 += 1) {
                        int i_1 = __key1196;
                        hash_2 ^= mixHash(expressionHash((ae_1.keys).get(i_1)), expressionHash((ae_1.values).get(i_1)));
                    }
                }
                return hash_2;
            case 49:
                StructLiteralExp se_1 = (StructLiteralExp)e;
                int hash_3 = 0;
                {
                    Slice<Expression> __r1198 = (se_1.elements).opSlice().copy();
                    int __key1199 = 0;
                    for (; __key1199 < __r1198.getLength();__key1199 += 1) {
                        Expression elem_1 = __r1198.get(__key1199);
                        hash_3 = mixHash(hash_3, elem_1 != null ? expressionHash(elem_1) : 0);
                    }
                }
                return hash_3;
            case 26:
                return (int)((VarExp)e).var;
            case 161:
                return (int)((FuncExp)e).fd;
            default:
            assert((e.equals).funcptr == equals);
            return (int)e;
        }
    }

    public static RootObject objectSyntaxCopy(RootObject o) {
        if (!(o != null))
            return null;
        {
            Type t = isType(o);
            if (t != null)
                return t.syntaxCopy();
        }
        {
            Expression e = isExpression(o);
            if (e != null)
                return e.syntaxCopy();
        }
        return o;
    }

    public static class Tuple extends RootObject
    {
        public DArray<RootObject> objects = new DArray<RootObject>();
        public  Tuple() {
            super();
        }

        public  Tuple(int numObjects) {
            super();
            this.objects.setDim(numObjects);
        }

        public  int dyncast() {
            return DYNCAST.tuple;
        }

        public  BytePtr toChars() {
            return this.objects.toChars();
        }


        public Tuple copy() {
            Tuple that = new Tuple();
            that.objects = this.objects;
            return that;
        }
    }
    public static class TemplatePrevious
    {
        public TemplatePrevious prev;
        public Scope sc;
        public DArray<RootObject> dedargs;
        public TemplatePrevious(){
        }
        public TemplatePrevious copy(){
            TemplatePrevious r = new TemplatePrevious();
            r.prev = prev;
            r.sc = sc;
            r.dedargs = dedargs;
            return r;
        }
        public TemplatePrevious(TemplatePrevious prev, Scope sc, DArray<RootObject> dedargs) {
            this.prev = prev;
            this.sc = sc;
            this.dedargs = dedargs;
        }

        public TemplatePrevious opAssign(TemplatePrevious that) {
            this.prev = that.prev;
            this.sc = that.sc;
            this.dedargs = that.dedargs;
            return this;
        }
    }
    public static class TemplateDeclaration extends ScopeDsymbol
    {
        public DArray<TemplateParameter> parameters;
        public DArray<TemplateParameter> origParameters;
        public Expression constraint;
        public AA<TemplateInstanceBox,TemplateInstance> instances = new AA<TemplateInstanceBox,TemplateInstance>();
        public TemplateDeclaration overnext;
        public TemplateDeclaration overroot;
        public FuncDeclaration funcroot;
        public Dsymbol onemember;
        public boolean literal;
        public boolean ismixin;
        public boolean isstatic;
        public Prot protection = new Prot();
        public int inuse;
        public TemplatePrevious previous;
        public  TemplateDeclaration(Loc loc, Identifier ident, DArray<TemplateParameter> parameters, Expression constraint, DArray<Dsymbol> decldefs, boolean ismixin, boolean literal) {
            super(loc, ident);
            this.parameters = parameters;
            this.origParameters = parameters;
            this.constraint = constraint;
            this.members = decldefs;
            this.literal = literal;
            this.ismixin = ismixin;
            this.isstatic = true;
            this.protection = new Prot(Prot.Kind.undefined);
            if ((this.members != null && ident != null))
            {
                Ref<Dsymbol> s = ref(null);
                if ((Dsymbol.oneMembers(this.members, ptr(s), ident) && s.value != null))
                {
                    this.onemember = s.value;
                    s.value.parent = this;
                }
            }
        }

        public  Dsymbol syntaxCopy(Dsymbol _param_0) {
            DArray<TemplateParameter> p = null;
            if (this.parameters != null)
            {
                p = new DArray<TemplateParameter>((this.parameters).length);
                {
                    int i = 0;
                    for (; i < (p).length;i++) {
                        p.set(i, (this.parameters).get(i).syntaxCopy());
                    }
                }
            }
            return new TemplateDeclaration(this.loc, this.ident, p, this.constraint != null ? this.constraint.syntaxCopy() : null, Dsymbol.arraySyntaxCopy(this.members), this.ismixin, this.literal);
        }

        public  boolean overloadInsert(Dsymbol s) {
            FuncDeclaration fd = s.isFuncDeclaration();
            if (fd != null)
            {
                if (this.funcroot != null)
                    return this.funcroot.overloadInsert(fd);
                this.funcroot = fd;
                return this.funcroot.overloadInsert(this);
            }
            TemplateDeclaration td = s.isTemplateDeclaration();
            if (!(td != null))
                return false;
            Ref<TemplateDeclaration> pthis = ref(this);
            Ptr<TemplateDeclaration> ptd = null;
            {
                ptd = pcopy(ptr(pthis));
                for (; ptd.get() != null;ptd = pcopy(((ptd.get()).overnext))){
                }
            }
            td.overroot = this;
            ptd.set(0, td);
            return true;
        }

        public  boolean hasStaticCtorOrDtor() {
            return false;
        }

        public  BytePtr kind() {
            return (this.onemember != null && this.onemember.isAggregateDeclaration() != null) ? this.onemember.kind() : new BytePtr("template");
        }

        public  BytePtr toChars() {
            if (this.literal)
                return this.toChars();
            OutBuffer buf = new OutBuffer();
            try {
                HdrGenState hgs = new HdrGenState();
                buf.writestring(this.ident.asString());
                buf.writeByte(40);
                {
                    int i = 0;
                    for (; i < (this.parameters).length;i++){
                        TemplateParameter tp = (this.parameters).get(i);
                        if ((i) != 0)
                            buf.writestring(new ByteSlice(", "));
                        toCBuffer(tp, buf, hgs);
                    }
                }
                buf.writeByte(41);
                if (this.onemember != null)
                {
                    FuncDeclaration fd = this.onemember.isFuncDeclaration();
                    if ((fd != null && fd.type != null))
                    {
                        TypeFunction tf = (TypeFunction)fd.type;
                        buf.writestring(parametersTypeToChars(tf.parameterList));
                    }
                }
                if (this.constraint != null)
                {
                    buf.writestring(new ByteSlice(" if ("));
                    toCBuffer(this.constraint, buf, hgs);
                    buf.writeByte(41);
                }
                return buf.extractChars();
            }
            finally {
            }
        }

        public  Prot prot() {
            return this.protection;
        }

        public  boolean evaluateConstraint(TemplateInstance ti, Scope sc, Scope paramscope, DArray<RootObject> dedargs, FuncDeclaration fd) {
            {
                TemplatePrevious p = this.previous;
                for (; p != null;p = (p).prev){
                    if (arrayObjectMatch((p).dedargs, dedargs))
                    {
                        {
                            Scope scx = sc;
                            for (; scx != null;scx = (scx).enclosing){
                                if (scx == (p).sc)
                                    return false;
                            }
                        }
                    }
                }
            }
            TemplatePrevious pr = new TemplatePrevious();
            pr.prev = this.previous;
            pr.sc = paramscope;
            pr.dedargs = dedargs;
            this.previous = pr;
            Scope scx = (paramscope).push(ti);
            (scx).parent = ti;
            (scx).tinst = null;
            (scx).minst = null;
            assert(!(ti.symtab != null));
            if (fd != null)
            {
                TypeFunction tf = (TypeFunction)fd.type;
                assert((tf.ty & 0xFF) == ENUMTY.Tfunction);
                (scx).parent = fd;
                DArray<Parameter> fparameters = tf.parameterList.parameters;
                int nfparams = tf.parameterList.length();
                {
                    int i = 0;
                    for (; i < nfparams;i++){
                        Parameter fparam = tf.parameterList.get(i);
                        fparam.storageClass &= 2704291852L;
                        fparam.storageClass |= 32L;
                        if ((tf.parameterList.varargs == VarArg.typesafe && i + 1 == nfparams))
                        {
                            fparam.storageClass |= 65536L;
                        }
                    }
                }
                {
                    int i = 0;
                    for (; i < (fparameters).length;i++){
                        Parameter fparam = (fparameters).get(i);
                        if (!(fparam.ident != null))
                            continue;
                        VarDeclaration v = new VarDeclaration(this.loc, fparam.type, fparam.ident, null, 0L);
                        v.storage_class = fparam.storageClass;
                        dsymbolSemantic(v, scx);
                        if (!(ti.symtab != null))
                            ti.symtab = new DsymbolTable();
                        if (!((scx).insert(v) != null))
                            this.error(new BytePtr("parameter `%s.%s` is already defined"), this.toChars(), v.toChars());
                        else
                            v.parent = fd;
                    }
                }
                if (this.isstatic)
                    fd.storage_class |= 1L;
                FuncDeclaration.HiddenParameters hiddenParams = fd.declareThis(scx, fd.isThis()).copy();
                fd.vthis = hiddenParams.vthis;
                fd.isThis2 = hiddenParams.isThis2;
                fd.selectorParameter = hiddenParams.selectorParameter;
            }
            Expression e = this.constraint.syntaxCopy();
            assert(ti.inst == null);
            ti.inst = ti;
            (scx).flags |= 16;
            Ref<Boolean> errors = ref(false);
            boolean result = evalStaticCondition(scx, this.constraint, e, errors);
            ti.inst = null;
            ti.symtab = null;
            scx = (scx).pop();
            this.previous = pr.prev;
            if (errors.value)
                return false;
            return result;
        }

        public  Scope scopeForTemplateParameters(TemplateInstance ti, Scope sc) {
            ScopeDsymbol paramsym = new ScopeDsymbol();
            paramsym.parent = (this._scope).parent;
            Scope paramscope = (this._scope).push(paramsym);
            (paramscope).tinst = ti;
            (paramscope).minst = (sc).minst;
            (paramscope).callsc = sc;
            (paramscope).stc = 0L;
            return paramscope;
        }

        public  int matchWithInstance(Scope sc, TemplateInstance ti, DArray<RootObject> dedtypes, DArray<Expression> fargs, int flag) {
            int LOGM = 0;
            int m = MATCH.nomatch;
            int dedtypes_dim = (dedtypes).length;
            (dedtypes).zero();
            if (this.errors)
                return MATCH.nomatch;
            int parameters_dim = (this.parameters).length;
            int variadic = ((this.isVariadic() != null) ? 1 : 0);
            if (((ti.tiargs).length > parameters_dim && !((variadic) != 0)))
            {
                return MATCH.nomatch;
            }
            assert(dedtypes_dim == parameters_dim);
            assert((dedtypes_dim >= (ti.tiargs).length || (variadic) != 0));
            assert(this._scope != null);
            Scope paramscope = this.scopeForTemplateParameters(ti, sc);
            m = MATCH.exact;
            try {
                {
                    int i = 0;
                L_outer1:
                    for (; i < dedtypes_dim;i++){
                        int m2 = MATCH.nomatch;
                        TemplateParameter tp = (this.parameters).get(i);
                        Ref<Declaration> sparam = ref(null);
                        this.inuse++;
                        m2 = tp.matchArg(ti.loc, paramscope, ti.tiargs, i, this.parameters, dedtypes, ptr(sparam));
                        this.inuse--;
                        if (m2 == MATCH.nomatch)
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        if (m2 < m)
                            m = m2;
                        if (!((flag) != 0))
                            dsymbolSemantic(sparam.value, paramscope);
                        if (!((paramscope).insert(sparam.value) != null))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                }
                if (!((flag) != 0))
                {
                    {
                        int i = 0;
                        for (; i < dedtypes_dim;i++){
                            if (!((dedtypes).get(i) != null))
                            {
                                assert(i < (ti.tiargs).length);
                                dedtypes.set(i, (Type)(ti.tiargs).get(i));
                            }
                        }
                    }
                }
                if (((m > MATCH.nomatch && this.constraint != null) && !((flag) != 0)))
                {
                    if (ti.hasNestedArgs(ti.tiargs, this.isstatic))
                        ti.parent = ti.enclosing;
                    else
                        ti.parent = this.parent;
                    FuncDeclaration fd = this.onemember != null ? this.onemember.isFuncDeclaration() : null;
                    if (fd != null)
                    {
                        assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                        TypeFunction tf = (TypeFunction)fd.type.syntaxCopy();
                        fd = new FuncDeclaration(fd.loc, fd.endloc, fd.ident, fd.storage_class, tf);
                        fd.parent = ti;
                        fd.inferRetType = true;
                        {
                            int i = 0;
                            for (; i < (tf.parameterList.parameters).length;i++) {
                                (tf.parameterList.parameters).get(i).defaultArg = null;
                            }
                        }
                        tf.next = null;
                        tf.incomplete = true;
                        tf.fargs = fargs;
                        int olderrors = global.startGagging();
                        fd.type = typeSemantic(tf, this.loc, paramscope);
                        if (global.endGagging(olderrors))
                        {
                            assert((fd.type.ty & 0xFF) != ENUMTY.Tfunction);
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
                        fd.originalType = fd.type;
                    }
                    if (!(this.evaluateConstraint(ti, sc, paramscope, dedtypes, fd)))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                }
                try {
                    /*goto Lret*/throw Dispatch1.INSTANCE;
                }
                catch(Dispatch0 __d){}
            /*Lnomatch:*/
                m = MATCH.nomatch;
            }
            catch(Dispatch1 __d){}
        /*Lret:*/
            (paramscope).pop();
            return m;
        }

        public  int leastAsSpecialized(Scope sc, TemplateDeclaration td2, DArray<Expression> fargs) {
            int LOG_LEASTAS = 0;
            DArray<RootObject> tiargs = new DArray<RootObject>();
            (tiargs).reserve((this.parameters).length);
            {
                int i = 0;
                for (; i < (this.parameters).length;i++){
                    TemplateParameter tp = (this.parameters).get(i);
                    if (tp.dependent)
                        break;
                    RootObject p = ;
                    if (!(p != null))
                        break;
                    (tiargs).push(p);
                }
            }
            TemplateInstance ti = new TemplateInstance(Loc.initial, this.ident, tiargs);
            DArray<RootObject> dedtypes = dedtypes = new DArray<RootObject>((td2.parameters).length);
            try {
                int m = td2.matchWithInstance(sc, ti, dedtypes, fargs, 1);
                try {
                    if (m > MATCH.nomatch)
                    {
                        TemplateTupleParameter tp = this.isVariadic();
                        if (((tp != null && !(tp.dependent)) && !(td2.isVariadic() != null)))
                            /*goto L1*/throw Dispatch0.INSTANCE;
                        return m;
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                return MATCH.nomatch;
            }
            finally {
            }
        }

        public  int deduceFunctionTemplateMatch(TemplateInstance ti, Scope sc, Ref<FuncDeclaration> fd, Type tthis, DArray<Expression> fargs) {
            int nfparams = 0;
            int nfargs = 0;
            int ntargs = 0;
            int fptupindex = 305419896;
            int match = MATCH.exact;
            int matchTiargs = MATCH.exact;
            ParameterList fparameters = new ParameterList();
            int fvarargs = VarArg.none;
            int wildmatch = 0;
            int inferStart = 0;
            Loc instLoc = ti.loc.copy();
            DArray<RootObject> tiargs = ti.tiargs;
            DArray<RootObject> dedargs = new DArray<RootObject>();
            DArray<RootObject> dedtypes = ti.tdtypes;
            assert(this._scope != null);
            (dedargs).setDim((this.parameters).length);
            (dedargs).zero();
            (dedtypes).setDim((this.parameters).length);
            (dedtypes).zero();
            if ((this.errors || fd.value.errors))
                return MATCH.nomatch;
            Scope paramscope = this.scopeForTemplateParameters(ti, sc);
            TemplateTupleParameter tp = this.isVariadic();
            Tuple declaredTuple = null;
            ntargs = 0;
            try {
                if (tiargs != null)
                {
                    ntargs = (tiargs).length;
                    int n = (this.parameters).length;
                    if (tp != null)
                        n--;
                    if (ntargs > n)
                    {
                        if (!(tp != null))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        Tuple t = new Tuple(ntargs - n);
                        assert(((this.parameters).length) != 0);
                        dedargs.set((this.parameters).length - 1, t);
                        {
                            int i = 0;
                            for (; i < t.objects.length;i++){
                                t.objects.set(i, (tiargs).get(n + i));
                            }
                        }
                        this.declareParameter(paramscope, tp, t);
                        declaredTuple = t;
                    }
                    else
                        n = ntargs;
                    memcpy((BytePtr)((dedargs).tdata()), ((tiargs).tdata()), (n * 4));
                    {
                        int i = 0;
                    L_outer2:
                        for (; i < n;i++){
                            assert(i < (this.parameters).length);
                            Ref<Declaration> sparam = ref(null);
                            int m = (this.parameters).get(i).matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, ptr(sparam));
                            if (m <= MATCH.nomatch)
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                            if (m < matchTiargs)
                                matchTiargs = m;
                            dsymbolSemantic(sparam.value, paramscope);
                            if (!((paramscope).insert(sparam.value) != null))
                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        }
                    }
                    if ((n < (this.parameters).length && !(declaredTuple != null)))
                    {
                        inferStart = n;
                    }
                    else
                        inferStart = (this.parameters).length;
                }
                fparameters = fd.value.getParameterList().copy();
                nfparams = fparameters.length();
                nfargs = fargs != null ? (fargs).length : 0;
                if (tp != null)
                {
                    matchTiargs = MATCH.convert;
                    if ((nfparams == 0 && nfargs != 0))
                    {
                        if (!(declaredTuple != null))
                        {
                            Tuple t = new Tuple();
                            dedargs.set((this.parameters).length - 1, t);
                            this.declareParameter(paramscope, tp, t);
                            declaredTuple = t;
                        }
                    }
                    else
                    {
                        try {
                            {
                                fptupindex = 0;
                            L_outer3:
                                for (; fptupindex < nfparams;fptupindex++){
                                    Parameter fparam = (fparameters.parameters).get(fptupindex);
                                    if ((fparam.type.ty & 0xFF) != ENUMTY.Tident)
                                        continue L_outer3;
                                    TypeIdentifier tid = (TypeIdentifier)fparam.type;
                                    if ((!(tp.ident.equals(tid.ident)) || (tid.idents.length) != 0))
                                        continue L_outer3;
                                    if (fparameters.varargs != VarArg.none)
                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                            }
                            fptupindex = 305419896;
                        }
                        catch(Dispatch0 __d){}
                    /*L1:*/
                    }
                }
                if ((this.toParent().isModule() != null || ((this._scope).stc & 1L) != 0))
                    tthis = null;
                if (tthis != null)
                {
                    boolean hasttp = false;
                    {
                        int i = 0;
                    L_outer4:
                        for (; i < (this.parameters).length;i++){
                            TemplateThisParameter ttp = (this.parameters).get(i).isTemplateThisParameter();
                            if (ttp != null)
                            {
                                hasttp = true;
                                Type t = new TypeIdentifier(Loc.initial, ttp.ident);
                                int m = deduceType(tthis, paramscope, t, this.parameters, dedtypes, null, 0, false);
                                if (m <= MATCH.nomatch)
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                if (m < match)
                                    match = m;
                            }
                        }
                    }
                    if ((fd.value.type != null && !(fd.value.isCtorDeclaration() != null)))
                    {
                        long stc = (this._scope).stc | fd.value.storage_class2;
                        Dsymbol p = this.parent;
                        for (; (p.isTemplateDeclaration() != null || p.isTemplateInstance() != null);) {
                            p = p.parent;
                        }
                        AggregateDeclaration ad = p.isAggregateDeclaration();
                        if (ad != null)
                            stc |= ad.storage_class;
                        byte mod = fd.value.type.mod;
                        if ((stc & 1048576L) != 0)
                            mod = (byte)4;
                        else
                        {
                            if ((stc & 536871424L) != 0)
                                mod |= MODFlags.shared_;
                            if ((stc & 4L) != 0)
                                mod |= MODFlags.const_;
                            if ((stc & 2147483648L) != 0)
                                mod |= MODFlags.wild;
                        }
                        byte thismod = tthis.mod;
                        if (hasttp)
                            mod = MODmerge(thismod, mod);
                        int m = MODmethodConv(thismod, mod);
                        if (m <= MATCH.nomatch)
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                        if (m < match)
                            match = m;
                    }
                }
                try {
                    {
                        int argi = 0;
                        int nfargs2 = nfargs;
                        {
                            int parami = 0;
                        L_outer5:
                            for (; parami < nfparams;parami++){
                                Parameter fparam = fparameters.get(parami);
                                Type prmtype = fparam.type.addStorageClass(fparam.storageClass);
                                Expression farg = null;
                                if ((fptupindex != 305419896 && parami == fptupindex))
                                {
                                    assert((prmtype.ty & 0xFF) == ENUMTY.Tident);
                                    TypeIdentifier tid = (TypeIdentifier)prmtype;
                                    if (!(declaredTuple != null))
                                    {
                                        declaredTuple = new Tuple();
                                        dedargs.set((this.parameters).length - 1, declaredTuple);
                                        int rem = 0;
                                        {
                                            int j = parami + 1;
                                            for (; j < nfparams;j++){
                                                Parameter p = fparameters.get(j);
                                                if (p.defaultArg != null)
                                                {
                                                    break;
                                                }
                                                if (!(reliesOnTemplateParameters(p.type, (this.parameters).opSlice(inferStart, (this.parameters).length))))
                                                {
                                                    Type pt = typeSemantic(p.type.syntaxCopy(), fd.value.loc, paramscope);
                                                    rem += (pt.ty & 0xFF) == ENUMTY.Ttuple ? (((TypeTuple)pt).arguments).length : 1;
                                                }
                                                else
                                                {
                                                    rem += 1;
                                                }
                                            }
                                        }
                                        if (nfargs2 - argi < rem)
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        declaredTuple.objects.setDim(nfargs2 - argi - rem);
                                        {
                                            int i = 0;
                                        L_outer6:
                                            for (; i < declaredTuple.objects.length;i++){
                                                farg = (fargs).get(argi + i);
                                                if (((farg.op & 0xFF) == 127 || (farg.type.ty & 0xFF) == ENUMTY.Terror))
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                if ((!((fparam.storageClass & 8192L) != 0) && (farg.type.ty & 0xFF) == ENUMTY.Tvoid))
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                Ref<Type> tt = ref(null);
                                                int m = MATCH.nomatch;
                                                {
                                                    byte wm = deduceWildHelper(farg.type, ptr(tt), tid);
                                                    if ((wm) != 0)
                                                    {
                                                        wildmatch |= (wm & 0xFF);
                                                        m = MATCH.constant;
                                                    }
                                                    else
                                                    {
                                                        m = deduceTypeHelper(farg.type, ptr(tt), tid);
                                                    }
                                                }
                                                if (m <= MATCH.nomatch)
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                if (m < match)
                                                    match = m;
                                                if (((((tt.value.ty & 0xFF) == ENUMTY.Tarray || (tt.value.ty & 0xFF) == ENUMTY.Tpointer) && !(tt.value.isMutable())) && (!((fparam.storageClass & 2097152L) != 0) || ((fparam.storageClass & 256L) != 0 && !(farg.isLvalue())))))
                                                {
                                                    tt.value = tt.value.mutableOf();
                                                }
                                                declaredTuple.objects.set(i, tt.value);
                                            }
                                        }
                                        this.declareParameter(paramscope, tp, declaredTuple);
                                    }
                                    else
                                    {
                                        {
                                            int i = 0;
                                        L_outer7:
                                            for (; i < declaredTuple.objects.length;i++){
                                                if (!(isType(declaredTuple.objects.get(i)) != null))
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                        }
                                    }
                                    assert(declaredTuple != null);
                                    argi += declaredTuple.objects.length;
                                    continue L_outer5;
                                }
                                try {
                                    if (!(reliesOnTemplateParameters(prmtype, (this.parameters).opSlice(inferStart, (this.parameters).length))))
                                    {
                                        prmtype = typeSemantic(prmtype.syntaxCopy(), fd.value.loc, paramscope);
                                        if ((prmtype.ty & 0xFF) == ENUMTY.Ttuple)
                                        {
                                            TypeTuple tt = (TypeTuple)prmtype;
                                            int tt_dim = (tt.arguments).length;
                                            {
                                                int j = 0;
                                            L_outer8:
                                                for (; j < tt_dim;comma(j++, argi += 1)){
                                                    Parameter p = (tt.arguments).get(j);
                                                    if ((((j == tt_dim - 1 && fparameters.varargs == VarArg.typesafe) && parami + 1 == nfparams) && argi < nfargs))
                                                    {
                                                        prmtype = p.type;
                                                        /*goto Lvarargs*/throw Dispatch0.INSTANCE;
                                                    }
                                                    if (argi >= nfargs)
                                                    {
                                                        if (p.defaultArg != null)
                                                            continue L_outer8;
                                                        if (fparam.defaultArg != null)
                                                            break;
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    farg = (fargs).get(argi);
                                                    if (!((farg.implicitConvTo(p.type)) != 0))
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                }
                                            }
                                            continue L_outer5;
                                        }
                                    }
                                    if (argi >= nfargs)
                                    {
                                        if (!(fparam.defaultArg != null))
                                            /*goto Lvarargs*/throw Dispatch0.INSTANCE;
                                        if (argi == nfargs)
                                        {
                                            {
                                                int i = 0;
                                                for (; i < (dedtypes).length;i++){
                                                    Type at = isType((dedtypes).get(i));
                                                    if ((at != null && (at.ty & 0xFF) == ENUMTY.Tnone))
                                                    {
                                                        TypeDeduced xt = (TypeDeduced)at;
                                                        dedtypes.set(i, xt.tded);
                                                    }
                                                }
                                            }
                                            {
                                                int i = ntargs;
                                            L_outer9:
                                                for (; i < (dedargs).length;i++){
                                                    TemplateParameter tparam = (this.parameters).get(i);
                                                    RootObject oarg = (dedargs).get(i);
                                                    RootObject oded = (dedtypes).get(i);
                                                    if (!(oarg != null))
                                                    {
                                                        if (oded != null)
                                                        {
                                                            if ((tparam.specialization() != null || !(tparam.isTemplateTypeParameter() != null)))
                                                            {
                                                                dedargs.set(i, oded);
                                                                int m2 = tparam.matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, null);
                                                                if (m2 <= MATCH.nomatch)
                                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                                if (m2 < matchTiargs)
                                                                    matchTiargs = m2;
                                                                if (!((dedtypes).get(i).equals(oded)))
                                                                    this.error(new BytePtr("specialization not allowed for deduced parameter `%s`"), tparam.ident.toChars());
                                                            }
                                                            else
                                                            {
                                                                if (MATCH.convert < matchTiargs)
                                                                    matchTiargs = MATCH.convert;
                                                            }
                                                            dedargs.set(i, this.declareParameter(paramscope, tparam, oded));
                                                        }
                                                        else
                                                        {
                                                            this.inuse++;
                                                            oded = tparam.defaultArg(instLoc, paramscope);
                                                            this.inuse--;
                                                            if (oded != null)
                                                                dedargs.set(i, this.declareParameter(paramscope, tparam, oded));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        nfargs2 = argi + 1;
                                        if ((prmtype.deco != null || prmtype.syntaxCopy().trySemantic(this.loc, paramscope) != null))
                                        {
                                            argi += 1;
                                            continue L_outer5;
                                        }
                                        farg = fparam.defaultArg.syntaxCopy();
                                        farg = expressionSemantic(farg, paramscope);
                                        farg = resolveProperties(paramscope, farg);
                                    }
                                    else
                                    {
                                        farg = (fargs).get(argi);
                                    }
                                    {
                                        if (((farg.op & 0xFF) == 127 || (farg.type.ty & 0xFF) == ENUMTY.Terror))
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        Type att = null;
                                        while(true) try {
                                        /*Lretry:*/
                                            Type argtype = farg.type;
                                            if (((!((fparam.storageClass & 8192L) != 0) && (argtype.ty & 0xFF) == ENUMTY.Tvoid) && (farg.op & 0xFF) != 161))
                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            farg = farg.optimize(0, (fparam.storageClass & 2101248L) != 0L);
                                            RootObject oarg = farg;
                                            if (((fparam.storageClass & 2097152L) != 0 && (!((fparam.storageClass & 256L) != 0) || farg.isLvalue())))
                                            {
                                                Type taai = null;
                                                if (((argtype.ty & 0xFF) == ENUMTY.Tarray && ((prmtype.ty & 0xFF) == ENUMTY.Tsarray || (((prmtype.ty & 0xFF) == ENUMTY.Taarray && ((taai = ((TypeAArray)prmtype).index).ty & 0xFF) == ENUMTY.Tident) && ((TypeIdentifier)taai).idents.length == 0))))
                                                {
                                                    if ((farg.op & 0xFF) == 121)
                                                    {
                                                        StringExp se = (StringExp)farg;
                                                        argtype = se.type.nextOf().sarrayOf((long)se.len);
                                                    }
                                                    else if ((farg.op & 0xFF) == 47)
                                                    {
                                                        ArrayLiteralExp ae = (ArrayLiteralExp)farg;
                                                        argtype = ae.type.nextOf().sarrayOf((long)(ae.elements).length);
                                                    }
                                                    else if ((farg.op & 0xFF) == 31)
                                                    {
                                                        SliceExp se = (SliceExp)farg;
                                                        {
                                                            Type tsa = toStaticArrayType(se);
                                                            if (tsa != null)
                                                                argtype = tsa;
                                                        }
                                                    }
                                                }
                                                oarg = argtype;
                                            }
                                            else if (((((fparam.storageClass & 4096L) == 0L && ((argtype.ty & 0xFF) == ENUMTY.Tarray || (argtype.ty & 0xFF) == ENUMTY.Tpointer)) && templateParameterLookup(prmtype, this.parameters) != 305419896) && ((TypeIdentifier)prmtype).idents.length == 0))
                                            {
                                                Type ta = argtype.castMod((prmtype.mod) != 0 ? (byte)(argtype.nextOf().mod & 0xFF) : (byte)0);
                                                if (!pequals(ta, argtype))
                                                {
                                                    Expression ea = farg.copy();
                                                    ea.type = ta;
                                                    oarg = ea;
                                                }
                                            }
                                            if (((fparameters.varargs == VarArg.typesafe && parami + 1 == nfparams) && argi + 1 < nfargs))
                                                /*goto Lvarargs*/throw Dispatch0.INSTANCE;
                                            IntRef wm = ref(0);
                                            int m = deduceType(oarg, paramscope, prmtype, this.parameters, dedtypes, ptr(wm), inferStart, false);
                                            wildmatch |= wm.value;
                                            if ((m == MATCH.nomatch && prmtype.deco != null))
                                                m = farg.implicitConvTo(prmtype);
                                            if (m == MATCH.nomatch)
                                            {
                                                AggregateDeclaration ad = isAggregate(farg.type);
                                                if (((ad != null && ad.aliasthis != null) && !pequals(argtype, att)))
                                                {
                                                    if ((!(att != null) && argtype.checkAliasThisRec()))
                                                        att = argtype;
                                                    {
                                                        Expression e = resolveAliasThis(sc, farg, true);
                                                        if (e != null)
                                                        {
                                                            farg = e;
                                                            /*goto Lretry*/throw Dispatch0.INSTANCE;
                                                        }
                                                    }
                                                }
                                            }
                                            if ((m > MATCH.nomatch && (fparam.storageClass & 2097408L) == 2097152L))
                                            {
                                                if (!(farg.isLvalue()))
                                                {
                                                    if ((((farg.op & 0xFF) == 121 || (farg.op & 0xFF) == 31) && ((prmtype.ty & 0xFF) == ENUMTY.Tsarray || (prmtype.ty & 0xFF) == ENUMTY.Taarray)))
                                                    {
                                                    }
                                                    else
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                }
                                            }
                                            if ((m > MATCH.nomatch && (fparam.storageClass & 4096L) != 0))
                                            {
                                                if (!(farg.isLvalue()))
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                if (!(farg.type.isMutable()))
                                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                            }
                                            if ((((m == MATCH.nomatch && (fparam.storageClass & 8192L) != 0) && (prmtype.ty & 0xFF) == ENUMTY.Tvoid) && (farg.type.ty & 0xFF) != ENUMTY.Tvoid))
                                                m = MATCH.convert;
                                            if (m != MATCH.nomatch)
                                            {
                                                if (m < match)
                                                    match = m;
                                                argi++;
                                                continue L_outer5;
                                            }
                                            break;
                                        } catch(Dispatch0 __d){}
                                    }
                                }
                                catch(Dispatch0 __d){}
                            /*Lvarargs:*/
                                if (!((fparameters.varargs == VarArg.typesafe && parami + 1 == nfparams)))
                                    /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                Type tb = prmtype.toBasetype();
                                {
                                    int __dispatch1 = 0;
                                    dispatched_1:
                                    do {
                                        switch (__dispatch1 != 0 ? __dispatch1 : (tb.ty & 0xFF))
                                        {
                                            case 1:
                                            case 2:
                                                if ((tb.ty & 0xFF) == ENUMTY.Tsarray)
                                                {
                                                    TypeSArray tsa = (TypeSArray)tb;
                                                    long sz = tsa.dim.toInteger();
                                                    if (sz != (long)(nfargs - argi))
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                }
                                                else if ((tb.ty & 0xFF) == ENUMTY.Taarray)
                                                {
                                                    TypeAArray taa = (TypeAArray)tb;
                                                    Expression dim = new IntegerExp(instLoc, (long)(nfargs - argi), Type.tsize_t);
                                                    int i = templateParameterLookup(taa.index, this.parameters);
                                                    if (i == 305419896)
                                                    {
                                                        Ref<Expression> e = ref(null);
                                                        Ref<Type> t = ref(null);
                                                        Ref<Dsymbol> s = ref(null);
                                                        Scope sco = null;
                                                        int errors = global.startGagging();
                                                        sco = sc;
                                                        resolve(taa.index, instLoc, sco, ptr(e), ptr(t), ptr(s), false);
                                                        if (!(e.value != null))
                                                        {
                                                            sco = paramscope;
                                                            resolve(taa.index, instLoc, sco, ptr(e), ptr(t), ptr(s), false);
                                                        }
                                                        global.endGagging(errors);
                                                        if (!(e.value != null))
                                                        {
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                        e.value = e.value.ctfeInterpret();
                                                        e.value = e.value.implicitCastTo(sco, Type.tsize_t);
                                                        e.value = e.value.optimize(0, false);
                                                        if (!(dim.equals(e.value)))
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    }
                                                    else
                                                    {
                                                        TemplateParameter tprm = (this.parameters).get(i);
                                                        TemplateValueParameter tvp = tprm.isTemplateValueParameter();
                                                        if (!(tvp != null))
                                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        Expression e_1 = (Expression)(dedtypes).get(i);
                                                        if (e_1 != null)
                                                        {
                                                            if (!(dim.equals(e_1)))
                                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                        }
                                                        else
                                                        {
                                                            Type vt = typeSemantic(tvp.valType, Loc.initial, sc);
                                                            int m = dim.implicitConvTo(vt);
                                                            if (m <= MATCH.nomatch)
                                                                /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                            dedtypes.set(i, dim);
                                                        }
                                                    }
                                                }
                                                /*goto case*/{ __dispatch1 = 0; continue dispatched_1; }
                                            case 0:
                                                __dispatch1 = 0;
                                                TypeArray ta = (TypeArray)tb;
                                                Type tret = fparam.isLazyArray();
                                            L_outer10:
                                                for (; argi < nfargs;argi++){
                                                    Expression arg = (fargs).get(argi);
                                                    assert(arg != null);
                                                    int m_1 = MATCH.nomatch;
                                                    if (tret != null)
                                                    {
                                                        if (ta.next.equals(arg.type))
                                                        {
                                                            m_1 = MATCH.exact;
                                                        }
                                                        else
                                                        {
                                                            m_1 = arg.implicitConvTo(tret);
                                                            if (m_1 == MATCH.nomatch)
                                                            {
                                                                if ((tret.toBasetype().ty & 0xFF) == ENUMTY.Tvoid)
                                                                    m_1 = MATCH.convert;
                                                            }
                                                        }
                                                    }
                                                    else
                                                    {
                                                        IntRef wm = ref(0);
                                                        m_1 = deduceType(arg, paramscope, ta.next, this.parameters, dedtypes, ptr(wm), inferStart, false);
                                                        wildmatch |= wm.value;
                                                    }
                                                    if (m_1 == MATCH.nomatch)
                                                        /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                                    if (m_1 < match)
                                                        match = m_1;
                                                }
                                                /*goto Lmatch*/throw Dispatch0.INSTANCE;
                                            case 7:
                                            case 6:
                                                /*goto Lmatch*/throw Dispatch0.INSTANCE;
                                            default:
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        }
                                    } while(__dispatch1 != 0);
                                }
                                throw new AssertionError("Unreachable code!");
                            }
                        }
                        if ((argi != nfargs2 && fparameters.varargs == VarArg.none))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                    }
                }
                catch(Dispatch0 __d){}
            /*Lmatch:*/
                {
                    int i = 0;
                    for (; i < (dedtypes).length;i++){
                        Type at = isType((dedtypes).get(i));
                        if (at != null)
                        {
                            if ((at.ty & 0xFF) == ENUMTY.Tnone)
                            {
                                TypeDeduced xt = (TypeDeduced)at;
                                at = xt.tded;
                            }
                            dedtypes.set(i, at.merge2());
                        }
                    }
                }
                try {
                    {
                        int i = ntargs;
                    L_outer11:
                        for (; i < (dedargs).length;i++){
                            TemplateParameter tparam = (this.parameters).get(i);
                            RootObject oarg = (dedargs).get(i);
                            RootObject oded = (dedtypes).get(i);
                            if (!(oarg != null))
                            {
                                if (oded != null)
                                {
                                    if ((tparam.specialization() != null || !(tparam.isTemplateTypeParameter() != null)))
                                    {
                                        dedargs.set(i, oded);
                                        int m2 = tparam.matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, null);
                                        if (m2 <= MATCH.nomatch)
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        if (m2 < matchTiargs)
                                            matchTiargs = m2;
                                        if (!((dedtypes).get(i).equals(oded)))
                                            this.error(new BytePtr("specialization not allowed for deduced parameter `%s`"), tparam.ident.toChars());
                                    }
                                    else
                                    {
                                        if (MATCH.convert < matchTiargs)
                                            matchTiargs = MATCH.convert;
                                    }
                                }
                                else
                                {
                                    this.inuse++;
                                    oded = tparam.defaultArg(instLoc, paramscope);
                                    this.inuse--;
                                    if (!(oded != null))
                                    {
                                        if (((pequals(tparam, tp) && fptupindex == 305419896) && ntargs <= (dedargs).length - 1))
                                        {
                                            oded = new Tuple();
                                        }
                                        else
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                    }
                                    if (isError(oded))
                                        /*goto Lerror*/throw Dispatch2.INSTANCE;
                                    ntargs++;
                                    if (tparam.specialization() != null)
                                    {
                                        dedargs.set(i, oded);
                                        int m2 = tparam.matchArg(instLoc, paramscope, dedargs, i, this.parameters, dedtypes, null);
                                        if (m2 <= MATCH.nomatch)
                                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                                        if (m2 < matchTiargs)
                                            matchTiargs = m2;
                                        if (!((dedtypes).get(i).equals(oded)))
                                            this.error(new BytePtr("specialization not allowed for deduced parameter `%s`"), tparam.ident.toChars());
                                    }
                                }
                                oded = this.declareParameter(paramscope, tparam, oded);
                                dedargs.set(i, oded);
                            }
                        }
                    }
                    {
                        int d = (dedargs).length;
                        if ((d) != 0)
                        {
                            {
                                Tuple va = isTuple((dedargs).get(d - 1));
                                if (va != null)
                                {
                                    (dedargs).setDim(d - 1);
                                    (dedargs).insert(d - 1, va.objects);
                                }
                            }
                        }
                    }
                    ti.tiargs = dedargs;
                    {
                        assert((paramscope).scopesym != null);
                        Scope sc2 = this._scope;
                        sc2 = (sc2).push((paramscope).scopesym);
                        sc2 = (sc2).push(ti);
                        (sc2).parent = ti;
                        (sc2).tinst = ti;
                        (sc2).minst = (sc).minst;
                        fd.value = this.doHeaderInstantiation(ti, sc2, fd.value, tthis, fargs);
                        sc2 = (sc2).pop();
                        sc2 = (sc2).pop();
                        if (!(fd.value != null))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                    }
                    if (this.constraint != null)
                    {
                        if (!(this.evaluateConstraint(ti, sc, paramscope, dedargs, fd.value)))
                            /*goto Lnomatch*/throw Dispatch1.INSTANCE;
                    }
                    (paramscope).pop();
                    return match | matchTiargs << 4;
                }
                catch(Dispatch1 __d){}
            /*Lnomatch:*/
                (paramscope).pop();
                return MATCH.nomatch;
            }
            catch(Dispatch2 __d){}
        /*Lerror:*/
            (paramscope).pop();
            return MATCH.nomatch;
        }

        public  RootObject declareParameter(Scope sc, TemplateParameter tp, RootObject o) {
            Type ta = isType(o);
            Expression ea = isExpression(o);
            Dsymbol sa = isDsymbol(o);
            Tuple va = isTuple(o);
            Declaration d = null;
            VarDeclaration v = null;
            if ((ea != null && (ea.op & 0xFF) == 20))
                ta = ea.type;
            else if ((ea != null && (ea.op & 0xFF) == 203))
                sa = ((ScopeExp)ea).sds;
            else if ((ea != null && ((ea.op & 0xFF) == 123 || (ea.op & 0xFF) == 124)))
                sa = ((ThisExp)ea).var;
            else if ((ea != null && (ea.op & 0xFF) == 161))
            {
                if (((FuncExp)ea).td != null)
                    sa = ((FuncExp)ea).td;
                else
                    sa = ((FuncExp)ea).fd;
            }
            if (ta != null)
            {
                d = new AliasDeclaration(Loc.initial, tp.ident, ta);
            }
            else if (sa != null)
            {
                d = new AliasDeclaration(Loc.initial, tp.ident, sa);
            }
            else if (ea != null)
            {
                Initializer _init = new ExpInitializer(this.loc, ea);
                TemplateValueParameter tvp = tp.isTemplateValueParameter();
                Type t = tvp != null ? tvp.valType : null;
                v = new VarDeclaration(this.loc, t, tp.ident, _init, 0L);
                v.storage_class = 8650752L;
                d = v;
            }
            else if (va != null)
            {
                d = new TupleDeclaration(this.loc, tp.ident, va.objects);
            }
            else
            {
                throw new AssertionError("Unreachable code!");
            }
            d.storage_class |= 262144L;
            if (ta != null)
            {
                Type t = ta;
                for (; (t.ty & 0xFF) != ENUMTY.Tenum;){
                    if (!(t.nextOf() != null))
                        break;
                    t = ((TypeNext)t).next;
                }
                {
                    Dsymbol s = t.toDsymbol(sc);
                    if (s != null)
                    {
                        if (s.isDeprecated())
                            d.storage_class |= 1024L;
                    }
                }
            }
            else if (sa != null)
            {
                if (sa.isDeprecated())
                    d.storage_class |= 1024L;
            }
            if (!((sc).insert(d) != null))
                this.error(new BytePtr("declaration `%s` is already defined"), tp.ident.toChars());
            dsymbolSemantic(d, sc);
            if (v != null)
                o = initializerToExpression(v._init, null);
            return o;
        }

        public  FuncDeclaration doHeaderInstantiation(TemplateInstance ti, Scope sc2, FuncDeclaration fd, Type tthis, DArray<Expression> fargs) {
            assert(fd != null);
            if (fd.isCtorDeclaration() != null)
                fd = new CtorDeclaration(fd.loc, fd.endloc, fd.storage_class, fd.type.syntaxCopy(), false);
            else
                fd = new FuncDeclaration(fd.loc, fd.endloc, fd.ident, fd.storage_class, fd.type.syntaxCopy());
            fd.parent = ti;
            assert((fd.type.ty & 0xFF) == ENUMTY.Tfunction);
            TypeFunction tf = (TypeFunction)fd.type;
            tf.fargs = fargs;
            if (tthis != null)
            {
                boolean hasttp = false;
                {
                    int i = 0;
                    for (; i < (this.parameters).length;i++){
                        TemplateParameter tp = (this.parameters).get(i);
                        TemplateThisParameter ttp = tp.isTemplateThisParameter();
                        if (ttp != null)
                            hasttp = true;
                    }
                }
                if (hasttp)
                {
                    tf = (TypeFunction)tf.addSTC(ModToStc((tthis.mod & 0xFF)));
                    assert(tf.deco == null);
                }
            }
            Scope scx = (sc2).push();
            {
                int i = 0;
                for (; i < (tf.parameterList.parameters).length;i++) {
                    (tf.parameterList.parameters).get(i).defaultArg = null;
                }
            }
            tf.incomplete = true;
            if (fd.isCtorDeclaration() != null)
            {
                (scx).flags |= 1;
                Dsymbol parent = this.toParentDecl();
                Type tret = null;
                AggregateDeclaration ad = parent.isAggregateDeclaration();
                if ((!(ad != null) || parent.isUnionDeclaration() != null))
                {
                    tret = Type.tvoid;
                }
                else
                {
                    tret = ad.handleType();
                    assert(tret != null);
                    tret = tret.addStorageClass(fd.storage_class | (scx).stc);
                    tret = tret.addMod(tf.mod);
                }
                tf.next = tret;
                if ((ad != null && ad.isStructDeclaration() != null))
                    tf.isref = true;
            }
            else
                tf.next = null;
            fd.type = tf;
            fd.type = fd.type.addSTC((scx).stc);
            fd.type = typeSemantic(fd.type, fd.loc, scx);
            scx = (scx).pop();
            if ((fd.type.ty & 0xFF) != ENUMTY.Tfunction)
                return null;
            fd.originalType = fd.type;
            return fd;
        }

        public  TemplateInstance findExistingInstance(TemplateInstance tithis, DArray<Expression> fargs) {
            tithis.fargs = fargs;
            TemplateInstanceBox tibox = tibox = new TemplateInstanceBox(tithis);
            Ptr<TemplateInstance> p = pcopy(tibox in this.instances);
            return p != null ? p.get() : null;
        }

        public  TemplateInstance addInstance(TemplateInstance ti) {
            TemplateInstanceBox tibox = tibox = new TemplateInstanceBox(ti);
            this.instances.set(tibox, __aaval1201);
            return ti;
        }

        public  void removeInstance(TemplateInstance ti) {
            TemplateInstanceBox tibox = tibox = new TemplateInstanceBox(ti);
            this.instances.remove(tibox);
        }

        public  TemplateDeclaration isTemplateDeclaration() {
            return this;
        }

        public  TemplateTupleParameter isVariadic() {
            int dim = (this.parameters).length;
            if (dim == 0)
                return null;
            return (this.parameters).get(dim - 1).isTemplateTupleParameter();
        }

        public  boolean isOverloadable() {
            return true;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateDeclaration() {}

        public TemplateDeclaration copy() {
            TemplateDeclaration that = new TemplateDeclaration();
            that.parameters = this.parameters;
            that.origParameters = this.origParameters;
            that.constraint = this.constraint;
            that.instances = this.instances;
            that.overnext = this.overnext;
            that.overroot = this.overroot;
            that.funcroot = this.funcroot;
            that.onemember = this.onemember;
            that.literal = this.literal;
            that.ismixin = this.ismixin;
            that.isstatic = this.isstatic;
            that.protection = this.protection;
            that.inuse = this.inuse;
            that.previous = this.previous;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
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
    public static class TypeDeduced extends Type
    {
        public Type tded;
        public DArray<Expression> argexps = new DArray<Expression>();
        public DArray<Type> tparams = new DArray<Type>();
        public  TypeDeduced(Type tt, Expression e, Type tparam) {
            super((byte)11);
            this.tded = tt;
            this.argexps.push(e);
            this.tparams.push(tparam);
        }

        public  void update(Expression e, Type tparam) {
            this.argexps.push(e);
            this.tparams.push(tparam);
        }

        public  void update(Type tt, Expression e, Type tparam) {
            this.tded = tt;
            this.argexps.push(e);
            this.tparams.push(tparam);
        }

        public  int matchAll(Type tt) {
            int match = MATCH.exact;
            {
                int j = 0;
                for (; j < this.argexps.length;j++){
                    Expression e = this.argexps.get(j);
                    assert(e != null);
                    if (pequals(e, emptyArrayElement))
                        continue;
                    Type t = tt.addMod(this.tparams.get(j).mod).substWildTo(1);
                    int m = e.implicitConvTo(t);
                    if (match > m)
                        match = m;
                    if (match <= MATCH.nomatch)
                        break;
                }
            }
            return match;
        }


        public TypeDeduced() {}

        public TypeDeduced copy() {
            TypeDeduced that = new TypeDeduced();
            that.tded = this.tded;
            that.argexps = this.argexps;
            that.tparams = this.tparams;
            that.ty = this.ty;
            that.mod = this.mod;
            that.deco = this.deco;
            that.cto = this.cto;
            that.ito = this.ito;
            that.sto = this.sto;
            that.scto = this.scto;
            that.wto = this.wto;
            that.wcto = this.wcto;
            that.swto = this.swto;
            that.swcto = this.swcto;
            that.pto = this.pto;
            that.rto = this.rto;
            that.arrayof = this.arrayof;
            that.vtinfo = this.vtinfo;
            that.ctype = this.ctype;
            return that;
        }
    }
    public static void functionResolve(MatchAccumulator m, Dsymbol dstart, Loc loc, Scope sc, DArray<RootObject> tiargs, Type tthis, DArray<Expression> fargs, Ptr<BytePtr> pMessage) {
        Ref<Scope> sc_ref = ref(sc);
        Ref<DArray<RootObject>> tiargs_ref = ref(tiargs);
        Ref<Type> tthis_ref = ref(tthis);
        Ref<DArray<Expression>> fargs_ref = ref(fargs);
        Ref<Ptr<BytePtr>> pMessage_ref = ref(pMessage);
        Ref<Slice<Expression>> fargs_ = ref(peekSlice(fargs_ref.value).copy());
        IntRef property = ref(0);
        IntRef ov_index = ref(0);
        Ref<TemplateDeclaration> td_best = ref(null);
        Ref<TemplateInstance> ti_best = ref(null);
        IntRef ta_last = ref(m.last != MATCH.nomatch ? MATCH.exact : MATCH.nomatch);
        Ref<Type> tthis_best = ref(null);
        Function1<FuncDeclaration,Integer> applyFunction = new Function1<FuncDeclaration,Integer>(){
            public Integer invoke(FuncDeclaration fd){
                if (pequals(fd, m.lastf))
                    return 0;
                if ((tiargs_ref.value != null && (tiargs_ref.value).length > 0))
                    return 0;
                if ((!(fd.isCtorDeclaration() != null) && fd.semanticRun < PASS.semanticdone))
                {
                    Ungag ungag = fd.ungagSpeculative().copy();
                    try {
                        dsymbolSemantic(fd, null);
                    }
                    finally {
                    }
                }
                if (fd.semanticRun < PASS.semanticdone)
                {
                    error(loc, new BytePtr("forward reference to template `%s`"), fd.toChars());
                    return 1;
                }
                TypeFunction tf = (TypeFunction)fd.type;
                int prop = tf.isproperty ? 1 : 2;
                if (property.value == 0)
                    property.value = prop;
                else if (property.value != prop)
                    error(fd.loc, new BytePtr("cannot overload both property and non-property functions"));
                Type tthis_fd = fd.needThis() ? tthis_ref.value : null;
                boolean isCtorCall = (tthis_fd != null && fd.isCtorDeclaration() != null);
                if (isCtorCall)
                {
                    if (((MODimplicitConv(tf.mod, tthis_fd.mod) || (tf.isWild() && (tf.isShared() ? 1 : 0) == (tthis_fd.isShared() ? 1 : 0))) || fd.isReturnIsolated()))
                    {
                        tthis_fd = null;
                    }
                    else
                        return 0;
                }
                {
                    DtorDeclaration dt = fd.isDtorDeclaration();
                    if (dt != null)
                    {
                        TypeFunction dtmod = dt.type.toTypeFunction();
                        int shared_dtor = (dtmod.mod & 0xFF) & MODFlags.shared_;
                        int shared_this = tthis_fd != null ? (tthis_fd.mod & 0xFF) & MODFlags.shared_ : 0;
                        if (((shared_dtor) != 0 && !((shared_this) != 0)))
                            tthis_fd = dtmod;
                        else if ((((shared_this) != 0 && !((shared_dtor) != 0)) && tthis_fd != null))
                            tf.mod = tthis_fd.mod;
                    }
                }
                int mfa = tf.callMatch(tthis_fd, fargs_.value, 0, pMessage_ref.value, sc_ref.value);
                if (mfa > MATCH.nomatch)
                {
                    try {
                        if (mfa > m.last)
                            /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                        try {
                            if (mfa < m.last)
                                /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            assert(m.lastf != null);
                            if ((m.lastf.overrides(fd)) != 0)
                                /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            if ((fd.overrides(m.lastf)) != 0)
                                /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                            {
                                int c1 = fd.leastAsSpecialized(m.lastf);
                                int c2 = m.lastf.leastAsSpecialized(fd);
                                if (c1 > c2)
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                if (c1 < c2)
                                    /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            }
                            if (!(m.lastf.type.equals(fd.type)))
                            {
                                int lastCovariant = m.lastf.type.covariant(fd.type, null, true);
                                int firstCovariant = fd.type.covariant(m.lastf.type, null, true);
                                if ((lastCovariant == 1 || lastCovariant == 2))
                                {
                                    if ((firstCovariant != 1 && firstCovariant != 2))
                                    {
                                        /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                                    }
                                }
                                else if ((firstCovariant == 1 || firstCovariant == 2))
                                {
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                }
                            }
                            if (((((tf.equals(m.lastf.type) && fd.storage_class == m.lastf.storage_class) && pequals(fd.parent, m.lastf.parent)) && fd.protection.opEquals(m.lastf.protection)) && fd.linkage == m.lastf.linkage))
                            {
                                if ((fd.fbody != null && !(m.lastf.fbody != null)))
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                if ((!(fd.fbody != null) && m.lastf.fbody != null))
                                    /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            }
                            if ((isCtorCall && (tf.mod & 0xFF) != (m.lastf.type.mod & 0xFF)))
                            {
                                if ((tthis_ref.value.mod & 0xFF) == (tf.mod & 0xFF))
                                    /*goto LfIsBetter*/throw Dispatch1.INSTANCE;
                                if ((tthis_ref.value.mod & 0xFF) == (m.lastf.type.mod & 0xFF))
                                    /*goto LlastIsBetter*/throw Dispatch0.INSTANCE;
                            }
                            m.nextf = fd;
                            m.count++;
                            return 0;
                        }
                        catch(Dispatch0 __d){}
                    /*LlastIsBetter:*/
                        return 0;
                    }
                    catch(Dispatch1 __d){}
                /*LfIsBetter:*/
                    td_best.value = null;
                    ti_best.value = null;
                    ta_last.value = MATCH.exact;
                    m.last = mfa;
                    m.lastf = fd;
                    tthis_best.value = tthis_fd;
                    ov_index.value = 0;
                    m.count = 1;
                    return 0;
                }
                return 0;
            }
        };
        Function1<TemplateDeclaration,Integer> applyTemplate = new Function1<TemplateDeclaration,Integer>(){
            public Integer invoke(TemplateDeclaration td){
                if ((td.inuse) != 0)
                {
                    td.error(loc, new BytePtr("recursive template expansion"));
                    return 1;
                }
                if (pequals(td, td_best.value))
                    return 0;
                if (sc_ref.value == null)
                    sc_ref.value = td._scope;
                if ((td.semanticRun == PASS.init && td._scope != null))
                {
                    Ungag ungag = td.ungagSpeculative().copy();
                    try {
                        dsymbolSemantic(td, td._scope);
                    }
                    finally {
                    }
                }
                if (td.semanticRun == PASS.init)
                {
                    error(loc, new BytePtr("forward reference to template `%s`"), td.toChars());
                /*Lerror:*/
                    m.lastf = null;
                    m.count = 0;
                    m.last = MATCH.nomatch;
                    return 1;
                }
                FuncDeclaration f = td.onemember != null ? td.onemember.isFuncDeclaration() : null;
                if (!(f != null))
                {
                    if (tiargs_ref.value == null)
                        tiargs_ref.value = new DArray<RootObject>();
                    TemplateInstance ti = new TemplateInstance(loc, td, tiargs_ref.value);
                    DArray<RootObject> dedtypes = dedtypes = new DArray<RootObject>((td.parameters).length);
                    try {
                        assert(td.semanticRun != PASS.init);
                        int mta = td.matchWithInstance(sc_ref.value, ti, dedtypes, fargs_ref.value, 0);
                        if ((mta <= MATCH.nomatch || mta < ta_last.value))
                            return 0;
                        templateInstanceSemantic(ti, sc_ref.value, fargs_ref.value);
                        if (!(ti.inst != null))
                            return 0;
                        Dsymbol s = ti.inst.toAlias();
                        FuncDeclaration fd = null;
                        {
                            TemplateDeclaration tdx = s.isTemplateDeclaration();
                            if (tdx != null)
                            {
                                DArray<RootObject> dedtypesX = new DArray<RootObject>();
                                try {
                                    {
                                        TemplatePrevious p = tdx.previous;
                                    L_outer12:
                                        for (; p != null;p = (p).prev){
                                            if (arrayObjectMatch((p).dedargs, dedtypesX))
                                            {
                                                {
                                                    Scope scx = sc_ref.value;
                                                L_outer13:
                                                    for (; scx != null;scx = (scx).enclosing){
                                                        if (scx == (p).sc)
                                                        {
                                                            error(loc, new BytePtr("recursive template expansion while looking for `%s.%s`"), ti.toChars(), tdx.toChars());
                                                            /*goto Lerror*/throw Dispatch0.INSTANCE;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    TemplatePrevious pr = new TemplatePrevious();
                                    pr.prev = tdx.previous;
                                    pr.sc = sc_ref.value;
                                    pr.dedargs = dedtypesX;
                                    tdx.previous = pr;
                                    fd = resolveFuncCall(loc, sc_ref.value, s, null, tthis_ref.value, fargs_ref.value, FuncResolveFlag.quiet);
                                    tdx.previous = pr.prev;
                                }
                                finally {
                                }
                            }
                            else if (s.isFuncDeclaration() != null)
                            {
                                fd = resolveFuncCall(loc, sc_ref.value, s, null, tthis_ref.value, fargs_ref.value, FuncResolveFlag.quiet);
                            }
                            else
                                /*goto Lerror*/throw Dispatch0.INSTANCE;
                        }
                        if (!(fd != null))
                            return 0;
                        if ((fd.type.ty & 0xFF) != ENUMTY.Tfunction)
                        {
                            m.lastf = fd;
                            m.count = 1;
                            m.last = MATCH.nomatch;
                            return 1;
                        }
                        Type tthis_fd = (fd.needThis() && !(fd.isCtorDeclaration() != null)) ? tthis_ref.value : null;
                        TypeFunction tf = (TypeFunction)fd.type;
                        int mfa = tf.callMatch(tthis_fd, fargs_.value, 0, null, sc_ref.value);
                        if (mfa < m.last)
                            return 0;
                        try {
                            if (mta < ta_last.value)
                                /*goto Ltd_best2*/throw Dispatch0.INSTANCE;
                            try {
                                if (mta > ta_last.value)
                                    /*goto Ltd2*/throw Dispatch1.INSTANCE;
                                if (mfa < m.last)
                                    /*goto Ltd_best2*/throw Dispatch0.INSTANCE;
                                if (mfa > m.last)
                                    /*goto Ltd2*/throw Dispatch1.INSTANCE;
                                m.nextf = fd;
                                m.count++;
                                return 0;
                            }
                            catch(Dispatch0 __d){}
                        /*Ltd_best2:*/
                            return 0;
                        }
                        catch(Dispatch1 __d){}
                    /*Ltd2:*/
                        assert(td._scope != null);
                        td_best.value = td;
                        ti_best.value = null;
                        property.value = 0;
                        ta_last.value = mta;
                        m.last = mfa;
                        m.lastf = fd;
                        tthis_best.value = tthis_fd;
                        ov_index.value = 0;
                        m.nextf = null;
                        m.count = 1;
                        return 0;
                    }
                    finally {
                    }
                }
                {
                    int ovi = 0;
                L_outer14:
                    for (; f != null;comma(f = f.overnext0, ovi++)){
                        if (((f.type.ty & 0xFF) != ENUMTY.Tfunction || f.errors))
                            /*goto Lerror*/throw Dispatch0.INSTANCE;
                        TemplateInstance ti = new TemplateInstance(loc, td, tiargs_ref.value);
                        ti.parent = td.parent;
                        Ref<FuncDeclaration> fd = ref(f);
                        int x = td.deduceFunctionTemplateMatch(ti, sc_ref.value, fd, tthis_ref.value, fargs_ref.value);
                        int mta = x >> 4;
                        int mfa = x & 15;
                        if ((!(fd.value != null) || mfa == MATCH.nomatch))
                            continue L_outer14;
                        Type tthis_fd = fd.value.needThis() ? tthis_ref.value : null;
                        boolean isCtorCall = (tthis_fd != null && fd.value.isCtorDeclaration() != null);
                        if (isCtorCall)
                        {
                            TypeFunction tf = (TypeFunction)fd.value.type;
                            assert(tf.next != null);
                            if (((MODimplicitConv(tf.mod, tthis_fd.mod) || (tf.isWild() && (tf.isShared() ? 1 : 0) == (tthis_fd.isShared() ? 1 : 0))) || fd.value.isReturnIsolated()))
                            {
                                tthis_fd = null;
                            }
                            else
                                continue L_outer14;
                        }
                        try {
                            if (mta < ta_last.value)
                                /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                            try {
                                if (mta > ta_last.value)
                                    /*goto Ltd*/throw Dispatch1.INSTANCE;
                                if (mfa < m.last)
                                    /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                if (mfa > m.last)
                                    /*goto Ltd*/throw Dispatch1.INSTANCE;
                                if (td_best.value != null)
                                {
                                    int c1 = td.leastAsSpecialized(sc_ref.value, td_best.value, fargs_ref.value);
                                    int c2 = td_best.value.leastAsSpecialized(sc_ref.value, td, fargs_ref.value);
                                    if (c1 > c2)
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    if (c1 < c2)
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                }
                                assert((fd.value != null && m.lastf != null));
                                {
                                    TypeFunction tf1 = (TypeFunction)fd.value.type;
                                    assert((tf1.ty & 0xFF) == ENUMTY.Tfunction);
                                    TypeFunction tf2 = (TypeFunction)m.lastf.type;
                                    assert((tf2.ty & 0xFF) == ENUMTY.Tfunction);
                                    int c1 = tf1.callMatch(tthis_fd, fargs_.value, 0, null, sc_ref.value);
                                    int c2 = tf2.callMatch(tthis_best.value, fargs_.value, 0, null, sc_ref.value);
                                    if (c1 > c2)
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    if (c1 < c2)
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                }
                                {
                                    int c1 = fd.value.leastAsSpecialized(m.lastf);
                                    int c2 = m.lastf.leastAsSpecialized(fd.value);
                                    if (c1 > c2)
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    if (c1 < c2)
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                }
                                if ((isCtorCall && (fd.value.type.mod & 0xFF) != (m.lastf.type.mod & 0xFF)))
                                {
                                    if ((tthis_ref.value.mod & 0xFF) == (fd.value.type.mod & 0xFF))
                                        /*goto Ltd*/throw Dispatch1.INSTANCE;
                                    if ((tthis_ref.value.mod & 0xFF) == (m.lastf.type.mod & 0xFF))
                                        /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                                }
                                m.nextf = fd.value;
                                m.count++;
                                continue L_outer14;
                            }
                            catch(Dispatch0 __d){}
                        /*Ltd_best:*/
                            continue L_outer14;
                        }
                        catch(Dispatch1 __d){}
                    /*Ltd:*/
                        assert(td._scope != null);
                        td_best.value = td;
                        ti_best.value = ti;
                        property.value = 0;
                        ta_last.value = mta;
                        m.last = mfa;
                        m.lastf = fd.value;
                        tthis_best.value = tthis_fd;
                        ov_index.value = ovi;
                        m.nextf = null;
                        m.count = 1;
                        continue L_outer14;
                    }
                }
                return 0;
            }
        };
        TemplateDeclaration td = dstart.isTemplateDeclaration();
        if ((td != null && td.funcroot != null))
            dstart = td.funcroot;
        Function1<Dsymbol,Integer> __lambda11 = new Function1<Dsymbol,Integer>(){
            public Integer invoke(Dsymbol s){
                if (s.errors)
                    return 0;
                {
                    FuncDeclaration fd = s.isFuncDeclaration();
                    if (fd != null)
                        return applyFunction.invoke(fd);
                }
                {
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (td != null)
                        return applyTemplate.invoke(td);
                }
                return 0;
            }
        };
        overloadApply(dstart, __lambda11, sc_ref.value);
        if (((td_best.value != null && ti_best.value != null) && m.count == 1))
        {
            assert((td_best.value.onemember != null && td_best.value.onemember.isFuncDeclaration() != null));
            assert(td_best.value._scope != null);
            if (sc_ref.value == null)
                sc_ref.value = td_best.value._scope;
            TemplateInstance ti = new TemplateInstance(loc, td_best.value, ti_best.value.tiargs);
            templateInstanceSemantic(ti, sc_ref.value, fargs_ref.value);
            m.lastf = ti.toAlias().isFuncDeclaration();
            if (!(m.lastf != null))
                /*goto Lnomatch*/throw Dispatch.INSTANCE;
            if (ti.errors)
            {
            /*Lerror:*/
                m.count = 1;
                assert(m.lastf != null);
                m.last = MATCH.nomatch;
                return ;
            }
            for (; (ov_index.value--) != 0;){
                m.lastf = m.lastf.overnext0;
                assert(m.lastf != null);
            }
            tthis_best.value = (m.lastf.needThis() && !(m.lastf.isCtorDeclaration() != null)) ? tthis_ref.value : null;
            TypeFunction tf = (TypeFunction)m.lastf.type;
            if ((tf.ty & 0xFF) == ENUMTY.Terror)
                /*goto Lerror*/throw Dispatch0.INSTANCE;
            assert((tf.ty & 0xFF) == ENUMTY.Tfunction);
            if (!((tf.callMatch(tthis_best.value, fargs_.value, 0, null, sc_ref.value)) != 0))
                /*goto Lnomatch*/throw Dispatch.INSTANCE;
            if ((tf.next != null && !(m.lastf.inferRetType)))
            {
                m.lastf.type = typeSemantic(tf, loc, sc_ref.value);
            }
        }
        else if (m.lastf != null)
        {
            assert(m.count >= 1);
        }
        else
        {
        /*Lnomatch:*/
            m.count = 0;
            m.lastf = null;
            m.last = MATCH.nomatch;
        }
    }

    public static int templateIdentifierLookup(Identifier id, DArray<TemplateParameter> parameters) {
        {
            int i = 0;
            for (; i < (parameters).length;i++){
                TemplateParameter tp = (parameters).get(i);
                if (tp.ident.equals(id))
                    return i;
            }
        }
        return 305419896;
    }

    public static int templateParameterLookup(Type tparam, DArray<TemplateParameter> parameters) {
        if ((tparam.ty & 0xFF) == ENUMTY.Tident)
        {
            TypeIdentifier tident = (TypeIdentifier)tparam;
            return templateIdentifierLookup(tident.ident, parameters);
        }
        return 305419896;
    }

    public static byte deduceWildHelper(Type t, Ptr<Type> at, Type tparam) {
        if (((tparam.mod & 0xFF) & MODFlags.wild) == 0)
            return (byte)0;
        at.set(0, null);
        // from template X!(IntegerInteger)
        Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>(){
            public Integer invoke(Integer U, Integer T){
                return U << 4 | T;
            }
        };

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(ByteByte)
        Function2<Byte,Byte,Integer> XByteByte = new Function2<Byte,Byte,Integer>(){
            public Integer invoke(Byte U, Byte T){
                return (U & 0xFF) << 4 | (T & 0xFF);
            }
        };

        switch (XByteByte.invoke(tparam.mod, t.mod))
        {
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 162:
            case 163:
            case 164:
            case 178:
            case 179:
            case 180:
                byte wm = (byte)((t.mod & 0xFF) & -3);
                if ((wm & 0xFF) == 0)
                    wm = (byte)16;
                byte m = (byte)((t.mod & 0xFF) & 5 | (tparam.mod & 0xFF) & (t.mod & 0xFF) & MODFlags.shared_);
                at.set(0, t.unqualify((m & 0xFF)));
                return wm;
            case 136:
            case 137:
            case 138:
            case 139:
            case 152:
            case 153:
            case 154:
            case 155:
            case 170:
            case 171:
            case 186:
            case 187:
                at.set(0, t.unqualify(((tparam.mod & 0xFF) & (t.mod & 0xFF))));
                return (byte)8;
            default:
            return (byte)0;
        }
    }

    public static Type rawTypeMerge(Type t1, Type t2) {
        if (t1.equals(t2))
            return t1;
        if (t1.equivalent(t2))
            return t1.castMod(MODmerge(t1.mod, t2.mod));
        Type t1b = t1.toBasetype();
        Type t2b = t2.toBasetype();
        if (t1b.equals(t2b))
            return t1b;
        if (t1b.equivalent(t2b))
            return t1b.castMod(MODmerge(t1b.mod, t2b.mod));
        byte ty = impcnvResult.get((t1b.ty & 0xFF)).get((t2b.ty & 0xFF));
        if ((ty & 0xFF) != ENUMTY.Terror)
            return Type.basic.get((ty & 0xFF));
        return null;
    }

    public static int deduceTypeHelper(Type t, Ptr<Type> at, Type tparam) {
        // from template X!(IntegerInteger)
        Function2<Integer,Integer,Integer> XIntegerInteger = new Function2<Integer,Integer,Integer>(){
            public Integer invoke(Integer U, Integer T){
                return U << 4 | T;
            }
        };

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(IntegerInteger)
        // removed duplicate function, [["int Xint, intIntegerInteger"]] signature: int Xint, intIntegerInteger

        // from template X!(ByteByte)
        Function2<Byte,Byte,Integer> XByteByte = new Function2<Byte,Byte,Integer>(){
            public Integer invoke(Byte U, Byte T){
                return (U & 0xFF) << 4 | (T & 0xFF);
            }
        };

        switch (XByteByte.invoke(tparam.mod, t.mod))
        {
            case 0:
            case 1:
            case 8:
            case 9:
            case 2:
            case 3:
            case 10:
            case 11:
            case 4:
                at.set(0, t);
                return MATCH.exact;
            case 17:
            case 136:
            case 153:
            case 34:
            case 51:
            case 170:
            case 187:
            case 68:
                at.set(0, t.mutableOf().unSharedOf());
                return MATCH.exact;
            case 16:
            case 24:
            case 25:
            case 19:
            case 26:
            case 27:
            case 20:
            case 138:
            case 155:
            case 52:
                at.set(0, t.mutableOf());
                return MATCH.constant;
            case 18:
                at.set(0, t);
                return MATCH.constant;
            case 35:
            case 42:
            case 43:
            case 50:
                at.set(0, t.unSharedOf());
                return MATCH.constant;
            case 148:
            case 59:
            case 180:
            case 186:
                at.set(0, t.unSharedOf().mutableOf());
                return MATCH.constant;
            case 58:
                at.set(0, t.unSharedOf().mutableOf());
                return MATCH.constant;
            case 128:
            case 129:
            case 137:
            case 132:
            case 130:
            case 131:
            case 139:
            case 144:
            case 145:
            case 152:
            case 146:
            case 147:
            case 154:
            case 32:
            case 33:
            case 40:
            case 41:
            case 36:
            case 48:
            case 49:
            case 56:
            case 57:
            case 160:
            case 161:
            case 168:
            case 169:
            case 164:
            case 162:
            case 163:
            case 171:
            case 176:
            case 177:
            case 184:
            case 185:
            case 178:
            case 179:
            case 64:
            case 65:
            case 72:
            case 73:
            case 66:
            case 67:
            case 74:
            case 75:
                return MATCH.nomatch;
            default:
            throw new AssertionError("Unreachable code!");
        }
    }

    static Expression emptyArrayElement = null;
    public static int deduceType(RootObject o, Scope sc, Type tparam, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, IntPtr wm, int inferStart, boolean ignoreAliasThis) {
        DeduceType v = new DeduceType(sc, tparam, parameters, dedtypes, wm, inferStart, ignoreAliasThis);
        {
            Type t = isType(o);
            if (t != null)
                t.accept(v);
            else {
                Expression e = isExpression(o);
                if (e != null)
                {
                    assert(wm != null);
                    e.accept(v);
                }
                else
                    throw new AssertionError("Unreachable code!");
            }
        }
        return v.result;
    }

    public static boolean reliesOnTident(Type t, DArray<TemplateParameter> tparams, int iStart) {
        return reliesOnTemplateParameters(t, (tparams).opSlice(0, (tparams).length));
    }

    public static boolean reliesOnTemplateParameters(Type t, Slice<TemplateParameter> tparams) {
        Ref<Slice<TemplateParameter>> tparams_ref = ref(tparams);
        Function1<TypeVector,Boolean> visitVector = new Function1<TypeVector,Boolean>(){
            public Boolean invoke(TypeVector t){
                return reliesOnTemplateParameters(t.basetype, tparams_ref.value);
            }
        };
        Function1<TypeAArray,Boolean> visitAArray = new Function1<TypeAArray,Boolean>(){
            public Boolean invoke(TypeAArray t){
                return (reliesOnTemplateParameters(t.next, tparams_ref.value) || reliesOnTemplateParameters(t.index, tparams_ref.value));
            }
        };
        Function1<TypeFunction,Boolean> visitFunction = new Function1<TypeFunction,Boolean>(){
            public Boolean invoke(TypeFunction t){
                {
                    int __key1209 = 0;
                    int __limit1210 = t.parameterList.length();
                    for (; __key1209 < __limit1210;__key1209 += 1) {
                        int i = __key1209;
                        Parameter fparam = t.parameterList.get(i);
                        if (reliesOnTemplateParameters(fparam.type, tparams_ref.value))
                            return true;
                    }
                }
                return reliesOnTemplateParameters(t.next, tparams_ref.value);
            }
        };
        Function1<TypeIdentifier,Boolean> visitIdentifier = new Function1<TypeIdentifier,Boolean>(){
            public Boolean invoke(TypeIdentifier t){
                {
                    Slice<TemplateParameter> __r1211 = tparams_ref.value.copy();
                    int __key1212 = 0;
                    for (; __key1212 < __r1211.getLength();__key1212 += 1) {
                        TemplateParameter tp = __r1211.get(__key1212);
                        if (tp.ident.equals(t.ident))
                            return true;
                    }
                }
                return false;
            }
        };
        Function1<TypeInstance,Boolean> visitInstance = new Function1<TypeInstance,Boolean>(){
            public Boolean invoke(TypeInstance t){
                {
                    Slice<TemplateParameter> __r1213 = tparams_ref.value.copy();
                    int __key1214 = 0;
                    for (; __key1214 < __r1213.getLength();__key1214 += 1) {
                        TemplateParameter tp = __r1213.get(__key1214);
                        if (pequals(t.tempinst.name, tp.ident))
                            return true;
                    }
                }
                if (t.tempinst.tiargs != null)
                {
                    Slice<RootObject> __r1215 = (t.tempinst.tiargs).opSlice().copy();
                    int __key1216 = 0;
                    for (; __key1216 < __r1215.getLength();__key1216 += 1) {
                        RootObject arg = __r1215.get(__key1216);
                        {
                            Type ta = isType(arg);
                            if (ta != null)
                            {
                                if (reliesOnTemplateParameters(ta, tparams_ref.value))
                                    return true;
                            }
                        }
                    }
                }
                return false;
            }
        };
        Function1<TypeTypeof,Boolean> visitTypeof = new Function1<TypeTypeof,Boolean>(){
            public Boolean invoke(TypeTypeof t){
                return reliesOnTemplateParameters(t.exp, tparams_ref.value);
            }
        };
        Function1<TypeTuple,Boolean> visitTuple = new Function1<TypeTuple,Boolean>(){
            public Boolean invoke(TypeTuple t){
                if (t.arguments != null)
                {
                    Slice<Parameter> __r1217 = (t.arguments).opSlice().copy();
                    int __key1218 = 0;
                    for (; __key1218 < __r1217.getLength();__key1218 += 1) {
                        Parameter arg = __r1217.get(__key1218);
                        if (reliesOnTemplateParameters(arg.type, tparams_ref.value))
                            return true;
                    }
                }
                return false;
            }
        };
        if (!(t != null))
            return false;
        Type tb = t.toBasetype();
        switch ((tb.ty & 0xFF))
        {
            case 41:
                return visitVector.invoke(tb.isTypeVector());
            case 2:
                return visitAArray.invoke(tb.isTypeAArray());
            case 5:
                return visitFunction.invoke(tb.isTypeFunction());
            case 6:
                return visitIdentifier.invoke(tb.isTypeIdentifier());
            case 35:
                return visitInstance.invoke(tb.isTypeInstance());
            case 36:
                return visitTypeof.invoke(tb.isTypeTypeof());
            case 37:
                return visitTuple.invoke(tb.isTypeTuple());
            case 9:
                return false;
            default:
            return reliesOnTemplateParameters(tb.nextOf(), tparams_ref.value);
        }
    }

    public static boolean reliesOnTemplateParameters(Expression e, Slice<TemplateParameter> tparams) {
        ReliesOnTemplateParameters v = new ReliesOnTemplateParameters(tparams);
        e.accept(v);
        return v.result;
    }

    public static abstract class TemplateParameter extends ASTNode
    {
        public Loc loc = new Loc();
        public Identifier ident;
        public boolean dependent;
        public  TemplateParameter(Loc loc, Identifier ident) {
            super();
            this.loc = loc.copy();
            this.ident = ident;
        }

        public  TemplateTypeParameter isTemplateTypeParameter() {
            return null;
        }

        public  TemplateValueParameter isTemplateValueParameter() {
            return null;
        }

        public  TemplateAliasParameter isTemplateAliasParameter() {
            return null;
        }

        public  TemplateThisParameter isTemplateThisParameter() {
            return null;
        }

        public  TemplateTupleParameter isTemplateTupleParameter() {
            return null;
        }

        public abstract TemplateParameter syntaxCopy();
        public abstract boolean declareParameter(Scope sc);
        public abstract void print(RootObject oarg, RootObject oded);
        public abstract RootObject specialization();
        public abstract RootObject defaultArg(Loc instLoc, Scope sc);
        public abstract boolean hasDefaultArg();
        public  BytePtr toChars() {
            return this.ident.toChars();
        }

        public  int dyncast() {
            return DYNCAST.templateparameter;
        }

        public  int matchArg(Loc instLoc, Scope sc, DArray<RootObject> tiargs, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam) {
            RootObject oarg = null;
            try {
                if (i < (tiargs).length)
                    oarg = (tiargs).get(i);
                else
                {
                    oarg = this.defaultArg(instLoc, sc);
                    if (!(oarg != null))
                    {
                        assert(i < (dedtypes).length);
                        oarg = (dedtypes).get(i);
                        if (!(oarg != null))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                return this.matchArg(sc, oarg, i, parameters, dedtypes, psparam);
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
                psparam.set(0, null);
            return MATCH.nomatch;
        }

        public abstract int matchArg(Scope sc, RootObject oarg, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam);
        public abstract Object dummyArg();
        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateParameter() {}

        public abstract TemplateParameter copy();
    }
    public static class TemplateTypeParameter extends TemplateParameter
    {
        public Type specType;
        public Type defaultType;
        public static Type tdummy = null;
        public  TemplateTypeParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
            super(loc, ident);
            this.specType = specType;
            this.defaultType = defaultType;
        }

        public  TemplateTypeParameter isTemplateTypeParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateTypeParameter(this.loc, this.ident, this.specType != null ? this.specType.syntaxCopy() : null, this.defaultType != null ? this.defaultType.syntaxCopy() : null);
        }

        public  boolean declareParameter(Scope sc) {
            TypeIdentifier ti = new TypeIdentifier(this.loc, this.ident);
            Declaration ad = new AliasDeclaration(this.loc, this.ident, ti);
            return (sc).insert(ad) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s\n"), this.ident.toChars());
            Type t = isType(oarg);
            Type ta = isType(oded);
            assert(ta != null);
            if (this.specType != null)
                printf(new BytePtr("\u0009Specialization: %s\n"), this.specType.toChars());
            if (this.defaultType != null)
                printf(new BytePtr("\u0009Default:        %s\n"), this.defaultType.toChars());
            printf(new BytePtr("\u0009Parameter:       %s\n"), t != null ? t.toChars() : new BytePtr("NULL"));
            printf(new BytePtr("\u0009Deduced Type:   %s\n"), ta.toChars());
        }

        public  RootObject specialization() {
            return this.specType;
        }

        public  RootObject defaultArg(Loc instLoc, Scope sc) {
            Type t = this.defaultType;
            if (t != null)
            {
                t = t.syntaxCopy();
                t = typeSemantic(t, this.loc, sc);
            }
            return t;
        }

        public  boolean hasDefaultArg() {
            return this.defaultType != null;
        }

        public  int matchArg(Scope sc, RootObject oarg, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam) {
            int m = MATCH.exact;
            Type ta = isType(oarg);
            try {
                if (!(ta != null))
                {
                    /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                }
                if (this.specType != null)
                {
                    if ((!(ta != null) || pequals(ta, tdummy)))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    int m2 = deduceType(ta, sc, this.specType, parameters, dedtypes, null, 0, false);
                    if (m2 <= MATCH.nomatch)
                    {
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                    if (m2 < m)
                        m = m2;
                    if ((dedtypes).get(i) != null)
                    {
                        Type t = (Type)(dedtypes).get(i);
                        if ((this.dependent && !(t.equals(ta))))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        ta = t;
                    }
                }
                else
                {
                    if ((dedtypes).get(i) != null)
                    {
                        Type t = (Type)(dedtypes).get(i);
                        if (!(t.equals(ta)))
                        {
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else
                    {
                        m = MATCH.convert;
                    }
                }
                dedtypes.set(i, ta);
                if (psparam != null)
                    psparam.set(0, (new AliasDeclaration(this.loc, this.ident, ta)));
                return this.dependent ? MATCH.exact : m;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
                psparam.set(0, null);
            return MATCH.nomatch;
        }

        public  Object dummyArg() {
            Type t = this.specType;
            if (!(t != null))
            {
                if (!(tdummy != null))
                    tdummy = new TypeIdentifier(this.loc, this.ident);
                t = tdummy;
            }
            return t;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateTypeParameter() {}

        public TemplateTypeParameter copy() {
            TemplateTypeParameter that = new TemplateTypeParameter();
            that.specType = this.specType;
            that.defaultType = this.defaultType;
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateThisParameter extends TemplateTypeParameter
    {
        public  TemplateThisParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
            super(loc, ident, specType, defaultType);
        }

        public  TemplateThisParameter isTemplateThisParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateThisParameter(this.loc, this.ident, this.specType != null ? this.specType.syntaxCopy() : null, this.defaultType != null ? this.defaultType.syntaxCopy() : null);
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateThisParameter() {}

        public TemplateThisParameter copy() {
            TemplateThisParameter that = new TemplateThisParameter();
            that.specType = this.specType;
            that.defaultType = this.defaultType;
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateValueParameter extends TemplateParameter
    {
        public Type valType;
        public Expression specValue;
        public Expression defaultValue;
        public static AA<Object,Expression> edummies = new AA<Object,Expression>();
        public  TemplateValueParameter(Loc loc, Identifier ident, Type valType, Expression specValue, Expression defaultValue) {
            super(loc, ident);
            this.valType = valType;
            this.specValue = specValue;
            this.defaultValue = defaultValue;
        }

        public  TemplateValueParameter isTemplateValueParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateValueParameter(this.loc, this.ident, this.valType.syntaxCopy(), this.specValue != null ? this.specValue.syntaxCopy() : null, this.defaultValue != null ? this.defaultValue.syntaxCopy() : null);
        }

        public  boolean declareParameter(Scope sc) {
            VarDeclaration v = new VarDeclaration(this.loc, this.valType, this.ident, null, 0L);
            v.storage_class = 262144L;
            return (sc).insert(v) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s\n"), this.ident.toChars());
            Expression ea = isExpression(oded);
            if (this.specValue != null)
                printf(new BytePtr("\u0009Specialization: %s\n"), this.specValue.toChars());
            printf(new BytePtr("\u0009Parameter Value: %s\n"), ea != null ? ea.toChars() : new BytePtr("NULL"));
        }

        public  RootObject specialization() {
            return this.specValue;
        }

        public  RootObject defaultArg(Loc instLoc, Scope sc) {
            Expression e = this.defaultValue;
            if (e != null)
            {
                e = e.syntaxCopy();
                int olderrs = global.errors;
                if ((e = expressionSemantic(e, sc)) == null)
                    return null;
                if ((e = resolveProperties(sc, e)) == null)
                    return null;
                e = e.resolveLoc(instLoc, sc);
                e = e.optimize(0, false);
                if (global.errors != olderrs)
                    e = new ErrorExp();
            }
            return e;
        }

        public  boolean hasDefaultArg() {
            return this.defaultValue != null;
        }

        public  int matchArg(Scope sc, RootObject oarg, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam) {
            int m = MATCH.exact;
            Expression ei = isExpression(oarg);
            Type vt = null;
            try {
                if ((!(ei != null) && oarg != null))
                {
                    Dsymbol si = isDsymbol(oarg);
                    FuncDeclaration f = si != null ? si.isFuncDeclaration() : null;
                    if (((!(f != null) || !(f.fbody != null)) || f.needThis()))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    ei = new VarExp(this.loc, f, true);
                    ei = expressionSemantic(ei, sc);
                    int olderrors = global.startGagging();
                    ei = resolveProperties(sc, ei);
                    ei = ei.ctfeInterpret();
                    if ((global.endGagging(olderrors) || (ei.op & 0xFF) == 127))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    m = MATCH.convert;
                }
                if ((ei != null && (ei.op & 0xFF) == 26))
                {
                    ei = ei.ctfeInterpret();
                }
                vt = typeSemantic(this.valType, this.loc, sc);
                if (ei.type != null)
                {
                    int m2 = ei.implicitConvTo(vt);
                    if (m2 < m)
                        m = m2;
                    if (m <= MATCH.nomatch)
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    ei = ei.implicitCastTo(sc, vt);
                    ei = ei.ctfeInterpret();
                }
                if (this.specValue != null)
                {
                    if ((ei == null || (ei.type in edummies != null && pequals(edummies.get(ei.type), ei))))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    Expression e = this.specValue;
                    sc = (sc).startCTFE();
                    e = expressionSemantic(e, sc);
                    e = resolveProperties(sc, e);
                    sc = (sc).endCTFE();
                    e = e.implicitCastTo(sc, vt);
                    e = e.ctfeInterpret();
                    ei = ei.syntaxCopy();
                    sc = (sc).startCTFE();
                    ei = expressionSemantic(ei, sc);
                    sc = (sc).endCTFE();
                    ei = ei.implicitCastTo(sc, vt);
                    ei = ei.ctfeInterpret();
                    if (!(ei.equals(e)))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                }
                else
                {
                    if ((dedtypes).get(i) != null)
                    {
                        Expression e = (Expression)(dedtypes).get(i);
                        if ((!(ei != null) || !(ei.equals(e))))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                dedtypes.set(i, ei);
                if (psparam != null)
                {
                    Initializer _init = new ExpInitializer(this.loc, ei);
                    Declaration sparam = new VarDeclaration(this.loc, vt, this.ident, _init, 0L);
                    sparam.storage_class = 8388608L;
                    psparam.set(0, sparam);
                }
                return this.dependent ? MATCH.exact : m;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
                psparam.set(0, null);
            return MATCH.nomatch;
        }

        public  Object dummyArg() {
            Expression e = this.specValue;
            if (!(e != null))
            {
                Ptr<Expression> pe = pcopy(this.valType in edummies);
                if (pe == null)
                {
                    e = defaultInit(this.valType, Loc.initial);
                    edummies.set((this.valType), __aaval1243);
                }
                else
                    e = pe.get();
            }
            return e;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateValueParameter() {}

        public TemplateValueParameter copy() {
            TemplateValueParameter that = new TemplateValueParameter();
            that.valType = this.valType;
            that.specValue = this.specValue;
            that.defaultValue = this.defaultValue;
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateAliasParameter extends TemplateParameter
    {
        public Type specType;
        public RootObject specAlias;
        public RootObject defaultAlias;
        public static Dsymbol sdummy = null;
        public  TemplateAliasParameter(Loc loc, Identifier ident, Type specType, RootObject specAlias, RootObject defaultAlias) {
            super(loc, ident);
            this.specType = specType;
            this.specAlias = specAlias;
            this.defaultAlias = defaultAlias;
        }

        public  TemplateAliasParameter isTemplateAliasParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateAliasParameter(this.loc, this.ident, this.specType != null ? this.specType.syntaxCopy() : null, objectSyntaxCopy(this.specAlias), objectSyntaxCopy(this.defaultAlias));
        }

        public  boolean declareParameter(Scope sc) {
            TypeIdentifier ti = new TypeIdentifier(this.loc, this.ident);
            Declaration ad = new AliasDeclaration(this.loc, this.ident, ti);
            return (sc).insert(ad) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s\n"), this.ident.toChars());
            Dsymbol sa = isDsymbol(oded);
            assert(sa != null);
            printf(new BytePtr("\u0009Parameter alias: %s\n"), sa.toChars());
        }

        public  RootObject specialization() {
            return this.specAlias;
        }

        public  RootObject defaultArg(Loc instLoc, Scope sc) {
            RootObject da = this.defaultAlias;
            Type ta = isType(this.defaultAlias);
            if (ta != null)
            {
                if ((ta.ty & 0xFF) == ENUMTY.Tinstance)
                {
                    da = ta.syntaxCopy();
                }
            }
            RootObject o = aliasParameterSemantic(this.loc, sc, da, null);
            return o;
        }

        public  boolean hasDefaultArg() {
            return this.defaultAlias != null;
        }

        public  int matchArg(Scope sc, RootObject oarg, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam) {
            int m = MATCH.exact;
            Type ta = isType(oarg);
            RootObject sa = (ta != null && ta.deco == null) ? null : getDsymbol(oarg);
            Expression ea = isExpression(oarg);
            if ((ea != null && ((ea.op & 0xFF) == 123 || (ea.op & 0xFF) == 124)))
                sa = ((ThisExp)ea).var;
            else if ((ea != null && (ea.op & 0xFF) == 203))
                sa = ((ScopeExp)ea).sds;
            try {
                if (sa != null)
                {
                    if (((Dsymbol)sa).isAggregateDeclaration() != null)
                        m = MATCH.convert;
                    if (this.specType != null)
                    {
                        Declaration d = ((Dsymbol)sa).isDeclaration();
                        if (!(d != null))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        if (!(d.type.equals(this.specType)))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                else
                {
                    sa = oarg;
                    if (ea != null)
                    {
                        if (this.specType != null)
                        {
                            if (!(ea.type.equals(this.specType)))
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                    }
                    else if (((ta != null && (ta.ty & 0xFF) == ENUMTY.Tinstance) && !(this.specAlias != null)))
                    {
                    }
                    else if ((sa != null && pequals(sa, TemplateTypeParameter.tdummy)))
                    {
                    }
                    else if (ta != null)
                    {
                        m = MATCH.convert;
                    }
                    else
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                }
                if (this.specAlias != null)
                {
                    if (pequals(sa, sdummy))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    Dsymbol sx = isDsymbol(sa);
                    if ((!pequals(sa, this.specAlias) && sx != null))
                    {
                        Type talias = isType(this.specAlias);
                        if (!(talias != null))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        TemplateInstance ti = sx.isTemplateInstance();
                        if ((!(ti != null) && sx.parent != null))
                        {
                            ti = sx.parent.isTemplateInstance();
                            if ((ti != null && !pequals(ti.name, sx.ident)))
                                /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        }
                        if (!(ti != null))
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                        Type t = new TypeInstance(Loc.initial, ti);
                        int m2 = deduceType(t, sc, talias, parameters, dedtypes, null, 0, false);
                        if (m2 <= MATCH.nomatch)
                            /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                    }
                }
                else if ((dedtypes).get(i) != null)
                {
                    RootObject si = (dedtypes).get(i);
                    if ((!(sa != null) || !pequals(si, sa)))
                        /*goto Lnomatch*/throw Dispatch0.INSTANCE;
                }
                dedtypes.set(i, sa);
                if (psparam != null)
                {
                    {
                        Dsymbol s = isDsymbol(sa);
                        if (s != null)
                        {
                            psparam.set(0, (new AliasDeclaration(this.loc, this.ident, s)));
                        }
                        else {
                            Type t = isType(sa);
                            if (t != null)
                            {
                                psparam.set(0, (new AliasDeclaration(this.loc, this.ident, t)));
                            }
                            else
                            {
                                assert(ea != null);
                                Initializer _init = new ExpInitializer(this.loc, ea);
                                VarDeclaration v = new VarDeclaration(this.loc, null, this.ident, _init, 0L);
                                v.storage_class = 8388608L;
                                dsymbolSemantic(v, sc);
                                psparam.set(0, v);
                            }
                        }
                    }
                }
                return this.dependent ? MATCH.exact : m;
            }
            catch(Dispatch0 __d){}
        /*Lnomatch:*/
            if (psparam != null)
                psparam.set(0, null);
            return MATCH.nomatch;
        }

        public  Object dummyArg() {
            RootObject s = this.specAlias;
            if (!(s != null))
            {
                if (!(sdummy != null))
                    sdummy = new Dsymbol();
                s = sdummy;
            }
            return s;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateAliasParameter() {}

        public TemplateAliasParameter copy() {
            TemplateAliasParameter that = new TemplateAliasParameter();
            that.specType = this.specType;
            that.specAlias = this.specAlias;
            that.defaultAlias = this.defaultAlias;
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateTupleParameter extends TemplateParameter
    {
        public  TemplateTupleParameter(Loc loc, Identifier ident) {
            super(loc, ident);
        }

        public  TemplateTupleParameter isTemplateTupleParameter() {
            return this;
        }

        public  TemplateParameter syntaxCopy() {
            return new TemplateTupleParameter(this.loc, this.ident);
        }

        public  boolean declareParameter(Scope sc) {
            TypeIdentifier ti = new TypeIdentifier(this.loc, this.ident);
            Declaration ad = new AliasDeclaration(this.loc, this.ident, ti);
            return (sc).insert(ad) != null;
        }

        public  void print(RootObject oarg, RootObject oded) {
            printf(new BytePtr(" %s... ["), this.ident.toChars());
            Tuple v = isTuple(oded);
            assert(v != null);
            {
                int i = 0;
                for (; i < v.objects.length;i++){
                    if ((i) != 0)
                        printf(new BytePtr(", "));
                    RootObject o = v.objects.get(i);
                    Dsymbol sa = isDsymbol(o);
                    if (sa != null)
                        printf(new BytePtr("alias: %s"), sa.toChars());
                    Type ta = isType(o);
                    if (ta != null)
                        printf(new BytePtr("type: %s"), ta.toChars());
                    Expression ea = isExpression(o);
                    if (ea != null)
                        printf(new BytePtr("exp: %s"), ea.toChars());
                    assert(!(isTuple(o) != null));
                }
            }
            printf(new BytePtr("]\n"));
        }

        public  RootObject specialization() {
            return null;
        }

        public  RootObject defaultArg(Loc instLoc, Scope sc) {
            return null;
        }

        public  boolean hasDefaultArg() {
            return false;
        }

        public  int matchArg(Loc instLoc, Scope sc, DArray<RootObject> tiargs, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam) {
            assert(i + 1 == (dedtypes).length);
            Tuple ovar = null;
            {
                Tuple u = isTuple((dedtypes).get(i));
                if (u != null)
                {
                    ovar = u;
                }
                else if ((i + 1 == (tiargs).length && isTuple((tiargs).get(i)) != null))
                    ovar = isTuple((tiargs).get(i));
                else
                {
                    ovar = new Tuple();
                    if (i < (tiargs).length)
                    {
                        ovar.objects.setDim((tiargs).length - i);
                        {
                            int j = 0;
                            for (; j < ovar.objects.length;j++) {
                                ovar.objects.set(j, (tiargs).get(i + j));
                            }
                        }
                    }
                }
            }
            return this.matchArg(sc, ovar, i, parameters, dedtypes, psparam);
        }

        public  int matchArg(Scope sc, RootObject oarg, int i, DArray<TemplateParameter> parameters, DArray<RootObject> dedtypes, Ptr<Declaration> psparam) {
            Tuple ovar = isTuple(oarg);
            if (!(ovar != null))
                return MATCH.nomatch;
            if ((dedtypes).get(i) != null)
            {
                Tuple tup = isTuple((dedtypes).get(i));
                if (!(tup != null))
                    return MATCH.nomatch;
                if (!(match(tup, ovar)))
                    return MATCH.nomatch;
            }
            dedtypes.set(i, ovar);
            if (psparam != null)
                psparam.set(0, (new TupleDeclaration(this.loc, this.ident, ovar.objects)));
            return this.dependent ? MATCH.exact : MATCH.convert;
        }

        public  Object dummyArg() {
            return null;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateTupleParameter() {}

        public TemplateTupleParameter copy() {
            TemplateTupleParameter that = new TemplateTupleParameter();
            that.loc = this.loc;
            that.ident = this.ident;
            that.dependent = this.dependent;
            return that;
        }
    }
    public static class TemplateInstance extends ScopeDsymbol
    {
        public Identifier name;
        public DArray<RootObject> tiargs;
        public DArray<RootObject> tdtypes = new DArray<RootObject>();
        public DArray<dmodule.Module> importedModules = new DArray<dmodule.Module>();
        public Dsymbol tempdecl;
        public Dsymbol enclosing;
        public Dsymbol aliasdecl;
        public TemplateInstance inst;
        public ScopeDsymbol argsym;
        public int inuse;
        public int nest;
        public boolean semantictiargsdone;
        public boolean havetempdecl;
        public boolean gagged;
        public int hash;
        public DArray<Expression> fargs;
        public DArray<TemplateInstance> deferred;
        public dmodule.Module memberOf;
        public TemplateInstance tinst;
        public TemplateInstance tnext;
        public dmodule.Module minst;
        public  TemplateInstance(Loc loc, Identifier ident, DArray<RootObject> tiargs) {
            super(loc, null);
            this.name = ident;
            this.tiargs = tiargs;
        }

        public  TemplateInstance(Loc loc, TemplateDeclaration td, DArray<RootObject> tiargs) {
            super(loc, null);
            this.name = td.ident;
            this.tiargs = tiargs;
            this.tempdecl = td;
            this.semantictiargsdone = true;
            this.havetempdecl = true;
            assert(this.tempdecl._scope != null);
        }

        public static DArray<RootObject> arraySyntaxCopy(DArray<RootObject> objs) {
            DArray<RootObject> a = null;
            if (objs != null)
            {
                a = new DArray<RootObject>((objs).length);
                {
                    int i = 0;
                    for (; i < (objs).length;i++) {
                        a.set(i, objectSyntaxCopy((objs).get(i)));
                    }
                }
            }
            return a;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            TemplateInstance ti = s != null ? (TemplateInstance)s : new TemplateInstance(this.loc, this.name, null);
            ti.tiargs = arraySyntaxCopy(this.tiargs);
            TemplateDeclaration td = null;
            if (((this.inst != null && this.tempdecl != null) && (td = this.tempdecl.isTemplateDeclaration()) != null))
                td.syntaxCopy(ti);
            else
                this.syntaxCopy(ti);
            return ti;
        }

        public  Dsymbol toAlias() {
            if (!(this.inst != null))
            {
                if (this._scope != null)
                {
                    dsymbolSemantic(this, this._scope);
                }
                if (!(this.inst != null))
                {
                    this.error(new BytePtr("cannot resolve forward reference"));
                    this.errors = true;
                    return this;
                }
            }
            if (!pequals(this.inst, this))
                return this.inst.toAlias();
            if (this.aliasdecl != null)
            {
                return this.aliasdecl.toAlias();
            }
            return this.inst;
        }

        public  BytePtr kind() {
            return new BytePtr("template instance");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            ps.set(0, null);
            return true;
        }

        public  BytePtr toChars() {
            OutBuffer buf = new OutBuffer();
            try {
                toCBufferInstance(this, buf, false);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  BytePtr toPrettyCharsHelper() {
            OutBuffer buf = new OutBuffer();
            try {
                toCBufferInstance(this, buf, true);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  void printInstantiationTrace() {
            if ((global.gag) != 0)
                return ;
            int max_shown = 6;
            BytePtr format = pcopy(new BytePtr("instantiated from here: `%s`"));
            int n_instantiations = 1;
            int n_totalrecursions = 0;
            {
                TemplateInstance cur = this;
                for (; cur != null;cur = cur.tinst){
                    n_instantiations += 1;
                    if ((((cur.tinst != null && cur.tempdecl != null) && cur.tinst.tempdecl != null) && cur.tempdecl.loc.equals(cur.tinst.tempdecl.loc)))
                        n_totalrecursions += 1;
                }
            }
            if ((n_instantiations <= 6 || global.params.verbose))
            {
                {
                    TemplateInstance cur = this;
                    for (; cur != null;cur = cur.tinst){
                        cur.errors = true;
                        errorSupplemental(cur.loc, format, cur.toChars());
                    }
                }
            }
            else if ((n_instantiations - n_totalrecursions) <= 6)
            {
                int recursionDepth = 0;
                {
                    TemplateInstance cur = this;
                    for (; cur != null;cur = cur.tinst){
                        cur.errors = true;
                        if ((((cur.tinst != null && cur.tempdecl != null) && cur.tinst.tempdecl != null) && cur.tempdecl.loc.equals(cur.tinst.tempdecl.loc)))
                        {
                            recursionDepth += 1;
                        }
                        else
                        {
                            if ((recursionDepth) != 0)
                                errorSupplemental(cur.loc, new BytePtr("%d recursive instantiations from here: `%s`"), recursionDepth + 2, cur.toChars());
                            else
                                errorSupplemental(cur.loc, format, cur.toChars());
                            recursionDepth = 0;
                        }
                    }
                }
            }
            else
            {
                int i = 0;
                {
                    TemplateInstance cur = this;
                    for (; cur != null;cur = cur.tinst){
                        cur.errors = true;
                        if (i == 3)
                            errorSupplemental(cur.loc, new BytePtr("... (%d instantiations, -v to show) ..."), n_instantiations - 6);
                        if ((i < 3 || i >= n_instantiations - 6 + 3))
                            errorSupplemental(cur.loc, format, cur.toChars());
                        i += 1;
                    }
                }
            }
        }

        public  Identifier getIdent() {
            if (((!(this.ident != null) && this.inst != null) && !(this.errors)))
                this.ident = this.genIdent(this.tiargs);
            return this.ident;
        }

        public  boolean equalsx(TemplateInstance ti) {
            assert(this.tdtypes.length == ti.tdtypes.length);
            try {
                if (!pequals(this.enclosing, ti.enclosing))
                {
                    /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                }
                if (!(arrayObjectMatch(this.tdtypes, ti.tdtypes)))
                    /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                {
                    FuncDeclaration fd = ti.toAlias().isFuncDeclaration();
                    if (fd != null)
                    {
                        if (!(fd.errors))
                        {
                            ParameterList fparameters = fd.getParameterList().copy();
                            int nfparams = fparameters.length();
                            {
                                int j = 0;
                            L_outer19:
                                for (; j < nfparams;j++){
                                    Parameter fparam = fparameters.get(j);
                                    if ((fparam.storageClass & 35184372088832L) != 0)
                                    {
                                        Expression farg = (this.fargs != null && j < (this.fargs).length) ? (this.fargs).get(j) : fparam.defaultArg;
                                        if (!(farg != null))
                                            /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                                        if (farg.isLvalue())
                                        {
                                            if (!((fparam.storageClass & 2097152L) != 0))
                                                /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                                        }
                                        else
                                        {
                                            if ((fparam.storageClass & 2097152L) != 0)
                                                /*goto Lnotequals*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                return true;
            }
            catch(Dispatch0 __d){}
        /*Lnotequals:*/
            return false;
        }

        public  int toHash() {
            if (!((this.hash) != 0))
            {
                this.hash = (int)this.enclosing;
                this.hash += arrayObjectHash(this.tdtypes);
                this.hash += ((this.hash == 0) ? 1 : 0);
            }
            return this.hash;
        }

        public  boolean needsCodegen() {
            if (global.params.allInst)
            {
                if (this.enclosing != null)
                {
                    if (!(this.enclosing.isFuncDeclaration() != null))
                        return true;
                    {
                        TemplateInstance ti = this.enclosing.isInstantiated();
                        if (ti != null)
                            return ti.needsCodegen();
                    }
                    return !(this.enclosing.inNonRoot());
                }
                return true;
            }
            if (!(this.minst != null))
            {
                TemplateInstance tnext = this.tnext;
                TemplateInstance tinst = this.tinst;
                this.tnext = null;
                this.tinst = null;
                if ((tinst != null && tinst.needsCodegen()))
                {
                    this.minst = tinst.minst;
                    assert(this.minst != null);
                    assert((this.minst.isRoot() || this.minst.rootImports()));
                    return true;
                }
                if ((tnext != null && (tnext.needsCodegen() || tnext.minst != null)))
                {
                    this.minst = tnext.minst;
                    assert(this.minst != null);
                    return (this.minst.isRoot() || this.minst.rootImports());
                }
                return false;
            }
            if ((this.enclosing != null && this.enclosing.inNonRoot()))
            {
                if (this.tinst != null)
                {
                    boolean r = this.tinst.needsCodegen();
                    this.minst = this.tinst.minst;
                    return r;
                }
                if (this.tnext != null)
                {
                    boolean r = this.tnext.needsCodegen();
                    this.minst = this.tnext.minst;
                    return r;
                }
                return false;
            }
            if ((global.params.useUnitTests || (global.params.debuglevel) != 0))
            {
                if (this.minst.isRoot())
                    return true;
                TemplateInstance tnext = this.tnext;
                TemplateInstance tinst = this.tinst;
                this.tnext = null;
                this.tinst = null;
                if ((tinst != null && tinst.needsCodegen()))
                {
                    this.minst = tinst.minst;
                    assert(this.minst != null);
                    assert((this.minst.isRoot() || this.minst.rootImports()));
                    return true;
                }
                if ((tnext != null && tnext.needsCodegen()))
                {
                    this.minst = tnext.minst;
                    assert(this.minst != null);
                    assert((this.minst.isRoot() || this.minst.rootImports()));
                    return true;
                }
                if (this.minst.rootImports())
                    return true;
                return false;
            }
            else
            {
                if ((!(this.minst.isRoot()) && !(this.minst.rootImports())))
                    return false;
                TemplateInstance tnext = this.tnext;
                this.tnext = null;
                if (((tnext != null && !(tnext.needsCodegen())) && tnext.minst != null))
                {
                    this.minst = tnext.minst;
                    assert(!(this.minst.isRoot()));
                    return false;
                }
                return true;
            }
        }

        public  boolean findTempDecl(Scope sc, Ptr<WithScopeSymbol> pwithsym) {
            if (pwithsym != null)
                pwithsym.set(0, null);
            if (this.havetempdecl)
                return true;
            if (!(this.tempdecl != null))
            {
                Identifier id = this.name;
                Ref<Dsymbol> scopesym = ref(null);
                Dsymbol s = (sc).search(this.loc, id, ptr(scopesym), 0);
                if (!(s != null))
                {
                    s = (sc).search_correct(id);
                    if (s != null)
                        this.error(new BytePtr("template `%s` is not defined, did you mean %s?"), id.toChars(), s.toChars());
                    else
                        this.error(new BytePtr("template `%s` is not defined"), id.toChars());
                    return false;
                }
                if (pwithsym != null)
                    pwithsym.set(0, scopesym.value.isWithScopeSymbol());
                TemplateInstance ti = null;
                if ((s.parent != null && (ti = s.parent.isTemplateInstance()) != null))
                {
                    if ((ti.tempdecl != null && pequals(ti.tempdecl.ident, id)))
                    {
                        TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                        assert(td != null);
                        if (td.overroot != null)
                            td = td.overroot;
                        s = td;
                    }
                }
                if (!(this.updateTempDecl(sc, s)))
                {
                    return false;
                }
            }
            assert(this.tempdecl != null);
            OverloadSet tovers = this.tempdecl.isOverloadSet();
            {
                int __key1244 = 0;
                int __limit1245 = tovers != null ? tovers.a.length : 1;
                Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                    public Integer invoke(Dsymbol s){
                        TemplateDeclaration td = s.isTemplateDeclaration();
                        if (!(td != null))
                            return 0;
                        if (td.semanticRun == PASS.init)
                        {
                            if (td._scope != null)
                            {
                                Ungag ungag = td.ungagSpeculative().copy();
                                try {
                                    dsymbolSemantic(td, td._scope);
                                }
                                finally {
                                }
                            }
                            if (td.semanticRun == PASS.init)
                            {
                                error(new BytePtr("`%s` forward references template declaration `%s`"), toChars(), td.toChars());
                                return 1;
                            }
                        }
                        return 0;
                    }
                };
                for (; __key1244 < __limit1245;__key1244 += 1) {
                    int oi = __key1244;
                    Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl;
                    int r = overloadApply(dstart, __lambda3, null);
                    if ((r) != 0)
                        return false;
                }
            }
            return true;
        }

        public  boolean updateTempDecl(Scope sc, Dsymbol s) {
            if (s != null)
            {
                Identifier id = this.name;
                s = s.toAlias();
                OverloadSet os = s.isOverloadSet();
                if (os != null)
                {
                    s = null;
                    {
                        int i = 0;
                        for (; i < os.a.length;i++){
                            Dsymbol s2 = os.a.get(i);
                            {
                                FuncDeclaration f = s2.isFuncDeclaration();
                                if (f != null)
                                    s2 = f.findTemplateDeclRoot();
                                else
                                    s2 = s2.isTemplateDeclaration();
                            }
                            if (s2 != null)
                            {
                                if (s != null)
                                {
                                    this.tempdecl = os;
                                    return true;
                                }
                                s = s2;
                            }
                        }
                    }
                    if (!(s != null))
                    {
                        this.error(new BytePtr("template `%s` is not defined"), id.toChars());
                        return false;
                    }
                }
                OverDeclaration od = s.isOverDeclaration();
                if (od != null)
                {
                    this.tempdecl = od;
                    return true;
                }
                {
                    FuncDeclaration f = s.isFuncDeclaration();
                    if (f != null)
                        this.tempdecl = f.findTemplateDeclRoot();
                    else
                        this.tempdecl = s.isTemplateDeclaration();
                }
                if (!(this.tempdecl != null))
                {
                    if ((!(s.parent != null) && (global.errors) != 0))
                        return false;
                    if ((!(s.parent != null) && s.getType() != null))
                    {
                        Dsymbol s2 = s.getType().toDsymbol(sc);
                        if (!(s2 != null))
                        {
                            error(this.loc, new BytePtr("`%s` is not a valid template instance, because `%s` is not a template declaration but a type (`%s == %s`)"), this.toChars(), id.toChars(), id.toChars(), s.getType().kind());
                            return false;
                        }
                        s = s2;
                    }
                    TemplateInstance ti = s.parent != null ? s.parent.isTemplateInstance() : null;
                    if (((ti != null && (pequals(ti.name, s.ident) || pequals(ti.toAlias().ident, s.ident))) && ti.tempdecl != null))
                    {
                        TemplateDeclaration td = ti.tempdecl.isTemplateDeclaration();
                        assert(td != null);
                        if (td.overroot != null)
                            td = td.overroot;
                        this.tempdecl = td;
                    }
                    else
                    {
                        this.error(new BytePtr("`%s` is not a template declaration, it is a %s"), id.toChars(), s.kind());
                        return false;
                    }
                }
            }
            return this.tempdecl != null;
        }

        public static boolean semanticTiargs(Loc loc, Scope sc, DArray<RootObject> tiargs, int flags) {
            if (tiargs == null)
                return true;
            boolean err = false;
            {
                int j = 0;
            L_outer20:
                for (; j < (tiargs).length;j++){
                    RootObject o = (tiargs).get(j);
                    Ref<Type> ta = ref(isType(o));
                    Ref<Expression> ea = ref(isExpression(o));
                    Ref<Dsymbol> sa = ref(isDsymbol(o));
                    if (ta.value != null)
                    {
                        resolve(ta.value, loc, sc, ptr(ea), ptr(ta), ptr(sa), (flags & 1) != 0);
                        if (ea.value != null)
                            /*goto Lexpr*/throw Dispatch.INSTANCE;
                        if (sa.value != null)
                            /*goto Ldsym*/throw Dispatch.INSTANCE;
                        if (ta.value == null)
                        {
                            assert((global.errors) != 0);
                            ta.value = Type.terror;
                        }
                    /*Ltype:*/
                        if ((ta.value.ty & 0xFF) == ENUMTY.Ttuple)
                        {
                            TypeTuple tt = (TypeTuple)ta.value;
                            int dim = (tt.arguments).length;
                            (tiargs).remove(j);
                            if ((dim) != 0)
                            {
                                (tiargs).reserve(dim);
                                {
                                    int i = 0;
                                    for (; i < dim;i++){
                                        Parameter arg = (tt.arguments).get(i);
                                        if (((flags & 2) != 0 && (arg.ident != null || arg.userAttribDecl != null)))
                                            (tiargs).insert(j + i, arg);
                                        else
                                            (tiargs).insert(j + i, arg.type);
                                    }
                                }
                            }
                            j--;
                            continue L_outer20;
                        }
                        if ((ta.value.ty & 0xFF) == ENUMTY.Terror)
                        {
                            err = true;
                            continue L_outer20;
                        }
                        tiargs.set(j, ta.value.merge2());
                    }
                    else if (ea.value != null)
                    {
                    /*Lexpr:*/
                        if ((flags & 1) != 0)
                        {
                            ea.value = expressionSemantic(ea.value, sc);
                            if (((ea.value.op & 0xFF) != 26 || (((VarExp)ea.value).var.storage_class & 262144L) != 0))
                            {
                                ea.value = ea.value.optimize(0, false);
                            }
                        }
                        else
                        {
                            sc = (sc).startCTFE();
                            ea.value = expressionSemantic(ea.value, sc);
                            sc = (sc).endCTFE();
                            if ((ea.value.op & 0xFF) == 26)
                            {
                            }
                            else if (definitelyValueParameter(ea.value))
                            {
                                if (ea.value.checkValue())
                                    ea.value = new ErrorExp();
                                int olderrs = global.errors;
                                ea.value = ea.value.ctfeInterpret();
                                if (global.errors != olderrs)
                                    ea.value = new ErrorExp();
                            }
                        }
                        if ((ea.value.op & 0xFF) == 126)
                        {
                            TupleExp te = (TupleExp)ea.value;
                            int dim = (te.exps).length;
                            (tiargs).remove(j);
                            if ((dim) != 0)
                            {
                                (tiargs).reserve(dim);
                                {
                                    int i = 0;
                                    for (; i < dim;i++) {
                                        (tiargs).insert(j + i, (te.exps).get(i));
                                    }
                                }
                            }
                            j--;
                            continue L_outer20;
                        }
                        if ((ea.value.op & 0xFF) == 127)
                        {
                            err = true;
                            continue L_outer20;
                        }
                        tiargs.set(j, ea.value);
                        if ((ea.value.op & 0xFF) == 20)
                        {
                            ta.value = ea.value.type;
                            /*goto Ltype*/throw Dispatch0.INSTANCE;
                        }
                        if ((ea.value.op & 0xFF) == 203)
                        {
                            sa.value = ((ScopeExp)ea.value).sds;
                            /*goto Ldsym*/throw Dispatch.INSTANCE;
                        }
                        if ((ea.value.op & 0xFF) == 161)
                        {
                            FuncExp fe = (FuncExp)ea.value;
                            if (((fe.fd.tok & 0xFF) == 0 && (fe.type.ty & 0xFF) == ENUMTY.Tpointer))
                            {
                                fe.fd.tok = TOK.function_;
                                fe.fd.vthis = null;
                            }
                            else if (fe.td != null)
                            {
                            }
                        }
                        if (((ea.value.op & 0xFF) == 27 && !((flags & 1) != 0)))
                        {
                            sa.value = ((DotVarExp)ea.value).var;
                            /*goto Ldsym*/throw Dispatch.INSTANCE;
                        }
                        if ((ea.value.op & 0xFF) == 36)
                        {
                            sa.value = ((TemplateExp)ea.value).td;
                            /*goto Ldsym*/throw Dispatch.INSTANCE;
                        }
                        if (((ea.value.op & 0xFF) == 37 && !((flags & 1) != 0)))
                        {
                            sa.value = ((DotTemplateExp)ea.value).td;
                            /*goto Ldsym*/throw Dispatch.INSTANCE;
                        }
                    }
                    else if (sa.value != null)
                    {
                    /*Ldsym:*/
                        if (sa.value.errors)
                        {
                            err = true;
                            continue L_outer20;
                        }
                        TupleDeclaration d = sa.value.toAlias().isTupleDeclaration();
                        if (d != null)
                        {
                            (tiargs).remove(j);
                            (tiargs).insert(j, d.objects);
                            j--;
                            continue L_outer20;
                        }
                        {
                            FuncAliasDeclaration fa = sa.value.isFuncAliasDeclaration();
                            if (fa != null)
                            {
                                FuncDeclaration f = fa.toAliasFunc();
                                if ((!(fa.hasOverloads) && f.isUnique()))
                                {
                                    sa.value = f;
                                }
                            }
                        }
                        tiargs.set(j, sa.value);
                        TemplateDeclaration td = sa.value.isTemplateDeclaration();
                        if (((td != null && td.semanticRun == PASS.init) && td.literal))
                        {
                            dsymbolSemantic(td, sc);
                        }
                        FuncDeclaration fd = sa.value.isFuncDeclaration();
                        if (fd != null)
                            fd.functionSemantic();
                    }
                    else if (isParameter(o) != null)
                    {
                    }
                    else
                    {
                        throw new AssertionError("Unreachable code!");
                    }
                }
            }
            return !(err);
        }

        public  boolean semanticTiargs(Scope sc) {
            if (this.semantictiargsdone)
                return true;
            if (semanticTiargs(this.loc, sc, this.tiargs, 0))
            {
                this.semantictiargsdone = true;
                return true;
            }
            return false;
        }

        public  boolean findBestMatch(Scope sc, DArray<Expression> fargs) {
            if (this.havetempdecl)
            {
                TemplateDeclaration tempdecl = this.tempdecl.isTemplateDeclaration();
                assert(tempdecl != null);
                assert(tempdecl._scope != null);
                this.tdtypes.setDim((tempdecl.parameters).length);
                if (!((tempdecl.matchWithInstance(sc, this, this.tdtypes, fargs, 2)) != 0))
                {
                    this.error(new BytePtr("incompatible arguments for template instantiation"));
                    return false;
                }
                return true;
            }
            int errs = global.errors;
            TemplateDeclaration td_last = null;
            DArray<RootObject> dedtypes = new DArray<RootObject>();
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (!(td != null))
                        return 0;
                    if ((td.inuse) != 0)
                    {
                        td.error(loc, new BytePtr("recursive template expansion"));
                        return 1;
                    }
                    if (pequals(td, td_best))
                        return 0;
                    if ((td.parameters).length < (tiargs).length)
                    {
                        if (!(td.isVariadic() != null))
                            return 0;
                    }
                    dedtypes.setDim((td.parameters).length);
                    dedtypes.zero();
                    assert(td.semanticRun != PASS.init);
                    int m = td.matchWithInstance(sc, this, dedtypes, fargs, 0);
                    if (m <= MATCH.nomatch)
                        return 0;
                    try {
                        if (m < m_best)
                            /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                        try {
                            if (m > m_best)
                                /*goto Ltd*/throw Dispatch1.INSTANCE;
                            {
                                int c1 = td.leastAsSpecialized(sc, td_best, fargs);
                                int c2 = td_best.leastAsSpecialized(sc, td, fargs);
                                if (c1 > c2)
                                    /*goto Ltd*/throw Dispatch1.INSTANCE;
                                if (c1 < c2)
                                    /*goto Ltd_best*/throw Dispatch0.INSTANCE;
                            }
                            td_ambig = td;
                            return 0;
                        }
                        catch(Dispatch0 __d){}
                    /*Ltd_best:*/
                        td_ambig = null;
                        return 0;
                    }
                    catch(Dispatch1 __d){}
                /*Ltd:*/
                    td_ambig = null;
                    td_best = td;
                    m_best = m;
                    tdtypes.setDim(dedtypes.length);
                    memcpy((BytePtr)(tdtypes.tdata()), (dedtypes.tdata()), (tdtypes.length * 4));
                    return 0;
                }
            };
            try {
                OverloadSet tovers = this.tempdecl.isOverloadSet();
                {
                    int __key1246 = 0;
                    int __limit1247 = tovers != null ? tovers.a.length : 1;
                    for (; __key1246 < __limit1247;__key1246 += 1) {
                        int oi = __key1246;
                        TemplateDeclaration td_best = null;
                        TemplateDeclaration td_ambig = null;
                        int m_best = MATCH.nomatch;
                        Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl;
                        overloadApply(dstart, __lambda3, null);
                        if (td_ambig != null)
                        {
                            error(this.loc, new BytePtr("%s `%s.%s` matches more than one template declaration:\n%s:     `%s`\nand\n%s:     `%s`"), td_best.kind(), td_best.parent.toPrettyChars(false), td_best.ident.toChars(), td_best.loc.toChars(global.params.showColumns), td_best.toChars(), td_ambig.loc.toChars(global.params.showColumns), td_ambig.toChars());
                            return false;
                        }
                        if (td_best != null)
                        {
                            if (!(td_last != null))
                                td_last = td_best;
                            else if (!pequals(td_last, td_best))
                            {
                                ScopeDsymbol.multiplyDefined(this.loc, td_last, td_best);
                                return false;
                            }
                        }
                    }
                }
                if (td_last != null)
                {
                    int dim = (td_last.parameters).length - (td_last.isVariadic() != null ? 1 : 0);
                    {
                        int i = 0;
                        for (; i < dim;i++){
                            if ((this.tiargs).length <= i)
                                (this.tiargs).push(this.tdtypes.get(i));
                            assert(i < (this.tiargs).length);
                            TemplateValueParameter tvp = (td_last.parameters).get(i).isTemplateValueParameter();
                            if (!(tvp != null))
                                continue;
                            assert(this.tdtypes.get(i) != null);
                            this.tiargs.set(i, this.tdtypes.get(i));
                        }
                    }
                    if (((td_last.isVariadic() != null && (this.tiargs).length == dim) && this.tdtypes.get(dim) != null))
                    {
                        Tuple va = isTuple(this.tdtypes.get(dim));
                        assert(va != null);
                        (this.tiargs).pushSlice(va.objects.opSlice());
                    }
                }
                else if ((this.errors && this.inst != null))
                {
                    assert((global.errors) != 0);
                    return false;
                }
                else
                {
                    TemplateDeclaration tdecl = this.tempdecl.isTemplateDeclaration();
                    if (errs != global.errors)
                        errorSupplemental(this.loc, new BytePtr("while looking for match for `%s`"), this.toChars());
                    else if ((tdecl != null && !(tdecl.overnext != null)))
                    {
                        this.error(new BytePtr("does not match template declaration `%s`"), tdecl.toChars());
                    }
                    else
                        error(this.loc, new BytePtr("%s `%s.%s` does not match any template declaration"), this.tempdecl.kind(), this.tempdecl.parent.toPrettyChars(false), this.tempdecl.ident.toChars());
                    return false;
                }
                this.tempdecl = td_last;
                return errs == global.errors;
            }
            finally {
            }
        }

        public  boolean needsTypeInference(Scope sc, int flag) {
            if (this.semanticRun != PASS.init)
                return false;
            int olderrs = global.errors;
            DArray<RootObject> dedtypes = new DArray<RootObject>();
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    TemplateDeclaration td = s.isTemplateDeclaration();
                    if (!(td != null))
                        return 0;
                    if ((td.inuse) != 0)
                    {
                        td.error(loc, new BytePtr("recursive template expansion"));
                        return 1;
                    }
                    if (!(td.onemember != null))
                        return 0;
                    {
                        TemplateDeclaration td2 = td.onemember.isTemplateDeclaration();
                        if (td2 != null)
                        {
                            if ((!(td2.onemember != null) || !(td2.onemember.isFuncDeclaration() != null)))
                                return 0;
                            if ((tiargs).length >= (td.parameters).length - (td.isVariadic() != null ? 1 : 0))
                                return 0;
                            return 1;
                        }
                    }
                    FuncDeclaration fd = td.onemember.isFuncDeclaration();
                    if ((!(fd != null) || (fd.type.ty & 0xFF) != ENUMTY.Tfunction))
                        return 0;
                    {
                        Slice<TemplateParameter> __r1250 = (td.parameters).opSlice().copy();
                        int __key1251 = 0;
                        for (; __key1251 < __r1250.getLength();__key1251 += 1) {
                            TemplateParameter tp = __r1250.get(__key1251);
                            if (tp.isTemplateThisParameter() != null)
                                return 1;
                        }
                    }
                    TypeFunction tf = (TypeFunction)fd.type;
                    {
                        int dim = tf.parameterList.length();
                        if ((dim) != 0)
                        {
                            TemplateTupleParameter tp = td.isVariadic();
                            if ((tp != null && (td.parameters).length > 1))
                                return 1;
                            if ((!(tp != null) && (tiargs).length < (td.parameters).length))
                            {
                                {
                                    int __key1252 = (tiargs).length;
                                    int __limit1253 = (td.parameters).length;
                                    for (; __key1252 < __limit1253;__key1252 += 1) {
                                        int i = __key1252;
                                        if (!((td.parameters).get(i).hasDefaultArg()))
                                            return 1;
                                    }
                                }
                            }
                            {
                                int __key1254 = 0;
                                int __limit1255 = dim;
                                for (; __key1254 < __limit1255;__key1254 += 1) {
                                    int i = __key1254;
                                    if ((tf.parameterList.get(i).storageClass & 256L) != 0)
                                        return 1;
                                }
                            }
                        }
                    }
                    if (!((flag) != 0))
                    {
                        dedtypes.setDim((td.parameters).length);
                        dedtypes.zero();
                        if (td.semanticRun == PASS.init)
                        {
                            if (td._scope != null)
                            {
                                Ungag ungag = td.ungagSpeculative().copy();
                                try {
                                    dsymbolSemantic(td, td._scope);
                                }
                                finally {
                                }
                            }
                            if (td.semanticRun == PASS.init)
                            {
                                error(new BytePtr("`%s` forward references template declaration `%s`"), toChars(), td.toChars());
                                return 1;
                            }
                        }
                        int m = td.matchWithInstance(sc, this, dedtypes, null, 0);
                        if (m <= MATCH.nomatch)
                            return 0;
                    }
                    return (count += 1) > 1 ? 1 : 0;
                }
            };
            try {
                int count = 0;
                OverloadSet tovers = this.tempdecl.isOverloadSet();
                {
                    int __key1248 = 0;
                    int __limit1249 = tovers != null ? tovers.a.length : 1;
                    for (; __key1248 < __limit1249;__key1248 += 1) {
                        int oi = __key1248;
                        Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl;
                        int r = overloadApply(dstart, __lambda3, null);
                        if ((r) != 0)
                            return true;
                    }
                }
                if (olderrs != global.errors)
                {
                    if (!((global.gag) != 0))
                    {
                        errorSupplemental(this.loc, new BytePtr("while looking for match for `%s`"), this.toChars());
                        this.semanticRun = PASS.semanticdone;
                        this.inst = this;
                    }
                    this.errors = true;
                }
                return false;
            }
            finally {
            }
        }

        public  boolean hasNestedArgs(DArray<RootObject> args, boolean isstatic) {
            int nested = 0;
            if (!(this.enclosing != null))
            {
                {
                    TemplateInstance ti = this.tempdecl.toParent().isTemplateInstance();
                    if (ti != null)
                        this.enclosing = ti.enclosing;
                }
            }
            {
                int i = 0;
            L_outer21:
                for (; i < (args).length;i++){
                    RootObject o = (args).get(i);
                    Expression ea = isExpression(o);
                    Dsymbol sa = isDsymbol(o);
                    Tuple va = isTuple(o);
                    if (ea != null)
                    {
                        if ((ea.op & 0xFF) == 26)
                        {
                            sa = ((VarExp)ea).var;
                            /*goto Lsa*/throw Dispatch.INSTANCE;
                        }
                        if ((ea.op & 0xFF) == 123)
                        {
                            sa = ((ThisExp)ea).var;
                            /*goto Lsa*/throw Dispatch.INSTANCE;
                        }
                        if ((ea.op & 0xFF) == 161)
                        {
                            if (((FuncExp)ea).td != null)
                                sa = ((FuncExp)ea).td;
                            else
                                sa = ((FuncExp)ea).fd;
                            /*goto Lsa*/throw Dispatch.INSTANCE;
                        }
                        if (((((((((ea.op & 0xFF) != 135 && (ea.op & 0xFF) != 140) && (ea.op & 0xFF) != 147) && (ea.op & 0xFF) != 13) && (ea.op & 0xFF) != 121) && (ea.op & 0xFF) != 47) && (ea.op & 0xFF) != 48) && (ea.op & 0xFF) != 49))
                        {
                            ea.error(new BytePtr("expression `%s` is not a valid template value argument"), ea.toChars());
                            this.errors = true;
                        }
                    }
                    else if (sa != null)
                    {
                    /*Lsa:*/
                        sa = sa.toAlias();
                        TemplateDeclaration td = sa.isTemplateDeclaration();
                        if (td != null)
                        {
                            TemplateInstance ti = sa.toParent().isTemplateInstance();
                            if ((ti != null && ti.enclosing != null))
                                sa = ti;
                        }
                        TemplateInstance ti = sa.isTemplateInstance();
                        Declaration d = sa.isDeclaration();
                        if ((((td != null && td.literal) || (ti != null && ti.enclosing != null)) || ((((d != null && !(d.isDataseg())) && !((d.storage_class & 8388608L) != 0)) && (!(d.isFuncDeclaration() != null) || d.isFuncDeclaration().isNested())) && !(this.isTemplateMixin() != null))))
                        {
                            Dsymbol dparent = sa.toParent2();
                            try {
                                if (!(dparent != null))
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                else if (!(this.enclosing != null))
                                    this.enclosing = dparent;
                                else if (!pequals(this.enclosing, dparent))
                                {
                                    {
                                        Dsymbol p = this.enclosing;
                                    L_outer22:
                                        for (; p != null;p = p.parent){
                                            if (pequals(p, dparent))
                                                /*goto L1*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                    {
                                        Dsymbol p = dparent;
                                    L_outer23:
                                        for (; p != null;p = p.parent){
                                            if (pequals(p, this.enclosing))
                                            {
                                                this.enclosing = dparent;
                                                /*goto L1*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                    this.error(new BytePtr("`%s` is nested in both `%s` and `%s`"), this.toChars(), this.enclosing.toChars(), dparent.toChars());
                                    this.errors = true;
                                }
                            }
                            catch(Dispatch0 __d){}
                        /*L1:*/
                            nested |= 1;
                        }
                    }
                    else if (va != null)
                    {
                        nested |= (this.hasNestedArgs(va.objects, isstatic) ? 1 : 0);
                    }
                }
            }
            return nested != 0;
        }

        public  DArray<Dsymbol> appendToModuleMember() {
            dmodule.Module mi = this.minst;
            if ((global.params.useUnitTests || (global.params.debuglevel) != 0))
            {
                if ((mi != null && !(mi.isRoot())))
                    mi = null;
            }
            if ((!(mi != null) || mi.isRoot()))
            {
                Function1<TemplateInstance,Dsymbol> getStrictEnclosing = new Function1<TemplateInstance,Dsymbol>(){
                    public Dsymbol invoke(TemplateInstance ti){
                        do {
                            {
                                if (ti.enclosing != null)
                                    return ti.enclosing;
                                ti = ti.tempdecl.isInstantiated();
                            }
                        } while (ti != null);
                        return null;
                    }
                };
                Dsymbol enc = getStrictEnclosing.invoke(this);
                mi = (enc != null ? enc : this.tempdecl).getModule();
                if (!(mi.isRoot()))
                    mi = mi.importedFrom;
                assert(mi.isRoot());
            }
            else
            {
            }
            if (this.memberOf == mi)
            {
                return null;
            }
            DArray<Dsymbol> a = mi.members;
            (a).push(this);
            this.memberOf = mi;
            if ((mi.semanticRun >= PASS.semantic2done && mi.isRoot()))
                dmodule.Module.addDeferredSemantic2(this);
            if ((mi.semanticRun >= PASS.semantic3done && mi.isRoot()))
                dmodule.Module.addDeferredSemantic3(this);
            return a;
        }

        public  void declareParameters(Scope sc) {
            TemplateDeclaration tempdecl = this.tempdecl.isTemplateDeclaration();
            assert(tempdecl != null);
            {
                int i = 0;
                for (; i < this.tdtypes.length;i++){
                    TemplateParameter tp = (tempdecl.parameters).get(i);
                    RootObject o = this.tdtypes.get(i);
                    tempdecl.declareParameter(sc, tp, o);
                }
            }
        }

        public  Identifier genIdent(DArray<RootObject> args) {
            assert(args == this.tiargs);
            OutBuffer buf = new OutBuffer();
            try {
                mangleToBuffer(this, buf);
                return Identifier.idPool(buf.peekSlice());
            }
            finally {
            }
        }

        public  void expandMembers(Scope sc2) {
            Ref<Scope> sc2_ref = ref(sc2);
            Function1<Dsymbol,Void> __lambda2 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.setScope(sc2_ref.value);
                    return null;
                }
            };
            foreachDsymbol(this.members, __lambda2);
            Function1<Dsymbol,Void> __lambda3 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.importAll(sc2_ref.value);
                    return null;
                }
            };
            foreachDsymbol(this.members, __lambda3);
            Function1<Dsymbol,Void> symbolDg = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    dsymbolSemantic(s, sc2_ref.value);
                    dmodule.Module.runDeferredSemantic();
                    return null;
                }
            };
            foreachDsymbol(this.members, symbolDg);
        }

        public  void tryExpandMembers(Scope sc2) {
            if ((dtemplate.tryExpandMembersnest += 1) > 500)
            {
                global.gag = 0;
                this.error(new BytePtr("recursive expansion"));
                fatal();
            }
            this.expandMembers(sc2);
            dtemplate.tryExpandMembersnest--;
        }

        public  void trySemantic3(Scope sc2) {
            if ((dtemplate.trySemantic3nest += 1) > 300)
            {
                global.gag = 0;
                this.error(new BytePtr("recursive expansion"));
                fatal();
            }
            semantic3(this, sc2);
            dtemplate.trySemantic3nest -= 1;
        }

        public  TemplateInstance isTemplateInstance() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateInstance() {}

        public TemplateInstance copy() {
            TemplateInstance that = new TemplateInstance();
            that.name = this.name;
            that.tiargs = this.tiargs;
            that.tdtypes = this.tdtypes;
            that.importedModules = this.importedModules;
            that.tempdecl = this.tempdecl;
            that.enclosing = this.enclosing;
            that.aliasdecl = this.aliasdecl;
            that.inst = this.inst;
            that.argsym = this.argsym;
            that.inuse = this.inuse;
            that.nest = this.nest;
            that.semantictiargsdone = this.semantictiargsdone;
            that.havetempdecl = this.havetempdecl;
            that.gagged = this.gagged;
            that.hash = this.hash;
            that.fargs = this.fargs;
            that.deferred = this.deferred;
            that.memberOf = this.memberOf;
            that.tinst = this.tinst;
            that.tnext = this.tnext;
            that.minst = this.minst;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
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
    public static void unSpeculative(Scope sc, RootObject o) {
        if (!(o != null))
            return ;
        {
            Tuple tup = isTuple(o);
            if (tup != null)
            {
                {
                    int i = 0;
                    for (; i < tup.objects.length;i++){
                        unSpeculative(sc, tup.objects.get(i));
                    }
                }
                return ;
            }
        }
        Dsymbol s = getDsymbol(o);
        if (!(s != null))
            return ;
        {
            Declaration d = s.isDeclaration();
            if (d != null)
            {
                {
                    VarDeclaration vd = d.isVarDeclaration();
                    if (vd != null)
                        o = vd.type;
                    else {
                        AliasDeclaration ad = d.isAliasDeclaration();
                        if (ad != null)
                        {
                            o = ad.getType();
                            if (!(o != null))
                                o = ad.toAlias();
                        }
                        else
                            o = d.toAlias();
                    }
                }
                s = getDsymbol(o);
                if (!(s != null))
                    return ;
            }
        }
        {
            TemplateInstance ti = s.isTemplateInstance();
            if (ti != null)
            {
                if ((ti.minst != null || (sc).minst == null))
                    return ;
                ti.minst = (sc).minst;
                if (!(ti.tinst != null))
                    ti.tinst = (sc).tinst;
                unSpeculative(sc, ti.tempdecl);
            }
        }
        {
            TemplateInstance ti = s.isInstantiated();
            if (ti != null)
                unSpeculative(sc, ti);
        }
    }

    public static boolean definitelyValueParameter(Expression e) {
        if (((((((((((e.op & 0xFF) == 126 || (e.op & 0xFF) == 203) || (e.op & 0xFF) == 20) || (e.op & 0xFF) == 30) || (e.op & 0xFF) == 36) || (e.op & 0xFF) == 37) || (e.op & 0xFF) == 161) || (e.op & 0xFF) == 127) || (e.op & 0xFF) == 123) || (e.op & 0xFF) == 124))
            return false;
        if ((e.op & 0xFF) != 27)
            return true;
        FuncDeclaration f = ((DotVarExp)e).var.isFuncDeclaration();
        if (f != null)
            return false;
        for (; (e.op & 0xFF) == 27;){
            e = ((DotVarExp)e).e1;
        }
        if (((e.op & 0xFF) == 123 || (e.op & 0xFF) == 124))
            return false;
        if ((e.op & 0xFF) == 30)
            return false;
        if ((e.op & 0xFF) != 26)
            return true;
        VarDeclaration v = ((VarExp)e).var.isVarDeclaration();
        if (!(v != null))
            return true;
        if ((v.storage_class & 8388608L) != 0)
            return true;
        return false;
    }

    public static class TemplateMixin extends TemplateInstance
    {
        public TypeQualified tqual;
        public  TemplateMixin(Loc loc, Identifier ident, TypeQualified tqual, DArray<RootObject> tiargs) {
            super(loc, (tqual.idents.length) != 0 ? (Identifier)tqual.idents.get(tqual.idents.length - 1) : ((TypeIdentifier)tqual).ident, tiargs != null ? tiargs : new DArray<RootObject>());
            this.ident = ident;
            this.tqual = tqual;
        }

        public  Dsymbol syntaxCopy(Dsymbol s) {
            TemplateMixin tm = new TemplateMixin(this.loc, this.ident, (TypeQualified)this.tqual.syntaxCopy(), this.tiargs);
            return this.syntaxCopy(tm);
        }

        public  BytePtr kind() {
            return new BytePtr("mixin");
        }

        public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
            return this.oneMember(ps, ident);
        }

        public  int apply(Function2<Dsymbol,Object,Integer> fp, Object param) {
            if (this._scope != null)
                dsymbolSemantic(this, null);
            Function1<Dsymbol,Integer> __lambda3 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    return (((s != null && (s.apply(fp, param)) != 0)) ? 1 : 0);
                }
            };
            return foreachDsymbol(this.members, __lambda3);
        }

        public  boolean hasPointers() {
            Function1<Dsymbol,Integer> __lambda1 = new Function1<Dsymbol,Integer>(){
                public Integer invoke(Dsymbol s){
                    return (s.hasPointers() ? 1 : 0);
                }
            };
            return foreachDsymbol(this.members, __lambda1) != 0;
        }

        public  void setFieldOffset(AggregateDeclaration ad, IntPtr poffset, boolean isunion) {
            if (this._scope != null)
                dsymbolSemantic(this, null);
            Function1<Dsymbol,Void> __lambda4 = new Function1<Dsymbol,Void>(){
                public Void invoke(Dsymbol s){
                    s.setFieldOffset(ad, poffset, isunion);
                    return null;
                }
            };
            foreachDsymbol(this.members, __lambda4);
        }

        public  BytePtr toChars() {
            OutBuffer buf = new OutBuffer();
            try {
                toCBufferInstance(this, buf, false);
                return buf.extractChars();
            }
            finally {
            }
        }

        public  boolean findTempDecl(Scope sc) {
            if (!(this.tempdecl != null))
            {
                Ref<Expression> e = ref(null);
                Ref<Type> t = ref(null);
                Ref<Dsymbol> s = ref(null);
                resolve(this.tqual, this.loc, sc, ptr(e), ptr(t), ptr(s), false);
                if (!(s.value != null))
                {
                    this.error(new BytePtr("is not defined"));
                    return false;
                }
                s.value = s.value.toAlias();
                this.tempdecl = s.value.isTemplateDeclaration();
                OverloadSet os = s.value.isOverloadSet();
                if (os != null)
                {
                    Dsymbol ds = null;
                    {
                        int i = 0;
                        for (; i < os.a.length;i++){
                            Dsymbol s2 = os.a.get(i).isTemplateDeclaration();
                            if (s2 != null)
                            {
                                if (ds != null)
                                {
                                    this.tempdecl = os;
                                    break;
                                }
                                ds = s2;
                            }
                        }
                    }
                }
                if (!(this.tempdecl != null))
                {
                    this.error(new BytePtr("`%s` isn't a template"), s.value.toChars());
                    return false;
                }
            }
            assert(this.tempdecl != null);
            OverloadSet tovers = this.tempdecl.isOverloadSet();
            {
                int __key1256 = 0;
                int __limit1257 = tovers != null ? tovers.a.length : 1;
                Function1<Dsymbol,Integer> __lambda2 = new Function1<Dsymbol,Integer>(){
                    public Integer invoke(Dsymbol s){
                        TemplateDeclaration td = s.isTemplateDeclaration();
                        if (!(td != null))
                            return 0;
                        if (td.semanticRun == PASS.init)
                        {
                            if (td._scope != null)
                                dsymbolSemantic(td, td._scope);
                            else
                            {
                                semanticRun = PASS.init;
                                return 1;
                            }
                        }
                        return 0;
                    }
                };
                for (; __key1256 < __limit1257;__key1256 += 1) {
                    int oi = __key1256;
                    Dsymbol dstart = tovers != null ? tovers.a.get(oi) : this.tempdecl;
                    int r = overloadApply(dstart, __lambda2, null);
                    if ((r) != 0)
                        return false;
                }
            }
            return true;
        }

        public  TemplateMixin isTemplateMixin() {
            return this;
        }

        public  void accept(Visitor v) {
            v.visit(this);
        }


        public TemplateMixin() {}

        public TemplateMixin copy() {
            TemplateMixin that = new TemplateMixin();
            that.tqual = this.tqual;
            that.name = this.name;
            that.tiargs = this.tiargs;
            that.tdtypes = this.tdtypes;
            that.importedModules = this.importedModules;
            that.tempdecl = this.tempdecl;
            that.enclosing = this.enclosing;
            that.aliasdecl = this.aliasdecl;
            that.inst = this.inst;
            that.argsym = this.argsym;
            that.inuse = this.inuse;
            that.nest = this.nest;
            that.semantictiargsdone = this.semantictiargsdone;
            that.havetempdecl = this.havetempdecl;
            that.gagged = this.gagged;
            that.hash = this.hash;
            that.fargs = this.fargs;
            that.deferred = this.deferred;
            that.memberOf = this.memberOf;
            that.tinst = this.tinst;
            that.tnext = this.tnext;
            that.minst = this.minst;
            that.members = this.members;
            that.symtab = this.symtab;
            that.endlinnum = this.endlinnum;
            that.importedScopes = this.importedScopes;
            that.prots = this.prots;
            that.accessiblePackages = this.accessiblePackages;
            that.privateAccessiblePackages = this.privateAccessiblePackages;
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
    public static class TemplateInstanceBox
    {
        public TemplateInstance ti;
        public  TemplateInstanceBox(TemplateInstance ti) {
            this.ti = ti;
            this.ti.toHash();
            assert((this.ti.hash) != 0);
        }

        public  int toHash() {
            assert((this.ti.hash) != 0);
            return this.ti.hash;
        }

        public  boolean opEquals(TemplateInstanceBox s) {
            boolean res = null;
            if ((this.ti.inst != null && s.ti.inst != null))
                res = this.ti == s.ti;
            else
                res = s.ti.equalsx(this.ti);
            return res;
        }

        public TemplateInstanceBox(){
        }
        public TemplateInstanceBox copy(){
            TemplateInstanceBox r = new TemplateInstanceBox();
            r.ti = ti;
            return r;
        }
        public TemplateInstanceBox opAssign(TemplateInstanceBox that) {
            this.ti = that.ti;
            return this;
        }
    }
}
