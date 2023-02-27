package ro.niculici.pseudo.parser.expression;

public class AssignmentExpression extends Expression {
    private final IdentifierExpression identifier;
    private final Expression value;

    public AssignmentExpression(String identifier, int line, Expression value) {
        this.identifier = new IdentifierExpression(identifier, line);
        this.value = value;
    }

    public IdentifierExpression getIdentifier() {
        return identifier;
    }

    public Expression getValue() {
        return value;
    }
}
