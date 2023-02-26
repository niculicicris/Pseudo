package ro.niculici.pseudo.parser.expression;

public class AssignmentExpression extends Expression {
    private final IdentifierExpression identifier;
    private final Expression value;

    public AssignmentExpression(String identifier, Expression value) {
        this.identifier = new IdentifierExpression(identifier);
        this.value = value;
    }

    public IdentifierExpression getIdentifier() {
        return identifier;
    }

    public Expression getValue() {
        return value;
    }
}
