package Jlox;
import java.util.List;

abstract class Stmt {

    interface Visitor<R> {
        R visitPrintStmt(Print printStmt);
        R visitExpressionStmt(Expression expressionStmt);
        R visitVarStmt(Var varStmt);
        R visitBlockStmt(Block blockStmt);
        R visitIfStmt(If ifStmt);
        R visitWhileStmt(While whileStmt);
        R visitFunStmt(Fun funStmt);
        R visitReturnStmt(Return returnStmt);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Print extends Stmt {
        Print(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    static class Expression extends Stmt {
        Expression(Expr expression) {
            this.expression = expression;
        }

        final Expr expression;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    static class Var extends Stmt {
        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        final Token name;
        final Expr initializer;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    static class Block extends Stmt {
        Block(List<Stmt> statements) {
            this.statements = statements;
        }

        final List<Stmt> statements;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBlockStmt(this);
        }
    }

    static class If extends Stmt {
        If(Expr condition, Stmt thenBranch, Stmt elseBranch) {
            this.condition = condition;
            this.thenBranch = thenBranch;
            this.elseBranch = elseBranch;
        }

        final Expr condition;
        final Stmt thenBranch;
        final Stmt elseBranch;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitIfStmt(this);
        }
    }

    static class While extends Stmt {
        While(Expr condition, Stmt body) {
            this.condition = condition;
            this.body = body;
        }

        final Expr condition;
        final Stmt body;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitWhileStmt(this);
        }
    }

    static class Fun extends Stmt {
        Fun(Token name, List<Token> params, List<Stmt> body) {
            this.name = name;
            this.params = params;
            this.body = body;
        }

        final Token name;
        final List<Token> params;
        final List<Stmt> body;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitFunStmt(this);
        }
    }

    static class Return extends Stmt {
        Return(Token keyword, Expr Value) {
            this.keyword = keyword;
            this.Value = Value;
        }

        final Token keyword;
        final Expr Value;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnStmt(this);
        }
    }
}
