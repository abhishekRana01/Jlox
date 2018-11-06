package Jlox;

public class AstPrinter implements Expr.Visitor<String>{

    String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary binary) {
        return parenthesize(binary.operator.lexeme, binary.left, binary.right);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping grouping) {
        System.out.println("visiting grouping");
        return parenthesize("group", grouping.expr);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal literal) {
        System.out.println("visiting literal " + literal.value);
        if(literal.value == null) {
            return "nil";
        }

        return literal.value.toString();
    }

    public String visitLogicalExpr(Expr.Logical logical) {
        return "";
    }



    @Override
    public String visitUnaryExpr(Expr.Unary unary) {
        return parenthesize(unary.operator.lexeme, unary.right);
    }

    public String visitVariableExpr(Expr.Variable variable) {
        return variable.name.lexeme;
    }

    public String visitAssignExpr(Expr.Assign assign) {
        return "";
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(").append(name);

        for(Expr expr : exprs) {
            stringBuilder.append(" ");
            stringBuilder.append(expr.accept(this));
        }

        stringBuilder.append(")");

        return stringBuilder.toString();
    }
}
