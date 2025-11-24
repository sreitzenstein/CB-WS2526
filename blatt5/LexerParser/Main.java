package LexerParser;

import org.antlr.v4.runtime.*;

import java.io.*;
import java.util.List;



public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== MiniC AST Builder ===");
            System.out.println("Geben Sie MiniC-Code ein und beenden Sie mit Strg+D (Unix/Mac) oder Strg+Z (Windows)");
            System.out.println();
            
            // Lese von stdin
            CharStream input = CharStreams.fromStream(System.in);
            
            // Lexer erstellen
            MiniCLexer lexer = new MiniCLexer(input);
            
            // Token-Stream
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            
            // Parser erstellen
            MiniCParser parser = new MiniCParser(tokens);
            
            // Fehlerbehandlung
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                      Object offendingSymbol,
                                      int line, int charPositionInLine,
                                      String msg,
                                      RecognitionException e) {
                    System.err.println("Syntaxfehler in Zeile " + line + ":" + charPositionInLine + " - " + msg);
                }
            });
            
            // Parse-Tree generieren
            MiniCParser.ProgramContext tree = parser.program();
            
            // Prüfe auf Parser-Fehler
            if (parser.getNumberOfSyntaxErrors() > 0) {
                System.err.println("\nParsing fehlgeschlagen mit " + parser.getNumberOfSyntaxErrors() + " Fehler(n).");
                System.exit(1);
            }
            
            // AST generieren
            ASTBuilder builder = new ASTBuilder();
            List<Stmt> ast = (List<Stmt>) builder.visit(tree);


            System.out.println("\n=== Symboltabelle & statische Checks ===\n");
            SymbolTableBuilder stb = new SymbolTableBuilder();
            stb.build(ast);

            System.out.println(stb.getRoot());


            // AST ausgeben
            System.out.println("\n=== Generierter AST ===\n");
            System.out.println("Program([");
            for (int i = 0; i < ast.size(); i++) {
                System.out.print("  " + ast.get(i));
                if (i < ast.size() - 1) {
                    System.out.println(",");
                } else {
                    System.out.println();
                }
            }
            System.out.println("])");

            
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Eingabe: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Fehler beim Verarbeiten: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}