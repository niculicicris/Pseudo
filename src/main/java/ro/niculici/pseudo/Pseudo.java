package ro.niculici.pseudo;


import ro.niculici.pseudo.app.Interpreter;

public final class Pseudo {

    public static void main(String[] args) {
        Interpreter interpreter = new Interpreter();
        interpreter.interpret(args);
    }
}