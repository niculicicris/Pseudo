package ro.niculici.pseudo.evaluator;

import ro.niculici.pseudo.error.ErrorManager;
import ro.niculici.pseudo.evaluator.variable.Variable;
import ro.niculici.pseudo.evaluator.variable.VariableManager;
import ro.niculici.pseudo.evaluator.variable.VariableType;
import ro.niculici.pseudo.parser.expression.*;

import java.util.List;
import java.util.Scanner;

public class Evaluator {
    private final String source;
    private final List<Expression> parseTree;
    private VariableManager variableManager;

    public Evaluator(String source, List<Expression> parseTree) {
        this.source = source;
        this.parseTree = parseTree;
    }

    public void evaluate() {
        variableManager = new VariableManager();
        parseTree.forEach(this::evaluateExpression);
    }

    private void evaluateExpression(Expression expression) {
        if (expression instanceof AssignmentExpression assignment)
            evaluateAssignmentExpression(assignment);

        if (expression instanceof ReadExpression read)
            evaluateReadExpression(read);

        if (expression instanceof WriteExpression write)
            evaluateWriteExpression(write);

        if (expression instanceof IfExpression _if)
            evaluateIfExpression(_if);

        if (expression instanceof ForExpression _for)
            evaluateForExpression(_for);

        if (expression instanceof WhileExpression _while)
            evaluateWhileExpression(_while);

        if (expression instanceof DoWhileExpression doWhile)
            evaluateDoWhileExpression(doWhile);

        if (expression instanceof RepeatUntilExpression repeatUntil)
            evaluateRepeatUntilExpression(repeatUntil);
    }

    private void evaluateAssignmentExpression(AssignmentExpression expression) {
        boolean forceConversion = false;

        Variable variable = variableManager.getVariable(expression.getIdentifier().getIdentifier());
        if (variable != null && variable.getType() == VariableType.INTEGER)
            forceConversion = true;

        Expression value = evaluateValueExpression(expression.getValue(), forceConversion);
        if (value instanceof IntegerExpression integer)
            variable = new Variable(VariableType.INTEGER, integer.getValue());
        else
            variable = new Variable(VariableType.REAL, ((RealExpression) value).getValue());

        variableManager.setVariable(expression.getIdentifier().getIdentifier(), variable);
    }

    private Expression evaluateValueExpression(Expression expression, boolean forceConversion) {
        if (expression instanceof IntegerExpression) return expression;
        if (expression instanceof RealExpression) return expression;

        if (expression instanceof IdentifierExpression identifier) {
            if (!variableManager.containsVariable(identifier.getIdentifier())) {
                ErrorManager.throwInvalidVariableError(source, identifier.getLine(), identifier.getIdentifier());
                return identifier;
            }
            Variable variable = variableManager.getVariable(identifier.getIdentifier());

            if (variable.getType() == VariableType.INTEGER)
                return new IntegerExpression((int) variable.getValue());
            else
                return new RealExpression(variable.getValue());
        }
        BinaryExpression binary = (BinaryExpression) expression;

        VariableType resultType = VariableType.INTEGER;
        double leftValue, rightValue, result = 0;

        Expression left = evaluateValueExpression(binary.getLeft(), false);
        Expression right = evaluateValueExpression(binary.getRight(), false);

        leftValue = getNumericalValue(left);
        rightValue = getNumericalValue(right);

        if (left instanceof RealExpression || right instanceof RealExpression)
            resultType = VariableType.REAL;

        switch (binary.getOperator()) {
            case PLUS -> result = leftValue + rightValue;
            case MINUS -> result = leftValue - rightValue;
            case STAR -> result = leftValue * rightValue;
            case DIVIDE -> {
                if (rightValue == 0) {
                    ErrorManager.throwZeroDivisionError(source, binary.getLine());
                    break;
                }

                result = leftValue / rightValue;
            }
            case INTEGER_DIVIDE -> {
                if (resultType == VariableType.REAL) {
                    ErrorManager.throwIntegerDivisionError(source, binary.getLine());
                    break;
                }

                if (rightValue == 0) {
                    ErrorManager.throwZeroDivisionError(source, binary.getLine());
                    break;
                }
                int integerResult = ((int) leftValue) / ((int) rightValue);

                result = integerResult;
            }
            default -> {
                if (resultType == VariableType.REAL) {
                    ErrorManager.throwIntegerDivisionError(source, binary.getLine());
                    break;
                }

                if (rightValue == 0) {
                    ErrorManager.throwZeroDivisionError(source, binary.getLine());
                    break;
                }

                result = ((int) leftValue) % ((int) rightValue);
            }
        }

        if (resultType == VariableType.INTEGER || forceConversion)
            return new IntegerExpression((int) result);
        else
            return new RealExpression(result);
    }

