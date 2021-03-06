package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.lexer.*;
import static org.dlang.dmd.parsetimevisitor.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utils.*;

public class astbase {
    private static final byte[] initializer_0 = {(byte)12, (byte)13, (byte)14, (byte)15, (byte)16, (byte)17, (byte)18, (byte)19, (byte)20, (byte)42, (byte)43, (byte)21, (byte)22, (byte)23, (byte)24, (byte)25, (byte)26, (byte)27, (byte)28, (byte)29, (byte)30, (byte)31, (byte)32, (byte)33, (byte)34};
    private static final SCstring[] initializer_1 = {new SCstring(256L, TOK.auto_, null), new SCstring(524288L, TOK.scope_, null), new SCstring(1L, TOK.static_, null), new SCstring(2L, TOK.extern_, null), new SCstring(4L, TOK.const_, null), new SCstring(8L, TOK.final_, null), new SCstring(16L, TOK.abstract_, null), new SCstring(512L, TOK.synchronized_, null), new SCstring(1024L, TOK.deprecated_, null), new SCstring(128L, TOK.override_, null), new SCstring(8192L, TOK.lazy_, null), new SCstring(268435456L, TOK.alias_, null), new SCstring(4096L, TOK.out_, null), new SCstring(2048L, TOK.in_, null), new SCstring(8388608L, TOK.enum_, null), new SCstring(1048576L, TOK.immutable_, null), new SCstring(536870912L, TOK.shared_, null), new SCstring(33554432L, TOK.nothrow_, null), new SCstring(2147483648L, TOK.inout_, null), new SCstring(67108864L, TOK.pure_, null), new SCstring(2097152L, TOK.ref_, null), new SCstring(134217728L, TOK.reserved, null), new SCstring(1073741824L, TOK.gshared, null), new SCstring(4398046511104L, TOK.at, new BytePtr("@nogc")), new SCstring(4294967296L, TOK.at, new BytePtr("@property")), new SCstring(8589934592L, TOK.at, new BytePtr("@safe")), new SCstring(17179869184L, TOK.at, new BytePtr("@trusted")), new SCstring(34359738368L, TOK.at, new BytePtr("@system")), new SCstring(137438953472L, TOK.at, new BytePtr("@disable")), new SCstring(1125899906842624L, TOK.at, new BytePtr("@__future")), new SCstring(0L, TOK.reserved, null)};
    static int __ctorpackageTag = 0;
    static BytePtr __ctormsg = new BytePtr("only object.d can define this reserved class name");
    static ByteSlice _initbasetab = slice(initializer_0);
    private static class SCstring
    {
        private long stc = 0L;
        private byte tok = 0;
        private BytePtr id = null;
        public SCstring(){ }
        public SCstring copy(){
            SCstring r = new SCstring();
            r.stc = stc;
            r.tok = tok;
            r.id = id;
            return r;
        }
        public SCstring(long stc, byte tok, BytePtr id) {
            this.stc = stc;
            this.tok = tok;
            this.id = id;
        }

        public SCstring opAssign(SCstring that) {
            this.stc = that.stc;
            this.tok = that.tok;
            this.id = that.id;
            return this;
        }
    }
    static Slice<SCstring> stcToCharstable = slice(initializer_1);

    public static class ASTBase
    {

        public static class Sizeok 
        {
            public static final int none = 0;
            public static final int fwd = 1;
            public static final int done = 2;
        }


        public static class Baseok 
        {
            public static final int none = 0;
            public static final int start = 1;
            public static final int done = 2;
            public static final int semanticdone = 3;
        }


        public static class MODFlags 
        {
            public static final int const_ = 1;
            public static final int immutable_ = 4;
            public static final int shared_ = 2;
            public static final int wild = 8;
            public static final int wildconst = 9;
            public static final int mutable = 16;
        }


        public static class STC 
        {
            public static final long undefined_ = 0L;
            public static final long static_ = 1L;
            public static final long extern_ = 2L;
            public static final long const_ = 4L;
            public static final long final_ = 8L;
            public static final long abstract_ = 16L;
            public static final long parameter = 32L;
            public static final long field = 64L;
            public static final long override_ = 128L;
            public static final long auto_ = 256L;
            public static final long synchronized_ = 512L;
            public static final long deprecated_ = 1024L;
            public static final long in_ = 2048L;
            public static final long out_ = 4096L;
            public static final long lazy_ = 8192L;
            public static final long foreach_ = 16384L;
            public static final long variadic = 65536L;
            public static final long ctorinit = 131072L;
            public static final long templateparameter = 262144L;
            public static final long scope_ = 524288L;
            public static final long immutable_ = 1048576L;
            public static final long ref_ = 2097152L;
            public static final long init = 4194304L;
            public static final long manifest = 8388608L;
            public static final long nodtor = 16777216L;
            public static final long nothrow_ = 33554432L;
            public static final long pure_ = 67108864L;
            public static final long tls = 134217728L;
            public static final long alias_ = 268435456L;
            public static final long shared_ = 536870912L;
            public static final long gshared = 1073741824L;
            public static final long wild = 2147483648L;
            public static final long property = 4294967296L;
            public static final long safe = 8589934592L;
            public static final long trusted = 17179869184L;
            public static final long system = 34359738368L;
            public static final long ctfe = 68719476736L;
            public static final long disable = 137438953472L;
            public static final long result = 274877906944L;
            public static final long nodefaultctor = 549755813888L;
            public static final long temp = 1099511627776L;
            public static final long rvalue = 2199023255552L;
            public static final long nogc = 4398046511104L;
            public static final long volatile_ = 8796093022208L;
            public static final long return_ = 17592186044416L;
            public static final long autoref = 35184372088832L;
            public static final long inference = 70368744177664L;
            public static final long exptemp = 140737488355328L;
            public static final long maybescope = 281474976710656L;
            public static final long scopeinferred = 562949953421312L;
            public static final long future = 1125899906842624L;
            public static final long local = 2251799813685248L;
            public static final long returninferred = 4503599627370496L;
            public static final long TYPECTOR = 2685403140L;
            public static final long FUNCATTR = 4462573780992L;
        }

        public static long STCStorageClass = 22196369506207L;

        public static class ENUMTY 
        {
            public static final int Tarray = 0;
            public static final int Tsarray = 1;
            public static final int Taarray = 2;
            public static final int Tpointer = 3;
            public static final int Treference = 4;
            public static final int Tfunction = 5;
            public static final int Tident = 6;
            public static final int Tclass = 7;
            public static final int Tstruct = 8;
            public static final int Tenum = 9;
            public static final int Tdelegate = 10;
            public static final int Tnone = 11;
            public static final int Tvoid = 12;
            public static final int Tint8 = 13;
            public static final int Tuns8 = 14;
            public static final int Tint16 = 15;
            public static final int Tuns16 = 16;
            public static final int Tint32 = 17;
            public static final int Tuns32 = 18;
            public static final int Tint64 = 19;
            public static final int Tuns64 = 20;
            public static final int Tfloat32 = 21;
            public static final int Tfloat64 = 22;
            public static final int Tfloat80 = 23;
            public static final int Timaginary32 = 24;
            public static final int Timaginary64 = 25;
            public static final int Timaginary80 = 26;
            public static final int Tcomplex32 = 27;
            public static final int Tcomplex64 = 28;
            public static final int Tcomplex80 = 29;
            public static final int Tbool = 30;
            public static final int Tchar = 31;
            public static final int Twchar = 32;
            public static final int Tdchar = 33;
            public static final int Terror = 34;
            public static final int Tinstance = 35;
            public static final int Ttypeof = 36;
            public static final int Ttuple = 37;
            public static final int Tslice = 38;
            public static final int Treturn = 39;
            public static final int Tnull = 40;
            public static final int Tvector = 41;
            public static final int Tint128 = 42;
            public static final int Tuns128 = 43;
            public static final int TMAX = 44;
        }


        public static class TFlags 
        {
            public static final int integral = 1;
            public static final int floating = 2;
            public static final int unsigned = 4;
            public static final int real_ = 8;
            public static final int imaginary = 16;
            public static final int complex = 32;
            public static final int char_ = 64;
        }


        public static class PKG 
        {
            public static final int unknown = 0;
            public static final int module_ = 1;
            public static final int package_ = 2;
        }


        public static class StructPOD 
        {
            public static final int no = 0;
            public static final int yes = 1;
            public static final int fwd = 2;
        }


        public static class TRUST 
        {
            public static final int default_ = 0;
            public static final int system = 1;
            public static final int trusted = 2;
            public static final int safe = 3;
        }


        public static class PURE 
        {
            public static final int impure = 0;
            public static final int fwdref = 1;
            public static final int weak = 2;
            public static final int const_ = 3;
            public static final int strong = 4;
        }


        public static class AliasThisRec 
        {
            public static final int no = 0;
            public static final int yes = 1;
            public static final int fwdref = 2;
            public static final int typeMask = 3;
            public static final int tracing = 4;
            public static final int tracingDT = 8;
        }


        public static class VarArg 
        {
            public static final int none = 0;
            public static final int variadic = 1;
            public static final int typesafe = 2;
        }

        public static abstract class ASTNode extends RootObject
        {
            // Erasure: accept<>
            public abstract void accept(ParseTimeVisitorASTBase v);


            // Erasure: __ctor<>
            public  ASTNode() {
                super();
            }


            public abstract ASTNode copy();
        }
        public static class Dsymbol extends ASTNode
        {
            public Loc loc = new Loc();
            public Identifier ident = null;
            public UnitTestDeclaration ddocUnittest = null;
            public UserAttributeDeclaration userAttribDecl = null;
            public Dsymbol parent = null;
            public BytePtr comment = null;
            // Erasure: __ctor<>
            public  Dsymbol() {
                super();
            }

            // Erasure: __ctor<Identifier>
            public  Dsymbol(Identifier ident) {
                super();
                this.ident = ident;
            }

            // Erasure: addComment<Ptr>
            public  void addComment(BytePtr comment) {
                if (this.comment == null)
                {
                    this.comment = pcopy(comment);
                }
                else if ((comment != null) && (strcmp(comment, this.comment) != 0))
                {
                    this.comment = pcopy(Lexer.combineComments(this.comment, comment, true));
                }
            }

            // Erasure: toChars<>
            public  BytePtr toChars() {
                return this.ident != null ? this.ident.toChars() : new BytePtr("__anonymous");
            }

            // Erasure: oneMember<Ptr, Identifier>
            public  boolean oneMember(Ptr<Dsymbol> ps, Identifier ident) {
                ps.set(0, this);
                return true;
            }

            // Erasure: oneMembers<DArray, Ptr, Identifier>
            public static boolean oneMembers(DArray<Dsymbol> members, Ptr<Dsymbol> ps, Identifier ident) {
                Dsymbol s = null;
                {
                    int i = 0;
                    for (; (i < members.length);i++){
                        Dsymbol sx = members.get(i);
                        boolean x = sx.oneMember(ps, ident);
                        if (!x)
                        {
                            assert((ps.get() == null));
                            return false;
                        }
                        if (ps.get() != null)
                        {
                            assert(ident != null);
                            if (((ps.get()).ident == null) || !(ps.get()).ident.equals(ident))
                            {
                                continue;
                            }
                            if (s == null)
                            {
                                s = ps.get();
                            }
                            else if (s.isOverloadable() && (ps.get()).isOverloadable())
                            {
                                FuncDeclaration f1 = s.isFuncDeclaration();
                                FuncDeclaration f2 = (ps.get()).isFuncDeclaration();
                                if ((f1 != null) && (f2 != null))
                                {
                                    for (; (!pequals(f1, f2));f1 = f1.overnext0){
                                        if ((f1.overnext0 == null))
                                        {
                                            f1.overnext0 = f2;
                                            break;
                                        }
                                    }
                                }
                            }
                            else
                            {
                                ps.set(0, null);
                                return false;
                            }
                        }
                    }
                }
                ps.set(0, s);
                return true;
            }

            // Erasure: isOverloadable<>
            public  boolean isOverloadable() {
                return false;
            }

            // Erasure: kind<>
            public  BytePtr kind() {
                return new BytePtr("symbol");
            }

            // Erasure: error<Ptr>
            public  void error(BytePtr format, Object... ap) {
                Ref<BytePtr> format_ref = ref(format);
                verror(this.loc, format_ref.value, new RawSlice<>(ap), this.kind(), new BytePtr(""), new BytePtr("Error: "));
            }

            // Erasure: isAttribDeclaration<>
            public  AttribDeclaration isAttribDeclaration() {
                return null;
            }

            // Erasure: isTemplateDeclaration<>
            public  TemplateDeclaration isTemplateDeclaration() {
                return null;
            }

            // Erasure: isFuncLiteralDeclaration<>
            public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
                return null;
            }

            // Erasure: isFuncDeclaration<>
            public  FuncDeclaration isFuncDeclaration() {
                return null;
            }

            // Erasure: isVarDeclaration<>
            public  VarDeclaration isVarDeclaration() {
                return null;
            }

            // Erasure: isTemplateInstance<>
            public  TemplateInstance isTemplateInstance() {
                return null;
            }

            // Erasure: isDeclaration<>
            public  Declaration isDeclaration() {
                return null;
            }

            // Erasure: isClassDeclaration<>
            public  ClassDeclaration isClassDeclaration() {
                return null;
            }

            // Erasure: isAggregateDeclaration<>
            public  AggregateDeclaration isAggregateDeclaration() {
                return null;
            }

            // Erasure: syntaxCopy<Dsymbol>
            public  Dsymbol syntaxCopy(Dsymbol s) {
                return null;
            }

