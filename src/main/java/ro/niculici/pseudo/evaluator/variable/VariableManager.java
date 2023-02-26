package ro.niculici.pseudo.evaluator.variable;

import java.util.HashMap;

public class VariableManager {
    private final HashMap<Integer, HashMap<String, Variable>> variables = new HashMap<>();
    private int scope = 0;

    public VariableManager() {
        variables.put(scope, new HashMap<>());
    }

    public void increaseScope() {
        variables.put(++scope, new HashMap<>());
    }

    public void decreaseScope() {
        variables.remove(scope--);
    }

    public boolean containsVariable(String identifier) {
        return variables.values().stream().anyMatch(map -> map.containsKey(identifier));
    }

    public Variable getVariable(String identifier) {
        Variable variable = null;

        for (var map : variables.values()) {
            if (!map.containsKey(identifier)) continue;
            variable = map.get(identifier);

            break;
        }

        return variable;
    }

    public double getVariableValue(String identifier) {
        return getVariable(identifier).getValue();
    }

    public void setVariable(String identifier, Variable variable) {
        for (var map : variables.values()) {
            if (!map.containsKey(identifier)) continue;
            map.put(identifier, variable);

            return;
        }

        variables.get(scope).put(identifier, variable);
    }

    public void increaseVariableValue(String identifier, double step) {
        Variable variable = getVariable(identifier);
        setVariable(identifier, new Variable(variable.getType(), variable.getValue() + step));
    }
}
