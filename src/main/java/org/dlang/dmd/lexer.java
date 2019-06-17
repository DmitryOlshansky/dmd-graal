package org.dlang.dmd;

import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;

import static org.dlang.dmd.root.filename.*;

import static org.dlang.dmd.root.File.*;

import static org.dlang.dmd.root.ShimsKt.*;

import static org.dlang.dmd.utils.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.entity.*;
import static org.dlang.dmd.errors.*;
import static org.dlang.dmd.globals.*;
import static org.dlang.dmd.id.*;
import static org.dlang.dmd.identifier.*;
import static org.dlang.dmd.tokens.*;
import static org.dlang.dmd.utf.*;

public class lexer {
    private static final byte[][] initializer_0 = {{(byte)0}, {(byte)39, (byte)0}, {(byte)39, (byte)26}, {(byte)123, (byte)123, (byte)113, (byte)123, (byte)0}, {(byte)255, (byte)0}, {(byte)255, (byte)128, (byte)0}, {(byte)255, (byte)255, (byte)0}, {(byte)255, (byte)255, (byte)0}, {(byte)120, (byte)34, (byte)26}};
    static Slice<ByteSlice> __unittest_L168_C1testcases = slice(initializer_0);
    static boolean scaninitdone = false;
    static ByteSlice scandate = new ByteSlice(new byte[12]);
    static ByteSlice scantime = new ByteSlice(new byte[9]);
    static ByteSlice scantimestamp = new ByteSlice(new byte[25]);

    private static class FLAGS 
    {
        public static final int none = 0;
        public static final int decimal = 1;
        public static final int unsigned = 2;
        public static final int long_ = 4;
    }


