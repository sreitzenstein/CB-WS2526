package Ast;

import java.util.List;

public class ASTPrinter {

    public static String printProgram(Program program) {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        for (Expr e : program.expressions) {
            sb.append(printExpr(e, 1));
        }
        return sb.toString();
    }

    // indentLevel: 1 = two spaces, 2 = four spaces, ...
    private static String indent(int indentLevel) {
        return "  ".repeat(Math.max(0, indentLevel));
    }

    private static String printExpr(Expr expr, int indentLevel) {
        StringBuilder sb = new StringBuilder();
        if (expr instanceof ListExpr) {
            sb.append(indent(indentLevel)).append("(").append(((ListExpr) expr).head);
            List<Expr> args = ((ListExpr) expr).args;
            if (args.isEmpty()) {
                sb.append(")\n");
                return sb.toString();
            }
            sb.append("\n");
            for (Expr a : args) {
                sb.append(printExpr(a, indentLevel + 1));
            }
            sb.append(indent(indentLevel)).append(")\n");
        } else if (expr instanceof NumberLiteral) {
            sb.append(indent(indentLevel)).append(((NumberLiteral) expr).toString()).append("\n");
        } else if (expr instanceof StringLiteral) {
            sb.append(indent(indentLevel)).append(((StringLiteral) expr).toString()).append("\n");
        } else if (expr instanceof BooleanLiteral) {
            sb.append(indent(indentLevel)).append(((BooleanLiteral) expr).toString()).append("\n");
        } else if (expr instanceof Identifier) {
            sb.append(indent(indentLevel)).append(((Identifier) expr).toString()).append("\n");
        } else {
            // Fallback: generische toString()
            sb.append(indent(indentLevel)).append(expr.toString()).append("\n");
        }
        return sb.toString();
    }
}
