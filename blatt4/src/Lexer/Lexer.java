package Lexer;

public class Lexer {
    private final String input;
    private int pos = 0;
    private int line = 1;
    private int col = 1;

    public Lexer(String input) {
        this.input = input;
    }

    private char peek() {
        return pos < input.length() ? input.charAt(pos) : '\0';
    }

    private char consume() {
        char c = peek();
        pos++;
        if (c == '\n') { line++; col = 1; } else { col++; }
        return c;
    }

    private void skipWhitespaceAndComments() {
        while (true) {
            char c = peek();
            if (c == ';') {
                // comment starts with ';;' per spec
                if (pos+1 < input.length() && input.charAt(pos+1) == ';') {
                    // consume until end of line
                    consume(); consume();
                    while (peek() != '\0' && peek() != '\n') consume();
                    continue;
                } else {
                    break; // single ';' -> treat as invalid later
                }
            }
            if (c == ' ' || c == '\t' || c == '\r' || c == '\n') {
                consume();
                continue;
            }
            break;
        }
    }

    public Token nextToken() {
        skipWhitespaceAndComments();
        int tokLine = line;
        int tokCol = col;
        char c = peek();

        if (c == '\0') return new Token(TokenType.EOF, "<EOF>", tokLine, tokCol);

        // Single-char tokens
        if (c == '(') { consume(); return new Token(TokenType.LPAREN, "(", tokLine, tokCol); }
        if (c == ')') { consume(); return new Token(TokenType.RPAREN, ")", tokLine, tokCol); }
        if (c == '+') { consume(); return new Token(TokenType.PLUS, "+", tokLine, tokCol); }
        if (c == '-') { consume(); return new Token(TokenType.MINUS, "-", tokLine, tokCol); }
        if (c == '*') { consume(); return new Token(TokenType.MUL, "*", tokLine, tokCol); }
        if (c == '/') { consume(); return new Token(TokenType.DIV, "/", tokLine, tokCol); }
        if (c == '>') { consume(); return new Token(TokenType.GT, ">", tokLine, tokCol); }
        if (c == '<') { consume(); return new Token(TokenType.LT, "<", tokLine, tokCol); }
        if (c == '=') { consume(); return new Token(TokenType.EQ, "=", tokLine, tokCol); }

        // String literal
        if (c == '"') {
            consume(); // consume opening "
            StringBuilder sb = new StringBuilder();
            while (peek() != '\0' && peek() != '"') {
                if (peek() == '\\') { // escape
                    consume();
                    char esc = consume();
                    // minimal escape handling
                    if (esc == 'n') sb.append('\n');
                    else if (esc == 't') sb.append('\t');
                    else sb.append(esc);
                } else {
                    sb.append(consume());
                }
            }
            if (peek() == '"') {
                consume(); // closing "
                return new Token(TokenType.STRING, sb.toString(), tokLine, tokCol);
            } else {
                // unterminated string -> Fehler
                System.err.printf("LexError: unterminated string at %d:%d%n", tokLine, tokCol);
                return new Token(TokenType.INVALID, sb.toString(), tokLine, tokCol);
            }
        }

        // Number (digit+)
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isDigit(peek())) sb.append(consume());

            // darf keine Buchstaben direkt folgen
            if (Character.isLetter(peek())) {
                System.err.printf("LexError: invalid character '%c' after number at %d:%d%n", peek(), line, col);
                return new Token(TokenType.INVALID, sb.toString() + peek(), tokLine, tokCol);
            }

            return new Token(TokenType.NUMBER, sb.toString(), tokLine, tokCol);
        }

        // Identifier or keyword (LETTER start)
        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (true) {
                char p = peek();
                if (Character.isLetterOrDigit(p) || p == '-' || p == '_') {
                    sb.append(consume());
                } else break;
            }
            String lex = sb.toString();
            // check keywords (case-sensitive)
            switch (lex) {
                case "if": return new Token(TokenType.IF, lex, tokLine, tokCol);
                case "def": return new Token(TokenType.DEF, lex, tokLine, tokCol);
                case "defn": return new Token(TokenType.DEFN, lex, tokLine, tokCol);
                case "let": return new Token(TokenType.LET, lex, tokLine, tokCol);
                case "do": return new Token(TokenType.DO, lex, tokLine, tokCol);
                case "print": return new Token(TokenType.PRINT, lex, tokLine, tokCol);
                case "str": return new Token(TokenType.STR, lex, tokLine, tokCol);
                case "list": return new Token(TokenType.LIST, lex, tokLine, tokCol);
                case "nth": return new Token(TokenType.NTH, lex, tokLine, tokCol);
                case "head": return new Token(TokenType.HEAD, lex, tokLine, tokCol);
                case "tail": return new Token(TokenType.TAIL, lex, tokLine, tokCol);
                case "true": return new Token(TokenType.TRUE, lex, tokLine, tokCol);
                case "false": return new Token(TokenType.FALSE, lex, tokLine, tokCol);
                default:
                    return new Token(TokenType.ID, lex, tokLine, tokCol);
            }
        }

        // sonst: invalid char
        System.err.printf("LexError: invalid character '%c' at %d:%d%n", c, tokLine, tokCol);
        consume();
        return new Token(TokenType.INVALID, String.valueOf(c), tokLine, tokCol);
    }
}
