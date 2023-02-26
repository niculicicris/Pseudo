package ro.niculici.pseudo.parser.expression;

public class BooleanExpression extends Expression {
    private final boolean value;

    public BooleanExpression(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }
}
