package ro.niculici.pseudo.lexer;

import ro.niculici.pseudo.error.ErrorManager;
import ro.niculici.pseudo.lexer.token.Token;
import ro.niculici.pseudo.lexer.token.TokenType;

import javax.lang.model.SourceVersion;
import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;

    List<Token> tokens;
    private int index;
    private int line;
    private int column;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> getTokens() {
        tokens = new ArrayList<>();
        index = 0; line = 0; column = 0;

        while (index < source.length())
            nextToken();
        addToken(TokenType.EOF, "\0");

        return tokens;
    }

    private void nextToken() {
        if (match(' ')) return;
        int startColumn = column;
        char ch = advance();

        if (nextIsEndLine(ch)) return;
        if (nextIsSmallToken(ch, startColumn)) return;
        if (nextIsStringToken(ch)) return;
        startColumn = column - 1;
        String word = advanceWord(ch);

        if (nextIsWordToken(word, startColumn)) return;
        if (nextIsInteger(word, startColumn)) return;
        if (nextIsDouble(word, startColumn)) return;
        if (nextIsIdentifier(word, startColumn)) return;

        ErrorManager.throwUnknownTokenError(source, word, line, startColumn);
    }

    private boolean nextIsEndLine(char ch) {
        if (ch != '\n') return false;
        addToken(TokenType.EOL, "\n");
        ++line; column = 0;

        return true;
    }

    private boolean nextIsSmallToken(char ch, int startColumn) {
        switch (ch) {
            case ',' -> {
                addToken(TokenType.COMMA, ",", line, startColumn);
                return true;
            }
            case '+' -> {
                addToken(TokenType.PLUS, "+", line, startColumn);
                return true;
            }
            case '-' -> {
                addToken(TokenType.MINUS, "-", line, startColumn);
                return true;
            }
            case '*' -> {
                addToken(TokenType.STAR, "*", line, startColumn);
                return true;
            }
            case '/' -> {
                addToken(TokenType.DIVIDE, "/", line, startColumn);
                return true;
            }
            case '%' -> {
                addToken(TokenType.MODULO, "%", line, startColumn);
                return true;
            }
            case '(' -> {
                addToken(TokenType.OPEN_BRACKET, "(", line, startColumn);
                return true;
            }
            case ')' -> {
                addToken(TokenType.CLOSE_BRACKET, ")", line, startColumn);
                return true;
            }
            case '[' -> {
                addToken(TokenType.OPEN_SQUARE_BRACKET, "[", line, startColumn);
                return true;
            }
            case ']' -> {
                addToken(TokenType.CLOSE_SQUARE_BRACKET, "]", line, startColumn);
                return true;
            }
            case '<' -> {
                if (match('='))
                    addToken(TokenType.LESS_OR_EQUAL, "<=", line, startColumn);
                else if (match('-'))
                    addToken(TokenType.ASSIGNMENT, "<-", line, startColumn);
                else
                    addToken(TokenType.LESS, "<", line, startColumn);

                return true;
            }
            case '>' -> {
                if (match('='))
                    addToken(TokenType.GREATER_OR_EQUAL, ">=", line, startColumn);
                else
                    addToken(TokenType.GREATER, ">", line, startColumn);

                return true;
            }
            case '=' -> {
                addToken(TokenType.EQUAL, "=", line, startColumn);
                return true;
            }
            case '!' -> {
                if (match('='))
                    addToken(TokenType.NOT_EQUAL, "!=", line, startColumn);
                else
                    addToken(TokenType.NOT, "!", line, startColumn);

                return true;
            }
        }

        return false;
    }

    private boolean nextIsStringToken(char ch) {
        if (ch != '\'') return false;
        int start = index - 1;
        int startColumn = column - 1;

        ch = advance();
        while (index < source.length() && ch != '\n' && ch != '\'')
            ch = advance();

        if (ch == '\n') {
            --index; --column;
        }

        if (ch != '\'') {
            ErrorManager.throwInvalidStringError(source, line, startColumn);
            return false;
        }
        addToken(TokenType.STRING, source.substring(start, index), line, startColumn);

        return true;
    }

    public boolean nextIsWordToken(String word, int column) {
        switch (word) {
            case "DIV" -> {
                addToken(TokenType.INTEGER_DIVIDE, word, line, column);
                return true;
            }

            case "MOD" -> {
                addToken(TokenType.MODULO, word, line, column);
                return true;
            }

            case "citeste" -> {
                addToken(TokenType.READ, word, line, column);
                return true;
            }

            case "scrie", "tipareste" -> {
                addToken(TokenType.WRITE, word, line, column);
                return true;
            }

            case "daca" -> {
                addToken(TokenType.IF, word, line, column);
                return true;
            }

            case "altfel" -> {
                addToken(TokenType.ELSE, word, line, column);
                return true;
            }

            case "SI", "AND", "&&" -> {
                addToken(TokenType.AND, word, line, column);
                return true;
            }

            case "SAU", "||" -> {
                addToken(TokenType.OR, word, line, column);
                return true;
            }

            case "NOT" -> {
                addToken(TokenType.NOT, word, line, column);
                return true;
            }

            case "pentru" -> {
                addToken(TokenType.FOR, word, line, column);
                return true;
            }

            case "cattimp" -> {
                addToken(TokenType.WHILE, word, line, column);
                return true;
            }

            case "cat" -> {
                addToken(TokenType.WHILE_1, word, line, column);
                return true;
            }

            case "timp" -> {
                addToken(TokenType.WHILE_2, word, line, column);
                return true;
            }

            case "repeta" -> {
                addToken(TokenType.REPEAT, word, line, column);
                return true;
            }

            case "atunci" -> {
                addToken(TokenType.THEN, word, line, column);
                return true;
            }

            case "executa" -> {
                addToken(TokenType.EXECUTE, word, line, column);
                return true;
            }

            case "panacand" -> {
                addToken(TokenType.UNTIL, word, line, column);
                return true;
            }

            case "pana" -> {
                addToken(TokenType.UNTIL_1, word, line, column);
                return true;
            }

            case "cand" -> {
                addToken(TokenType.UNTIL_2, word, line, column);
                return true;
            }

            case "sfarsit" -> {
                addToken(TokenType.END, word, line, column);
                return true;
            }
        }

        return false;
    }

    private boolean nextIsInteger(String word, int column) {
        try {
            Integer.parseInt(word);
            addToken(TokenType.INTEGER, word, line, column);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean nextIsDouble(String word, int column) {
        try {
            Double.parseDouble(word);
            addToken(TokenType.REAL, word, line, column);

            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean nextIsIdentifier(String word, int column) {
        if (!SourceVersion.isIdentifier(word) || SourceVersion.isKeyword(word)) return false;
        addToken(TokenType.IDENTIFIER, word, line, column);

        return true;
    }

    private void addToken(TokenType type, String text) {
        tokens.add(new Token(type, text, line, column));
    }

    private void addToken(TokenType type, String text, int line, int column) {
        tokens.add(new Token(type, text, line, column));
    }

    public char advance() {
        ++index; ++column;
        return source.charAt(index - 1);
    }

    public String advanceWord(char ch) {
        if (index >= source.length()) return String.valueOf(ch);
        StringBuilder word = new StringBuilder(String.valueOf(ch));

        ch = advance();
        while (index < source.length() && !",+-*/%()[]<>=!'\n ".contains(String.valueOf(ch))) {
            word.append(ch);
            ch = advance();
        }

        if (!",+-*/%()[]<>=!'\n ".contains(String.valueOf(ch)))
            word.append(ch);
        else {
            --index; --column;
        }

        return word.toString();
    }

    public boolean match(char ch) {
        if (index >= source.length()) return false;
        if (source.charAt(index) != ch) return false;
        ++index; ++column;

        return true;
    }
}
