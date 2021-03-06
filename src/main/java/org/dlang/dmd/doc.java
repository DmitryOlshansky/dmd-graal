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
import static org.dlang.dmd.attrib.*;
import static org.dlang.dmd.cond.*;
import static org.dlang.dmd.dclass.*;
import static org.dlang.dmd.declaration.*;
import static org.dlang.dmd.denum.*;
import static org.dlang.dmd.dimport.*;
import static org.dlang.dmd.dmacro.*;
import static org.dlang.dmd.dmodule.*;
import static org.dlang.dmd.dscope.*;
import static org.dlang.dmd.dstruct.*;
import static org.dlang.dmd.dsymbol.*;
import static org.dlang.dmd.dsymbolsem.*;
import static org.dlang.dmd.dtemplate.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.func.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.hdrgen.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.mtype.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;
import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.visitor.*;

public class doc {
    static Slice<ByteSlice> writetable = slice(new ByteSlice[]{new ByteSlice("AUTHORS"), new ByteSlice("BUGS"), new ByteSlice("COPYRIGHT"), new ByteSlice("DATE"), new ByteSlice("DEPRECATED"), new ByteSlice("EXAMPLES"), new ByteSlice("HISTORY"), new ByteSlice("LICENSE"), new ByteSlice("RETURNS"), new ByteSlice("SEE_ALSO"), new ByteSlice("STANDARDS"), new ByteSlice("THROWS"), new ByteSlice("VERSION")});
    static OutBuffer gendocfilembuf = new OutBuffer();
    static int gendocfilembuf_done = 0;
    private static class EmitComment extends Visitor
    {
        private OutBuffer buf = null;
        private Ptr<Scope> sc = null;
        // Erasure: __ctor<Ptr, Ptr>
        public  EmitComment(OutBuffer buf, Ptr<Scope> sc) {
            this.buf = pcopy(buf);
            this.sc = pcopy(sc);
        }

        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol _param_0) {
        }

        // Erasure: visit<InvariantDeclaration>
        public  void visit(InvariantDeclaration _param_0) {
        }

        // Erasure: visit<UnitTestDeclaration>
        public  void visit(UnitTestDeclaration _param_0) {
        }

        // Erasure: visit<PostBlitDeclaration>
        public  void visit(PostBlitDeclaration _param_0) {
        }

        // Erasure: visit<DtorDeclaration>
        public  void visit(DtorDeclaration _param_0) {
        }

        // Erasure: visit<StaticCtorDeclaration>
        public  void visit(StaticCtorDeclaration _param_0) {
        }

        // Erasure: visit<StaticDtorDeclaration>
        public  void visit(StaticDtorDeclaration _param_0) {
        }

        // Erasure: visit<TypeInfoDeclaration>
        public  void visit(TypeInfoDeclaration _param_0) {
        }

        // Erasure: emit<Ptr, Dsymbol, Ptr>
        public  void emit(Ptr<Scope> sc, Dsymbol s, BytePtr com) {
            if ((s != null) && ((sc.get()).lastdc != null) && isDitto(com))
            {
                ((sc.get()).lastdc.get()).a.value.push(s);
                return ;
            }
            {
                Ptr<DocComment> dc = (sc.get()).lastdc;
                if ((dc) != null)
                {
                    assertMsg(((dc.get()).a.value.length > 0), new ByteSlice("Expects at least one declaration for adocumentation comment"));
                    Dsymbol symbol = (dc.get()).a.value.get(0);
                    (this.buf).writestring(new ByteSlice("$(DDOC_MEMBER"));
                    (this.buf).writestring(new ByteSlice("$(DDOC_MEMBER_HEADER"));
                    emitAnchor(this.buf, symbol, sc, true);
                    (this.buf).writeByte(41);
                    (this.buf).writestring(ddoc_decl_s);
                    {
                        int i = 0;
                        for (; (i < (dc.get()).a.value.length);i++){
                            Dsymbol sx = (dc.get()).a.value.get(i);
                            if ((i == 0))
                            {
                                int o = (this.buf).offset;
                                toDocBuffer(sx, this.buf, sc);
                                highlightCode(sc, sx, this.buf, o);
                                (this.buf).writestring(new ByteSlice("$(DDOC_OVERLOAD_SEPARATOR)"));
                                continue;
                            }
                            (this.buf).writestring(new ByteSlice("$(DDOC_DITTO "));
                            {
                                int o = (this.buf).offset;
                                toDocBuffer(sx, this.buf, sc);
                                highlightCode(sc, sx, this.buf, o);
                            }
                            (this.buf).writestring(new ByteSlice("$(DDOC_OVERLOAD_SEPARATOR)"));
                            (this.buf).writeByte(41);
                        }
                    }
                    (this.buf).writestring(ddoc_decl_e);
                    (this.buf).writestring(ddoc_decl_dd_s);
                    {
                        (dc.get()).writeSections(sc, (dc.get()).a.value, this.buf);
                        {
                            ScopeDsymbol sds = (dc.get()).a.value.get(0).isScopeDsymbol();
                            if ((sds) != null)
                            {
                                emitMemberComments(sds, this.buf, sc);
                            }
                        }
                    }
                    (this.buf).writestring(ddoc_decl_dd_e);
                    (this.buf).writeByte(41);
                }
            }
            if (s != null)
            {
                Ptr<DocComment> dc = DocComment.parse(s, com);
                (dc.get()).pmacrotable = pcopy(ptr((sc.get())._module.macrotable));
                (sc.get()).lastdc = pcopy(dc);
            }
        }

        // Erasure: visit<Import>
        public  void visit(Import imp) {
            if ((imp.prot().kind != Prot.Kind.public_) && ((this.sc.get()).protection.kind != Prot.Kind.export_))
            {
                return ;
            }
            this.emit(this.sc, imp, imp.comment);
        }

        // Erasure: visit<Declaration>
        public  void visit(Declaration d) {
            BytePtr com = pcopy(d.comment);
            {
                TemplateDeclaration td = getEponymousParent(d);
                if ((td) != null)
                {
                    if (isDitto(td.comment))
                    {
                        com = pcopy(td.comment);
                    }
                    else
                    {
                        com = pcopy(Lexer.combineComments(td.comment, com, true));
                    }
                }
                else
                {
                    if (d.ident == null)
                    {
                        return ;
                    }
                    if (d.type == null)
                    {
                        if ((d.isCtorDeclaration() == null) && (d.isAliasDeclaration() == null) && (d.isVarDeclaration() == null))
                        {
                            return ;
                        }
                    }
                    if ((d.protection.kind == Prot.Kind.private_) || ((this.sc.get()).protection.kind == Prot.Kind.private_))
                    {
                        return ;
                    }
                }
            }
            if (com == null)
            {
                return ;
            }
            this.emit(this.sc, d, com);
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(AggregateDeclaration ad) {
            BytePtr com = pcopy(ad.comment);
            {
                TemplateDeclaration td = getEponymousParent(ad);
                if ((td) != null)
                {
                    if (isDitto(td.comment))
                    {
                        com = pcopy(td.comment);
                    }
                    else
                    {
                        com = pcopy(Lexer.combineComments(td.comment, com, true));
                    }
                }
                else
                {
                    if ((ad.prot().kind == Prot.Kind.private_) || ((this.sc.get()).protection.kind == Prot.Kind.private_))
                    {
                        return ;
                    }
                    if (ad.comment == null)
                    {
                        return ;
                    }
                }
            }
            if (com == null)
            {
                return ;
            }
            this.emit(this.sc, ad, com);
        }

        // Erasure: visit<TemplateDeclaration>
        public  void visit(TemplateDeclaration td) {
            if ((td.prot().kind == Prot.Kind.private_) || ((this.sc.get()).protection.kind == Prot.Kind.private_))
            {
                return ;
            }
            if (td.comment == null)
            {
                return ;
            }
            {
                Dsymbol ss = getEponymousMember(td);
                if ((ss) != null)
                {
                    ss.accept(this);
                    return ;
                }
            }
            this.emit(this.sc, td, td.comment);
        }

        // Erasure: visit<EnumDeclaration>
        public  void visit(EnumDeclaration ed) {
            if ((ed.prot().kind == Prot.Kind.private_) || ((this.sc.get()).protection.kind == Prot.Kind.private_))
            {
                return ;
            }
            if (ed.isAnonymous() && (ed.members != null))
            {
                {
                    int i = 0;
                    for (; (i < (ed.members).length);i++){
                        Dsymbol s = (ed.members).get(i);
                        emitComment(s, this.buf, this.sc);
                    }
                }
                return ;
            }
            if (ed.comment == null)
            {
                return ;
            }
            if (ed.isAnonymous())
            {
                return ;
            }
            this.emit(this.sc, ed, ed.comment);
        }

        // Erasure: visit<EnumMember>
        public  void visit(EnumMember em) {
            if ((em.prot().kind == Prot.Kind.private_) || ((this.sc.get()).protection.kind == Prot.Kind.private_))
            {
                return ;
            }
            if (em.comment == null)
            {
                return ;
            }
            this.emit(this.sc, em, em.comment);
        }

        // Erasure: visit<AttribDeclaration>
        public  void visit(AttribDeclaration ad) {
            DArray<Dsymbol> d = ad.include(null);
            if (d != null)
            {
                {
                    int i = 0;
                    for (; (i < (d).length);i++){
                        Dsymbol s = (d).get(i);
                        emitComment(s, this.buf, this.sc);
                    }
                }
            }
        }

        // Erasure: visit<ProtDeclaration>
        public  void visit(ProtDeclaration pd) {
            if (pd.decl != null)
            {
                Ptr<Scope> scx = this.sc;
                this.sc = pcopy((this.sc.get()).copy());
                (this.sc.get()).protection.opAssign(pd.protection.copy());
                this.visit((AttribDeclaration)pd);
                (scx.get()).lastdc = pcopy((this.sc.get()).lastdc);
                this.sc = pcopy((this.sc.get()).pop());
            }
        }

        // Erasure: visit<ConditionalDeclaration>
        public  void visit(ConditionalDeclaration cd) {
            if ((cd.condition.inc != Include.notComputed))
            {
                this.visit((AttribDeclaration)cd);
                return ;
            }
            DArray<Dsymbol> d = cd.decl != null ? cd.decl : cd.elsedecl;
            {
                int i = 0;
                for (; (i < (d).length);i++){
                    Dsymbol s = (d).get(i);
                    emitComment(s, this.buf, this.sc);
                }
            }
        }


        public EmitComment() {}
    }
    private static class ToDocBuffer extends Visitor
    {
        private OutBuffer buf = null;
        private Ptr<Scope> sc = null;
        // Erasure: __ctor<Ptr, Ptr>
        public  ToDocBuffer(OutBuffer buf, Ptr<Scope> sc) {
            this.buf = pcopy(buf);
            this.sc = pcopy(sc);
        }

        // Erasure: visit<Dsymbol>
        public  void visit(Dsymbol s) {
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            hgs.value.ddoc = true;
            toCBuffer(s, this.buf, ptr(hgs));
        }

        // Erasure: prefix<Dsymbol>
        public  void prefix(Dsymbol s) {
            if (s.isDeprecated())
            {
                (this.buf).writestring(new ByteSlice("deprecated "));
            }
            {
                Declaration d = s.isDeclaration();
                if ((d) != null)
                {
                    emitProtection(this.buf, d);
                    if (d.isStatic())
                    {
                        (this.buf).writestring(new ByteSlice("static "));
                    }
                    else if (d.isFinal())
                    {
                        (this.buf).writestring(new ByteSlice("final "));
                    }
                    else if (d.isAbstract())
                    {
                        (this.buf).writestring(new ByteSlice("abstract "));
                    }
                    if (d.isFuncDeclaration() != null)
                    {
                        return ;
                    }
                    if (d.isImmutable())
                    {
                        (this.buf).writestring(new ByteSlice("immutable "));
                    }
                    if ((d.storage_class & 536870912L) != 0)
                    {
                        (this.buf).writestring(new ByteSlice("shared "));
                    }
                    if (d.isWild())
                    {
                        (this.buf).writestring(new ByteSlice("inout "));
                    }
                    if (d.isConst())
                    {
                        (this.buf).writestring(new ByteSlice("const "));
                    }
                    if (d.isSynchronized())
                    {
                        (this.buf).writestring(new ByteSlice("synchronized "));
                    }
                    if ((d.storage_class & 8388608L) != 0)
                    {
                        (this.buf).writestring(new ByteSlice("enum "));
                    }
                    if ((d.type == null) && (d.isVarDeclaration() != null) && !d.isImmutable() && ((d.storage_class & 536870912L) == 0) && !d.isWild() && !d.isConst() && !d.isSynchronized())
                    {
                        (this.buf).writestring(new ByteSlice("auto "));
                    }
                }
            }
        }

        // Erasure: visit<Import>
        public  void visit(Import i) {
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            hgs.value.ddoc = true;
            emitProtection(this.buf, i);
            toCBuffer((Dsymbol)i, this.buf, ptr(hgs));
        }

        // Erasure: visit<Declaration>
        public  void visit(Declaration d) {
            if (d.ident == null)
            {
                return ;
            }
            TemplateDeclaration td = getEponymousParent(d);
            Ref<HdrGenState> hgs = ref(new HdrGenState());
            hgs.value.ddoc = true;
            if (d.isDeprecated())
            {
                (this.buf).writestring(new ByteSlice("$(DEPRECATED "));
            }
            this.prefix(d);
            if (d.type != null)
            {
                Type origType = d.originalType != null ? d.originalType : d.type;
                if (((origType.ty & 0xFF) == ENUMTY.Tfunction))
                {
                    functionToBufferFull(((TypeFunction)origType), this.buf, d.ident, ptr(hgs), td);
                }
                else
                {
                    toCBuffer(origType, this.buf, d.ident, ptr(hgs));
                }
            }
            else
            {
                (this.buf).writestring(d.ident.asString());
            }
            if ((d.isVarDeclaration() != null) && (td != null))
            {
                (this.buf).writeByte(40);
                if ((td.origParameters != null) && ((td.origParameters).length != 0))
                {
                    {
                        int i = 0;
                        for (; (i < (td.origParameters).length);i++){
                            if (i != 0)
                            {
                                (this.buf).writestring(new ByteSlice(", "));
                            }
                            toCBuffer((td.origParameters).get(i), this.buf, ptr(hgs));
                        }
                    }
                }
                (this.buf).writeByte(41);
            }
            if ((td != null) && (td.constraint != null))
            {
                boolean noFuncDecl = td.isFuncDeclaration() == null;
                if (noFuncDecl)
                {
                    (this.buf).writestring(new ByteSlice("$(DDOC_CONSTRAINT "));
                }
                toCBuffer(td.constraint, this.buf, ptr(hgs));
                if (noFuncDecl)
                {
                    (this.buf).writestring(new ByteSlice(")"));
                }
            }
            if (d.isDeprecated())
            {
                (this.buf).writestring(new ByteSlice(")"));
            }
            (this.buf).writestring(new ByteSlice(";\n"));
        }

        // Erasure: visit<AliasDeclaration>
        public  void visit(AliasDeclaration ad) {
            if (ad.ident == null)
            {
                return ;
            }
            if (ad.isDeprecated())
            {
                (this.buf).writestring(new ByteSlice("deprecated "));
            }
            emitProtection(this.buf, (Declaration)ad);
            (this.buf).printf(new BytePtr("alias %s = "), ad.toChars());
            {
                Dsymbol s = ad.aliassym;
                if ((s) != null)
                {
                    this.prettyPrintDsymbol(s, ad.parent.value);
                }
                else {
                    Type type = ad.getType();
                    if ((type) != null)
                    {
                        if (((type.ty & 0xFF) == ENUMTY.Tclass) || ((type.ty & 0xFF) == ENUMTY.Tstruct) || ((type.ty & 0xFF) == ENUMTY.Tenum))
                        {
                            {
                                Dsymbol s = type.toDsymbol(null);
                                if ((s) != null)
                                {
                                    this.prettyPrintDsymbol(s, ad.parent.value);
                                }
                                else
                                {
                                    (this.buf).writestring(type.toChars());
                                }
                            }
                        }
                        else
                        {
                            (this.buf).writestring(type.toChars());
                        }
                    }
                }
            }
            (this.buf).writestring(new ByteSlice(";\n"));
        }

        // Erasure: parentToBuffer<Dsymbol>
        public  void parentToBuffer(Dsymbol s) {
            if ((s != null) && (s.isPackage() == null) && (s.isModule() == null))
            {
                this.parentToBuffer(s.parent.value);
                (this.buf).writestring(s.toChars());
                (this.buf).writestring(new ByteSlice("."));
            }
        }

        // Erasure: inSameModule<Dsymbol, Dsymbol>
        public static boolean inSameModule(Dsymbol s, Dsymbol p) {
            for (; s != null;s = s.parent.value){
                if (s.isModule() != null)
                {
                    break;
                }
            }
            for (; p != null;p = p.parent.value){
                if (p.isModule() != null)
                {
                    break;
                }
            }
            return pequals(s, p);
        }

        // Erasure: prettyPrintDsymbol<Dsymbol, Dsymbol>
        public  void prettyPrintDsymbol(Dsymbol s, Dsymbol parent) {
            if ((s.parent.value != null) && (pequals(s.parent.value, parent)))
            {
                (this.buf).writestring(s.toChars());
            }
            else if (!inSameModule(s, parent))
            {
                (this.buf).writestring(s.toPrettyChars(false));
            }
            else
            {
                if ((parent.isModule() == null) && (parent.isPackage() == null))
                {
                    (this.buf).writestring(new ByteSlice("."));
                }
                this.parentToBuffer(s.parent.value);
                (this.buf).writestring(s.toChars());
            }
        }

        // Erasure: visit<AggregateDeclaration>
        public  void visit(AggregateDeclaration ad) {
            if (ad.ident == null)
            {
                return ;
            }
            (this.buf).printf(new BytePtr("%s %s"), ad.kind(), ad.toChars());
            (this.buf).writestring(new ByteSlice(";\n"));
        }

        // Erasure: visit<StructDeclaration>
        public  void visit(StructDeclaration sd) {
            if (sd.ident == null)
            {
                return ;
            }
            {
                TemplateDeclaration td = getEponymousParent(sd);
                if ((td) != null)
                {
                    toDocBuffer(td, this.buf, this.sc);
                }
                else
                {
                    (this.buf).printf(new BytePtr("%s %s"), sd.kind(), sd.toChars());
                }
            }
            (this.buf).writestring(new ByteSlice(";\n"));
        }

        // Erasure: visit<ClassDeclaration>
        public  void visit(ClassDeclaration cd) {
            if (cd.ident == null)
            {
                return ;
            }
            {
                TemplateDeclaration td = getEponymousParent(cd);
                if ((td) != null)
                {
                    toDocBuffer(td, this.buf, this.sc);
                }
                else
                {
                    if ((cd.isInterfaceDeclaration() == null) && cd.isAbstract())
                    {
                        (this.buf).writestring(new ByteSlice("abstract "));
                    }
                    (this.buf).printf(new BytePtr("%s %s"), cd.kind(), cd.toChars());
                }
            }
            int any = 0;
            {
                int i = 0;
                for (; (i < (cd.baseclasses).length);i++){
                    Ptr<BaseClass> bc = (cd.baseclasses).get(i);
                    if (((bc.get()).sym != null) && (pequals((bc.get()).sym.ident, Id.Object)))
                    {
                        continue;
                    }
                    if (any != 0)
                    {
                        (this.buf).writestring(new ByteSlice(", "));
                    }
                    else
                    {
                        (this.buf).writestring(new ByteSlice(": "));
                        any = 1;
                    }
                    if ((bc.get()).sym != null)
                    {
                        (this.buf).printf(new BytePtr("$(DDOC_PSUPER_SYMBOL %s)"), (bc.get()).sym.toPrettyChars(false));
                    }
                    else
                    {
                        Ref<HdrGenState> hgs = ref(new HdrGenState());
                        toCBuffer((bc.get()).type, this.buf, (Identifier)null, ptr(hgs));
                    }
                }
            }
            (this.buf).writestring(new ByteSlice(";\n"));
        }

        // Erasure: visit<EnumDeclaration>
        public  void visit(EnumDeclaration ed) {
            if (ed.ident == null)
            {
                return ;
            }
            (this.buf).printf(new BytePtr("%s %s"), ed.kind(), ed.toChars());
            if (ed.memtype != null)
            {
                (this.buf).writestring(new ByteSlice(": $(DDOC_ENUM_BASETYPE "));
                Ref<HdrGenState> hgs = ref(new HdrGenState());
                toCBuffer(ed.memtype, this.buf, (Identifier)null, ptr(hgs));
                (this.buf).writestring(new ByteSlice(")"));
            }
            (this.buf).writestring(new ByteSlice(";\n"));
        }

        // Erasure: visit<EnumMember>
        public  void visit(EnumMember em) {
            if (em.ident == null)
            {
                return ;
            }
            (this.buf).writestring(em.toChars());
        }


        public ToDocBuffer() {}
    }
    static ByteSlice percentEncodehexDigits = new ByteSlice("0123456789ABCDEF");

    public static class Escape
    {
        public Slice<ByteSlice> strings = new RawSlice<ByteSlice>(new ByteSlice[255]);
        // Erasure: escapeChar<byte>
        public  ByteSlice escapeChar(byte c) {
            return this.strings.get((c & 0xFF));
        }

        public Escape(){ }
        public Escape copy(){
            Escape r = new Escape();
            r.strings = strings;
            return r;
        }
        public Escape(Slice<ByteSlice> strings) {
            this.strings = strings;
        }