    static int LS = 8232;
    static int PS = 8233;
    static ByteSlice cmtable = slice(new byte[]{(byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)56, (byte)32, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)63, (byte)62, (byte)62, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)38, (byte)46, (byte)38, (byte)38, (byte)62, (byte)62, (byte)36, (byte)36, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)44, (byte)36, (byte)36, (byte)32, (byte)0, (byte)32, (byte)32, (byte)60, (byte)32, (byte)38, (byte)46, (byte)38, (byte)38, (byte)62, (byte)62, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)36, (byte)36, (byte)60, (byte)36, (byte)36, (byte)44, (byte)36, (byte)36, (byte)32, (byte)32, (byte)32, (byte)32, (byte)32, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0, (byte)0});
    static int CMoctal = 1;
    static int CMhex = 2;
    static int CMidchar = 4;
    static int CMzerosecond = 8;
    static int CMdigitsecond = 16;
    static int CMsinglechar = 32;
    public static boolean isoctal(byte c) {
        return (cmtable.get(c) & (byte)1) != (byte)0;
    }

    public static boolean ishex(byte c) {
        return (cmtable.get(c) & (byte)2) != (byte)0;
    }

    public static boolean isidchar(byte c) {
        return (cmtable.get(c) & (byte)4) != (byte)0;
    }

    public static boolean isZeroSecond(byte c) {
        return (cmtable.get(c) & (byte)8) != (byte)0;
    }

    public static boolean isDigitSecond(byte c) {
        return (cmtable.get(c) & (byte)16) != (byte)0;
    }

    public static boolean issinglechar(byte c) {
        return (cmtable.get(c) & (byte)32) != (byte)0;
    }

    public static boolean c_isxdigit(int c) {
        return c >= 48 && c <= 57 || c >= 97 && c <= 102 || c >= 65 && c <= 70;
    }

    public static boolean c_isalnum(int c) {
        return c >= 48 && c <= 57 || c >= 97 && c <= 122 || c >= 65 && c <= 90;
    }

    public static void test_0() {
        ByteSlice text =  new ByteSlice("int");
        StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
        Lexer lex1 = new Lexer(null, text.toBytePtr(), 0, text.getLength(), false, false, diagnosticReporter);
        byte tok = TOK.reserved;
        tok = lex1.nextToken();
        assert(tok == (byte)133);
        tok = lex1.nextToken();
        assert(tok == (byte)11);
        tok = lex1.nextToken();
        assert(tok == (byte)11);
        tok = lex1.nextToken();
        assert(tok == (byte)11);
    }
    public static void test_1() {
        int errors = global.startGagging();
        Slice<ByteSlice> testcases = lexer.__unittest_L168_C1testcases;
        {
            Slice<ByteSlice> __r51 = testcases;
            int __key52 = 0;
            for (; __key52 < __r51.getLength();__key52 += 1) {
                ByteSlice testcase = __r51.get(__key52);
                StderrDiagnosticReporter diagnosticReporter = new StderrDiagnosticReporter(global.params.useDeprecated);
                Lexer lex2 = new Lexer(null, testcase.toBytePtr(), 0, testcase.getLength() - 1, false, false, diagnosticReporter);
                byte tok = lex2.nextToken();
                int iterations = 1;
                for (; tok != (byte)11 && iterations++ < testcase.getLength();){
                    tok = lex2.nextToken();
                }
                assert(tok == (byte)11);
                tok = lex2.nextToken();
                assert(tok == (byte)11);
            }
        }
        global.endGagging(errors);
    }
    public static class Lexer extends Object
    {
        public static OutBuffer stringbuffer;
        public Loc scanloc;
        public Loc prevloc;
        public BytePtr p;
        public Token token;
        public BytePtr base;
        public BytePtr end;
        public BytePtr line;
        public boolean doDocComment;
        public boolean anyToken;
        public boolean commentToken;
        public int lastDocLine;
        public DiagnosticReporter diagnosticReporter;
        public Token tokenFreelist;
        public  Lexer(BytePtr filename, BytePtr base, int begoffset, int endoffset, boolean doDocComment, boolean commentToken, DiagnosticReporter diagnosticReporter) {
            {
                assert(diagnosticReporter != null);
            }
            {
                {
                    assert(diagnosticReporter != null);
                }
            }
            this.diagnosticReporter = diagnosticReporter;
            comma(null, new Loc(filename, 1, 1));
            this.token = new Token();
            this.base = base;
            this.end = base.plus(endoffset * 1);
            this.p = base.plus(begoffset * 1);
            this.line = this.p;
            this.doDocComment = doDocComment;
            this.commentToken = commentToken;
            this.lastDocLine = 0;
            if (this.p != null && this.p.get(0) == (byte)35 && this.p.get(1) == (byte)33)
            {
                this.p.plusAssign(2);
                for (; (1) != 0;){
                    byte c = this.p.postInc().get(0);
                    switch ((int)c)
                    {
                        case (byte)0:
                        case (byte)26:
                            this.p.postDec();
                        case (byte)10:
                            break;
                        default:
                        {
                            continue;
                        }
                    }
                    break;
                }
                this.endOfLine();
            }
        }

        public  boolean errors() {
            return this.diagnosticReporter.errorCount() > 0;
        }

        public  Token allocateToken() {
            if (this.tokenFreelist != null)
            {
                Token t = this.tokenFreelist;
                this.tokenFreelist = (t).next;
                (t).next = null;
                return t;
            }
            return new Token();
        }

        public  void releaseToken(Token token) {
            (token).next = this.tokenFreelist;
            this.tokenFreelist = token;
        }

        public  byte nextToken() {
            this.prevloc = this.token.loc;
            if (this.token.next != null)
            {
                Token t = this.token.next;
                memcpy(this.token, t, 48);
                this.releaseToken(t);
            }
            else
            {
                this.scan(this.token);
            }
            return this.token.value;
        }

        public  byte peekNext() {
            return (this.peek(this.token)).value;
        }

        public  byte peekNext2() {
            Token t = this.peek(this.token);
            return (this.peek(t)).value;
        }

        public  void scan(Token t) {
            int lastLine = this.scanloc.linnum;
            Loc startLoc = new Loc();
            (t).blockComment = null;
            (t).lineComment = null;
            for (; (1) != 0;){
                (t).ptr = this.p;
                (t).loc = this.loc();
                dispatched_1:
                do {
                    int __dispatch1 = 0;
                    switch (__dispatch1 != 0 ? __dispatch1 : (int)this.p.get(0))
                    {
                        case (byte)0:
                        case (byte)26:
                            (t).value = TOK.endOfFile;
                            return ;
                        case (byte)32:
                        case (byte)9:
                        case (byte)11:
                        case (byte)12:
                            this.p.postInc();
                            continue;
                        case (byte)13:
                            this.p.postInc();
                            if (this.p.get(0) != (byte)10)
                                this.endOfLine();
                            continue;
                        case (byte)10:
                            this.p.postInc();
                            this.endOfLine();
                            continue;
                        case (byte)48:
                            if (!(isZeroSecond(this.p.get(1))))
                            {
                                this.p.plusAssign(1);
                                (t).unsvalue = 0L;
                                (t).value = TOK.int32Literal;
                                return ;
                            }
                            /*goto Lnumber*/{ __dispatch1 = -1; continue dispatched_1; }
                        case (byte)49:
                        case (byte)50:
                        case (byte)51:
                        case (byte)52:
                        case (byte)53:
                        case (byte)54:
                        case (byte)55:
                        case (byte)56:
                        case (byte)57:
                            if (!(isDigitSecond(this.p.get(1))))
                            {
                                (t).unsvalue = (long)(this.p.get(0) - (byte)48);
                                this.p.plusAssign(1);
                                (t).value = TOK.int32Literal;
                                return ;
                            }
                        /*Lnumber:*/
                        case -1:
                            (t).value = this.number(t);
                            return ;
                        case (byte)39:
                            if (issinglechar(this.p.get(1)) && this.p.get(2) == (byte)39)
                            {
                                (t).unsvalue = (long)this.p.get(1);
                                (t).value = TOK.charLiteral;
                                this.p.plusAssign(3);
                            }
                            else
                                (t).value = this.charConstant(t);
                            return ;
                        case (byte)114:
                            if (this.p.get(1) != (byte)34)
                                /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                            this.p.postInc();
                            /*goto case*/{ __dispatch1 = 96; continue dispatched_1; }
                        case (byte)96:
                            this.wysiwygStringConstant(t);
                            return ;
                        case (byte)120:
                            if (this.p.get(1) != (byte)34)
                                /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                            this.p.postInc();
                            BytePtr start = this.p;
                            OutBuffer hexString = new OutBuffer(null, 0, 0, 0, false, false);
                            (t).value = this.hexStringConstant(t);
                            (hexString).write(start, ((this.p.minus(start)) / 1));
                            this.error(new BytePtr("Built-in hex string literals are obsolete, use `std.conv.hexString!%s` instead."), (hexString).extractChars());
                            return ;
                        case (byte)113:
                            if (this.p.get(1) == (byte)34)
                            {
                                this.p.postInc();
                                this.delimitedStringConstant(t);
                                return ;
                            }
                            else if (this.p.get(1) == (byte)123)
                            {
                                this.p.postInc();
                                this.tokenStringConstant(t);
                                return ;
                            }
                            else
                                /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                        case (byte)34:
                            this.escapeStringConstant(t);
                            return ;
                        case (byte)97:
                        case (byte)98:
                        case (byte)99:
                        case (byte)100:
                        case (byte)101:
                        case (byte)102:
                        case (byte)103:
                        case (byte)104:
                        case (byte)105:
                        case (byte)106:
                        case (byte)107:
                        case (byte)108:
                        case (byte)109:
                        case (byte)110:
                        case (byte)111:
                        case (byte)112:
                        case (byte)115:
                        case (byte)116:
                        case (byte)117:
                        case (byte)118:
                        case (byte)119:
                        case (byte)121:
                        case (byte)122:
                        case (byte)65:
                        case (byte)66:
                        case (byte)67:
                        case (byte)68:
                        case (byte)69:
                        case (byte)70:
                        case (byte)71:
                        case (byte)72:
                        case (byte)73:
                        case (byte)74:
                        case (byte)75:
                        case (byte)76:
                        case (byte)77:
                        case (byte)78:
                        case (byte)79:
                        case (byte)80:
                        case (byte)81:
                        case (byte)82:
                        case (byte)83:
                        case (byte)84:
                        case (byte)85:
                        case (byte)86:
                        case (byte)87:
                        case (byte)88:
                        case (byte)89:
                        case (byte)90:
                        case (byte)95:
                        /*case_ident:*/
                        case -2:
                            {
                                for (; (1) != 0;){
                                    byte c = (this.p.plusAssign(1)).get(0);
                                    if (isidchar(c))
                                        continue;
                                    else if ((c & (byte)128) != 0)
                                    {
                                        BytePtr s = this.p;
                                        int u = this.decodeUTF();
                                        if (isUniAlpha(u))
                                            continue;
                                        this.error(new BytePtr("char 0x%04x not allowed in identifier"), u);
                                        this.p = s;
                                    }
                                    break;
                                }
                                Identifier id = Identifier.idPool((t).ptr, ((this.p.minus((t).ptr)) / 1));
                                (t).ident = id;
                                (t).value = (byte)id.getValue();
                                this.anyToken = true;
                                if ((t).ptr.get(0) == (byte)95)
                                {
                                    boolean initdone = lexer.scaninitdone;
                                    ByteSlice date = lexer.scandate;
                                    ByteSlice time = lexer.scantime;
                                    ByteSlice timestamp = lexer.scantimestamp;
                                    if (!(initdone))
                                    {
                                        initdone = true;
                                        int ct = 0;
                                        time(ptr(ct));
                                        BytePtr p = ctime(ptr(ct));
                                        assert(p != null);
                                        sprintf(ptr(date),  new ByteSlice("%.6s %.4s"), p.plus(4), p.plus(20));
                                        sprintf(ptr(time),  new ByteSlice("%.8s"), p.plus(11));
                                        sprintf(ptr(timestamp),  new ByteSlice("%.24s"), p);
                                    }
                                    if (id.equals(Id.DATE))
                                    {
                                        (t).ustring = ptr(date);
                                        /*goto Lstr*/
                                        (t).value = TOK.string_;
                                        (t).postfix = (byte)0;
                                        (t).len = strlen((t).ustring);
                                    }
                                    else if (id.equals(Id.TIME))
                                    {
                                        (t).ustring = ptr(time);
                                        /*goto Lstr*/
                                        (t).value = TOK.string_;
                                        (t).postfix = (byte)0;
                                        (t).len = strlen((t).ustring);
                                    }
                                    else if (id.equals(Id.VENDOR))
                                    {
                                        (t).ustring = xarraydup(global.vendor).toBytePtr();
                                        /*goto Lstr*/
                                        (t).value = TOK.string_;
                                        (t).postfix = (byte)0;
                                        (t).len = strlen((t).ustring);
                                    }
                                    else if (id.equals(Id.TIMESTAMP))
                                    {
                                        (t).ustring = ptr(timestamp);
                                    /*Lstr:*/
                                        (t).value = TOK.string_;
                                        (t).postfix = (byte)0;
                                        (t).len = strlen((t).ustring);
                                    }
                                    else if (id.equals(Id.VERSIONX))
                                    {
                                        (t).value = TOK.int64Literal;
                                        (t).unsvalue = (long)global.versionNumber();
                                    }
                                    else if (id.equals(Id.EOFX))
                                    {
                                        (t).value = TOK.endOfFile;
                                        for (; !(this.p.get(0) == (byte)0 || this.p.get(0) == (byte)26);) {
                                            this.p.postInc();
                                        }
                                    }
                                }
                                return ;
                            }
                        case (byte)47:
                            this.p.postInc();
                            switch ((int)this.p.get(0))
                            {
                                case (byte)61:
                                    this.p.postInc();
                                    (t).value = TOK.divAssign;
                                    return ;
                                case (byte)42:
                                    this.p.postInc();
                                    startLoc = this.loc();
                                    for (; (1) != 0;){
                                        for (; (1) != 0;){
                                            byte c = this.p.get(0);
                                            switch ((int)c)
                                            {
                                                case (byte)47:
                                                    break;
                                                case (byte)10:
                                                    this.endOfLine();
                                                    this.p.postInc();
                                                    continue;
                                                case (byte)13:
                                                    this.p.postInc();
                                                    if (this.p.get(0) != (byte)10)
                                                        this.endOfLine();
                                                    continue;
                                                case (byte)0:
                                                case (byte)26:
                                                    this.error(new BytePtr("unterminated /* */ comment"));
                                                    this.p = this.end;
                                                    (t).loc = this.loc();
                                                    (t).value = TOK.endOfFile;
                                                    return ;
                                                default:
                                                {
                                                    if ((c & (byte)128) != 0)
                                                    {
                                                        int u = this.decodeUTF();
                                                        if (u == 8233 || u == 8232)
                                                            this.endOfLine();
                                                    }
                                                    this.p.postInc();
                                                    continue;
                                                }
                                            }
                                            break;
                                        }
                                        this.p.postInc();
                                        if (this.p.get(-2) == (byte)42 && this.p.minus(3) != (t).ptr)
                                            break;
                                    }
                                    if (this.commentToken)
                                    {
                                        (t).loc = startLoc;
                                        (t).value = TOK.comment;
                                        return ;
                                    }
                                    else if (this.doDocComment && (t).ptr.get(2) == (byte)42 && this.p.minus(4) != (t).ptr)
                                    {
                                        this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                        this.lastDocLine = this.scanloc.linnum;
                                    }
                                    continue;
                                case (byte)47:
                                    startLoc = this.loc();
                                    for (; (1) != 0;){
                                        byte c = (this.p.plusAssign(1)).get(0);
                                        switch ((int)c)
                                        {
                                            case (byte)10:
                                                break;
                                            case (byte)13:
                                                if (this.p.get(1) == (byte)10)
                                                    this.p.postInc();
                                                break;
                                            case (byte)0:
                                            case (byte)26:
                                                if (this.commentToken)
                                                {
                                                    this.p = this.end;
                                                    (t).loc = startLoc;
                                                    (t).value = TOK.comment;
                                                    return ;
                                                }
                                                if (this.doDocComment && (t).ptr.get(2) == (byte)47)
                                                {
                                                    this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                                    this.lastDocLine = this.scanloc.linnum;
                                                }
                                                this.p = this.end;
                                                (t).loc = this.loc();
                                                (t).value = TOK.endOfFile;
                                                return ;
                                            default:
                                            {
                                                if ((c & (byte)128) != 0)
                                                {
                                                    int u = this.decodeUTF();
                                                    if (u == 8233 || u == 8232)
                                                        break;
                                                }
                                                continue;
                                            }
                                        }
                                        break;
                                    }
                                    if (this.commentToken)
                                    {
                                        this.p.postInc();
                                        this.endOfLine();
                                        (t).loc = startLoc;
                                        (t).value = TOK.comment;
                                        return ;
                                    }
                                    if (this.doDocComment && (t).ptr.get(2) == (byte)47)
                                    {
                                        this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                        this.lastDocLine = this.scanloc.linnum;
                                    }
                                    this.p.postInc();
                                    this.endOfLine();
                                    continue;
                                case (byte)43:
                                    {
                                        int nest = 0;
                                        startLoc = this.loc();
                                        this.p.postInc();
                                        nest = 1;
                                        for (; (1) != 0;){
                                            byte c = this.p.get(0);
                                            switch ((int)c)
                                            {
                                                case (byte)47:
                                                    this.p.postInc();
                                                    if (this.p.get(0) == (byte)43)
                                                    {
                                                        this.p.postInc();
                                                        nest++;
                                                    }
                                                    continue;
                                                case (byte)43:
                                                    this.p.postInc();
                                                    if (this.p.get(0) == (byte)47)
                                                    {
                                                        this.p.postInc();
                                                        if ((nest -= 1) == 0)
                                                            break;
                                                    }
                                                    continue;
                                                case (byte)13:
                                                    this.p.postInc();
                                                    if (this.p.get(0) != (byte)10)
                                                        this.endOfLine();
                                                    continue;
                                                case (byte)10:
                                                    this.endOfLine();
                                                    this.p.postInc();
                                                    continue;
                                                case (byte)0:
                                                case (byte)26:
                                                    this.error(new BytePtr("unterminated /+ +/ comment"));
                                                    this.p = this.end;
                                                    (t).loc = this.loc();
                                                    (t).value = TOK.endOfFile;
                                                    return ;
                                                default:
                                                {
                                                    if ((c & (byte)128) != 0)
                                                    {
                                                        int u = this.decodeUTF();
                                                        if (u == 8233 || u == 8232)
                                                            this.endOfLine();
                                                    }
                                                    this.p.postInc();
                                                    continue;
                                                }
                                            }
                                            break;
                                        }
                                        if (this.commentToken)
                                        {
                                            (t).loc = startLoc;
                                            (t).value = TOK.comment;
                                            return ;
                                        }
                                        if (this.doDocComment && (t).ptr.get(2) == (byte)43 && this.p.minus(4) != (t).ptr)
                                        {
                                            this.getDocComment(t, ((lastLine == startLoc.linnum) ? 1 : 0), startLoc.linnum - this.lastDocLine > 1);
                                            this.lastDocLine = this.scanloc.linnum;
                                        }
                                        continue;
                                    }
                                default:
                                {
                                    break;
                                }
                            }
                            (t).value = TOK.div;
                            return ;
                        case (byte)46:
                            this.p.postInc();
                            if ((isdigit((int)this.p.get(0))) != 0)
                            {
                                this.p.postDec();
                                (t).value = this.inreal(t);
                            }
                            else if (this.p.get(0) == (byte)46)
                            {
                                if (this.p.get(1) == (byte)46)
                                {
                                    this.p.plusAssign(2);
                                    (t).value = TOK.dotDotDot;
                                }
                                else
                                {
                                    this.p.postInc();
                                    (t).value = TOK.slice;
                                }
                            }
                            else
                                (t).value = TOK.dot;
                            return ;
                        case (byte)38:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.andAssign;
                            }
                            else if (this.p.get(0) == (byte)38)
                            {
                                this.p.postInc();
                                (t).value = TOK.andAnd;
                            }
                            else
                                (t).value = TOK.and;
                            return ;
                        case (byte)124:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.orAssign;
                            }
                            else if (this.p.get(0) == (byte)124)
                            {
                                this.p.postInc();
                                (t).value = TOK.orOr;
                            }
                            else
                                (t).value = TOK.or;
                            return ;
                        case (byte)45:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.minAssign;
                            }
                            else if (this.p.get(0) == (byte)45)
                            {
                                this.p.postInc();
                                (t).value = TOK.minusMinus;
                            }
                            else
                                (t).value = TOK.min;
                            return ;
                        case (byte)43:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.addAssign;
                            }
                            else if (this.p.get(0) == (byte)43)
                            {
                                this.p.postInc();
                                (t).value = TOK.plusPlus;
                            }
                            else
                                (t).value = TOK.add;
                            return ;
                        case (byte)60:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.lessOrEqual;
                            }
                            else if (this.p.get(0) == (byte)60)
                            {
                                this.p.postInc();
                                if (this.p.get(0) == (byte)61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.leftShiftAssign;
                                }
                                else
                                    (t).value = TOK.leftShift;
                            }
                            else
                                (t).value = TOK.lessThan;
                            return ;
                        case (byte)62:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.greaterOrEqual;
                            }
                            else if (this.p.get(0) == (byte)62)
                            {
                                this.p.postInc();
                                if (this.p.get(0) == (byte)61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.rightShiftAssign;
                                }
                                else if (this.p.get(0) == (byte)62)
                                {
                                    this.p.postInc();
                                    if (this.p.get(0) == (byte)61)
                                    {
                                        this.p.postInc();
                                        (t).value = TOK.unsignedRightShiftAssign;
                                    }
                                    else
                                        (t).value = TOK.unsignedRightShift;
                                }
                                else
                                    (t).value = TOK.rightShift;
                            }
                            else
                                (t).value = TOK.greaterThan;
                            return ;
                        case (byte)33:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.notEqual;
                            }
                            else
                                (t).value = TOK.not;
                            return ;
                        case (byte)61:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.equal;
                            }
                            else if (this.p.get(0) == (byte)62)
                            {
                                this.p.postInc();
                                (t).value = TOK.goesTo;
                            }
                            else
                                (t).value = TOK.assign;
                            return ;
                        case (byte)126:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.concatenateAssign;
                            }
                            else
                                (t).value = TOK.tilde;
                            return ;
                        case (byte)94:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)94)
                            {
                                this.p.postInc();
                                if (this.p.get(0) == (byte)61)
                                {
                                    this.p.postInc();
                                    (t).value = TOK.powAssign;
                                }
                                else
                                    (t).value = TOK.pow;
                            }
                            else if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.xorAssign;
                            }
                            else
                                (t).value = TOK.xor;
                            return ;
                        case (byte)40:
                            this.p.postInc();
                            (t).value = TOK.leftParentheses;
                            return ;
                        case (byte)41:
                            this.p.postInc();
                            (t).value = TOK.rightParentheses;
                            return ;
                        case (byte)91:
                            this.p.postInc();
                            (t).value = TOK.leftBracket;
                            return ;
                        case (byte)93:
                            this.p.postInc();
                            (t).value = TOK.rightBracket;
                            return ;
                        case (byte)123:
                            this.p.postInc();
                            (t).value = TOK.leftCurly;
                            return ;
                        case (byte)125:
                            this.p.postInc();
                            (t).value = TOK.rightCurly;
                            return ;
                        case (byte)63:
                            this.p.postInc();
                            (t).value = TOK.question;
                            return ;
                        case (byte)44:
                            this.p.postInc();
                            (t).value = TOK.comma;
                            return ;
                        case (byte)59:
                            this.p.postInc();
                            (t).value = TOK.semicolon;
                            return ;
                        case (byte)58:
                            this.p.postInc();
                            (t).value = TOK.colon;
                            return ;
                        case (byte)36:
                            this.p.postInc();
                            (t).value = TOK.dollar;
                            return ;
                        case (byte)64:
                            this.p.postInc();
                            (t).value = TOK.at;
                            return ;
                        case (byte)42:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.mulAssign;
                            }
                            else
                                (t).value = TOK.mul;
                            return ;
                        case (byte)37:
                            this.p.postInc();
                            if (this.p.get(0) == (byte)61)
                            {
                                this.p.postInc();
                                (t).value = TOK.modAssign;
                            }
                            else
                                (t).value = TOK.mod;
                            return ;
                        case (byte)35:
                            {
                                this.p.postInc();
                                Token n = new Token();
                                this.scan(n);
                                if (n.value == (byte)120)
                                {
                                    if (n.ident.equals(Id.line))
                                    {
                                        this.poundLine();
                                        continue;
                                    }
                                    else
                                    {
                                        Loc locx = this.loc();
                                        this.warning(locx, new BytePtr("C preprocessor directive `#%s` is not supported"), n.ident.toChars());
                                    }
                                }
                                else if (n.value == (byte)183)
                                {
                                    this.error(new BytePtr("C preprocessor directive `#if` is not supported, use `version` or `static if`"));
                                }
                                (t).value = TOK.pound;
                                return ;
                            }
                        default:
                        {
                            {
                                int c = (int)this.p.get(0);
                                if ((c & 128) != 0)
                                {
                                    c = this.decodeUTF();
                                    if (isUniAlpha(c))
                                        /*goto case_ident*/{ __dispatch1 = -2; continue dispatched_1; }
                                    if (c == 8233 || c == 8232)
                                    {
                                        this.endOfLine();
                                        this.p.postInc();
                                        continue;
                                    }
                                }
                                if (c < 128 && (isprint(c)) != 0)
                                    this.error(new BytePtr("character '%c' is not a valid token"), c);
                                else
                                    this.error(new BytePtr("character 0x%02x is not a valid token"), c);
                                this.p.postInc();
                                continue;
                            }
                        }
                    }
                } while(false);
            }
        }

        public  Token peek(Token ct) {
            Token t = null;
            if ((ct).next != null)
                t = (ct).next;
            else
            {
                t = this.allocateToken();
                this.scan(t);
                (ct).next = t;
            }
            return t;
        }

        public  Token peekPastParen(Token tk) {
            int parens = 1;
            int curlynest = 0;
            for (; (1) != 0;){
                tk = this.peek(tk);
                switch ((tk).value)
                {
                    case (byte)1:
                        parens++;
                        continue;
                    case (byte)2:
                        parens -= 1;
                        if ((parens) != 0)
                            continue;
                        tk = this.peek(tk);
                        break;
                    case (byte)5:
                        curlynest++;
                        continue;
                    case (byte)6:
                        if ((curlynest -= 1) >= 0)
                            continue;
                        break;
                    case (byte)9:
                        if ((curlynest) != 0)
                            continue;
                        break;
                    case (byte)11:
                        break;
                    default:
                    {
                        continue;
                    }
                }
                return tk;
            }
        }

        public  int escapeSequence() {
            return Lexer.escapeSequence(this.token.loc, this.diagnosticReporter, this.p);
        }

        public static int escapeSequence(Loc loc, DiagnosticReporter handler, Ref<BytePtr> sequence) {
            {
                assert(handler != null);
            }
            {
                {
                    assert(handler != null);
                }
            }
            BytePtr p = sequence.value;
            try {
                int c = (int)p.get(0);
                int ndigits = 0;
                dispatched_1:
                do {
                    int __dispatch1 = 0;
                    switch (__dispatch1 != 0 ? __dispatch1 : c)
                    {
                        case 39:
                        case 34:
                        case 63:
                        case 92:
                        /*Lconsume:*/
                        case -1:
                            p.postInc();
                            break;
                        case 97:
                            c = 7;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 98:
                            c = 8;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 102:
                            c = 12;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 110:
                            c = 10;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 114:
                            c = 13;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 116:
                            c = 9;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 118:
                            c = 11;
                            /*goto Lconsume*/{ __dispatch1 = -1; continue dispatched_1; }
                        case 117:
                            ndigits = 4;
                            /*goto Lhex*/{ __dispatch1 = -2; continue dispatched_1; }
                        case 85:
                            ndigits = 8;
                            /*goto Lhex*/{ __dispatch1 = -2; continue dispatched_1; }
                        case 120:
                            ndigits = 2;
                        /*Lhex:*/
                        case -2:
                            p.postInc();
                            c = p.get(0);
                            if (ishex((byte)c))
                            {
                                int v = 0;
                                int n = 0;
                                for (; (1) != 0;){
                                    if ((isdigit((int)(byte)c)) != 0)
                                        c -= 48;
                                    else if ((islower(c)) != 0)
                                        c -= 87;
                                    else
                                        c -= 55;
                                    v = v * 16 + c;
                                    c = (p.plusAssign((byte)1)).get(0);
                                    if ((n += 1) == ndigits)
                                        break;
                                    if (!(ishex((byte)c)))
                                    {
                                        handler.error(loc, new BytePtr("escape hex sequence has %d hex digits instead of %d"), n, ndigits);
                                        break;
                                    }
                                }
                                if (ndigits != 2 && !(utf_isValidDchar(v)))
                                {
                                    handler.error(loc, new BytePtr("invalid UTF character \\U%08x"), v);
                                    v = 63;
                                }
                                c = v;
                            }
                            else
                            {
                                handler.error(loc, new BytePtr("undefined escape hex sequence \\%c%c"), sequence.value.get(0), c);
                                p.postInc();
                            }
                            break;
                        case 38:
                            {
                                BytePtr idstart = p.plusAssign(1);
                                for (; (1) != 0;p.postInc()){
                                    switch ((int)p.get(0))
                                    {
                                        case (byte)59:
                                            c = HtmlNamedEntity(idstart, ((p.minus(idstart)) / 1));
                                            if (c == -1)
                                            {
                                                handler.error(loc, new BytePtr("unnamed character entity &%.*s;"), (p.minus(idstart)) / 1, idstart);
                                                c = 63;
                                            }
                                            p.postInc();
                                            break;
                                        default:
                                        {
                                            if ((isalpha((int)p.get(0))) != 0 || p != idstart && (isdigit((int)p.get(0))) != 0)
                                                continue;
                                            handler.error(loc, new BytePtr("unterminated named entity &%.*s;"), (p.minus(idstart)) / 1 + 1, idstart);
                                            c = 63;
                                            break;
                                        }
                                    }
                                    break;
                                }
                            }
                            break;
                        case 0:
                        case 26:
                            c = 92;
                            break;
                        default:
                        {
                            if (isoctal((byte)c))
                            {
                                int v = 0;
                                int n = 0;
                                do
                                {
                                    v = v * 8 + (c - 48);
                                    c = (p.plusAssign((byte)1)).get(0);
                                }
                                while ((n += 1) < 3 && isoctal((byte)c));
                                c = v;
                                if (c > 255)
                                    handler.error(loc, new BytePtr("escape octal sequence \\%03o is larger than \\377"), c);
                            }
                            else
                            {
                                handler.error(loc, new BytePtr("undefined escape sequence \\%c"), c);
                                p.postInc();
                            }
                            break;
                        }
                    }
                } while(false);
                return c;
            }
            finally {
                sequence.value = p;
            }
        }

        public  void wysiwygStringConstant(Token result) {
            (result).value = TOK.string_;
            Loc start = this.loc();
            byte terminator = this.p.get(0);
            this.p.postInc();
            Lexer.stringbuffer.reset();
            for (; (1) != 0;){
                int c = (int)this.p.get(0);
                this.p.postInc();
                switch (c)
                {
                    case 10:
                        this.endOfLine();
                        break;
                    case 13:
                        if (this.p.get(0) == (byte)10)
                            continue;
                        c = '\n';
                        this.endOfLine();
                        break;
                    case 0:
                    case 26:
                        this.error(new BytePtr("unterminated string constant starting at %s"), start.toChars(global.params.showColumns));
                        (result).setString();
                        this.p.postDec();
                        return ;
                    default:
                    {
                        if (c == (int)terminator)
                        {
                            (result).setString(Lexer.stringbuffer);
                            this.stringPostfix(result);
                            return ;
                        }
                        else if ((c & 128) != 0)
                        {
                            this.p.postDec();
                            int u = this.decodeUTF();
                            this.p.postInc();
                            if (u == 8233 || u == 8232)
                                this.endOfLine();
                            Lexer.stringbuffer.writeUTF8(u);
                            continue;
                        }
                        break;
                    }
                }
                Lexer.stringbuffer.writeByte(c);
            }
        }

        public  byte hexStringConstant(Token t) {
            Loc start = this.loc();
            int n = 0;
            int v = -1;
            this.p.postInc();
            Lexer.stringbuffer.reset();
            for (; (1) != 0;){
                int c = (int)this.p.postInc().get(0);
                dispatched_1:
                do {
                    int __dispatch1 = 0;
                    switch (__dispatch1 != 0 ? __dispatch1 : c)
                    {
                        case 32:
                        case 9:
                        case 11:
                        case 12:
                            continue;
                        case 13:
                            if (this.p.get(0) == (byte)10)
                                continue;
                            /*goto case*/{ __dispatch1 = 10; continue dispatched_1; }
                        case 10:
                            this.endOfLine();
                            continue;
                        case 0:
                        case 26:
                            this.error(new BytePtr("unterminated string constant starting at %s"), start.toChars(global.params.showColumns));
                            (t).setString();
                            this.p.postDec();
                            return TOK.hexadecimalString;
                        case 34:
                            if ((n & 1) != 0)
                            {
                                this.error(new BytePtr("odd number (%d) of hex characters in hex string"), n);
                                Lexer.stringbuffer.writeByte(v);
                            }
                            (t).setString(Lexer.stringbuffer);
                            this.stringPostfix(t);
                            return TOK.hexadecimalString;
                        default:
                        {
                            if (c >= 48 && c <= 57)
                                c -= 48;
                            else if (c >= 97 && c <= 102)
                                c -= 87;
                            else if (c >= 65 && c <= 70)
                                c -= 55;
                            else if ((c & 128) != 0)
                            {
                                this.p.postDec();
                                int u = this.decodeUTF();
                                this.p.postInc();
                                if (u == 8233 || u == 8232)
                                    this.endOfLine();
                                else
                                    this.error(new BytePtr("non-hex character \\u%04x in hex string"), u);
                            }
                            else
                                this.error(new BytePtr("non-hex character '%c' in hex string"), c);
                            if ((n & 1) != 0)
                            {
                                v = v << 4 | c;
                                Lexer.stringbuffer.writeByte(v);
                            }
                            else
                                v = c;
                            n++;
                            break;
                        }
                    }
                } while(false);
            }
        }

        public  void delimitedStringConstant(Token result) {
            (result).value = TOK.string_;
            Loc start = this.loc();
            int delimleft = '\u0000';
            int delimright = '\u0000';
            int nest = 1;
            int nestcount = -1;
            Identifier hereid = null;
            int blankrol = 0;
            int startline = 0;
            this.p.postInc();
            Lexer.stringbuffer.reset();
            try {
                for (; (1) != 0;){
                    int c = (int)this.p.postInc().get(0);
                    dispatched_1:
                    do {
                        int __dispatch1 = 0;
                        switch (__dispatch1 != 0 ? __dispatch1 : c)
                        {
                            case 10:
                            /*Lnextline:*/
                            case -1:
                                this.endOfLine();
                                startline = 1;
                                if ((blankrol) != 0)
                                {
                                    blankrol = 0;
                                    continue;
                                }
                                if (hereid != null)
                                {
                                    Lexer.stringbuffer.writeUTF8(c);
                                    continue;
                                }
                                break;
                            case 13:
                                if (this.p.get(0) == (byte)10)
                                    continue;
                                c = '\n';
                                /*goto Lnextline*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 0:
                            case 26:
                                this.error(new BytePtr("unterminated delimited string constant starting at %s"), start.toChars(global.params.showColumns));
                                (result).setString();
                                this.p.postDec();
                                return ;
                            default:
                            {
                                if ((c & 128) != 0)
                                {
                                    this.p.postDec();
                                    c = this.decodeUTF();
                                    this.p.postInc();
                                    if (c == 8233 || c == 8232)
                                        /*goto Lnextline*/{ __dispatch1 = -1; continue dispatched_1; }
                                }
                                break;
                            }
                        }
                    } while(false);
                    if (delimleft == 0)
                    {
                        delimleft = c;
                        nest = 1;
                        nestcount = 1;
                        if (c == 40)
                            delimright = '\u0029';
                        else if (c == 123)
                            delimright = '\u007d';
                        else if (c == 91)
                            delimright = '\u005d';
                        else if (c == 60)
                            delimright = '\u003e';
                        else if ((isalpha(c)) != 0 || c == 95 || c >= 128 && isUniAlpha(c))
                        {
                            Token tok = new Token();
                            this.p.postDec();
                            this.scan(tok);
                            if (tok.value != (byte)120)
                            {
                                this.error(new BytePtr("identifier expected for heredoc, not %s"), tok.toChars());
                                delimright = c;
                            }
                            else
                            {
                                hereid = tok.ident;
                                blankrol = 1;
                            }
                            nest = 0;
                        }
                        else
                        {
                            delimright = c;
                            nest = 0;
                            if ((isspace(c)) != 0)
                                this.error(new BytePtr("delimiter cannot be whitespace"));
                        }
                    }
                    else
                    {
                        if ((blankrol) != 0)
                        {
                            this.error(new BytePtr("heredoc rest of line should be blank"));
                            blankrol = 0;
                            continue;
                        }
                        if (nest == 1)
                        {
                            if (c == delimleft)
                                nestcount++;
                            else if (c == delimright)
                            {
                                nestcount--;
                                if (nestcount == 0)
                                    /*goto Ldone*/throw Dispatch.INSTANCE;
                            }
                        }
                        else if (c == delimright)
                            /*goto Ldone*/throw Dispatch.INSTANCE;
                        if ((startline) != 0 && (isalpha(c)) != 0 || c == 95 || c >= 128 && isUniAlpha(c) && hereid != null)
                        {
                            Token tok = new Token();
                            BytePtr psave = this.p;
                            this.p.postDec();
                            this.scan(tok);
                            if (tok.value == (byte)120 && tok.ident == hereid)
                            {
                                /*goto Ldone*/throw Dispatch.INSTANCE;
                            }
                            this.p = psave;
                        }
                        Lexer.stringbuffer.writeUTF8(c);
                        startline = 0;
                    }
                }
            }
            catch(Dispatch __d){}
        /*Ldone:*/
            if (this.p.get(0) == (byte)34)
                this.p.postInc();
            else if (hereid != null)
                this.error(new BytePtr("delimited string must end in %s\""), hereid.toChars());
            else
                this.error(new BytePtr("delimited string must end in %c\""), delimright);
            (result).setString(Lexer.stringbuffer);
            this.stringPostfix(result);
        }

        public  void tokenStringConstant(Token result) {
            (result).value = TOK.string_;
            int nest = 1;
            Loc start = this.loc();
            BytePtr pstart = this.p.plusAssign(1);
            for (; (1) != 0;){
                Token tok = new Token();
                this.scan(tok);
                switch (tok.value)
                {
                    case (byte)5:
                        nest++;
                        continue;
                    case (byte)6:
                        if ((nest -= 1) == 0)
                        {
                            (result).setString(pstart, ((this.p.minus(1).minus(pstart)) / 1));
                            this.stringPostfix(result);
                            return ;
                        }
                        continue;
                    case (byte)11:
                        this.error(new BytePtr("unterminated token string constant starting at %s"), start.toChars(global.params.showColumns));
                        (result).setString();
                        return ;
                    default:
                    {
                        continue;
                    }
                }
            }
        }

        public  void escapeStringConstant(Token t) {
            (t).value = TOK.string_;
            Loc start = this.loc();
            this.p.postInc();
            Lexer.stringbuffer.reset();
            for (; (1) != 0;){
                int c = (int)this.p.postInc().get(0);
                switch (c)
                {
                    case 92:
                        switch ((int)this.p.get(0))
                        {
                            case (byte)117:
                            case (byte)85:
                            case (byte)38:
                                c = this.escapeSequence();
                                Lexer.stringbuffer.writeUTF8(c);
                                continue;
                            default:
                            {
                                c = this.escapeSequence();
                                break;
                            }
                        }
                        break;
                    case 10:
                        this.endOfLine();
                        break;
                    case 13:
                        if (this.p.get(0) == (byte)10)
                            continue;
                        c = '\n';
                        this.endOfLine();
                        break;
                    case 34:
                        (t).setString(Lexer.stringbuffer);
                        this.stringPostfix(t);
                        return ;
                    case 0:
                    case 26:
                        this.p.postDec();
                        this.error(new BytePtr("unterminated string constant starting at %s"), start.toChars(global.params.showColumns));
                        (t).setString();
                        return ;
                    default:
                    {
                        if ((c & 128) != 0)
                        {
                            this.p.postDec();
                            c = this.decodeUTF();
                            if (c == 8232 || c == 8233)
                            {
                                c = '\n';
                                this.endOfLine();
                            }
                            this.p.postInc();
                            Lexer.stringbuffer.writeUTF8(c);
                            continue;
                        }
                        break;
                    }
                }
                Lexer.stringbuffer.writeByte(c);
            }
        }

        public  byte charConstant(Token t) {
            byte tk = TOK.charLiteral;
            this.p.postInc();
            int c = (int)this.p.postInc().get(0);
            dispatched_1:
            do {
                int __dispatch1 = 0;
                switch (__dispatch1 != 0 ? __dispatch1 : c)
                {
                    case 92:
                        switch ((int)this.p.get(0))
                        {
                            case (byte)117:
                                (t).unsvalue = (long)this.escapeSequence();
                                tk = TOK.wcharLiteral;
                                break;
                            case (byte)85:
                            case (byte)38:
                                (t).unsvalue = (long)this.escapeSequence();
                                tk = TOK.dcharLiteral;
                                break;
                            default:
                            {
                                (t).unsvalue = (long)this.escapeSequence();
                                break;
                            }
                        }
                        break;
                    case 10:
                    /*L1:*/
                    case -1:
                        this.endOfLine();
                    case 13:
                        /*goto case*/{ __dispatch1 = 39; continue dispatched_1; }
                    case 0:
                    case 26:
                        this.p.postDec();
                    case 39:
                        this.error(new BytePtr("unterminated character constant"));
                        (t).unsvalue = 63L;
                        return tk;
                    default:
                    {
                        if ((c & 128) != 0)
                        {
                            this.p.postDec();
                            c = this.decodeUTF();
                            this.p.postInc();
                            if (c == 8232 || c == 8233)
                                /*goto L1*/{ __dispatch1 = -1; continue dispatched_1; }
                            if (c < 55296 || c >= 57344 && c < 65534)
                                tk = TOK.wcharLiteral;
                            else
                                tk = TOK.dcharLiteral;
                        }
                        (t).unsvalue = (long)c;
                        break;
                    }
                }
            } while(false);
            if (this.p.get(0) != (byte)39)
            {
                this.error(new BytePtr("unterminated character constant"));
                (t).unsvalue = 63L;
                return tk;
            }
            this.p.postInc();
            return tk;
        }

        public  void stringPostfix(Token t) {
            switch ((int)this.p.get(0))
            {
                case (byte)99:
                case (byte)119:
                case (byte)100:
                    (t).postfix = (byte)this.p.get(0);
                    this.p.postInc();
                    break;
                default:
                {
                    (t).postfix = (byte)0;
                    break;
                }
            }
        }

        public  byte number(Token t) {
            int base = 10;
            BytePtr start = this.p;
            long n = 0L;
            int d = 0;
            boolean err = false;
            Ref<Boolean> overflow = ref(false);
            boolean anyBinaryDigitsNoSingleUS = false;
            boolean anyHexDigitsNoSingleUS = false;
            int c = (int)this.p.get(0);
            try {
                if (c == 48)
                {
                    this.p.plusAssign(1);
                    c = this.p.get(0);
                    dispatched_1:
                    do {
                        int __dispatch1 = 0;
                        switch (__dispatch1 != 0 ? __dispatch1 : c)
                        {
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
                                base = 8;
                                break;
                            case 120:
                            case 88:
                                this.p.plusAssign(1);
                                base = 16;
                                break;
                            case 98:
                            case 66:
                                this.p.plusAssign(1);
                                base = 2;
                                break;
                            case 46:
                                if (this.p.get(1) == (byte)46)
                                    /*goto Ldone*/throw Dispatch.INSTANCE;
                                if ((isalpha((int)this.p.get(1))) != 0 || this.p.get(1) == (byte)95 || (this.p.get(1) & (byte)128) != 0)
                                    /*goto Ldone*/throw Dispatch.INSTANCE;
                                /*goto Lreal*/throw Dispatch.INSTANCE;
                            case 105:
                            case 102:
                            case 70:
                                /*goto Lreal*/throw Dispatch.INSTANCE;
                            case 95:
                                this.p.plusAssign(1);
                                base = 8;
                                break;
                            case 76:
                                if (this.p.get(1) == (byte)105)
                                    /*goto Lreal*/throw Dispatch.INSTANCE;
                                break;
                            default:
                            {
                                break;
                            }
                        }
                    } while(false);
                }
                for (; (1) != 0;){
                    c = this.p.get(0);
                    dispatched_1:
                    do {
                        int __dispatch1 = 0;
                        switch (__dispatch1 != 0 ? __dispatch1 : c)
                        {
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
                                this.p.plusAssign(1);
                                d = (c - 48);
                                break;
                            case 97:
                            case 98:
                            case 99:
                            case 100:
                            case 101:
                            case 102:
                            case 65:
                            case 66:
                            case 67:
                            case 68:
                            case 69:
                            case 70:
                                this.p.plusAssign(1);
                                if (base != 16)
                                {
                                    if (c == 101 || c == 69 || c == 102 || c == 70)
                                        /*goto Lreal*/{ __dispatch1 = -1; continue dispatched_1; }
                                }
                                if (c >= 97)
                                    d = (c + 10 - 97);
                                else
                                    d = (c + 10 - 65);
                                break;
                            case 76:
                                if (this.p.get(1) == (byte)105)
                                    /*goto Lreal*/{ __dispatch1 = -1; continue dispatched_1; }
                                /*goto Ldone*/throw Dispatch.INSTANCE;
                            case 46:
                                if (this.p.get(1) == (byte)46)
                                    /*goto Ldone*/throw Dispatch.INSTANCE;
                                if (base == 10 && (isalpha((int)this.p.get(1))) != 0 || this.p.get(1) == (byte)95 || (this.p.get(1) & (byte)128) != 0)
                                    /*goto Ldone*/throw Dispatch.INSTANCE;
                                /*goto Lreal*/{ __dispatch1 = -1; continue dispatched_1; }
                            case 112:
                            case 80:
                            case 105:
                            /*Lreal:*/
                            case -1:
                                this.p = start;
                                return this.inreal(t);
                            case 95:
                                this.p.plusAssign(1);
                                continue;
                            default:
                            {
                                /*goto Ldone*/throw Dispatch.INSTANCE;
                            }
                        }
                    } while(false);
                    anyHexDigitsNoSingleUS = true;
                    anyBinaryDigitsNoSingleUS = true;
                    if (!(err) && d >= base)
                    {
                        this.error(new BytePtr("%s digit expected, not `%c`"), base == 2 ? new BytePtr("binary") : base == 8 ? new BytePtr("octal") : new BytePtr("decimal"), c);
                        err = true;
                    }
                    if (n <= 1152921504606846975L)
                        n = n * (long)base + (long)d;
                    else
                    {
                        n = mulu(n, base, overflow);
                        n = addu(n, (long)d, overflow);
                    }
                }
            }
            catch(Dispatch __d){}
        /*Ldone:*/
            if (overflow.value && !(err))
            {
                this.error(new BytePtr("integer overflow"));
                err = true;
            }
            if (base == 2 && !(anyBinaryDigitsNoSingleUS) || base == 16 && !(anyHexDigitsNoSingleUS))
                this.error(new BytePtr("`%.*s` isn't a valid integer literal, use `%.*s0` instead"), (this.p.minus(start)) / 1, start, 2, start);
            int flags = base == 10 ? FLAGS.decimal : FLAGS.none;
            BytePtr psuffix = this.p;
            for (; (1) != 0;){
                int f = FLAGS.none;
                dispatched_1:
                do {
                    int __dispatch1 = 0;
                    switch (__dispatch1 != 0 ? __dispatch1 : (int)this.p.get(0))
                    {
                        case (byte)85:
                        case (byte)117:
                            f = FLAGS.unsigned;
                            /*goto L1*/{ __dispatch1 = -1; continue dispatched_1; }
                        case (byte)108:
                            f = FLAGS.long_;
                            this.error(new BytePtr("lower case integer suffix 'l' is not allowed. Please use 'L' instead"));
                            /*goto L1*/{ __dispatch1 = -1; continue dispatched_1; }
                        case (byte)76:
                            f = FLAGS.long_;
                        /*L1:*/
                        case -1:
                            this.p.postInc();
                            if ((flags & f) != 0 && !(err))
                            {
                                this.error(new BytePtr("unrecognized token"));
                                err = true;
                            }
                            flags = flags | f;
                            continue;
                        default:
                        {
                            break;
                        }
                    }
                } while(false);
                break;
            }
            if (base == 8 && n >= 8L)
            {
                if (err)
                    this.error(new BytePtr("octal literals larger than 7 are no longer supported"));
                else
                    this.error(new BytePtr("octal literals `0%llo%.*s` are no longer supported, use `std.conv.octal!%llo%.*s` instead"), n, (this.p.minus(psuffix)) / 1, psuffix, n, (this.p.minus(psuffix)) / 1, psuffix);
            }
            byte result = TOK.reserved;
            switch (flags)
            {
                case FLAGS.none:
                    if ((n & -9223372036854775808L) != 0)
                        result = TOK.uns64Literal;
                    else if ((n & -4294967296L) != 0)
                        result = TOK.int64Literal;
                    else if ((n & 2147483648L) != 0)
                        result = TOK.uns32Literal;
                    else
                        result = TOK.int32Literal;
                    break;
                case FLAGS.decimal:
                    if ((n & -9223372036854775808L) != 0)
                    {
                        if (!(err))
                        {
                            this.error(new BytePtr("signed integer overflow"));
                            err = true;
                        }
                        result = TOK.uns64Literal;
                    }
                    else if ((n & -2147483648L) != 0)
                        result = TOK.int64Literal;
                    else
                        result = TOK.int32Literal;
                    break;
                case FLAGS.unsigned:
                case 3:
                    if ((n & -4294967296L) != 0)
                        result = TOK.uns64Literal;
                    else
                        result = TOK.uns32Literal;
                    break;
                case 5:
                    if ((n & -9223372036854775808L) != 0)
                    {
                        if (!(err))
                        {
                            this.error(new BytePtr("signed integer overflow"));
                            err = true;
                        }
                        result = TOK.uns64Literal;
                    }
                    else
                        result = TOK.int64Literal;
                    break;
                case FLAGS.long_:
                    if ((n & -9223372036854775808L) != 0)
                        result = TOK.uns64Literal;
                    else
                        result = TOK.int64Literal;
                    break;
                case 6:
                case 7:
                    result = TOK.uns64Literal;
                    break;
                default:
                {
                    throw new AssertionError("Unreachable code!");
                }
            }
            (t).unsvalue = n;
            return result;
        }

        public  byte inreal(Token t) {
            boolean isWellformedString = true;
            Lexer.stringbuffer.reset();
            BytePtr pstart = this.p;
            boolean hex = false;
            int c = (int)this.p.postInc().get(0);
            if (c == 48)
            {
                c = this.p.postInc().get(0);
                if (c == 120 || c == 88)
                {
                    hex = true;
                    c = this.p.postInc().get(0);
                }
            }
            for (; (1) != 0;){
                if (c == 46)
                {
                    c = this.p.postInc().get(0);
                    break;
                }
                if ((isdigit(c)) != 0 || hex && (isxdigit(c)) != 0 || c == 95)
                {
                    c = this.p.postInc().get(0);
                    continue;
                }
                break;
            }
            for (; (1) != 0;){
                if ((isdigit(c)) != 0 || hex && (isxdigit(c)) != 0 || c == 95)
                {
                    c = this.p.postInc().get(0);
                    continue;
                }
                break;
            }
            if (c == 101 || c == 69 || hex && c == 112 || c == 80)
            {
                c = this.p.postInc().get(0);
                if (c == 45 || c == 43)
                {
                    c = this.p.postInc().get(0);
                }
                boolean anyexp = false;
                for (; (1) != 0;){
                    if ((isdigit(c)) != 0)
                    {
                        anyexp = true;
                        c = this.p.postInc().get(0);
                        continue;
                    }
                    if (c == 95)
                    {
                        c = this.p.postInc().get(0);
                        continue;
                    }
                    if (!(anyexp))
                    {
                        this.error(new BytePtr("missing exponent"));
                        isWellformedString = false;
                    }
                    break;
                }
            }
            else if (hex)
            {
                this.error(new BytePtr("exponent required for hex float"));
                isWellformedString = false;
            }
            this.p.minusAssign(1);
            for (; pstart.lessThan(this.p);){
                if (pstart.get(0) != (byte)95)
                    Lexer.stringbuffer.writeByte((int)pstart.get(0));
                pstart.plusAssign(1);
            }
            Lexer.stringbuffer.writeByte(0);
            BytePtr sbufptr = Lexer.stringbuffer.data.toBytePtr();
            byte result = TOK.reserved;
            boolean isOutOfRange = false;
            (t).floatvalue = isWellformedString ? CTFloat.parse(sbufptr, ptr(isOutOfRange)) : CTFloat.zero;
            dispatched_1:
            do {
                int __dispatch1 = 0;
                switch (__dispatch1 != 0 ? __dispatch1 : (int)this.p.get(0))
                {
                    case (byte)70:
                    case (byte)102:
                        if (isWellformedString && !(isOutOfRange))
                            isOutOfRange = Port.isFloat32LiteralOutOfRange(sbufptr);
                        result = TOK.float32Literal;
                        this.p.postInc();
                        break;
                    default:
                    {
                        if (isWellformedString && !(isOutOfRange))
                            isOutOfRange = Port.isFloat64LiteralOutOfRange(sbufptr);
                        result = TOK.float64Literal;
                        break;
                    }
                    case (byte)108:
                        this.error(new BytePtr("use 'L' suffix instead of 'l'"));
                        /*goto case*/{ __dispatch1 = 76; continue dispatched_1; }
                    case (byte)76:
                        result = TOK.float80Literal;
                        this.p.postInc();
                        break;
                }
            } while(false);
            if (this.p.get(0) == (byte)105 || this.p.get(0) == (byte)73)
            {
                if (this.p.get(0) == (byte)73)
                    this.error(new BytePtr("use 'i' suffix instead of 'I'"));
                this.p.postInc();
                switch (result)
                {
                    case (byte)111:
                        result = TOK.imaginary32Literal;
                        break;
                    case (byte)112:
                        result = TOK.imaginary64Literal;
                        break;
                    case (byte)113:
                        result = TOK.imaginary80Literal;
                        break;
                    default:
                    {
                        break;
                    }
                }
            }
            boolean isLong = result == (byte)113 || result == (byte)116;
            if (isOutOfRange && !(isLong))
            {
                BytePtr suffix = result == (byte)111 || result == (byte)114 ? new BytePtr("f") : new BytePtr("");
                this.error(this.scanloc, new BytePtr("number `%s%s` is not representable"), sbufptr, suffix);
            }
            return result;
        }

        public  Loc loc() {
            this.scanloc.charnum = ((this.p.plus(1).minus(this.line)) / 1);
            return this.scanloc;
        }

        public  void error(BytePtr format, Object... args) {
            this.diagnosticReporter.error(this.token.loc, format, new Slice<>(args));
        }

        public  void error(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.error(loc, format, new Slice<>(args));
        }

        public  void errorSupplemental(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.errorSupplemental(loc, format, new Slice<>(args));
        }

        public  void warning(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.warning(loc, format, new Slice<>(args));
        }

        public  void warningSupplemental(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.warningSupplemental(loc, format, new Slice<>(args));
        }

        public  void deprecation(BytePtr format, Object... args) {
            this.diagnosticReporter.deprecation(this.token.loc, format, new Slice<>(args));
        }

        public  void deprecation(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.deprecation(loc, format, new Slice<>(args));
        }

        public  void deprecationSupplemental(Loc loc, BytePtr format, Object... args) {
            this.diagnosticReporter.deprecationSupplemental(loc, format, new Slice<>(args));
        }

        public  void poundLine() {
            int linnum = this.scanloc.linnum;
            BytePtr filespec = null;
            Loc loc = this.loc();
            Token tok = new Token();
            this.scan(tok);
            try {
                if (tok.value == (byte)105 || tok.value == (byte)107)
                {
                    int lin = (int)(tok.unsvalue - 1L);
                    if ((long)lin != tok.unsvalue - 1L)
                        this.error(new BytePtr("line number `%lld` out of range"), tok.unsvalue);
                    else
                        linnum = lin;
                }
                else if (tok.value == (byte)218)
                {
                }
                else
                    /*goto Lerr*/throw Dispatch.INSTANCE;
                for (; (1) != 0;){
                    dispatched_1:
                    do {
                        int __dispatch1 = 0;
                        switch (__dispatch1 != 0 ? __dispatch1 : (int)this.p.get(0))
                        {
                            case (byte)0:
                            case (byte)26:
                            case (byte)10:
                            /*Lnewline:*/
                            case -1:
                                this.scanloc.linnum = linnum;
                                if (filespec != null)
                                    this.scanloc.filename = filespec;
                                return ;
                            case (byte)13:
                                this.p.postInc();
                                if (this.p.get(0) != (byte)10)
                                {
                                    this.p.postDec();
                                    /*goto Lnewline*/{ __dispatch1 = -1; continue dispatched_1; }
                                }
                                continue;
                            case (byte)32:
                            case (byte)9:
                            case (byte)11:
                            case (byte)12:
                                this.p.postInc();
                                continue;
                            case (byte)95:
                                if (memcmp(this.p,  new ByteSlice("__FILE__"), 8) == 0)
                                {
                                    this.p.plusAssign(8);
                                    filespec = Mem.xstrdup(this.scanloc.filename);
                                    continue;
                                }
                                /*goto Lerr*/throw Dispatch.INSTANCE;
                            case (byte)34:
                                if (filespec != null)
                                    /*goto Lerr*/throw Dispatch.INSTANCE;
                                Lexer.stringbuffer.reset();
                                this.p.postInc();
                                for (; (1) != 0;){
                                    int c = 0;
                                    c = this.p.get(0);
                                    dispatched_2:
                                    do {
                                        int __dispatch2 = 0;
                                        switch (__dispatch2 != 0 ? __dispatch2 : c)
                                        {
                                            case 10:
                                            case 13:
                                            case 0:
                                            case 26:
                                                /*goto Lerr*/throw Dispatch.INSTANCE;
                                            case 34:
                                                Lexer.stringbuffer.writeByte(0);
                                                filespec = Mem.xstrdup(Lexer.stringbuffer.data.toBytePtr());
                                                this.p.postInc();
                                                break;
                                            default:
                                            {
                                                if ((c & 128) != 0)
                                                {
                                                    int u = this.decodeUTF();
                                                    if (u == 8233 || u == 8232)
                                                        /*goto Lerr*/throw Dispatch.INSTANCE;
                                                }
                                                Lexer.stringbuffer.writeByte(c);
                                                this.p.postInc();
                                                continue;
                                            }
                                        }
                                    } while(false);
                                    break;
                                }
                                continue;
                            default:
                            {
                                if ((this.p.get(0) & (byte)128) != 0)
                                {
                                    int u = this.decodeUTF();
                                    if (u == 8233 || u == 8232)
                                        /*goto Lnewline*/{ __dispatch1 = -1; continue dispatched_1; }
                                }
                                /*goto Lerr*/throw Dispatch.INSTANCE;
                            }
                        }
                    } while(false);
                }
            }
            catch(Dispatch __d){}
        /*Lerr:*/
            this.error(loc, new BytePtr("#line integer [\"filespec\"]\\n expected"));
        }

        public  int decodeUTF() {
            BytePtr s = this.p;
            assert((s.get(0) & (byte)128) != 0);
            int len = 0;
            {
                len = 1;
                for (; len < 4 && (s.get(len)) != 0;len++){
                }
            }
            IntRef idx = ref(0);
            IntRef u = ref('\uffff');
            BytePtr msg = utf_decodeChar(s, len, idx, u);
            this.p.plusAssign((idx.value - 1) * 1);
            if (msg != null)
            {
                this.error(new BytePtr("%s"), msg);
            }
            return u.value;
        }

        public  void getDocComment(Token t, int lineComment, boolean newParagraph) {
            byte ct = (t).ptr.get(2);
            BytePtr q = (t).ptr.plus(3);
            BytePtr qend = this.p;
            if (ct == (byte)42 || ct == (byte)43)
                qend.minusAssign(2);
            for (; q.lessThan(qend);q.postInc()){
                if ((int)q.get(0) != (int)ct)
                    break;
            }
            int linestart = 0;
            if (ct == (byte)47)
            {
                for (; q.lessThan(qend) && q.get(0) == (byte)32 || q.get(0) == (byte)9;) {
                    q.plusAssign(1);
                }
            }
            else if (q.lessThan(qend))
            {
                if (q.get(0) == (byte)13)
                {
                    q.plusAssign(1);
                    if (q.lessThan(qend) && q.get(0) == (byte)10)
                        q.plusAssign(1);
                    linestart = 1;
                }
                else if (q.get(0) == (byte)10)
                {
                    q.plusAssign(1);
                    linestart = 1;
                }
            }
            if (ct != (byte)47)
            {
                for (; q.lessThan(qend);qend.postDec()){
                    if ((int)qend.get(-1) != (int)ct)
                        break;
                }
            }
            OutBuffer buf = new OutBuffer();
            try {
                Function0<Void> trimTrailingWhitespace = new Function0<Void>(){
                    public Void invoke(){
                        ByteSlice s = buf.peekSlice();
                        int len = s.getLength();
                        for (; (len) != 0 && s.get(len - (byte)1) == (byte)32 || s.get(len - (byte)1) == (byte)9;) {
                            len -= 1;
                        }
                        buf.setsize(len);
                        return null;
                    }
                };
                for (; q.lessThan(qend);q.postInc()){
                    byte c = q.get(0);
                    dispatched_1:
                    do {
                        int __dispatch1 = 0;
                        switch (__dispatch1 != 0 ? __dispatch1 : (int)c)
                        {
                            case (byte)42:
                            case (byte)43:
                                if ((linestart) != 0 && (int)c == (int)ct)
                                {
                                    linestart = 0;
                                    trimTrailingWhitespace.invoke();
                                    continue;
                                }
                                break;
                            case (byte)32:
                            case (byte)9:
                                break;
                            case (byte)13:
                                if (q.get(1) == (byte)10)
                                    continue;
                                /*goto Lnewline*/{ __dispatch1 = -1; continue dispatched_1; }
                            default:

                                if (c == (byte)226)
                                {
                                    if (q.get(1) == (byte)128 && q.get(2) == (byte)168 || q.get(2) == (byte)169)
                                    {
                                        q.plusAssign(2);
                                        /*goto Lnewline*/{ __dispatch1 = -1; continue dispatched_1; }
                                    }
                                }
                                linestart = 0;
                                break;
                            /*Lnewline:*/
                            case -1:
                                c = (byte)10;

                            case (byte)10:
                                linestart = 1;
                                trimTrailingWhitespace.invoke();
                                break;
                        }
                    } while(false);
                    buf.writeByte((int)c);
                }
                trimTrailingWhitespace.invoke();
                ByteSlice s = buf.peekSlice();
                if (s.getLength() == 0 || s.get(s.getLength() - (byte)1) != (byte)10)
                    buf.writeByte(10);
                Ptr<BytePtr> dc = (lineComment) != 0 && this.anyToken ? ptr((t).lineComment) : ptr((t).blockComment);
                if (dc.get(0) != null)
                    dc.set(0, Lexer.combineComments(dc.get(0), buf.peekChars(), newParagraph));
                else
                    dc.set(0, buf.extractChars());
            }
            finally {
            }
        }

        public static BytePtr combineComments(BytePtr c1, BytePtr c2, boolean newParagraph) {
            BytePtr c = c2;
            int newParagraphSize = newParagraph ? 1 : 0;
            if (c1 != null)
            {
                c = c1;
                if (c2 != null)
                {
                    int len1 = strlen(c1);
                    int len2 = strlen(c2);
                    int insertNewLine = 0;
                    if ((len1) != 0 && c1.get(len1 - (byte)1) != (byte)10)
                    {
                        len1 += 1;
                        insertNewLine = 1;
                    }
                    BytePtr p = Mem.xmalloc(len1 + newParagraphSize + len2 + 1).toBytePtr();
                    memcpy(p, c1, len1 - insertNewLine);
                    if ((insertNewLine) != 0)
                        p.set((len1 - 1), (byte)10);
                    if (newParagraph)
                        p.set(len1, (byte)10);
                    memcpy((p.plus(len1 * 1).plus(newParagraphSize * 1)), c2, len2);
                    p.set((len1 + newParagraphSize + len2), (byte)0);
                    c = p;
                }
            }
            return c;
        }

        public  void endOfLine() {
            this.scanloc.linnum++;
            this.line = this.p;
        }

    }/*
    public static void test_2() {
        test( new ByteSlice("'"), (byte)39);
        test( new ByteSlice("\""), (byte)34);
        test( new ByteSlice("?"), (byte)63);
        test( new ByteSlice("\\"), (byte)92);
        test( new ByteSlice("0"), (byte)0);
        test( new ByteSlice("a"), (byte)7);
        test( new ByteSlice("b"), (byte)8);
        test( new ByteSlice("f"), (byte)12);
        test( new ByteSlice("n"), (byte)10);
        test( new ByteSlice("r"), (byte)13);
        test( new ByteSlice("t"), (byte)9);
        test( new ByteSlice("v"), (byte)11);
        test( new ByteSlice("x00"), 0);
        test( new ByteSlice("xff"), 255);
        test( new ByteSlice("xFF"), 255);
        test( new ByteSlice("xa7"), 167);
        test( new ByteSlice("x3c"), 60);
        test( new ByteSlice("xe2"), 226);
        test( new ByteSlice("1"), (byte)1);
        test( new ByteSlice("42"), (byte)34);
        test( new ByteSlice("357"), (byte)239);
        test( new ByteSlice("u1234"), '\u1234');
        test( new ByteSlice("uf0e4"), '\uf0e4');
        test( new ByteSlice("U0001f603"), '\u1f603');
        test( new ByteSlice("&quot;"), (byte)34);
        test( new ByteSlice("&lt;"), (byte)60);
        test( new ByteSlice("&gt;"), (byte)62);
    }
    public static void test_3() {
        Function4<ByteSlice,ByteSlice,Integer,Integer,Void> test = new Function4<ByteSlice,ByteSlice,Integer,Integer,Void>(){
            public Void invoke(ByteSlice sequence, ByteSlice expectedError, Integer expectedReturnValue, Integer expectedScanLength){
                ExpectDiagnosticReporter handler = new ExpectDiagnosticReporter(expectedError);
                BytePtr p = sequence.toBytePtr().toBytePtr();
                int actualReturnValue = Lexer.escapeSequence(Loc.initial, handler, p);
                assert(handler.gotError);
                assert(expectedReturnValue == actualReturnValue);
                int actualScanLength = (p.minus(sequence.toBytePtr().toBytePtr())) / 1;
                assert(expectedScanLength == actualScanLength);
            }
        };
        test( new ByteSlice("c"),  new ByteSlice("undefined escape sequence \\c"), '\u0063', 1);
        test( new ByteSlice("!"),  new ByteSlice("undefined escape sequence \\!"), '\u0021', 1);
        test( new ByteSlice("x1"),  new ByteSlice("escape hex sequence has 1 hex digits instead of 2"), '\u0001', 2);
        test( new ByteSlice("u1"),  new ByteSlice("escape hex sequence has 1 hex digits instead of 4"), '\u0001', 2);
        test( new ByteSlice("u12"),  new ByteSlice("escape hex sequence has 2 hex digits instead of 4"), '\u0012', 3);
        test( new ByteSlice("u123"),  new ByteSlice("escape hex sequence has 3 hex digits instead of 4"), '\u0123', 4);
        test( new ByteSlice("U0"),  new ByteSlice("escape hex sequence has 1 hex digits instead of 8"), '\u0000', 2);
        test( new ByteSlice("U00"),  new ByteSlice("escape hex sequence has 2 hex digits instead of 8"), '\u0000', 3);
        test( new ByteSlice("U000"),  new ByteSlice("escape hex sequence has 3 hex digits instead of 8"), '\u0000', 4);
        test( new ByteSlice("U0000"),  new ByteSlice("escape hex sequence has 4 hex digits instead of 8"), '\u0000', 5);
        test( new ByteSlice("U0001f"),  new ByteSlice("escape hex sequence has 5 hex digits instead of 8"), '\u001f', 6);
        test( new ByteSlice("U0001f6"),  new ByteSlice("escape hex sequence has 6 hex digits instead of 8"), '\u01f6', 7);
        test( new ByteSlice("U0001f60"),  new ByteSlice("escape hex sequence has 7 hex digits instead of 8"), '\u1f60', 8);
        test( new ByteSlice("ud800"),  new ByteSlice("invalid UTF character \\U0000d800"), '\u003f', 5);
        test( new ByteSlice("udfff"),  new ByteSlice("invalid UTF character \\U0000dfff"), '\u003f', 5);
        test( new ByteSlice("U00110000"),  new ByteSlice("invalid UTF character \\U00110000"), '\u003f', 9);
        test( new ByteSlice("xg0"),  new ByteSlice("undefined escape hex sequence \\xg"), '\u0067', 2);
        test( new ByteSlice("ug000"),  new ByteSlice("undefined escape hex sequence \\ug"), '\u0067', 2);
        test( new ByteSlice("Ug0000000"),  new ByteSlice("undefined escape hex sequence \\Ug"), '\u0067', 2);
        test( new ByteSlice("&BAD;"),  new ByteSlice("unnamed character entity &BAD;"), '\u003f', 5);
        test( new ByteSlice("&quot"),  new ByteSlice("unterminated named entity &quot;"), '\u003f', 5);
        test( new ByteSlice("400"),  new ByteSlice("escape octal sequence \\400 is larger than \\377"), '\u0100', 3);
    }*/
}
