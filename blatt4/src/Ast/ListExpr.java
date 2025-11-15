package Ast;

import java.util.List;
import java.util.stream.Collectors;

// S-Expression / List expression: head + list of argument Exprs
public class ListExpr extends Expr {
    public final String head;
    public final List<Expr> args;

    public ListExpr(String head, List<Expr> args) {
        this.head = head;
        this.args = args;
    }

    @Override
    public String toString() {
        String argsStr = args.stream().map(Object::toString).collect(Collectors.joining(", "));
        return "ListExpr (" + head + " [" + argsStr + "])";
    }
}
