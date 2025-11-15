package Ast;

public class Identifier extends Expr {
    public final String name;
    public Identifier(String name) { this.name = name; }
    @Override public String toString() { return "Id (" + name + ")"; }
}
