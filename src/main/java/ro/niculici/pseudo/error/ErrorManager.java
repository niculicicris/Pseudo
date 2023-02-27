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

    public static void throwUnexpectedTokenError(String source, Token token, String expected) {
        String sourceLine = source.split("\n")[token.line()];

        System.out.println("(" + token.line() + ":" + token.column() + ") EROARE: Un simbol neasteptat a fost gasit. Se astepta " +
                "un simbol de tip '" + expected + "'.");
        System.out.println(sourceLine);
        System.out.println(" ".repeat(token.column()) + "~".repeat(token.text().length()));

        System.exit(0);
    }

    public static void throwIndentError(String source, int line) {
        String sourceLine = source.split("\n")[line];

        System.out.println("EROARE: Indentarea de pe linia " + line + " nu este valida.");
        System.out.println(sourceLine);
        for (char ch : sourceLine.toCharArray()) {
            if (ch != ' ') break;
            System.out.print("~");
        }
        System.out.println();

        System.exit(0);
    }

    public static void throwNumberFormatError(String word) {
        System.out.println("EROARE: '" + word + "' nu este un numar valid.");
        System.exit(0);
    }

    public static void throwInvalidVariableError(String source, int line, String identifier) {
        System.out.println("EROARE: '" + identifier + "' de pe linia " + line + " nu este un identificator pentru o variabila.");
        System.out.println(source.split("\n")[line]);

        System.exit(0);
    }

    public static void throwIntegerDivisionError(String source, int line) {
        System.out.println("EROARE: Impartirea numerelor intregi de pe linia " + line + " nu poate sa fie efectuata pe valori reale.");
        System.out.println(source.split("\n")[line]);

        System.exit(0);
    }

    public static void throwZeroDivisionError(String source, int line) {
        System.out.println("EROARE: Impartirea de pe linia " + line + " nu poate sa fie facuta la 0.");
        System.out.println(source.split("\n")[line]);

        System.exit(0);
    }

    public static void throwForLoopStepError() {
        System.out.print("EROARE: Pasul la un ciclu pentru nu poate sa fie egal cu 0");
        System.exit(0);
    }
}
