package de.hsbi.interpreter.semantic;

import de.hsbi.interpreter.ast.*;
import de.hsbi.interpreter.symbols.*;

import java.util.ArrayList;
import java.util.List;

/**
 * performs semantic analysis on the AST
 * - type checking
 * - lvalue checking
 * - function overload resolution
 * - virtual method validation
 */
public class SemanticAnalyzer implements ASTVisitor<Type> {
    private SymbolTable symbolTable;
    private ClassSymbol currentClass;
    private Type currentFunctionReturnType;
    private List<String> errors;

    public SemanticAnalyzer(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        this.currentClass = null;
        this.currentFunctionReturnType = null;
        this.errors = new ArrayList<>();
    }

    public List<String> getErrors() {
        return errors;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    private void error(String message) {
        errors.add(message);
    }

    /**
     * analyze the program
     * @param program the AST root
     * @return true if no errors were found
     */
    public boolean analyze(Program program) {
        visitProgram(program);
        return !hasErrors();
    }

    @Override
    public Type visitProgram(Program node) {
        // analyze all functions
        for (FunctionDecl func : node.getFunctions()) {
            func.accept(this);
        }

        // analyze all classes
        for (ClassDecl cls : node.getClasses()) {
            cls.accept(this);
        }

        return null;
    }

    @Override
    public Type visitClassDecl(ClassDecl node) {
        currentClass = symbolTable.getClass(node.getName());

        // check for inheritance cycles
        if (hasInheritanceCycle(currentClass)) {
            error("class '" + node.getName() + "' has cyclic inheritance");
        }

        // analyze fields
        for (VarDecl field : node.getFields()) {
            // check that field is not a reference
            if (field.isReference()) {
                error("class '" + node.getName() + "': field '" + field.getName() + "' cannot be a reference");
            }
            field.accept(this);
        }

        // analyze methods
        for (MethodDecl method : node.getMethods()) {
            method.accept(this);
        }

        // analyze constructors
        for (ConstructorDecl constructor : node.getConstructors()) {
            constructor.accept(this);
        }

        // check virtual method overrides
        checkVirtualMethodOverrides(currentClass);

        currentClass = null;
        return null;
    }

    @Override
    public Type visitFunctionDecl(FunctionDecl node) {
        currentFunctionReturnType = node.getReturnType();

        symbolTable.enterScope("function:" + node.getName());

        // define parameters in function scope
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
            param.accept(this);
        }

        // analyze body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunctionReturnType = null;
        return null;
    }

    @Override
    public Type visitMethodDecl(MethodDecl node) {
        currentFunctionReturnType = node.getReturnType();

        symbolTable.enterScope("method:" + node.getName());

        // define parameters in method scope
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
            param.accept(this);
        }

