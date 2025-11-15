package Parser;

import java.util.ArrayList;
import java.util.List;

import Lexer.*;
import Ast.*;

public class Parser {
    private Lexer lexer;
    private Token lookahead;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.lookahead = lexer.nextToken();
    }

    private void errorExpected(TokenType expected) {
        System.err.printf(
                "ParseError: erwartetes Token %s, aber gefunden %s (%s) bei %d:%d%n",
                expected, lookahead.type, lookahead.text, lookahead.line, lookahead.column
        );
        System.exit(1);
    }

    private void errorUnexpected() {
        System.err.printf(
                "ParseError: unerwartetes Token %s (%s) bei %d:%d%n",
                lookahead.type, lookahead.text, lookahead.line, lookahead.column
        );
        System.exit(1);
    }

    private void match(TokenType expected) {
        if (lookahead.type == expected) {
            lookahead = lexer.nextToken();
        } else {
            errorExpected(expected);
        }
    }

    // ---------- Start ----------
    public Program parseStart() {
        List<Expr> exprs = parseExpressionList();
        match(TokenType.EOF);
        return new Program(exprs);
    }

    // ExpressionList -> zero or more Expression
    private List<Expr> parseExpressionList() {
        List<Expr> list = new ArrayList<>();
        while (lookahead.type != TokenType.EOF) {
            list.add(parseExpression());
        }
        return list;
    }

    // Expression -> Literal | ListExpr
    private Expr parseExpression() {
        switch (lookahead.type) {
            case NUMBER:
            case STRING:
            case TRUE:
            case FALSE:
            case ID:
                return parseLiteral();
            case LPAREN:
                return parseListExpr();
            default:
                errorUnexpected();
                return null; // unreachable
        }
    }

    // Literal -> NUMBER | STRING | TRUE | FALSE | ID
    private Expr parseLiteral() {
        Token t = lookahead;
        switch (t.type) {
            case NUMBER:
                match(TokenType.NUMBER);
                return new NumberLiteral(t.text);
            case STRING:
                match(TokenType.STRING);
                return new StringLiteral(t.text);
            case TRUE:
                match(TokenType.TRUE);
                return new BooleanLiteral(true);
            case FALSE:
                match(TokenType.FALSE);
                return new BooleanLiteral(false);
            case ID:
                match(TokenType.ID);
                return new Identifier(t.text);
            default:
                errorUnexpected();
                return null;
        }
    }

    // ListExpr -> '(' Head ExprSeq ')'
    private Expr parseListExpr() {
        match(TokenType.LPAREN);
        String headText = parseHeadText(); // get head as string
        List<Expr> args = parseExprSeq();  // possibly empty
        match(TokenType.RPAREN);
        return new ListExpr(headText, args);
    }

    // Head
    private String parseHeadText() {
        Token t = lookahead;
        switch (t.type) {
            case ID:
                match(TokenType.ID);
                return t.text;
            case IF: case DEF: case DEFN: case LET: case DO:
            case PRINT: case STR: case LIST: case NTH: case HEAD: case TAIL:
                match(t.type);
                return t.text;
            case PLUS: case MINUS: case MUL: case DIV: case GT: case LT: case EQ:
                match(t.type);
                return t.text;
            default:
                errorUnexpected();
                return null;
        }
    }

    // ExprSeq -> (Expression)*
    private List<Expr> parseExprSeq() {
        List<Expr> args = new ArrayList<>();
        while (lookahead.type != TokenType.RPAREN && lookahead.type != TokenType.EOF) {
            args.add(parseExpression());
        }
        return args;
    }
}
