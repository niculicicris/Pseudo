package ro.niculici.pseudo.parser.expression;

public class IdentifierExpression extends Expression {
    private final String identifier;

    public IdentifierExpression(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
