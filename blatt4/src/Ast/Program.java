package Ast;

import java.util.List;

public class Program {
    public final List<Expr> expressions;

    public Program(List<Expr> expressions) {
        this.expressions = expressions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        for (Expr e : expressions) {
            sb.append("  ").append(e.toString()).append("\n");
        }
        return sb.toString();
    }
}
