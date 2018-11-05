package Jlox;

import java.util.ArrayList;
import java.util.List;

import static Jlox.TokenType.*;

public class Parser {
    List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;

        for(Token token : tokens) {
//            System.out.println(token + " have it your way");
        }

    }

    private static class ParseError extends RuntimeException {}

    private Expr expression() {
        return assignment();
    }

    private Expr assignment() {
        Expr expr = equality();

        if(match(EQUAL)) {
            Token equals = previous();
            Expr value = assignment();

            if(expr instanceof Expr.Variable) {
                Token name = ((Expr.Variable) expr).name;
                return new Expr.Assign(name, value);
            }

            error(equals, "Invaid assignment ");
        }

        return expr;
    }

    private Expr equality() {
        Expr expr = comparison();

        while(match(BANG_EQUAL, EQUAL_EQUAL)) {
            Token operator = previous();
            Expr right = comparison();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr comparison() {
        Expr expr = addition();

        while (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            Expr right = addition();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr addition() {
        Expr expr = multiplication();

        while (match(MINUS, PLUS)) {
            Token operator = previous();
            Expr right = multiplication();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr multiplication() {
        Expr expr = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            Expr right = unary();

            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    private Expr unary() {
        if(match(BANG, MINUS)) {
            Token operator = previous();
            Expr right = unary();

            return new Expr.Unary(operator, right);
        }

        return primary();
    }

    private Expr primary(){
        if(match(FALSE)) return new Expr.Literal(false);
        if(match(TRUE)) return new Expr.Literal(true);
        if(match(NIL)) return new Expr.Literal(null);
        if(match(NUMBER)) {
//            System.out.println("matched number in parser.primary()");
            return new Expr.Literal(previous().literal);
        }

        if(match(STRING)) return new Expr.Literal(previous().literal);

        if(match(LEFT_PAREN)) {
//            System.out.println("left parens");
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expecting )");
            return new Expr.Grouping(expr);
        }

        if(match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        throw error(peek(), "Expecting expression");

    }

    private Token consume(TokenType type, String message) {
        if(check(type)) return advance();
//        System.out.println("throwing error");
        throw error(peek(), message);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token.line, message);
        return new ParseError();
    }


    private boolean match(TokenType ...tokenTypes) {
        for(TokenType tokenType : tokenTypes) {
            if(check(tokenType)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if(isAtEnd()) return false;
        return type == peek().type;
    }

    private Token advance() {
        if(!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return  peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        advance();

        while (!isAtEnd()) {
            if(previous().type == EOF) return;

            switch (peek().type) {
                case CLASS:
                case FOR:
                case WHILE:
                case VAR:
                case IF:
                case FUN:
                case PRINT:
                case RETURN:
                    return;

            }

            advance();
        }

    }

//    Expr parse() {
//        try {
//
//            return expression();
//        }
//        catch (ParseError error) {
//            return null;
//        }
//    }

    private Stmt statement() {
        if(match(PRINT)) return printStmt();
        if(match(LEFT_BRACE)) return new Stmt.Block(block());
        if(match(IF)) return ifStatement();

        return exprStmt();
    }

    private Stmt ifStatement() {
        consume(LEFT_PAREN, "Expecting ( after if.");

        Expr expr = expression();

        consume(RIGHT_PAREN, "Expecting ) after if condition.");
        Stmt thenStatement = statement();
        Stmt elseStatement = null;
        if(match(ELSE)) {
            elseStatement = statement();
        }

        return new Stmt.If(expr, thenStatement, elseStatement);
    }

    private List<Stmt> block() {
        List<Stmt> stmts = new ArrayList<>();

        while (!check(RIGHT_BRACE) && !isAtEnd()) {
            stmts.add(declaration());
        }

        consume(RIGHT_BRACE, "Expecting } after block");

        return stmts;
    }

    private Stmt printStmt() {
        Expr expr = expression();

        consume(SEMICOLON, "Expecting ; after value.");
        return new Stmt.Print(expr);
    }

    private Stmt exprStmt() {
        Expr expr = expression();

        consume(SEMICOLON, "Expecting ; after value.");
        return new Stmt.Expression(expr);
    }

    private Stmt declaration() {
        try {
            if(match(VAR)) return varDeclaration();

            return statement();

        } catch (ParseError error) {
            synchronize();
            return null;
        }

    }

    private Stmt varDeclaration() {
        Token name = consume(IDENTIFIER, "Expecting variable name.");

        Expr expr = null;
        if(match(EQUAL)) {
            expr = expression();
        }

        consume(SEMICOLON, "Expecting ; after variable declaration.");

        return new Stmt.Var(name, expr);
    }

    List<Stmt> parse() {
        try {
            List<Stmt> stmts = new ArrayList<Stmt>();
            while (!isAtEnd()) {
                stmts.add(declaration());
            }

            return stmts;
        }
        catch (ParseError error) {
//            System.err.println("Error while parsing");
            return null;
        }
    }
}