            // Erasure: dyncast<>
            public  int dyncast() {
                return DYNCAST.dsymbol;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Dsymbol copy() {
                Dsymbol that = new Dsymbol();
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class AliasThis extends Dsymbol
        {
            public Identifier ident = null;
            // Erasure: __ctor<Loc, Identifier>
            public  AliasThis(Loc loc, Identifier ident) {
                super(null);
                this.loc.opAssign(loc.copy());
                this.ident = ident;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AliasThis() {}

            public AliasThis copy() {
                AliasThis that = new AliasThis();
                that.ident = this.ident;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static abstract class Declaration extends Dsymbol
        {
            public long storage_class = 0L;
            public Prot protection = new Prot();
            public int linkage = 0;
            public Type type = null;
            // Erasure: __ctor<Identifier>
            public  Declaration(Identifier id) {
                super(id);
                this.storage_class = 0L;
                this.protection.opAssign(new Prot(Prot.Kind.undefined, null));
                this.linkage = LINK.default_;
            }

            // Erasure: isDeclaration<>
            public  Declaration isDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Declaration() {}

            public abstract Declaration copy();
        }
        public static class ScopeDsymbol extends Dsymbol
        {
            public DArray<Dsymbol> members = null;
            // Erasure: __ctor<>
            public  ScopeDsymbol() {
                super();
            }

            // Erasure: __ctor<Identifier>
            public  ScopeDsymbol(Identifier id) {
                super(id);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ScopeDsymbol copy() {
                ScopeDsymbol that = new ScopeDsymbol();
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class Import extends Dsymbol
        {
            public DArray<Identifier> packages = null;
            public Identifier id = null;
            public Identifier aliasId = null;
            public int isstatic = 0;
            public Prot protection = new Prot();
            public DArray<Identifier> names = new DArray<Identifier>();
            public DArray<Identifier> aliases = new DArray<Identifier>();
            // Erasure: __ctor<Loc, Ptr, Identifier, Identifier, int>
            public  Import(Loc loc, DArray<Identifier> packages, Identifier id, Identifier aliasId, int isstatic) {
                super(null);
                this.loc.opAssign(loc.copy());
                this.packages = pcopy(packages);
                this.id = id;
                this.aliasId = aliasId;
                this.isstatic = isstatic;
                this.protection.opAssign(new Prot(Prot.Kind.private_, null));
                if (aliasId != null)
                {
                    this.ident = aliasId;
                }
                else if ((packages != null) && ((packages).length != 0))
                {
                    this.ident = (packages).get(0);
                }
                else
                {
                    this.ident = id;
                }
            }

            // Erasure: addAlias<Identifier, Identifier>
            public  void addAlias(Identifier name, Identifier _alias) {
                if (this.isstatic != 0)
                {
                    this.error(new BytePtr("cannot have an import bind list"));
                }
                if (this.aliasId == null)
                {
                    this.ident = null;
                }
                this.names.push(name);
                this.aliases.push(_alias);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Import() {}

            public Import copy() {
                Import that = new Import();
                that.packages = this.packages;
                that.id = this.id;
                that.aliasId = this.aliasId;
                that.isstatic = this.isstatic;
                that.protection = this.protection;
                that.names = this.names;
                that.aliases = this.aliases;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static abstract class AttribDeclaration extends Dsymbol
        {
            public DArray<Dsymbol> decl = null;
            // Erasure: __ctor<Ptr>
            public  AttribDeclaration(DArray<Dsymbol> decl) {
                super();
                this.decl = pcopy(decl);
            }

            // Erasure: isAttribDeclaration<>
            public  AttribDeclaration isAttribDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AttribDeclaration() {}

            public abstract AttribDeclaration copy();
        }
        public static class StaticAssert extends Dsymbol
        {
            public Expression exp = null;
            public Expression msg = null;
            // Erasure: __ctor<Loc, Expression, Expression>
            public  StaticAssert(Loc loc, Expression exp, Expression msg) {
                super(Id.empty);
                this.loc.opAssign(loc.copy());
                this.exp = exp;
                this.msg = msg;
            }


            public StaticAssert() {}

            public StaticAssert copy() {
                StaticAssert that = new StaticAssert();
                that.exp = this.exp;
                that.msg = this.msg;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class DebugSymbol extends Dsymbol
        {
            public int level = 0;
            // Erasure: __ctor<Loc, Identifier>
            public  DebugSymbol(Loc loc, Identifier ident) {
                super(ident);
                this.loc.opAssign(loc.copy());
            }

            // Erasure: __ctor<Loc, int>
            public  DebugSymbol(Loc loc, int level) {
                super();
                this.level = level;
                this.loc.opAssign(loc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DebugSymbol() {}

            public DebugSymbol copy() {
                DebugSymbol that = new DebugSymbol();
                that.level = this.level;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class VersionSymbol extends Dsymbol
        {
            public int level = 0;
            // Erasure: __ctor<Loc, Identifier>
            public  VersionSymbol(Loc loc, Identifier ident) {
                super(ident);
                this.loc.opAssign(loc.copy());
            }

            // Erasure: __ctor<Loc, int>
            public  VersionSymbol(Loc loc, int level) {
                super();
                this.level = level;
                this.loc.opAssign(loc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VersionSymbol() {}

            public VersionSymbol copy() {
                VersionSymbol that = new VersionSymbol();
                that.level = this.level;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class VarDeclaration extends Declaration
        {
            public Type type = null;
            public Initializer _init = null;
            public long storage_class = 0L;
            public int ctfeAdrOnStack = 0;
            public int sequenceNumber = 0;
            public static int nextSequenceNumber = 0;
            // Erasure: __ctor<Loc, Type, Identifier, Initializer, long>
            public  VarDeclaration(Loc loc, Type type, Identifier id, Initializer _init, long st) {
                super(id);
                this.type = type;
                this._init = _init;
                this.loc.opAssign(loc.copy());
                this.storage_class = st;
                this.sequenceNumber = (nextSequenceNumber += 1);
                this.ctfeAdrOnStack = -1;
            }

            // defaulted all parameters starting with #5
            public  VarDeclaration(Loc loc, Type type, Identifier id, Initializer _init) {
                this(loc, type, id, _init, 0L);
            }

            // Erasure: isVarDeclaration<>
            public  VarDeclaration isVarDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VarDeclaration() {}

            public VarDeclaration copy() {
                VarDeclaration that = new VarDeclaration();
                that.type = this.type;
                that._init = this._init;
                that.storage_class = this.storage_class;
                that.ctfeAdrOnStack = this.ctfeAdrOnStack;
                that.sequenceNumber = this.sequenceNumber;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class Ensure
        {
            public Identifier id = null;
            public Statement ensure = null;
            public Ensure(){ }
            public Ensure copy(){
                Ensure r = new Ensure();
                r.id = id;
                r.ensure = ensure;
                return r;
            }
            public Ensure(Identifier id, Statement ensure) {
                this.id = id;
                this.ensure = ensure;
            }

            public Ensure opAssign(Ensure that) {
                this.id = that.id;
                this.ensure = that.ensure;
                return this;
            }
        }
        public static class FuncDeclaration extends Declaration
        {
            public Statement fbody = null;
            public DArray<Statement> frequires = null;
            public DArray<Ensure> fensures = null;
            public Loc endloc = new Loc();
            public long storage_class = 0L;
            public Type type = null;
            public boolean inferRetType = false;
            public ForeachStatement fes = null;
            public FuncDeclaration overnext0 = null;
            // Erasure: __ctor<Loc, Loc, Identifier, long, Type>
            public  FuncDeclaration(Loc loc, Loc endloc, Identifier id, long storage_class, Type type) {
                super(id);
                this.storage_class = storage_class;
                this.type = type;
                if (type != null)
                {
                    this.storage_class &= -4465259184133L;
                }
                this.loc.opAssign(loc.copy());
                this.endloc.opAssign(endloc.copy());
                this.inferRetType = (type != null) && (type.nextOf() == null);
            }

            // Erasure: isFuncLiteralDeclaration<>
            public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
                return null;
            }

            // Erasure: isOverloadable<>
            public  boolean isOverloadable() {
                return true;
            }

            // Erasure: isFuncDeclaration<>
            public  FuncDeclaration isFuncDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncDeclaration() {}

            public FuncDeclaration copy() {
                FuncDeclaration that = new FuncDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class AliasDeclaration extends Declaration
        {
            public Dsymbol aliassym = null;
            // Erasure: __ctor<Loc, Identifier, Dsymbol>
            public  AliasDeclaration(Loc loc, Identifier id, Dsymbol s) {
                super(id);
                this.loc.opAssign(loc.copy());
                this.aliassym = s;
            }

            // Erasure: __ctor<Loc, Identifier, Type>
            public  AliasDeclaration(Loc loc, Identifier id, Type type) {
                super(id);
                this.loc.opAssign(loc.copy());
                this.type = type;
            }

            // Erasure: isOverloadable<>
            public  boolean isOverloadable() {
                return true;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AliasDeclaration() {}

            public AliasDeclaration copy() {
                AliasDeclaration that = new AliasDeclaration();
                that.aliassym = this.aliassym;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class TupleDeclaration extends Declaration
        {
            public DArray<RootObject> objects = null;
            // Erasure: __ctor<Loc, Identifier, Ptr>
            public  TupleDeclaration(Loc loc, Identifier id, DArray<RootObject> objects) {
                super(id);
                this.loc.opAssign(loc.copy());
                this.objects = pcopy(objects);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TupleDeclaration() {}

            public TupleDeclaration copy() {
                TupleDeclaration that = new TupleDeclaration();
                that.objects = this.objects;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class FuncLiteralDeclaration extends FuncDeclaration
        {
            public byte tok = 0;
            // Erasure: __ctor<Loc, Loc, Type, byte, ForeachStatement, Identifier>
            public  FuncLiteralDeclaration(Loc loc, Loc endloc, Type type, byte tok, ForeachStatement fes, Identifier id) {
                super(loc, endloc, null, 0L, type);
                this.ident = id != null ? id : Id.empty;
                this.tok = tok;
                this.fes = fes;
            }

            // defaulted all parameters starting with #6
            public  FuncLiteralDeclaration(Loc loc, Loc endloc, Type type, byte tok, ForeachStatement fes) {
                this(loc, endloc, type, tok, fes, (Identifier)null);
            }

            // Erasure: isFuncLiteralDeclaration<>
            public  FuncLiteralDeclaration isFuncLiteralDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncLiteralDeclaration() {}

            public FuncLiteralDeclaration copy() {
                FuncLiteralDeclaration that = new FuncLiteralDeclaration();
                that.tok = this.tok;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class PostBlitDeclaration extends FuncDeclaration
        {
            // Erasure: __ctor<Loc, Loc, long, Identifier>
            public  PostBlitDeclaration(Loc loc, Loc endloc, long stc, Identifier id) {
                super(loc, endloc, id, stc, null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PostBlitDeclaration() {}

            public PostBlitDeclaration copy() {
                PostBlitDeclaration that = new PostBlitDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class CtorDeclaration extends FuncDeclaration
        {
            // Erasure: __ctor<Loc, Loc, long, Type, boolean>
            public  CtorDeclaration(Loc loc, Loc endloc, long stc, Type type, boolean isCopyCtor) {
                super(loc, endloc, Id.ctor, stc, type);
            }

            // defaulted all parameters starting with #5
            public  CtorDeclaration(Loc loc, Loc endloc, long stc, Type type) {
                this(loc, endloc, stc, type, false);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CtorDeclaration() {}

            public CtorDeclaration copy() {
                CtorDeclaration that = new CtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class DtorDeclaration extends FuncDeclaration
        {
            // Erasure: __ctor<Loc, Loc>
            public  DtorDeclaration(Loc loc, Loc endloc) {
                super(loc, endloc, Id.dtor, 0L, null);
            }

            // Erasure: __ctor<Loc, Loc, long, Identifier>
            public  DtorDeclaration(Loc loc, Loc endloc, long stc, Identifier id) {
                super(loc, endloc, id, stc, null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DtorDeclaration() {}

            public DtorDeclaration copy() {
                DtorDeclaration that = new DtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class InvariantDeclaration extends FuncDeclaration
        {
            // Erasure: __ctor<Loc, Loc, long, Identifier, Statement>
            public  InvariantDeclaration(Loc loc, Loc endloc, long stc, Identifier id, Statement fbody) {
                super(loc, endloc, id != null ? id : Identifier.generateId(new BytePtr("__invariant")), stc, null);
                this.fbody = fbody;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InvariantDeclaration() {}

            public InvariantDeclaration copy() {
                InvariantDeclaration that = new InvariantDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class UnitTestDeclaration extends FuncDeclaration
        {
            public BytePtr codedoc = null;
            // Erasure: __ctor<Loc, Loc, long, Ptr>
            public  UnitTestDeclaration(Loc loc, Loc endloc, long stc, BytePtr codedoc) {
                super(loc, endloc, Identifier.generateIdWithLoc(new ByteSlice("__unittest"), loc), stc, null);
                this.codedoc = pcopy(codedoc);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UnitTestDeclaration() {}

            public UnitTestDeclaration copy() {
                UnitTestDeclaration that = new UnitTestDeclaration();
                that.codedoc = this.codedoc;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class NewDeclaration extends FuncDeclaration
        {
            public DArray<Parameter> parameters = null;
            public int varargs = 0;
            // Erasure: __ctor<Loc, Loc, long, Ptr, int>
            public  NewDeclaration(Loc loc, Loc endloc, long stc, DArray<Parameter> fparams, int varargs) {
                super(loc, endloc, Id.classNew, 1L | stc, null);
                this.parameters = pcopy(fparams);
                this.varargs = varargs;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NewDeclaration() {}

            public NewDeclaration copy() {
                NewDeclaration that = new NewDeclaration();
                that.parameters = this.parameters;
                that.varargs = this.varargs;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class DeleteDeclaration extends FuncDeclaration
        {
            public DArray<Parameter> parameters = null;
            // Erasure: __ctor<Loc, Loc, long, Ptr>
            public  DeleteDeclaration(Loc loc, Loc endloc, long stc, DArray<Parameter> fparams) {
                super(loc, endloc, Id.classDelete, 1L | stc, null);
                this.parameters = pcopy(fparams);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeleteDeclaration() {}

            public DeleteDeclaration copy() {
                DeleteDeclaration that = new DeleteDeclaration();
                that.parameters = this.parameters;
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class StaticCtorDeclaration extends FuncDeclaration
        {
            // Erasure: __ctor<Loc, Loc, long>
            public  StaticCtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc(new ByteSlice("_staticCtor"), loc), 1L | stc, null);
            }

            // Erasure: __ctor<Loc, Loc, Array, long>
            public  StaticCtorDeclaration(Loc loc, Loc endloc, ByteSlice name, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc(name, loc), 1L | stc, null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticCtorDeclaration() {}

            public StaticCtorDeclaration copy() {
                StaticCtorDeclaration that = new StaticCtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class StaticDtorDeclaration extends FuncDeclaration
        {
            // from template __ctor!()
            // Erasure: __ctor<Loc, Loc, long>
            public  StaticDtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc(new ByteSlice("__staticDtor"), loc), 1L | stc, null);
            }


            // Erasure: __ctor<Loc, Loc, Array, long>
            public  StaticDtorDeclaration(Loc loc, Loc endloc, ByteSlice name, long stc) {
                super(loc, endloc, Identifier.generateIdWithLoc(name, loc), 1L | stc, null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticDtorDeclaration() {}

            public StaticDtorDeclaration copy() {
                StaticDtorDeclaration that = new StaticDtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class SharedStaticCtorDeclaration extends StaticCtorDeclaration
        {
            // Erasure: __ctor<Loc, Loc, long>
            public  SharedStaticCtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc, new ByteSlice("_sharedStaticCtor"), stc);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SharedStaticCtorDeclaration() {}

            public SharedStaticCtorDeclaration copy() {
                SharedStaticCtorDeclaration that = new SharedStaticCtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class SharedStaticDtorDeclaration extends StaticDtorDeclaration
        {
            // Erasure: __ctor<Loc, Loc, long>
            public  SharedStaticDtorDeclaration(Loc loc, Loc endloc, long stc) {
                super(loc, endloc, new ByteSlice("_sharedStaticDtor"), stc);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SharedStaticDtorDeclaration() {}

            public SharedStaticDtorDeclaration copy() {
                SharedStaticDtorDeclaration that = new SharedStaticDtorDeclaration();
                that.fbody = this.fbody;
                that.frequires = this.frequires;
                that.fensures = this.fensures;
                that.endloc = this.endloc;
                that.storage_class = this.storage_class;
                that.type = this.type;
                that.inferRetType = this.inferRetType;
                that.fes = this.fes;
                that.overnext0 = this.overnext0;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class Package extends ScopeDsymbol
        {
            public int isPkgMod = 0;
            public int tag = 0;
            // Erasure: __ctor<Identifier>
            public  Package(Identifier ident) {
                super(ident);
                this.isPkgMod = PKG.unknown;
                this.tag = astbase.__ctorpackageTag++;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Package() {}

            public Package copy() {
                Package that = new Package();
                that.isPkgMod = this.isPkgMod;
                that.tag = this.tag;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class EnumDeclaration extends ScopeDsymbol
        {
            public Type type = null;
            public Type memtype = null;
            public Prot protection = new Prot();
            // Erasure: __ctor<Loc, Identifier, Type>
            public  EnumDeclaration(Loc loc, Identifier id, Type memtype) {
                super(id);
                this.loc.opAssign(loc.copy());
                this.type = new TypeEnum(this);
                this.memtype = memtype;
                this.protection.opAssign(new Prot(Prot.Kind.undefined, null));
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public EnumDeclaration() {}

            public EnumDeclaration copy() {
                EnumDeclaration that = new EnumDeclaration();
                that.type = this.type;
                that.memtype = this.memtype;
                that.protection = this.protection;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static abstract class AggregateDeclaration extends ScopeDsymbol
        {
            public Prot protection = new Prot();
            public int sizeok = 0;
            public Type type = null;
            // Erasure: __ctor<Loc, Identifier>
            public  AggregateDeclaration(Loc loc, Identifier id) {
                super(id);
                this.loc.opAssign(loc.copy());
                this.protection.opAssign(new Prot(Prot.Kind.public_, null));
                this.sizeok = Sizeok.none;
            }

            // Erasure: isAggregateDeclaration<>
            public  AggregateDeclaration isAggregateDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AggregateDeclaration() {}

            public abstract AggregateDeclaration copy();
        }
        public static class TemplateDeclaration extends ScopeDsymbol
        {
            public DArray<TemplateParameter> parameters = null;
            public DArray<TemplateParameter> origParameters = null;
            public Expression constraint = null;
            public boolean literal = false;
            public boolean ismixin = false;
            public boolean isstatic = false;
            public Prot protection = new Prot();
            public Dsymbol onemember = null;
            // Erasure: __ctor<Loc, Identifier, Ptr, Expression, Ptr, boolean, boolean>
            public  TemplateDeclaration(Loc loc, Identifier id, DArray<TemplateParameter> parameters, Expression constraint, DArray<Dsymbol> decldefs, boolean ismixin, boolean literal) {
                super(id);
                this.loc.opAssign(loc.copy());
                this.parameters = pcopy(parameters);
                this.origParameters = pcopy(parameters);
                this.members = pcopy(decldefs);
                this.literal = literal;
                this.ismixin = ismixin;
                this.isstatic = true;
                this.protection.opAssign(new Prot(Prot.Kind.undefined, null));
                if ((this.members != null) && (this.ident != null))
                {
                    Ref<Dsymbol> s = ref(null);
                    if (Dsymbol.oneMembers(this.members, ptr(s), this.ident) && (s.value != null))
                    {
                        this.onemember = s.value;
                        s.value.parent = this;
                    }
                }
            }

            // defaulted all parameters starting with #7
            public  TemplateDeclaration(Loc loc, Identifier id, DArray<TemplateParameter> parameters, Expression constraint, DArray<Dsymbol> decldefs, boolean ismixin) {
                this(loc, id, parameters, constraint, decldefs, ismixin, false);
            }

            // defaulted all parameters starting with #6
            public  TemplateDeclaration(Loc loc, Identifier id, DArray<TemplateParameter> parameters, Expression constraint, DArray<Dsymbol> decldefs) {
                this(loc, id, parameters, constraint, decldefs, false, false);
            }

            // Erasure: isOverloadable<>
            public  boolean isOverloadable() {
                return true;
            }

            // Erasure: isTemplateDeclaration<>
            public  TemplateDeclaration isTemplateDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateDeclaration() {}

            public TemplateDeclaration copy() {
                TemplateDeclaration that = new TemplateDeclaration();
                that.parameters = this.parameters;
                that.origParameters = this.origParameters;
                that.constraint = this.constraint;
                that.literal = this.literal;
                that.ismixin = this.ismixin;
                that.isstatic = this.isstatic;
                that.protection = this.protection;
                that.onemember = this.onemember;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class TemplateInstance extends ScopeDsymbol
        {
            public Identifier name = null;
            public DArray<RootObject> tiargs = null;
            public Dsymbol tempdecl = null;
            public boolean semantictiargsdone = false;
            public boolean havetempdecl = false;
            public TemplateInstance inst = null;
            // Erasure: __ctor<Loc, Identifier, Ptr>
            public  TemplateInstance(Loc loc, Identifier ident, DArray<RootObject> tiargs) {
                super(null);
                this.loc.opAssign(loc.copy());
                this.name = ident;
                this.tiargs = pcopy(tiargs);
            }

            // Erasure: __ctor<Loc, TemplateDeclaration, Ptr>
            public  TemplateInstance(Loc loc, TemplateDeclaration td, DArray<RootObject> tiargs) {
                super(null);
                this.loc.opAssign(loc.copy());
                this.name = td.ident;
                this.tempdecl = td;
                this.semantictiargsdone = true;
                this.havetempdecl = true;
            }

            // Erasure: isTemplateInstance<>
            public  TemplateInstance isTemplateInstance() {
                return this;
            }

            // Erasure: arraySyntaxCopy<Ptr>
            public  DArray<RootObject> arraySyntaxCopy(DArray<RootObject> objs) {
                DArray<RootObject> a = null;
                if (objs != null)
                {
                    a = pcopy(new DArray<RootObject>());
                    (a).setDim((objs).length);
                    {
                        int i = 0;
                        for (; (i < (objs).length);i++) {
                            a.set(i, this.objectSyntaxCopy((objs).get(i)));
                        }
                    }
                }
                return a;
            }

            // Erasure: objectSyntaxCopy<RootObject>
            public  RootObject objectSyntaxCopy(RootObject o) {
                if (o == null)
                {
                    return null;
                }
                {
                    Type t = isType(o);
                    if ((t) != null)
                    {
                        return t.syntaxCopy();
                    }
                }
                {
                    Expression e = isExpression(o);
                    if ((e) != null)
                    {
                        return e.syntaxCopy();
                    }
                }
                return o;
            }

            // Erasure: syntaxCopy<Dsymbol>
            public  Dsymbol syntaxCopy(Dsymbol s) {
                TemplateInstance ti = s != null ? ((TemplateInstance)s) : new TemplateInstance(this.loc, this.name, null);
                ti.tiargs = pcopy(this.arraySyntaxCopy(this.tiargs));
                TemplateDeclaration td = null;
                if ((this.inst != null) && (this.tempdecl != null) && ((td = this.tempdecl.isTemplateDeclaration()) != null))
                {
                    td.syntaxCopy(ti);
                }
                else
                {
                    this.syntaxCopy(ti);
                }
                return ti;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateInstance() {}

            public TemplateInstance copy() {
                TemplateInstance that = new TemplateInstance();
                that.name = this.name;
                that.tiargs = this.tiargs;
                that.tempdecl = this.tempdecl;
                that.semantictiargsdone = this.semantictiargsdone;
                that.havetempdecl = this.havetempdecl;
                that.inst = this.inst;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class Nspace extends ScopeDsymbol
        {
            public Expression identExp = null;
            // Erasure: __ctor<Loc, Identifier, Expression, Ptr>
            public  Nspace(Loc loc, Identifier ident, Expression identExp, DArray<Dsymbol> members) {
                super(ident);
                this.loc.opAssign(loc.copy());
                this.members = pcopy(members);
                this.identExp = identExp;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Nspace() {}

            public Nspace copy() {
                Nspace that = new Nspace();
                that.identExp = this.identExp;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class CompileDeclaration extends AttribDeclaration
        {
            public DArray<Expression> exps = null;
            // Erasure: __ctor<Loc, Ptr>
            public  CompileDeclaration(Loc loc, DArray<Expression> exps) {
                super(null);
                this.loc.opAssign(loc.copy());
                this.exps = pcopy(exps);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompileDeclaration() {}

            public CompileDeclaration copy() {
                CompileDeclaration that = new CompileDeclaration();
                that.exps = this.exps;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class UserAttributeDeclaration extends AttribDeclaration
        {
            public DArray<Expression> atts = null;
            // Erasure: __ctor<Ptr, Ptr>
            public  UserAttributeDeclaration(DArray<Expression> atts, DArray<Dsymbol> decl) {
                super(decl);
                this.atts = pcopy(atts);
            }

            // Erasure: concat<Ptr, Ptr>
            public static DArray<Expression> concat(DArray<Expression> udas1, DArray<Expression> udas2) {
                DArray<Expression> udas = null;
                if ((udas1 == null) || ((udas1).length == 0))
                {
                    udas = pcopy(udas2);
                }
                else if ((udas2 == null) || ((udas2).length == 0))
                {
                    udas = pcopy(udas1);
                }
                else
                {
                    udas = pcopy(new DArray<Expression>(2));
                    udas.set(0, new TupleExp(Loc.initial, udas1));
                    udas.set(1, new TupleExp(Loc.initial, udas2));
                }
                return udas;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UserAttributeDeclaration() {}

            public UserAttributeDeclaration copy() {
                UserAttributeDeclaration that = new UserAttributeDeclaration();
                that.atts = this.atts;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class LinkDeclaration extends AttribDeclaration
        {
            public int linkage = 0;
            // Erasure: __ctor<int, Ptr>
            public  LinkDeclaration(int p, DArray<Dsymbol> decl) {
                super(decl);
                this.linkage = p;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LinkDeclaration() {}

            public LinkDeclaration copy() {
                LinkDeclaration that = new LinkDeclaration();
                that.linkage = this.linkage;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class AnonDeclaration extends AttribDeclaration
        {
            public boolean isunion = false;
            // Erasure: __ctor<Loc, boolean, Ptr>
            public  AnonDeclaration(Loc loc, boolean isunion, DArray<Dsymbol> decl) {
                super(decl);
                this.loc.opAssign(loc.copy());
                this.isunion = isunion;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AnonDeclaration() {}

            public AnonDeclaration copy() {
                AnonDeclaration that = new AnonDeclaration();
                that.isunion = this.isunion;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class AlignDeclaration extends AttribDeclaration
        {
            public Expression ealign = null;
            // Erasure: __ctor<Loc, Expression, Ptr>
            public  AlignDeclaration(Loc loc, Expression ealign, DArray<Dsymbol> decl) {
                super(decl);
                this.loc.opAssign(loc.copy());
                this.ealign = ealign;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AlignDeclaration() {}

            public AlignDeclaration copy() {
                AlignDeclaration that = new AlignDeclaration();
                that.ealign = this.ealign;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class CPPMangleDeclaration extends AttribDeclaration
        {
            public int cppmangle = 0;
            // Erasure: __ctor<int, Ptr>
            public  CPPMangleDeclaration(int p, DArray<Dsymbol> decl) {
                super(decl);
                this.cppmangle = p;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CPPMangleDeclaration() {}

            public CPPMangleDeclaration copy() {
                CPPMangleDeclaration that = new CPPMangleDeclaration();
                that.cppmangle = this.cppmangle;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class CPPNamespaceDeclaration extends AttribDeclaration
        {
            public Expression exp = null;
            // Erasure: __ctor<Identifier, Ptr>
            public  CPPNamespaceDeclaration(Identifier ident, DArray<Dsymbol> decl) {
                super(decl);
                this.ident = ident;
            }

            // Erasure: __ctor<Expression, Ptr>
            public  CPPNamespaceDeclaration(Expression exp, DArray<Dsymbol> decl) {
                super(decl);
                this.exp = exp;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CPPNamespaceDeclaration() {}

            public CPPNamespaceDeclaration copy() {
                CPPNamespaceDeclaration that = new CPPNamespaceDeclaration();
                that.exp = this.exp;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class ProtDeclaration extends AttribDeclaration
        {
            public Prot protection = new Prot();
            public DArray<Identifier> pkg_identifiers = null;
            // Erasure: __ctor<Loc, Prot, Ptr>
            public  ProtDeclaration(Loc loc, Prot p, DArray<Dsymbol> decl) {
                super(decl);
                this.loc.opAssign(loc.copy());
                this.protection.opAssign(p.copy());
            }

            // Erasure: __ctor<Loc, Ptr, Ptr>
            public  ProtDeclaration(Loc loc, DArray<Identifier> pkg_identifiers, DArray<Dsymbol> decl) {
                super(decl);
                this.loc.opAssign(loc.copy());
                this.protection.kind = Prot.Kind.package_;
                this.protection.pkg = null;
                this.pkg_identifiers = pcopy(pkg_identifiers);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ProtDeclaration() {}

            public ProtDeclaration copy() {
                ProtDeclaration that = new ProtDeclaration();
                that.protection = this.protection;
                that.pkg_identifiers = this.pkg_identifiers;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class PragmaDeclaration extends AttribDeclaration
        {
            public DArray<Expression> args = null;
            // Erasure: __ctor<Loc, Identifier, Ptr, Ptr>
            public  PragmaDeclaration(Loc loc, Identifier ident, DArray<Expression> args, DArray<Dsymbol> decl) {
                super(decl);
                this.loc.opAssign(loc.copy());
                this.ident = ident;
                this.args = pcopy(args);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PragmaDeclaration() {}

            public PragmaDeclaration copy() {
                PragmaDeclaration that = new PragmaDeclaration();
                that.args = this.args;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class StorageClassDeclaration extends AttribDeclaration
        {
            public long stc = 0L;
            // Erasure: __ctor<long, Ptr>
            public  StorageClassDeclaration(long stc, DArray<Dsymbol> decl) {
                super(decl);
                this.stc = stc;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StorageClassDeclaration() {}

            public StorageClassDeclaration copy() {
                StorageClassDeclaration that = new StorageClassDeclaration();
                that.stc = this.stc;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class ConditionalDeclaration extends AttribDeclaration
        {
            public Condition condition = null;
            public DArray<Dsymbol> elsedecl = null;
            // Erasure: __ctor<Condition, Ptr, Ptr>
            public  ConditionalDeclaration(Condition condition, DArray<Dsymbol> decl, DArray<Dsymbol> elsedecl) {
                super(decl);
                this.condition = condition;
                this.elsedecl = pcopy(elsedecl);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ConditionalDeclaration() {}

            public ConditionalDeclaration copy() {
                ConditionalDeclaration that = new ConditionalDeclaration();
                that.condition = this.condition;
                that.elsedecl = this.elsedecl;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class DeprecatedDeclaration extends StorageClassDeclaration
        {
            public Expression msg = null;
            // Erasure: __ctor<Expression, Ptr>
            public  DeprecatedDeclaration(Expression msg, DArray<Dsymbol> decl) {
                super(1024L, decl);
                this.msg = msg;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeprecatedDeclaration() {}

            public DeprecatedDeclaration copy() {
                DeprecatedDeclaration that = new DeprecatedDeclaration();
                that.msg = this.msg;
                that.stc = this.stc;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class StaticIfDeclaration extends ConditionalDeclaration
        {
            // Erasure: __ctor<Condition, Ptr, Ptr>
            public  StaticIfDeclaration(Condition condition, DArray<Dsymbol> decl, DArray<Dsymbol> elsedecl) {
                super(condition, decl, elsedecl);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticIfDeclaration() {}

            public StaticIfDeclaration copy() {
                StaticIfDeclaration that = new StaticIfDeclaration();
                that.condition = this.condition;
                that.elsedecl = this.elsedecl;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class StaticForeachDeclaration extends AttribDeclaration
        {
            public StaticForeach sfe = null;
            // Erasure: __ctor<StaticForeach, Ptr>
            public  StaticForeachDeclaration(StaticForeach sfe, DArray<Dsymbol> decl) {
                super(decl);
                this.sfe = sfe;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticForeachDeclaration() {}

            public StaticForeachDeclaration copy() {
                StaticForeachDeclaration that = new StaticForeachDeclaration();
                that.sfe = this.sfe;
                that.decl = this.decl;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class EnumMember extends VarDeclaration
        {
            public Expression origValue = null;
            public Type origType = null;
            // Erasure: value<>
            public  Expression value() {
                return (((ExpInitializer)this._init)).exp;
            }

            // Erasure: __ctor<Loc, Identifier, Expression, Type>
            public  EnumMember(Loc loc, Identifier id, Expression value, Type origType) {
                super(loc, null, id != null ? id : Id.empty, new ExpInitializer(loc, value), 0L);
                this.origValue = value;
                this.origType = origType;
            }

            // Erasure: __ctor<Loc, Identifier, Expression, Type, long, UserAttributeDeclaration, DeprecatedDeclaration>
            public  EnumMember(Loc loc, Identifier id, Expression value, Type memtype, long stc, UserAttributeDeclaration uad, DeprecatedDeclaration dd) {
                this(loc, id, value, memtype);
                this.storage_class = stc;
                this.userAttribDecl = uad;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public EnumMember() {}

            public EnumMember copy() {
                EnumMember that = new EnumMember();
                that.origValue = this.origValue;
                that.origType = this.origType;
                that.type = this.type;
                that._init = this._init;
                that.storage_class = this.storage_class;
                that.ctfeAdrOnStack = this.ctfeAdrOnStack;
                that.sequenceNumber = this.sequenceNumber;
                that.storage_class = this.storage_class;
                that.protection = this.protection;
                that.linkage = this.linkage;
                that.type = this.type;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class Module extends Package
        {
            public static AggregateDeclaration moduleinfo = null;
            public FileName srcfile = new FileName();
            public BytePtr arg = null;
            // Erasure: __ctor<Ptr, Identifier, int, int>
            public  Module(BytePtr filename, Identifier ident, int doDocComment, int doHdrGen) {
                super(ident);
                this.arg = pcopy(filename);
                this.srcfile = new FileName(FileName.defaultExt(toDString(filename), toByteSlice(global.mars_ext)));
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Module() {}

            public Module copy() {
                Module that = new Module();
                that.srcfile = this.srcfile;
                that.arg = this.arg;
                that.isPkgMod = this.isPkgMod;
                that.tag = this.tag;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class StructDeclaration extends AggregateDeclaration
        {
            public int zeroInit = 0;
            public int ispod = 0;
            // Erasure: __ctor<Loc, Identifier, boolean>
            public  StructDeclaration(Loc loc, Identifier id, boolean inObject) {
                super(loc, id);
                this.zeroInit = 0;
                this.ispod = StructPOD.fwd;
                this.type = new TypeStruct(this);
                if (inObject)
                {
                    if ((pequals(id, Id.ModuleInfo)) && (Module.moduleinfo == null))
                    {
                        Module.moduleinfo = this;
                    }
                }
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StructDeclaration() {}

            public StructDeclaration copy() {
                StructDeclaration that = new StructDeclaration();
                that.zeroInit = this.zeroInit;
                that.ispod = this.ispod;
                that.protection = this.protection;
                that.sizeok = this.sizeok;
                that.type = this.type;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class UnionDeclaration extends StructDeclaration
        {
            // Erasure: __ctor<Loc, Identifier>
            public  UnionDeclaration(Loc loc, Identifier id) {
                super(loc, id, false);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UnionDeclaration() {}

            public UnionDeclaration copy() {
                UnionDeclaration that = new UnionDeclaration();
                that.zeroInit = this.zeroInit;
                that.ispod = this.ispod;
                that.protection = this.protection;
                that.sizeok = this.sizeok;
                that.type = this.type;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class ClassDeclaration extends AggregateDeclaration
        {
            public static ClassDeclaration object = null;
            public static ClassDeclaration throwable = null;
            public static ClassDeclaration exception = null;
            public static ClassDeclaration errorException = null;
            public static ClassDeclaration cpp_type_info_ptr = null;
            public DArray<Ptr<BaseClass>> baseclasses = null;
            public int baseok = 0;
            // Erasure: __ctor<Loc, Identifier, Ptr, Ptr, boolean>
            public  ClassDeclaration(Loc loc, Identifier id, DArray<Ptr<BaseClass>> baseclasses, DArray<Dsymbol> members, boolean inObject) {
                super(loc, id == null ? Identifier.generateId(new BytePtr("__anonclass")) : id );
                if (baseclasses != null)
                {
                    this.baseclasses = pcopy(baseclasses);
                }
                else
                {
                    this.baseclasses = pcopy(new DArray<Ptr<BaseClass>>());
                }
                this.members = pcopy(members);
                this.type = new TypeClass(this);
                if (id != null)
                {
                    if ((pequals(id, Id.__sizeof)) || (pequals(id, Id.__xalignof)) || (pequals(id, Id._mangleof)))
                    {
                        this.error(new BytePtr("illegal class name"));
                    }
                    if (((id.toChars().get(0) & 0xFF) == 84))
                    {
                        if ((pequals(id, Id.TypeInfo)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.dtypeinfo = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Class)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoclass = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Interface)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfointerface = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Struct)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfostruct = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Pointer)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfopointer = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Array)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoarray = this;
                        }
                        if ((pequals(id, Id.TypeInfo_StaticArray)))
                        {
                            Type.typeinfostaticarray = this;
                        }
                        if ((pequals(id, Id.TypeInfo_AssociativeArray)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoassociativearray = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Enum)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoenum = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Function)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfofunction = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Delegate)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfodelegate = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Tuple)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfotypelist = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Const)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoconst = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Invariant)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoinvariant = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Shared)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfoshared = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Wild)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfowild = this;
                        }
                        if ((pequals(id, Id.TypeInfo_Vector)))
                        {
                            if (!inObject)
                            {
                                this.error(new BytePtr("%s"), astbase.__ctormsg);
                            }
                            Type.typeinfovector = this;
                        }
                    }
                    if ((pequals(id, Id.Object)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        }
                        object = this;
                    }
                    if ((pequals(id, Id.Throwable)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        }
                        throwable = this;
                    }
                    if ((pequals(id, Id.Exception)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        }
                        exception = this;
                    }
                    if ((pequals(id, Id.Error)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        }
                        errorException = this;
                    }
                    if ((pequals(id, Id.cpp_type_info_ptr)))
                    {
                        if (!inObject)
                        {
                            this.error(new BytePtr("%s"), astbase.__ctormsg);
                        }
                        cpp_type_info_ptr = this;
                    }
                }
                this.baseok = Baseok.none;
            }

            // Erasure: isClassDeclaration<>
            public  ClassDeclaration isClassDeclaration() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ClassDeclaration() {}

            public ClassDeclaration copy() {
                ClassDeclaration that = new ClassDeclaration();
                that.baseclasses = this.baseclasses;
                that.baseok = this.baseok;
                that.protection = this.protection;
                that.sizeok = this.sizeok;
                that.type = this.type;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class InterfaceDeclaration extends ClassDeclaration
        {
            // Erasure: __ctor<Loc, Identifier, Ptr>
            public  InterfaceDeclaration(Loc loc, Identifier id, DArray<Ptr<BaseClass>> baseclasses) {
                super(loc, id, baseclasses, null, false);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InterfaceDeclaration() {}

            public InterfaceDeclaration copy() {
                InterfaceDeclaration that = new InterfaceDeclaration();
                that.baseclasses = this.baseclasses;
                that.baseok = this.baseok;
                that.protection = this.protection;
                that.sizeok = this.sizeok;
                that.type = this.type;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class TemplateMixin extends TemplateInstance
        {
            public TypeQualified tqual = null;
            // Erasure: __ctor<Loc, Identifier, TypeQualified, Ptr>
            public  TemplateMixin(Loc loc, Identifier ident, TypeQualified tqual, DArray<RootObject> tiargs) {
                super(loc, tqual.idents.length != 0 ? ((Identifier)tqual.idents.get(tqual.idents.length - 1)) : (((TypeIdentifier)tqual)).ident, tiargs != null ? tiargs : new DArray<RootObject>());
                this.ident = ident;
                this.tqual = tqual;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateMixin() {}

            public TemplateMixin copy() {
                TemplateMixin that = new TemplateMixin();
                that.tqual = this.tqual;
                that.name = this.name;
                that.tiargs = this.tiargs;
                that.tempdecl = this.tempdecl;
                that.semantictiargsdone = this.semantictiargsdone;
                that.havetempdecl = this.havetempdecl;
                that.inst = this.inst;
                that.members = this.members;
                that.loc = this.loc;
                that.ident = this.ident;
                that.ddocUnittest = this.ddocUnittest;
                that.userAttribDecl = this.userAttribDecl;
                that.parent = this.parent;
                that.comment = this.comment;
                return that;
            }
        }
        public static class ParameterList
        {
            public DArray<Parameter> parameters = null;
            public int varargs = VarArg.none;
            public ParameterList(){ }
            public ParameterList copy(){
                ParameterList r = new ParameterList();
                r.parameters = parameters;
                r.varargs = varargs;
                return r;
            }
            public ParameterList(DArray<Parameter> parameters, int varargs) {
                this.parameters = parameters;
                this.varargs = varargs;
            }

            public ParameterList opAssign(ParameterList that) {
                this.parameters = that.parameters;
                this.varargs = that.varargs;
                return this;
            }
        }
        public static class Parameter extends ASTNode
        {
            public long storageClass = 0L;
            public Type type = null;
            public Identifier ident = null;
            public Expression defaultArg = null;
            public UserAttributeDeclaration userAttribDecl = null;
            // Erasure: __ctor<long, Type, Identifier, Expression, UserAttributeDeclaration>
            public  Parameter(long storageClass, Type type, Identifier ident, Expression defaultArg, UserAttributeDeclaration userAttribDecl) {
                super();
                this.storageClass = storageClass;
                this.type = type;
                this.ident = ident;
                this.defaultArg = defaultArg;
                this.userAttribDecl = userAttribDecl;
            }

            // Erasure: dim<Ptr>
            public static int dim(DArray<Parameter> parameters) {
                Ref<Integer> nargs = ref(0);
                Function2<Integer,Parameter,Integer> dimDg = new Function2<Integer,Parameter,Integer>() {
                    public Integer invoke(Integer n, Parameter p) {
                     {
                        nargs.value += 1;
                        return 0;
                    }}

                };
                _foreach(parameters, dimDg, null);
                return nargs.value;
            }

            // Erasure: getNth<Ptr, int, Ptr>
            public static Parameter getNth(DArray<Parameter> parameters, int nth, Ptr<Integer> pn) {
                Ref<Parameter> param = ref(null);
                Function2<Integer,Parameter,Integer> getNthParamDg = new Function2<Integer,Parameter,Integer>() {
                    public Integer invoke(Integer n, Parameter p) {
                     {
                        if ((n == nth))
                        {
                            param.value = p;
                            return 1;
                        }
                        return 0;
                    }}

                };
                int res = _foreach(parameters, getNthParamDg, null);
                return res != 0 ? param.value : null;
            }

            // defaulted all parameters starting with #3
            public static Parameter getNth(DArray<Parameter> parameters, int nth) {
                return getNth(parameters, nth, (Ptr<Integer>)null);
            }

            // Erasure: _foreach<Ptr, Function2, Ptr>
            public static int _foreach(DArray<Parameter> parameters, Function2<Integer,Parameter,Integer> dg, Ptr<Integer> pn) {
                assert(dg != null);
                if (parameters == null)
                {
                    return 0;
                }
                Ref<Integer> n = ref(pn != null ? pn.get() : 0);
                int result = 0;
                {
                    int __key146 = 0;
                    int __limit147 = (parameters).length;
                    for (; (__key146 < __limit147);__key146 += 1) {
                        int i = __key146;
                        Parameter p = (parameters).get(i);
                        Type t = p.type.toBasetype();
                        if (((t.ty & 0xFF) == ENUMTY.Ttuple))
                        {
                            TypeTuple tu = ((TypeTuple)t);
                            result = _foreach(tu.arguments, dg, ptr(n));
                        }
                        else
                        {
                            result = dg.invoke(n.value++, p);
                        }
                        if (result != 0)
                        {
                            break;
                        }
                    }
                }
                if (pn != null)
                {
                    pn.set(0, n.value);
                }
                return result;
            }

            // defaulted all parameters starting with #3
            public static int _foreach(DArray<Parameter> parameters, Function2<Integer,Parameter,Integer> dg) {
                return _foreach(parameters, dg, (Ptr<Integer>)null);
            }

            // Erasure: syntaxCopy<>
            public  Parameter syntaxCopy() {
                return new Parameter(this.storageClass, this.type != null ? this.type.syntaxCopy() : null, this.ident, this.defaultArg != null ? this.defaultArg.syntaxCopy() : null, this.userAttribDecl != null ? ((UserAttributeDeclaration)this.userAttribDecl.syntaxCopy(null)) : null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }

            // Erasure: arraySyntaxCopy<Ptr>
            public static DArray<Parameter> arraySyntaxCopy(DArray<Parameter> parameters) {
                DArray<Parameter> params = null;
                if (parameters != null)
                {
                    params = pcopy(new DArray<Parameter>());
                    (params).setDim((parameters).length);
                    {
                        int i = 0;
                        for (; (i < (params).length);i++) {
                            params.set(i, (parameters).get(i).syntaxCopy());
                        }
                    }
                }
                return params;
            }


            public Parameter() {}

            public Parameter copy() {
                Parameter that = new Parameter();
                that.storageClass = this.storageClass;
                that.type = this.type;
                that.ident = this.ident;
                that.defaultArg = this.defaultArg;
                that.userAttribDecl = this.userAttribDecl;
                return that;
            }
        }
        public static abstract class Statement extends ASTNode
        {
            public Loc loc = new Loc();
            // Erasure: __ctor<Loc>
            public  Statement(Loc loc) {
                super();
                this.loc.opAssign(loc.copy());
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Statement() {}

            public abstract Statement copy();
        }
        public static class ImportStatement extends Statement
        {
            public DArray<Dsymbol> imports = null;
            // Erasure: __ctor<Loc, Ptr>
            public  ImportStatement(Loc loc, DArray<Dsymbol> imports) {
                super(loc);
                this.imports = pcopy(imports);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class ScopeStatement extends Statement
        {
            public Statement statement = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, Statement, Loc>
            public  ScopeStatement(Loc loc, Statement s, Loc endloc) {
                super(loc);
                this.statement = s;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class ReturnStatement extends Statement
        {
            public Expression exp = null;
            // Erasure: __ctor<Loc, Expression>
            public  ReturnStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            // Erasure: isReturnStatement<>
            public  ReturnStatement isReturnStatement() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ReturnStatement() {}

            public ReturnStatement copy() {
                ReturnStatement that = new ReturnStatement();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class LabelStatement extends Statement
        {
            public Identifier ident = null;
            public Statement statement = null;
            // Erasure: __ctor<Loc, Identifier, Statement>
            public  LabelStatement(Loc loc, Identifier ident, Statement statement) {
                super(loc);
                this.ident = ident;
                this.statement = statement;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LabelStatement() {}

            public LabelStatement copy() {
                LabelStatement that = new LabelStatement();
                that.ident = this.ident;
                that.statement = this.statement;
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class CompileStatement extends Statement
        {
            public DArray<Expression> exps = null;
            // Erasure: __ctor<Loc, Ptr>
            public  CompileStatement(Loc loc, DArray<Expression> exps) {
                super(loc);
                this.exps = pcopy(exps);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class WhileStatement extends Statement
        {
            public Expression condition = null;
            public Statement _body = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, Expression, Statement, Loc>
            public  WhileStatement(Loc loc, Expression c, Statement b, Loc endloc) {
                super(loc);
                this.condition = c;
                this._body = b;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class ForStatement extends Statement
        {
            public Statement _init = null;
            public Expression condition = null;
            public Expression increment = null;
            public Statement _body = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, Statement, Expression, Expression, Statement, Loc>
            public  ForStatement(Loc loc, Statement _init, Expression condition, Expression increment, Statement _body, Loc endloc) {
                super(loc);
                this._init = _init;
                this.condition = condition;
                this.increment = increment;
                this._body = _body;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
                that.loc = this.loc;
                return that;
            }
        }
        public static class DoStatement extends Statement
        {
            public Statement _body = null;
            public Expression condition = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, Statement, Expression, Loc>
            public  DoStatement(Loc loc, Statement b, Expression c, Loc endloc) {
                super(loc);
                this._body = b;
                this.condition = c;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class ForeachRangeStatement extends Statement
        {
            public byte op = 0;
            public Parameter prm = null;
            public Expression lwr = null;
            public Expression upr = null;
            public Statement _body = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, byte, Parameter, Expression, Expression, Statement, Loc>
            public  ForeachRangeStatement(Loc loc, byte op, Parameter prm, Expression lwr, Expression upr, Statement _body, Loc endloc) {
                super(loc);
                this.op = op;
                this.prm = prm;
                this.lwr = lwr;
                this.upr = upr;
                this._body = _body;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
                that.loc = this.loc;
                return that;
            }
        }
        public static class ForeachStatement extends Statement
        {
            public byte op = 0;
            public DArray<Parameter> parameters = null;
            public Expression aggr = null;
            public Statement _body = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, byte, Ptr, Expression, Statement, Loc>
            public  ForeachStatement(Loc loc, byte op, DArray<Parameter> parameters, Expression aggr, Statement _body, Loc endloc) {
                super(loc);
                this.op = op;
                this.parameters = pcopy(parameters);
                this.aggr = aggr;
                this._body = _body;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
                that.loc = this.loc;
                return that;
            }
        }
        public static class IfStatement extends Statement
        {
            public Parameter prm = null;
            public Expression condition = null;
            public Statement ifbody = null;
            public Statement elsebody = null;
            public VarDeclaration match = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, Parameter, Expression, Statement, Statement, Loc>
            public  IfStatement(Loc loc, Parameter prm, Expression condition, Statement ifbody, Statement elsebody, Loc endloc) {
                super(loc);
                this.prm = prm;
                this.condition = condition;
                this.ifbody = ifbody;
                this.elsebody = elsebody;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class SwitchStatement extends Statement
        {
            public Expression condition = null;
            public Statement _body = null;
            public boolean isFinal = false;
            // Erasure: __ctor<Loc, Expression, Statement, boolean>
            public  SwitchStatement(Loc loc, Expression c, Statement b, boolean isFinal) {
                super(loc);
                this.condition = c;
                this._body = b;
                this.isFinal = isFinal;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SwitchStatement() {}

            public SwitchStatement copy() {
                SwitchStatement that = new SwitchStatement();
                that.condition = this.condition;
                that._body = this._body;
                that.isFinal = this.isFinal;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CaseRangeStatement extends Statement
        {
            public Expression first = null;
            public Expression last = null;
            public Statement statement = null;
            // Erasure: __ctor<Loc, Expression, Expression, Statement>
            public  CaseRangeStatement(Loc loc, Expression first, Expression last, Statement s) {
                super(loc);
                this.first = first;
                this.last = last;
                this.statement = s;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class CaseStatement extends Statement
        {
            public Expression exp = null;
            public Statement statement = null;
            // Erasure: __ctor<Loc, Expression, Statement>
            public  CaseStatement(Loc loc, Expression exp, Statement s) {
                super(loc);
                this.exp = exp;
                this.statement = s;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CaseStatement() {}

            public CaseStatement copy() {
                CaseStatement that = new CaseStatement();
                that.exp = this.exp;
                that.statement = this.statement;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DefaultStatement extends Statement
        {
            public Statement statement = null;
            // Erasure: __ctor<Loc, Statement>
            public  DefaultStatement(Loc loc, Statement s) {
                super(loc);
                this.statement = s;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DefaultStatement() {}

            public DefaultStatement copy() {
                DefaultStatement that = new DefaultStatement();
                that.statement = this.statement;
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class GotoDefaultStatement extends Statement
        {
            // Erasure: __ctor<Loc>
            public  GotoDefaultStatement(Loc loc) {
                super(loc);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GotoDefaultStatement() {}

            public GotoDefaultStatement copy() {
                GotoDefaultStatement that = new GotoDefaultStatement();
                that.loc = this.loc;
                return that;
            }
        }
        public static class GotoCaseStatement extends Statement
        {
            public Expression exp = null;
            // Erasure: __ctor<Loc, Expression>
            public  GotoCaseStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GotoCaseStatement() {}

            public GotoCaseStatement copy() {
                GotoCaseStatement that = new GotoCaseStatement();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class GotoStatement extends Statement
        {
            public Identifier ident = null;
            // Erasure: __ctor<Loc, Identifier>
            public  GotoStatement(Loc loc, Identifier ident) {
                super(loc);
                this.ident = ident;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GotoStatement() {}

            public GotoStatement copy() {
                GotoStatement that = new GotoStatement();
                that.ident = this.ident;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SynchronizedStatement extends Statement
        {
            public Expression exp = null;
            public Statement _body = null;
            // Erasure: __ctor<Loc, Expression, Statement>
            public  SynchronizedStatement(Loc loc, Expression exp, Statement _body) {
                super(loc);
                this.exp = exp;
                this._body = _body;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
            public Statement _body = null;
            public Loc endloc = new Loc();
            // Erasure: __ctor<Loc, Expression, Statement, Loc>
            public  WithStatement(Loc loc, Expression exp, Statement _body, Loc endloc) {
                super(loc);
                this.exp = exp;
                this._body = _body;
                this.endloc.opAssign(endloc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public WithStatement() {}

            public WithStatement copy() {
                WithStatement that = new WithStatement();
                that.exp = this.exp;
                that._body = this._body;
                that.endloc = this.endloc;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TryCatchStatement extends Statement
        {
            public Statement _body = null;
            public DArray<Catch> catches = null;
            // Erasure: __ctor<Loc, Statement, Ptr>
            public  TryCatchStatement(Loc loc, Statement _body, DArray<Catch> catches) {
                super(loc);
                this._body = _body;
                this.catches = pcopy(catches);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class TryFinallyStatement extends Statement
        {
            public Statement _body = null;
            public Statement finalbody = null;
            // Erasure: __ctor<Loc, Statement, Statement>
            public  TryFinallyStatement(Loc loc, Statement _body, Statement finalbody) {
                super(loc);
                this._body = _body;
                this.finalbody = finalbody;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TryFinallyStatement() {}

            public TryFinallyStatement copy() {
                TryFinallyStatement that = new TryFinallyStatement();
                that._body = this._body;
                that.finalbody = this.finalbody;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ThrowStatement extends Statement
        {
            public Expression exp = null;
            // Erasure: __ctor<Loc, Expression>
            public  ThrowStatement(Loc loc, Expression exp) {
                super(loc);
                this.exp = exp;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ThrowStatement() {}

            public ThrowStatement copy() {
                ThrowStatement that = new ThrowStatement();
                that.exp = this.exp;
                that.loc = this.loc;
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
            // Erasure: __ctor<Loc, Ptr>
            public  InlineAsmStatement(Loc loc, Ptr<Token> tokens) {
                super(loc, tokens);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InlineAsmStatement() {}

            public InlineAsmStatement copy() {
                InlineAsmStatement that = new InlineAsmStatement();
                that.tokens = this.tokens;
                that.loc = this.loc;
                return that;
            }
        }
        public static class GccAsmStatement extends AsmStatement
        {
            // Erasure: __ctor<Loc, Ptr>
            public  GccAsmStatement(Loc loc, Ptr<Token> tokens) {
                super(loc, tokens);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public GccAsmStatement() {}

            public GccAsmStatement copy() {
                GccAsmStatement that = new GccAsmStatement();
                that.tokens = this.tokens;
                that.loc = this.loc;
                return that;
            }
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

            // Erasure: isExpStatement<>
            public  ExpStatement isExpStatement() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
                    Slice<Statement> __r148 = sts.copy();
                    int __key149 = 0;
                    for (; (__key149 < __r148.getLength());__key149 += 1) {
                        Statement s = __r148.get(__key149);
                        (this.statements).push(s);
                    }
                }
            }

            // Erasure: isCompoundStatement<>
            public  CompoundStatement isCompoundStatement() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class CompoundAsmStatement extends CompoundStatement
        {
            public long stc = 0L;
            // Erasure: __ctor<Loc, Ptr, long>
            public  CompoundAsmStatement(Loc loc, DArray<Statement> s, long stc) {
                super(loc, s);
                this.stc = stc;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
        public static class Catch extends RootObject
        {
            public Loc loc = new Loc();
            public Type type = null;
            public Identifier ident = null;
            public Statement handler = null;
            // Erasure: __ctor<Loc, Type, Identifier, Statement>
            public  Catch(Loc loc, Type t, Identifier id, Statement handler) {
                super();
                this.loc.opAssign(loc.copy());
                this.type = t;
                this.ident = id;
                this.handler = handler;
            }


            public Catch() {}

            public Catch copy() {
                Catch that = new Catch();
                that.loc = this.loc;
                that.type = this.type;
                that.ident = this.ident;
                that.handler = this.handler;
                return that;
            }
        }
        public static abstract class Type extends ASTNode
        {
            public byte ty = 0;
            public byte mod = 0;
            public BytePtr deco = null;
            public static Type tvoid = null;
            public static Type tint8 = null;
            public static Type tuns8 = null;
            public static Type tint16 = null;
            public static Type tuns16 = null;
            public static Type tint32 = null;
            public static Type tuns32 = null;
            public static Type tint64 = null;
            public static Type tuns64 = null;
            public static Type tint128 = null;
            public static Type tuns128 = null;
            public static Type tfloat32 = null;
            public static Type tfloat64 = null;
            public static Type tfloat80 = null;
            public static Type timaginary32 = null;
            public static Type timaginary64 = null;
            public static Type timaginary80 = null;
            public static Type tcomplex32 = null;
            public static Type tcomplex64 = null;
            public static Type tcomplex80 = null;
            public static Type tbool = null;
            public static Type tchar = null;
            public static Type twchar = null;
            public static Type tdchar = null;
            public static Slice<Type> basic = new RawSlice<Type>(new Type[44]);
            public static Type tshiftcnt = null;
            public static Type tvoidptr = null;
            public static Type tstring = null;
            public static Type twstring = null;
            public static Type tdstring = null;
            public static Type tvalist = null;
            public static Type terror = null;
            public static Type tnull = null;
            public static Type tsize_t = null;
            public static Type tptrdiff_t = null;
            public static Type thash_t = null;
            public static ClassDeclaration dtypeinfo = null;
            public static ClassDeclaration typeinfoclass = null;
            public static ClassDeclaration typeinfointerface = null;
            public static ClassDeclaration typeinfostruct = null;
            public static ClassDeclaration typeinfopointer = null;
            public static ClassDeclaration typeinfoarray = null;
            public static ClassDeclaration typeinfostaticarray = null;
            public static ClassDeclaration typeinfoassociativearray = null;
            public static ClassDeclaration typeinfovector = null;
            public static ClassDeclaration typeinfoenum = null;
            public static ClassDeclaration typeinfofunction = null;
            public static ClassDeclaration typeinfodelegate = null;
            public static ClassDeclaration typeinfotypelist = null;
            public static ClassDeclaration typeinfoconst = null;
            public static ClassDeclaration typeinfoinvariant = null;
            public static ClassDeclaration typeinfoshared = null;
            public static ClassDeclaration typeinfowild = null;
            public static StringTable stringtable = new StringTable();
            public static ByteSlice sizeTy = slice(new byte[]{(byte)60, (byte)64, (byte)76, (byte)60, (byte)60, (byte)96, (byte)88, (byte)64, (byte)64, (byte)60, (byte)60, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)64, (byte)56, (byte)88, (byte)88, (byte)60, (byte)68, (byte)84, (byte)56, (byte)60, (byte)64, (byte)64});
            public Type cto = null;
            public Type ito = null;
            public Type sto = null;
            public Type scto = null;
            public Type wto = null;
            public Type wcto = null;
            public Type swto = null;
            public Type swcto = null;
            public Type pto = null;
            public Type rto = null;
            public Type arrayof = null;
            // Erasure: __ctor<byte>
            public  Type(byte ty) {
                super();
                this.ty = ty;
            }

            // Erasure: toChars<>
            public  BytePtr toChars() {
                return new BytePtr("type");
            }

            // Erasure: _init<>
            public static void _init() {
                stringtable._init(14000);
                {
                    int i = 0;
                    for (; ((astbase._initbasetab.get(i) & 0xFF) != ENUMTY.Terror);i++){
                        Type t = new TypeBasic(astbase._initbasetab.get(i));
                        t = t.merge();
                        basic.set(((astbase._initbasetab.get(i) & 0xFF)), t);
                    }
                }
                basic.set(34, new TypeError());
                tvoid = basic.get(12);
                tint8 = basic.get(13);
                tuns8 = basic.get(14);
                tint16 = basic.get(15);
                tuns16 = basic.get(16);
                tint32 = basic.get(17);
                tuns32 = basic.get(18);
                tint64 = basic.get(19);
                tuns64 = basic.get(20);
                tint128 = basic.get(42);
                tuns128 = basic.get(43);
                tfloat32 = basic.get(21);
                tfloat64 = basic.get(22);
                tfloat80 = basic.get(23);
                timaginary32 = basic.get(24);
                timaginary64 = basic.get(25);
                timaginary80 = basic.get(26);
                tcomplex32 = basic.get(27);
                tcomplex64 = basic.get(28);
                tcomplex80 = basic.get(29);
                tbool = basic.get(30);
                tchar = basic.get(31);
                twchar = basic.get(32);
                tdchar = basic.get(33);
                tshiftcnt = tint32;
                terror = basic.get(34);
                tnull = basic.get(40);
                tnull = new TypeNull();
                tnull.deco = pcopy(tnull.merge().deco);
                tvoidptr = tvoid.pointerTo();
                tstring = tchar.immutableOf().arrayOf();
                twstring = twchar.immutableOf().arrayOf();
                tdstring = tdchar.immutableOf().arrayOf();
                tvalist = Target.va_listType();
                boolean isLP64 = global.params.isLP64;
                tsize_t = basic.get(isLP64 ? 20 : 18);
                tptrdiff_t = basic.get(isLP64 ? 19 : 17);
                thash_t = tsize_t;
            }

            // Erasure: pointerTo<>
            public  Type pointerTo() {
                if (((this.ty & 0xFF) == ENUMTY.Terror))
                {
                    return this;
                }
                if (this.pto == null)
                {
                    Type t = new TypePointer(this);
                    if (((this.ty & 0xFF) == ENUMTY.Tfunction))
                    {
                        t.deco = pcopy(t.merge().deco);
                        this.pto = t;
                    }
                    else
                    {
                        this.pto = t.merge();
                    }
                }
                return this.pto;
            }

            // Erasure: arrayOf<>
            public  Type arrayOf() {
                if (((this.ty & 0xFF) == ENUMTY.Terror))
                {
                    return this;
                }
                if (this.arrayof == null)
                {
                    Type t = new TypeDArray(this);
                    this.arrayof = t.merge();
                }
                return this.arrayof;
            }

            // Erasure: isImmutable<>
            public  boolean isImmutable() {
                return ((this.mod & 0xFF) & MODFlags.immutable_) != 0;
            }

            // Erasure: nullAttributes<>
            public  Type nullAttributes() {
                int sz = (sizeTy.get((this.ty & 0xFF)) & 0xFF);
                Type t = null;
                (t) = (this).copy();
                t.deco = null;
                t.arrayof = null;
                t.pto = null;
                t.rto = null;
                t.cto = null;
                t.ito = null;
                t.sto = null;
                t.scto = null;
                t.wto = null;
                t.wcto = null;
                t.swto = null;
                t.swcto = null;
                if (((t.ty & 0xFF) == ENUMTY.Tstruct))
                {
                    (((TypeStruct)t)).att = AliasThisRec.fwdref;
                }
                if (((t.ty & 0xFF) == ENUMTY.Tclass))
                {
                    (((TypeClass)t)).att = AliasThisRec.fwdref;
                }
                return t;
            }

            // Erasure: makeConst<>
            public  Type makeConst() {
                if (this.cto != null)
                {
                    return this.cto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)1;
                return t;
            }

            // Erasure: makeWildConst<>
            public  Type makeWildConst() {
                if (this.wcto != null)
                {
                    return this.wcto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)9;
                return t;
            }

            // Erasure: makeShared<>
            public  Type makeShared() {
                if (this.sto != null)
                {
                    return this.sto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)2;
                return t;
            }

            // Erasure: makeSharedConst<>
            public  Type makeSharedConst() {
                if (this.scto != null)
                {
                    return this.scto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)3;
                return t;
            }

            // Erasure: makeImmutable<>
            public  Type makeImmutable() {
                if (this.ito != null)
                {
                    return this.ito;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)4;
                return t;
            }

            // Erasure: makeWild<>
            public  Type makeWild() {
                if (this.wto != null)
                {
                    return this.wto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)8;
                return t;
            }

            // Erasure: makeSharedWildConst<>
            public  Type makeSharedWildConst() {
                if (this.swcto != null)
                {
                    return this.swcto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)11;
                return t;
            }

            // Erasure: makeSharedWild<>
            public  Type makeSharedWild() {
                if (this.swto != null)
                {
                    return this.swto;
                }
                Type t = this.nullAttributes();
                t.mod = (byte)10;
                return t;
            }

            // Erasure: merge<>
            public  Type merge() {
                if (((this.ty & 0xFF) == ENUMTY.Terror))
                {
                    return this;
                }
                if (((this.ty & 0xFF) == ENUMTY.Ttypeof))
                {
                    return this;
                }
                if (((this.ty & 0xFF) == ENUMTY.Tident))
                {
                    return this;
                }
                if (((this.ty & 0xFF) == ENUMTY.Tinstance))
                {
                    return this;
                }
                if (((this.ty & 0xFF) == ENUMTY.Taarray) && ((((TypeAArray)this)).index.merge().deco == null))
                {
                    return this;
                }
                if (((this.ty & 0xFF) != ENUMTY.Tenum) && (this.nextOf() != null) && (this.nextOf().deco == null))
                {
                    return this;
                }
                Type t = this;
                assert(t != null);
                return t;
            }

            // Erasure: addSTC<long>
            public  Type addSTC(long stc) {
                Type t = this;
                if (t.isImmutable())
                {
                }
                else if ((stc & 1048576L) != 0)
                {
                    t = t.makeImmutable();
                }
                else
                {
                    if (((stc & 536870912L) != 0) && !t.isShared())
                    {
                        if (t.isWild())
                        {
                            if (t.isConst())
                            {
                                t = t.makeSharedWildConst();
                            }
                            else
                            {
                                t = t.makeSharedWild();
                            }
                        }
                        else
                        {
                            if (t.isConst())
                            {
                                t = t.makeSharedConst();
                            }
                            else
                            {
                                t = t.makeShared();
                            }
                        }
                    }
                    if (((stc & 4L) != 0) && !t.isConst())
                    {
                        if (t.isShared())
                        {
                            if (t.isWild())
                            {
                                t = t.makeSharedWildConst();
                            }
                            else
                            {
                                t = t.makeSharedConst();
                            }
                        }
                        else
                        {
                            if (t.isWild())
                            {
                                t = t.makeWildConst();
                            }
                            else
                            {
                                t = t.makeConst();
                            }
                        }
                    }
                    if (((stc & 2147483648L) != 0) && !t.isWild())
                    {
                        if (t.isShared())
                        {
                            if (t.isConst())
                            {
                                t = t.makeSharedWildConst();
                            }
                            else
                            {
                                t = t.makeSharedWild();
                            }
                        }
                        else
                        {
                            if (t.isConst())
                            {
                                t = t.makeWildConst();
                            }
                            else
                            {
                                t = t.makeWild();
                            }
                        }
                    }
                }
                return t;
            }

            // Erasure: toExpression<>
            public  Expression toExpression() {
                return null;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return null;
            }

            // Erasure: sharedWildConstOf<>
            public  Type sharedWildConstOf() {
                if (((this.mod & 0xFF) == 11))
                {
                    return this;
                }
                if (this.swcto != null)
                {
                    assert(((this.swcto.mod & 0xFF) == 11));
                    return this.swcto;
                }
                Type t = this.makeSharedWildConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: sharedConstOf<>
            public  Type sharedConstOf() {
                if (((this.mod & 0xFF) == 3))
                {
                    return this;
                }
                if (this.scto != null)
                {
                    assert(((this.scto.mod & 0xFF) == 3));
                    return this.scto;
                }
                Type t = this.makeSharedConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: wildConstOf<>
            public  Type wildConstOf() {
                if (((this.mod & 0xFF) == MODFlags.wildconst))
                {
                    return this;
                }
                if (this.wcto != null)
                {
                    assert(((this.wcto.mod & 0xFF) == MODFlags.wildconst));
                    return this.wcto;
                }
                Type t = this.makeWildConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: constOf<>
            public  Type constOf() {
                if (((this.mod & 0xFF) == MODFlags.const_))
                {
                    return this;
                }
                if (this.cto != null)
                {
                    assert(((this.cto.mod & 0xFF) == MODFlags.const_));
                    return this.cto;
                }
                Type t = this.makeConst();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: sharedWildOf<>
            public  Type sharedWildOf() {
                if (((this.mod & 0xFF) == 10))
                {
                    return this;
                }
                if (this.swto != null)
                {
                    assert(((this.swto.mod & 0xFF) == 10));
                    return this.swto;
                }
                Type t = this.makeSharedWild();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: wildOf<>
            public  Type wildOf() {
                if (((this.mod & 0xFF) == MODFlags.wild))
                {
                    return this;
                }
                if (this.wto != null)
                {
                    assert(((this.wto.mod & 0xFF) == MODFlags.wild));
                    return this.wto;
                }
                Type t = this.makeWild();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: sharedOf<>
            public  Type sharedOf() {
                if (((this.mod & 0xFF) == MODFlags.shared_))
                {
                    return this;
                }
                if (this.sto != null)
                {
                    assert(((this.sto.mod & 0xFF) == MODFlags.shared_));
                    return this.sto;
                }
                Type t = this.makeShared();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: immutableOf<>
            public  Type immutableOf() {
                if (this.isImmutable())
                {
                    return this;
                }
                if (this.ito != null)
                {
                    assert(this.ito.isImmutable());
                    return this.ito;
                }
                Type t = this.makeImmutable();
                t = t.merge();
                t.fixTo(this);
                return t;
            }

            // Erasure: fixTo<Type>
            public  void fixTo(Type t) {
                Type mto = null;
                Type tn = this.nextOf();
                if ((tn == null) || ((this.ty & 0xFF) != ENUMTY.Tsarray) && ((tn.mod & 0xFF) == (t.nextOf().mod & 0xFF)))
                {
                    switch ((t.mod & 0xFF))
                    {
                        case 0:
                            mto = t;
                            break;
                        case 1:
                            this.cto = t;
                            break;
                        case 8:
                            this.wto = t;
                            break;
                        case 9:
                            this.wcto = t;
                            break;
                        case 2:
                            this.sto = t;
                            break;
                        case 3:
                            this.scto = t;
                            break;
                        case 10:
                            this.swto = t;
                            break;
                        case 11:
                            this.swcto = t;
                            break;
                        case 4:
                            this.ito = t;
                            break;
                        default:
                        break;
                    }
                }
                assert(((this.mod & 0xFF) != (t.mod & 0xFF)));
                switch ((this.mod & 0xFF))
                {
                    case 0:
                        break;
                    case 1:
                        this.cto = mto;
                        t.cto = this;
                        break;
                    case 8:
                        this.wto = mto;
                        t.wto = this;
                        break;
                    case 9:
                        this.wcto = mto;
                        t.wcto = this;
                        break;
                    case 2:
                        this.sto = mto;
                        t.sto = this;
                        break;
                    case 3:
                        this.scto = mto;
                        t.scto = this;
                        break;
                    case 10:
                        this.swto = mto;
                        t.swto = this;
                        break;
                    case 11:
                        this.swcto = mto;
                        t.swcto = this;
                        break;
                    case 4:
                        t.ito = this;
                        if (t.cto != null)
                        {
                            t.cto.ito = this;
                        }
                        if (t.sto != null)
                        {
                            t.sto.ito = this;
                        }
                        if (t.scto != null)
                        {
                            t.scto.ito = this;
                        }
                        if (t.wto != null)
                        {
                            t.wto.ito = this;
                        }
                        if (t.wcto != null)
                        {
                            t.wcto.ito = this;
                        }
                        if (t.swto != null)
                        {
                            t.swto.ito = this;
                        }
                        if (t.swcto != null)
                        {
                            t.swcto.ito = this;
                        }
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
            }

            // Erasure: addMod<byte>
            public  Type addMod(byte mod) {
                Type t = this;
                if (!t.isImmutable())
                {
                    switch ((mod & 0xFF))
                    {
                        case 0:
                            break;
                        case 1:
                            if (this.isShared())
                            {
                                if (this.isWild())
                                {
                                    t = this.sharedWildConstOf();
                                }
                                else
                                {
                                    t = this.sharedConstOf();
                                }
                            }
                            else
                            {
                                if (this.isWild())
                                {
                                    t = this.wildConstOf();
                                }
                                else
                                {
                                    t = this.constOf();
                                }
                            }
                            break;
                        case 8:
                            if (this.isShared())
                            {
                                if (this.isConst())
                                {
                                    t = this.sharedWildConstOf();
                                }
                                else
                                {
                                    t = this.sharedWildOf();
                                }
                            }
                            else
                            {
                                if (this.isConst())
                                {
                                    t = this.wildConstOf();
                                }
                                else
                                {
                                    t = this.wildOf();
                                }
                            }
                            break;
                        case 9:
                            if (this.isShared())
                            {
                                t = this.sharedWildConstOf();
                            }
                            else
                            {
                                t = this.wildConstOf();
                            }
                            break;
                        case 2:
                            if (this.isWild())
                            {
                                if (this.isConst())
                                {
                                    t = this.sharedWildConstOf();
                                }
                                else
                                {
                                    t = this.sharedWildOf();
                                }
                            }
                            else
                            {
                                if (this.isConst())
                                {
                                    t = this.sharedConstOf();
                                }
                                else
                                {
                                    t = this.sharedOf();
                                }
                            }
                            break;
                        case 3:
                            if (this.isWild())
                            {
                                t = this.sharedWildConstOf();
                            }
                            else
                            {
                                t = this.sharedConstOf();
                            }
                            break;
                        case 10:
                            if (this.isConst())
                            {
                                t = this.sharedWildConstOf();
                            }
                            else
                            {
                                t = this.sharedWildOf();
                            }
                            break;
                        case 11:
                            t = this.sharedWildConstOf();
                            break;
                        case 4:
                            t = this.immutableOf();
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
                return t;
            }

            // Erasure: nextOf<>
            public  Type nextOf() {
                return null;
            }

            // Erasure: isscalar<>
            public  boolean isscalar() {
                return false;
            }

            // Erasure: isConst<>
            public  boolean isConst() {
                return ((this.mod & 0xFF) & MODFlags.const_) != 0;
            }

            // Erasure: isWild<>
            public  boolean isWild() {
                return ((this.mod & 0xFF) & MODFlags.wild) != 0;
            }

            // Erasure: isShared<>
            public  boolean isShared() {
                return ((this.mod & 0xFF) & MODFlags.shared_) != 0;
            }

            // Erasure: toBasetype<>
            public  Type toBasetype() {
                return this;
            }

            // Erasure: toDsymbol<Ptr>
            public  Dsymbol toDsymbol(Ptr<Scope> sc) {
                return null;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Type() {}

            public abstract Type copy();
        }
        public static class TypeBasic extends Type
        {
            public BytePtr dstring = null;
            public int flags = 0;
            // Erasure: __ctor<byte>
            public  TypeBasic(byte ty) {
                super(ty);
                BytePtr d = null;
                int flags = 0;
                switch ((ty & 0xFF))
                {
                    case 12:
                        d = pcopy(Token.toChars(TOK.void_));
                        break;
                    case 13:
                        d = pcopy(Token.toChars(TOK.int8));
                        flags |= 1;
                        break;
                    case 14:
                        d = pcopy(Token.toChars(TOK.uns8));
                        flags |= 5;
                        break;
                    case 15:
                        d = pcopy(Token.toChars(TOK.int16));
                        flags |= 1;
                        break;
                    case 16:
                        d = pcopy(Token.toChars(TOK.uns16));
                        flags |= 5;
                        break;
                    case 17:
                        d = pcopy(Token.toChars(TOK.int32));
                        flags |= 1;
                        break;
                    case 18:
                        d = pcopy(Token.toChars(TOK.uns32));
                        flags |= 5;
                        break;
                    case 21:
                        d = pcopy(Token.toChars(TOK.float32));
                        flags |= 10;
                        break;
                    case 19:
                        d = pcopy(Token.toChars(TOK.int64));
                        flags |= 1;
                        break;
                    case 20:
                        d = pcopy(Token.toChars(TOK.uns64));
                        flags |= 5;
                        break;
                    case 42:
                        d = pcopy(Token.toChars(TOK.int128));
                        flags |= 1;
                        break;
                    case 43:
                        d = pcopy(Token.toChars(TOK.uns128));
                        flags |= 5;
                        break;
                    case 22:
                        d = pcopy(Token.toChars(TOK.float64));
                        flags |= 10;
                        break;
                    case 23:
                        d = pcopy(Token.toChars(TOK.float80));
                        flags |= 10;
                        break;
                    case 24:
                        d = pcopy(Token.toChars(TOK.imaginary32));
                        flags |= 18;
                        break;
                    case 25:
                        d = pcopy(Token.toChars(TOK.imaginary64));
                        flags |= 18;
                        break;
                    case 26:
                        d = pcopy(Token.toChars(TOK.imaginary80));
                        flags |= 18;
                        break;
                    case 27:
                        d = pcopy(Token.toChars(TOK.complex32));
                        flags |= 34;
                        break;
                    case 28:
                        d = pcopy(Token.toChars(TOK.complex64));
                        flags |= 34;
                        break;
                    case 29:
                        d = pcopy(Token.toChars(TOK.complex80));
                        flags |= 34;
                        break;
                    case 30:
                        d = pcopy(new BytePtr("bool"));
                        flags |= 5;
                        break;
                    case 31:
                        d = pcopy(Token.toChars(TOK.char_));
                        flags |= 69;
                        break;
                    case 32:
                        d = pcopy(Token.toChars(TOK.wchar_));
                        flags |= 69;
                        break;
                    case 33:
                        d = pcopy(Token.toChars(TOK.dchar_));
                        flags |= 69;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                this.dstring = pcopy(d);
                this.flags = flags;
                this.merge();
            }

            // Erasure: isscalar<>
            public  boolean isscalar() {
                return (this.flags & 3) != 0;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeBasic() {}

            public TypeBasic copy() {
                TypeBasic that = new TypeBasic();
                that.dstring = this.dstring;
                that.flags = this.flags;
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
                return that;
            }
        }
        public static class TypeError extends Type
        {
            // Erasure: __ctor<>
            public  TypeError() {
                super((byte)34);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeError copy() {
                TypeError that = new TypeError();
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
                return that;
            }
        }
        public static class TypeNull extends Type
        {
            // Erasure: __ctor<>
            public  TypeNull() {
                super((byte)40);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeNull copy() {
                TypeNull that = new TypeNull();
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
                return that;
            }
        }
        public static class TypeVector extends Type
        {
            public Type basetype = null;
            // Erasure: __ctor<Type>
            public  TypeVector(Type baseType) {
                super((byte)41);
                this.basetype = this.basetype;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return new TypeVector(this.basetype.syntaxCopy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeVector() {}

            public TypeVector copy() {
                TypeVector that = new TypeVector();
                that.basetype = this.basetype;
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
                return that;
            }
        }
        public static class TypeEnum extends Type
        {
            public EnumDeclaration sym = null;
            // Erasure: __ctor<EnumDeclaration>
            public  TypeEnum(EnumDeclaration sym) {
                super((byte)9);
                this.sym = sym;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeEnum() {}

            public TypeEnum copy() {
                TypeEnum that = new TypeEnum();
                that.sym = this.sym;
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
                return that;
            }
        }
        public static class TypeTuple extends Type
        {
            public DArray<Parameter> arguments = null;
            // Erasure: __ctor<Ptr>
            public  TypeTuple(DArray<Parameter> arguments) {
                super((byte)37);
                this.arguments = pcopy(arguments);
            }

            // Erasure: __ctor<Ptr>
            public  TypeTuple(DArray<Expression> exps, ETag1 __tag) {
                super((byte)37);
                DArray<Parameter> arguments = new DArray<Parameter>();
                if (exps != null)
                {
                    (arguments).setDim((exps).length);
                    {
                        int i = 0;
                        for (; (i < (exps).length);i++){
                            Expression e = (exps).get(i);
                            if (((e.type.ty & 0xFF) == ENUMTY.Ttuple))
                            {
                                e.error(new BytePtr("cannot form tuple of tuples"));
                            }
                            Parameter arg = new Parameter(0L, e.type, null, null, null);
                            arguments.set(i, arg);
                        }
                    }
                }
                this.arguments = pcopy(arguments);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                DArray<Parameter> args = Parameter.arraySyntaxCopy(this.arguments);
                Type t = new TypeTuple(args);
                t.mod = this.mod;
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeTuple() {}

            public TypeTuple copy() {
                TypeTuple that = new TypeTuple();
                that.arguments = this.arguments;
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
                return that;
            }
        }
        public static class TypeClass extends Type
        {
            public ClassDeclaration sym = null;
            public int att = AliasThisRec.fwdref;
            // Erasure: __ctor<ClassDeclaration>
            public  TypeClass(ClassDeclaration sym) {
                super((byte)7);
                this.sym = sym;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeClass() {}

            public TypeClass copy() {
                TypeClass that = new TypeClass();
                that.sym = this.sym;
                that.att = this.att;
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
                return that;
            }
        }
        public static class TypeStruct extends Type
        {
            public StructDeclaration sym = null;
            public int att = AliasThisRec.fwdref;
            // Erasure: __ctor<StructDeclaration>
            public  TypeStruct(StructDeclaration sym) {
                super((byte)8);
                this.sym = sym;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                return this;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeStruct() {}

            public TypeStruct copy() {
                TypeStruct that = new TypeStruct();
                that.sym = this.sym;
                that.att = this.att;
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
                return that;
            }
        }
        public static class TypeReference extends TypeNext
        {
            // Erasure: __ctor<Type>
            public  TypeReference(Type t) {
                super((byte)4, t);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = this.next.value.syntaxCopy();
                if ((pequals(t, this.next.value)))
                {
                    t = this;
                }
                else
                {
                    t = new TypeReference(t);
                    t.mod = this.mod;
                }
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeReference() {}

            public TypeReference copy() {
                TypeReference that = new TypeReference();
                that.next = this.next;
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
                return that;
            }
        }
        public static abstract class TypeNext extends Type
        {
            public Ref<Type> next = ref(null);
            // Erasure: __ctor<byte, Type>
            public  TypeNext(byte ty, Type next) {
                super(ty);
                this.next.value = next;
            }

            // Erasure: nextOf<>
            public  Type nextOf() {
                return this.next.value;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeNext() {}

            public abstract TypeNext copy();
        }
        public static class TypeSlice extends TypeNext
        {
            public Expression lwr = null;
            public Expression upr = null;
            // Erasure: __ctor<Type, Expression, Expression>
            public  TypeSlice(Type next, Expression lwr, Expression upr) {
                super((byte)38, next);
                this.lwr = lwr;
                this.upr = upr;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = new TypeSlice(this.next.value.syntaxCopy(), this.lwr.syntaxCopy(), this.upr.syntaxCopy());
                t.mod = this.mod;
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeSlice() {}

            public TypeSlice copy() {
                TypeSlice that = new TypeSlice();
                that.lwr = this.lwr;
                that.upr = this.upr;
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypeDelegate extends TypeNext
        {
            // Erasure: __ctor<Type>
            public  TypeDelegate(Type t) {
                super((byte)5, t);
                this.ty = (byte)10;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = this.next.value.syntaxCopy();
                if ((pequals(t, this.next.value)))
                {
                    t = this;
                }
                else
                {
                    t = new TypeDelegate(t);
                    t.mod = this.mod;
                }
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeDelegate() {}

            public TypeDelegate copy() {
                TypeDelegate that = new TypeDelegate();
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypePointer extends TypeNext
        {
            // Erasure: __ctor<Type>
            public  TypePointer(Type t) {
                super((byte)3, t);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = this.next.value.syntaxCopy();
                if ((pequals(t, this.next.value)))
                {
                    t = this;
                }
                else
                {
                    t = new TypePointer(t);
                    t.mod = this.mod;
                }
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypePointer() {}

            public TypePointer copy() {
                TypePointer that = new TypePointer();
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypeFunction extends TypeNext
        {
            public ParameterList parameterList = new ParameterList();
            public boolean isnothrow = false;
            public boolean isnogc = false;
            public boolean isproperty = false;
            public boolean isref = false;
            public boolean isreturn = false;
            public boolean isscope = false;
            public int linkage = 0;
            public int trust = 0;
            public int purity = PURE.impure;
            public byte iswild = 0;
            public DArray<Expression> fargs = null;
            // Erasure: __ctor<ParameterList, Type, int, long>
            public  TypeFunction(ParameterList pl, Type treturn, int linkage, long stc) {
                super((byte)5, treturn);
                assert((VarArg.none <= pl.varargs) && (pl.varargs <= VarArg.typesafe));
                this.parameterList.opAssign(pl.copy());
                this.linkage = linkage;
                if ((stc & 67108864L) != 0)
                {
                    this.purity = PURE.fwdref;
                }
                if ((stc & 33554432L) != 0)
                {
                    this.isnothrow = true;
                }
                if ((stc & 4398046511104L) != 0)
                {
                    this.isnogc = true;
                }
                if ((stc & 4294967296L) != 0)
                {
                    this.isproperty = true;
                }
                if ((stc & 2097152L) != 0)
                {
                    this.isref = true;
                }
                if ((stc & 17592186044416L) != 0)
                {
                    this.isreturn = true;
                }
                if ((stc & 524288L) != 0)
                {
                    this.isscope = true;
                }
                this.trust = TRUST.default_;
                if ((stc & 8589934592L) != 0)
                {
                    this.trust = TRUST.safe;
                }
                if ((stc & 34359738368L) != 0)
                {
                    this.trust = TRUST.system;
                }
                if ((stc & 17179869184L) != 0)
                {
                    this.trust = TRUST.trusted;
                }
            }

            // defaulted all parameters starting with #4
            public  TypeFunction(ParameterList pl, Type treturn, int linkage) {
                this(pl, treturn, linkage, 0L);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type treturn = this.next.value != null ? this.next.value.syntaxCopy() : null;
                DArray<Parameter> params = Parameter.arraySyntaxCopy(this.parameterList.parameters);
                TypeFunction t = new TypeFunction(new ParameterList(params, this.parameterList.varargs), treturn, this.linkage, 0L);
                t.mod = this.mod;
                t.isnothrow = this.isnothrow;
                t.isnogc = this.isnogc;
                t.purity = this.purity;
                t.isproperty = this.isproperty;
                t.isref = this.isref;
                t.isreturn = this.isreturn;
                t.isscope = this.isscope;
                t.iswild = this.iswild;
                t.trust = this.trust;
                t.fargs = pcopy(this.fargs);
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeFunction() {}

            public TypeFunction copy() {
                TypeFunction that = new TypeFunction();
                that.parameterList = this.parameterList;
                that.isnothrow = this.isnothrow;
                that.isnogc = this.isnogc;
                that.isproperty = this.isproperty;
                that.isref = this.isref;
                that.isreturn = this.isreturn;
                that.isscope = this.isscope;
                that.linkage = this.linkage;
                that.trust = this.trust;
                that.purity = this.purity;
                that.iswild = this.iswild;
                that.fargs = this.fargs;
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypeArray extends TypeNext
        {
            // Erasure: __ctor<byte, Type>
            public  TypeArray(byte ty, Type next) {
                super(ty, next);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeArray() {}

            public TypeArray copy() {
                TypeArray that = new TypeArray();
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypeDArray extends TypeArray
        {
            // Erasure: __ctor<Type>
            public  TypeDArray(Type t) {
                super((byte)0, t);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = this.next.value.syntaxCopy();
                if ((pequals(t, this.next.value)))
                {
                    t = this;
                }
                else
                {
                    t = new TypeDArray(t);
                    t.mod = this.mod;
                }
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeDArray() {}

            public TypeDArray copy() {
                TypeDArray that = new TypeDArray();
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypeAArray extends TypeArray
        {
            public Type index = null;
            public Loc loc = new Loc();
            // Erasure: __ctor<Type, Type>
            public  TypeAArray(Type t, Type index) {
                super((byte)2, t);
                this.index = index;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = this.next.value.syntaxCopy();
                Type ti = this.index.syntaxCopy();
                if ((pequals(t, this.next.value)) && (pequals(ti, this.index)))
                {
                    t = this;
                }
                else
                {
                    t = new TypeAArray(t, ti);
                    t.mod = this.mod;
                }
                return t;
            }

            // Erasure: toExpression<>
            public  Expression toExpression() {
                Expression e = this.next.value.toExpression();
                if (e != null)
                {
                    Expression ei = this.index.toExpression();
                    if (ei != null)
                    {
                        return new ArrayExp(this.loc, e, ei);
                    }
                }
                return null;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeAArray() {}

            public TypeAArray copy() {
                TypeAArray that = new TypeAArray();
                that.index = this.index;
                that.loc = this.loc;
                that.next = this.next;
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
                return that;
            }
        }
        public static class TypeSArray extends TypeArray
        {
            public Expression dim = null;
            // Erasure: __ctor<Type, Expression>
            public  TypeSArray(Type t, Expression dim) {
                super((byte)1, t);
                this.dim = dim;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                Type t = this.next.value.syntaxCopy();
                Expression e = this.dim.syntaxCopy();
                t = new TypeSArray(t, e);
                t.mod = this.mod;
                return t;
            }

            // Erasure: toExpression<>
            public  Expression toExpression() {
                Expression e = this.next.value.toExpression();
                if (e != null)
                {
                    e = new ArrayExp(this.dim.loc, e, this.dim);
                }
                return e;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeSArray() {}

            public TypeSArray copy() {
                TypeSArray that = new TypeSArray();
                that.dim = this.dim;
                that.next = this.next;
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
                return that;
            }
        }
        public static abstract class TypeQualified extends Type
        {
            public DArray<RootObject> idents = new DArray<RootObject>();
            public Loc loc = new Loc();
            // Erasure: __ctor<byte, Loc>
            public  TypeQualified(byte ty, Loc loc) {
                super(ty);
                this.loc.opAssign(loc.copy());
            }

            // Erasure: addIdent<Identifier>
            public  void addIdent(Identifier id) {
                this.idents.push(id);
            }

            // Erasure: addInst<TemplateInstance>
            public  void addInst(TemplateInstance ti) {
                this.idents.push(ti);
            }

            // Erasure: addIndex<RootObject>
            public  void addIndex(RootObject e) {
                this.idents.push(e);
            }

            // Erasure: syntaxCopyHelper<TypeQualified>
            public  void syntaxCopyHelper(TypeQualified t) {
                this.idents.setDim(t.idents.length);
                {
                    int i = 0;
                    for (; (i < this.idents.length);i++){
                        RootObject id = t.idents.get(i);
                        if ((id.dyncast() == DYNCAST.dsymbol))
                        {
                            TemplateInstance ti = ((TemplateInstance)id);
                            ti = ((TemplateInstance)ti.syntaxCopy(null));
                            id = ti;
                        }
                        else if ((id.dyncast() == DYNCAST.expression))
                        {
                            Expression e = ((Expression)id);
                            e = e.syntaxCopy();
                            id = e;
                        }
                        else if ((id.dyncast() == DYNCAST.type))
                        {
                            Type tx = ((Type)id);
                            tx = tx.syntaxCopy();
                            id = tx;
                        }
                        this.idents.set(i, id);
                    }
                }
            }

            // Erasure: toExpressionHelper<Expression, int>
            public  Expression toExpressionHelper(Expression e, int i) {
                for (; (i < this.idents.length);i++){
                    RootObject id = this.idents.get(i);
                    switch (id.dyncast())
                    {
                        case DYNCAST.identifier:
                            e = new DotIdExp(e.loc, e, ((Identifier)id));
                            break;
                        case DYNCAST.dsymbol:
                            TemplateInstance ti = (((Dsymbol)id)).isTemplateInstance();
                            assert(ti != null);
                            e = new DotTemplateInstanceExp(e.loc, e, ti.name, ti.tiargs);
                            break;
                        case DYNCAST.type:
                            e = new ArrayExp(this.loc, e, new TypeExp(this.loc, ((Type)id)));
                            break;
                        case DYNCAST.expression:
                            e = new ArrayExp(this.loc, e, ((Expression)id));
                            break;
                        default:
                        throw new AssertionError("Unreachable code!");
                    }
                }
                return e;
            }

            // defaulted all parameters starting with #2
            public  Expression toExpressionHelper(Expression e) {
                return toExpressionHelper(e, 0);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeQualified() {}

            public abstract TypeQualified copy();
        }
        public static class TypeTraits extends Type
        {
            public TraitsExp exp = null;
            public Loc loc = new Loc();
            public boolean inAliasDeclaration = false;
            // Erasure: __ctor<Loc, TraitsExp>
            public  TypeTraits(Loc loc, TraitsExp exp) {
                super((byte)6);
                this.loc.opAssign(loc.copy());
                this.exp = exp;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                TraitsExp te = ((TraitsExp)this.exp.syntaxCopy());
                TypeTraits tt = new TypeTraits(this.loc, te);
                tt.mod = this.mod;
                return tt;
            }


            public TypeTraits() {}

            public TypeTraits copy() {
                TypeTraits that = new TypeTraits();
                that.exp = this.exp;
                that.loc = this.loc;
                that.inAliasDeclaration = this.inAliasDeclaration;
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
                return that;
            }
        }
        public static class TypeIdentifier extends TypeQualified
        {
            public Identifier ident = null;
            // Erasure: __ctor<Loc, Identifier>
            public  TypeIdentifier(Loc loc, Identifier ident) {
                super((byte)6, loc);
                this.ident = ident;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                TypeIdentifier t = new TypeIdentifier(this.loc, this.ident);
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            // Erasure: toExpression<>
            public  Expression toExpression() {
                return this.toExpressionHelper(new IdentifierExp(this.loc, this.ident), 0);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeIdentifier() {}

            public TypeIdentifier copy() {
                TypeIdentifier that = new TypeIdentifier();
                that.ident = this.ident;
                that.idents = this.idents;
                that.loc = this.loc;
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
                return that;
            }
        }
        public static class TypeReturn extends TypeQualified
        {
            // Erasure: __ctor<Loc>
            public  TypeReturn(Loc loc) {
                super((byte)39, loc);
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                TypeReturn t = new TypeReturn(this.loc);
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeReturn() {}

            public TypeReturn copy() {
                TypeReturn that = new TypeReturn();
                that.idents = this.idents;
                that.loc = this.loc;
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
                return that;
            }
        }
        public static class TypeTypeof extends TypeQualified
        {
            public Expression exp = null;
            // Erasure: __ctor<Loc, Expression>
            public  TypeTypeof(Loc loc, Expression exp) {
                super((byte)36, loc);
                this.exp = exp;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                TypeTypeof t = new TypeTypeof(this.loc, this.exp.syntaxCopy());
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeTypeof() {}

            public TypeTypeof copy() {
                TypeTypeof that = new TypeTypeof();
                that.exp = this.exp;
                that.idents = this.idents;
                that.loc = this.loc;
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
                return that;
            }
        }
        public static class TypeInstance extends TypeQualified
        {
            public TemplateInstance tempinst = null;
            // Erasure: __ctor<Loc, TemplateInstance>
            public  TypeInstance(Loc loc, TemplateInstance tempinst) {
                super((byte)35, loc);
                this.tempinst = tempinst;
            }

            // Erasure: syntaxCopy<>
            public  Type syntaxCopy() {
                TypeInstance t = new TypeInstance(this.loc, ((TemplateInstance)this.tempinst.syntaxCopy(null)));
                t.syntaxCopyHelper(this);
                t.mod = this.mod;
                return t;
            }

            // Erasure: toExpression<>
            public  Expression toExpression() {
                return this.toExpressionHelper(new ScopeExp(this.loc, this.tempinst), 0);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeInstance() {}

            public TypeInstance copy() {
                TypeInstance that = new TypeInstance();
                that.tempinst = this.tempinst;
                that.idents = this.idents;
                that.loc = this.loc;
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
                return that;
            }
        }
        public static abstract class Expression extends ASTNode
        {
            public byte op = 0;
            public byte size = 0;
            public byte parens = 0;
            public Type type = null;
            public Loc loc = new Loc();
            // Erasure: __ctor<Loc, byte, int>
            public  Expression(Loc loc, byte op, int size) {
                super();
                this.loc.opAssign(loc.copy());
                this.op = op;
                this.size = (byte)size;
            }

            // Erasure: syntaxCopy<>
            public  Expression syntaxCopy() {
                return this.copy();
            }

            // Erasure: error<Ptr>
            public  void error(BytePtr format, Object... ap) {
                Ref<BytePtr> format_ref = ref(format);
                if ((!pequals(this.type, Type.terror)))
                {
                    verror(this.loc, format_ref.value, new RawSlice<>(ap), null, null, new BytePtr("Error: "));
                }
            }

            // Erasure: dyncast<>
            public  int dyncast() {
                return DYNCAST.expression;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Expression() {}

            public abstract Expression copy();
        }
        public static class DeclarationExp extends Expression
        {
            public Dsymbol declaration = null;
            // Erasure: __ctor<Loc, Dsymbol>
            public  DeclarationExp(Loc loc, Dsymbol declaration) {
                super(loc, TOK.declaration, 28);
                this.declaration = declaration;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeclarationExp() {}

            public DeclarationExp copy() {
                DeclarationExp that = new DeclarationExp();
                that.declaration = this.declaration;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IntegerExp extends Expression
        {
            public long value = 0L;
            // Erasure: __ctor<Loc, long, Type>
            public  IntegerExp(Loc loc, long value, Type type) {
                super(loc, TOK.int64, 32);
                assert(type != null);
                if (!type.isscalar())
                {
                    if (((type.ty & 0xFF) != ENUMTY.Terror))
                    {
                        this.error(new BytePtr("integral constant must be scalar type, not %s"), type.toChars());
                    }
                    type = Type.terror;
                }
                this.type = type;
                this.setInteger(value);
            }

            // Erasure: setInteger<long>
            public  void setInteger(long value) {
                this.value = value;
                this.normalize();
            }

            // Erasure: normalize<>
            public  void normalize() {
                {
                    int __dispatch5 = 0;
                    dispatched_5:
                    do {
                        switch (__dispatch5 != 0 ? __dispatch5 : (this.type.toBasetype().ty & 0xFF))
                        {
                            case 30:
                                this.value = ((this.value != 0L) ? 1 : 0);
                                break;
                            case 13:
                                this.value = (long)(byte)this.value;
                                break;
                            case 31:
                            case 14:
                                this.value = (long)(int)this.value;
                                break;
                            case 15:
                                this.value = (long)(int)this.value;
                                break;
                            case 32:
                            case 16:
                                __dispatch5 = 0;
                                this.value = (long)(int)this.value;
                                break;
                            case 17:
                                this.value = (long)(int)this.value;
                                break;
                            case 33:
                            case 18:
                                __dispatch5 = 0;
                                this.value = (long)(int)this.value;
                                break;
                            case 19:
                                this.value = (long)(long)this.value;
                                break;
                            case 20:
                                __dispatch5 = 0;
                                this.value = this.value;
                                break;
                            case 3:
                                if ((Target.ptrsize == 8))
                                {
                                    /*goto case*/{ __dispatch5 = 20; continue dispatched_5; }
                                }
                                if ((Target.ptrsize == 4))
                                {
                                    /*goto case*/{ __dispatch5 = 18; continue dispatched_5; }
                                }
                                if ((Target.ptrsize == 2))
                                {
                                    /*goto case*/{ __dispatch5 = 16; continue dispatched_5; }
                                }
                                throw new AssertionError("Unreachable code!");
                            default:
                            break;
                        }
                    } while(__dispatch5 != 0);
                }
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IntegerExp() {}

            public IntegerExp copy() {
                IntegerExp that = new IntegerExp();
                that.value = this.value;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NewAnonClassExp extends Expression
        {
            public Expression thisexp = null;
            public DArray<Expression> newargs = null;
            public ClassDeclaration cd = null;
            public DArray<Expression> arguments = null;
            // Erasure: __ctor<Loc, Expression, Ptr, ClassDeclaration, Ptr>
            public  NewAnonClassExp(Loc loc, Expression thisexp, DArray<Expression> newargs, ClassDeclaration cd, DArray<Expression> arguments) {
                super(loc, TOK.newAnonymousClass, 40);
                this.thisexp = thisexp;
                this.newargs = pcopy(newargs);
                this.cd = cd;
                this.arguments = pcopy(arguments);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NewAnonClassExp() {}

            public NewAnonClassExp copy() {
                NewAnonClassExp that = new NewAnonClassExp();
                that.thisexp = this.thisexp;
                that.newargs = this.newargs;
                that.cd = this.cd;
                that.arguments = this.arguments;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IsExp extends Expression
        {
            public Type targ = null;
            public Identifier id = null;
            public Type tspec = null;
            public DArray<TemplateParameter> parameters = null;
            public byte tok = 0;
            public byte tok2 = 0;
            // Erasure: __ctor<Loc, Type, Identifier, byte, Type, byte, Ptr>
            public  IsExp(Loc loc, Type targ, Identifier id, byte tok, Type tspec, byte tok2, DArray<TemplateParameter> parameters) {
                super(loc, TOK.is_, 42);
                this.targ = targ;
                this.id = id;
                this.tok = tok;
                this.tspec = tspec;
                this.tok2 = tok2;
                this.parameters = pcopy(parameters);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IsExp() {}

            public IsExp copy() {
                IsExp that = new IsExp();
                that.targ = this.targ;
                that.id = this.id;
                that.tspec = this.tspec;
                that.parameters = this.parameters;
                that.tok = this.tok;
                that.tok2 = this.tok2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class RealExp extends Expression
        {
            public double value = 0.0;
            // Erasure: __ctor<Loc, double, Type>
            public  RealExp(Loc loc, double value, Type type) {
                super(loc, TOK.float64, 40);
                this.value = value;
                this.type = type;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public RealExp() {}

            public RealExp copy() {
                RealExp that = new RealExp();
                that.value = this.value;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NullExp extends Expression
        {
            // Erasure: __ctor<Loc, Type>
            public  NullExp(Loc loc, Type type) {
                super(loc, TOK.null_, 24);
                this.type = type;
            }

            // defaulted all parameters starting with #2
            public  NullExp(Loc loc) {
                this(loc, (Type)null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NullExp() {}

            public NullExp copy() {
                NullExp that = new NullExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeidExp extends Expression
        {
            public RootObject obj = null;
            // Erasure: __ctor<Loc, RootObject>
            public  TypeidExp(Loc loc, RootObject o) {
                super(loc, TOK.typeid_, 28);
                this.obj = o;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeidExp() {}

            public TypeidExp copy() {
                TypeidExp that = new TypeidExp();
                that.obj = this.obj;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TraitsExp extends Expression
        {
            public Identifier ident = null;
            public DArray<RootObject> args = null;
            // Erasure: __ctor<Loc, Identifier, Ptr>
            public  TraitsExp(Loc loc, Identifier ident, DArray<RootObject> args) {
                super(loc, TOK.traits, 32);
                this.ident = ident;
                this.args = pcopy(args);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TraitsExp() {}

            public TraitsExp copy() {
                TraitsExp that = new TraitsExp();
                that.ident = this.ident;
                that.args = this.args;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class StringExp extends Expression
        {
            public BytePtr string = null;
            public CharPtr wstring = null;
            public Ptr<Integer> dstring = null;
            public int len = 0;
            public byte sz = (byte)1;
            public byte postfix = (byte)0;
            // Erasure: __ctor<Loc, Ptr>
            public  StringExp(Loc loc, BytePtr string) {
                super(loc, TOK.string_, 34);
                this.string = pcopy(string);
                this.len = strlen(string);
                this.sz = (byte)1;
            }

            // Erasure: __ctor<Loc, Ptr, int>
            public  StringExp(Loc loc, Object string, int len) {
                super(loc, TOK.string_, 34);
                this.string = pcopy(((BytePtr)string));
                this.len = len;
                this.sz = (byte)1;
            }

            // Erasure: __ctor<Loc, Ptr, int, byte>
            public  StringExp(Loc loc, Object string, int len, byte postfix) {
                super(loc, TOK.string_, 34);
                this.string = pcopy(((BytePtr)string));
                this.len = len;
                this.postfix = postfix;
                this.sz = (byte)1;
            }

            // Erasure: writeTo<Ptr, boolean, int>
            public  void writeTo(Object dest, boolean zero, int tyto) {
                int encSize = 0;
                switch (tyto)
                {
                    case 0:
                        encSize = (this.sz & 0xFF);
                        break;
                    case 31:
                        encSize = 1;
                        break;
                    case 32:
                        encSize = 2;
                        break;
                    case 33:
                        encSize = 4;
                        break;
                    default:
                    throw new AssertionError("Unreachable code!");
                }
                if (((this.sz & 0xFF) == encSize))
                {
                    memcpy((BytePtr)dest, (this.string), (this.len * (this.sz & 0xFF)));
                    if (zero)
                    {
                        memset(((BytePtr)dest).plus((this.len * (this.sz & 0xFF))), 0, (this.sz & 0xFF));
                    }
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }

            // defaulted all parameters starting with #3
            public  void writeTo(Object dest, boolean zero) {
                writeTo(dest, zero, 0);
            }

            // Erasure: toStringz<>
            public  ByteSlice toStringz() {
                int nbytes = this.len * (this.sz & 0xFF);
                BytePtr s = pcopy(((BytePtr)Mem.xmalloc(nbytes + (this.sz & 0xFF))));
                this.writeTo(s, true, 0);
                return s.slice(0,nbytes);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StringExp() {}

            public StringExp copy() {
                StringExp that = new StringExp();
                that.string = this.string;
                that.wstring = this.wstring;
                that.dstring = this.dstring;
                that.len = this.len;
                that.sz = this.sz;
                that.postfix = this.postfix;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NewExp extends Expression
        {
            public Expression thisexp = null;
            public DArray<Expression> newargs = null;
            public Type newtype = null;
            public DArray<Expression> arguments = null;
            // Erasure: __ctor<Loc, Expression, Ptr, Type, Ptr>
            public  NewExp(Loc loc, Expression thisexp, DArray<Expression> newargs, Type newtype, DArray<Expression> arguments) {
                super(loc, TOK.new_, 40);
                this.thisexp = thisexp;
                this.newargs = pcopy(newargs);
                this.newtype = newtype;
                this.arguments = pcopy(arguments);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NewExp() {}

            public NewExp copy() {
                NewExp that = new NewExp();
                that.thisexp = this.thisexp;
                that.newargs = this.newargs;
                that.newtype = this.newtype;
                that.arguments = this.arguments;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AssocArrayLiteralExp extends Expression
        {
            public DArray<Expression> keys = null;
            public DArray<Expression> values = null;
            // Erasure: __ctor<Loc, Ptr, Ptr>
            public  AssocArrayLiteralExp(Loc loc, DArray<Expression> keys, DArray<Expression> values) {
                super(loc, TOK.assocArrayLiteral, 32);
                assert(((keys).length == (values).length));
                this.keys = pcopy(keys);
                this.values = pcopy(values);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AssocArrayLiteralExp() {}

            public AssocArrayLiteralExp copy() {
                AssocArrayLiteralExp that = new AssocArrayLiteralExp();
                that.keys = this.keys;
                that.values = this.values;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ArrayLiteralExp extends Expression
        {
            public Expression basis = null;
            public DArray<Expression> elements = null;
            // Erasure: __ctor<Loc, Ptr>
            public  ArrayLiteralExp(Loc loc, DArray<Expression> elements) {
                super(loc, TOK.arrayLiteral, 32);
                this.elements = pcopy(elements);
            }

            // Erasure: __ctor<Loc, Expression>
            public  ArrayLiteralExp(Loc loc, Expression e) {
                super(loc, TOK.arrayLiteral, 32);
                this.elements = pcopy(new DArray<Expression>());
                (this.elements).push(e);
            }

            // Erasure: __ctor<Loc, Expression, Ptr>
            public  ArrayLiteralExp(Loc loc, Expression basis, DArray<Expression> elements) {
                super(loc, TOK.arrayLiteral, 32);
                this.basis = basis;
                this.elements = pcopy(elements);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ArrayLiteralExp() {}

            public ArrayLiteralExp copy() {
                ArrayLiteralExp that = new ArrayLiteralExp();
                that.basis = this.basis;
                that.elements = this.elements;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class FuncExp extends Expression
        {
            public FuncLiteralDeclaration fd = null;
            public TemplateDeclaration td = null;
            public byte tok = 0;
            // Erasure: __ctor<Loc, Dsymbol>
            public  FuncExp(Loc loc, Dsymbol s) {
                super(loc, TOK.function_, 33);
                this.td = s.isTemplateDeclaration();
                this.fd = s.isFuncLiteralDeclaration();
                if (this.td != null)
                {
                    assert(this.td.literal);
                    assert((this.td.members != null) && ((this.td.members).length == 1));
                    this.fd = (this.td.members).get(0).isFuncLiteralDeclaration();
                }
                this.tok = this.fd.tok;
                assert(this.fd.fbody != null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncExp() {}

            public FuncExp copy() {
                FuncExp that = new FuncExp();
                that.fd = this.fd;
                that.td = this.td;
                that.tok = this.tok;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IntervalExp extends Expression
        {
            public Expression lwr = null;
            public Expression upr = null;
            // Erasure: __ctor<Loc, Expression, Expression>
            public  IntervalExp(Loc loc, Expression lwr, Expression upr) {
                super(loc, TOK.interval, 32);
                this.lwr = lwr;
                this.upr = upr;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IntervalExp() {}

            public IntervalExp copy() {
                IntervalExp that = new IntervalExp();
                that.lwr = this.lwr;
                that.upr = this.upr;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TypeExp extends Expression
        {
            // Erasure: __ctor<Loc, Type>
            public  TypeExp(Loc loc, Type type) {
                super(loc, TOK.type, 24);
                this.type = type;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TypeExp() {}

            public TypeExp copy() {
                TypeExp that = new TypeExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ScopeExp extends Expression
        {
            public ScopeDsymbol sds = null;
            // Erasure: __ctor<Loc, ScopeDsymbol>
            public  ScopeExp(Loc loc, ScopeDsymbol sds) {
                super(loc, TOK.scope_, 28);
                this.sds = sds;
                assert(sds.isTemplateDeclaration() == null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ScopeExp() {}

            public ScopeExp copy() {
                ScopeExp that = new ScopeExp();
                that.sds = this.sds;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IdentifierExp extends Expression
        {
            public Identifier ident = null;
            // Erasure: __ctor<Loc, Identifier>
            public  IdentifierExp(Loc loc, Identifier ident) {
                super(loc, TOK.identifier, 28);
                this.ident = ident;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IdentifierExp() {}

            public IdentifierExp copy() {
                IdentifierExp that = new IdentifierExp();
                that.ident = this.ident;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class UnaExp extends Expression
        {
            public Expression e1 = null;
            // Erasure: __ctor<Loc, byte, int, Expression>
            public  UnaExp(Loc loc, byte op, int size, Expression e1) {
                super(loc, op, size);
                this.e1 = e1;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UnaExp() {}

            public UnaExp copy() {
                UnaExp that = new UnaExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DefaultInitExp extends Expression
        {
            public byte subop = 0;
            // Erasure: __ctor<Loc, byte, int>
            public  DefaultInitExp(Loc loc, byte subop, int size) {
                super(loc, TOK.default_, size);
                this.subop = subop;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DefaultInitExp() {}

            public DefaultInitExp copy() {
                DefaultInitExp that = new DefaultInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static abstract class BinExp extends Expression
        {
            public Expression e1 = null;
            public Expression e2 = null;
            // Erasure: __ctor<Loc, byte, int, Expression, Expression>
            public  BinExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
                super(loc, op, size);
                this.e1 = e1;
                this.e2 = e2;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public BinExp() {}

            public abstract BinExp copy();
        }
        public static class DsymbolExp extends Expression
        {
            public Dsymbol s = null;
            public boolean hasOverloads = false;
            // Erasure: __ctor<Loc, Dsymbol, boolean>
            public  DsymbolExp(Loc loc, Dsymbol s, boolean hasOverloads) {
                super(loc, TOK.dSymbol, 29);
                this.s = s;
                this.hasOverloads = hasOverloads;
            }

            // defaulted all parameters starting with #3
            public  DsymbolExp(Loc loc, Dsymbol s) {
                this(loc, s, true);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DsymbolExp() {}

            public DsymbolExp copy() {
                DsymbolExp that = new DsymbolExp();
                that.s = this.s;
                that.hasOverloads = this.hasOverloads;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TemplateExp extends Expression
        {
            public TemplateDeclaration td = null;
            public FuncDeclaration fd = null;
            // Erasure: __ctor<Loc, TemplateDeclaration, FuncDeclaration>
            public  TemplateExp(Loc loc, TemplateDeclaration td, FuncDeclaration fd) {
                super(loc, TOK.template_, 32);
                this.td = td;
                this.fd = fd;
            }

            // defaulted all parameters starting with #3
            public  TemplateExp(Loc loc, TemplateDeclaration td) {
                this(loc, td, (FuncDeclaration)null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateExp() {}

            public TemplateExp copy() {
                TemplateExp that = new TemplateExp();
                that.td = this.td;
                that.fd = this.fd;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SymbolExp extends Expression
        {
            public Declaration var = null;
            public boolean hasOverloads = false;
            // Erasure: __ctor<Loc, byte, int, Declaration, boolean>
            public  SymbolExp(Loc loc, byte op, int size, Declaration var, boolean hasOverloads) {
                super(loc, op, size);
                assert(var != null);
                this.var = var;
                this.hasOverloads = hasOverloads;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SymbolExp() {}

            public SymbolExp copy() {
                SymbolExp that = new SymbolExp();
                that.var = this.var;
                that.hasOverloads = this.hasOverloads;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class VarExp extends SymbolExp
        {
            // Erasure: __ctor<Loc, Declaration, boolean>
            public  VarExp(Loc loc, Declaration var, boolean hasOverloads) {
                super(loc, TOK.variable, 29, var, var.isVarDeclaration() == null && hasOverloads);
                this.type = var.type;
            }

            // defaulted all parameters starting with #3
            public  VarExp(Loc loc, Declaration var) {
                this(loc, var, true);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VarExp() {}

            public VarExp copy() {
                VarExp that = new VarExp();
                that.var = this.var;
                that.hasOverloads = this.hasOverloads;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TupleExp extends Expression
        {
            public Expression e0 = null;
            public DArray<Expression> exps = null;
            // Erasure: __ctor<Loc, Expression, Ptr>
            public  TupleExp(Loc loc, Expression e0, DArray<Expression> exps) {
                super(loc, TOK.tuple, 32);
                this.e0 = e0;
                this.exps = pcopy(exps);
            }

            // Erasure: __ctor<Loc, Ptr>
            public  TupleExp(Loc loc, DArray<Expression> exps) {
                super(loc, TOK.tuple, 32);
                this.exps = pcopy(exps);
            }

            // Erasure: __ctor<Loc, TupleDeclaration>
            public  TupleExp(Loc loc, TupleDeclaration tup) {
                super(loc, TOK.tuple, 32);
                this.exps = pcopy(new DArray<Expression>());
                (this.exps).reserve((tup.objects).length);
                {
                    int i = 0;
                    for (; (i < (tup.objects).length);i++){
                        RootObject o = (tup.objects).get(i);
                        {
                            Dsymbol s = this.getDsymbol(o);
                            if ((s) != null)
                            {
                                Expression e = new DsymbolExp(loc, s, true);
                                (this.exps).push(e);
                            }
                            else if ((o.dyncast() == DYNCAST.expression))
                            {
                                Expression e = (((Expression)o)).copy();
                                e.loc.opAssign(loc.copy());
                                (this.exps).push(e);
                            }
                            else if ((o.dyncast() == DYNCAST.type))
                            {
                                Type t = ((Type)o);
                                Expression e = new TypeExp(loc, t);
                                (this.exps).push(e);
                            }
                            else
                            {
                                this.error(new BytePtr("%s is not an expression"), o.toChars());
                            }
                        }
                    }
                }
            }

            // Erasure: isDsymbol<RootObject>
            public  Dsymbol isDsymbol(RootObject o) {
                if ((o == null) || (o.dyncast() != 0) || (DYNCAST.dsymbol != 0))
                {
                    return null;
                }
                return ((Dsymbol)o);
            }

            // Erasure: getDsymbol<RootObject>
            public  Dsymbol getDsymbol(RootObject oarg) {
                Dsymbol sa = null;
                Expression ea = isExpression(oarg);
                if (ea != null)
                {
                    if (((ea.op & 0xFF) == 26))
                    {
                        sa = (((VarExp)ea)).var;
                    }
                    else if (((ea.op & 0xFF) == 161))
                    {
                        if ((((FuncExp)ea)).td != null)
                        {
                            sa = (((FuncExp)ea)).td;
                        }
                        else
                        {
                            sa = (((FuncExp)ea)).fd;
                        }
                    }
                    else if (((ea.op & 0xFF) == 36))
                    {
                        sa = (((TemplateExp)ea)).td;
                    }
                    else
                    {
                        sa = null;
                    }
                }
                else
                {
                    Type ta = isType(oarg);
                    if (ta != null)
                    {
                        sa = ta.toDsymbol(null);
                    }
                    else
                    {
                        sa = this.isDsymbol(oarg);
                    }
                }
                return sa;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TupleExp() {}

            public TupleExp copy() {
                TupleExp that = new TupleExp();
                that.e0 = this.e0;
                that.exps = this.exps;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DollarExp extends IdentifierExp
        {
            // Erasure: __ctor<Loc>
            public  DollarExp(Loc loc) {
                super(loc, Id.dollar);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DollarExp() {}

            public DollarExp copy() {
                DollarExp that = new DollarExp();
                that.ident = this.ident;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ThisExp extends Expression
        {
            // Erasure: __ctor<Loc>
            public  ThisExp(Loc loc) {
                super(loc, TOK.this_, 24);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ThisExp() {}

            public ThisExp copy() {
                ThisExp that = new ThisExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class SuperExp extends ThisExp
        {
            // Erasure: __ctor<Loc>
            public  SuperExp(Loc loc) {
                super(loc);
                this.op = TOK.super_;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public SuperExp() {}

            public SuperExp copy() {
                SuperExp that = new SuperExp();
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AddrExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  AddrExp(Loc loc, Expression e) {
                super(loc, TOK.address, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AddrExp() {}

            public AddrExp copy() {
                AddrExp that = new AddrExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PreExp extends UnaExp
        {
            // Erasure: __ctor<byte, Loc, Expression>
            public  PreExp(byte op, Loc loc, Expression e) {
                super(loc, op, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PreExp() {}

            public PreExp copy() {
                PreExp that = new PreExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PtrExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  PtrExp(Loc loc, Expression e) {
                super(loc, TOK.star, 28, e);
            }

            // Erasure: __ctor<Loc, Expression, Type>
            public  PtrExp(Loc loc, Expression e, Type t) {
                super(loc, TOK.star, 28, e);
                this.type = t;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PtrExp() {}

            public PtrExp copy() {
                PtrExp that = new PtrExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NegExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  NegExp(Loc loc, Expression e) {
                super(loc, TOK.negate, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NegExp() {}

            public NegExp copy() {
                NegExp that = new NegExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class UAddExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  UAddExp(Loc loc, Expression e) {
                super(loc, TOK.uadd, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UAddExp() {}

            public UAddExp copy() {
                UAddExp that = new UAddExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class NotExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  NotExp(Loc loc, Expression e) {
                super(loc, TOK.not, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public NotExp() {}

            public NotExp copy() {
                NotExp that = new NotExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ComExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  ComExp(Loc loc, Expression e) {
                super(loc, TOK.tilde, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ComExp() {}

            public ComExp copy() {
                ComExp that = new ComExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DeleteExp extends UnaExp
        {
            public boolean isRAII = false;
            // Erasure: __ctor<Loc, Expression, boolean>
            public  DeleteExp(Loc loc, Expression e, boolean isRAII) {
                super(loc, TOK.delete_, 29, e);
                this.isRAII = isRAII;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DeleteExp() {}

            public DeleteExp copy() {
                DeleteExp that = new DeleteExp();
                that.isRAII = this.isRAII;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CastExp extends UnaExp
        {
            public Type to = null;
            public byte mod = (byte)255;
            // Erasure: __ctor<Loc, Expression, Type>
            public  CastExp(Loc loc, Expression e, Type t) {
                super(loc, TOK.cast_, 33, e);
                this.to = t;
            }

            // Erasure: __ctor<Loc, Expression, byte>
            public  CastExp(Loc loc, Expression e, byte mod) {
                super(loc, TOK.cast_, 33, e);
                this.mod = mod;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CastExp() {}

            public CastExp copy() {
                CastExp that = new CastExp();
                that.to = this.to;
                that.mod = this.mod;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CallExp extends UnaExp
        {
            public DArray<Expression> arguments = null;
            // Erasure: __ctor<Loc, Expression, Ptr>
            public  CallExp(Loc loc, Expression e, DArray<Expression> exps) {
                super(loc, TOK.call, 32, e);
                this.arguments = pcopy(exps);
            }

            // Erasure: __ctor<Loc, Expression>
            public  CallExp(Loc loc, Expression e) {
                super(loc, TOK.call, 32, e);
            }

            // Erasure: __ctor<Loc, Expression, Expression>
            public  CallExp(Loc loc, Expression e, Expression earg1) {
                super(loc, TOK.call, 32, e);
                DArray<Expression> arguments = new DArray<Expression>();
                if (earg1 != null)
                {
                    (arguments).setDim(1);
                    arguments.set(0, earg1);
                }
                this.arguments = pcopy(arguments);
            }

            // Erasure: __ctor<Loc, Expression, Expression, Expression>
            public  CallExp(Loc loc, Expression e, Expression earg1, Expression earg2) {
                super(loc, TOK.call, 32, e);
                DArray<Expression> arguments = new DArray<Expression>();
                (arguments).setDim(2);
                arguments.set(0, earg1);
                arguments.set(1, earg2);
                this.arguments = pcopy(arguments);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CallExp() {}

            public CallExp copy() {
                CallExp that = new CallExp();
                that.arguments = this.arguments;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DotIdExp extends UnaExp
        {
            public Identifier ident = null;
            // Erasure: __ctor<Loc, Expression, Identifier>
            public  DotIdExp(Loc loc, Expression e, Identifier ident) {
                super(loc, TOK.dotIdentifier, 32, e);
                this.ident = ident;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DotIdExp() {}

            public DotIdExp copy() {
                DotIdExp that = new DotIdExp();
                that.ident = this.ident;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AssertExp extends UnaExp
        {
            public Expression msg = null;
            // Erasure: __ctor<Loc, Expression, Expression>
            public  AssertExp(Loc loc, Expression e, Expression msg) {
                super(loc, TOK.assert_, 32, e);
                this.msg = msg;
            }

            // defaulted all parameters starting with #3
            public  AssertExp(Loc loc, Expression e) {
                this(loc, e, (Expression)null);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AssertExp() {}

            public AssertExp copy() {
                AssertExp that = new AssertExp();
                that.msg = this.msg;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CompileExp extends Expression
        {
            public DArray<Expression> exps = null;
            // Erasure: __ctor<Loc, Ptr>
            public  CompileExp(Loc loc, DArray<Expression> exps) {
                super(loc, TOK.mixin_, 28);
                this.exps = pcopy(exps);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CompileExp() {}

            public CompileExp copy() {
                CompileExp that = new CompileExp();
                that.exps = this.exps;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ImportExp extends UnaExp
        {
            // Erasure: __ctor<Loc, Expression>
            public  ImportExp(Loc loc, Expression e) {
                super(loc, TOK.import_, 28, e);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ImportExp() {}

            public ImportExp copy() {
                ImportExp that = new ImportExp();
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DotTemplateInstanceExp extends UnaExp
        {
            public TemplateInstance ti = null;
            // Erasure: __ctor<Loc, Expression, Identifier, Ptr>
            public  DotTemplateInstanceExp(Loc loc, Expression e, Identifier name, DArray<RootObject> tiargs) {
                super(loc, TOK.dotTemplateInstance, 32, e);
                this.ti = new TemplateInstance(loc, name, tiargs);
            }

            // Erasure: __ctor<Loc, Expression, TemplateInstance>
            public  DotTemplateInstanceExp(Loc loc, Expression e, TemplateInstance ti) {
                super(loc, TOK.dotTemplateInstance, 32, e);
                this.ti = ti;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DotTemplateInstanceExp() {}

            public DotTemplateInstanceExp copy() {
                DotTemplateInstanceExp that = new DotTemplateInstanceExp();
                that.ti = this.ti;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ArrayExp extends UnaExp
        {
            public DArray<Expression> arguments = null;
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ArrayExp(Loc loc, Expression e1, Expression index) {
                super(loc, TOK.array, 32, e1);
                this.arguments = pcopy(new DArray<Expression>());
                if (index != null)
                {
                    (this.arguments).push(index);
                }
            }

            // defaulted all parameters starting with #3
            public  ArrayExp(Loc loc, Expression e1) {
                this(loc, e1, (Expression)null);
            }

            // Erasure: __ctor<Loc, Expression, Ptr>
            public  ArrayExp(Loc loc, Expression e1, DArray<Expression> args) {
                super(loc, TOK.array, 32, e1);
                this.arguments = pcopy(args);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ArrayExp() {}

            public ArrayExp copy() {
                ArrayExp that = new ArrayExp();
                that.arguments = this.arguments;
                that.e1 = this.e1;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class FuncInitExp extends DefaultInitExp
        {
            // Erasure: __ctor<Loc>
            public  FuncInitExp(Loc loc) {
                super(loc, TOK.functionString, 25);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FuncInitExp() {}

            public FuncInitExp copy() {
                FuncInitExp that = new FuncInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PrettyFuncInitExp extends DefaultInitExp
        {
            // Erasure: __ctor<Loc>
            public  PrettyFuncInitExp(Loc loc) {
                super(loc, TOK.prettyFunction, 25);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PrettyFuncInitExp() {}

            public PrettyFuncInitExp copy() {
                PrettyFuncInitExp that = new PrettyFuncInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class FileInitExp extends DefaultInitExp
        {
            // Erasure: __ctor<Loc, byte>
            public  FileInitExp(Loc loc, byte tok) {
                super(loc, tok, 25);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public FileInitExp() {}

            public FileInitExp copy() {
                FileInitExp that = new FileInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class LineInitExp extends DefaultInitExp
        {
            // Erasure: __ctor<Loc>
            public  LineInitExp(Loc loc) {
                super(loc, TOK.line, 25);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LineInitExp() {}

            public LineInitExp copy() {
                LineInitExp that = new LineInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ModuleInitExp extends DefaultInitExp
        {
            // Erasure: __ctor<Loc>
            public  ModuleInitExp(Loc loc) {
                super(loc, TOK.moduleString, 25);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ModuleInitExp() {}

            public ModuleInitExp copy() {
                ModuleInitExp that = new ModuleInitExp();
                that.subop = this.subop;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CommaExp extends BinExp
        {
            public boolean isGenerated = false;
            public boolean allowCommaExp = false;
            // Erasure: __ctor<Loc, Expression, Expression, boolean>
            public  CommaExp(Loc loc, Expression e1, Expression e2, boolean generated) {
                super(loc, TOK.comma, 34, e1, e2);
                this.allowCommaExp = (this.isGenerated = generated);
            }

            // defaulted all parameters starting with #4
            public  CommaExp(Loc loc, Expression e1, Expression e2) {
                this(loc, e1, e2, true);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CommaExp() {}

            public CommaExp copy() {
                CommaExp that = new CommaExp();
                that.isGenerated = this.isGenerated;
                that.allowCommaExp = this.allowCommaExp;
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PostExp extends BinExp
        {
            // Erasure: __ctor<byte, Loc, Expression>
            public  PostExp(byte op, Loc loc, Expression e) {
                super(loc, op, 32, e, new IntegerExp(loc, 1L, Type.tint32));
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PostExp() {}

            public PostExp copy() {
                PostExp that = new PostExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PowExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  PowExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.pow, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PowExp() {}

            public PowExp copy() {
                PowExp that = new PowExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class MulExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  MulExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.mul, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MulExp() {}

            public MulExp copy() {
                MulExp that = new MulExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DivExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  DivExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.div, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DivExp() {}

            public DivExp copy() {
                DivExp that = new DivExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ModExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ModExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.mod, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ModExp() {}

            public ModExp copy() {
                ModExp that = new ModExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AddExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  AddExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.add, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AddExp() {}

            public AddExp copy() {
                AddExp that = new AddExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class MinExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  MinExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.min, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MinExp() {}

            public MinExp copy() {
                MinExp that = new MinExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CatExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  CatExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.concatenate, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CatExp() {}

            public CatExp copy() {
                CatExp that = new CatExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ShlExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ShlExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.leftShift, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShlExp() {}

            public ShlExp copy() {
                ShlExp that = new ShlExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ShrExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ShrExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.rightShift, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShrExp() {}

            public ShrExp copy() {
                ShrExp that = new ShrExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class UshrExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  UshrExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.unsignedRightShift, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UshrExp() {}

            public UshrExp copy() {
                UshrExp that = new UshrExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class EqualExp extends BinExp
        {
            // Erasure: __ctor<byte, Loc, Expression, Expression>
            public  EqualExp(byte op, Loc loc, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
                assert(((op & 0xFF) == 58) || ((op & 0xFF) == 59));
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public EqualExp() {}

            public EqualExp copy() {
                EqualExp that = new EqualExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class InExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  InExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.in_, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public InExp() {}

            public InExp copy() {
                InExp that = new InExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class IdentityExp extends BinExp
        {
            // Erasure: __ctor<byte, Loc, Expression, Expression>
            public  IdentityExp(byte op, Loc loc, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public IdentityExp() {}

            public IdentityExp copy() {
                IdentityExp that = new IdentityExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CmpExp extends BinExp
        {
            // Erasure: __ctor<byte, Loc, Expression, Expression>
            public  CmpExp(byte op, Loc loc, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CmpExp() {}

            public CmpExp copy() {
                CmpExp that = new CmpExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AndExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  AndExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.and, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AndExp() {}

            public AndExp copy() {
                AndExp that = new AndExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class XorExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  XorExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.xor, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public XorExp() {}

            public XorExp copy() {
                XorExp that = new XorExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class OrExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  OrExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.or, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public OrExp() {}

            public OrExp copy() {
                OrExp that = new OrExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class LogicalExp extends BinExp
        {
            // Erasure: __ctor<Loc, byte, Expression, Expression>
            public  LogicalExp(Loc loc, byte op, Expression e1, Expression e2) {
                super(loc, op, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public LogicalExp() {}

            public LogicalExp copy() {
                LogicalExp that = new LogicalExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CondExp extends BinExp
        {
            public Expression econd = null;
            // Erasure: __ctor<Loc, Expression, Expression, Expression>
            public  CondExp(Loc loc, Expression econd, Expression e1, Expression e2) {
                super(loc, TOK.question, 36, e1, e2);
                this.econd = econd;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CondExp() {}

            public CondExp copy() {
                CondExp that = new CondExp();
                that.econd = this.econd;
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AssignExp extends BinExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  AssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.assign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AssignExp() {}

            public AssignExp copy() {
                AssignExp that = new AssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class BinAssignExp extends BinExp
        {
            // Erasure: __ctor<Loc, byte, int, Expression, Expression>
            public  BinAssignExp(Loc loc, byte op, int size, Expression e1, Expression e2) {
                super(loc, op, size, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public BinAssignExp() {}

            public BinAssignExp copy() {
                BinAssignExp that = new BinAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AddAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  AddAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.addAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AddAssignExp() {}

            public AddAssignExp copy() {
                AddAssignExp that = new AddAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class MinAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  MinAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.minAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MinAssignExp() {}

            public MinAssignExp copy() {
                MinAssignExp that = new MinAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class MulAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  MulAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.mulAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public MulAssignExp() {}

            public MulAssignExp copy() {
                MulAssignExp that = new MulAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DivAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  DivAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.divAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DivAssignExp() {}

            public DivAssignExp copy() {
                DivAssignExp that = new DivAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ModAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ModAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.modAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ModAssignExp() {}

            public ModAssignExp copy() {
                ModAssignExp that = new ModAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class PowAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  PowAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.powAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public PowAssignExp() {}

            public PowAssignExp copy() {
                PowAssignExp that = new PowAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class AndAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  AndAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.andAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public AndAssignExp() {}

            public AndAssignExp copy() {
                AndAssignExp that = new AndAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class OrAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  OrAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.orAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public OrAssignExp() {}

            public OrAssignExp copy() {
                OrAssignExp that = new OrAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class XorAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  XorAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.xorAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public XorAssignExp() {}

            public XorAssignExp copy() {
                XorAssignExp that = new XorAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ShlAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ShlAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.leftShiftAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShlAssignExp() {}

            public ShlAssignExp copy() {
                ShlAssignExp that = new ShlAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class ShrAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  ShrAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.rightShiftAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ShrAssignExp() {}

            public ShrAssignExp copy() {
                ShrAssignExp that = new ShrAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class UshrAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  UshrAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.unsignedRightShiftAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public UshrAssignExp() {}

            public UshrAssignExp copy() {
                UshrAssignExp that = new UshrAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class CatAssignExp extends BinAssignExp
        {
            // Erasure: __ctor<Loc, Expression, Expression>
            public  CatAssignExp(Loc loc, Expression e1, Expression e2) {
                super(loc, TOK.concatenateAssign, 32, e1, e2);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public CatAssignExp() {}

            public CatAssignExp copy() {
                CatAssignExp that = new CatAssignExp();
                that.e1 = this.e1;
                that.e2 = this.e2;
                that.op = this.op;
                that.size = this.size;
                that.parens = this.parens;
                that.type = this.type;
                that.loc = this.loc;
                return that;
            }
        }
        public static class TemplateParameter extends ASTNode
        {
            public Loc loc = new Loc();
            public Identifier ident = null;
            // Erasure: __ctor<Loc, Identifier>
            public  TemplateParameter(Loc loc, Identifier ident) {
                super();
                this.loc.opAssign(loc.copy());
                this.ident = ident;
            }

            // Erasure: syntaxCopy<>
            public  TemplateParameter syntaxCopy() {
                return null;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateParameter() {}

            public TemplateParameter copy() {
                TemplateParameter that = new TemplateParameter();
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateAliasParameter extends TemplateParameter
        {
            public Type specType = null;
            public RootObject specAlias = null;
            public RootObject defaultAlias = null;
            // Erasure: __ctor<Loc, Identifier, Type, RootObject, RootObject>
            public  TemplateAliasParameter(Loc loc, Identifier ident, Type specType, RootObject specAlias, RootObject defaultAlias) {
                super(loc, ident);
                this.ident = ident;
                this.specType = specType;
                this.specAlias = specAlias;
                this.defaultAlias = defaultAlias;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
                return that;
            }
        }
        public static class TemplateTypeParameter extends TemplateParameter
        {
            public Type specType = null;
            public Type defaultType = null;
            // Erasure: __ctor<Loc, Identifier, Type, Type>
            public  TemplateTypeParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
                super(loc, ident);
                this.ident = ident;
                this.specType = specType;
                this.defaultType = defaultType;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateTypeParameter() {}

            public TemplateTypeParameter copy() {
                TemplateTypeParameter that = new TemplateTypeParameter();
                that.specType = this.specType;
                that.defaultType = this.defaultType;
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateTupleParameter extends TemplateParameter
        {
            // Erasure: __ctor<Loc, Identifier>
            public  TemplateTupleParameter(Loc loc, Identifier ident) {
                super(loc, ident);
                this.ident = ident;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateTupleParameter() {}

            public TemplateTupleParameter copy() {
                TemplateTupleParameter that = new TemplateTupleParameter();
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static class TemplateValueParameter extends TemplateParameter
        {
            public Type valType = null;
            public Expression specValue = null;
            public Expression defaultValue = null;
            // Erasure: __ctor<Loc, Identifier, Type, Expression, Expression>
            public  TemplateValueParameter(Loc loc, Identifier ident, Type valType, Expression specValue, Expression defaultValue) {
                super(loc, ident);
                this.ident = ident;
                this.valType = valType;
                this.specValue = specValue;
                this.defaultValue = defaultValue;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
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
                return that;
            }
        }
        public static class TemplateThisParameter extends TemplateTypeParameter
        {
            // Erasure: __ctor<Loc, Identifier, Type, Type>
            public  TemplateThisParameter(Loc loc, Identifier ident, Type specType, Type defaultType) {
                super(loc, ident, specType, defaultType);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public TemplateThisParameter() {}

            public TemplateThisParameter copy() {
                TemplateThisParameter that = new TemplateThisParameter();
                that.specType = this.specType;
                that.defaultType = this.defaultType;
                that.loc = this.loc;
                that.ident = this.ident;
                return that;
            }
        }
        public static abstract class Condition extends ASTNode
        {
            public Loc loc = new Loc();
            // Erasure: __ctor<Loc>
            public  Condition(Loc loc) {
                super();
                this.loc.opAssign(loc.copy());
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Condition() {}

            public abstract Condition copy();
        }
        public static class StaticForeach extends RootObject
        {
            public Loc loc = new Loc();
            public ForeachStatement aggrfe = null;
            public ForeachRangeStatement rangefe = null;
            // Erasure: __ctor<Loc, ForeachStatement, ForeachRangeStatement>
            public  StaticForeach(Loc loc, ForeachStatement aggrfe, ForeachRangeStatement rangefe) {
                super();
                this.loc.opAssign(loc.copy());
                this.aggrfe = aggrfe;
                this.rangefe = rangefe;
            }


            public StaticForeach() {}

            public StaticForeach copy() {
                StaticForeach that = new StaticForeach();
                that.loc = this.loc;
                that.aggrfe = this.aggrfe;
                that.rangefe = this.rangefe;
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

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StaticIfCondition() {}

            public StaticIfCondition copy() {
                StaticIfCondition that = new StaticIfCondition();
                that.exp = this.exp;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DVCondition extends Condition
        {
            public int level = 0;
            public Identifier ident = null;
            public Module mod = null;
            // Erasure: __ctor<Module, int, Identifier>
            public  DVCondition(Module mod, int level, Identifier ident) {
                super(Loc.initial);
                this.mod = mod;
                this.ident = ident;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DVCondition() {}

            public DVCondition copy() {
                DVCondition that = new DVCondition();
                that.level = this.level;
                that.ident = this.ident;
                that.mod = this.mod;
                that.loc = this.loc;
                return that;
            }
        }
        public static class DebugCondition extends DVCondition
        {
            // Erasure: __ctor<Module, int, Identifier>
            public  DebugCondition(Module mod, int level, Identifier ident) {
                super(mod, level, ident);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public DebugCondition() {}

            public DebugCondition copy() {
                DebugCondition that = new DebugCondition();
                that.level = this.level;
                that.ident = this.ident;
                that.mod = this.mod;
                that.loc = this.loc;
                return that;
            }
        }
        public static class VersionCondition extends DVCondition
        {
            // Erasure: __ctor<Module, int, Identifier>
            public  VersionCondition(Module mod, int level, Identifier ident) {
                super(mod, level, ident);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VersionCondition() {}

            public VersionCondition copy() {
                VersionCondition that = new VersionCondition();
                that.level = this.level;
                that.ident = this.ident;
                that.mod = this.mod;
                that.loc = this.loc;
                return that;
            }
        }

        public static class InitKind 
        {
            public static final byte void_ = (byte)0;
            public static final byte error = (byte)1;
            public static final byte struct_ = (byte)2;
            public static final byte array = (byte)3;
            public static final byte exp = (byte)4;
        }

        public static class Initializer extends ASTNode
        {
            public Loc loc = new Loc();
            public byte kind = 0;
            // Erasure: __ctor<Loc, byte>
            public  Initializer(Loc loc, byte kind) {
                super();
                this.loc.opAssign(loc.copy());
                this.kind = kind;
            }

            // Erasure: toExpression<Type>
            public  Expression toExpression(Type t) {
                return null;
            }

            // defaulted all parameters starting with #1
            public  Expression toExpression() {
                return toExpression((Type)null);
            }

            // Erasure: isExpInitializer<>
            public  ExpInitializer isExpInitializer() {
                return ((this.kind & 0xFF) == 4) ? ((ExpInitializer)this) : null;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public Initializer() {}

            public Initializer copy() {
                Initializer that = new Initializer();
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class ExpInitializer extends Initializer
        {
            public Expression exp = null;
            // Erasure: __ctor<Loc, Expression>
            public  ExpInitializer(Loc loc, Expression exp) {
                super(loc, InitKind.exp);
                this.exp = exp;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ExpInitializer() {}

            public ExpInitializer copy() {
                ExpInitializer that = new ExpInitializer();
                that.exp = this.exp;
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class StructInitializer extends Initializer
        {
            public DArray<Identifier> field = new DArray<Identifier>();
            public DArray<Initializer> value = new DArray<Initializer>();
            // Erasure: __ctor<Loc>
            public  StructInitializer(Loc loc) {
                super(loc, InitKind.struct_);
            }

            // Erasure: addInit<Identifier, Initializer>
            public  void addInit(Identifier field, Initializer value) {
                this.field.push(field);
                this.value.push(value);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public StructInitializer() {}

            public StructInitializer copy() {
                StructInitializer that = new StructInitializer();
                that.field = this.field;
                that.value = this.value;
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class ArrayInitializer extends Initializer
        {
            public DArray<Expression> index = new DArray<Expression>();
            public DArray<Initializer> value = new DArray<Initializer>();
            public int dim = 0;
            public Type type = null;
            // Erasure: __ctor<Loc>
            public  ArrayInitializer(Loc loc) {
                super(loc, InitKind.array);
            }

            // Erasure: addInit<Expression, Initializer>
            public  void addInit(Expression index, Initializer value) {
                this.index.push(index);
                this.value.push(value);
                this.dim = 0;
                this.type = null;
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public ArrayInitializer() {}

            public ArrayInitializer copy() {
                ArrayInitializer that = new ArrayInitializer();
                that.index = this.index;
                that.value = this.value;
                that.dim = this.dim;
                that.type = this.type;
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class VoidInitializer extends Initializer
        {
            // Erasure: __ctor<Loc>
            public  VoidInitializer(Loc loc) {
                super(loc, InitKind.void_);
            }

            // Erasure: accept<ParseTimeVisitor>
            public  void accept(ParseTimeVisitorASTBase v) {
                v.visit(this);
            }


            public VoidInitializer() {}

            public VoidInitializer copy() {
                VoidInitializer that = new VoidInitializer();
                that.loc = this.loc;
                that.kind = this.kind;
                return that;
            }
        }
        public static class Tuple extends RootObject
        {
            public DArray<RootObject> objects = new DArray<RootObject>();
            // Erasure: dyncast<>
            public  int dyncast() {
                return DYNCAST.tuple;
            }

            // Erasure: toChars<>
            public  BytePtr toChars() {
                return this.objects.toChars();
            }

            // Erasure: __ctor<>
            public  Tuple() {
                super();
            }


            public Tuple copy() {
                Tuple that = new Tuple();
                that.objects = this.objects;
                return that;
            }
        }
        public static class BaseClass
        {
            public Type type = null;
            public BaseClass(){ }
            public BaseClass copy(){
                BaseClass r = new BaseClass();
                r.type = type;
                return r;
            }
            public BaseClass(Type type) {
                this.type = type;
            }

            public BaseClass opAssign(BaseClass that) {
                this.type = that.type;
                return this;
            }
        }
        public static class ModuleDeclaration
        {
            public Loc loc = new Loc();
            public Identifier id = null;
            public DArray<Identifier> packages = null;
            public boolean isdeprecated = false;
            public Expression msg = null;
            // Erasure: __ctor<Loc, Ptr, Identifier, Expression, boolean>
            public  ModuleDeclaration(Loc loc, DArray<Identifier> packages, Identifier id, Expression msg, boolean isdeprecated) {
                this.loc.opAssign(loc.copy());
                this.packages = pcopy(packages);
                this.id = id;
                this.msg = msg;
                this.isdeprecated = isdeprecated;
            }

            // Erasure: toChars<>
            public  BytePtr toChars() {
                OutBuffer buf = new OutBuffer();
                try {
                    if ((this.packages != null) && ((this.packages).length != 0))
                    {
                        {
                            int i = 0;
                            for (; (i < (this.packages).length);i++){
                                Identifier pid = (this.packages).get(i);
                                buf.writestring(pid.asString());
                                buf.writeByte(46);
                            }
                        }
                    }
                    buf.writestring(this.id.asString());
                    return buf.extractChars();
                }
                finally {
                }
            }

            public ModuleDeclaration(){ }
            public ModuleDeclaration copy(){
                ModuleDeclaration r = new ModuleDeclaration();
                r.loc = loc.copy();
                r.id = id;
                r.packages = packages;
                r.isdeprecated = isdeprecated;
                r.msg = msg;
                return r;
            }
            public ModuleDeclaration opAssign(ModuleDeclaration that) {
                this.loc = that.loc;
                this.id = that.id;
                this.packages = that.packages;
                this.isdeprecated = that.isdeprecated;
                this.msg = that.msg;
                return this;
            }
        }
        public static class Prot
        {

            public static class Kind 
            {
                public static final int undefined = 0;
                public static final int none = 1;
                public static final int private_ = 2;
                public static final int package_ = 3;
                public static final int protected_ = 4;
                public static final int public_ = 5;
                public static final int export_ = 6;
            }

            public int kind = 0;
            public Package pkg = null;
            public Prot(){ }
            public Prot copy(){
                Prot r = new Prot();
                r.kind = kind;
                r.pkg = pkg;
                return r;
            }
            public Prot(int kind, Package pkg) {
                this.kind = kind;
                this.pkg = pkg;
            }

            public Prot opAssign(Prot that) {
                this.kind = that.kind;
                this.pkg = that.pkg;
                return this;
            }
        }
        public static class Scope
        {
            public Scope(){ }
            public Scope copy(){
                Scope r = new Scope();
                return r;
            }
            public Scope opAssign(Scope that) {
                return this;
            }
        }
        // Erasure: isTuple<RootObject>
        public static Tuple isTuple(RootObject o) {
            if ((o == null) || (o.dyncast() != DYNCAST.tuple))
            {
                return null;
            }
            return ((Tuple)o);
        }

        // Erasure: isType<RootObject>
        public static Type isType(RootObject o) {
            if ((o == null) || (o.dyncast() != DYNCAST.type))
            {
                return null;
            }
            return ((Type)o);
        }

        // Erasure: isExpression<RootObject>
        public static Expression isExpression(RootObject o) {
            if ((o == null) || (o.dyncast() != DYNCAST.expression))
            {
                return null;
            }
            return ((Expression)o);
        }

        // Erasure: isTemplateParameter<RootObject>
        public static TemplateParameter isTemplateParameter(RootObject o) {
            if ((o == null) || (o.dyncast() != DYNCAST.templateparameter))
            {
                return null;
            }
            return ((TemplateParameter)o);
        }

        // Erasure: protectionToChars<int>
        public static BytePtr protectionToChars(int kind) {
            switch (kind)
            {
                case Prot.Kind.undefined:
                    return null;
                case Prot.Kind.none:
                    return new BytePtr("none");
                case Prot.Kind.private_:
                    return new BytePtr("private");
                case Prot.Kind.package_:
                    return new BytePtr("package");
                case Prot.Kind.protected_:
                    return new BytePtr("protected");
                case Prot.Kind.public_:
                    return new BytePtr("public");
                case Prot.Kind.export_:
                    return new BytePtr("export");
                default:
                throw SwitchError.INSTANCE;
            }
        }

        // Erasure: stcToBuffer<Ptr, long>
        public static boolean stcToBuffer(OutBuffer buf, long stc) {
            Ref<Long> stc_ref = ref(stc);
            boolean result = false;
            if (((stc_ref.value & 17592186568704L) == 17592186568704L))
            {
                stc_ref.value &= -524289L;
            }
            for (; stc_ref.value != 0;){
                BytePtr p = pcopy(stcToChars(stc_ref));
                if (p == null)
                {
                    break;
                }
                if (!result)
                {
                    result = true;
                }
                else
                {
                    (buf).writeByte(32);
                }
                (buf).writestring(p);
            }
            return result;
        }

        // Erasure: typeToExpression<Type>
        public static Expression typeToExpression(Type t) {
            return t.toExpression();
        }

        // Erasure: stcToChars<long>
        public static BytePtr stcToChars(Ref<Long> stc) {
            {
                int i = 0;
                for (; astbase.stcToCharstable.get(i).stc != 0;i++){
                    long tbl = astbase.stcToCharstable.get(i).stc;
                    assert((tbl & 22196369506207L) != 0);
                    if ((stc.value & tbl) != 0)
                    {
                        stc.value &= ~tbl;
                        if ((tbl == 134217728L))
                        {
                            return new BytePtr("__thread");
                        }
                        byte tok = astbase.stcToCharstable.get(i).tok;
                        if (((tok & 0xFF) == 225))
                        {
                            return astbase.stcToCharstable.get(i).id;
                        }
                        else
                        {
                            return Token.toChars(tok);
                        }
                    }
                }
            }
            return null;
        }

        // Erasure: linkageToChars<int>
        public static BytePtr linkageToChars(int linkage) {
            switch (linkage)
            {
                case LINK.default_:
                case LINK.system:
                    return null;
                case LINK.d:
                    return new BytePtr("D");
                case LINK.c:
                    return new BytePtr("C");
                case LINK.cpp:
                    return new BytePtr("C++");
                case LINK.windows:
                    return new BytePtr("Windows");
                case LINK.pascal:
                    return new BytePtr("Pascal");
                case LINK.objc:
                    return new BytePtr("Objective-C");
                default:
                throw SwitchError.INSTANCE;
            }
        }

        public static class Target
        {
            public static int ptrsize = 0;
            // Erasure: va_listType<>
            public static Type va_listType() {
                if (global.params.isWindows)
                {
                    return Type.tchar.pointerTo();
                }
                else if (global.params.isLinux || global.params.isFreeBSD || global.params.isOpenBSD || global.params.isDragonFlyBSD || global.params.isSolaris || global.params.isOSX)
                {
                    if (global.params.is64bit)
                    {
                        return (new TypeIdentifier(Loc.initial, Identifier.idPool(new ByteSlice("__va_list_tag")))).pointerTo();
                    }
                    else
                    {
                        return Type.tchar.pointerTo();
                    }
                }
                else
                {
                    throw new AssertionError("Unreachable code!");
                }
            }

            public Target(){ }
            public Target copy(){
                Target r = new Target();
                return r;
            }
            public Target opAssign(Target that) {
                return this;
            }
        }
        public ASTBase(){ }
        public ASTBase copy(){
            ASTBase r = new ASTBase();
            return r;
        }
        public ASTBase opAssign(ASTBase that) {
            return this;
        }
    }
}
