package de.hsbi.interpreter.ast;

/**
 * represents a variable reference (e.g., x)
 */
public class VarExpr extends Expression {
    private String name;

    public VarExpr(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVarExpr(this);
    }
}
