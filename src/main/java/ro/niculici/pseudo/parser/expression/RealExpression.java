package ro.niculici.pseudo.parser.expression;

public class RealExpression extends Expression {
    private final double value;

    public RealExpression(String stringValue) {
        this.value = Double.parseDouble(stringValue);
    }

    public RealExpression(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }
}
