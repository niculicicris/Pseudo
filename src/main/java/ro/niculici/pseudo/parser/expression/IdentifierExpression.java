package ro.niculici.pseudo.parser.expression;

public class IdentifierExpression extends Expression {
    private final String identifier;
    private final int line;

    public IdentifierExpression(String identifier, int line) {
        this.identifier = identifier;
        this.line = line;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getLine() {
        return line;
    }
}
