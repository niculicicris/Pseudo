package ro.niculici.pseudo.app;

import ro.niculici.pseudo.error.ErrorManager;
import ro.niculici.pseudo.evaluator.Evaluator;
import ro.niculici.pseudo.file.Source;
import ro.niculici.pseudo.lexer.Lexer;
import ro.niculici.pseudo.lexer.token.Token;
import ro.niculici.pseudo.parser.Parser;
import ro.niculici.pseudo.parser.expression.*;

import java.util.List;

public class Interpreter {

    public void interpret(String[] args) {
        if (args.length != 1) {
            ErrorManager.throwInvalidArgumentsError();
            return;
        }
        String source = (new Source(args[0])).getSource();

        if (source == null) {
            ErrorManager.throwInvalidFileError(args[0]);
            return;
        }
        source = source.replace("\r\n", "\n");

        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.getTokens();

        Parser parser = new Parser(source, tokens);
        List<Expression> parseTree = parser.getParseTree();

        Evaluator evaluator = new Evaluator(source, parseTree);
        evaluator.evaluate();
    }
}
