package de.hsbi.interpreter.ast;

import java.util.List;

/**
 * represents a function call (e.g., add(5, 3))
 */
public class CallExpr extends Expression {
    private String functionName;
    private List<Expression> arguments;

    public CallExpr(String functionName, List<Expression> arguments) {
        this.functionName = functionName;
        this.arguments = arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitCallExpr(this);
    }
}
