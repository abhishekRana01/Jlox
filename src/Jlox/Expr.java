package Jlox;
import java.util.List;

abstract class Expr {

    interface Visitor<R> {
        R visitBinaryExpr(Binary binaryExpr);
        R visitUnaryExpr(Unary unaryExpr);
        R visitGroupingExpr(Grouping groupingExpr);
        R visitLiteralExpr(Literal literalExpr);
        R visitVariableExpr(Variable variableExpr);
        R visitAssignExpr(Assign assignExpr);
        R visitLogicalExpr(Logical logicalExpr);
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        final Token operator;
        final Expr right;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    static class Grouping extends Expr {
        Grouping(Expr expr) {
            this.expr = expr;
        }

        final Expr expr;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        final Object value;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }

        final Token name;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    static class Assign extends Expr {
        Assign(Token name, Expr value) {
            this.name = name;
            this.value = value;
        }

        final Token name;
        final Expr value;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }
    }

    static class Logical extends Expr {
        Logical(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        final Expr left;
        final Token operator;
        final Expr right;

        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLogicalExpr(this);
        }
    }
}