        // analyze body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunctionReturnType = null;
        return null;
    }

    @Override
    public Type visitConstructorDecl(ConstructorDecl node) {
        currentFunctionReturnType = new Type(Type.BaseType.VOID);

        symbolTable.enterScope("constructor:" + node.getName());

        // define parameters in constructor scope
        for (Parameter param : node.getParameters()) {
            VarSymbol paramSymbol = new VarSymbol(param.getName(), param.getType(), param.isReference());
            symbolTable.define(paramSymbol);
            param.accept(this);
        }

        // analyze body
        if (node.getBody() != null) {
            node.getBody().accept(this);
        }

        symbolTable.exitScope();
        currentFunctionReturnType = null;
        return null;
    }

    @Override
    public Type visitVarDecl(VarDecl node) {
        // check that type exists
        if (!isValidType(node.getType())) {
            error("variable '" + node.getName() + "': unknown type '" + node.getType() + "'");
        }

        // check initializer if present
        if (node.getInitializer() != null) {
            Type initType = node.getInitializer().accept(this);

            // check that initializer type matches variable type
            if (!typesMatch(node.getType(), initType)) {
                error("variable '" + node.getName() + "': initializer type '" + initType + "' does not match variable type '" + node.getType() + "'");
            }

            // if variable is a reference, initializer must be an lvalue
            if (node.isReference() && !isLValue(node.getInitializer())) {
                error("variable '" + node.getName() + "': reference must be initialized with an lvalue");
            }
        } else {
            // references must be initialized
            if (node.isReference()) {
                error("variable '" + node.getName() + "': reference must be initialized");
            }
        }

        // define variable in current scope
        VarSymbol varSymbol = new VarSymbol(node.getName(), node.getType(), node.isReference());
        symbolTable.define(varSymbol);

        return null;
    }

    @Override
    public Type visitParameter(Parameter node) {
        // check that type exists
        if (!isValidType(node.getType())) {
            error("parameter '" + node.getName() + "': unknown type '" + node.getType() + "'");
        }
        return null;
    }

    @Override
    public Type visitBlockStmt(BlockStmt node) {
        symbolTable.enterScope("block");

        for (Statement stmt : node.getStatements()) {
            stmt.accept(this);
        }

        symbolTable.exitScope();
        return null;
    }

    @Override
    public Type visitIfStmt(IfStmt node) {
        Type condType = node.getCondition().accept(this);

        // condition can be bool, int, char, or string (implicit conversion to bool)
        if (condType != null && !canConvertToBool(condType)) {
            error("if condition must be bool, int, char, or string");
        }

        node.getThenStmt().accept(this);

        if (node.getElseStmt() != null) {
            node.getElseStmt().accept(this);
        }

        return null;
    }

    @Override
    public Type visitWhileStmt(WhileStmt node) {
        Type condType = node.getCondition().accept(this);

        // condition can be bool, int, char, or string (implicit conversion to bool)
        if (condType != null && !canConvertToBool(condType)) {
            error("while condition must be bool, int, char, or string");
        }

        node.getBody().accept(this);
        return null;
    }

    @Override
    public Type visitReturnStmt(ReturnStmt node) {
        if (currentFunctionReturnType == null) {
            error("return statement outside of function");
            return null;
        }

        if (node.getValue() == null) {
            // return without value - function must be void
            if (!currentFunctionReturnType.isVoid()) {
                error("non-void function must return a value");
            }
        } else {
            // return with value
            Type returnType = node.getValue().accept(this);

            if (currentFunctionReturnType.isVoid()) {
                error("void function cannot return a value");
            } else if (!typesMatch(currentFunctionReturnType, returnType)) {
                error("return type '" + returnType + "' does not match function return type '" + currentFunctionReturnType + "'");
            }
        }

        return null;
    }

    @Override
    public Type visitExprStmt(ExprStmt node) {
        node.getExpression().accept(this);
        return null;
    }

    @Override
    public Type visitBinaryExpr(BinaryExpr node) {
        Type leftType = node.getLeft().accept(this);
        Type rightType = node.getRight().accept(this);

        if (leftType == null || rightType == null) {
            return null; // error already reported
        }

        BinaryExpr.Operator op = node.getOperator();

        // check operator compatibility
        switch (op) {
            case PLUS:
            case MINUS:
            case MULT:
            case DIV:
            case MOD:
                // arithmetic operators: both operands must be int
                if (!leftType.equals(new Type(Type.BaseType.INT)) || !rightType.equals(new Type(Type.BaseType.INT))) {
                    error("arithmetic operator requires int operands");
                    return null;
                }
                node.setType(new Type(Type.BaseType.INT));
                return new Type(Type.BaseType.INT);

            case LT:
            case LEQ:
            case GT:
            case GEQ:
                // relational operators: int or char
                if (leftType.equals(new Type(Type.BaseType.INT)) && rightType.equals(new Type(Type.BaseType.INT))) {
                    node.setType(new Type(Type.BaseType.BOOL));
                    return new Type(Type.BaseType.BOOL);
                } else if (leftType.equals(new Type(Type.BaseType.CHAR)) && rightType.equals(new Type(Type.BaseType.CHAR))) {
                    node.setType(new Type(Type.BaseType.BOOL));
                    return new Type(Type.BaseType.BOOL);
                } else {
                    error("relational operator requires int or char operands");
                    return null;
                }

            case EQ:
            case NEQ:
                // equality operators: same type
                if (!typesMatch(leftType, rightType)) {
                    error("equality operator requires operands of same type");
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            case AND:
            case OR:
                // logical operators: both operands must be bool
                if (!leftType.equals(new Type(Type.BaseType.BOOL)) || !rightType.equals(new Type(Type.BaseType.BOOL))) {
                    error("logical operator requires bool operands");
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            default:
                error("unknown binary operator: " + op);
                return null;
        }
    }

    @Override
    public Type visitUnaryExpr(UnaryExpr node) {
        Type operandType = node.getOperand().accept(this);

        if (operandType == null) {
            return null; // error already reported
        }

        UnaryExpr.Operator op = node.getOperator();

        switch (op) {
            case PLUS:
            case MINUS:
                // unary plus/minus: operand must be int
                if (!operandType.equals(new Type(Type.BaseType.INT))) {
                    error("unary +/- requires int operand");
                    return null;
                }
                node.setType(new Type(Type.BaseType.INT));
                return new Type(Type.BaseType.INT);

            case NOT:
                // logical not: operand must be bool
                if (!operandType.equals(new Type(Type.BaseType.BOOL))) {
                    error("logical ! requires bool operand");
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            default:
                error("unknown unary operator: " + op);
                return null;
        }
    }

    @Override
    public Type visitAssignExpr(AssignExpr node) {
        // target must be an lvalue
        if (!isLValue(node.getTarget())) {
            error("left side of assignment must be an lvalue");
        }

        Type targetType = node.getTarget().accept(this);
        Type valueType = node.getValue().accept(this);

        if (targetType == null || valueType == null) {
            return null; // error already reported
        }

        // check for slicing: Base b; b = derived; (allowed)
        if (targetType.getBaseType() == Type.BaseType.CLASS && valueType.getBaseType() == Type.BaseType.CLASS) {
            ClassSymbol targetClass = symbolTable.getClass(targetType.getClassName());
            ClassSymbol valueClass = symbolTable.getClass(valueType.getClassName());

            if (targetClass != null && valueClass != null && isDerivedFrom(valueClass, targetClass)) {
                // slicing is allowed
                node.setType(valueType);
                return valueType;
            }
        }

        // types must match
        if (!typesMatch(targetType, valueType)) {
            error("assignment: type mismatch ('" + targetType + "' vs '" + valueType + "')");
            return null;
        }

        node.setType(valueType);
        return valueType;
    }

    @Override
    public Type visitVarExpr(VarExpr node) {
        Symbol symbol = symbolTable.resolve(node.getName());

        if (symbol == null) {
            error("variable '" + node.getName() + "' not found");
            return null;
        }

        if (symbol.getKind() != Symbol.SymbolKind.VARIABLE) {
            error("'" + node.getName() + "' is not a variable");
            return null;
        }

        node.setType(symbol.getType());
        return symbol.getType();
    }

    @Override
    public Type visitCallExpr(CallExpr node) {
        // resolve function
        Symbol symbol = symbolTable.resolve(node.getFunctionName());

        if (symbol == null) {
            error("function '" + node.getFunctionName() + "' not found");
            return null;
        }

        if (symbol.getKind() != Symbol.SymbolKind.FUNCTION) {
            error("'" + node.getFunctionName() + "' is not a function");
            return null;
        }

        FunctionSymbol function = (FunctionSymbol) symbol;

        // check argument count
        if (node.getArguments().size() != function.getParameters().size()) {
            error("function '" + node.getFunctionName() + "': expected " + function.getParameters().size() + " arguments, got " + node.getArguments().size());
            return null;
        }

        // check argument types
        for (int i = 0; i < node.getArguments().size(); i++) {
            Expression arg = node.getArguments().get(i);
            Parameter param = function.getParameters().get(i);

            Type argType = arg.accept(this);
            if (argType == null) {
                continue; // error already reported
            }

            if (!typesMatch(param.getType(), argType)) {
                error("function '" + node.getFunctionName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')");
            }

            // if parameter is a reference, argument must be an lvalue
            if (param.isReference() && !isLValue(arg)) {
                error("function '" + node.getFunctionName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)");
            }
        }

        node.setType(function.getType());
        return function.getType();
    }

    @Override
    public Type visitMemberAccessExpr(MemberAccessExpr node) {
        Type objectType = node.getObject().accept(this);

        if (objectType == null) {
            return null; // error already reported
        }

        if (objectType.getBaseType() != Type.BaseType.CLASS) {
            error("member access: object must be of class type");
            return null;
        }

        ClassSymbol classSymbol = symbolTable.getClass(objectType.getClassName());
        if (classSymbol == null) {
            error("member access: class '" + objectType.getClassName() + "' not found");
            return null;
        }

        if (node.isMethodCall()) {
            // method call
            MethodSymbol method = classSymbol.lookupMethod(node.getMemberName());
            if (method == null) {
                error("class '" + objectType.getClassName() + "' has no method '" + node.getMemberName() + "'");
                return null;
            }

            // check argument count
            if (node.getArguments().size() != method.getParameters().size()) {
                error("method '" + node.getMemberName() + "': expected " + method.getParameters().size() + " arguments, got " + node.getArguments().size());
                return null;
            }

            // check argument types
            for (int i = 0; i < node.getArguments().size(); i++) {
                Expression arg = node.getArguments().get(i);
                Parameter param = method.getParameters().get(i);

                Type argType = arg.accept(this);
                if (argType == null) {
                    continue; // error already reported
                }

                if (!typesMatch(param.getType(), argType)) {
                    error("method '" + node.getMemberName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')");
                }

                // if parameter is a reference, argument must be an lvalue
                if (param.isReference() && !isLValue(arg)) {
                    error("method '" + node.getMemberName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)");
                }
            }

            node.setType(method.getType());
            return method.getType();
        } else {
            // field access
            VarSymbol field = classSymbol.lookupField(node.getMemberName());
            if (field == null) {
                error("class '" + objectType.getClassName() + "' has no field '" + node.getMemberName() + "'");
                return null;
            }

            node.setType(field.getType());
            return field.getType();
        }
    }

    @Override
    public Type visitConstructorCallExpr(ConstructorCallExpr node) {
        // first check if this is a built-in function
        if (isBuiltinFunction(node.getClassName())) {
            return checkBuiltinFunction(node);
        }

        // then check if this is a function call (not a constructor)
        Symbol symbol = symbolTable.resolve(node.getClassName());
        if (symbol != null && symbol.getKind() == Symbol.SymbolKind.FUNCTION) {
            // this is actually a function call, not a constructor
            FunctionSymbol function = (FunctionSymbol) symbol;

            // check argument count
            if (node.getArguments().size() != function.getParameters().size()) {
                error("function '" + node.getClassName() + "': expected " + function.getParameters().size() + " arguments, got " + node.getArguments().size());
                return null;
            }

            // check argument types
            for (int i = 0; i < node.getArguments().size(); i++) {
                Expression arg = node.getArguments().get(i);
                Parameter param = function.getParameters().get(i);

                Type argType = arg.accept(this);
                if (argType == null) {
                    continue; // error already reported
                }

                if (!typesMatch(param.getType(), argType)) {
                    error("function '" + node.getClassName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')");
                }

                // if parameter is a reference, argument must be an lvalue
                if (param.isReference() && !isLValue(arg)) {
                    error("function '" + node.getClassName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)");
                }
            }

            node.setType(function.getType());
            return function.getType();
        }

        // otherwise, it's a constructor call
        ClassSymbol classSymbol = symbolTable.getClass(node.getClassName());

        if (classSymbol == null) {
            error("class '" + node.getClassName() + "' not found");
            return null;
        }

        // find matching constructor
        ConstructorSymbol constructor = findConstructor(classSymbol, node.getArguments());

        if (constructor == null) {
            error("class '" + node.getClassName() + "': no matching constructor found");
            return null;
        }

        // check argument types
        for (int i = 0; i < node.getArguments().size(); i++) {
            Expression arg = node.getArguments().get(i);
            Parameter param = constructor.getParameters().get(i);

            Type argType = arg.accept(this);
            if (argType == null) {
                continue; // error already reported
            }

            if (!typesMatch(param.getType(), argType)) {
                error("constructor '" + node.getClassName() + "': argument " + (i + 1) + " type mismatch (expected '" + param.getType() + "', got '" + argType + "')");
            }

            // if parameter is a reference, argument must be an lvalue
            if (param.isReference() && !isLValue(arg)) {
                error("constructor '" + node.getClassName() + "': argument " + (i + 1) + " must be an lvalue (parameter is a reference)");
            }
        }

        Type type = new Type(node.getClassName());
        node.setType(type);
        return type;
    }

    @Override
    public Type visitLiteralExpr(LiteralExpr node) {
        Type type;
        switch (node.getLiteralType()) {
            case INT:
                type = new Type(Type.BaseType.INT);
                break;
            case BOOL:
                type = new Type(Type.BaseType.BOOL);
                break;
            case CHAR:
                type = new Type(Type.BaseType.CHAR);
                break;
            case STRING:
                type = new Type(Type.BaseType.STRING);
                break;
            default:
                error("unknown literal type");
                return null;
        }
        node.setType(type);
        return type;
    }

    @Override
    public Type visitType(Type node) {
        // types don't have types
        return null;
    }

    // helper methods

    private boolean isValidType(Type type) {
        if (type.isPrimitive()) {
            return true;
        }
        return symbolTable.hasClass(type.getClassName());
    }

    private boolean typesMatch(Type t1, Type t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        return t1.equals(t2);
    }

    private boolean canConvertToBool(Type type) {
        // bool, int, char, string can be converted to bool
        Type.BaseType baseType = type.getBaseType();
        return baseType == Type.BaseType.BOOL ||
               baseType == Type.BaseType.INT ||
               baseType == Type.BaseType.CHAR ||
               baseType == Type.BaseType.STRING;
    }

    private boolean isLValue(Expression expr) {
        if (expr instanceof VarExpr) {
            return true;
        }
        if (expr instanceof MemberAccessExpr) {
            MemberAccessExpr memberAccess = (MemberAccessExpr) expr;
            return !memberAccess.isMethodCall(); // field access is lvalue, method call is not
        }
        return false;
    }

    private boolean hasInheritanceCycle(ClassSymbol cls) {
        ClassSymbol current = cls.getBaseClass();
        while (current != null) {
            if (current == cls) {
                return true;
            }
            current = current.getBaseClass();
        }
        return false;
    }

    private boolean isDerivedFrom(ClassSymbol derived, ClassSymbol base) {
        ClassSymbol current = derived.getBaseClass();
        while (current != null) {
            if (current == base) {
                return true;
            }
            current = current.getBaseClass();
        }
        return false;
    }

    private ConstructorSymbol findConstructor(ClassSymbol classSymbol, List<Expression> arguments) {
        // find constructor with matching signature
        for (ConstructorSymbol constructor : classSymbol.getConstructors().values()) {
            if (constructor.getParameters().size() == arguments.size()) {
                boolean match = true;
                for (int i = 0; i < arguments.size(); i++) {
                    Type argType = arguments.get(i).getType();
                    Type paramType = constructor.getParameters().get(i).getType();
                    if (argType == null || !typesMatch(paramType, argType)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return constructor;
                }
            }
        }

        // if no constructor found and no arguments, use default constructor
        if (arguments.isEmpty()) {
            // check if default constructor exists
            for (ConstructorSymbol constructor : classSymbol.getConstructors().values()) {
                if (constructor.getParameters().isEmpty()) {
                    return constructor;
                }
            }
        }

        return null;
    }

    private void checkVirtualMethodOverrides(ClassSymbol classSymbol) {
        if (classSymbol.getBaseClass() == null) {
            return; // no base class, nothing to check
        }

        // check each method in this class
        for (MethodSymbol method : classSymbol.getMethods().values()) {
            MethodSymbol baseMethod = classSymbol.getBaseClass().lookupMethod(method.getName());

            if (baseMethod != null) {
                // method with same name exists in base class
                // check if signature matches
                if (parametersMatch(method.getParameters(), baseMethod.getParameters())) {
                    // signatures match - this is an override
                    // check that base method is virtual
                    if (!baseMethod.isVirtual()) {
                        error("method '" + method.getName() + "' in class '" + classSymbol.getName() + "' overrides non-virtual method in base class");
                    }

                    // check that return types match
                    if (!typesMatch(method.getType(), baseMethod.getType())) {
                        error("method '" + method.getName() + "' in class '" + classSymbol.getName() + "' has different return type than base method");
                    }
                }
            }
        }
    }

    private boolean parametersMatch(List<Parameter> params1, List<Parameter> params2) {
        if (params1.size() != params2.size()) {
            return false;
        }

        for (int i = 0; i < params1.size(); i++) {
            Parameter p1 = params1.get(i);
            Parameter p2 = params2.get(i);

            if (!typesMatch(p1.getType(), p2.getType()) || p1.isReference() != p2.isReference()) {
                return false;
            }
        }

        return true;
    }

    private boolean isBuiltinFunction(String name) {
        return name.equals("print_bool") ||
               name.equals("print_int") ||
               name.equals("print_char") ||
               name.equals("print_string");
    }

    private Type checkBuiltinFunction(ConstructorCallExpr node) {
        String name = node.getClassName();

        // all built-in functions take exactly 1 argument
        if (node.getArguments().size() != 1) {
            error("built-in function '" + name + "' expects 1 argument, got " + node.getArguments().size());
            return null;
        }

        Expression arg = node.getArguments().get(0);
        Type argType = arg.accept(this);

        if (argType == null) {
            return null; // error already reported
        }

        // check argument type
        Type expectedType = null;
        if (name.equals("print_bool")) {
            expectedType = new Type(Type.BaseType.BOOL);
        } else if (name.equals("print_int")) {
            expectedType = new Type(Type.BaseType.INT);
        } else if (name.equals("print_char")) {
            expectedType = new Type(Type.BaseType.CHAR);
        } else if (name.equals("print_string")) {
            expectedType = new Type(Type.BaseType.STRING);
        }

        if (!typesMatch(expectedType, argType)) {
            error("built-in function '" + name + "': argument type mismatch (expected '" + expectedType + "', got '" + argType + "')");
            return null;
        }

        // built-in functions return void
        Type returnType = new Type(Type.BaseType.VOID);
        node.setType(returnType);
        return returnType;
    }
}
