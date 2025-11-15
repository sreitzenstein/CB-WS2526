package Lexer;

public enum TokenType {
    // Struktur
    LPAREN, RPAREN, EOF,

    // Literale / Lexer-Terminals
    NUMBER, STRING, ID, TRUE, FALSE,

    // Operatoren/Heads (wir können auch die Symbole als Token haben)
    PLUS, MINUS, MUL, DIV, GT, LT, EQ,

    // Keywords (als eigene Token erleichtert das Parsing)
    IF, DEF, DEFN, LET, DO, PRINT, STR, LIST, NTH, HEAD, TAIL,

    // Fehler
    INVALID
}
