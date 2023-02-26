package ro.niculici.pseudo.evaluator.variable;

import ro.niculici.pseudo.evaluator.variable.VariableType;

public class Variable {
    private final VariableType type;
    private final double value;

    public Variable(VariableType type, double value) {
        this.type = type;
        this.value = value;
    }

    public VariableType getType() {
        return type;
    }

    public double getValue() {
        return value;
    }
}
