package ro.niculici.pseudo.lexer.token;

public record Token(TokenType type, String text, int line, int column) {}
