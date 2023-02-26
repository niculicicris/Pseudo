package ro.niculici.pseudo.error;

import ro.niculici.pseudo.lexer.token.Token;

public class ErrorManager {

    public static void throwInvalidArgumentsError() {
        System.out.println("UTILIZARE: pseudo <fisier>");
        System.exit(0);
    }

    public static void throwInvalidFileError(String fileName) {
        System.out.println("EROARE: Fisierul '" + fileName + "' nu a putut sa fie accesat sau nu exista.");
        System.exit(0);
    }

    public static void throwInvalidStringError(String source, int line, int column) {
        String sourceLine = source.split("\n")[line];

        System.out.println("(" + line + ":" + column + ") EROARE: Sirul de caractere nu este complet.");
        System.out.println(sourceLine);
        System.out.println(" ".repeat(column) + "~".repeat(sourceLine.length() - column));

        System.exit(0);
    }

    public static void throwUnknownTokenError(String source, String word, int line, int column) {
        String sourceLine = source.split("\n")[line];

        System.out.println("(" + line + ":" + column + ") EROARE: Un simbol necunoscut a fost gasit.");
        System.out.println(sourceLine);
        System.out.println(" ".repeat(column) + "~".repeat(word.length()));

        System.exit(0);
    }

    public static void throwUnexpectedTokenError(String source, Token token) {
        String sourceLine = source.split("\n")[token.line()];

        System.out.println("(" + token.line() + ":" + token.column() + ") EROARE: Un simbol neasteptat a fost gasit.");
        System.out.println(sourceLine);
        System.out.println(" ".repeat(token.column()) + "~".repeat(token.text().length()));

        System.exit(0);
    }

    public static void throwIndentError(String source, int line) {
        System.out.println("(" + line + ") EROARE: Indentarea de pe aceasta linie nu este valida.");
        System.out.println(source.split("\n")[line]);

        System.exit(0);
    }

    public static void throwNumberFormatError(String word) {
        System.out.println("EROARE: '" + word + "' nu este un numar valid.");
        System.exit(0);
    }

    public static void throwInvalidVariableError(String identifier) {
        System.out.println("EROARE: '" + identifier + "' nu este un identificator pentru o variabila.");
        System.exit(0);
    }

    public static void throwIntegerDivisionError() {
        System.out.println("EROARE: Impartirea numerelor intregi nu poate sa fie efectuata pe valori reale.");
        System.exit(0);
    }

    public static void throwZeroDivisionError() {
        System.out.print("EROARE: Impartirea la 0 este nedefinita.");
        System.exit(0);
    }

    public static void throwForLoopStepError() {
        System.out.print("EROARE: Pasul la un ciclu pentru nu poate sa fie egal cu 0");
        System.exit(0);
    }
}
