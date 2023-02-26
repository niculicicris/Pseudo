package ro.niculici.pseudo.parser.expression;

import java.util.List;

public class ForExpression extends Expression {
    private final AssignmentExpression initialAssignment;
    private final Expression limit;
    private final Expression step;
    private final List<Expression> body;

    public ForExpression(AssignmentExpression initialAssignment, Expression limit, Expression step, List<Expression> body) {
        this.initialAssignment = initialAssignment;
        this.limit = limit;
        this.step = step;
        this.body = body;
    }

    public AssignmentExpression getInitialAssignment() {
        return initialAssignment;
    }

    public Expression getLimit() {
        return limit;
    }

    public Expression getStep() {
        return step;
    }

    public List<Expression> getBody() {
        return body;
    }
}
