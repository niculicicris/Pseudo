package ro.niculici.pseudo.parser.expression;

public class IntegerExpression extends Expression {
    private final int value;

    public IntegerExpression(String stringValue) {
        this.value = Integer.parseInt(stringValue);
    }

    public IntegerExpression(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
