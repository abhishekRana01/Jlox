package Jlox;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object>, Stmt.Visitor<Void> {

    Environment globals             =   new Environment();
    private Environment environment =   globals;
    private Map<Expr, Integer> locals = new HashMap<>();

    Interpreter() {
        globals.define("clock", new LoxCallable() {
            @Override
            public Object call(Interpreter interpreter, List<Object> args) {
                return (double)System.currentTimeMillis() / 1000.0;
            }

            @Override
            public int arity() {
                return 0;
            }

            @Override
            public String toString() {
                return "<native fn>";
            }

        });
    }

    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    public Object visitGroupingExpr(Expr.Grouping grouping) {
        return evaluate(grouping);
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    public Object visitUnaryExpr(Expr.Unary unary) {
        Object right = evaluate(unary.right);

        switch (unary.operator.type) {
            case MINUS:
                checkNumberOperand(unary.operator, right);
                return -(double)right;
            case BANG:
                return !isTruthy(right);
        }

        return null;
    }

    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);

        if(distance!= null) {
            return environment.getAt(distance, name.lexeme);
        }

        return globals.get(name);
    }

    public Object visitAssignExpr(Expr.Assign expr) {
        Object value = evaluate(expr.value);
        Integer distance = locals.get(expr);
        if(distance != null) {
            environment.assignAt(distance, expr.name, value);
        }
        else globals.assign(expr.name, value);
        return value;
    }

    public Object visitLogicalExpr(Expr.Logical expr) {

        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {

            case OR:
                if(isTruthy(left)) return left;
                if(isTruthy(right)) return right;
                return left;

            case AND:
                if(!isTruthy(left)) return left;
                if(!isTruthy(right)) return right;
                return right;
        }

        return null;
    }

    public Object visitCallExpr(Expr.Call expr) {
        Object callee = evaluate(expr.callee);

        List<Object> args = new ArrayList<>();

        for(Expr argument : expr.arguments) {
            args.add(evaluate(argument));
        }

        if( !(callee instanceof LoxCallable) ) {
            throw new RuntimeError(expr.paren, "Can only call functions and classes.");
        }

        LoxCallable function = (LoxCallable) callee;

        if( args.size() != function.arity()) {
            throw new RuntimeError(expr.paren, "Expected " + function.arity() +
                    " arguments but got " + args.size());
        }

        return function.call(this, args);
    }

    private void checkNumberOperand(Token token, Object operand) {
        if(operand instanceof Double) return;
        throw new RuntimeError(token, "Operand must be a number");
    }

    private void checkNumberOperands(Token token, Object left, Object right) {
        if(left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(token, "Operands must be number");
    }

    private boolean isTruthy(Object object) {
        if(object == null) return false;
        if(object instanceof Boolean) {
            return (boolean)object;
        }

        return true;
    }

    public Object visitBinaryExpr(Expr.Binary binary) {
        Object left = evaluate(binary.left);
        Object right = evaluate(binary.right);

        switch (binary.operator.type) {
            case MINUS:
                checkNumberOperands(binary.operator, left, right);
                return (double) left - (double) right;
            case SLASH:
                checkNumberOperands(binary.operator, left, right);
                if((double)right == 0) {
                    throw new RuntimeError(binary.operator,"Division by zero not allowed");
                }
                return (double) left / (double) right;
            case STAR:
                checkNumberOperands(binary.operator, left, right);
                return (double) left * (double) right;
            case PLUS:
                if(left instanceof Double && right instanceof Double) return (double) left + (double) right;
                if(left instanceof String && right instanceof String) return (String) left + (String) right;
                if( (left instanceof String && right instanceof Double) ) {
                    return left + stringify(right);
                }
                if( (left instanceof Double && right instanceof String) ) {
                    return stringify(left) + right;
                }

                throw new RuntimeError(binary.operator, "Operand must be numbers or strings");
            case GREATER:
                checkNumberOperands(binary.operator, left, right);
                return (double) left > (double) right;
            case GREATER_EQUAL:
                checkNumberOperands(binary.operator, left, right);
                return (double) left >= (double) right;
            case LESS:
                checkNumberOperands(binary.operator, left, right);
                return (double) left < (double) right;
            case LESS_EQUAL:
                checkNumberOperands(binary.operator, left, right);
                return (double) left <= (double) right;
            case BANG_EQUAL:
                return !isEqual(left, right);
            case EQUAL_EQUAL:
                return isEqual(left, right);
                default:
                    return null;
        }
    }

    private boolean isEqual(Object left, Object right) {
        if(left == null && right == null) return true;
        if(left == null) return false;

        return left.equals(right);
    }

    private String stringify(Object object) {
        if(object == null) return "nil";

        if(object instanceof Double) {
            String text = object.toString();
            if(text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }

            return text;
        }

        return object.toString();
    }

    public Void visitExpressionStmt(Stmt.Expression stmt) {
        Object value = evaluate(stmt.expression);
        return null;
    }

    public Void visitPrintStmt(Stmt.Print stmt) {
        Object value = evaluate(stmt.expression);
//        System.out.println("StopWierd in intrpereter.java");
        System.out.println(stringify(value));
        return null;
    }

    public Void visitVarStmt(Stmt.Var var) {
        Object value = null;
        if(var.initializer != null) {
            value = evaluate(var.initializer);
        }

        environment.define(var.name.lexeme, value);
        return null;
    }

    public Void visitBlockStmt(Stmt.Block block) {
        executeBlock(block.statements, new Environment(environment));
        return null;
    }

    public Void visitIfStmt(Stmt.If ifStmt) {
        Object value = evaluate(ifStmt.condition);

        if(isTruthy(value)) {
            execute(ifStmt.thenBranch);
        }
        else if(ifStmt.elseBranch != null) {
            execute(ifStmt.elseBranch);
        }

        return null;
    }

    public Void visitWhileStmt(Stmt.While stmt) {
        Expr condition = stmt.condition;

        while(isTruthy(evaluate(condition))) {
            execute(stmt.body);
        }

        return null;
    }

    public Void visitFunStmt(Stmt.Fun stmt) {
        LoxFunction loxFunction = new LoxFunction(stmt, environment);
        environment.define(stmt.name.lexeme, loxFunction);
        return null;
    }

    public Void visitReturnStmt(Stmt.Return stmt) {
        Object value = null;
        if(stmt.Value != null) value = evaluate(stmt.Value);

        throw new Return(value);
    }

    public Void visitClassStmt(Stmt.Class stmt) {
        environment.define(stmt.name.lexeme, null);
        LoxClass klaas = new LoxClass(stmt.name.lexeme);
        environment.assign(stmt.name, klaas);
        return null;
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    void executeBlock(List<Stmt> stmts, Environment environment) {
        Environment previous = this.environment;

        try {
            this.environment = environment;
            for(Stmt stmt : stmts) {
                execute(stmt);
            }
        } finally {
            this.environment = previous;
        }
    }

    private Void execute(Stmt stmt) {
        return stmt.accept(this);
    }

    void  interpret(List<Stmt> stmts) {
        try {
            for(Stmt stmt : stmts) {
                execute(stmt);
            }
        } catch (RuntimeError e) {
            Lox.runtimeError(e);
        }
    }
}






















