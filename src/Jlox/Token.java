package Jlox;


enum TokenType {

    //single chars
    LEFT_PAREN, RIGHT_PAREN, LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,

    //one or two chars
    BANG, BANG_EQUAL, EQUAL, EQUAL_EQUAL,
    GREATER, LESS, GREATER_EQUAL, LESS_EQUAL,

    //Literals
    IDENTIFIER, STRING, NUMBER,

    //Keywords
    AND, OR, CLASS, FUN, IF, ELSE, FOR, WHILE, NIL,
    PRINT, RETURN, SUPER, THIS, TRUE, FALSE, VAR,

    EOF

}

public class Token {
    TokenType type;
    String lexeme;
    Object literal;
    int line;

    Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return type + " " + lexeme + " " + literal;
    }
}
