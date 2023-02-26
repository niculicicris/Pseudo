package ro.niculici.pseudo.parser.expression;

import java.util.List;

public class RepeatUntilExpression extends Expression {
    private final List<Expression> body;
    private final Expression condition;

    public RepeatUntilExpression(List<Expression> body, Expression condition) {
        this.body = body;
        this.condition = condition;
    }

    public List<Expression> getBody() {
        return body;
    }

    public Expression getCondition() {
        return condition;
    }
}
