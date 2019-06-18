package org.dlang.dmd;

import junit.framework.TestCase;
import org.dlang.dmd.root.ByteSlice;
import org.dlang.dmd.root.Slice;

import static org.dlang.dmd.globals.global;
import static org.dlang.dmd.root.ShimsKt.toBytePtr;

public class TestLexer extends TestCase {

    public static void test_0() {
        ByteSlice text =  new ByteSlice("int");
        errors.StderrDiagnosticReporter diagnosticReporter = new errors.StderrDiagnosticReporter(global.params.useDeprecated);
        lexer.Lexer lex1 = new lexer.Lexer(null, toBytePtr(text), 0, text.getLength(), false, false, diagnosticReporter);
        byte tok = tokens.TOK.reserved;
        tok = lex1.nextToken();
        assert((tok & 0xFF) == 133);
        tok = lex1.nextToken();
        assert(tok == 11);
        tok = lex1.nextToken();
        assert(tok == 11);
        tok = lex1.nextToken();
        assert(tok == 11);
    }

    public static void test_1() {
        int errors = global.startGagging();
        {
            Slice<ByteSlice> __r51 = lexer.__unittest_L168_C1testcases;
            int __key52 = 0;
            for (; __key52 < __r51.getLength();__key52 += 1) {
                ByteSlice testcase = __r51.get(__key52);
                errors.StderrDiagnosticReporter diagnosticReporter = new errors.StderrDiagnosticReporter(global.params.useDeprecated);
                lexer.Lexer lex2 = new lexer.Lexer(null, toBytePtr(testcase), 0, testcase.getLength() - 1, false, false, diagnosticReporter);
                byte tok = lex2.nextToken();
                int iterations = 1;
                for (; tok != 11 && iterations++ < testcase.getLength();){
                    tok = lex2.nextToken();
                }
                assert(tok == 11);
                tok = lex2.nextToken();
                assert(tok == 11);
            }
        }
        global.endGagging(errors);
    }
}
