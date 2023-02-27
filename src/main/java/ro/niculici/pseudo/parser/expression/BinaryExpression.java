package ro.niculici.pseudo.parser.expression;

import ro.niculici.pseudo.lexer.token.TokenType;

public class BinaryExpression extends Expression {
    private final Expression left;
    private final TokenType operator;
    private final Expression right;
    private final int line;

    public BinaryExpression(Expression left, TokenType operator, Expression right, int line) {
        this.left = left;
        this.operator = operator;
        this.right = right;
        this.line = line;
    }

    public Expression getLeft() {
        return left;
    }

    public TokenType getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    public int getLine() {
        return line;
    }
}
