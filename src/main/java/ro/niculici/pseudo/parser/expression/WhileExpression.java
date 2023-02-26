package ro.niculici.pseudo.parser.expression;

import java.util.List;

public class WhileExpression extends Expression {
    private final Expression condition;
    private final List<Expression> body;

    public WhileExpression(Expression condition, List<Expression> body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Expression> getBody() {
        return body;
    }
}
