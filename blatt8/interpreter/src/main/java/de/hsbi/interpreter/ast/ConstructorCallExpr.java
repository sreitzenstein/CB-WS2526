package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a constructor call (e.g., Dog("Bello"))
 */
public class ConstructorCallExpr extends Expression {
    private String className;
    private List<Expression> arguments;

    public ConstructorCallExpr(String className, List<Expression> arguments) {
        this.className = className;
        this.arguments = arguments;
    }

    public String getClassName() {
        return className;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitConstructorCallExpr(this);
    }
}
