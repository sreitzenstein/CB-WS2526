package Ast;

public class StringLiteral extends Expr {
    public final String value;
    public StringLiteral(String value) { this.value = value; }
    @Override public String toString() { return "String (\"" + value + "\")"; }
}
