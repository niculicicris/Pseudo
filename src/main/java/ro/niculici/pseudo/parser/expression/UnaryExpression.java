package ro.niculici.pseudo.parser.expression;

import ro.niculici.pseudo.lexer.token.TokenType;

public class UnaryExpression extends Expression {
    private final TokenType operator;
    private final Expression right;

    public UnaryExpression(TokenType operator, Expression right) {
        this.operator = operator;
        this.right = right;
    }

    public TokenType getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }
}