    private void evaluateReadExpression(ReadExpression expression) {
        expression.getIdentifiers().forEach(identifier -> {
            Variable variable = readVariable();
            variableManager.setVariable(identifier.getIdentifier(), variable);
        });
    }

    private Variable readVariable() {
        String word = (new Scanner(System.in)).next();

        try {
            return new Variable(VariableType.INTEGER, Integer.parseInt(word));
        } catch (NumberFormatException ignore) {}

        try {
            return new Variable(VariableType.REAL, Double.parseDouble(word));
        } catch (NumberFormatException ignore) {}

        ErrorManager.throwNumberFormatError(word);

        return null;
    }

    private void evaluateWriteExpression(WriteExpression expression) {
        final StringBuilder builder = new StringBuilder();

        expression.getContents().forEach(content -> {
            if (content instanceof StringExpression string) {
                builder.append(string.getString());
                return;
            }
            Expression value = evaluateValueExpression(content, false);

            if (value instanceof IntegerExpression integer)
                builder.append(integer.getValue());
            else
                builder.append(((RealExpression) value).getValue());
        });

        System.out.println(builder);
    }

    private void evaluateIfExpression(IfExpression expression) {
        BooleanExpression condition = evaluateConditionExpression(expression.getCondition());

        variableManager.increaseScope();
        if (condition.getValue())
            expression.getBody().forEach(this::evaluateExpression);
        else
            expression.getElseBody().forEach(this::evaluateExpression);
        variableManager.decreaseScope();
    }

    private void evaluateForExpression(ForExpression expression) {
        String identifier = expression.getInitialAssignment().getIdentifier().getIdentifier();

        variableManager.increaseScope();

        evaluateAssignmentExpression(expression.getInitialAssignment());
        boolean forceConversion = variableManager.getVariable(identifier).getType() == VariableType.INTEGER;

        double limit = getNumericalValue(evaluateValueExpression(expression.getLimit(), forceConversion));
        double step = getNumericalValue(evaluateValueExpression(expression.getStep(), forceConversion));

        if (step == 0) {
            ErrorManager.throwForLoopStepError();
            return;
        }

        if (step > 0)
            while (variableManager.getVariableValue(identifier) <= limit) {
                expression.getBody().forEach(this::evaluateExpression);

                variableManager.increaseVariableValue(identifier, step);
                limit = getNumericalValue(evaluateValueExpression(expression.getLimit(), forceConversion));
            }
        else
            while (variableManager.getVariableValue(identifier) >= limit) {
                expression.getBody().forEach(this::evaluateExpression);

                variableManager.increaseVariableValue(identifier, step);
                limit = getNumericalValue(evaluateValueExpression(expression.getLimit(), forceConversion));
            }

        variableManager.decreaseScope();
    }

    private void evaluateWhileExpression(WhileExpression expression) {
        BooleanExpression condition = evaluateConditionExpression(expression.getCondition());

        variableManager.increaseScope();
        while (condition.getValue()) {
            expression.getBody().forEach(this::evaluateExpression);
            condition = evaluateConditionExpression(expression.getCondition());
        }
        variableManager.decreaseScope();
    }

    private void evaluateDoWhileExpression(DoWhileExpression expression) {
        BooleanExpression condition;

        variableManager.increaseScope();
        do {
            expression.getBody().forEach(this::evaluateExpression);
            condition = evaluateConditionExpression(expression.getCondition());
        } while (condition.getValue());
        variableManager.decreaseScope();
    }

    private void evaluateRepeatUntilExpression(RepeatUntilExpression expression) {
        BooleanExpression condition;

        variableManager.increaseScope();
        do {
            expression.getBody().forEach(this::evaluateExpression);
            condition = evaluateConditionExpression(expression.getCondition());
        } while (!condition.getValue());
        variableManager.decreaseScope();
    }

    private BooleanExpression evaluateConditionExpression(Expression expression) {
        return new BooleanExpression(getNumericalValue(evaluateConditionValueExpression(expression)) == 1);
    }