        public Escape opAssign(Escape that) {
            this.strings = that.strings;
            return this;
        }
    }
    public static class Section extends Object
    {
        public BytePtr name = null;
        public int namelen = 0;
        public BytePtr _body = null;
        public int bodylen = 0;
        public int nooutput = 0;
        // Erasure: write<Loc, Ptr, Ptr, Ptr, Ptr>
        public  void write(Loc loc, Ptr<DocComment> dc, Ptr<Scope> sc, DArray<Dsymbol> a, OutBuffer buf) {
            assert((a).length != 0);
            try {
                if (this.namelen != 0)
                {
                    {
                        Slice<ByteSlice> __r1058 = doc.writetable.copy();
                        int __key1059 = 0;
                    L_outer1:
                        for (; (__key1059 < __r1058.getLength());__key1059 += 1) {
                            ByteSlice entry = __r1058.get(__key1059).copy();
                            if (iequals(toByteSlice(entry), this.name.slice(0,this.namelen)))
                            {
                                (buf).printf(new BytePtr("$(DDOC_%s "), entry.getPtr(0));
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                        }
                    }
                    (buf).writestring(new ByteSlice("$(DDOC_SECTION "));
                    (buf).writestring(new ByteSlice("$(DDOC_SECTION_H "));
                    int o = (buf).offset;
                    {
                        int u = 0;
                        for (; (u < this.namelen);u++){
                            byte c = this.name.get(u);
                            (buf).writeByte(((c & 0xFF) == 95) ? 32 : (c & 0xFF));
                        }
                    }
                    escapeStrayParenthesis(loc, buf, o, false);
                    (buf).writestring(new ByteSlice(")"));
                }
                else
                {
                    (buf).writestring(new ByteSlice("$(DDOC_DESCRIPTION "));
                }
            }
            catch(Dispatch0 __d){}
        /*L1:*/
            int o = (buf).offset;
            (buf).write(this._body, this.bodylen);
            escapeStrayParenthesis(loc, buf, o, true);
            highlightText(sc, a, loc, buf, o);
            (buf).writestring(new ByteSlice(")"));
        }


        public Section() {}

        public Section copy() {
            Section that = new Section();
            that.name = this.name;
            that.namelen = this.namelen;
            that._body = this._body;
            that.bodylen = this.bodylen;
            that.nooutput = this.nooutput;
            return that;
        }
    }
    public static class ParamSection extends Section
    {
        // Erasure: write<Loc, Ptr, Ptr, Ptr, Ptr>
        public  void write(Loc loc, Ptr<DocComment> dc, Ptr<Scope> sc, DArray<Dsymbol> a, OutBuffer buf) {
            assert((a).length != 0);
            Dsymbol s = (a).get(0);
            BytePtr p = pcopy(this._body);
            int len = this.bodylen;
            BytePtr pend = pcopy(p.plus(len));
            BytePtr tempstart = null;
            int templen = 0;
            BytePtr namestart = null;
            int namelen = 0;
            BytePtr textstart = null;
            int textlen = 0;
            int paramcount = 0;
            (buf).writestring(new ByteSlice("$(DDOC_PARAMS "));
        L_outer2:
            for (; (p.lessThan(pend));){
                try {
                    try {
                        try {
                        L_outer3:
                            for (; 1 != 0;){
                                {
                                    int __dispatch0 = 0;
                                    dispatched_0:
                                    do {
                                        switch (__dispatch0 != 0 ? __dispatch0 : (p.get() & 0xFF))
                                        {
                                            case 32:
                                            case 9:
                                                p.postInc();
                                                continue L_outer3;
                                            case 10:
                                                p.postInc();
                                                /*goto Lcont*/throw Dispatch1.INSTANCE;
                                            default:
                                            if (isIdStart(p) || isCVariadicArg(p.slice(0,((pend.minus(p))))))
                                            {
                                                break;
                                            }
                                            if (namelen != 0)
                                            {
                                                /*goto Ltext*/throw Dispatch0.INSTANCE;
                                            }
                                            /*goto Lskipline*/throw Dispatch2.INSTANCE;
                                        }
                                    } while(__dispatch0 != 0);
                                }
                                break;
                            }
                            tempstart = pcopy(p);
                            for (; isIdTail(p);) {
                                p.plusAssign(utfStride(p));
                            }
                            if (isCVariadicArg(p.slice(0,((pend.minus(p))))))
                            {
                                p.plusAssign(3);
                            }
                            templen = ((p.minus(tempstart)));
                            for (; ((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 9);) {
                                p.postInc();
                            }
                            if (((p.get() & 0xFF) != 61))
                            {
                                if (namelen != 0)
                                {
                                    /*goto Ltext*/throw Dispatch0.INSTANCE;
                                }
                                /*goto Lskipline*/throw Dispatch2.INSTANCE;
                            }
                            p.postInc();
                            if (namelen != 0)
                            {
                            /*L1:*/
                                paramcount += 1;
                                Ref<HdrGenState> hgs = ref(new HdrGenState());
                                (buf).writestring(new ByteSlice("$(DDOC_PARAM_ROW "));
                                {
                                    (buf).writestring(new ByteSlice("$(DDOC_PARAM_ID "));
                                    {
                                        int o = (buf).offset;
                                        Parameter fparam = isFunctionParameter(a, namestart, namelen);
                                        if (fparam == null)
                                        {
                                            fparam = isEponymousFunctionParameter(a, namestart, namelen);
                                        }
                                        boolean isCVariadic = isCVariadicParameter(a, namestart.slice(0,namelen));
                                        if (isCVariadic)
                                        {
                                            (buf).writestring(new ByteSlice("..."));
                                        }
                                        else if ((fparam != null) && (fparam.type != null) && (fparam.ident != null))
                                        {
                                            toCBuffer(fparam.type, buf, fparam.ident, ptr(hgs));
                                        }
                                        else
                                        {
                                            if (isTemplateParameter(a, namestart, namelen) != null)
                                            {
                                                paramcount -= 1;
                                            }
                                            else if (fparam == null)
                                            {
                                                warning(s.loc, new BytePtr("Ddoc: function declaration has no parameter '%.*s'"), namelen, namestart);
                                            }
                                            (buf).write(namestart, namelen);
                                        }
                                        escapeStrayParenthesis(loc, buf, o, true);
                                        highlightCode(sc, a, buf, o);
                                    }
                                    (buf).writestring(new ByteSlice(")"));
                                    (buf).writestring(new ByteSlice("$(DDOC_PARAM_DESC "));
                                    {
                                        int o = (buf).offset;
                                        (buf).write(textstart, textlen);
                                        escapeStrayParenthesis(loc, buf, o, true);
                                        highlightText(sc, a, loc, buf, o);
                                    }
                                    (buf).writestring(new ByteSlice(")"));
                                }
                                (buf).writestring(new ByteSlice(")"));
                                namelen = 0;
                                if ((p.greaterOrEqual(pend)))
                                {
                                    break;
                                }
                            }
                            namestart = pcopy(tempstart);
                            namelen = templen;
                            for (; ((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 9);) {
                                p.postInc();
                            }
                            textstart = pcopy(p);
                        }
                        catch(Dispatch0 __d){}
                    /*Ltext:*/
                        for (; ((p.get() & 0xFF) != 10);) {
                            p.postInc();
                        }
                        textlen = ((p.minus(textstart)));
                        p.postInc();
                    }
                    catch(Dispatch1 __d){}
                /*Lcont:*/
                    continue L_outer2;
                }
                catch(Dispatch2 __d){}
            /*Lskipline:*/
                for (; ((p.postInc().get() & 0xFF) != 10);){
                }
            }
            if (namelen != 0)
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
            (buf).writestring(new ByteSlice(")"));
            TypeFunction tf = ((a).length == 1) ? isTypeFunction(s) : null;
            if (tf != null)
            {
                int pcount = (tf.parameterList.parameters != null ? (tf.parameterList.parameters).length : 0) + ((tf.parameterList.varargs == VarArg.variadic) ? 1 : 0);
                if ((pcount != paramcount))
                {
                    warning(s.loc, new BytePtr("Ddoc: parameter count mismatch, expected %d, got %d"), pcount, paramcount);
                    if ((paramcount == 0))
                    {
                        warningSupplemental(s.loc, new BytePtr("Note that the format is `param = description`"));
                    }
                }
            }
        }


        public ParamSection() {}

        public ParamSection copy() {
            ParamSection that = new ParamSection();
            that.name = this.name;
            that.namelen = this.namelen;
            that._body = this._body;
            that.bodylen = this.bodylen;
            that.nooutput = this.nooutput;
            return that;
        }
    }
    public static class MacroSection extends Section
    {
        // Erasure: write<Loc, Ptr, Ptr, Ptr, Ptr>
        public  void write(Loc loc, Ptr<DocComment> dc, Ptr<Scope> sc, DArray<Dsymbol> a, OutBuffer buf) {
            DocComment.parseMacros((dc.get()).escapetable, (dc.get()).pmacrotable, this._body, this.bodylen);
        }


        public MacroSection() {}

        public MacroSection copy() {
            MacroSection that = new MacroSection();
            that.name = this.name;
            that.namelen = this.namelen;
            that._body = this._body;
            that.bodylen = this.bodylen;
            that.nooutput = this.nooutput;
            return that;
        }
    }
    // Erasure: isCVariadicParameter<Ptr, Array>
    public static boolean isCVariadicParameter(DArray<Dsymbol> a, ByteSlice p) {
        {
            Slice<Dsymbol> __r1060 = (a).opSlice().copy();
            int __key1061 = 0;
            for (; (__key1061 < __r1060.getLength());__key1061 += 1) {
                Dsymbol member = __r1060.get(__key1061);
                TypeFunction tf = isTypeFunction(member);
                if ((tf != null) && (tf.parameterList.varargs == VarArg.variadic) && __equals(p, new ByteSlice("...")))
                {
                    return true;
                }
            }
        }
        return false;
    }

    // Erasure: getEponymousMember<TemplateDeclaration>
    public static Dsymbol getEponymousMember(TemplateDeclaration td) {
        if (td.onemember == null)
        {
            return null;
        }
        {
            AggregateDeclaration ad = td.onemember.isAggregateDeclaration();
            if ((ad) != null)
            {
                return ad;
            }
        }
        {
            FuncDeclaration fd = td.onemember.isFuncDeclaration();
            if ((fd) != null)
            {
                return fd;
            }
        }
        {
            EnumMember em = td.onemember.isEnumMember();
            if ((em) != null)
            {
                return null;
            }
        }
        {
            VarDeclaration vd = td.onemember.isVarDeclaration();
            if ((vd) != null)
            {
                return td.constraint != null ? null : vd;
            }
        }
        return null;
    }

    // Erasure: getEponymousParent<Dsymbol>
    public static TemplateDeclaration getEponymousParent(Dsymbol s) {
        if (s.parent.value == null)
        {
            return null;
        }
        TemplateDeclaration td = s.parent.value.isTemplateDeclaration();
        return (td != null) && (getEponymousMember(td) != null) ? td : null;
    }

    static ByteSlice ddoc_default = new ByteSlice("LPAREN = (\nRPAREN = )\nBACKTICK = `\nDOLLAR = $\nCOMMA = ,\nQUOTE = &quot;\nLF =\n$(LF)\n\nESCAPES =\n  /</&lt;/\n  />/&gt;/\n  /&/&amp;/\n\nH1 = <h1>$0</h1>\nH2 = <h2>$0</h2>\nH3 = <h3>$0</h3>\nH4 = <h4>$0</h4>\nH5 = <h5>$0</h5>\nH6 = <h6>$0</h6>\nB = <b>$0</b>\nI = <i>$0</i>\nEM = <em>$0</em>\nSTRONG = <strong>$0</strong>\nU = <u>$0</u>\nP = <p>$0</p>\nDL = <dl>$0</dl>\nDT = <dt>$0</dt>\nDD = <dd>$0</dd>\nTABLE = <table>$0</table>\nTHEAD = <thead>$0</thead>\nTBODY = <tbody>$0</tbody>\nTR = <tr>$0</tr>\nTH = <th>$0</th>\nTD = <td>$0</td>\nTH_ALIGN = <th align=\"$1\">$+</th>\nTD_ALIGN = <td align=\"$1\">$+</td>\nOL = <ol>$0</ol>\nOL_START = <ol start=\"$1\">$2</ol>\nUL = <ul>$0</ul>\nLI = <li>$0</li>\nBIG = <span class=\"font_big\">$0</span>\nSMALL = <small>$0</small>\nBR = <br>\nHR = <hr />\nLINK = <a href=\"$0\">$0</a>\nLINK2 = <a href=\"$1\">$+</a>\nLINK_TITLE = <a href=\"$1\" title=\"$2\">$3</a>\nSYMBOL_LINK = <a href=\"$1\">$(DDOC_PSYMBOL $+)</a>\nPHOBOS_PATH = https://dlang.org/phobos/\nDOC_ROOT_std = $(PHOBOS_PATH)\nDOC_ROOT_core = $(PHOBOS_PATH)\nDOC_ROOT_etc = $(PHOBOS_PATH)\nDOC_ROOT_object = $(PHOBOS_PATH)\nDOC_EXTENSION = .html\nIMAGE = <img src=\"$1\" alt=\"$+\" />\nIMAGE_TITLE = <img src=\"$1\" alt=\"$3\" title=\"$2\" />\nBLOCKQUOTE = <blockquote>$0</blockquote>\nDEPRECATED = $0\n\nRED = <span class=\"color_red\">$0</span>\nBLUE = <span class=\"color_blue\">$0</span>\nGREEN = <span class=\"color_green\">$0</span>\nYELLOW = <span class=\"color_yellow\">$0</span>\nBLACK = <span class=\"color_black\">$0</span>\nWHITE = <span class=\"color_white\">$0</span>\n\nD_CODE =\n<section class=\"code_listing\">\n  <div class=\"code_sample\">\n    <div class=\"dlang\">\n      <ol class=\"code_lines\">\n        <li><code class=\"code\">$0</code></li>\n      </ol>\n    </div>\n  </div>\n</section>\n\nOTHER_CODE =\n<section class=\"code_listing\">\n  <div class=\"code_sample\">\n    <div class=\"dlang\">\n      <ol class=\"code_lines\">\n        <li><code class=\"code language-$1\">$+</code></li>\n      </ol>\n    </div>\n  </div>\n</section>\n\nD_INLINECODE = <code class=\"code\">$0</code>\nDDOC_BACKQUOTED = $(D_INLINECODE $0)\nD_COMMENT = <span class=\"comment\">$0</span>\nD_STRING = <span class=\"string_literal\">$0</span>\nD_KEYWORD = <span class=\"keyword\">$0</span>\nD_PSYMBOL = <span class=\"psymbol\">$0</span>\nD_PARAM = <span class=\"param\">$0</span>\n\nDDOC_BLANKLINE = <br><br>\nDDOC_COMMENT = <!-- $0 -->\n\nDDOC =\n<!DOCTYPE html>\n<html>\n  <head>\n    <meta charset=\"UTF-8\">\n    <title>$(TITLE)</title>\n    <style type=\"text/css\" media=\"screen\">\n      html, body, div, span, object, iframe, h1, h2, h3, h4, h5, h6, p,\n      blockquote, pre, a, abbr, address, cite, code, del, dfn, em, figure,\n      img, ins, kbd, q, s, samp, small, strong, sub, sup, var, b, u, i, dl,\n      dt, dd, ol, ul, li, fieldset, form, label, legend, table, caption,\n      tbody, tfoot, thead, tr, th, td {\n        background: transparent none repeat scroll 0 0;\n        border: 0 none;\n        font-size: 100%;\n        margin: 0;\n        outline: 0 none;\n        padding: 0;\n        vertical-align: baseline;\n      }\n\n      h1 { font-size: 200%; }\n      h2 { font-size: 160%; }\n      h3 { font-size: 120%; }\n      h4 { font-size: 100%; }\n      h5 { font-size: 80%; }\n      h6 { font-size: 80%; font-weight: normal; }\n\n      ul, ol {\n        margin: 1.4em 0;\n      }\n      ul ul, ol ol, ul ol, ol ul {\n        margin-top: 0;\n        margin-bottom: 0;\n      }\n      ul, ol {\n        margin-left: 2.8em;\n      }\n\n      ol {\n        list-style: decimal;\n      }\n      ol ol {\n        list-style: lower-alpha;\n      }\n      ol ol ol {\n        list-style: lower-roman;\n      }\n      ol ol ol ol {\n        list-style: decimal;\n      }\n\n      blockquote {\n        margin: 0.1em;\n        margin-left: 1em;\n        border-left: 2px solid #cccccc;\n        padding-left: 0.7em;\n      }\n\n      .color_red { color: #dc322f; }\n      .color_blue { color: #268bd2; }\n      .color_green { color: #859901; }\n      .color_yellow { color: #b58901; }\n      .color_black { color: black; }\n      .color_white { color: white; }\n\n      .font_big {\n        font-size: 1.2em;\n      }\n\n      .ddoc_section_h {\n        font-weight: bold;\n        font-size: 13px;\n        line-height: 19.5px;\n        margin-top: 11px;\n        display: block;\n      }\n\n      body.dlang .dlang {\n        display: inline-block;\n      }\n\n      body.dlang .declaration .dlang {\n          display: block;\n      }\n\n      body.dlang .ddoc_header_anchor a.dlang {\n        display: block;\n        color: rgba(0, 136, 204, 1);\n        text-decoration: none;\n      }\n\n      body.dlang .ddoc_header_anchor .code {\n        color: rgba(0, 136, 204, 1);\n      }\n\n      #ddoc_main .module {\n          border-color: currentColor rgba(233, 233, 233, 1) rgba(233, 233, 233, 1);\n          border-style: none solid solid;\n          border-width: 0 1px 1px;\n          overflow-x: hidden;\n          padding: 15px;\n      }\n\n      #ddoc_main .section .section {\n        margin-top: 0;\n      }\n\n      #ddoc_main .ddoc_module_members_section {\n          padding: 1px 0 0;\n          transition: transform 0.3s ease 0s;\n      }\n\n      #ddoc_main .ddoc_member, #ddoc_main .ddoc_module_members section.intro {\n          background: #fff none repeat scroll 0 0;\n          list-style-type: none;\n          width: 100%;\n      }\n\n      #ddoc_main .ddoc_header_anchor {\n          font-size: 1.4em;\n          transition: transform 0.3s ease 0s;\n      }\n\n      #ddoc_main .ddoc_header_anchor > .code {\n          display: inline-block;\n\n      }\n\n      #ddoc_main .ddoc_decl {\n        background-color: transparent;\n        height: 100%;\n        left: 0;\n        top: 0;\n        padding: 0;\n        padding-left: 15px;\n      }\n\n      #ddoc_main .ddoc_decl .section, #ddoc_main .section.ddoc_sections {\n        background: white none repeat scroll 0 0;\n        margin: 0;\n        padding: 5px;\n        position: relative;\n        border-radius: 5px;\n      }\n\n      #ddoc_main .ddoc_decl .section h4:first-of-type, #ddoc_main .section.ddoc_sections h4:first-of-type {\n        font-size: 13px;\n        line-height: 1.5;\n        margin-top: 21px;\n      }\n\n      #ddoc_main .section .declaration {\n          margin-top: 21px;\n      }\n\n      #ddoc_main .section .declaration .code {\n          color: rgba(0, 0, 0, 1);\n          margin-bottom: 15px;\n          padding-bottom: 6px;\n      }\n\n      #ddoc_main .declaration div .para {\n          margin-bottom: 0;\n      }\n\n      #ddoc_main .ddoc_params .graybox tr td:first-of-type {\n        padding: 7px;\n        text-align: right;\n        vertical-align: top;\n        word-break: normal;\n        white-space: nowrap;\n      }\n\n      #ddoc_main .ddoc_params .graybox {\n        border: 0 none;\n      }\n\n      #ddoc_main .ddoc_params .graybox td {\n        border-color: rgba(214, 214, 214, 1);\n      }\n\n      #ddoc_main .ddoc_params .graybox tr:first-child > td {\n        border-top: 0 none;\n      }\n\n      #ddoc_main .ddoc_params .graybox tr:last-child > td {\n        border-bottom: 0 none;\n      }\n\n      #ddoc_main .ddoc_params .graybox tr > td:first-child {\n        border-left: 0 none;\n      }\n\n      #ddoc_main .ddoc_params .graybox tr > td:last-child {\n        border-right: 0 none;\n        width: 100%;\n      }\n\n      #ddoc_main em.term, #ddoc_main em.term .code {\n        color: rgba(65, 65, 65, 1);\n        font-size: 12px;\n        font-style: italic;\n        line-height: 1.5;\n      }\n\n      #ddoc_main .see-also {\n        cursor: pointer;\n        font-family: Menlo,monospace;\n      }\n\n      #ddoc_main .ddoc_decl .section > div:last-of-type {\n        margin-bottom: 15px;\n      }\n\n      #ddoc_main .ddoc_member, #ddoc_main .ddoc_module_members {\n          transition: transform 0.3s ease 0s;\n      }\n\n      #ddoc_main .code_sample {\n        background: inherit;\n      }\n\n      #ddoc_main .declaration .code-line {\n          display: block;\n          font: 1em Menlo,monospace;\n      }\n\n      #ddoc_main a[name] {\n        margin: -112px 0 0;\n        padding-top: 112px;\n      }\n\n      #ddoc_main .ddoc_decl td {\n        max-width: inherit;\n      }\n\n      #ddoc_main .declaration a {\n        color: inherit;\n      }\n\n      #ddoc_main .declaration a:hover {\n          color: rgba(0, 136, 204, 1);\n          text-decoration: underline;\n      }\n\n      body.ddoc {\n        background-color: transparent;\n        color: rgba(0, 0, 0, 1);\n        font-family: Helvetica,Arial,sans-serif;\n        font-size: 62.5%;\n        margin: 0;\n        border: 0;\n        left: 0;\n        top: 0;\n        padding: 0;\n      }\n\n      .ddoc a[name] {\n        display: block;\n        height: 0;\n        margin: -85px 0 0;\n        padding-top: 85px;\n        width: 0;\n      }\n\n      .ddoc .module {\n          border-color: transparent;\n          background-color: rgba(255, 255, 255, 1);\n          border-color: currentColor rgba(233, 233, 233, 1) rgba(233, 233, 233, 1);\n          border-image: none;\n          border-style: none solid solid;\n          border-width: 0 1px 1px;\n          box-shadow: 0 0 1px rgba(0, 0, 0, 0.07);\n          display: block;\n          margin-left: 0;\n          min-height: calc(100% - 173px);\n          overflow: auto;\n          padding-bottom: 100px;\n      }\n\n      .ddoc .content_wrapper {\n          background-color: rgba(242, 242, 242, 1);\n          margin: 0 auto;\n          max-width: 980px;\n      }\n\n      .ddoc .section {\n        padding: 15px 25px 30px;\n      }\n\n      .ddoc .section .section {\n        margin: 30px 0 0;\n        padding: 0;\n      }\n\n      .ddoc .para {\n        color: rgba(65, 65, 65, 1);\n        font-size: 1.4em;\n        line-height: 145%;\n        margin-bottom: 15px;\n      }\n\n      .ddoc .ddoc_examples .para {\n        margin-bottom: 0;\n      }\n\n      .ddoc .module_name {\n          color: rgba(0, 0, 0, 1);\n          display: block;\n          font-family: Helvetica;\n          font-size: 2.8em;\n          font-weight: 100;\n          margin-bottom: 0;\n          padding: 15px 0;\n      }\n\n      .ddoc .module a {\n          color: rgba(0, 136, 204, 1);\n          text-decoration: none;\n      }\n\n      .ddoc .code {\n        color: rgba(128, 128, 128, 1);\n        font-family: Menlo,monospace;\n        font-size: 0.85em;\n        word-wrap: break-word;\n      }\n\n      .ddoc .code i {\n        font-style: normal;\n      }\n\n      .ddoc .code .code {\n        font-size: 1em;\n      }\n\n      .ddoc .code_sample {\n        background-clip: padding-box;\n        margin: 1px 0;\n        text-align: left;\n      }\n\n      .ddoc .code_sample {\n        display: block;\n        font-size: 1.4em;\n        margin-left: 21px;\n      }\n\n      .ddoc ol .code_sample {\n        font-size: 1em;\n      }\n\n      .ddoc .code_lines {\n        counter-reset: li;\n        line-height: 1.6em;\n        list-style: outside none none;\n        margin: 0;\n        padding: 0;\n      }\n\n      .ddoc .code_listing .code_sample div {\n        margin-left: 13px;\n        width: 93%;\n      }\n\n      .ddoc .code_listing .code_sample div .code_lines li {\n        list-style-type: none;\n        margin: 0;\n        padding-right: 10px;\n      }\n\n      .ddoc .code_sample div .code_lines li::before {\n        margin-left: -33px;\n        margin-right: 25px;\n      }\n\n      .ddoc .code_sample div .code_lines li:nth-child(n+10)::before {\n        margin-left: -39px;\n        margin-right: 25px;\n      }\n\n      .ddoc .code_sample div .code_lines li:nth-child(n+100)::before {\n        margin-left: -46px;\n        margin-right: 25px;\n      }\n\n      .ddoc .code_sample .code_lines .code {\n        color: #000;\n      }\n\n      .ddoc div.dlang {\n        margin: 10px 0 21px;\n        padding: 4px 0 2px 10px;\n      }\n\n      .ddoc div.dlang {\n          margin: 10px 0 21px;\n          padding: 4px 0 2px 10px;\n      }\n\n      .ddoc div.dlang {\n        border-left: 5px solid rgba(0, 155, 51, 0.2);\n      }\n\n      .ddoc .code_lines li::before {\n        color: rgba(128, 128, 128, 1);\n        content: counter(li, decimal);\n        counter-increment: li;\n        font-family: Menlo,monospace;\n        font-size: 0.9em;\n        margin-right: 16px;\n      }\n\n      .ddoc .code_lines li {\n        padding-left: 0;\n        white-space: pre-wrap;\n      }\n\n      .ddoc .code_lines li:only-of-type::before {\n        color: rgba(255, 255, 255, 1);\n        content: \" \";\n      }\n\n      .ddoc .code_lines li:only-of-type {\n        color: rgba(255, 255, 255, 1);\n        content: \" \";\n      }\n\n      .ddoc .code_lines li:nth-child(n+10) {\n        text-indent: -17px;\n      }\n\n      .ddoc .code_lines li:nth-child(n+10)::before {\n        margin-right: 12px;\n      }\n\n      .ddoc .graybox {\n        border: 1px solid rgba(233, 233, 233, 1);\n        border-collapse: collapse;\n        border-spacing: 0;\n        empty-cells: hide;\n        margin: 20px 0 36px;\n        text-align: left;\n      }\n\n      .ddoc .graybox p {\n        margin: 0;\n        min-width: 50px;\n      }\n\n      .ddoc th {\n        margin: 0;\n        max-width: 260px;\n        padding: 5px 10px 5px 10px;\n        vertical-align: bottom;\n      }\n\n      .ddoc td {\n        border: 1px solid rgba(233, 233, 233, 1);\n        margin: 0;\n        max-width: 260px;\n        padding: 5px 10px 5px 10px;\n        vertical-align: middle;\n      }\n\n      .punctuation {\n        color: rgba(0, 0, 0, 1);\n      }\n\n      .comment {\n        color: rgba(0, 131, 18, 1);\n      }\n\n      .operator {\n        color: #000;\n      }\n\n      .keyword {\n        color: rgba(170, 13, 145, 1);\n      }\n\n      .keyword_type {\n        color: rgba(170, 51, 145, 1);\n      }\n\n      .string_literal {\n        color: rgba(196, 26, 22, 1);\n      }\n\n      .ddoc_psuper_symbol {\n        color: rgba(92, 38, 153, 1);\n      }\n\n      .param {\n        color: rgba(0, 0, 0, 1);\n      }\n\n      .psymbol {\n        color: rgba(0, 0, 0, 1);\n      }\n\n      .ddoc_member_header .ddoc_header_anchor .code {\n        font-size: 1em;\n      }\n    </style>\n  </head>\n  <body id=\"ddoc_main\" class=\"ddoc dlang\">\n    <div class=\"content_wrapper\">\n      <article class=\"module\">\n        <h1 class=\"module_name\">$(TITLE)</h1>\n        <section id=\"module_content\">$(BODY)</section>\n      </article>\n    </div>\n  </body>\n</html>$(LF)\n\nDDOC_MODULE_MEMBERS = <section class=\"section ddoc_module_members_section\">\n  <div class=\"ddoc_module_members\">\n    $(DDOC_MEMBERS $0)\n  </div>\n</section>$(LF)\n\nDDOC_CLASS_MEMBERS = $(DDOC_MEMBERS $0)$(LF)\nDDOC_STRUCT_MEMBERS = $(DDOC_MEMBERS $0)$(LF)\nDDOC_ENUM_MEMBERS = $(DDOC_MEMBERS $0)$(LF)\nDDOC_TEMPLATE_MEMBERS = $(DDOC_MEMBERS $0)$(LF)\n\nDDOC_MEMBERS = <ul class=\"ddoc_members\">\n  $0\n</ul>\n\nDDOC_MEMBER = <li class=\"ddoc_member\">\n  $0\n</li>\n\nDDOC_MEMBER_HEADER = <div class=\"ddoc_member_header\">\n  $0\n</div>\n\nDDOC_HEADER_ANCHOR = <div class=\"ddoc_header_anchor\">\n  <a href=\"#$1\" id=\"$1\"><code class=\"code\">$2</code></a>\n</div>\n\nDDOC_DECL = <div class=\"ddoc_decl\">\n  <section class=\"section\">\n    <div class=\"declaration\">\n      <h4>Declaration</h4>\n      <div class=\"dlang\">\n        <p class=\"para\">\n          <code class=\"code\">\n            $0\n          </code>\n        </p>\n      </div>\n    </div>\n  </section>\n</div>\n\nDDOC_ANCHOR = <span class=\"ddoc_anchor\" id=\"$1\"></span>\n\nDDOC_DECL_DD = <div class=\"ddoc_decl\">\n  $0\n</div>\n\nDDOC_SECTIONS = <section class=\"section ddoc_sections\">\n  $0\n</section>$(LF)\n\nDDOC_SUMMARY = <div class=\"ddoc_summary\">\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_DESCRIPTION = <div class=\"ddoc_description\">\n  <h4>Discussion</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_EXAMPLES = <div class=\"ddoc_examples\">\n  <h4>Examples</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>\n\nDDOC_RETURNS = <div class=\"ddoc_returns\">\n  <h4>Return Value</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_PARAMS = <div class=\"ddoc_params\">\n  <h4>Parameters</h4>\n  <table cellspacing=\"0\" cellpadding=\"5\" border=\"0\" class=\"graybox\">\n    <tbody>\n      $0\n    </tbody>\n  </table>\n</div>$(LF)\n\nDDOC_PARAM_ROW = <tr class=\"ddoc_param_row\">\n  $0\n</tr>$(LF)\n\nDDOC_PARAM_ID = <td scope=\"ddoc_param_id\">\n  <code class=\"code\">\n    <em class=\"term\">$0</em>\n  </code>\n</td>$(LF)\n\nDDOC_PARAM_DESC = <td>\n  <div class=\"ddoc_param_desc\">\n    <p class=\"para\">\n      $0\n    </p>\n  </div>\n</td>\n\nDDOC_LICENSE = <div class=\"ddoc_license\">\n  <h4>License</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_AUTHORS = <div class=\"ddoc_authors\">\n  <h4>Authors</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_BUGS = <div class=\"ddoc_bugs\">\n  <h4>Bugs</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_COPYRIGHT = <div class=\"ddoc_copyright\">\n  <h4>Copyright</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_DATE = <div class=\"ddoc_date\">\n  <h4>Date</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_DEPRECATED = <div class=\"ddoc_deprecated\">\n  <h4>Deprecated</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_HISTORY = <div class=\"ddoc_history\">\n  <h4>History</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_SEE_ALSO = <div class=\"ddoc_see_also\">\n  <h4>See Also</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_STANDARDS = <div class=\"ddoc_standards\">\n  <h4>Standards</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>\n\nDDOC_THROWS = <div class=\"ddoc_throws\">\n  <h4>Throws</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>\n\nDDOC_VERSION = <div class=\"ddoc_version\">\n  <h4>Version</h4>\n  <p class=\"para\">\n    $0\n  </p>\n</div>\n\nDDOC_SECTION = <div class=\"ddoc_section\">\n  <p class=\"para\">\n    $0\n  </p>\n</div>$(LF)\n\nDDOC_SECTION_H = <span class=\"ddoc_section_h\">$0:</span>$(LF)\n\nDDOC_DITTO = <br>\n$0\n\nDDOC_PSYMBOL = <code class=\"code\">$0</code>\nDDOC_ENUM_BASETYPE = $0\nDDOC_PSUPER_SYMBOL = <span class=\"ddoc_psuper_symbol\">$0</span>\nDDOC_KEYWORD = <code class=\"code\">$0</code>\nDDOC_PARAM = <code class=\"code\">$0</code>\nDDOC_CONSTRAINT = $(DDOC_CONSTRAINT) if ($0)\nDDOC_OVERLOAD_SEPARATOR = $0\nDDOC_TEMPLATE_PARAM_LIST = $0\nDDOC_TEMPLATE_PARAM = $0\nDDOC_LINK_AUTODETECT = $(LINK $0)\nDDOC_AUTO_PSYMBOL = $(DDOC_PSYMBOL $0)\nDDOC_AUTO_KEYWORD = $(DDOC_KEYWORD $0)\nDDOC_AUTO_PARAM = $(DDOC_PARAM $0)\nDDOC_AUTO_PSYMBOL_SUPPRESS = $0\n");
    static ByteSlice ddoc_decl_s = new ByteSlice("$(DDOC_DECL ");
    static ByteSlice ddoc_decl_e = new ByteSlice(")\n");
    static ByteSlice ddoc_decl_dd_s = new ByteSlice("$(DDOC_DECL_DD ");
    static ByteSlice ddoc_decl_dd_e = new ByteSlice(")\n");
    // Erasure: gendocfile<Module>
    public static void gendocfile(dmodule.Module m) {
        Ref<OutBuffer> buf = ref(new OutBuffer());
        try {
            if (doc.gendocfilembuf_done == 0)
            {
                doc.gendocfilembuf_done = 1;
                doc.gendocfilembuf.writestring(ddoc_default);
                BytePtr p = pcopy(getenv(new BytePtr("DDOCFILE")));
                if (p != null)
                {
                    global.params.ddocfiles.shift(p);
                }
                {
                    int i = 0;
                    for (; (i < global.params.ddocfiles.length);i++){
                        FileBuffer buffer = readFile(m.loc, global.params.ddocfiles.get(i)).copy();
                        ByteSlice data = buffer.data.copy();
                        doc.gendocfilembuf.write(data.getPtr(0), data.getLength());
                    }
                }
            }
            DocComment.parseMacros(m.escapetable, ptr(m.macrotable), doc.gendocfilembuf.peekSlice().getPtr(0), doc.gendocfilembuf.peekSlice().getLength());
            Ptr<Scope> sc = Scope.createGlobal(m);
            Ptr<DocComment> dc = DocComment.parse(m, m.comment);
            (dc.get()).pmacrotable = pcopy(ptr(m.macrotable));
            (dc.get()).escapetable = pcopy(m.escapetable);
            (sc.get()).lastdc = pcopy(dc);
            {
                ByteSlice p = toDString(m.toPrettyChars(false)).copy();
                Macro.define(ptr(m.macrotable), new ByteSlice("TITLE"), p);
            }
            {
                Ref<Integer> t = ref(0);
                time(ptr(t));
                BytePtr p = pcopy(ctime(ptr(t)));
                p = pcopy(Mem.xstrdup(p));
                Macro.define(ptr(m.macrotable), new ByteSlice("DATETIME"), p.slice(0,strlen(p)));
                Macro.define(ptr(m.macrotable), new ByteSlice("YEAR"), p.slice(20,24));
            }
            ByteSlice srcfilename = m.srcfile.asString().copy();
            Macro.define(ptr(m.macrotable), new ByteSlice("SRCFILENAME"), srcfilename);
            ByteSlice docfilename = m.docfile.asString().copy();
            Macro.define(ptr(m.macrotable), new ByteSlice("DOCFILENAME"), docfilename);
            if ((dc.get()).copyright != null)
            {
                (dc.get()).copyright.nooutput = 1;
                Macro.define(ptr(m.macrotable), new ByteSlice("COPYRIGHT"), (dc.get()).copyright._body.slice(0,(dc.get()).copyright.bodylen));
            }
            if (m.isDocFile)
            {
                Ptr<Loc> ploc = m.md != null ? ptr(m.md.get().loc) : ptr(m.loc);
                Loc loc = loc = new Loc((ploc.get()).filename != null ? (ploc.get()).filename : srcfilename.getPtr(0), (ploc.get()).linnum, (ploc.get()).charnum);
                int commentlen = strlen(m.comment);
                Ref<DArray<Dsymbol>> a = ref(new DArray<Dsymbol>());
                try {
                    if ((dc.get()).macros != null)
                    {
                        commentlen = (((dc.get()).macros.name.minus(m.comment)));
                        (dc.get()).macros.write(loc, dc, sc, a.value, buf.value);
                    }
                    buf.value.write(m.comment, commentlen);
                    highlightText(sc, a.value, loc, buf.value, 0);
                }
                finally {
                }
            }
            else
            {
                Ref<DArray<Dsymbol>> a = ref(new DArray<Dsymbol>());
                try {
                    a.value.push(m);
                    (dc.get()).writeSections(sc, a.value, buf.value);
                    emitMemberComments(m, buf.value, sc);
                }
                finally {
                }
            }
            Macro.define(ptr(m.macrotable), new ByteSlice("BODY"), buf.value.peekSlice());
            Ref<OutBuffer> buf2 = ref(new OutBuffer());
            try {
                buf2.value.writestring(new ByteSlice("$(DDOC)"));
                Ref<Integer> end = ref(buf2.value.offset);
                (m.macrotable.value.get()).expand(buf2.value, 0, ptr(end), new ByteSlice());
                {
                    ByteSlice slice = buf2.value.peekSlice().copy();
                    buf.value.setsize(0);
                    buf.value.reserve(slice.getLength());
                    BytePtr p = pcopy(slice.getPtr(0));
                    {
                        int j = 0;
                        for (; (j < slice.getLength());j++){
                            byte c = p.get(j);
                            if (((c & 0xFF) == 255) && (j + 1 < slice.getLength()))
                            {
                                j++;
                                continue;
                            }
                            if (((c & 0xFF) == 10))
                            {
                                buf.value.writeByte(13);
                            }
                            else if (((c & 0xFF) == 13))
                            {
                                buf.value.writestring(new ByteSlice("\r\n"));
                                if ((j + 1 < slice.getLength()) && ((p.get(j + 1) & 0xFF) == 10))
                                {
                                    j++;
                                }
                                continue;
                            }
                            buf.value.writeByte((c & 0xFF));
                        }
                    }
                }
                writeFile(m.loc, m.docfile.asString(), toByteSlice(buf.value.peekSlice()));
            }
            finally {
            }
        }
        finally {
        }
    }

    // Erasure: escapeDdocString<Ptr, int>
    public static void escapeDdocString(OutBuffer buf, int start) {
        {
            int u = start;
            for (; (u < (buf).offset);u++){
                byte c = (byte)(buf).data.get(u);
                switch ((c & 0xFF))
                {
                    case 36:
                        (buf).remove(u, 1);
                        (buf).insert(u, new ByteSlice("$(DOLLAR)"));
                        u += 8;
                        break;
                    case 40:
                        (buf).remove(u, 1);
                        (buf).insert(u, new ByteSlice("$(LPAREN)"));
                        u += 8;
                        break;
                    case 41:
                        (buf).remove(u, 1);
                        (buf).insert(u, new ByteSlice("$(RPAREN)"));
                        u += 8;
                        break;
                    default:
                    break;
                }
            }
        }
    }

    // Erasure: escapeStrayParenthesis<Loc, Ptr, int, boolean>
    public static void escapeStrayParenthesis(Loc loc, OutBuffer buf, int start, boolean respectBackslashEscapes) {
        int par_open = 0;
        byte inCode = (byte)0;
        boolean atLineStart = true;
        {
            int u = start;
            for (; (u < (buf).offset);u++){
                byte c = (byte)(buf).data.get(u);
                switch ((c & 0xFF))
                {
                    case 40:
                        if (inCode == 0)
                        {
                            par_open++;
                        }
                        atLineStart = false;
                        break;
                    case 41:
                        if (inCode == 0)
                        {
                            if ((par_open == 0))
                            {
                                warning(loc, new BytePtr("Ddoc: Stray ')'. This may cause incorrect Ddoc output. Use $(RPAREN) instead for unpaired right parentheses."));
                                (buf).remove(u, 1);
                                (buf).insert(u, new ByteSlice("$(RPAREN)"));
                                u += 8;
                            }
                            else
                            {
                                par_open--;
                            }
                        }
                        atLineStart = false;
                        break;
                    case 10:
                        atLineStart = true;
                        break;
                    case 32:
                    case 13:
                    case 9:
                        break;
                    case 45:
                    case 96:
                    case 126:
                        int numdash = 1;
                        {
                            u += 1;
                            for (; (u < (buf).offset) && (((buf).data.get(u) & 0xFF) == (c & 0xFF));u += 1) {
                                numdash += 1;
                            }
                        }
                        u -= 1;
                        if (((c & 0xFF) == 96) || atLineStart && (numdash >= 3))
                        {
                            if (((inCode & 0xFF) == (c & 0xFF)))
                            {
                                inCode = (byte)0;
                            }
                            else if (inCode == 0)
                            {
                                inCode = c;
                            }
                        }
                        atLineStart = false;
                        break;
                    case 92:
                        if ((inCode == 0) && respectBackslashEscapes && (u + 1 < (buf).offset) && global.params.markdown)
                        {
                            if ((((buf).data.get(u + 1) & 0xFF) == 40) || (((buf).data.get(u + 1) & 0xFF) == 41))
                            {
                                ByteSlice paren = (((buf).data.get(u + 1) & 0xFF) == 40) ? new ByteSlice("$(LPAREN)") : new ByteSlice("$(RPAREN)").copy();
                                (buf).remove(u, 2);
                                (buf).insert(u, toByteSlice(paren));
                                u += 8;
                            }
                            else if ((((buf).data.get(u + 1) & 0xFF) == 92))
                            {
                                u += 1;
                            }
                        }
                        break;
                    default:
                    atLineStart = false;
                    break;
                }
            }
        }
        if (par_open != 0)
        {
            par_open = 0;
            {
                int u = (buf).offset;
                for (; (u > start);){
                    u--;
                    byte c = (byte)(buf).data.get(u);
                    switch ((c & 0xFF))
                    {
                        case 41:
                            par_open++;
                            break;
                        case 40:
                            if ((par_open == 0))
                            {
                                warning(loc, new BytePtr("Ddoc: Stray '('. This may cause incorrect Ddoc output. Use $(LPAREN) instead for unpaired left parentheses."));
                                (buf).remove(u, 1);
                                (buf).insert(u, new ByteSlice("$(LPAREN)"));
                            }
                            else
                            {
                                par_open--;
                            }
                            break;
                        default:
                        break;
                    }
                }
            }
        }
    }

    // Erasure: skipNonQualScopes<Ptr>
    public static Ptr<Scope> skipNonQualScopes(Ptr<Scope> sc) {
        for (; (sc != null) && ((sc.get()).scopesym == null);) {
            sc = pcopy((sc.get()).enclosing);
        }
        return sc;
    }

    // Erasure: emitAnchorName<Ptr, Dsymbol, Ptr, boolean>
    public static boolean emitAnchorName(OutBuffer buf, Dsymbol s, Ptr<Scope> sc, boolean includeParent) {
        if ((s == null) || (s.isPackage() != null) || (s.isModule() != null))
        {
            return false;
        }
        boolean dot = false;
        TemplateDeclaration eponymousParent = getEponymousParent(s);
        if (includeParent && (s.parent.value != null) || (eponymousParent != null))
        {
            dot = emitAnchorName(buf, s.parent.value, sc, includeParent);
        }
        else if (includeParent && (sc != null))
        {
            dot = emitAnchorName(buf, (sc.get()).scopesym, skipNonQualScopes((sc.get()).enclosing), includeParent);
        }
        if (eponymousParent != null)
        {
            return dot;
        }
        if (dot)
        {
            (buf).writeByte(46);
        }
        TemplateDeclaration td = null;
        if ((s.isCtorDeclaration() != null) || ((td = s.isTemplateDeclaration()) != null) && (td.onemember != null) && (td.onemember.isCtorDeclaration() != null))
        {
            (buf).writestring(new ByteSlice("this"));
        }
        else
        {
            (buf).writestring(s.toChars());
        }
        return true;
    }

    // Erasure: emitAnchor<Ptr, Dsymbol, Ptr, boolean>
    public static void emitAnchor(OutBuffer buf, Dsymbol s, Ptr<Scope> sc, boolean forHeader) {
        Identifier ident = null;
        {
            Ref<OutBuffer> anc = ref(new OutBuffer());
            try {
                emitAnchorName(anc.value, s, skipNonQualScopes(sc), true);
                ident = Identifier.idPool(anc.value.peekSlice());
            }
            finally {
            }
        }
        Ptr<Integer> pcount = pcopy((sc.get()).anchorCounts.getLvalue((ident)));
        int count = 0;
        if (!forHeader)
        {
            if (pcount != null)
            {
                TemplateDeclaration td = getEponymousParent(s);
                if ((pequals((sc.get()).prevAnchor, ident)) && ((sc.get()).lastdc != null) && isDitto(s.comment) || (td != null) && isDitto(td.comment))
                {
                    return ;
                }
                count = (pcount.get() += 1);
            }
            else
            {
                (sc.get()).anchorCounts.set((ident), __aaval1062);
                count = 1;
            }
        }
        (sc.get()).prevAnchor = ident;
        ByteSlice macroName = forHeader ? new ByteSlice("DDOC_HEADER_ANCHOR") : new ByteSlice("DDOC_ANCHOR").copy();
        {
            Import imp = s.isImport();
            if ((imp) != null)
            {
                if ((imp.aliases.length > 0))
                {
                    {
                        int i = 0;
                        for (; (i < imp.aliases.length);i++){
                            Identifier a = imp.aliases.get(i);
                            Identifier id = a != null ? a : imp.names.get(i);
                            Loc loc = new Loc(null, 0, 0).copy();
                            {
                                Dsymbol symFromId = (sc.get()).search(loc, id, null, 0);
                                if ((symFromId) != null)
                                {
                                    emitAnchor(buf, symFromId, sc, forHeader);
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (imp.aliasId != null)
                    {
                        ByteSlice symbolName = imp.aliasId.asString().copy();
                        (buf).printf(new BytePtr("$(%.*s %.*s"), macroName.getLength(), macroName.getPtr(0), symbolName.getLength(), symbolName.getPtr(0));
                        if (forHeader)
                        {
                            (buf).printf(new BytePtr(", %.*s"), symbolName.getLength(), symbolName.getPtr(0));
                        }
                    }
                    else
                    {
                        Runnable0 printFullyQualifiedImport = new Runnable0() {
                            public Void invoke() {
                             {
                                if ((imp.packages != null) && ((imp.packages).length != 0))
                                {
                                    {
                                        Slice<Identifier> __r1063 = (imp.packages).opSlice().copy();
                                        Ref<Integer> __key1064 = ref(0);
                                        for (; (__key1064.value < __r1063.getLength());__key1064.value += 1) {
                                            Identifier pid = __r1063.get(__key1064.value);
                                            (buf).printf(new BytePtr("%s."), pid.toChars());
                                        }
                                    }
                                }
                                (buf).writestring(imp.id.asString());
                                return null;
                            }}

                        };
                        (buf).printf(new BytePtr("$(%.*s "), macroName.getLength(), macroName.getPtr(0));
                        printFullyQualifiedImport.invoke();
                        if (forHeader)
                        {
                            (buf).printf(new BytePtr(", "));
                            printFullyQualifiedImport.invoke();
                        }
                    }
                    (buf).writeByte(41);
                }
            }
            else
            {
                ByteSlice symbolName = ident.asString().copy();
                (buf).printf(new BytePtr("$(%.*s %.*s"), macroName.getLength(), macroName.getPtr(0), symbolName.getLength(), symbolName.getPtr(0));
                if ((count > 1))
                {
                    (buf).printf(new BytePtr(".%u"), count);
                }
                if (forHeader)
                {
                    Identifier shortIdent = null;
                    {
                        Ref<OutBuffer> anc = ref(new OutBuffer());
                        try {
                            emitAnchorName(anc.value, s, skipNonQualScopes(sc), false);
                            shortIdent = Identifier.idPool(anc.value.peekSlice());
                        }
                        finally {
                        }
                    }
                    ByteSlice shortName = shortIdent.asString().copy();
                    (buf).printf(new BytePtr(", %.*s"), shortName.getLength(), shortName.getPtr(0));
                }
                (buf).writeByte(41);
            }
        }
    }

    // defaulted all parameters starting with #4
    public static void emitAnchor(OutBuffer buf, Dsymbol s, Ptr<Scope> sc) {
        emitAnchor(buf, s, sc, false);
    }

    // Erasure: getCodeIndent<Ptr>
    public static int getCodeIndent(BytePtr src) {
        for (; (src != null) && ((src.get() & 0xFF) == 13) || ((src.get() & 0xFF) == 10);) {
            src.plusAssign(1);
        }
        int codeIndent = 0;
        for (; (src != null) && ((src.get() & 0xFF) == 32) || ((src.get() & 0xFF) == 9);){
            codeIndent++;
            src.postInc();
        }
        return codeIndent;
    }

    // Erasure: expandTemplateMixinComments<TemplateMixin, Ptr, Ptr>
    public static void expandTemplateMixinComments(TemplateMixin tm, OutBuffer buf, Ptr<Scope> sc) {
        if (tm.semanticRun == 0)
        {
            dsymbolSemantic(tm, sc);
        }
        TemplateDeclaration td = (tm != null) && (tm.tempdecl != null) ? tm.tempdecl.isTemplateDeclaration() : null;
        if ((td != null) && (td.members != null))
        {
            {
                int i = 0;
                for (; (i < (td.members).length);i++){
                    Dsymbol sm = (td.members).get(i);
                    TemplateMixin tmc = sm.isTemplateMixin();
                    if ((tmc != null) && (tmc.comment != null))
                    {
                        expandTemplateMixinComments(tmc, buf, sc);
                    }
                    else
                    {
                        emitComment(sm, buf, sc);
                    }
                }
            }
        }
    }

    // Erasure: emitMemberComments<ScopeDsymbol, Ptr, Ptr>
    public static void emitMemberComments(ScopeDsymbol sds, OutBuffer buf, Ptr<Scope> sc) {
        if (sds.members == null)
        {
            return ;
        }
        ByteSlice m = new ByteSlice("$(DDOC_MEMBERS ").copy();
        if (sds.isTemplateDeclaration() != null)
        {
            m = new ByteSlice("$(DDOC_TEMPLATE_MEMBERS ").copy();
        }
        else if (sds.isClassDeclaration() != null)
        {
            m = new ByteSlice("$(DDOC_CLASS_MEMBERS ").copy();
        }
        else if (sds.isStructDeclaration() != null)
        {
            m = new ByteSlice("$(DDOC_STRUCT_MEMBERS ").copy();
        }
        else if (sds.isEnumDeclaration() != null)
        {
            m = new ByteSlice("$(DDOC_ENUM_MEMBERS ").copy();
        }
        else if (sds.isModule() != null)
        {
            m = new ByteSlice("$(DDOC_MODULE_MEMBERS ").copy();
        }
        int offset1 = (buf).offset;
        (buf).writestring(m);
        int offset2 = (buf).offset;
        sc = pcopy((sc.get()).push(sds));
        {
            int i = 0;
            for (; (i < (sds.members).length);i++){
                Dsymbol s = (sds.members).get(i);
                if ((s.comment != null) && (s.isTemplateMixin() != null) && (s.parent.value != null) && (s.parent.value.isTemplateDeclaration() == null))
                {
                    expandTemplateMixinComments(((TemplateMixin)s), buf, sc);
                }
                emitComment(s, buf, sc);
            }
        }
        emitComment(null, buf, sc);
        (sc.get()).pop();
        if (((buf).offset == offset2))
        {
            (buf).offset = offset1;
        }
        else
        {
            (buf).writestring(new ByteSlice(")"));
        }
    }

    // Erasure: emitProtection<Ptr, Import>
    public static void emitProtection(OutBuffer buf, Import i) {
        emitProtection(buf, i.protection);
    }

    // Erasure: emitProtection<Ptr, Declaration>
    public static void emitProtection(OutBuffer buf, Declaration d) {
        Prot prot = d.protection.copy();
        if ((prot.kind != Prot.Kind.undefined) && (prot.kind != Prot.Kind.public_))
        {
            emitProtection(buf, prot);
        }
    }

    // Erasure: emitProtection<Ptr, Prot>
    public static void emitProtection(OutBuffer buf, Prot prot) {
        protectionToBuffer(buf, prot);
        (buf).writeByte(32);
    }

    // Erasure: emitComment<Dsymbol, Ptr, Ptr>
    public static void emitComment(Dsymbol s, OutBuffer buf, Ptr<Scope> sc) {
        // skipping duplicate class EmitComment
        EmitComment v = new EmitComment(buf, sc);
        if (s == null)
        {
            v.emit(sc, null, null);
        }
        else
        {
            s.accept(v);
        }
    }

    // Erasure: toDocBuffer<Dsymbol, Ptr, Ptr>
    public static void toDocBuffer(Dsymbol s, OutBuffer buf, Ptr<Scope> sc) {
        // skipping duplicate class ToDocBuffer
        ToDocBuffer v = new ToDocBuffer(buf, sc);
        s.accept(v);
    }

    public static class DocComment
    {
        public DArray<Section> sections = new DArray<Section>();
        public Section summary = null;
        public Section copyright = null;
        public Section macros = null;
        public Ptr<Ptr<Macro>> pmacrotable = null;
        public Ptr<Escape> escapetable = null;
        public Ref<DArray<Dsymbol>> a = ref(new DArray<Dsymbol>());
        // Erasure: parse<Dsymbol, Ptr>
        public static Ptr<DocComment> parse(Dsymbol s, BytePtr comment) {
            Ptr<DocComment> dc = refPtr(new DocComment(new DArray<Section>(), null, null, null, null, null, new DArray<Dsymbol>()));
            (dc.get()).a.value.push(s);
            if (comment == null)
            {
                return dc;
            }
            (dc.get()).parseSections(comment);
            {
                int i = 0;
                for (; (i < (dc.get()).sections.length);i++){
                    Section sec = (dc.get()).sections.get(i);
                    if (iequals(new ByteSlice("copyright"), sec.name.slice(0,sec.namelen)))
                    {
                        (dc.get()).copyright = sec;
                    }
                    if (iequals(new ByteSlice("macros"), sec.name.slice(0,sec.namelen)))
                    {
                        (dc.get()).macros = sec;
                    }
                }
            }
            return dc;
        }

        // Erasure: parseMacros<Ptr, Ptr, Ptr, int>
        public static void parseMacros(Ptr<Escape> escapetable, Ptr<Ptr<Macro>> pmacrotable, BytePtr m, int mlen) {
            BytePtr p = pcopy(m);
            int len = mlen;
            BytePtr pend = pcopy(p.plus(len));
            BytePtr tempstart = null;
            int templen = 0;
            BytePtr namestart = null;
            int namelen = 0;
            BytePtr textstart = null;
            int textlen = 0;
            try {
            L_outer4:
                for (; (p.lessThan(pend));){
                    try {
                        try {
                            try {
                            L_outer5:
                                for (; 1 != 0;){
                                    if ((p.greaterOrEqual(pend)))
                                    {
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    }
                                    {
                                        int __dispatch4 = 0;
                                        dispatched_4:
                                        do {
                                            switch (__dispatch4 != 0 ? __dispatch4 : (p.get() & 0xFF))
                                            {
                                                case 32:
                                                case 9:
                                                    p.postInc();
                                                    continue L_outer5;
                                                case 13:
                                                case 10:
                                                    p.postInc();
                                                    /*goto Lcont*/throw Dispatch1.INSTANCE;
                                                default:
                                                if (isIdStart(p))
                                                {
                                                    break;
                                                }
                                                if (namelen != 0)
                                                {
                                                    /*goto Ltext*/throw Dispatch0.INSTANCE;
                                                }
                                                /*goto Lskipline*/throw Dispatch2.INSTANCE;
                                            }
                                        } while(__dispatch4 != 0);
                                    }
                                    break;
                                }
                                tempstart = pcopy(p);
                            L_outer6:
                                for (; 1 != 0;){
                                    if ((p.greaterOrEqual(pend)))
                                    {
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    }
                                    if (!isIdTail(p))
                                    {
                                        break;
                                    }
                                    p.plusAssign(utfStride(p));
                                }
                                templen = ((p.minus(tempstart)));
                            L_outer7:
                                for (; 1 != 0;){
                                    if ((p.greaterOrEqual(pend)))
                                    {
                                        /*goto Ldone*/throw Dispatch0.INSTANCE;
                                    }
                                    if (!(((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 9)))
                                    {
                                        break;
                                    }
                                    p.postInc();
                                }
                                if (((p.get() & 0xFF) != 61))
                                {
                                    if (namelen != 0)
                                    {
                                        /*goto Ltext*/throw Dispatch0.INSTANCE;
                                    }
                                    /*goto Lskipline*/throw Dispatch2.INSTANCE;
                                }
                                p.postInc();
                                if ((p.greaterOrEqual(pend)))
                                {
                                    /*goto Ldone*/throw Dispatch0.INSTANCE;
                                }
                                if (namelen != 0)
                                {
                                /*L1:*/
                                    if (iequals(new ByteSlice("ESCAPES"), namestart.slice(0,namelen)))
                                    {
                                        parseEscapes(escapetable, textstart, textlen);
                                    }
                                    else
                                    {
                                        Macro.define(pmacrotable, namestart.slice(0,namelen), textstart.slice(0,textlen));
                                    }
                                    namelen = 0;
                                    if ((p.greaterOrEqual(pend)))
                                    {
                                        break;
                                    }
                                }
                                namestart = pcopy(tempstart);
                                namelen = templen;
                                for (; (p.lessThan(pend)) && ((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 9);) {
                                    p.postInc();
                                }
                                textstart = pcopy(p);
                            }
                            catch(Dispatch0 __d){}
                        /*Ltext:*/
                            for (; (p.lessThan(pend)) && ((p.get() & 0xFF) != 13) && ((p.get() & 0xFF) != 10);) {
                                p.postInc();
                            }
                            textlen = ((p.minus(textstart)));
                            p.postInc();
                        }
                        catch(Dispatch1 __d){}
                    /*Lcont:*/
                        continue L_outer4;
                    }
                    catch(Dispatch2 __d){}
                /*Lskipline:*/
                    for (; (p.lessThan(pend)) && ((p.get() & 0xFF) != 13) && ((p.get() & 0xFF) != 10);) {
                        p.postInc();
                    }
                }
            }
            catch(Dispatch0 __d){}
        /*Ldone:*/
            if (namelen != 0)
            {
                /*goto L1*/throw Dispatch0.INSTANCE;
            }
        }

        // Erasure: parseEscapes<Ptr, Ptr, int>
        public static void parseEscapes(Ptr<Escape> escapetable, BytePtr textstart, int textlen) {
            if (escapetable == null)
            {
                escapetable = pcopy(refPtr(new Escape(new ByteSlice())));
                memset(escapetable, 0, 2040);
            }
            BytePtr p = pcopy(textstart);
            BytePtr pend = pcopy(p.plus(textlen));
            for (; 1 != 0;){
                for (; 1 != 0;){
                    if ((p.plus(4).greaterOrEqual(pend)))
                    {
                        return ;
                    }
                    if (!(((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 9) || ((p.get() & 0xFF) == 13) || ((p.get() & 0xFF) == 10) || ((p.get() & 0xFF) == 44)))
                    {
                        break;
                    }
                    p.postInc();
                }
                if (((p.get(0) & 0xFF) != 47) || ((p.get(2) & 0xFF) != 47))
                {
                    return ;
                }
                byte c = p.get(1);
                p.plusAssign(3);
                BytePtr start = pcopy(p);
                for (; 1 != 0;){
                    if ((p.greaterOrEqual(pend)))
                    {
                        return ;
                    }
                    if (((p.get() & 0xFF) == 47))
                    {
                        break;
                    }
                    p.postInc();
                }
                int len = ((p.minus(start)));
                BytePtr s = pcopy(((BytePtr)memcpy((BytePtr)Mem.xmalloc(len + 1), (start), len)));
                s.set(len, (byte)0);
                (escapetable.get()).strings.set(((c & 0xFF)), s.slice(0,len));
                p.postInc();
            }
        }

        // Erasure: parseSections<Ptr>
        public  void parseSections(BytePtr comment) {
            BytePtr p = null;
            BytePtr pstart = null;
            BytePtr pend = null;
            BytePtr idstart = null;
            int idlen = 0;
            BytePtr name = null;
            int namelen = 0;
            p = pcopy(comment);
        L_outer8:
            for (; p.get() != 0;){
                BytePtr pstart0 = pcopy(p);
                p = pcopy(skipwhitespace(p));
                pstart = pcopy(p);
                pend = pcopy(p);
                if (((p.get() & 0xFF) == 45) || ((p.get() & 0xFF) == 43) || ((p.get() & 0xFF) == 42) && (((p.plus(1)).get() & 0xFF) == 32) || (((p.plus(1)).get() & 0xFF) == 9))
                {
                    pstart = pcopy(pstart0);
                }
                else
                {
                    BytePtr pitem = pcopy(p);
                    for (; ((pitem.get() & 0xFF) >= 48) && ((pitem.get() & 0xFF) <= 57);) {
                        pitem.plusAssign(1);
                    }
                    if ((pitem.greaterThan(p)) && ((pitem.get() & 0xFF) == 46) && (((pitem.plus(1)).get() & 0xFF) == 32) || (((pitem.plus(1)).get() & 0xFF) == 9))
                    {
                        pstart = pcopy(pstart0);
                    }
                }
                idlen = 0;
                int inCode = 0;
                try {
                L_outer9:
                    for (; 1 != 0;){
                        if (((p.get() & 0xFF) == 45) || ((p.get() & 0xFF) == 96) || ((p.get() & 0xFF) == 126))
                        {
                            byte c = p.get();
                            int numdash = 0;
                            for (; ((p.get() & 0xFF) == (c & 0xFF));){
                                numdash += 1;
                                p.postInc();
                            }
                            if ((p.get() == 0) || ((p.get() & 0xFF) == 13) || ((p.get() & 0xFF) == 10) || (inCode == 0) && ((c & 0xFF) != 45) && (numdash >= 3))
                            {
                                inCode = (inCode == (c & 0xFF)) ? 0 : (c & 0xFF);
                                if (inCode != 0)
                                {
                                    for (; (pstart0.lessThan(pstart)) && isIndentWS(pstart.minus(1));) {
                                        pstart.minusAssign(1);
                                    }
                                }
                            }
                            pend = pcopy(p);
                        }
                        if ((inCode == 0) && isIdStart(p))
                        {
                            BytePtr q = pcopy(p.plus(utfStride(p)));
                            for (; isIdTail(q);) {
                                q.plusAssign(utfStride(q));
                            }
                            if (((q.get() & 0xFF) == 58) && (isupper((p.get() & 0xFF)) != 0) && (isspace((q.get(1) & 0xFF)) != 0) || ((q.get(1) & 0xFF) == 0))
                            {
                                idlen = ((q.minus(p)));
                                idstart = pcopy(p);
                                {
                                    pend = pcopy(p);
                                    for (; (pend.greaterThan(pstart));pend.postDec()){
                                        if (((pend.get(-1) & 0xFF) == 10))
                                        {
                                            break;
                                        }
                                    }
                                }
                                p = pcopy(q.plus(1));
                                break;
                            }
                        }
                    L_outer10:
                        for (; 1 != 0;){
                            if (p.get() == 0)
                            {
                                /*goto L1*/throw Dispatch0.INSTANCE;
                            }
                            if (((p.get() & 0xFF) == 10))
                            {
                                p.postInc();
                                if (((p.get() & 0xFF) == 10) && (this.summary == null) && (namelen == 0) && (inCode == 0))
                                {
                                    pend = pcopy(p);
                                    p.postInc();
                                    /*goto L1*/throw Dispatch0.INSTANCE;
                                }
                                break;
                            }
                            p.postInc();
                            pend = pcopy(p);
                        }
                        p = pcopy(skipwhitespace(p));
                    }
                }
                catch(Dispatch0 __d){}
            /*L1:*/
                if ((namelen != 0) || (pstart.lessThan(pend)))
                {
                    Section s = null;
                    if (iequals(new ByteSlice("Params"), name.slice(0,namelen)))
                    {
                        s = new ParamSection();
                    }
                    else if (iequals(new ByteSlice("Macros"), name.slice(0,namelen)))
                    {
                        s = new MacroSection();
                    }
                    else
                    {
                        s = new Section();
                    }
                    s.name = pcopy(name);
                    s.namelen = namelen;
                    s._body = pcopy(pstart);
                    s.bodylen = ((pend.minus(pstart)));
                    s.nooutput = 0;
                    this.sections.push(s);
                    if ((this.summary == null) && (namelen == 0))
                    {
                        this.summary = s;
                    }
                }
                if (idlen != 0)
                {
                    name = pcopy(idstart);
                    namelen = idlen;
                }
                else
                {
                    name = null;
                    namelen = 0;
                    if (p.get() == 0)
                    {
                        break;
                    }
                }
            }
        }

        // Erasure: writeSections<Ptr, Ptr, Ptr>
        public  void writeSections(Ptr<Scope> sc, DArray<Dsymbol> a, OutBuffer buf) {
            assert((a).length != 0);
            Loc loc = (a).get(0).loc.copy();
            {
                dmodule.Module m = (a).get(0).isModule();
                if ((m) != null)
                {
                    if (m.md != null)
                    {
                        loc.opAssign((m.md.get()).loc.value.copy());
                    }
                }
            }
            int offset1 = (buf).offset;
            (buf).writestring(new ByteSlice("$(DDOC_SECTIONS "));
            int offset2 = (buf).offset;
            {
                int i = 0;
                for (; (i < this.sections.length);i++){
                    Section sec = this.sections.get(i);
                    if (sec.nooutput != 0)
                    {
                        continue;
                    }
                    if ((sec.namelen == 0) && (i == 0))
                    {
                        (buf).writestring(new ByteSlice("$(DDOC_SUMMARY "));
                        int o = (buf).offset;
                        (buf).write(sec._body, sec.bodylen);
                        escapeStrayParenthesis(loc, buf, o, true);
                        highlightText(sc, a, loc, buf, o);
                        (buf).writestring(new ByteSlice(")"));
                    }
                    else
                    {
                        sec.write(loc, ptr(this), sc, a, buf);
                    }
                }
            }
            {
                int i = 0;
                for (; (i < (a).length);i++){
                    Dsymbol s = (a).get(i);
                    {
                        Dsymbol td = getEponymousParent(s);
                        if ((td) != null)
                        {
                            s = td;
                        }
                    }
                    {
                        UnitTestDeclaration utd = s.ddocUnittest;
                        for (; utd != null;utd = utd.ddocUnittest){
                            if ((utd.protection.kind == Prot.Kind.private_) || (utd.comment == null) || (utd.fbody.value == null))
                            {
                                continue;
                            }
                            BytePtr c = pcopy(utd.comment);
                            for (; ((c.get() & 0xFF) == 32) || ((c.get() & 0xFF) == 9) || ((c.get() & 0xFF) == 10) || ((c.get() & 0xFF) == 13);) {
                                c.plusAssign(1);
                            }
                            (buf).writestring(new ByteSlice("$(DDOC_EXAMPLES "));
                            int o = (buf).offset;
                            (buf).writestring(c);
                            if (utd.codedoc != null)
                            {
                                BytePtr codedoc = pcopy(stripLeadingNewlines(utd.codedoc));
                                int n = getCodeIndent(codedoc);
                                for (; n-- != 0;) {
                                    (buf).writeByte(32);
                                }
                                (buf).writestring(new ByteSlice("----\n"));
                                (buf).writestring(codedoc);
                                (buf).writestring(new ByteSlice("----\n"));
                                highlightText(sc, a, loc, buf, o);
                            }
                            (buf).writestring(new ByteSlice(")"));
                        }
                    }
                }
            }
            if (((buf).offset == offset2))
            {
                (buf).offset = offset1;
                (buf).writestring(new ByteSlice("\n"));
            }
            else
            {
                (buf).writestring(new ByteSlice(")"));
            }
        }

        public DocComment(){ }
        public DocComment copy(){
            DocComment r = new DocComment();
            r.sections = sections.copy();
            r.summary = summary;
            r.copyright = copyright;
            r.macros = macros;
            r.pmacrotable = pmacrotable;
            r.escapetable = escapetable;
            r.a = a.copy();
            return r;
        }
        public DocComment(DArray<Section> sections, Section summary, Section copyright, Section macros, Ptr<Ptr<Macro>> pmacrotable, Ptr<Escape> escapetable, DArray<Dsymbol> a) {
            this.sections = sections;
            this.summary = summary;
            this.copyright = copyright;
            this.macros = macros;
            this.pmacrotable = pmacrotable;
            this.escapetable = escapetable;
            this.a = ref(a);
        }

        public DocComment opAssign(DocComment that) {
            this.sections = that.sections;
            this.summary = that.summary;
            this.copyright = that.copyright;
            this.macros = that.macros;
            this.pmacrotable = that.pmacrotable;
            this.escapetable = that.escapetable;
            this.a = that.a;
            return this;
        }
    }
    // Erasure: isDitto<Ptr>
    public static boolean isDitto(BytePtr comment) {
        if (comment != null)
        {
            BytePtr p = pcopy(skipwhitespace(comment));
            if ((Port.memicmp(p, new BytePtr("ditto"), 5) == 0) && ((skipwhitespace(p.plus(5)).get() & 0xFF) == 0))
            {
                return true;
            }
        }
        return false;
    }

    // Erasure: skipwhitespace<Ptr>
    public static BytePtr skipwhitespace(BytePtr p) {
        return skipwhitespace(toDString(p)).getPtr(0);
    }

    // Erasure: skipwhitespace<Array>
    public static ByteSlice skipwhitespace(ByteSlice p) {
        {
            ByteSlice __r1066 = p.copy();
            int __key1065 = 0;
            for (; (__key1065 < __r1066.getLength());__key1065 += 1) {
                byte c = __r1066.get(__key1065);
                int idx = __key1065;
                switch ((c & 0xFF))
                {
                    case 32:
                    case 9:
                    case 10:
                        continue;
                    default:
                    return p.slice(idx,p.getLength());
                }
            }
        }
        return p.slice(p.getLength(),p.getLength());
    }

    // Erasure: skipChars<Ptr, int, Array>
    public static int skipChars(OutBuffer buf, int i, ByteSlice chars) {
    /*Outer:*/
        {
            ByteSlice __r1068 = (buf).peekSlice().slice(i,(buf).peekSlice().getLength()).copy();
            int __key1067 = 0;
            for (; (__key1067 < __r1068.getLength());__key1067 += 1) {
                byte c = __r1068.get(__key1067);
                int j = __key1067;
                {
                    ByteSlice __r1069 = chars.copy();
                    int __key1070 = 0;
                    for (; (__key1070 < __r1069.getLength());__key1070 += 1) {
                        byte d = __r1069.get(__key1070);
                        if (((d & 0xFF) == (c & 0xFF)))
                        {
                            continue Outer;
                        }
                    }
                }
                return i + j;
            }
        }
        return (buf).offset;
    }

    // Erasure: replaceChar<Array, byte, Array>
    public static ByteSlice replaceChar(ByteSlice s, byte c, ByteSlice r) {
        int count = 0;
        {
            ByteSlice __r1071 = s.copy();
            int __key1072 = 0;
            for (; (__key1072 < __r1071.getLength());__key1072 += 1) {
                byte sc = __r1071.get(__key1072);
                if (((sc & 0xFF) == (c & 0xFF)))
                {
                    count += 1;
                }
            }
        }
        if ((count == 0))
        {
            return s;
        }
        Ref<ByteSlice> result = ref(new ByteSlice().copy());
        reserve(result, s.getLength() - count + r.getLength() * count);
        int start = 0;
        {
            ByteSlice __r1074 = s.copy();
            int __key1073 = 0;
            for (; (__key1073 < __r1074.getLength());__key1073 += 1) {
                byte sc = __r1074.get(__key1073);
                int i = __key1073;
                if (((sc & 0xFF) == (c & 0xFF)))
                {
                    result.value.append(s.slice(start,i));
                    result.value.append(toByteSlice(r));
                    start = i + 1;
                }
            }
        }
        result.value.append(s.slice(start,s.getLength()));
        return toByteSlice(result);
    }

    // Erasure: toLowercase<Array>
    public static ByteSlice toLowercase(ByteSlice s) {
        Ref<ByteSlice> lower = ref(new ByteSlice().copy());
        {
            int __key1075 = 0;
            int __limit1076 = s.getLength();
            for (; (__key1075 < __limit1076);__key1075 += 1) {
                int i = __key1075;
                byte c = s.get(i);
                if (((c & 0xFF) >= 65) && ((c & 0xFF) <= 90))
                {
                    if (lower.value.getLength() == 0)
                    {
                        reserve(lower, s.getLength());
                    }
                    lower.value.append(s.slice(lower.value.getLength(),i));
                    (c & 0xFF) += 32;
                    lower.value.append(c);
                }
            }
        }
        if (lower.value.getLength() != 0)
        {
            lower.value.append(s.slice(lower.value.getLength(),s.getLength()));
        }
        else
        {
            lower.value = s.copy();
        }
        return lower.value;
    }

    // Erasure: getMarkdownIndent<Ptr, int, int>
    public static int getMarkdownIndent(OutBuffer buf, int from, int to) {
        ByteSlice slice = (buf).peekSlice().copy();
        if ((to > slice.getLength()))
        {
            to = slice.getLength();
        }
        int indent = 0;
        {
            ByteSlice __r1077 = slice.slice(from,to).copy();
            int __key1078 = 0;
            for (; (__key1078 < __r1077.getLength());__key1078 += 1) {
                byte c = __r1077.get(__key1078);
                indent += ((c & 0xFF) == 9) ? 4 - indent % 4 : 1;
            }
        }
        return indent;
    }

    // Erasure: skiptoident<Ptr, int>
    public static int skiptoident(OutBuffer buf, int i) {
        Ref<Integer> i_ref = ref(i);
        ByteSlice slice = (buf).peekSlice().copy();
        for (; (i_ref.value < slice.getLength());){
            Ref<Integer> c = ref(0x0ffff);
            int oi = i_ref.value;
            if (utf_decodeChar(slice.getPtr(0), slice.getLength(), i_ref, c) != null)
            {
                break;
            }
            if ((c.value >= 128))
            {
                if (!isUniAlpha(c.value))
                {
                    continue;
                }
            }
            else if (!((isalpha(c.value) != 0) || (c.value == 95) || (c.value == 10)))
            {
                continue;
            }
            i_ref.value = oi;
            break;
        }
        return i_ref.value;
    }

    // Erasure: skippastident<Ptr, int>
    public static int skippastident(OutBuffer buf, int i) {
        Ref<Integer> i_ref = ref(i);
        ByteSlice slice = (buf).peekSlice().copy();
        for (; (i_ref.value < slice.getLength());){
            Ref<Integer> c = ref(0x0ffff);
            int oi = i_ref.value;
            if (utf_decodeChar(slice.getPtr(0), slice.getLength(), i_ref, c) != null)
            {
                break;
            }
            if ((c.value >= 128))
            {
                if (isUniAlpha(c.value))
                {
                    continue;
                }
            }
            else if ((isalnum(c.value) != 0) || (c.value == 95))
            {
                continue;
            }
            i_ref.value = oi;
            break;
        }
        return i_ref.value;
    }

    // Erasure: skipPastIdentWithDots<Ptr, int>
    public static int skipPastIdentWithDots(OutBuffer buf, int i) {
        Ref<Integer> i_ref = ref(i);
        ByteSlice slice = (buf).peekSlice().copy();
        boolean lastCharWasDot = false;
        for (; (i_ref.value < slice.getLength());){
            Ref<Integer> c = ref(0x0ffff);
            int oi = i_ref.value;
            if (utf_decodeChar(slice.getPtr(0), slice.getLength(), i_ref, c) != null)
            {
                break;
            }
            if ((c.value == 46))
            {
                if (lastCharWasDot)
                {
                    i_ref.value = oi;
                    break;
                }
                lastCharWasDot = true;
                continue;
            }
            else
            {
                if ((c.value >= 128))
                {
                    if (isUniAlpha(c.value))
                    {
                        lastCharWasDot = false;
                        continue;
                    }
                }
                else if ((isalnum(c.value) != 0) || (c.value == 95))
                {
                    lastCharWasDot = false;
                    continue;
                }
                i_ref.value = oi;
                break;
            }
        }
        if (lastCharWasDot)
        {
            return i_ref.value - 1;
        }
        return i_ref.value;
    }

    // Erasure: skippastURL<Ptr, int>
    public static int skippastURL(OutBuffer buf, int i) {
        ByteSlice slice = (buf).peekSlice().slice(i,(buf).peekSlice().getLength()).copy();
        int j = 0;
        boolean sawdot = false;
        try {
            if ((slice.getLength() > 7) && (Port.memicmp(slice.getPtr(0), new BytePtr("http://"), 7) == 0))
            {
                j = 7;
            }
            else if ((slice.getLength() > 8) && (Port.memicmp(slice.getPtr(0), new BytePtr("https://"), 8) == 0))
            {
                j = 8;
            }
            else
            {
                /*goto Lno*/throw Dispatch0.INSTANCE;
            }
            for (; (j < slice.getLength());j++){
                byte c = slice.get(j);
                if (isalnum((c & 0xFF)) != 0)
                {
                    continue;
                }
                if (((c & 0xFF) == 45) || ((c & 0xFF) == 95) || ((c & 0xFF) == 63) || ((c & 0xFF) == 61) || ((c & 0xFF) == 37) || ((c & 0xFF) == 38) || ((c & 0xFF) == 47) || ((c & 0xFF) == 43) || ((c & 0xFF) == 35) || ((c & 0xFF) == 126))
                {
                    continue;
                }
                if (((c & 0xFF) == 46))
                {
                    sawdot = true;
                    continue;
                }
                break;
            }
            if (sawdot)
            {
                return i + j;
            }
        }
        catch(Dispatch0 __d){}
    /*Lno:*/
        return i;
    }

    // Erasure: removeBlankLineMacro<Ptr, int, int>
    public static void removeBlankLineMacro(OutBuffer buf, Ref<Integer> iAt, Ref<Integer> i) {
        if (iAt.value == 0)
        {
            return ;
        }
        int macroLength = 17;
        (buf).remove(iAt.value, 17);
        if ((i.value > iAt.value))
        {
            i.value -= 17;
        }
        iAt.value = 0;
    }

    // Erasure: replaceMarkdownThematicBreak<Ptr, int, int, Loc>
    public static boolean replaceMarkdownThematicBreak(OutBuffer buf, Ref<Integer> i, int iLineStart, Loc loc) {
        if (!global.params.markdown)
        {
            return false;
        }
        ByteSlice slice = (buf).peekSlice().copy();
        byte c = (buf).data.get(i.value);
        int j = i.value + 1;
        int repeat = 1;
        for (; (j < slice.getLength());j++){
            if ((((buf).data.get(j) & 0xFF) == (c & 0xFF)))
            {
                repeat += 1;
            }
            else if ((((buf).data.get(j) & 0xFF) != 32) && (((buf).data.get(j) & 0xFF) != 9))
            {
                break;
            }
        }
        if ((repeat >= 3))
        {
            if ((j >= (buf).offset) || (((buf).data.get(j) & 0xFF) == 10) || (((buf).data.get(j) & 0xFF) == 13))
            {
                if (global.params.vmarkdown)
                {
                    ByteSlice s = (buf).peekSlice().slice(i.value,j).copy();
                    message(loc, new BytePtr("Ddoc: converted '%.*s' to a thematic break"), s.getLength(), s.getPtr(0));
                }
                (buf).remove(iLineStart, j - iLineStart);
                i.value = (buf).insert(iLineStart, new ByteSlice("$(HR)")) - 1;
                return true;
            }
        }
        return false;
    }

    // Erasure: detectAtxHeadingLevel<Ptr, int>
    public static int detectAtxHeadingLevel(OutBuffer buf, int i) {
        if (!global.params.markdown)
        {
            return 0;
        }
        int iHeadingStart = i;
        int iAfterHashes = skipChars(buf, i, new ByteSlice("#"));
        int headingLevel = (iAfterHashes - iHeadingStart);
        if ((headingLevel > 6))
        {
            return 0;
        }
        int iTextStart = skipChars(buf, iAfterHashes, new ByteSlice(" \u0009"));
        boolean emptyHeading = (((buf).data.get(iTextStart) & 0xFF) == 13) || (((buf).data.get(iTextStart) & 0xFF) == 10);
        if (!emptyHeading && (iTextStart == iAfterHashes))
        {
            return 0;
        }
        return headingLevel;
    }

    // Erasure: removeAnyAtxHeadingSuffix<Ptr, int>
    public static void removeAnyAtxHeadingSuffix(OutBuffer buf, int i) {
        int j = i;
        int iSuffixStart = 0;
        int iWhitespaceStart = j;
        ByteSlice slice = (buf).peekSlice().copy();
        for (; (j < slice.getLength());j++){
            switch ((slice.get(j) & 0xFF))
            {
                case 35:
                    if ((iWhitespaceStart != 0) && (iSuffixStart == 0))
                    {
                        iSuffixStart = j;
                    }
                    continue;
                case 32:
                case 9:
                    if (iWhitespaceStart == 0)
                    {
                        iWhitespaceStart = j;
                    }
                    continue;
                case 13:
                case 10:
                    break;
                default:
                iSuffixStart = 0;
                iWhitespaceStart = 0;
                continue;
            }
            break;
        }
        if (iSuffixStart != 0)
        {
            (buf).remove(iWhitespaceStart, j - iWhitespaceStart);
        }
    }

    // Erasure: endMarkdownHeading<Ptr, int, int, Loc, int>
    public static void endMarkdownHeading(OutBuffer buf, int iStart, Ref<Integer> iEnd, Loc loc, Ref<Integer> headingLevel) {
        if (!global.params.markdown)
        {
            return ;
        }
        if (global.params.vmarkdown)
        {
            ByteSlice s = (buf).peekSlice().slice(iStart,iEnd.value).copy();
            message(loc, new BytePtr("Ddoc: added heading '%.*s'"), s.getLength(), s.getPtr(0));
        }
        ByteSlice heading = new ByteSlice("$(H0 ");
        heading.set(3, (byte)(48 + headingLevel.value));
        (buf).insert(iStart, toByteSlice(heading));
        iEnd.value += 5;
        int iBeforeNewline = iEnd.value;
        for (; (((buf).data.get(iBeforeNewline - 1) & 0xFF) == 13) || (((buf).data.get(iBeforeNewline - 1) & 0xFF) == 10);) {
            iBeforeNewline -= 1;
        }
        (buf).insert(iBeforeNewline, new ByteSlice(")"));
        headingLevel.value = 0;
    }

    // Erasure: endAllMarkdownQuotes<Ptr, int, int>
    public static int endAllMarkdownQuotes(OutBuffer buf, int i, Ref<Integer> quoteLevel) {
        int length = quoteLevel.value;
        for (; (quoteLevel.value > 0);quoteLevel.value -= 1) {
            i = (buf).insert(i, new ByteSlice(")"));
        }
        return length;
    }

    // Erasure: endAllListsAndQuotes<Ptr, int, Array, int, int>
    public static int endAllListsAndQuotes(OutBuffer buf, Ref<Integer> i, Slice<MarkdownList> nestedLists, Ref<Integer> quoteLevel, Ref<Integer> quoteMacroLevel) {
        Ref<Slice<MarkdownList>> nestedLists_ref = ref(nestedLists);
        quoteMacroLevel.value = 0;
        quoteMacroLevel.value = 0;
        int i0 = i.value;
        i.value += MarkdownList.endAllNestedLists(buf, i.value, nestedLists_ref);
        i.value += endAllMarkdownQuotes(buf, i.value, quoteLevel);
        return i.value - i0;
    }

    // Erasure: replaceMarkdownEmphasis<Ptr, Loc, Array, int>
    public static int replaceMarkdownEmphasis(OutBuffer buf, Loc loc, Slice<MarkdownDelimiter> inlineDelimiters, int downToLevel) {
        if (!global.params.markdown)
        {
            return 0;
        }
        Function2<Ref<MarkdownDelimiter>,Ref<MarkdownDelimiter>,Integer> replaceEmphasisPair = new Function2<Ref<MarkdownDelimiter>,Ref<MarkdownDelimiter>,Integer>() {
            public Integer invoke(MarkdownDelimiter start, MarkdownDelimiter end) {
             {
                int count = (start.count == 1) || (end.count == 1) ? 1 : 2;
                Ref<Integer> iStart = ref(start.iStart);
                Ref<Integer> iEnd = ref(end.iStart);
                end.count -= count;
                start.count -= count;
                iStart.value += start.count;
                if (start.count == 0)
                {
                    start.type = (byte)0;
                }
                if (end.count == 0)
                {
                    end.type = (byte)0;
                }
                if (global.params.vmarkdown)
                {
                    ByteSlice s = (buf).peekSlice().slice(iStart.value + count,iEnd.value).copy();
                    message(loc, new BytePtr("Ddoc: emphasized text '%.*s'"), s.getLength(), s.getPtr(0));
                }
                (buf).remove(iStart.value, count);
                iEnd.value -= count;
                (buf).remove(iEnd.value, count);
                ByteSlice macroName = (count >= 2) ? new ByteSlice("$(STRONG ") : new ByteSlice("$(EM ").copy();
                (buf).insert(iEnd.value, new ByteSlice(")"));
                (buf).insert(iStart.value, toByteSlice(macroName));
                int delta = 1 + macroName.getLength() - (count + count);
                end.iStart += count;
                return delta;
            }}

        };
        int delta = 0;
        int start = inlineDelimiters.getLength() - 1;
        for (; (start >= downToLevel);){
            for (; (start >= downToLevel) && ((inlineDelimiters.get(start).type & 0xFF) != 42) || !inlineDelimiters.get(start).leftFlanking;) {
                start -= 1;
            }
            if ((start < downToLevel))
            {
                break;
            }
            int end = start + 1;
            for (; (end < inlineDelimiters.getLength()) && ((inlineDelimiters.get(end).type & 0xFF) != (inlineDelimiters.get(start).type & 0xFF)) || (inlineDelimiters.get(end).macroLevel != inlineDelimiters.get(start).macroLevel) || !inlineDelimiters.get(end).rightFlanking;) {
                end += 1;
            }
            if ((end == inlineDelimiters.getLength()))
            {
                if (!inlineDelimiters.get(start).rightFlanking)
                {
                    inlineDelimiters.get(start).type = (byte)0;
                }
                start -= 1;
                continue;
            }
            if (inlineDelimiters.get(start).leftFlanking && inlineDelimiters.get(start).rightFlanking || inlineDelimiters.get(end).leftFlanking && inlineDelimiters.get(end).rightFlanking && ((inlineDelimiters.get(start).count + inlineDelimiters.get(end).count) % 3 == 0))
            {
                start -= 1;
                continue;
            }
            int delta0 = replaceEmphasisPair.invoke(inlineDelimiters.get(start), inlineDelimiters.get(end));
            for (; (end < inlineDelimiters.getLength());end += 1) {
                inlineDelimiters.get(end).iStart += delta0;
            }
            delta += delta0;
        }
        inlineDelimiters.getLength() = downToLevel;
        return delta;
    }

    // defaulted all parameters starting with #4
    public static int replaceMarkdownEmphasis(OutBuffer buf, Loc loc, Slice<MarkdownDelimiter> inlineDelimiters) {
        return replaceMarkdownEmphasis(buf, loc, inlineDelimiters, 0);
    }

    // Erasure: isIdentifier<Ptr, Ptr, int>
    public static boolean isIdentifier(DArray<Dsymbol> a, BytePtr p, int len) {
        {
            Slice<Dsymbol> __r1079 = (a).opSlice().copy();
            int __key1080 = 0;
            for (; (__key1080 < __r1079.getLength());__key1080 += 1) {
                Dsymbol member = __r1079.get(__key1080);
                {
                    Import imp = member.isImport();
                    if ((imp) != null)
                    {
                        if (imp.aliasId != null)
                        {
                            if (__equals(p.slice(0,len), imp.aliasId.asString()))
                            {
                                return true;
                            }
                        }
                        else
                        {
                            ByteSlice fullyQualifiedImport = new ByteSlice().copy();
                            if ((imp.packages != null) && ((imp.packages).length != 0))
                            {
                                {
                                    Slice<Identifier> __r1081 = (imp.packages).opSlice().copy();
                                    int __key1082 = 0;
                                    for (; (__key1082 < __r1081.getLength());__key1082 += 1) {
                                        Identifier pid = __r1081.get(__key1082);
                                        fullyQualifiedImport.append(toByteSlice((concat(pid.asString(), new ByteSlice(".")))));
                                    }
                                }
                            }
                            fullyQualifiedImport.append(toByteSlice(imp.id.asString()));
                            if (__equals(p.slice(0,len), toByteSlice(fullyQualifiedImport)))
                            {
                                return true;
                            }
                        }
                    }
                    else if (member.ident != null)
                    {
                        if (__equals(p.slice(0,len), member.ident.asString()))
                        {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    // Erasure: isKeyword<Ptr, int>
    public static boolean isKeyword(BytePtr p, int len) {
        Slice<ByteSlice> table = slice(new ByteSlice[]{new ByteSlice("true"), new ByteSlice("false"), new ByteSlice("null")});
        {
            Slice<ByteSlice> __r1083 = table.copy();
            int __key1084 = 0;
            for (; (__key1084 < __r1083.getLength());__key1084 += 1) {
                ByteSlice s = __r1083.get(__key1084).copy();
                if (__equals(p.slice(0,len), toByteSlice(s)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    // Erasure: isTypeFunction<Dsymbol>
    public static TypeFunction isTypeFunction(Dsymbol s) {
        FuncDeclaration f = s.isFuncDeclaration();
        if ((f != null) && (f.type != null))
        {
            Type t = f.originalType != null ? f.originalType : f.type;
            if (((t.ty & 0xFF) == ENUMTY.Tfunction))
            {
                return ((TypeFunction)t);
            }
        }
        return null;
    }

    // Erasure: isFunctionParameter<Dsymbol, Ptr, int>
    public static Parameter isFunctionParameter(Dsymbol s, BytePtr p, int len) {
        TypeFunction tf = isTypeFunction(s);
        if ((tf != null) && (tf.parameterList.parameters != null))
        {
            {
                Slice<Parameter> __r1085 = (tf.parameterList.parameters).opSlice().copy();
                int __key1086 = 0;
                for (; (__key1086 < __r1085.getLength());__key1086 += 1) {
                    Parameter fparam = __r1085.get(__key1086);
                    if ((fparam.ident != null) && __equals(p.slice(0,len), fparam.ident.asString()))
                    {
                        return fparam;
                    }
                }
            }
        }
        return null;
    }

    // Erasure: isFunctionParameter<Ptr, Ptr, int>
    public static Parameter isFunctionParameter(DArray<Dsymbol> a, BytePtr p, int len) {
        {
            int i = 0;
            for (; (i < (a).length);i++){
                Parameter fparam = isFunctionParameter((a).get(i), p, len);
                if (fparam != null)
                {
                    return fparam;
                }
            }
        }
        return null;
    }

    // Erasure: isEponymousFunctionParameter<Ptr, Ptr, int>
    public static Parameter isEponymousFunctionParameter(DArray<Dsymbol> a, BytePtr p, int len) {
        {
            int i = 0;
            for (; (i < (a).length);i++){
                TemplateDeclaration td = (a).get(i).isTemplateDeclaration();
                if ((td != null) && (td.onemember != null))
                {
                    td = td.onemember.isTemplateDeclaration();
                }
                if (td == null)
                {
                    AliasDeclaration ad = (a).get(i).isAliasDeclaration();
                    if ((ad != null) && (ad.aliassym != null))
                    {
                        td = ad.aliassym.isTemplateDeclaration();
                    }
                }
                for (; td != null;){
                    Dsymbol sym = getEponymousMember(td);
                    if (sym != null)
                    {
                        Parameter fparam = isFunctionParameter(sym, p, len);
                        if (fparam != null)
                        {
                            return fparam;
                        }
                    }
                    td = td.overnext.value;
                }
            }
        }
        return null;
    }

    // Erasure: isTemplateParameter<Ptr, Ptr, int>
    public static TemplateParameter isTemplateParameter(DArray<Dsymbol> a, BytePtr p, int len) {
        {
            int i = 0;
            for (; (i < (a).length);i++){
                TemplateDeclaration td = (a).get(i).isTemplateDeclaration();
                if (td == null)
                {
                    td = getEponymousParent((a).get(i));
                }
                if ((td != null) && (td.origParameters != null))
                {
                    {
                        Slice<TemplateParameter> __r1087 = (td.origParameters).opSlice().copy();
                        int __key1088 = 0;
                        for (; (__key1088 < __r1087.getLength());__key1088 += 1) {
                            TemplateParameter tp = __r1087.get(__key1088);
                            if ((tp.ident != null) && __equals(p.slice(0,len), tp.ident.asString()))
                            {
                                return tp;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    // Erasure: isReservedName<Array>
    public static boolean isReservedName(ByteSlice str) {
        Slice<ByteSlice> table = slice(new ByteSlice[]{new ByteSlice("__ctor"), new ByteSlice("__dtor"), new ByteSlice("__postblit"), new ByteSlice("__invariant"), new ByteSlice("__unitTest"), new ByteSlice("__require"), new ByteSlice("__ensure"), new ByteSlice("__dollar"), new ByteSlice("__ctfe"), new ByteSlice("__withSym"), new ByteSlice("__result"), new ByteSlice("__returnLabel"), new ByteSlice("__vptr"), new ByteSlice("__monitor"), new ByteSlice("__gate"), new ByteSlice("__xopEquals"), new ByteSlice("__xopCmp"), new ByteSlice("__LINE__"), new ByteSlice("__FILE__"), new ByteSlice("__MODULE__"), new ByteSlice("__FUNCTION__"), new ByteSlice("__PRETTY_FUNCTION__"), new ByteSlice("__DATE__"), new ByteSlice("__TIME__"), new ByteSlice("__TIMESTAMP__"), new ByteSlice("__VENDOR__"), new ByteSlice("__VERSION__"), new ByteSlice("__EOF__"), new ByteSlice("__CXXLIB__"), new ByteSlice("__LOCAL_SIZE"), new ByteSlice("___tls_get_addr"), new ByteSlice("__entrypoint")}).copy();
        {
            Slice<ByteSlice> __r1089 = table.copy();
            int __key1090 = 0;
            for (; (__key1090 < __r1089.getLength());__key1090 += 1) {
                ByteSlice s = __r1089.get(__key1090).copy();
                if (__equals(str, toByteSlice(s)))
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static class MarkdownDelimiter
    {
        public int iStart = 0;
        public int count = 0;
        public int macroLevel = 0;
        public boolean leftFlanking = false;
        public boolean rightFlanking = false;
        public boolean atParagraphStart = false;
        public byte type = 0;
        // Erasure: isValid<>
        public  boolean isValid() {
            return this.count != 0;
        }

        // Erasure: invalidate<>
        public  void invalidate() {
            this.count = 0;
        }

        public MarkdownDelimiter(){ }
        public MarkdownDelimiter copy(){
            MarkdownDelimiter r = new MarkdownDelimiter();
            r.iStart = iStart;
            r.count = count;
            r.macroLevel = macroLevel;
            r.leftFlanking = leftFlanking;
            r.rightFlanking = rightFlanking;
            r.atParagraphStart = atParagraphStart;
            r.type = type;
            return r;
        }
        public MarkdownDelimiter(int iStart, int count, int macroLevel, boolean leftFlanking, boolean rightFlanking, boolean atParagraphStart, byte type) {
            this.iStart = iStart;
            this.count = count;
            this.macroLevel = macroLevel;
            this.leftFlanking = leftFlanking;
            this.rightFlanking = rightFlanking;
            this.atParagraphStart = atParagraphStart;
            this.type = type;
        }

        public MarkdownDelimiter opAssign(MarkdownDelimiter that) {
            this.iStart = that.iStart;
            this.count = that.count;
            this.macroLevel = that.macroLevel;
            this.leftFlanking = that.leftFlanking;
            this.rightFlanking = that.rightFlanking;
            this.atParagraphStart = that.atParagraphStart;
            this.type = that.type;
            return this;
        }
    }
    public static class MarkdownList
    {
        public ByteSlice orderedStart = new ByteSlice();
        public Ref<Integer> iStart = ref(0);
        public int iContentStart = 0;
        public int delimiterIndent = 0;
        public int contentIndent = 0;
        public int macroLevel = 0;
        public byte type = 0;
        // Erasure: isValid<>
        public  boolean isValid() {
            return (this.type & 0xFF) != 255;
        }

        // Erasure: parseItem<Ptr, int, int>
        public static MarkdownList parseItem(OutBuffer buf, int iLineStart, int i) {
            if (!global.params.markdown)
            {
                return new MarkdownList(new ByteSlice(), 0, 0, 0, 0, 0, (byte)255);
            }
            if ((((buf).data.get(i) & 0xFF) == 43) || (((buf).data.get(i) & 0xFF) == 45) || (((buf).data.get(i) & 0xFF) == 42))
            {
                return parseUnorderedListItem(buf, iLineStart, i);
            }
            else
            {
                return parseOrderedListItem(buf, iLineStart, i);
            }
        }

        // Erasure: isAtItemInThisList<Ptr, int, int>
        public  boolean isAtItemInThisList(OutBuffer buf, int iLineStart, int i) {
            MarkdownList item = ((this.type & 0xFF) == 46) || ((this.type & 0xFF) == 41) ? parseOrderedListItem(buf, iLineStart, i) : parseUnorderedListItem(buf, iLineStart, i).copy();
            if (((item.type & 0xFF) == (this.type & 0xFF)))
            {
                return (item.delimiterIndent < this.contentIndent) && (item.contentIndent > this.delimiterIndent);
            }
            return false;
        }

        // Erasure: startItem<Ptr, int, int, int, Array, Loc>
        public  boolean startItem(OutBuffer buf, Ref<Integer> iLineStart, Ref<Integer> i, Ref<Integer> iPrecedingBlankLine, Slice<MarkdownList> nestedLists, Loc loc) {
            (buf).remove(this.iStart.value, this.iContentStart - this.iStart.value);
            if ((nestedLists.getLength() == 0) || (this.delimiterIndent >= nestedLists.get(nestedLists.getLength() - 1).contentIndent) || __equals((buf).data.slice(iLineStart.value - 4,iLineStart.value), new ByteSlice("$(LI")))
            {
                nestedLists.append(this);
                if (((this.type & 0xFF) == 46))
                {
                    if (this.orderedStart.getLength() != 0)
                    {
                        this.iStart.value = (buf).insert(this.iStart.value, new ByteSlice("$(OL_START "));
                        this.iStart.value = (buf).insert(this.iStart.value, toByteSlice(this.orderedStart));
                        this.iStart.value = (buf).insert(this.iStart.value, new ByteSlice(",\n"));
                    }
                    else
                    {
                        this.iStart.value = (buf).insert(this.iStart.value, new ByteSlice("$(OL\n"));
                    }
                }
                else
                {
                    this.iStart.value = (buf).insert(this.iStart.value, new ByteSlice("$(UL\n"));
                }
                removeBlankLineMacro(buf, iPrecedingBlankLine, iStart);
            }
            else if (nestedLists.getLength() != 0)
            {
                nestedLists.get(nestedLists.getLength() - 1).delimiterIndent = this.delimiterIndent;
                nestedLists.get(nestedLists.getLength() - 1).contentIndent = this.contentIndent;
            }
            this.iStart.value = (buf).insert(this.iStart.value, new ByteSlice("$(LI\n"));
            i.value = this.iStart.value - 1;
            iLineStart.value = i.value;
            if (global.params.vmarkdown)
            {
                int iEnd = this.iStart.value;
                for (; (iEnd < (buf).offset) && (((buf).data.get(iEnd) & 0xFF) != 13) && (((buf).data.get(iEnd) & 0xFF) != 10);) {
                    iEnd += 1;
                }
                ByteSlice s = (buf).peekSlice().slice(this.iStart.value,iEnd).copy();
                message(loc, new BytePtr("Ddoc: starting list item '%.*s'"), s.getLength(), s.getPtr(0));
            }
            return true;
        }

        // Erasure: endAllNestedLists<Ptr, int, Array>
        public static int endAllNestedLists(OutBuffer buf, int i, Slice<MarkdownList> nestedLists) {
            int iStart = i;
            for (; nestedLists.getLength() != 0;nestedLists.getLength() = nestedLists.getLength() - 1) {
                i = (buf).insert(i, new ByteSlice(")\n)"));
            }
            return i - iStart;
        }

        // Erasure: handleSiblingOrEndingList<Ptr, int, int, Array>
        public static void handleSiblingOrEndingList(OutBuffer buf, Ref<Integer> i, Ref<Integer> iParagraphStart, Slice<MarkdownList> nestedLists) {
            int iAfterSpaces = skipChars(buf, i.value + 1, new ByteSlice(" \u0009"));
            if (nestedLists.get(nestedLists.getLength() - 1).isAtItemInThisList(buf, i.value + 1, iAfterSpaces))
            {
                i.value = (buf).insert(i.value, new ByteSlice(")"));
                iParagraphStart.value = skipChars(buf, i.value, new ByteSlice(" \u0009\r\n"));
            }
            else if ((iAfterSpaces >= (buf).offset) || (((buf).data.get(iAfterSpaces) & 0xFF) != 13) && (((buf).data.get(iAfterSpaces) & 0xFF) != 10))
            {
                int indent = getMarkdownIndent(buf, i.value + 1, iAfterSpaces);
                for (; (nestedLists.getLength() != 0) && (nestedLists.get(nestedLists.getLength() - 1).contentIndent > indent);){
                    i.value = (buf).insert(i.value, new ByteSlice(")\n)"));
                    nestedLists.getLength() = nestedLists.getLength() - 1;
                    iParagraphStart.value = skipChars(buf, i.value, new ByteSlice(" \u0009\r\n"));
                    if ((nestedLists.getLength() != 0) && nestedLists.get(nestedLists.getLength() - 1).isAtItemInThisList(buf, i.value + 1, iParagraphStart.value))
                    {
                        i.value = (buf).insert(i.value, new ByteSlice(")"));
                        iParagraphStart.value += 1;
                        break;
                    }
                }
            }
        }

        // Erasure: parseUnorderedListItem<Ptr, int, int>
        public static MarkdownList parseUnorderedListItem(OutBuffer buf, int iLineStart, int i) {
            if ((i + 1 < (buf).offset) && (((buf).data.get(i) & 0xFF) == 45) || (((buf).data.get(i) & 0xFF) == 42) || (((buf).data.get(i) & 0xFF) == 43) && (((buf).data.get(i + 1) & 0xFF) == 32) || (((buf).data.get(i + 1) & 0xFF) == 9) || (((buf).data.get(i + 1) & 0xFF) == 13) || (((buf).data.get(i + 1) & 0xFF) == 10))
            {
                int iContentStart = skipChars(buf, i + 1, new ByteSlice(" \u0009"));
                int delimiterIndent = getMarkdownIndent(buf, iLineStart, i);
                int contentIndent = getMarkdownIndent(buf, iLineStart, iContentStart);
                MarkdownList list = new MarkdownList(new ByteSlice(), iLineStart, iContentStart, delimiterIndent, contentIndent, 0, (byte)(buf).data.get(i)).copy();
                return list;
            }
            return new MarkdownList(new ByteSlice(), 0, 0, 0, 0, 0, (byte)255);
        }

        // Erasure: parseOrderedListItem<Ptr, int, int>
        public static MarkdownList parseOrderedListItem(OutBuffer buf, int iLineStart, int i) {
            int iAfterNumbers = skipChars(buf, i, new ByteSlice("0123456789"));
            if ((iAfterNumbers - i > 0) && (iAfterNumbers - i <= 9) && (iAfterNumbers + 1 < (buf).offset) && (((buf).data.get(iAfterNumbers) & 0xFF) == 46) && (((buf).data.get(iAfterNumbers + 1) & 0xFF) == 32) || (((buf).data.get(iAfterNumbers + 1) & 0xFF) == 9) || (((buf).data.get(iAfterNumbers + 1) & 0xFF) == 13) || (((buf).data.get(iAfterNumbers + 1) & 0xFF) == 10))
            {
                int iContentStart = skipChars(buf, iAfterNumbers + 1, new ByteSlice(" \u0009"));
                int delimiterIndent = getMarkdownIndent(buf, iLineStart, i);
                int contentIndent = getMarkdownIndent(buf, iLineStart, iContentStart);
                int iNumberStart = skipChars(buf, i, new ByteSlice("0"));
                if ((iNumberStart == iAfterNumbers))
                {
                    iNumberStart -= 1;
                }
                ByteSlice orderedStart = (buf).peekSlice().slice(iNumberStart,iAfterNumbers).copy();
                if (__equals(orderedStart, new ByteSlice("1")))
                {
                    orderedStart = new ByteSlice().copy();
                }
                return new MarkdownList(idup(orderedStart), iLineStart, iContentStart, delimiterIndent, contentIndent, 0, (byte)(buf).data.get(iAfterNumbers));
            }
            return new MarkdownList(new ByteSlice(), 0, 0, 0, 0, 0, (byte)255);
        }

        public MarkdownList(){ }
        public MarkdownList copy(){
            MarkdownList r = new MarkdownList();
            r.orderedStart = orderedStart.copy();
            r.iStart = iStart.copy();
            r.iContentStart = iContentStart;
            r.delimiterIndent = delimiterIndent;
            r.contentIndent = contentIndent;
            r.macroLevel = macroLevel;
            r.type = type;
            return r;
        }
        public MarkdownList(ByteSlice orderedStart, int iStart, int iContentStart, int delimiterIndent, int contentIndent, int macroLevel, byte type) {
            this.orderedStart = orderedStart;
            this.iStart = ref(iStart);
            this.iContentStart = iContentStart;
            this.delimiterIndent = delimiterIndent;
            this.contentIndent = contentIndent;
            this.macroLevel = macroLevel;
            this.type = type;
        }

        public MarkdownList opAssign(MarkdownList that) {
            this.orderedStart = that.orderedStart;
            this.iStart = that.iStart;
            this.iContentStart = that.iContentStart;
            this.delimiterIndent = that.delimiterIndent;
            this.contentIndent = that.contentIndent;
            this.macroLevel = that.macroLevel;
            this.type = that.type;
            return this;
        }
    }
    public static class MarkdownLink
    {
        public ByteSlice href = new ByteSlice();
        public ByteSlice title = new ByteSlice();
        public Ref<ByteSlice> label = ref(new ByteSlice());
        public Dsymbol symbol = null;
        // Erasure: replaceLink<Ptr, int, Loc, Array, int, MarkdownLinkReferences>
        public static boolean replaceLink(OutBuffer buf, Ref<Integer> i, Loc loc, Slice<MarkdownDelimiter> inlineDelimiters, int delimiterIndex, MarkdownLinkReferences linkReferences) {
            Ref<Slice<MarkdownDelimiter>> inlineDelimiters_ref = ref(inlineDelimiters);
            Ref<MarkdownLinkReferences> linkReferences_ref = ref(linkReferences);
            MarkdownDelimiter delimiter = inlineDelimiters_ref.value.get(delimiterIndex).copy();
            MarkdownLink link = new MarkdownLink();
            int iEnd = link.parseReferenceDefinition(buf, i.value, delimiter);
            if ((iEnd > i.value))
            {
                i.value = delimiter.iStart;
                link.storeAndReplaceDefinition(buf, i, iEnd, linkReferences_ref, loc);
                inlineDelimiters_ref.value.getLength() = delimiterIndex;
                return true;
            }
            iEnd = link.parseInlineLink(buf, i.value);
            if ((iEnd == i.value))
            {
                iEnd = link.parseReferenceLink(buf, i.value, delimiter);
                if ((iEnd > i.value))
                {
                    ByteSlice label = link.label.value.copy();
                    link.opAssign(linkReferences_ref.value.lookupReference(label, buf, i.value, loc).copy());
                    if ((link.href.getLength() == 0) && !delimiter.rightFlanking)
                    {
                        link.opAssign(linkReferences_ref.value.lookupSymbol(label).copy());
                    }
                    if (link.href.getLength() == 0)
                    {
                        return false;
                    }
                }
            }
            if ((iEnd == i.value))
            {
                return false;
            }
            int delta = replaceMarkdownEmphasis(buf, loc, inlineDelimiters_ref, delimiterIndex);
            iEnd += delta;
            i.value += delta;
            if (global.params.vmarkdown)
            {
                ByteSlice s = (buf).peekSlice().slice(delimiter.iStart,iEnd).copy();
                message(loc, new BytePtr("Ddoc: linking '%.*s' to '%.*s'"), s.getLength(), s.getPtr(0), link.href.getLength(), link.href.getPtr(0));
            }
            link.replaceLink(buf, i, iEnd, delimiter);
            return true;
        }

        // Erasure: replaceReferenceDefinition<Ptr, int, Array, int, MarkdownLinkReferences, Loc>
        public static boolean replaceReferenceDefinition(OutBuffer buf, Ref<Integer> i, Slice<MarkdownDelimiter> inlineDelimiters, int delimiterIndex, MarkdownLinkReferences linkReferences, Loc loc) {
            Ref<MarkdownLinkReferences> linkReferences_ref = ref(linkReferences);
            MarkdownDelimiter delimiter = inlineDelimiters.get(delimiterIndex).copy();
            MarkdownLink link = new MarkdownLink();
            int iEnd = link.parseReferenceDefinition(buf, i.value, delimiter);
            if ((iEnd == i.value))
            {
                return false;
            }
            i.value = delimiter.iStart;
            link.storeAndReplaceDefinition(buf, i, iEnd, linkReferences_ref, loc);
            inlineDelimiters.getLength() = delimiterIndex;
            return true;
        }

        // Erasure: parseInlineLink<Ptr, int>
        public  int parseInlineLink(OutBuffer buf, int i) {
            Ref<Integer> iEnd = ref(i + 1);
            if ((iEnd.value >= (buf).offset) || (((buf).data.get(iEnd.value) & 0xFF) != 40))
            {
                return i;
            }
            iEnd.value += 1;
            if (!this.parseHref(buf, iEnd))
            {
                return i;
            }
            iEnd.value = skipChars(buf, iEnd.value, new ByteSlice(" \u0009\r\n"));
            if ((((buf).data.get(iEnd.value) & 0xFF) != 41))
            {
                if (this.parseTitle(buf, iEnd))
                {
                    iEnd.value = skipChars(buf, iEnd.value, new ByteSlice(" \u0009\r\n"));
                }
            }
            if ((((buf).data.get(iEnd.value) & 0xFF) != 41))
            {
                return i;
            }
            return iEnd.value + 1;
        }

        // Erasure: parseReferenceLink<Ptr, int, MarkdownDelimiter>
        public  int parseReferenceLink(OutBuffer buf, int i, MarkdownDelimiter delimiter) {
            Ref<Integer> iStart = ref(i + 1);
            int iEnd = iStart.value;
            if ((iEnd >= (buf).offset) || (((buf).data.get(iEnd) & 0xFF) != 91) || (iEnd + 1 < (buf).offset) && (((buf).data.get(iEnd + 1) & 0xFF) == 93))
            {
                iStart.value = delimiter.iStart + delimiter.count - 1;
                if ((((buf).data.get(iEnd) & 0xFF) == 91))
                {
                    iEnd += 2;
                }
            }
            this.parseLabel(buf, iStart);
            if (this.label.value.getLength() == 0)
            {
                return i;
            }
            if ((iEnd < iStart.value))
            {
                iEnd = iStart.value;
            }
            return iEnd;
        }

        // Erasure: parseReferenceDefinition<Ptr, int, MarkdownDelimiter>
        public  int parseReferenceDefinition(OutBuffer buf, int i, MarkdownDelimiter delimiter) {
            if (!delimiter.atParagraphStart || ((delimiter.type & 0xFF) != 91) || (i + 1 >= (buf).offset) || (((buf).data.get(i + 1) & 0xFF) != 58))
            {
                return i;
            }
            Ref<Integer> iEnd = ref(delimiter.iStart);
            this.parseLabel(buf, iEnd);
            if ((this.label.value.getLength() == 0) || (iEnd.value != i + 1))
            {
                return i;
            }
            iEnd.value += 1;
            iEnd.value = skipChars(buf, iEnd.value, new ByteSlice(" \u0009"));
            skipOneNewline(buf, iEnd);
            if (!this.parseHref(buf, iEnd) || (this.href.getLength() == 0))
            {
                return i;
            }
            iEnd.value = skipChars(buf, iEnd.value, new ByteSlice(" \u0009"));
            boolean requireNewline = !skipOneNewline(buf, iEnd);
            int iBeforeTitle = iEnd.value;
            if (this.parseTitle(buf, iEnd))
            {
                iEnd.value = skipChars(buf, iEnd.value, new ByteSlice(" \u0009"));
                if ((iEnd.value < (buf).offset) && (((buf).data.get(iEnd.value) & 0xFF) != 13) && (((buf).data.get(iEnd.value) & 0xFF) != 10))
                {
                    this.title.getLength() = 0;
                    iEnd.value = iBeforeTitle;
                }
            }
            iEnd.value = skipChars(buf, iEnd.value, new ByteSlice(" \u0009"));
            if (requireNewline && (iEnd.value < (buf).offset - 1) && (((buf).data.get(iEnd.value) & 0xFF) != 13) && (((buf).data.get(iEnd.value) & 0xFF) != 10))
            {
                return i;
            }
            return iEnd.value;
        }

        // Erasure: parseLabel<Ptr, int>
        public  boolean parseLabel(OutBuffer buf, Ref<Integer> i) {
            if ((((buf).data.get(i.value) & 0xFF) != 91))
            {
                return false;
            }
            ByteSlice slice = (buf).peekSlice().copy();
            int j = i.value + 1;
            boolean inSymbol = (j + 15 < slice.getLength()) && __equals(slice.slice(j,j + 15), new ByteSlice("$(DDOC_PSYMBOL "));
            if (inSymbol)
            {
                j += 15;
            }
        L_outer11:
            for (; (j < slice.getLength());j += 1){
                byte c = slice.get(j);
                {
                    int __dispatch7 = 0;
                    dispatched_7:
                    do {
                        switch (__dispatch7 != 0 ? __dispatch7 : (c & 0xFF))
                        {
                            case 32:
                            case 9:
                            case 13:
                            case 10:
                                if ((this.label.value.getLength() != 0) && ((this.label.value.get(this.label.value.getLength() - 1) & 0xFF) != 32))
                                {
                                    this.label.value.append((byte)32);
                                }
                                break;
                            case 41:
                                if (inSymbol && (j + 1 < slice.getLength()) && ((slice.get(j + 1) & 0xFF) == 93))
                                {
                                    j += 1;
                                    /*goto case*/{ __dispatch7 = 93; continue dispatched_7; }
                                }
                                /*goto default*/ { __dispatch7 = -2; continue dispatched_7; }
                            case 91:
                                if (((slice.get(j - 1) & 0xFF) != 92))
                                {
                                    this.label.value.getLength() = 0;
                                    return false;
                                }
                                break;
                            case 93:
                                __dispatch7 = 0;
                                if ((this.label.value.getLength() != 0) && ((this.label.value.get(this.label.value.getLength() - 1) & 0xFF) == 32))
                                {
                                    (__arraylength1093.get()).getLength() = (__arraylength1093.get()).getLength() - 1;
                                }
                                if (this.label.value.getLength() != 0)
                                {
                                    i.value = j + 1;
                                    return true;
                                }
                                return false;
                            default:
                            __dispatch7 = 0;
                            this.label.value.append(c);
                            break;
                        }
                    } while(__dispatch7 != 0);
                }
            }
            this.label.value.getLength() = 0;
            return false;
        }

        // Erasure: parseHref<Ptr, int>
        public  boolean parseHref(OutBuffer buf, Ref<Integer> i) {
            int j = skipChars(buf, i.value, new ByteSlice(" \u0009"));
            int iHrefStart = j;
            int parenDepth = 1;
            boolean inPointy = false;
            ByteSlice slice = (buf).peekSlice().copy();
            try {
            L_outer12:
                for (; (j < slice.getLength());j++){
                    {
                        int __dispatch8 = 0;
                        dispatched_8:
                        do {
                            switch (__dispatch8 != 0 ? __dispatch8 : (slice.get(j) & 0xFF))
                            {
                                case 60:
                                    if (!inPointy && (j == iHrefStart))
                                    {
                                        inPointy = true;
                                        iHrefStart += 1;
                                    }
                                    break;
                                case 62:
                                    if (inPointy && ((slice.get(j - 1) & 0xFF) != 92))
                                    {
                                        /*goto LReturnHref*/throw Dispatch0.INSTANCE;
                                    }
                                    break;
                                case 40:
                                    if (!inPointy && ((slice.get(j - 1) & 0xFF) != 92))
                                    {
                                        parenDepth += 1;
                                    }
                                    break;
                                case 41:
                                    if (!inPointy && ((slice.get(j - 1) & 0xFF) != 92))
                                    {
                                        parenDepth -= 1;
                                        if (parenDepth == 0)
                                        {
                                            /*goto LReturnHref*/throw Dispatch0.INSTANCE;
                                        }
                                    }
                                    break;
                                case 32:
                                case 9:
                                case 13:
                                case 10:
                                    if (inPointy)
                                    {
                                        return false;
                                    }
                                    /*goto LReturnHref*/throw Dispatch0.INSTANCE;
                                default:
                                break;
                            }
                        } while(__dispatch8 != 0);
                    }
                }
                if (inPointy)
                {
                    return false;
                }
            }
            catch(Dispatch0 __d){}
        /*LReturnHref:*/
            ByteSlice href = dup(slice.slice(iHrefStart,j)).copy();
            this.href = toByteSlice(replaceChar(percentEncode(removeEscapeBackslashes(href)), (byte)44, new ByteSlice("$(COMMA)"))).copy();
            i.value = j;
            if (inPointy)
            {
                i.value += 1;
            }
            return true;
        }

        // Erasure: parseTitle<Ptr, int>
        public  boolean parseTitle(OutBuffer buf, Ref<Integer> i) {
            int j = skipChars(buf, i.value, new ByteSlice(" \u0009"));
            if ((j >= (buf).offset))
            {
                return false;
            }
            byte type = (byte)(buf).data.get(j);
            if (((type & 0xFF) != 34) && ((type & 0xFF) != 39) && ((type & 0xFF) != 40))
            {
                return false;
            }
            if (((type & 0xFF) == 40))
            {
                type = (byte)41;
            }
            int iTitleStart = j + 1;
            int iNewline = 0;
            ByteSlice slice = (buf).peekSlice().copy();
            try {
                {
                    j = iTitleStart;
                L_outer13:
                    for (; (j < slice.getLength());j++){
                        byte c = slice.get(j);
                        {
                            int __dispatch9 = 0;
                            dispatched_9:
                            do {
                                switch (__dispatch9 != 0 ? __dispatch9 : (c & 0xFF))
                                {
                                    case 41:
                                    case 34:
                                    case 39:
                                        if (((type & 0xFF) == (c & 0xFF)) && ((slice.get(j - 1) & 0xFF) != 92))
                                        {
                                            /*goto LEndTitle*/throw Dispatch0.INSTANCE;
                                        }
                                        iNewline = 0;
                                        break;
                                    case 32:
                                    case 9:
                                    case 13:
                                        break;
                                    case 10:
                                        if (iNewline != 0)
                                        {
                                            return false;
                                        }
                                        iNewline = j;
                                        break;
                                    default:
                                    iNewline = 0;
                                    break;
                                }
                            } while(__dispatch9 != 0);
                        }
                    }
                }
                return false;
            }
            catch(Dispatch0 __d){}
        /*LEndTitle:*/
            ByteSlice title = dup(slice.slice(iTitleStart,j)).copy();
            this.title = toByteSlice(replaceChar(replaceChar(removeEscapeBackslashes(title), (byte)44, new ByteSlice("$(COMMA)")), (byte)34, new ByteSlice("$(QUOTE)"))).copy();
            i.value = j + 1;
            return true;
        }

        // Erasure: replaceLink<Ptr, int, int, MarkdownDelimiter>
        public  void replaceLink(OutBuffer buf, Ref<Integer> i, int iLinkEnd, MarkdownDelimiter delimiter) {
            int iAfterLink = i.value - delimiter.count;
            ByteSlice macroName = new ByteSlice().copy();
            if (this.symbol != null)
            {
                macroName = new ByteSlice("$(SYMBOL_LINK ").copy();
            }
            else if (this.title.getLength() != 0)
            {
                if (((delimiter.type & 0xFF) == 91))
                {
                    macroName = new ByteSlice("$(LINK_TITLE ").copy();
                }
                else
                {
                    macroName = new ByteSlice("$(IMAGE_TITLE ").copy();
                }
            }
            else
            {
                if (((delimiter.type & 0xFF) == 91))
                {
                    macroName = new ByteSlice("$(LINK2 ").copy();
                }
                else
                {
                    macroName = new ByteSlice("$(IMAGE ").copy();
                }
            }
            (buf).remove(delimiter.iStart, delimiter.count);
            (buf).remove(i.value - delimiter.count, iLinkEnd - i.value);
            iLinkEnd = (buf).insert(delimiter.iStart, toByteSlice(macroName));
            iLinkEnd = (buf).insert(iLinkEnd, toByteSlice(this.href));
            iLinkEnd = (buf).insert(iLinkEnd, new ByteSlice(", "));
            iAfterLink += macroName.getLength() + this.href.getLength() + 2;
            if (this.title.getLength() != 0)
            {
                iLinkEnd = (buf).insert(iLinkEnd, toByteSlice(this.title));
                iLinkEnd = (buf).insert(iLinkEnd, new ByteSlice(", "));
                iAfterLink += this.title.getLength() + 2;
                {
                    int j = iLinkEnd;
                    for (; (j < iAfterLink);j += 1) {
                        if ((((buf).data.get(j) & 0xFF) == 44))
                        {
                            (buf).remove(j, 1);
                            j = (buf).insert(j, new ByteSlice("$(COMMA)")) - 1;
                            iAfterLink += 7;
                        }
                    }
                }
            }
            (buf).insert(iAfterLink, new ByteSlice(")"));
            i.value = iAfterLink;
        }

        // Erasure: storeAndReplaceDefinition<Ptr, int, int, MarkdownLinkReferences, Loc>
        public  void storeAndReplaceDefinition(OutBuffer buf, Ref<Integer> i, int iEnd, MarkdownLinkReferences linkReferences, Loc loc) {
            if (global.params.vmarkdown)
            {
                message(loc, new BytePtr("Ddoc: found link reference '%.*s' to '%.*s'"), this.label.value.getLength(), this.label.value.getPtr(0), this.href.getLength(), this.href.getPtr(0));
            }
            iEnd = skipChars(buf, iEnd, new ByteSlice(" \u0009\r\n"));
            (buf).remove(i.value, iEnd - i.value);
            i.value -= 2;
            ByteSlice lowercaseLabel = toLowercase(this.label.value).copy();
            if (linkReferences.references.getLvalue(lowercaseLabel) == null)
            {
                linkReferences.references.set(lowercaseLabel, __aaval1094);
            }
        }

        // Erasure: removeEscapeBackslashes<Array>
        public static ByteSlice removeEscapeBackslashes(ByteSlice s) {
            if (s.getLength() == 0)
            {
                return s;
            }
            int i = 0;
            {
                i = 0;
                for (; (i < s.getLength() - 1);i += 1) {
                    if (((s.get(i) & 0xFF) == 92) && (ispunct((s.get(i + 1) & 0xFF)) != 0))
                    {
                        break;
                    }
                }
            }
            if ((i == s.getLength() - 1))
            {
                return s;
            }
            int j = i + 1;
            s.set(i, s.get(j));
            {
                comma(i += 1, j += 1);
                for (; (j < s.getLength());comma(i += 1, j += 1)){
                    if ((j < s.getLength() - 1) && ((s.get(j) & 0xFF) == 92) && (ispunct((s.get(j + 1) & 0xFF)) != 0))
                    {
                        j += 1;
                    }
                    s.set(i, s.get(j));
                }
            }
            s.getLength() = s.getLength() - (j - i);
            return s;
        }

        // Erasure: percentEncode<Array>
        public static ByteSlice percentEncode(ByteSlice s) {
            Function1<Byte,Boolean> shouldEncode = new Function1<Byte,Boolean>() {
                public Boolean invoke(Byte c) {
                 {
                    return ((c & 0xFF) < 48) && ((c & 0xFF) != 33) && ((c & 0xFF) != 35) && ((c & 0xFF) != 36) && ((c & 0xFF) != 37) && ((c & 0xFF) != 38) && ((c & 0xFF) != 39) && ((c & 0xFF) != 40) && ((c & 0xFF) != 41) && ((c & 0xFF) != 42) && ((c & 0xFF) != 43) && ((c & 0xFF) != 44) && ((c & 0xFF) != 45) && ((c & 0xFF) != 46) && ((c & 0xFF) != 47) || ((c & 0xFF) > 57) && ((c & 0xFF) < 65) && ((c & 0xFF) != 58) && ((c & 0xFF) != 59) && ((c & 0xFF) != 61) && ((c & 0xFF) != 63) && ((c & 0xFF) != 64) || ((c & 0xFF) > 90) && ((c & 0xFF) < 97) && ((c & 0xFF) != 91) && ((c & 0xFF) != 93) && ((c & 0xFF) != 95) || ((c & 0xFF) > 122) && ((c & 0xFF) != 126);
                }}

            };
            {
                int i = 0;
                for (; (i < s.getLength());i += 1){
                    if (shouldEncode.invoke(s.get(i)))
                    {
                        byte encoded1 = doc.percentEncodehexDigits.get(((s.get(i) & 0xFF) >> 4));
                        byte encoded2 = doc.percentEncodehexDigits.get(((s.get(i) & 0xFF) & 15));
                        s = concat(concat(concat(concat(s.slice(0,i), (byte)37), encoded1), encoded2), s.slice(i + 1,s.getLength())).copy();
                        i += 2;
                    }
                }
            }
            return s;
        }

        // Erasure: skipOneNewline<Ptr, int>
        public static boolean skipOneNewline(OutBuffer buf, Ref<Integer> i) {
            if ((i.value < (buf).offset) && (((buf).data.get(i.value) & 0xFF) == 13))
            {
                i.value += 1;
            }
            if ((i.value < (buf).offset) && (((buf).data.get(i.value) & 0xFF) == 10))
            {
                i.value += 1;
                return true;
            }
            return false;
        }

        public MarkdownLink(){ }
        public MarkdownLink copy(){
            MarkdownLink r = new MarkdownLink();
            r.href = href.copy();
            r.title = title.copy();
            r.label = label.copy();
            r.symbol = symbol;
            return r;
        }
        public MarkdownLink(ByteSlice href, ByteSlice title, ByteSlice label, Dsymbol symbol) {
            this.href = href;
            this.title = title;
            this.label = ref(label);
            this.symbol = symbol;
        }

        public MarkdownLink opAssign(MarkdownLink that) {
            this.href = that.href;
            this.title = that.title;
            this.label = that.label;
            this.symbol = that.symbol;
            return this;
        }
    }
    public static class MarkdownLinkReferences
    {
        public AA<ByteSlice,MarkdownLink> references = new AA<ByteSlice,MarkdownLink>();
        public AA<ByteSlice,MarkdownLink> symbols = new AA<ByteSlice,MarkdownLink>();
        public Ptr<Scope> _scope = null;
        public boolean extractedAll = false;
        // Erasure: lookupReference<Array, Ptr, int, Loc>
        public  MarkdownLink lookupReference(ByteSlice label, OutBuffer buf, int i, Loc loc) {
            ByteSlice lowercaseLabel = toLowercase(label).copy();
            if (this.references.getLvalue(lowercaseLabel) == null)
            {
                this.extractReferences(buf, i, loc);
            }
            if (this.references.getLvalue(lowercaseLabel) != null)
            {
                return this.references.get(lowercaseLabel);
            }
            return new MarkdownLink(new ByteSlice(), new ByteSlice(), new ByteSlice(), null);
        }

        // Erasure: lookupSymbol<Array>
        public  MarkdownLink lookupSymbol(ByteSlice name) {
            if (this.symbols.getLvalue(name) != null)
            {
                return this.symbols.get(name);
            }
            Slice<ByteSlice> ids = split(name, (byte)46).copy();
            MarkdownLink link = new MarkdownLink();
            Identifier id = Identifier.lookup(ids.get(0).getPtr(0), ids.get(0).getLength());
            if (id != null)
            {
                Loc loc = new Loc(null, 0, 0).copy();
                Dsymbol symbol = (this._scope.get()).search(loc, id, null, 2);
                {
                    int i = 1;
                    for (; (symbol != null) && (i < ids.getLength());i += 1){
                        id = Identifier.lookup(ids.get(i).getPtr(0), ids.get(i).getLength());
                        symbol = (id != null) ? symbol.search(loc, id, 2) : null;
                    }
                }
                if (symbol != null)
                {
                    link.opAssign(new MarkdownLink(this.createHref(symbol), new ByteSlice(), name, symbol));
                }
            }
            this.symbols.set(name, __aaval1095);
            return link;
        }

        // Erasure: extractReferences<Ptr, int, Loc>
        public  void extractReferences(OutBuffer buf, int i, Loc loc) {
            Ref<Integer> i_ref = ref(i);
            MarkdownLinkReferences __self = this;
            Function2<OutBuffer,Integer,Boolean> isFollowedBySpace = new Function2<OutBuffer,Integer,Boolean>() {
                public Boolean invoke(OutBuffer buf, Integer i) {
                 {
                    return (i + 1 < (buf).offset) && (((buf).data.get(i + 1) & 0xFF) == 32) || (((buf).data.get(i + 1) & 0xFF) == 9);
                }}

            };
            if (this.extractedAll)
            {
                return ;
            }
            boolean leadingBlank = false;
            int inCode = 0;
            boolean newParagraph = true;
            Ref<Slice<MarkdownDelimiter>> delimiters = ref(new RawSlice<MarkdownDelimiter>().copy());
        L_outer14:
            for (; (i_ref.value < (buf).offset);i_ref.value += 1){
                byte c = (buf).data.get(i_ref.value);
                {
                    int __dispatch10 = 0;
                    dispatched_10:
                    do {
                        switch (__dispatch10 != 0 ? __dispatch10 : (c & 0xFF))
                        {
                            case 32:
                            case 9:
                                break;
                            case 10:
                                if (leadingBlank && (inCode == 0))
                                {
                                    newParagraph = true;
                                }
                                leadingBlank = true;
                                break;
                            case 92:
                                i_ref.value += 1;
                                break;
                            case 35:
                                if (leadingBlank && (inCode == 0))
                                {
                                    newParagraph = true;
                                }
                                leadingBlank = false;
                                break;
                            case 62:
                                if (leadingBlank && (inCode == 0))
                                {
                                    newParagraph = true;
                                }
                                break;
                            case 43:
                                __dispatch10 = 0;
                                if (leadingBlank && (inCode == 0) && isFollowedBySpace.invoke(buf, i_ref.value))
                                {
                                    newParagraph = true;
                                }
                                else
                                {
                                    leadingBlank = false;
                                }
                                break;
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57:
                                if (leadingBlank && (inCode == 0))
                                {
                                    i_ref.value = skipChars(buf, i_ref.value, new ByteSlice("0123456789"));
                                    if ((i_ref.value < (buf).offset) && (((buf).data.get(i_ref.value) & 0xFF) == 46) || (((buf).data.get(i_ref.value) & 0xFF) == 41) && isFollowedBySpace.invoke(buf, i_ref.value))
                                    {
                                        newParagraph = true;
                                    }
                                    else
                                    {
                                        leadingBlank = false;
                                    }
                                }
                                break;
                            case 42:
                                if (leadingBlank && (inCode == 0))
                                {
                                    newParagraph = true;
                                    if (!isFollowedBySpace.invoke(buf, i_ref.value))
                                    {
                                        leadingBlank = false;
                                    }
                                }
                                break;
                            case 96:
                                __dispatch10 = 0;
                            case 126:
                                if (leadingBlank && (i_ref.value + 2 < (buf).offset) && (((buf).data.get(i_ref.value + 1) & 0xFF) == (c & 0xFF)) && (((buf).data.get(i_ref.value + 2) & 0xFF) == (c & 0xFF)))
                                {
                                    inCode = (inCode == (c & 0xFF)) ? 0 : (c & 0xFF);
                                    i_ref.value = skipChars(buf, i_ref.value, slice(new byte[]{(byte)c})) - 1;
                                    newParagraph = true;
                                }
                                leadingBlank = false;
                                break;
                            case 45:
                                if (leadingBlank && (inCode == 0) && isFollowedBySpace.invoke(buf, i_ref.value))
                                {
                                    /*goto case*/{ __dispatch10 = 43; continue dispatched_10; }
                                }
                                else
                                {
                                    /*goto case*/{ __dispatch10 = 96; continue dispatched_10; }
                                }
                            case 91:
                                if (leadingBlank && (inCode == 0) && newParagraph)
                                {
                                    delimiters.value.append(new MarkdownDelimiter(i_ref.value, 1, 0, false, false, true, (byte)c));
                                }
                                break;
                            case 93:
                                if ((delimiters.value.getLength() != 0) && (inCode == 0) && MarkdownLink.replaceReferenceDefinition(buf, i_ref, delimiters, delimiters.value.getLength() - 1, this, loc))
                                {
                                    i_ref.value -= 1;
                                }
                                break;
                            default:
                            if (leadingBlank)
                            {
                                newParagraph = false;
                            }
                            leadingBlank = false;
                            break;
                        }
                    } while(__dispatch10 != 0);
                }
            }
            this.extractedAll = true;
        }

        // Erasure: split<Array, byte>
        public static Slice<ByteSlice> split(ByteSlice s, byte delimiter) {
            Slice<ByteSlice> result = new RawSlice<ByteSlice>().copy();
            int iStart = 0;
            {
                int __key1096 = 0;
                int __limit1097 = s.getLength();
                for (; (__key1096 < __limit1097);__key1096 += 1) {
                    int i = __key1096;
                    if (((s.get(i) & 0xFF) == (delimiter & 0xFF)))
                    {
                        result.append(s.slice(iStart,i));
                        iStart = i + 1;
                    }
                }
            }
            result.append(s.slice(iStart,s.getLength()));
            return result;
        }

        // Erasure: createHref<Dsymbol>
        public  ByteSlice createHref(Dsymbol symbol) {
            Dsymbol root = symbol;
            ByteSlice lref = new ByteSlice().copy();
            for (; (symbol != null) && (symbol.ident != null) && (symbol.isModule() == null);){
                if (lref.getLength() != 0)
                {
                    lref = concat((byte)46, lref).copy();
                }
                lref = concat(symbol.ident.asString(), lref).copy();
                symbol = symbol.parent.value;
            }
            ByteSlice path = new ByteSlice().copy();
            if ((symbol != null) && (symbol.ident != null) && (!pequals(symbol.isModule(), (this._scope.get())._module)))
            {
                do {
                    {
                        root = symbol;
                        {
                            dmodule.Module m = symbol.isModule();
                            if ((m) != null)
                            {
                                if (m.docfile.opCast())
                                {
                                    path = m.docfile.asString().copy();
                                    break;
                                }
                            }
                        }
                        if (path.getLength() != 0)
                        {
                            path = concat((byte)95, path).copy();
                        }
                        path = concat(symbol.ident.asString(), path).copy();
                        symbol = symbol.parent.value;
                    }
                } while ((symbol != null) && (symbol.ident != null));
                if ((symbol == null) && (path.getLength() != 0))
                {
                    path.append(new ByteSlice("$(DOC_EXTENSION)"));
                }
            }
            for (; root.parent.value != null;) {
                root = root.parent.value;
            }
            Dsymbol scopeRoot = (this._scope.get())._module;
            for (; scopeRoot.parent.value != null;) {
                scopeRoot = scopeRoot.parent.value;
            }
            if ((!pequals(scopeRoot, root)))
            {
                path = concat(concat(concat(new ByteSlice("$(DOC_ROOT_"), root.ident.asString()), (byte)41), path).copy();
                lref = concat((byte)46, lref).copy();
            }
            return toByteSlice((concat(concat(path, (byte)35), lref)));
        }

        public MarkdownLinkReferences(){ }
        public MarkdownLinkReferences copy(){
            MarkdownLinkReferences r = new MarkdownLinkReferences();
            r.references = references;
            r.symbols = symbols;
            r._scope = _scope;
            r.extractedAll = extractedAll;
            return r;
        }
        public MarkdownLinkReferences(AA<ByteSlice,MarkdownLink> references, AA<ByteSlice,MarkdownLink> symbols, Ptr<Scope> _scope, boolean extractedAll) {
            this.references = references;
            this.symbols = symbols;
            this._scope = _scope;
            this.extractedAll = extractedAll;
        }

        public MarkdownLinkReferences opAssign(MarkdownLinkReferences that) {
            this.references = that.references;
            this.symbols = that.symbols;
            this._scope = that._scope;
            this.extractedAll = that.extractedAll;
            return this;
        }
    }

    public static class TableColumnAlignment 
    {
        public static final int none = 0;
        public static final int left = 1;
        public static final int center = 2;
        public static final int right = 3;
    }

    // Erasure: parseTableDelimiterRow<Ptr, int, boolean, Array>
    public static int parseTableDelimiterRow(OutBuffer buf, int iStart, boolean inQuote, IntSlice columnAlignments) {
        int i = skipChars(buf, iStart, inQuote ? new ByteSlice(">| \u0009") : new ByteSlice("| \u0009"));
        for (; (i < (buf).offset) && (((buf).data.get(i) & 0xFF) != 13) && (((buf).data.get(i) & 0xFF) != 10);){
            boolean leftColon = ((buf).data.get(i) & 0xFF) == 58;
            if (leftColon)
            {
                i += 1;
            }
            if ((i >= (buf).offset) || (((buf).data.get(i) & 0xFF) != 45))
            {
                break;
            }
            i = skipChars(buf, i, new ByteSlice("-"));
            boolean rightColon = (i < (buf).offset) && (((buf).data.get(i) & 0xFF) == 58);
            i = skipChars(buf, i, new ByteSlice(": \u0009"));
            if ((i >= (buf).offset) || (((buf).data.get(i) & 0xFF) != 124) && (((buf).data.get(i) & 0xFF) != 13) && (((buf).data.get(i) & 0xFF) != 10))
            {
                break;
            }
            i = skipChars(buf, i, new ByteSlice("| \u0009"));
            columnAlignments.append(leftColon && rightColon ? TableColumnAlignment.center : leftColon ? TableColumnAlignment.left : rightColon ? TableColumnAlignment.right : TableColumnAlignment.none);
        }
        if ((i < (buf).offset) && (((buf).data.get(i) & 0xFF) != 13) && (((buf).data.get(i) & 0xFF) != 10) && (((buf).data.get(i) & 0xFF) != 41))
        {
            columnAlignments.getLength() = 0;
            return 0;
        }
        if ((i < (buf).offset) && (((buf).data.get(i) & 0xFF) == 13))
        {
            i += 1;
        }
        if ((i < (buf).offset) && (((buf).data.get(i) & 0xFF) == 10))
        {
            i += 1;
        }
        return i;
    }

    // Erasure: startTable<Ptr, int, int, Loc, boolean, Array, Array>
    public static int startTable(OutBuffer buf, int iStart, int iEnd, Loc loc, boolean inQuote, Slice<MarkdownDelimiter> inlineDelimiters, IntSlice columnAlignments) {
        Ref<Slice<MarkdownDelimiter>> inlineDelimiters_ref = ref(inlineDelimiters);
        Ref<IntSlice> columnAlignments_ref = ref(columnAlignments);
        columnAlignments_ref.value = new IntSlice().copy();
        int iDelimiterRowEnd = parseTableDelimiterRow(buf, iEnd + 1, inQuote, columnAlignments_ref);
        if (iDelimiterRowEnd != 0)
        {
            int delta = replaceTableRow(buf, iStart, iEnd, loc, inlineDelimiters_ref, columnAlignments_ref.value, true);
            if (delta != 0)
            {
                (buf).remove(iEnd + delta, iDelimiterRowEnd - iEnd);
                (buf).insert(iEnd + delta, new ByteSlice("$(TBODY "));
                (buf).insert(iStart, new ByteSlice("$(TABLE "));
                return delta + 15;
            }
        }
        columnAlignments_ref.value.getLength() = 0;
        return 0;
    }

    // Erasure: replaceTableRow<Ptr, int, int, Loc, Array, Array, boolean>
    public static int replaceTableRow(OutBuffer buf, int iStart, int iEnd, Loc loc, Slice<MarkdownDelimiter> inlineDelimiters, IntSlice columnAlignments, boolean headerRow) {
        Ref<Slice<MarkdownDelimiter>> inlineDelimiters_ref = ref(inlineDelimiters);
        if ((columnAlignments.getLength() == 0) || (iStart == iEnd))
        {
            return 0;
        }
        iStart = skipChars(buf, iStart, new ByteSlice(" \u0009"));
        int cellCount = 0;
        {
            Slice<MarkdownDelimiter> __r1098 = inlineDelimiters_ref.value.copy();
            int __key1099 = 0;
            for (; (__key1099 < __r1098.getLength());__key1099 += 1) {
                MarkdownDelimiter delimiter = __r1098.get(__key1099).copy();
                if (((delimiter.type & 0xFF) == 124) && !delimiter.leftFlanking)
                {
                    cellCount += 1;
                }
            }
        }
        boolean ignoreLast = (inlineDelimiters_ref.value.getLength() > 0) && ((inlineDelimiters_ref.value.get(inlineDelimiters_ref.value.getLength() - 1).type & 0xFF) == 124);
        if (ignoreLast)
        {
            int iLast = skipChars(buf, inlineDelimiters_ref.value.get(inlineDelimiters_ref.value.getLength() - 1).iStart + inlineDelimiters_ref.value.get(inlineDelimiters_ref.value.getLength() - 1).count, new ByteSlice(" \u0009"));
            ignoreLast = iLast >= iEnd;
        }
        if (!ignoreLast)
        {
            cellCount += 1;
        }
        if (headerRow && (cellCount != columnAlignments.getLength()))
        {
            return 0;
        }
        if (headerRow && global.params.vmarkdown)
        {
            ByteSlice s = (buf).peekSlice().slice(iStart,iEnd).copy();
            message(loc, new BytePtr("Ddoc: formatting table '%.*s'"), s.getLength(), s.getPtr(0));
        }
        Ref<Integer> delta = ref(0);
        Runnable4<Integer,Integer,Integer,Integer> replaceTableCell = new Runnable4<Integer,Integer,Integer,Integer>() {
            public Void invoke(Integer iCellStart, Integer iCellEnd, Integer cellIndex, Integer di) {
             {
                Ref<Integer> iCellEnd_ref = ref(iCellEnd);
                int eDelta = replaceMarkdownEmphasis(buf, loc, inlineDelimiters_ref, di);
                delta.value += eDelta;
                iCellEnd_ref.value += eDelta;
                Ref<Integer> i = ref(iCellEnd_ref.value - 1);
                for (; (i.value > iCellStart) && (((buf).data.get(i.value) & 0xFF) == 124) || (((buf).data.get(i.value) & 0xFF) == 32) || (((buf).data.get(i.value) & 0xFF) == 9);) {
                    i.value -= 1;
                }
                i.value += 1;
                (buf).remove(i.value, iCellEnd_ref.value - i.value);
                delta.value -= iCellEnd_ref.value - i.value;
                iCellEnd_ref.value = i.value;
                (buf).insert(iCellEnd_ref.value, new ByteSlice(")"));
                delta.value += 1;
                i.value = skipChars(buf, iCellStart, new ByteSlice("| \u0009"));
                (buf).remove(iCellStart, i.value - iCellStart);
                delta.value -= i.value - iCellStart;
                {
                    int __dispatch11 = 0;
                    dispatched_11:
                    do {
                        switch (__dispatch11 != 0 ? __dispatch11 : columnAlignments.get(cellIndex))
                        {
                            case TableColumnAlignment.none:
                                (buf).insert(iCellStart, headerRow ? new ByteSlice("$(TH ") : new ByteSlice("$(TD "));
                                delta.value += 5;
                                break;
                            case TableColumnAlignment.left:
                                (buf).insert(iCellStart, new ByteSlice("left, "));
                                delta.value += 6;
                                /*goto default*/ { __dispatch11 = -1; continue dispatched_11; }
                            case TableColumnAlignment.center:
                                (buf).insert(iCellStart, new ByteSlice("center, "));
                                delta.value += 8;
                                /*goto default*/ { __dispatch11 = -1; continue dispatched_11; }
                            case TableColumnAlignment.right:
                                (buf).insert(iCellStart, new ByteSlice("right, "));
                                delta.value += 7;
                                /*goto default*/ { __dispatch11 = -1; continue dispatched_11; }
                            default:
                            __dispatch11 = 0;
                            (buf).insert(iCellStart, headerRow ? new ByteSlice("$(TH_ALIGN ") : new ByteSlice("$(TD_ALIGN "));
                            delta.value += 11;
                            break;
                        }
                    } while(__dispatch11 != 0);
                }
                return null;
            }}

        };
        int cellIndex = cellCount - 1;
        int iCellEnd = iEnd;
        {
            Slice<MarkdownDelimiter> __r1101 = inlineDelimiters_ref.value.copy();
            int __key1100 = __r1101.getLength();
            for (; __key1100-- != 0;) {
                MarkdownDelimiter delimiter = __r1101.get(__key1100).copy();
                int di = __key1100;
                if (((delimiter.type & 0xFF) == 124))
                {
                    if (ignoreLast && (di == inlineDelimiters_ref.value.getLength() - 1))
                    {
                        ignoreLast = false;
                        continue;
                    }
                    if ((cellIndex >= columnAlignments.getLength()))
                    {
                        (buf).remove(delimiter.iStart, iEnd + delta.value - delimiter.iStart);
                        delta.value -= iEnd + delta.value - delimiter.iStart;
                        iCellEnd = iEnd + delta.value;
                        cellIndex -= 1;
                        continue;
                    }
                    replaceTableCell.invoke(delimiter.iStart, iCellEnd, cellIndex, di);
                    iCellEnd = delimiter.iStart;
                    cellIndex -= 1;
                }
            }
        }
        if ((cellIndex >= 0))
        {
            replaceTableCell.invoke(iStart, iCellEnd, cellIndex, 0);
        }
        (buf).insert(iEnd + delta.value, new ByteSlice(")"));
        (buf).insert(iStart, new ByteSlice("$(TR "));
        delta.value += 6;
        if (headerRow)
        {
            (buf).insert(iEnd + delta.value, new ByteSlice(")"));
            (buf).insert(iStart, new ByteSlice("$(THEAD "));
            delta.value += 9;
        }
        return delta.value;
    }

    // Erasure: endTable<Ptr, int, Array>
    public static int endTable(OutBuffer buf, int i, IntSlice columnAlignments) {
        if (columnAlignments.getLength() == 0)
        {
            return 0;
        }
        (buf).insert(i, new ByteSlice("))"));
        columnAlignments.getLength() = 0;
        return 2;
    }

    // Erasure: endRowAndTable<Ptr, int, int, Loc, Array, Array>
    public static int endRowAndTable(OutBuffer buf, int iStart, int iEnd, Loc loc, Slice<MarkdownDelimiter> inlineDelimiters, IntSlice columnAlignments) {
        Ref<Slice<MarkdownDelimiter>> inlineDelimiters_ref = ref(inlineDelimiters);
        Ref<IntSlice> columnAlignments_ref = ref(columnAlignments);
        int delta = replaceTableRow(buf, iStart, iEnd, loc, inlineDelimiters_ref, columnAlignments_ref.value, false);
        delta += endTable(buf, iEnd + delta, columnAlignments_ref);
        return delta;
    }

    // Erasure: highlightText<Ptr, Ptr, Loc, Ptr, int>
    public static void highlightText(Ptr<Scope> sc, DArray<Dsymbol> a, Loc loc, OutBuffer buf, int offset) {
        int incrementLoc = (loc.linnum == 0) ? 1 : 0;
        loc.linnum += incrementLoc;
        loc.charnum = 0;
        boolean leadingBlank = true;
        Ref<Integer> iParagraphStart = ref(offset);
        Ref<Integer> iPrecedingBlankLine = ref(0);
        Ref<Integer> headingLevel = ref(0);
        int headingMacroLevel = 0;
        Ref<Integer> quoteLevel = ref(0);
        boolean lineQuoted = false;
        Ref<Integer> quoteMacroLevel = ref(0);
        Ref<Slice<MarkdownList>> nestedLists = ref(new RawSlice<MarkdownList>().copy());
        Ref<Slice<MarkdownDelimiter>> inlineDelimiters = ref(new RawSlice<MarkdownDelimiter>().copy());
        Ref<MarkdownLinkReferences> linkReferences = ref(new MarkdownLinkReferences());
        Ref<IntSlice> columnAlignments = ref(new IntSlice().copy());
        boolean tableRowDetected = false;
        int inCode = 0;
        int inBacktick = 0;
        int macroLevel = 0;
        int previousMacroLevel = 0;
        int parenLevel = 0;
        int iCodeStart = 0;
        int codeFenceLength = 0;
        int codeIndent = 0;
        ByteSlice codeLanguage = new ByteSlice().copy();
        Ref<Integer> iLineStart = ref(offset);
        linkReferences.value._scope = pcopy(sc);
        {
            Ref<Integer> i = ref(offset);
        L_outer15:
            for (; (i.value < (buf).offset);i.value++){
                byte c = (byte)(buf).data.get(i.value);
            /*Lcont:*/
                {
                    int __dispatch12 = 0;
                    dispatched_12:
                    do {
                        switch (__dispatch12 != 0 ? __dispatch12 : (c & 0xFF))
                        {
                            case 32:
                            case 9:
                                break;
                            case 10:
                                if (inBacktick != 0)
                                {
                                    inBacktick = 0;
                                    inCode = 0;
                                }
                                if (headingLevel.value != 0)
                                {
                                    i.value += replaceMarkdownEmphasis(buf, loc, inlineDelimiters, 0);
                                    endMarkdownHeading(buf, iParagraphStart.value, i, loc, headingLevel);
                                    removeBlankLineMacro(buf, iPrecedingBlankLine, i);
                                    i.value += 1;
                                    iParagraphStart.value = skipChars(buf, i.value, new ByteSlice(" \u0009\r\n"));
                                }
                                if (tableRowDetected && (columnAlignments.value.getLength() == 0))
                                {
                                    i.value += startTable(buf, iLineStart.value, i.value, loc, lineQuoted, inlineDelimiters, columnAlignments);
                                }
                                else if (columnAlignments.value.getLength() != 0)
                                {
                                    int delta = replaceTableRow(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments.value, false);
                                    if (delta != 0)
                                    {
                                        i.value += delta;
                                    }
                                    else
                                    {
                                        i.value += endTable(buf, i.value, columnAlignments);
                                    }
                                }
                                if ((inCode == 0) && (nestedLists.value.getLength() != 0) && (quoteLevel.value == 0))
                                {
                                    MarkdownList.handleSiblingOrEndingList(buf, i, iParagraphStart, nestedLists);
                                }
                                iPrecedingBlankLine.value = 0;
                                if ((inCode == 0) && (i.value == iLineStart.value) && (i.value + 1 < (buf).offset))
                                {
                                    i.value += endTable(buf, i.value, columnAlignments);
                                    if (!lineQuoted && (quoteLevel.value != 0))
                                    {
                                        endAllListsAndQuotes(buf, i, nestedLists, quoteLevel, quoteMacroLevel);
                                    }
                                    i.value += replaceMarkdownEmphasis(buf, loc, inlineDelimiters, 0);
                                    if ((iParagraphStart.value <= i.value))
                                    {
                                        iPrecedingBlankLine.value = i.value;
                                        i.value = (buf).insert(i.value, new ByteSlice("$(DDOC_BLANKLINE)"));
                                        iParagraphStart.value = i.value + 1;
                                    }
                                }
                                else if ((inCode != 0) && (i.value == iLineStart.value) && (i.value + 1 < (buf).offset) && !lineQuoted && (quoteLevel.value != 0))
                                {
                                    inCode = 0;
                                    i.value = (buf).insert(i.value, new ByteSlice(")"));
                                    i.value += endAllMarkdownQuotes(buf, i.value, quoteLevel);
                                    quoteMacroLevel.value = 0;
                                }
                                leadingBlank = true;
                                lineQuoted = false;
                                tableRowDetected = false;
                                iLineStart.value = i.value + 1;
                                loc.linnum += incrementLoc;
                                if ((previousMacroLevel < macroLevel) && (iParagraphStart.value < iLineStart.value))
                                {
                                    iParagraphStart.value = iLineStart.value;
                                }
                                previousMacroLevel = macroLevel;
                                break;
                            case 60:
                                leadingBlank = false;
                                if (inCode != 0)
                                {
                                    break;
                                }
                                ByteSlice slice = (buf).peekSlice().copy();
                                BytePtr p = pcopy(slice.getPtr(i.value));
                                ByteSlice se = ((sc.get())._module.escapetable.get()).escapeChar((byte)60).copy();
                                if (__equals(se, new ByteSlice("&lt;")))
                                {
                                    if (((p.get(1) & 0xFF) == 33) && ((p.get(2) & 0xFF) == 45) && ((p.get(3) & 0xFF) == 45))
                                    {
                                        int j = i.value + 4;
                                        p.plusAssign(4);
                                    L_outer16:
                                        for (; 1 != 0;){
                                            if ((j == slice.getLength()))
                                            {
                                                /*goto L1*/{ __dispatch12 = -1; continue dispatched_12; }
                                            }
                                            if (((p.get(0) & 0xFF) == 45) && ((p.get(1) & 0xFF) == 45) && ((p.get(2) & 0xFF) == 62))
                                            {
                                                i.value = j + 2;
                                                break;
                                            }
                                            j++;
                                            p.postInc();
                                        }
                                        break;
                                    }
                                    if ((isalpha((p.get(1) & 0xFF)) != 0) || ((p.get(1) & 0xFF) == 47) && (isalpha((p.get(2) & 0xFF)) != 0))
                                    {
                                        int j_1 = i.value + 2;
                                        p.plusAssign(2);
                                        for (; 1 != 0;){
                                            if ((j_1 == slice.getLength()))
                                            {
                                                break;
                                            }
                                            if (((p.get(0) & 0xFF) == 62))
                                            {
                                                i.value = j_1;
                                                break;
                                            }
                                            j_1++;
                                            p.postInc();
                                        }
                                        break;
                                    }
                                }
                            /*L1:*/
                            case -1:
                            __dispatch12 = 0;
                                if (se.getLength() != 0)
                                {
                                    (buf).remove(i.value, 1);
                                    i.value = (buf).insert(i.value, se);
                                    i.value--;
                                }
                                break;
                            case 62:
                                if (leadingBlank && (inCode == 0) || (quoteLevel.value != 0) && global.params.markdown)
                                {
                                    if ((quoteLevel.value == 0) && global.params.vmarkdown)
                                    {
                                        int iEnd = i.value + 1;
                                        for (; (iEnd < (buf).offset) && (((buf).data.get(iEnd) & 0xFF) != 10);) {
                                            iEnd += 1;
                                        }
                                        ByteSlice s = (buf).peekSlice().slice(i.value,iEnd).copy();
                                        message(loc, new BytePtr("Ddoc: starting quote block with '%.*s'"), s.getLength(), s.getPtr(0));
                                    }
                                    lineQuoted = true;
                                    int lineQuoteLevel = 1;
                                    int iAfterDelimiters = i.value + 1;
                                    for (; (iAfterDelimiters < (buf).offset);iAfterDelimiters += 1){
                                        byte c0 = (buf).data.get(iAfterDelimiters);
                                        if (((c0 & 0xFF) == 62))
                                        {
                                            lineQuoteLevel += 1;
                                        }
                                        else if (((c0 & 0xFF) != 32) && ((c0 & 0xFF) != 9))
                                        {
                                            break;
                                        }
                                    }
                                    if (quoteMacroLevel.value == 0)
                                    {
                                        quoteMacroLevel.value = macroLevel;
                                    }
                                    (buf).remove(i.value, iAfterDelimiters - i.value);
                                    if ((quoteLevel.value < lineQuoteLevel))
                                    {
                                        i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                        if (nestedLists.value.getLength() != 0)
                                        {
                                            int indent = getMarkdownIndent(buf, iLineStart.value, i.value);
                                            if ((indent < nestedLists.value.get(nestedLists.value.getLength() - 1).contentIndent))
                                            {
                                                i.value += MarkdownList.endAllNestedLists(buf, i.value, nestedLists);
                                            }
                                        }
                                        for (; (quoteLevel.value < lineQuoteLevel);quoteLevel.value += 1){
                                            i.value = (buf).insert(i.value, new ByteSlice("$(BLOCKQUOTE\n"));
                                            iLineStart.value = (iParagraphStart.value = i.value);
                                        }
                                        i.value -= 1;
                                    }
                                    else
                                    {
                                        i.value -= 1;
                                        if (nestedLists.value.getLength() != 0)
                                        {
                                            MarkdownList.handleSiblingOrEndingList(buf, i, iParagraphStart, nestedLists);
                                        }
                                    }
                                    break;
                                }
                                leadingBlank = false;
                                if (inCode != 0)
                                {
                                    break;
                                }
                                ByteSlice se_1 = ((sc.get())._module.escapetable.get()).escapeChar((byte)62).copy();
                                if (se_1.getLength() != 0)
                                {
                                    (buf).remove(i.value, 1);
                                    i.value = (buf).insert(i.value, se_1);
                                    i.value--;
                                }
                                break;
                            case 38:
                                leadingBlank = false;
                                if (inCode != 0)
                                {
                                    break;
                                }
                                BytePtr p_1 = pcopy(toBytePtr(buf.data.getPtr(i.value)));
                                if (((p_1.get(1) & 0xFF) == 35) || (isalpha((p_1.get(1) & 0xFF)) != 0))
                                {
                                    break;
                                }
                                ByteSlice se_2 = ((sc.get())._module.escapetable.get()).escapeChar((byte)38).copy();
                                if (se_2.getLength() != 0)
                                {
                                    (buf).remove(i.value, 1);
                                    i.value = (buf).insert(i.value, se_2);
                                    i.value--;
                                }
                                break;
                            case 96:
                                int iAfterDelimiter = skipChars(buf, i.value, new ByteSlice("`"));
                                int count = iAfterDelimiter - i.value;
                                if ((inBacktick == count))
                                {
                                    inBacktick = 0;
                                    inCode = 0;
                                    Ref<OutBuffer> codebuf = ref(new OutBuffer());
                                    try {
                                        codebuf.value.write(((buf).peekSlice().getPtr(0).plus(iCodeStart).plus(count)), i.value - (iCodeStart + count));
                                        highlightCode(sc, a, codebuf.value, 0);
                                        escapeStrayParenthesis(loc, codebuf.value, 0, false);
                                        (buf).remove(iCodeStart, i.value - iCodeStart + count);
                                        ByteSlice pre = new ByteSlice("$(DDOC_BACKQUOTED ").copy();
                                        i.value = (buf).insert(iCodeStart, toByteSlice(pre));
                                        i.value = (buf).insert(i.value, codebuf.value.peekSlice());
                                        i.value = (buf).insert(i.value, new ByteSlice(")"));
                                        i.value--;
                                        break;
                                    }
                                    finally {
                                    }
                                }
                                if (leadingBlank && global.params.markdown && (count >= 3))
                                {
                                    boolean moreBackticks = false;
                                    {
                                        int j_2 = iAfterDelimiter;
                                        for (; !moreBackticks && (j_2 < (buf).offset);j_2 += 1) {
                                            if ((((buf).data.get(j_2) & 0xFF) == 96))
                                            {
                                                moreBackticks = true;
                                            }
                                            else if ((((buf).data.get(j_2) & 0xFF) == 13) || (((buf).data.get(j_2) & 0xFF) == 10))
                                            {
                                                break;
                                            }
                                        }
                                    }
                                    if (!moreBackticks)
                                    {
                                        /*goto case*/{ __dispatch12 = 45; continue dispatched_12; }
                                    }
                                }
                                if (inCode != 0)
                                {
                                    if (inBacktick != 0)
                                    {
                                        i.value = iAfterDelimiter - 1;
                                    }
                                    break;
                                }
                                inCode = (c & 0xFF);
                                inBacktick = count;
                                codeIndent = 0;
                                iCodeStart = i.value;
                                i.value = iAfterDelimiter - 1;
                                break;
                            case 35:
                                if (leadingBlank && (inCode == 0))
                                {
                                    leadingBlank = false;
                                    headingLevel.value = detectAtxHeadingLevel(buf, i.value);
                                    if (headingLevel.value == 0)
                                    {
                                        break;
                                    }
                                    i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                    if (!lineQuoted && (quoteLevel.value != 0))
                                    {
                                        i.value += endAllListsAndQuotes(buf, iLineStart, nestedLists, quoteLevel, quoteMacroLevel);
                                    }
                                    i.value = skipChars(buf, i.value + headingLevel.value, new ByteSlice(" \u0009"));
                                    (buf).remove(iLineStart.value, i.value - iLineStart.value);
                                    i.value = (iParagraphStart.value = iLineStart.value);
                                    removeAnyAtxHeadingSuffix(buf, i.value);
                                    i.value -= 1;
                                    headingMacroLevel = macroLevel;
                                }
                                break;
                            case 126:
                                if (leadingBlank && global.params.markdown)
                                {
                                    int iAfterDelimiter_1 = skipChars(buf, i.value, new ByteSlice("~"));
                                    if ((iAfterDelimiter_1 - i.value >= 3))
                                    {
                                        /*goto case*/{ __dispatch12 = 45; continue dispatched_12; }
                                    }
                                }
                                leadingBlank = false;
                                break;
                            case 45:
                                __dispatch12 = 0;
                                if (leadingBlank)
                                {
                                    if ((inCode == 0) && ((c & 0xFF) == 45))
                                    {
                                        MarkdownList list = MarkdownList.parseItem(buf, iLineStart.value, i.value).copy();
                                        if (list.isValid())
                                        {
                                            if (replaceMarkdownThematicBreak(buf, i, iLineStart.value, loc))
                                            {
                                                removeBlankLineMacro(buf, iPrecedingBlankLine, i);
                                                iParagraphStart.value = skipChars(buf, i.value + 1, new ByteSlice(" \u0009\r\n"));
                                                break;
                                            }
                                            else
                                            {
                                                /*goto case*/{ __dispatch12 = 43; continue dispatched_12; }
                                            }
                                        }
                                    }
                                    int istart = i.value;
                                    int eollen = 0;
                                    leadingBlank = false;
                                    byte c0_1 = c;
                                    int iInfoString = 0;
                                    if (inCode == 0)
                                    {
                                        codeLanguage.getLength() = 0;
                                    }
                                L_outer17:
                                    for (; 1 != 0;){
                                        i.value += 1;
                                        if ((i.value >= (buf).offset))
                                        {
                                            break;
                                        }
                                        c = (byte)(buf).data.get(i.value);
                                        if (((c & 0xFF) == 10))
                                        {
                                            eollen = 1;
                                            break;
                                        }
                                        if (((c & 0xFF) == 13))
                                        {
                                            eollen = 1;
                                            if ((i.value + 1 >= (buf).offset))
                                            {
                                                break;
                                            }
                                            if ((((buf).data.get(i.value + 1) & 0xFF) == 10))
                                            {
                                                eollen = 2;
                                                break;
                                            }
                                        }
                                        if (((c & 0xFF) != (c0_1 & 0xFF)) || (iInfoString != 0))
                                        {
                                            if (global.params.markdown && (iInfoString == 0) && (inCode == 0) && (i.value - istart >= 3))
                                            {
                                                codeFenceLength = i.value - istart;
                                                i.value = (iInfoString = skipChars(buf, i.value, new ByteSlice(" \u0009")));
                                            }
                                            else if ((iInfoString != 0) && ((c & 0xFF) != 96))
                                            {
                                                if ((codeLanguage.getLength() == 0) && ((c & 0xFF) == 32) || ((c & 0xFF) == 9))
                                                {
                                                    codeLanguage = toByteSlice(idup((buf).data.slice(iInfoString,i.value))).copy();
                                                }
                                            }
                                            else
                                            {
                                                iInfoString = 0;
                                                /*goto Lcont*/throw Dispatch0.INSTANCE;
                                            }
                                        }
                                    }
                                    if ((i.value - istart < 3) || (inCode != 0) && (inCode != (c0_1 & 0xFF)) || (inCode != 45) && (i.value - istart < codeFenceLength))
                                    {
                                        /*goto Lcont*/throw Dispatch0.INSTANCE;
                                    }
                                    if (iInfoString != 0)
                                    {
                                        if (codeLanguage.getLength() == 0)
                                        {
                                            codeLanguage = toByteSlice(idup((buf).data.slice(iInfoString,i.value))).copy();
                                        }
                                    }
                                    else
                                    {
                                        codeFenceLength = i.value - istart;
                                    }
                                    (buf).remove(iLineStart.value, i.value - iLineStart.value + eollen);
                                    i.value = iLineStart.value;
                                    if (eollen != 0)
                                    {
                                        leadingBlank = true;
                                    }
                                    if ((inCode != 0) && (i.value <= iCodeStart))
                                    {
                                        inCode = 0;
                                        break;
                                    }
                                    if (inCode != 0)
                                    {
                                        inCode = 0;
                                        Ref<OutBuffer> codebuf_1 = ref(new OutBuffer());
                                        try {
                                            codebuf_1.value.write(((buf).data.plus(iCodeStart)), i.value - iCodeStart);
                                            codebuf_1.value.writeByte(0);
                                            boolean lineStart = true;
                                            BytePtr endp = pcopy(toBytePtr(codebuf_1.value.data).plus(codebuf_1.value.offset));
                                            {
                                                BytePtr p_2 = pcopy(toBytePtr(codebuf_1.value.data));
                                                for (; (p_2.lessThan(endp));){
                                                    if (lineStart)
                                                    {
                                                        int j_3 = codeIndent;
                                                        BytePtr q = pcopy(p_2);
                                                        for (; (j_3-- > 0) && (q.lessThan(endp)) && isIndentWS(q);) {
                                                            q.plusAssign(1);
                                                        }
                                                        codebuf_1.value.remove(((p_2.minus(toBytePtr(codebuf_1.value.data)))), ((q.minus(p_2))));
                                                        assert((toBytePtr(codebuf_1.value.data).lessOrEqual(p_2)));
                                                        assert((p_2.lessThan(toBytePtr(codebuf_1.value.data).plus(codebuf_1.value.offset))));
                                                        lineStart = false;
                                                        endp = pcopy(toBytePtr(codebuf_1.value.data).plus(codebuf_1.value.offset));
                                                        continue;
                                                    }
                                                    if (((p_2.get() & 0xFF) == 10))
                                                    {
                                                        lineStart = true;
                                                    }
                                                    p_2.plusAssign(1);
                                                }
                                            }
                                            if ((codeLanguage.getLength() == 0) || __equals(codeLanguage, new ByteSlice("dlang")) || __equals(codeLanguage, new ByteSlice("d")))
                                            {
                                                highlightCode2(sc, a, codebuf_1.value, 0);
                                            }
                                            else
                                            {
                                                codebuf_1.value.remove(codebuf_1.value.offset - 1, 1);
                                            }
                                            escapeStrayParenthesis(loc, codebuf_1.value, 0, false);
                                            (buf).remove(iCodeStart, i.value - iCodeStart);
                                            i.value = (buf).insert(iCodeStart, codebuf_1.value.peekSlice());
                                            i.value = (buf).insert(i.value, new ByteSlice(")\n"));
                                            i.value -= 2;
                                        }
                                        finally {
                                        }
                                    }
                                    else
                                    {
                                        i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                        if (!lineQuoted && (quoteLevel.value != 0))
                                        {
                                            int delta_1 = endAllListsAndQuotes(buf, iLineStart, nestedLists, quoteLevel, quoteMacroLevel);
                                            i.value += delta_1;
                                            istart += delta_1;
                                        }
                                        inCode = (c0_1 & 0xFF);
                                        codeIndent = istart - iLineStart.value;
                                        if ((codeLanguage.getLength() != 0) && !__equals(codeLanguage, new ByteSlice("dlang")) && !__equals(codeLanguage, new ByteSlice("d")))
                                        {
                                            {
                                                int j_4 = 0;
                                                for (; (j_4 < codeLanguage.getLength() - 1);j_4 += 1) {
                                                    if (((codeLanguage.get(j_4) & 0xFF) == 92) && (ispunct((codeLanguage.get(j_4 + 1) & 0xFF)) != 0))
                                                    {
                                                        codeLanguage = concat(codeLanguage.slice(0,j_4), codeLanguage.slice(j_4 + 1,codeLanguage.getLength())).copy();
                                                    }
                                                }
                                            }
                                            if (global.params.vmarkdown)
                                            {
                                                message(loc, new BytePtr("Ddoc: adding code block for language '%.*s'"), codeLanguage.getLength(), codeLanguage.getPtr(0));
                                            }
                                            i.value = (buf).insert(i.value, new ByteSlice("$(OTHER_CODE "));
                                            i.value = (buf).insert(i.value, toByteSlice(codeLanguage));
                                            i.value = (buf).insert(i.value, new ByteSlice(","));
                                        }
                                        else
                                        {
                                            i.value = (buf).insert(i.value, new ByteSlice("$(D_CODE "));
                                        }
                                        iCodeStart = i.value;
                                        i.value--;
                                        leadingBlank = true;
                                    }
                                }
                                break;
                            case 95:
                                if (leadingBlank && (inCode == 0) && replaceMarkdownThematicBreak(buf, i, iLineStart.value, loc))
                                {
                                    i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                    if (!lineQuoted && (quoteLevel.value != 0))
                                    {
                                        i.value += endAllListsAndQuotes(buf, iLineStart, nestedLists, quoteLevel, quoteMacroLevel);
                                    }
                                    removeBlankLineMacro(buf, iPrecedingBlankLine, i);
                                    iParagraphStart.value = skipChars(buf, i.value + 1, new ByteSlice(" \u0009\r\n"));
                                    break;
                                }
                                /*goto default*/ { __dispatch12 = -6; continue dispatched_12; }
                            case 43:
                                __dispatch12 = 0;
                            case 48:
                            case 49:
                            case 50:
                            case 51:
                            case 52:
                            case 53:
                            case 54:
                            case 55:
                            case 56:
                            case 57:
                                if (leadingBlank && (inCode == 0))
                                {
                                    MarkdownList list_1 = MarkdownList.parseItem(buf, iLineStart.value, i.value).copy();
                                    if (list_1.isValid())
                                    {
                                        if ((nestedLists.value.getLength() == 0) && (list_1.orderedStart.getLength() != 0) && (iParagraphStart.value < iLineStart.value))
                                        {
                                            i.value += list_1.orderedStart.getLength() - 1;
                                            break;
                                        }
                                        i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                        if (!lineQuoted && (quoteLevel.value != 0))
                                        {
                                            int delta_2 = endAllListsAndQuotes(buf, iLineStart, nestedLists, quoteLevel, quoteMacroLevel);
                                            i.value += delta_2;
                                            list_1.iStart.value += delta_2;
                                            list_1.iContentStart += delta_2;
                                        }
                                        list_1.macroLevel = macroLevel;
                                        list_1.startItem(buf, iLineStart, i, iPrecedingBlankLine, nestedLists, loc);
                                        break;
                                    }
                                }
                                leadingBlank = false;
                                break;
                            case 42:
                                if ((inCode != 0) || (inBacktick != 0) || !global.params.markdown)
                                {
                                    leadingBlank = false;
                                    break;
                                }
                                if (leadingBlank)
                                {
                                    if (replaceMarkdownThematicBreak(buf, i, iLineStart.value, loc))
                                    {
                                        i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                        if (!lineQuoted && (quoteLevel.value != 0))
                                        {
                                            i.value += endAllListsAndQuotes(buf, iLineStart, nestedLists, quoteLevel, quoteMacroLevel);
                                        }
                                        removeBlankLineMacro(buf, iPrecedingBlankLine, i);
                                        iParagraphStart.value = skipChars(buf, i.value + 1, new ByteSlice(" \u0009\r\n"));
                                        break;
                                    }
                                    MarkdownList list_2 = MarkdownList.parseItem(buf, iLineStart.value, i.value).copy();
                                    if (list_2.isValid())
                                    {
                                        /*goto case*/{ __dispatch12 = 43; continue dispatched_12; }
                                    }
                                }
                                int leftC = (i.value > offset) ? ((buf).data.get(i.value - 1) & 0xFF) : 0;
                                int iAfterEmphasis = skipChars(buf, i.value + 1, new ByteSlice("*"));
                                int rightC = (iAfterEmphasis < (buf).offset) ? ((buf).data.get(iAfterEmphasis) & 0xFF) : 0;
                                int count_1 = (iAfterEmphasis - i.value);
                                boolean leftFlanking = (rightC != 0) && (isspace(rightC) == 0) && (ispunct(rightC) == 0) || (leftC == 0) || (isspace(leftC) != 0) || (ispunct(leftC) != 0);
                                boolean rightFlanking = (leftC != 0) && (isspace(leftC) == 0) && (ispunct(leftC) == 0) || (rightC == 0) || (isspace(rightC) != 0) || (ispunct(rightC) != 0);
                                MarkdownDelimiter emphasis = new MarkdownDelimiter(i.value, count_1, macroLevel, leftFlanking, rightFlanking, false, c).copy();
                                if (!emphasis.leftFlanking && !emphasis.rightFlanking)
                                {
                                    i.value = iAfterEmphasis - 1;
                                    break;
                                }
                                inlineDelimiters.value.append(emphasis);
                                i.value += emphasis.count;
                                i.value -= 1;
                                break;
                            case 33:
                                leadingBlank = false;
                                if ((inCode != 0) || !global.params.markdown)
                                {
                                    break;
                                }
                                if ((i.value < (buf).offset - 1) && (((buf).data.get(i.value + 1) & 0xFF) == 91))
                                {
                                    MarkdownDelimiter imageStart = new MarkdownDelimiter(i.value, 2, macroLevel, false, false, false, c).copy();
                                    inlineDelimiters.value.append(imageStart);
                                    i.value += 1;
                                }
                                break;
                            case 91:
                                if ((inCode != 0) || !global.params.markdown)
                                {
                                    leadingBlank = false;
                                    break;
                                }
                                int leftC_1 = (i.value > offset) ? ((buf).data.get(i.value - 1) & 0xFF) : 0;
                                boolean rightFlanking_1 = (leftC_1 != 0) && (isspace(leftC_1) == 0) && (ispunct(leftC_1) == 0);
                                boolean atParagraphStart = leadingBlank && (iParagraphStart.value >= iLineStart.value);
                                MarkdownDelimiter linkStart = new MarkdownDelimiter(i.value, 1, macroLevel, false, rightFlanking_1, atParagraphStart, c).copy();
                                inlineDelimiters.value.append(linkStart);
                                leadingBlank = false;
                                break;
                            case 93:
                                leadingBlank = false;
                                if ((inCode != 0) || !global.params.markdown)
                                {
                                    break;
                                }
                                {
                                    int d = inlineDelimiters.value.getLength() - 1;
                                    for (; (d >= 0);d -= 1){
                                        MarkdownDelimiter delimiter = inlineDelimiters.value.get(d).copy();
                                        if (((delimiter.type & 0xFF) == 91) || ((delimiter.type & 0xFF) == 33))
                                        {
                                            if (delimiter.isValid() && MarkdownLink.replaceLink(buf, i, loc, inlineDelimiters, d, linkReferences))
                                            {
                                                if ((i.value <= delimiter.iStart))
                                                {
                                                    leadingBlank = true;
                                                }
                                                if (((delimiter.type & 0xFF) == 91))
                                                {
                                                    d -= 1;
                                                    for (; (d >= 0);d -= 1) {
                                                        if (((inlineDelimiters.value.get(d).type & 0xFF) == 91))
                                                        {
                                                            inlineDelimiters.value.get(d).invalidate();
                                                        }
                                                    }
                                                }
                                            }
                                            else
                                            {
                                                inlineDelimiters.value = concat(inlineDelimiters.value.slice(0,d), inlineDelimiters.value.slice((d + 1),inlineDelimiters.value.getLength())).copy();
                                            }
                                            break;
                                        }
                                    }
                                }
                                break;
                            case 124:
                                if ((inCode != 0) || !global.params.markdown)
                                {
                                    leadingBlank = false;
                                    break;
                                }
                                tableRowDetected = true;
                                inlineDelimiters.value.append(new MarkdownDelimiter(i.value, 1, macroLevel, leadingBlank, false, false, c));
                                leadingBlank = false;
                                break;
                            case 92:
                                leadingBlank = false;
                                if ((inCode != 0) || (i.value + 1 >= (buf).offset) || !global.params.markdown)
                                {
                                    break;
                                }
                                byte c1 = (byte)(buf).data.get(i.value + 1);
                                if (ispunct((c1 & 0xFF)) != 0)
                                {
                                    if (global.params.vmarkdown)
                                    {
                                        message(loc, new BytePtr("Ddoc: backslash-escaped %c"), (c1 & 0xFF));
                                    }
                                    (buf).remove(i.value, 1);
                                    ByteSlice se_3 = ((sc.get())._module.escapetable.get()).escapeChar(c1).copy();
                                    if (se_3.getLength() == 0)
                                    {
                                        se_3 = ((c1 & 0xFF) == 36) ? new ByteSlice("$(DOLLAR)") : ((c1 & 0xFF) == 44) ? new ByteSlice("$(COMMA)") : new ByteSlice().copy();
                                    }
                                    if (se_3.getLength() != 0)
                                    {
                                        (buf).remove(i.value, 1);
                                        i.value = (buf).insert(i.value, se_3);
                                        i.value--;
                                    }
                                }
                                break;
                            case 36:
                                leadingBlank = false;
                                if ((inCode != 0) || (inBacktick != 0))
                                {
                                    break;
                                }
                                ByteSlice slice_1 = (buf).peekSlice().copy();
                                BytePtr p_3 = pcopy(slice_1.getPtr(i.value));
                                if (((p_3.get(1) & 0xFF) == 40) && isIdStart(p_3.getPtr(2)))
                                {
                                    macroLevel += 1;
                                }
                                break;
                            case 40:
                                if ((inCode == 0) && (i.value > offset) && (((buf).data.get(i.value - 1) & 0xFF) != 36))
                                {
                                    parenLevel += 1;
                                }
                                break;
                            case 41:
                                leadingBlank = false;
                                if ((inCode != 0) || (inBacktick != 0))
                                {
                                    break;
                                }
                                if ((parenLevel > 0))
                                {
                                    parenLevel -= 1;
                                }
                                else if (macroLevel != 0)
                                {
                                    int downToLevel = inlineDelimiters.value.getLength();
                                    for (; (downToLevel > 0) && (inlineDelimiters.value.get((downToLevel - 1)).macroLevel >= macroLevel);) {
                                        downToLevel -= 1;
                                    }
                                    if ((headingLevel.value != 0) && (headingMacroLevel >= macroLevel))
                                    {
                                        endMarkdownHeading(buf, iParagraphStart.value, i, loc, headingLevel);
                                        removeBlankLineMacro(buf, iPrecedingBlankLine, i);
                                    }
                                    i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
                                    for (; (nestedLists.value.getLength() != 0) && (nestedLists.value.get(nestedLists.value.getLength() - 1).macroLevel >= macroLevel);){
                                        i.value = (buf).insert(i.value, new ByteSlice(")\n)"));
                                        nestedLists.value.getLength() = nestedLists.value.getLength() - 1;
                                    }
                                    if ((quoteLevel.value != 0) && (quoteMacroLevel.value >= macroLevel))
                                    {
                                        i.value += endAllMarkdownQuotes(buf, i.value, quoteLevel);
                                    }
                                    i.value += replaceMarkdownEmphasis(buf, loc, inlineDelimiters, downToLevel);
                                    macroLevel -= 1;
                                    quoteMacroLevel.value = 0;
                                }
                                break;
                            default:
                            __dispatch12 = 0;
                            leadingBlank = false;
                            if ((sc.get())._module.isDocFile || (inCode != 0))
                            {
                                break;
                            }
                            BytePtr start = pcopy(toBytePtr(buf.data).plus(i.value));
                            if (isIdStart(start))
                            {
                                int j_5 = skippastident(buf, i.value);
                                if ((i.value < j_5))
                                {
                                    int k = skippastURL(buf, i.value);
                                    if ((i.value < k))
                                    {
                                        if (macroLevel != 0)
                                        {
                                            i.value = k - 1;
                                        }
                                        else
                                        {
                                            i.value = (buf).bracket(i.value, new BytePtr("$(DDOC_LINK_AUTODETECT "), k, new BytePtr(")")) - 1;
                                        }
                                        break;
                                    }
                                }
                                else
                                {
                                    break;
                                }
                                int len = j_5 - i.value;
                                if (((c & 0xFF) == 95) && (i.value == 0) || (isdigit(((start.minus(1)).get() & 0xFF)) == 0) && (i.value == (buf).offset - 1) || !isReservedName(start.slice(0,len)))
                                {
                                    (buf).remove(i.value, 1);
                                    i.value = (buf).bracket(i.value, new BytePtr("$(DDOC_AUTO_PSYMBOL_SUPPRESS "), j_5 - 1, new BytePtr(")")) - 1;
                                    break;
                                }
                                if (isIdentifier(a, start, len))
                                {
                                    i.value = (buf).bracket(i.value, new BytePtr("$(DDOC_AUTO_PSYMBOL "), j_5, new BytePtr(")")) - 1;
                                    break;
                                }
                                if (isKeyword(start, len))
                                {
                                    i.value = (buf).bracket(i.value, new BytePtr("$(DDOC_AUTO_KEYWORD "), j_5, new BytePtr(")")) - 1;
                                    break;
                                }
                                if (isFunctionParameter(a, start, len) != null)
                                {
                                    i.value = (buf).bracket(i.value, new BytePtr("$(DDOC_AUTO_PARAM "), j_5, new BytePtr(")")) - 1;
                                    break;
                                }
                                i.value = j_5 - 1;
                            }
                            break;
                        }
                    } while(__dispatch12 != 0);
                }
            }
        }
        if ((inCode == 45))
        {
            error(loc, new BytePtr("unmatched `---` in DDoc comment"));
        }
        else if (inCode != 0)
        {
            (buf).insert((buf).offset, new ByteSlice(")"));
        }
        Ref<Integer> i = ref((buf).offset);
        if (headingLevel.value != 0)
        {
            endMarkdownHeading(buf, iParagraphStart.value, i, loc, headingLevel);
            removeBlankLineMacro(buf, iPrecedingBlankLine, i);
        }
        i.value += endRowAndTable(buf, iLineStart.value, i.value, loc, inlineDelimiters, columnAlignments);
        i.value += replaceMarkdownEmphasis(buf, loc, inlineDelimiters, 0);
        endAllListsAndQuotes(buf, i, nestedLists, quoteLevel, quoteMacroLevel);
    }

    // Erasure: highlightCode<Ptr, Dsymbol, Ptr, int>
    public static void highlightCode(Ptr<Scope> sc, Dsymbol s, OutBuffer buf, int offset) {
        Import imp = s.isImport();
        if ((imp != null) && (imp.aliases.length > 0))
        {
            {
                int i = 0;
                for (; (i < imp.aliases.length);i++){
                    Identifier a = imp.aliases.get(i);
                    Identifier id = a != null ? a : imp.names.get(i);
                    Loc loc = new Loc(null, 0, 0).copy();
                    {
                        Dsymbol symFromId = (sc.get()).search(loc, id, null, 0);
                        if ((symFromId) != null)
                        {
                            highlightCode(sc, symFromId, buf, offset);
                        }
                    }
                }
            }
        }
        else
        {
            Ref<OutBuffer> ancbuf = ref(new OutBuffer());
            try {
                emitAnchor(ancbuf.value, s, sc, false);
                (buf).insert(offset, ancbuf.value.peekSlice());
                offset += ancbuf.value.offset;
                Ref<DArray<Dsymbol>> a = ref(new DArray<Dsymbol>());
                try {
                    a.value.push(s);
                    highlightCode(sc, a.value, buf, offset);
                }
                finally {
                }
            }
            finally {
            }
        }
    }

    // Erasure: highlightCode<Ptr, Ptr, Ptr, int>
    public static void highlightCode(Ptr<Scope> sc, DArray<Dsymbol> a, OutBuffer buf, int offset) {
        boolean resolvedTemplateParameters = false;
        {
            int i = offset;
            for (; (i < (buf).offset);i++){
                byte c = (byte)(buf).data.get(i);
                ByteSlice se = ((sc.get())._module.escapetable.get()).escapeChar(c).copy();
                if (se.getLength() != 0)
                {
                    (buf).remove(i, 1);
                    i = (buf).insert(i, se);
                    i--;
                    continue;
                }
                BytePtr start = pcopy(toBytePtr(buf.data).plus(i));
                if (isIdStart(start))
                {
                    int j = skipPastIdentWithDots(buf, i);
                    if ((i < j))
                    {
                        int len = j - i;
                        if (isIdentifier(a, start, len))
                        {
                            i = (buf).bracket(i, new BytePtr("$(DDOC_PSYMBOL "), j, new BytePtr(")")) - 1;
                            continue;
                        }
                    }
                    j = skippastident(buf, i);
                    if ((i < j))
                    {
                        int len = j - i;
                        if (isIdentifier(a, start, len))
                        {
                            i = (buf).bracket(i, new BytePtr("$(DDOC_PSYMBOL "), j, new BytePtr(")")) - 1;
                            continue;
                        }
                        if (isFunctionParameter(a, start, len) != null)
                        {
                            i = (buf).bracket(i, new BytePtr("$(DDOC_PARAM "), j, new BytePtr(")")) - 1;
                            continue;
                        }
                        i = j - 1;
                    }
                }
                else if (!resolvedTemplateParameters)
                {
                    int previ = i;
                    {
                        int __key1106 = 0;
                        int __limit1107 = (a).length;
                        for (; (__key1106 < __limit1107);__key1106 += 1) {
                            int symi = __key1106;
                            FuncDeclaration fd = (a).get(symi).isFuncDeclaration();
                            if ((fd == null) || (fd.parent.value == null) || (fd.parent.value.isTemplateDeclaration() == null))
                            {
                                continue;
                            }
                            TemplateDeclaration td = fd.parent.value.isTemplateDeclaration();
                            DArray<Integer> paramLens = new DArray<Integer>();
                            try {
                                paramLens.reserve((td.parameters).length);
                                Ref<OutBuffer> parametersBuf = ref(new OutBuffer());
                                try {
                                    Ref<HdrGenState> hgs = ref(new HdrGenState());
                                    parametersBuf.value.writeByte(40);
                                    {
                                        int __key1109 = 0;
                                        int __limit1110 = (td.parameters).length;
                                        for (; (__key1109 < __limit1110);__key1109 += 1) {
                                            int parami = __key1109;
                                            TemplateParameter tp = (td.parameters).get(parami);
                                            if (parami != 0)
                                            {
                                                parametersBuf.value.writestring(new ByteSlice(", "));
                                            }
                                            int lastOffset = parametersBuf.value.offset;
                                            toCBuffer(tp, parametersBuf.value, ptr(hgs));
                                            paramLens.set(parami, parametersBuf.value.offset - lastOffset);
                                        }
                                    }
                                    parametersBuf.value.writeByte(41);
                                    ByteSlice templateParams = parametersBuf.value.peekSlice().copy();
                                    if (__equals(start.slice(0,templateParams.getLength()), templateParams))
                                    {
                                        ByteSlice templateParamListMacro = new ByteSlice("$(DDOC_TEMPLATE_PARAM_LIST ").copy();
                                        (buf).bracket(i, templateParamListMacro.getPtr(0), i + templateParams.getLength(), new BytePtr(")"));
                                        i += 28;
                                        {
                                            IntSlice __r1111 = paramLens.opSlice().copy();
                                            int __key1112 = 0;
                                            for (; (__key1112 < __r1111.getLength());__key1112 += 1) {
                                                int len = __r1111.get(__key1112);
                                                i = (buf).bracket(i, new BytePtr("$(DDOC_TEMPLATE_PARAM "), i + len, new BytePtr(")"));
                                                i += 2;
                                            }
                                        }
                                        resolvedTemplateParameters = true;
                                        i = previ;
                                        continue;
                                    }
                                }
                                finally {
                                }
                            }
                            finally {
                            }
                        }
                    }
                }
            }
        }
    }

    // Erasure: highlightCode3<Ptr, Ptr, Ptr, Ptr>
    public static void highlightCode3(Ptr<Scope> sc, OutBuffer buf, BytePtr p, BytePtr pend) {
        for (; (p.lessThan(pend));p.postInc()){
            ByteSlice se = ((sc.get())._module.escapetable.get()).escapeChar(p.get()).copy();
            if (se.getLength() != 0)
            {
                (buf).writestring(se);
            }
            else
            {
                (buf).writeByte((p.get() & 0xFF));
            }
        }
    }

    // Erasure: highlightCode2<Ptr, Ptr, Ptr, int>
    public static void highlightCode2(Ptr<Scope> sc, DArray<Dsymbol> a, OutBuffer buf, int offset) {
        int errorsave = global.startGagging();
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        try {
            Lexer lex = new Lexer(null, toBytePtr(buf.data), 0, (buf).offset - 1, false, true, diagnosticReporter);
            try {
                Ref<OutBuffer> res = ref(new OutBuffer());
                try {
                    BytePtr lastp = pcopy(toBytePtr(buf.data));
                    res.value.reserve((buf).offset);
                    for (; 1 != 0;){
                        Ref<Token> tok = ref(new Token().copy());
                        lex.scan(ptr(tok));
                        highlightCode3(sc, res.value, lastp, tok.value.ptr);
                        ByteSlice highlight = new ByteSlice().copy();
                        switch ((tok.value.value & 0xFF))
                        {
                            case 120:
                                if (sc == null)
                                {
                                    break;
                                }
                                int len = ((lex.p.value.minus(tok.value.ptr)));
                                if (isIdentifier(a, tok.value.ptr, len))
                                {
                                    highlight = new ByteSlice("$(D_PSYMBOL ").copy();
                                    break;
                                }
                                if (isFunctionParameter(a, tok.value.ptr, len) != null)
                                {
                                    highlight = new ByteSlice("$(D_PARAM ").copy();
                                    break;
                                }
                                break;
                            case 46:
                                highlight = new ByteSlice("$(D_COMMENT ").copy();
                                break;
                            case 121:
                                highlight = new ByteSlice("$(D_STRING ").copy();
                                break;
                            default:
                            if (tok.value.isKeyword() != 0)
                            {
                                highlight = new ByteSlice("$(D_KEYWORD ").copy();
                            }
                            break;
                        }
                        if (highlight.getLength() != 0)
                        {
                            res.value.writestring(highlight);
                            int o = res.value.offset;
                            highlightCode3(sc, res.value, tok.value.ptr, lex.p.value);
                            if (((tok.value.value & 0xFF) == 46) || ((tok.value.value & 0xFF) == 121))
                            {
                                escapeDdocString(res.value, o);
                            }
                            res.value.writeByte(41);
                        }
                        else
                        {
                            highlightCode3(sc, res.value, tok.value.ptr, lex.p.value);
                        }
                        if (((tok.value.value & 0xFF) == 11))
                        {
                            break;
                        }
                        lastp = pcopy(lex.p.value);
                    }
                    (buf).setsize(offset);
                    (buf).write(res.value);
                    global.endGagging(errorsave);
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

    // Erasure: isCVariadicArg<Array>
    public static boolean isCVariadicArg(ByteSlice p) {
        return (p.getLength() >= 3) && __equals(p.slice(0,3), new ByteSlice("..."));
    }

    // Erasure: isIdStart<Ptr>
    public static boolean isIdStart(BytePtr p) {
        Ref<Integer> c = ref((p.get() & 0xFF));
        if ((isalpha(c.value) != 0) || (c.value == 95))
        {
            return true;
        }
        if ((c.value >= 128))
        {
            Ref<Integer> i = ref(0);
            if (utf_decodeChar(p, 4, i, c) != null)
            {
                return false;
            }
            if (isUniAlpha(c.value))
            {
                return true;
            }
        }
        return false;
    }

    // Erasure: isIdTail<Ptr>
    public static boolean isIdTail(BytePtr p) {
        Ref<Integer> c = ref((p.get() & 0xFF));
        if ((isalnum(c.value) != 0) || (c.value == 95))
        {
            return true;
        }
        if ((c.value >= 128))
        {
            Ref<Integer> i = ref(0);
            if (utf_decodeChar(p, 4, i, c) != null)
            {
                return false;
            }
            if (isUniAlpha(c.value))
            {
                return true;
            }
        }
        return false;
    }

    // Erasure: isIndentWS<Ptr>
    public static boolean isIndentWS(BytePtr p) {
        return ((p.get() & 0xFF) == 32) || ((p.get() & 0xFF) == 9);
    }

    // Erasure: utfStride<Ptr>
    public static int utfStride(BytePtr p) {
        Ref<Integer> c = ref((p.get() & 0xFF));
        if ((c.value < 128))
        {
            return 1;
        }
        Ref<Integer> i = ref(0);
        utf_decodeChar(p, 4, i, c);
        return i.value;
    }

    // Erasure: stripLeadingNewlines<Ptr>
    public static BytePtr stripLeadingNewlines(BytePtr s) {
        for (; (s != null) && ((s.get() & 0xFF) == 10) || ((s.get() & 0xFF) == 13);) {
            s.postInc();
        }
        return s;
    }

}
