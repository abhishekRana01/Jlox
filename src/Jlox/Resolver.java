package Jlox;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class Resolver implements Expr.Visitor<Void>, Stmt.Visitor<Void> {

    private final Interpreter interpreter;
    private final Stack<Map<String, Boolean>> scopes    =   new Stack<>();
    private FunctionType currentFunction  =  FunctionType.FUNCTION;

    Resolver(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    private enum FunctionType {
        NONE,
        FUNCTION
    }

    public Void visitBlockStmt(Stmt.Block stmt) {
        beginScope();
        resolve(stmt.statements);
        endScope();
        return null;
    }

    public Void visitVarStmt(Stmt.Var stmt) {
        declare(stmt.name);

        if(stmt.initializer != null) {
            resolve(stmt.initializer);
        }

        define(stmt.name);
        return null;
    }

    public Void visitFunStmt(Stmt.Fun stmt) {
        declare(stmt.name);
        define(stmt.name);

        resolveFunction(stmt, FunctionType.FUNCTION);
        return null;
    }

    public Void visitExpressionStmt(Stmt.Expression stmt) {
        resolve(stmt.expression);
        return null;
    }

    public Void visitIfStmt(Stmt.If stmt) {
        resolve(stmt.condition);
        resolve(stmt.thenBranch);
        if(stmt.elseBranch != null) resolve(stmt.elseBranch);
        return null;
    }

    public Void visitWhileStmt(Stmt.While stmt) {
        resolve(stmt.condition);
        resolve(stmt.body);
        return null;
    }

    public Void visitPrintStmt(Stmt.Print stmt) {
        resolve(stmt.expression);
        return null;
    }

    public Void visitReturnStmt(Stmt.Return stmt) {
        if(currentFunction == FunctionType.NONE) {
            Lox.error(stmt.keyword.line, "Cannot return from top-level code");
        }
        if(stmt.Value != null) resolve(stmt.Value);
        return null;
    }

    public Void visitClassStmt(Stmt.Class stmt){
        declare(stmt.name);
        define(stmt.name);
        return null;
    }

    public Void visitAssignExpr(Expr.Assign expr) {
        resolve(expr.value);
        resolveLocal(expr, expr.name);
        return null;
    }

    public Void visitVariableExpr(Expr.Variable expr) {
        if(!scopes.isEmpty() && scopes.peek().get(expr.name.lexeme) == Boolean.FALSE) {
            Lox.error(expr.name.line, "Cannot read local var in it's own initializer");
        }

        resolveLocal(expr, expr.name);
        return null;
    }

    public Void visitUnaryExpr(Expr.Unary expr) {
        resolve(expr.right);
        return null;
    }

    public Void visitBinaryExpr(Expr.Binary expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    public Void visitCallExpr(Expr.Call expr) {
        resolve(expr.callee);

        for(Expr arg : expr.arguments) {
            resolve(arg);
        }

        return null;
    }

    public Void visitGroupingExpr(Expr.Grouping expr) {
        resolve(expr.expr);
        return null;
    }

    public Void visitLiteralExpr(Expr.Literal expr) {
        return null;
    }

    public Void visitLogicalExpr(Expr.Logical expr) {
        resolve(expr.left);
        resolve(expr.right);
        return null;
    }

    private void resolveFunction(Stmt.Fun stmt, FunctionType functionType) {
        FunctionType enclosingFunction = currentFunction;
        currentFunction = functionType;

        beginScope();
        for(Token param : stmt.params) {
            declare(param);
            define(param);
        }

        resolve(stmt.body);

        endScope();

        currentFunction = enclosingFunction;
    }

    private void resolveLocal(Expr expr, Token token) {
        for(int i = scopes.size() -1; i >=0; i--) {
            if(scopes.get(i).containsKey(token.lexeme)) {
                interpreter.resolve(expr, scopes.size() - 1 -i);
                return;
            }
        }
    }

    private void declare(Token name) {
        if(scopes.isEmpty()) return;
        if(scopes.peek().containsKey(name.lexeme)) {
            Lox.error(name.line, "Variable already declared in this scope.");
        }
        scopes.peek().put(name.lexeme, false);
    }

    private void define(Token name) {
        if(!scopes.isEmpty()) scopes.peek().put(name.lexeme, true);
    }

    public void resolve(List<Stmt> stmts) {
        for(Stmt stmt : stmts) {
            resolve(stmt);
        }
    }

    private void resolve(Stmt stmt) {
        stmt.accept(this);
    }

    private void resolve(Expr expr) {
        expr.accept(this);
    }

    private void beginScope() {
        scopes.push(new HashMap<String, Boolean>());
    }

    private void endScope() {
        scopes.pop();
    }

}
