package Ast;

public class BooleanLiteral extends Expr {
    public final boolean value;
    public BooleanLiteral(boolean value) { this.value = value; }
    @Override public String toString() { return "Bool (" + value + ")"; }
}
