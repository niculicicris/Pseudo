package ro.niculici.pseudo.parser;

import ro.niculici.pseudo.error.ErrorManager;
import ro.niculici.pseudo.lexer.token.Token;
import ro.niculici.pseudo.lexer.token.TokenType;
import ro.niculici.pseudo.parser.expression.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final String source;
    private final List<Token> tokens;

    private List<Expression> parseTree;
    private int index;
    private  int indentLevel = 0;

    public Parser(String source, List<Token> tokens) {
        this.source = source;
        this.tokens = tokens;
    }

    public List<Expression> getParseTree() {
        parseTree = new ArrayList<>();
        index = 0; indentLevel = 0;

        while (index < tokens.size())
            parseNextExpression(true);

        return parseTree;
    }

    private boolean parseNextExpression(boolean throwIndentError) {
        Token token = advance();

        if (token.type() == TokenType.EOL || token.type() == TokenType.EOF) return false;

        if (throwIndentError && !hasValidIndent(token)) {
            ErrorManager.throwIndentError(source, token.line());
            return false;
        }

        if (parseAssignmentExpression(token.type())) return true;
        if (parseReadExpression(token.type())) return true;
        if (parseWriteExpression(token.type())) return true;
        if (parseIfExpression(token.type())) return true;
        if (parseForExpression(token.type())) return true;
        if (parseWhileExpression(token.type())) return true;
        if (parseDoWhileExpression(token.type())) return true;
        if (parseRepeatUntilExpression(token.type())) return true;

        ErrorManager.throwUnexpectedTokenError(source, token);

        return false;
    }

    private boolean parseAssignmentExpression(TokenType type) {
        if (type != TokenType.IDENTIFIER) return false;

        if (!match(TokenType.ASSIGNMENT)) {
            throwUnexpectedTokenError("<-");
            return false;
        }
        parseTree.add(new AssignmentExpression(peekBackwards(2).text(), peekBackwards(1).line(),
                parseValueExpression(2, 2)));

        if (!match(TokenType.EOL, TokenType.EOF))
            throwUnexpectedTokenError("sfarsit de linie");

        return true;
    }

    private Expression parseValueExpression(int priority, int originalPriority) {
        if (priority == 5 && match(TokenType.NOT)) {
            Expression right = parseValueExpression(priority, priority);
            return new UnaryExpression(TokenType.NOT, right);
        }
        Expression left = (priority == 1) ? parseValue(originalPriority)
                : parseValueExpression(priority - 1, priority);

        while (matchPriority(priority)) {
            TokenType operator = peekBackwards(1).type();
            Expression right = (priority == 1) ? parseValue(originalPriority)
                    : parseValueExpression(priority - 1, priority);

            left = new BinaryExpression(left, operator, right, getToken().line());
        }

        return left;
    }

    private boolean matchPriority(int priority) {

        switch (priority) {
            case 1 -> {
                return match(TokenType.STAR, TokenType.DIVIDE, TokenType.INTEGER_DIVIDE, TokenType.MODULO);
            }

            case 2 -> {
                return match(TokenType.PLUS, TokenType.MINUS);
            }

            case 3 -> {
                return match(TokenType.LESS, TokenType.LESS_OR_EQUAL, TokenType.EQUAL,
                        TokenType.NOT_EQUAL, TokenType.GREATER, TokenType.GREATER_OR_EQUAL);
            }

            default -> {
                return match(TokenType.AND, TokenType.OR);
            }
        }
    }

    private Expression parseValue(int originalPriority) {
        if (match(TokenType.INTEGER)) return new IntegerExpression(peekBackwards(1).text());
        if (match(TokenType.REAL)) return new RealExpression(peekBackwards(1).text());
        if (match(TokenType.IDENTIFIER)) return new IdentifierExpression(peekBackwards(1).text(), peekBackwards(1).line());
        if (match(TokenType.OPEN_BRACKET)) {
            Expression value = parseValueExpression(originalPriority, originalPriority);

            if (!match(TokenType.CLOSE_BRACKET))
                throwUnexpectedTokenError(")");

            return value;
        }

        if (match(TokenType.OPEN_SQUARE_BRACKET)) {
            Expression left = parseValue(originalPriority);

            if (!match(TokenType.DIVIDE))
                throwUnexpectedTokenError("/");

            Expression right = parseValue(originalPriority);

            if (!match(TokenType.CLOSE_SQUARE_BRACKET))
                throwUnexpectedTokenError("]");

            return new BinaryExpression(left, TokenType.INTEGER_DIVIDE, right, getToken().line());
        }
        throwUnexpectedTokenError("identificator sau valoare numerica");

        return null;
    }

    private boolean parseReadExpression(TokenType type) {
        if (type != TokenType.READ) return false;
        List<IdentifierExpression> identifiers = new ArrayList<>();

        if (!match(TokenType.IDENTIFIER)) {
            throwUnexpectedTokenError("identificator");
            return false;
        }
        identifiers.add(new IdentifierExpression(peekBackwards(1).text(), peekBackwards(1).line()));

        boolean nextIsComma = true;
        while (!match(TokenType.EOL, TokenType.EOF)) {
            if (nextIsComma && !match(TokenType.COMMA)) {
                throwUnexpectedTokenError(",");
                return false;
            }

            if (!nextIsComma && !match(TokenType.IDENTIFIER)) {
                throwUnexpectedTokenError("identificator");
                return false;
            }

            if (!nextIsComma)
                identifiers.add(new IdentifierExpression(peekBackwards(1).text(), peekBackwards(1).line()));

            nextIsComma = !nextIsComma;
        }

        parseTree.add(new ReadExpression(identifiers));

        return true;
    }

    private boolean parseWriteExpression(TokenType type) {
        if (type != TokenType.WRITE) return false;
        List<Expression> contents = new ArrayList<>();

        if (match(TokenType.STRING))
            contents.add(new StringExpression(peekBackwards(1).text()));
        else
            contents.add(parseValueExpression(2, 2));

        boolean nextIsComma = true;
        while (!match(TokenType.EOL, TokenType.EOF)) {
            if (nextIsComma && !match(TokenType.COMMA)) {
                throwUnexpectedTokenError(",");
                return false;
            }

            if (!nextIsComma) {
                if (match(TokenType.STRING))
                    contents.add(new StringExpression(peekBackwards(1).text()));
                else
                    contents.add(parseValueExpression(2, 2));

            }

            nextIsComma = !nextIsComma;
        }

        parseTree.add(new WriteExpression(contents));

        return true;
    }

    private boolean parseIfExpression(TokenType type) {
        if (type != TokenType.IF) return false;
        Expression condition = parseValueExpression(4, 4);

        if (!match(TokenType.THEN)) {
            throwUnexpectedTokenError("atunci");
            return false;
        }

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }
        List<Expression> body = parseBody();
        List<Expression> elseBody = new ArrayList<>();

        if (match(TokenType.ELSE)) {
            if (!hasValidIndent(peekBackwards(1))) {
                ErrorManager.throwIndentError(source, peekBackwards(1).line());
                return true;
            }

            if (!match(TokenType.EOL, TokenType.EOF)) {
                throwUnexpectedTokenError("sfarsit de linie");
                return false;
            }

            elseBody = parseBody();
        }

        parseEnd(type, "daca");
        parseTree.add(new IfExpression(condition, body, elseBody));

        return true;
    }

    private boolean parseForExpression(TokenType type) {
        if (type != TokenType.FOR) return false;

        if (!match(TokenType.IDENTIFIER)) {
            throwUnexpectedTokenError("identificator");
            return false;
        }

        if (!match(TokenType.ASSIGNMENT)) {
            throwUnexpectedTokenError("<-");
            return false;
        }
        AssignmentExpression assignment = new AssignmentExpression(peekBackwards(2).text(), peekBackwards(1).line(),
                parseValueExpression(2, 2));

        if (!match(TokenType.COMMA)) {
            throwUnexpectedTokenError(",");
            return false;
        }
        Expression limit = parseValueExpression(2, 2);
        Expression step = new IntegerExpression("1");

        if (match(TokenType.COMMA)) step = parseValueExpression(2, 2);

        if (!match(TokenType.EXECUTE)) {
            throwUnexpectedTokenError("executa");
            return false;
        }

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }
        List<Expression> body = parseBody();

        parseEnd(type, "pentru");
        parseTree.add(new ForExpression(assignment, limit, step, body));

        return true;
    }

    private boolean parseWhileExpression(TokenType type) {
        if (type != TokenType.WHILE) return false;
        Expression condition = parseValueExpression(4, 4);

        if (!match(TokenType.EXECUTE)) {
            throwUnexpectedTokenError("executa");
            return false;
        }

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }
        List<Expression> body = parseBody();

        parseEnd(type, "cattimp");
        parseTree.add(new WhileExpression(condition, body));

        return true;
    }

    private boolean parseDoWhileExpression(TokenType type) {
        if (type != TokenType.EXECUTE) return false;

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }
        List<Expression> body = parseBody();

        if (!match(TokenType.WHILE)) {
            throwUnexpectedTokenError("cattimp");
            return false;
        }

        if (!hasValidIndent(peekBackwards(1))) {
            ErrorManager.throwIndentError(source, peekBackwards(1).line());
            return false;
        }
        Expression condition = parseValueExpression(4, 4);

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }

        parseTree.add(new DoWhileExpression(body, condition));

        return true;
    }

    private boolean parseRepeatUntilExpression(TokenType type) {
        if (type != TokenType.REPEAT) return false;

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }
        List<Expression> body = parseBody();

        if (!match(TokenType.UNTIL)) {
            throwUnexpectedTokenError("panacand");
            return false;
        }

        if (!hasValidIndent(peekBackwards(1))) {
            ErrorManager.throwIndentError(source, peekBackwards(1).line());
            return false;
        }
        Expression condition = parseValueExpression(4, 4);

        if (!match(TokenType.EOL, TokenType.EOF)) {
            throwUnexpectedTokenError("sfarsit de linie");
            return false;
        }

        parseTree.add(new RepeatUntilExpression(body, condition));

        return true;
    }
    
    private List<Expression> parseBody() {
        List<Expression> body = new ArrayList<>();

        ++indentLevel;
        while (index < tokens.size() && hasValidIndent(getToken())) {
            if (!parseNextExpression(false)) continue;

            body.add(parseTree.get(parseTree.size() - 1));
            parseTree.remove(parseTree.size() - 1);
        }
        --indentLevel;

        return body;
    }

    private void parseEnd(TokenType type, String expected) {
        if (!match(TokenType.END)) return;

        if (!hasValidIndent(peekBackwards(1))) {
            ErrorManager.throwIndentError(source, peekBackwards(1).line());
            return;
        }

        if (!match(type)) {
            throwUnexpectedTokenError(expected);
            return;
        }

        if (!match(TokenType.EOL, TokenType.EOF))
            throwUnexpectedTokenError("sfarsit de linie");
    }

    private void throwUnexpectedTokenError(String expected) {
        ErrorManager.throwUnexpectedTokenError(source, getToken(), expected);
    }

    private Token getToken() {
        return tokens.get(index);
    }

    private boolean hasValidIndent(Token token) {
        if (token.type() == TokenType.EOL || token.type() == TokenType.EOF) return true;
        return token.column() % 4 == 0 && token.column() / 4 == indentLevel;
    }

    private Token advance() {
        return tokens.get(index++);
    }

    private Token peekBackwards(int depth) {
        return tokens.get(index - depth);
    }

    private boolean match(TokenType ... types) {
        if (index >= tokens.size()) return false;
        if (!List.of(types).contains(getToken().type())) return false;
        ++index;

        return true;
    }
}
