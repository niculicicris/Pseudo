package ro.niculici.pseudo.parser.expression;

import java.util.List;

public class DoWhileExpression extends Expression {
    private final List<Expression> body;
    private final Expression condition;

    public DoWhileExpression(List<Expression> body, Expression condition) {
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
