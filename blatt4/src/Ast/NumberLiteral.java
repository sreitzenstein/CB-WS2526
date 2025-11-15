package Ast;

// Literale
public class NumberLiteral extends Expr {
    public final String value; // keep as String to preserve formatting (int strings)
    public NumberLiteral(String value) { this.value = value; }
    @Override public String toString() { return "Number (" + value + ")"; }
}
