package ro.niculici.pseudo.parser.expression;

import java.util.List;

public class WriteExpression extends Expression {
    private final List<Expression> contents;

    public WriteExpression(List<Expression> contents) {
        this.contents = contents;
    }

    public List<Expression> getContents() {
        return contents;
    }
}
