package ro.niculici.pseudo.parser.expression;

import java.util.ArrayList;
import java.util.List;

public class IfExpression extends Expression {
    private final Expression condition;
    private final List<Expression> body;
    private final List<Expression> elseBody;

    public IfExpression(Expression condition, List<Expression> body, List<Expression> elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Expression> getBody() {
        return body;
    }

    public List<Expression> getElseBody() {
        return elseBody;
    }
}
