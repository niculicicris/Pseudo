package ro.niculici.pseudo.parser.expression;

public class StringExpression extends Expression {
    private final String string;

    public StringExpression(String string) {
        if (string.equals("''")) this.string = "";
        else this.string = string.replaceAll("'", "");
    }

    public String getString() {
        return string;
    }
}
