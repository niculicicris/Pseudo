package ro.niculici.pseudo.parser.expression;

import java.util.List;

public class ReadExpression extends Expression {
    private final List<IdentifierExpression> identifiers;

    public ReadExpression(List<IdentifierExpression> identifiers) {
        this.identifiers = identifiers;
    }

    public List<IdentifierExpression> getIdentifiers() {
        return identifiers;
    }
}
