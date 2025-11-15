import Lexer.*;
import Parser.*;
import Ast.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("Text eingeben (EOF mit Strg+D/Strg+Z):");

        Scanner scanner = new Scanner(System.in);
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            sb.append(scanner.nextLine()).append("\n");
        }
        scanner.close();

        String input = sb.toString();

        // ---------------- Lexer ----------------
        System.out.println("\n--- Lexer-Ausgabe ---");
        Lexer lexer = new Lexer(input);
        Token t;
        boolean lexerError = false;
        do {
            t = lexer.nextToken();
            System.out.println(t);
            if (t.type == TokenType.INVALID) {
                System.err.println("Lexer-Fehler entdeckt, Abbruch.");
                lexerError = true;
                break;
            }
        } while (t.type != TokenType.EOF);

        if (lexerError) return;

        // ---------------- Parser + AST ----------------
        System.out.println("\n--- Parser / AST-Ausgabe ---");
        lexer = new Lexer(input); // reset lexer for parser
        Parser parser = new Parser(lexer);
        Program program = parser.parseStart();

        String pretty = Ast.ASTPrinter.printProgram(program);
        System.out.println(pretty);
    }
}
