package org.dlang.dmd;
import kotlin.jvm.functions.*;

import org.dlang.dmd.root.*;
import static org.dlang.dmd.root.filename.*;
import static org.dlang.dmd.root.File.*;
import static org.dlang.dmd.root.ShimsKt.*;
import static org.dlang.dmd.root.SliceKt.*;
import static org.dlang.dmd.root.DArrayKt.*;
import static org.dlang.dmd.statement.*;
import static org.dlang.dmd.visitor.*;

public class statement_rewrite_walker {

    public static class StatementRewriteWalker extends SemanticTimePermissiveVisitor
    {
        public Ptr<Statement> ps = null;
        public  void visitStmt(Ref<Statement> s) {
            this.ps = pcopy((ptr(s)));
            s.value.accept(this);
        }

        public  void replaceCurrent(Statement s) {
            this.ps.set(0, s);
        }

        public  void visit(PeelStatement s) {
            if (s.s.value != null)
            {
                this.visitStmt(s);
            }
        }

        public  void visit(CompoundStatement s) {
            if ((s.statements != null) && ((s.statements.get()).length != 0))
            {
                {
                    int i = 0;
                    for (; (i < (s.statements.get()).length);i++){
                        if ((s.statements.get()).get(i) != null)
                        {
                            this.visitStmt((s.statements.get()).get(i));
                        }
                    }
                }
            }
        }

        public  void visit(CompoundDeclarationStatement s) {
            this.visit((CompoundStatement)s);
        }

        public  void visit(UnrolledLoopStatement s) {
            if ((s.statements != null) && ((s.statements.get()).length != 0))
            {
                {
                    int i = 0;
                    for (; (i < (s.statements.get()).length);i++){
                        if ((s.statements.get()).get(i) != null)
                        {
                            this.visitStmt((s.statements.get()).get(i));
                        }
                    }
                }
            }
        }

        public  void visit(ScopeStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        public  void visit(WhileStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(DoStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(ForStatement s) {
            if (s._init.value != null)
            {
                this.visitStmt(_init);
            }
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(ForeachStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(ForeachRangeStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(IfStatement s) {
            if (s.ifbody.value != null)
            {
                this.visitStmt(ifbody);
            }
            if (s.elsebody.value != null)
            {
                this.visitStmt(elsebody);
            }
        }

        public  void visit(SwitchStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(CaseStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        public  void visit(CaseRangeStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        public  void visit(DefaultStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        public  void visit(SynchronizedStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(WithStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
        }

        public  void visit(TryCatchStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
            if ((s.catches != null) && ((s.catches.get()).length != 0))
            {
                {
                    int i = 0;
                    for (; (i < (s.catches.get()).length);i++){
                        Catch c = (s.catches.get()).get(i);
                        if ((c != null) && (c.handler.value != null))
                        {
                            this.visitStmt(handler);
                        }
                    }
                }
            }
        }

        public  void visit(TryFinallyStatement s) {
            if (s._body.value != null)
            {
                this.visitStmt(_body);
            }
            if (s.finalbody.value != null)
            {
                this.visitStmt(finalbody);
            }
        }

        public  void visit(DebugStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }

        public  void visit(LabelStatement s) {
            if (s.statement.value != null)
            {
                this.visitStmt(statement);
            }
        }


        public StatementRewriteWalker() {}

        public StatementRewriteWalker copy() {
            StatementRewriteWalker that = new StatementRewriteWalker();
            that.ps = this.ps;
            return that;
        }
    }
}