    private Expression evaluateConditionValueExpression(Expression expression) {
        if (expression instanceof IntegerExpression) return expression;
        if (expression instanceof RealExpression) return expression;

        if (expression instanceof IdentifierExpression identifier) {
            if (!variableManager.containsVariable(identifier.getIdentifier())) {
                ErrorManager.throwInvalidVariableError(source, identifier.getLine(), identifier.getIdentifier());
                return identifier;
            }
            Variable variable = variableManager.getVariable(identifier.getIdentifier());

            if (variable.getType() == VariableType.INTEGER)
                return new IntegerExpression((int) variable.getValue());
            else
                return new RealExpression(variable.getValue());
        }

        if (expression instanceof UnaryExpression unary) {
            Expression right = evaluateConditionValueExpression(unary.getRight());

            if (right instanceof BooleanExpression bool)
                return new BooleanExpression(!bool.getValue());
            else
                return new BooleanExpression(getNumericalValue(right) == 0);
        }
        BinaryExpression binary = (BinaryExpression) expression;

        int resultType = 0;
        double leftValue, rightValue, result = 0;

        Expression left = evaluateConditionValueExpression(binary.getLeft());
        Expression right = evaluateConditionValueExpression(binary.getRight());

        leftValue = getNumericalValue(left);
        rightValue = getNumericalValue(right);

        if (left instanceof RealExpression || right instanceof RealExpression)
            resultType = 1;

        if (left instanceof BooleanExpression || right instanceof BooleanExpression)
            resultType = 2;

        if (resultType == 2 && !(left instanceof BooleanExpression))
            leftValue = (leftValue == 0) ? 0 : 1;

        if (resultType == 2 && !(right instanceof BooleanExpression))
            leftValue = (leftValue == 0) ? 0 : 1;

        switch (binary.getOperator()) {
            case PLUS -> result = leftValue + rightValue;
            case MINUS -> result = leftValue - rightValue;
            case STAR -> result = leftValue * rightValue;
            case DIVIDE -> {
                if (rightValue == 0) {
                    ErrorManager.throwZeroDivisionError(source, binary.getLine());
                    break;
                }

                result = leftValue / rightValue;
            }
            case INTEGER_DIVIDE -> {
                if (resultType == 1) {
                    ErrorManager.throwIntegerDivisionError(source, binary.getLine());
                    break;
                }

                if (rightValue == 0) {
                    ErrorManager.throwZeroDivisionError(source, binary.getLine());
                    break;
                }
                int integerResult = ((int) leftValue) / ((int) rightValue);

                result = integerResult;
            }
            case MODULO -> {
                if (resultType == 1) {
                    ErrorManager.throwIntegerDivisionError(source, binary.getLine());
                    break;
                }

                if (rightValue == 0) {
                    ErrorManager.throwZeroDivisionError(source, binary.getLine());
                    break;
                }

                result = ((int) leftValue) % ((int) rightValue);
            }

            case LESS -> {
                boolean booleanResult = leftValue < rightValue;
                result = booleanResult ? 1 : 0;
            }

            case LESS_OR_EQUAL -> {
                boolean booleanResult = leftValue <= rightValue;
                result = booleanResult ? 1 : 0;
            }

            case EQUAL -> {
                boolean booleanResult = leftValue == rightValue;
                result = booleanResult ? 1 : 0;
            }

            case NOT_EQUAL -> {
                boolean booleanResult = leftValue != rightValue;
                result = booleanResult ? 1 : 0;
            }

            case GREATER -> {
                boolean booleanResult = leftValue > rightValue;
                result = booleanResult ? 1 : 0;
            }

            case GREATER_OR_EQUAL -> {
                boolean booleanResult = leftValue >= rightValue;
                result = booleanResult ? 1 : 0;
            }

            case AND -> result = (leftValue == 1 && rightValue == 1) ? 1 : 0;

            case OR -> result = (leftValue == 1 || rightValue == 1) ? 1 : 0;
        }

        if (resultType == 0)
            return new IntegerExpression((int) result);
        else if (resultType == 1)
            return new RealExpression(result);
        else
            return new BooleanExpression(result == 1);
    }

    private double getNumericalValue(Expression expression) {
        if (expression instanceof IntegerExpression integer)
            return integer.getValue();
        else if (expression instanceof RealExpression real)
            return real.getValue();
        else
            return ((BooleanExpression) expression).getValue() ? 1 : 0;
    }
}
