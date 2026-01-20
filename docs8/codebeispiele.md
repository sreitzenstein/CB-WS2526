# Scopes

```java
// Scope.java

//Scope Aufbau (12-23)
public class Scope {
    private String name;
    private Scope parent;
    private Map<String, Symbol> symbols;
    private Map<String, List<FunctionSymbol>> overloadedFunctions;

    public Scope(String name, Scope parent) {
        this.name = name;
        this.parent = parent;
        this.symbols = new HashMap<>();
        this.overloadedFunctions = new HashMap<>();
    }


//rekursives Auflösen der Symbole in Scopes (100-109)
    public Symbol resolve(String name) {
        Symbol symbol = symbols.get(name);
        if (symbol != null) {
            return symbol;
        }
        if (parent != null) {
            return parent.resolve(name);
        }
        return null;
    }


// SymbolTable.java (33-46)
    public void enterScope(String name) {
        Scope newScope = new Scope(name, currentScope);
        currentScope = newScope;
    }

    public void exitScope() {
        if (currentScope.getParent() == null) {
            throw new RuntimeException("cannot exit global scope");
        }
        currentScope = currentScope.getParent();
    }



//--------------------------------------------------------------------------------------------------

//2pass


    //SymbilTableBuilder.java

    //(59-80)
    /**
     * build the symbol table from a program (performs two passes)
     * @param program the AST root node
     * @return the populated symbol table
     */
    public SymbolTable build(Program program) {
        // first pass: register all classes and functions
        firstPass = true; //dadurch wird nur registriert
        visitProgram(program); //besucht alle Klassen und Funktionen

        // Löse basisklassen auf
        for (ClassSymbol classSymbol : symbolTable.getClasses().values()) {
            if (classSymbol.getBaseClassName() != null) {
                ClassSymbol baseClass = symbolTable.getClass(classSymbol.getBaseClassName());
                if (baseClass == null) {
                    throw new RuntimeException("base class '" + classSymbol.getBaseClassName() + "' not found for class '" + classSymbol.getName() + "'");
                }
                classSymbol.setBaseClass(baseClass);
            }
        }

        // second pass: process class members and function bodies
        firstPass = false; //dadurch wird dann geprocessed
        visitProgram(program);

        return symbolTable;
    }




    // (98-121)
    public Void visitClassDecl(ClassDecl node) {
        if (firstPass) {
            // first pass: register the class
            ClassSymbol classSymbol = new ClassSymbol(node.getName(), node.getBaseClass(), node);
            symbolTable.registerClass(classSymbol);
        } else {
            // second pass: process class members
            currentClass = symbolTable.getClass(node.getName());

            // process fields
            for (VarDecl field : node.getFields()) {
                VarSymbol fieldSymbol = new VarSymbol(field.getName(), field.getType(), field.isReference());
                currentClass.addField(fieldSymbol);
            }

            // process methods
            for (MethodDecl method : node.getMethods()) {
                MethodSymbol methodSymbol = new MethodSymbol(
                    method.getName(),
                    method.getReturnType(),
                    method.getParameters(),
                    method.isVirtual(),
                    method,
                    currentClass
                    //.....

```

# Semantische Checks

```java
// SemanticAnalyzer.java

//(724-740)
private boolean typesMatch(Type t1, Type t2) {
        if (t1 == null || t2 == null) {
            return false;
        }
        if (t1.equals(t2)) {
            return true;
        }
        // check for inheritance: t2 can be assigned to t1 if t2 is derived from t1
        if (t1.getBaseType() == Type.BaseType.CLASS && t2.getBaseType() == Type.BaseType.CLASS) {
            ClassSymbol class1 = symbolTable.getClass(t1.getClassName());
            ClassSymbol class2 = symbolTable.getClass(t2.getClassName());
            if (class1 != null && class2 != null) {
                return isDerivedFrom(class2, class1);
            }
        }
        return false;
    }




//--------------------------------------------------------------------------------------------------




//(312-377) Binäre operatoren

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
                    error("arithmetic operator requires int operands", node);
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
                    error("relational operator requires int or char operands", node);
                    return null;
                }

            case EQ:
            case NEQ:
                // equality operators: same type
                if (!typesMatch(leftType, rightType)) {
                    error("equality operator requires operands of same type", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            case AND:
            case OR:
                // logical operators: both operands must be bool
                if (!leftType.equals(new Type(Type.BaseType.BOOL)) || !rightType.equals(new Type(Type.BaseType.BOOL))) {
                    error("logical operator requires bool operands", node);
                    return null;
                }
                node.setType(new Type(Type.BaseType.BOOL));
                return new Type(Type.BaseType.BOOL);

            default:
                error("unknown binary operator: " + op, node);
                return null;
        }
    }




//--------------------------------------------------------------------------------------------------





    // lvalue (751-760)

      private boolean isLValue(Expression expr) {
        if (expr instanceof VarExpr) { //also eine variable
            return true;
        }
        if (expr instanceof MemberAccessExpr) {
            MemberAccessExpr memberAccess = (MemberAccessExpr) expr; //wenn feldzugruff abr kein methodenaufruf ist es auch eine
            return !memberAccess.isMethodCall(); // field access is lvalue, method call is not
        }
        return false;
    }

    /*
    Beispiel: Funktion: void f(int& x), dann ist der Aufruf f(5) falsch!
    */

    //(206-209)
            // if variable is a reference, initializer must be an lvalue
            if (node.isReference() && !isLValue(node.getInitializer())) {
                error("variable '" + node.getName() + "': reference must be initialized with an lvalue", node);
            }




//--------------------------------------------------------------------------------------------------





    // Überladungsauflösung
    // Scope.java (38-57)
     public void define(Symbol symbol) {
        // Special handling for function overloading
        if (symbol instanceof FunctionSymbol) {
            FunctionSymbol funcSymbol = (FunctionSymbol) symbol;
            List<FunctionSymbol> overloads = overloadedFunctions.computeIfAbsent(
                symbol.getName(), k -> new ArrayList<>());

            // Check for duplicate signature
            for (FunctionSymbol existing : overloads) {
                if (hasSameSignature(existing, funcSymbol)) {
                    throw new RuntimeException("function '" + symbol.getName() +
                        "' with same signature already defined in scope '" + name + "'");
                }
            }

            overloads.add(funcSymbol);
            // Also store in symbols map (first definition or overwrite for lookup)
            symbols.put(symbol.getName(), symbol);
            return;
        } //......




//--------------------------------------------------------------------------------------------------




//Vererbungsprüfung (SemanticAnalyzer.java, 762-771)
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
```

# Interpreter

```java

//Interpreter.java

//tree walking (55-92)
/**
     * execute a function
     */
    public Value executeFunction(FunctionDecl func, List<Value> arguments) {
        // create new environment for function
        Environment funcEnv = new Environment(globalEnv);
        Environment previousEnv = currentEnv;
        currentEnv = funcEnv;

        try {
            // bind parameters
            for (int i = 0; i < func.getParameters().size(); i++) {
                Parameter param = func.getParameters().get(i);
                Value arg = arguments.get(i);

                if (param.isReference()) {
                    // parameter is a reference - create reference value
                    currentEnv.define(param.getName(), new Value(arg));
                } else {
                    // parameter is not a reference - copy the value (with slicing)
                    currentEnv.define(param.getName(), copyValueWithSlicing(arg, param.getType()));
                }
            }

            // execute function body
            if (func.getBody() != null) {
                func.getBody().accept(this);
            }

            // if no return, return default value
            return Value.defaultValue(func.getReturnType());

        } catch (ReturnException e) {
            return e.getValue();
        } finally {
            currentEnv = previousEnv;
        }
    }




//--------------------------------------------------------------------------------------------------




// dynamic dispatch
// (512.546)
  public Value visitMemberAccessExpr(MemberAccessExpr node) {
        Value objValue = node.getObject().accept(this);
        ObjectValue obj = objValue.getObjectValue();

        if (node.isMethodCall()) {
            // Use resolved method from semantic analysis (handles overloading)
            MethodSymbol method = node.getResolvedMethod();

            if (method == null) {
                // Fallback to lookup if not resolved (shouldn't happen after semantic analysis)
                Type staticType = node.getObject().getType();
                ClassSymbol staticClass = classes.get(staticType.getClassName());
                method = staticClass.lookupMethod(node.getMemberName());
            }

            if (method == null) {
                throw new RuntimeError("method '" + node.getMemberName() + "' not found");
            }

            // Get the STATIC type from the expression (the declared type of the reference/variable)
            Type staticType = node.getObject().getType();
            ClassSymbol staticClass = classes.get(staticType.getClassName());

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            // Check if the method (or any ancestor's version) is virtual
            boolean isVirtual = isMethodVirtual(staticClass, node.getMemberName());

            // resolve virtual method (if the method is virtual, use dynamic dispatch)
            if (isVirtual) {
                // dynamic dispatch: find matching method in the actual runtime type
                // We need to find a method with the same signature in the derived class
                MethodSymbol dynamicMethod = findMatchingMethod(obj.getClassSymbol(), node.getMemberName(), method);
                if (dynamicMethod != null) {
                    method = dynamicMethod;
                }
            } //...


    //(903-941)
    /**
     * Check if a method is virtual, looking up the inheritance chain.
     * A method is virtual if it or any of its ancestors declared it as virtual.
     */
    private boolean isMethodVirtual(ClassSymbol classSymbol, String methodName) {
        ClassSymbol current = classSymbol;
        while (current != null) {
            List<MethodSymbol> overloads = current.getMethods().get(methodName);
            if (overloads != null) {
                for (MethodSymbol method : overloads) {
                    if (method.isVirtual()) {
                        return true;
                    }
                }
            }
            current = current.getBaseClass();
        }
        return false;
    }

    /**
     * Find a method with matching signature in the given class (for dynamic dispatch).
     */
    private MethodSymbol findMatchingMethod(ClassSymbol classSymbol, String methodName, MethodSymbol original) {
        List<MethodSymbol> overloads = classSymbol.lookupMethodOverloads(methodName);
        for (MethodSymbol method : overloads) {
            if (method.getParameters().size() == original.getParameters().size()) {
                boolean match = true;
                for (int i = 0; i < method.getParameters().size(); i++) {
                    Type t1 = method.getParameters().get(i).getType();
                    Type t2 = original.getParameters().get(i).getType();
                    if (!t1.equals(t2)) {
                        match = false;
                        break;
                    }
                }
                if (match) {
                    return method;
                }
            }
        }
        return null;
    }




//--------------------------------------------------------------------------------------------------




// referenzsemantik Value.java
//(14-27)

  // constructor for non-reference values
    public Value(Type type, Object data) {
        this.type = type;
        this.data = data;
        this.isReference = false;
        this.referencedValue = null;
    }

    // constructor for reference values
    public Value(Value referencedValue) {
        this.type = referencedValue.getType();
        this.data = null;
        this.isReference = true;
        this.referencedValue = referencedValue;
    }

//(42-48)
// get the actual value (following references if necessary)
    public Object getData() {
        if (isReference) {
            return referencedValue.getData();
        }
        return data;
    }


// Interpreter.java, executeFunction(...) (65-76)
   // bind parameters
   for (int i = 0; i < func.getParameters().size(); i++) {
       Parameter param = func.getParameters().get(i);
       Value arg = arguments.get(i);

       if (param.isReference()) {
           // parameter is a reference - create reference value
           currentEnv.define(param.getName(), new Value(arg));
       } else {
           // parameter is not a reference - copy the value (with slicing)
           currentEnv.define(param.getName(), copyValueWithSlicing(arg, param.getType()));
       }




//--------------------------------------------------------------------------------------------------




//Objekt repräsentation
// ObjectValue.java (11-18)
public class ObjectValue {
    private ClassSymbol classSymbol;
    private Map<String, Value> fields;

    public ObjectValue(ClassSymbol classSymbol) {
        this.classSymbol = classSymbol;
        this.fields = new HashMap<>();
    }


// Interpreter.java (725-752)
private Value copyValueWithSlicing(Value value, Type targetType) {
        if (value.getType().getBaseType() == Type.BaseType.CLASS) {
            ObjectValue original = value.getObjectValue();
            ClassSymbol targetClass;

            // Determine the target class (for slicing)
            if (targetType.getBaseType() == Type.BaseType.CLASS) {
                targetClass = classes.get(targetType.getClassName());
            } else {
                targetClass = original.getClassSymbol();
            }

            if (targetClass == null) {
                targetClass = original.getClassSymbol();
            }

            // Create a new object with the TARGET class (this enables correct slicing)
            ObjectValue copy = new ObjectValue(targetClass);

            // Only copy fields that belong to the target class (and its ancestors)
            copyFieldsForClass(copy, original, targetClass);

            return new Value(targetType, copy);
        } else {
            // primitive types - create new value with same data
            return new Value(value.getType(), value.getData());
        }
    }
```

# REPL

```java
// Main.java

//Mehrzeilige Eingaben
//promptlogik (169-176)
   while (true) {
            try {
                // print prompt
                if (multiLineInput.length() == 0) {
                    System.out.print(">>> ");
                } else {
                    System.out.print("... ");
                }

// Klammerzählung (220-304)
  private static boolean isInputComplete(String input) {
        // simple heuristic: check if braces are balanced
        int braceCount = 0;
        boolean inString = false;
        boolean inChar = false;
        boolean inLineComment = false;
        boolean inBlockComment = false;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            char next = (i + 1 < input.length()) ? input.charAt(i + 1) : '\0';

            if (inLineComment) {
                if (c == '\n') {
                    inLineComment = false;
                }
                continue;
            }

            if (inBlockComment) {
                if (c == '*' && next == '/') {
                    inBlockComment = false;
                    i++; // skip next char
                }
                continue;
            }

            if (inString) {
                if (c == '\\') {
                    i++; // skip next char
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }

            if (inChar) {
                if (c == '\\') {
                    i++; // skip next char
                } else if (c == '\'') {
                    inChar = false;
                }
                continue;
            }

            // check for comments
            if (c == '/' && next == '/') {
                inLineComment = true;
                i++; // skip next char
                continue;
            }

            if (c == '/' && next == '*') {
                inBlockComment = true;
                i++; // skip next char
                continue;
            }

            // check for strings/chars
            if (c == '"') {
                inString = true;
                continue;
            }

            if (c == '\'') {
                inChar = true;
                continue;
            }

            // count braces
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }
        }

        // input is complete if braces are balanced and ends with ; or }
        if (braceCount != 0) {
            return false;
        }

        String trimmed = input.trim();
        return trimmed.endsWith(";") || trimmed.endsWith("}");
    }




//--------------------------------------------------------------------------------------------------




//Session persistenz
//(76-78)
// no file - start with empty environment
                symbolTable = new SymbolTable();
                interpreter = new Interpreter(symbolTable);
//(354-356)
 // build symbol table incrementally (extends existing symbolTable)
            SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder(symbolTable);
            symbolTableBuilder.build(program);




//--------------------------------------------------------------------------------------------------




// Dummy Wrap
// Wrapper hinzufügen (306-320)
 private static void executeREPLInput(String input) {
        try {
            // check if input is a function or class definition
            String trimmed = input.trim();
            boolean isDefinition = trimmed.matches("(?s)^(class|struct)\\s+\\w+.*") ||
                                  trimmed.matches("(?s)^\\w+\\s+\\w+\\s*\\([^)]*\\)\\s*\\{.*");

            String sourceToParseCode;
            if (isDefinition) {
                // don't wrap definitions
                sourceToParseCode = input;
            } else {
                // wrap input in dummy function for statements
                sourceToParseCode = "void __repl_dummy__() { " + input + " }";
            }

// Wrapper entfernen (358-361)
            // immediately remove dummy function so it can be redefined next time
            if (!isDefinition) {
                symbolTable.getGlobalScope().remove("__repl_dummy__");
            }




//--------------------------------------------------------------------------------------------------




//Expression auto print
//erkennen + ausgabe (395-412)

                // check if single expression statement
                boolean isExprStmt = (block.getStatements().size() == 1 &&
                                      block.getStatements().get(0) instanceof ExprStmt);

                // execute statements
                for (Statement stmt : block.getStatements()) {
                    // auto-print for expression statements (evaluate before executing the statement)
                    if (isExprStmt && stmt instanceof ExprStmt) {
                        ExprStmt exprStmt = (ExprStmt) stmt;
                        de.hsbi.interpreter.runtime.Value value = exprStmt.getExpression().accept(interpreter);

                        if (value != null && value.getType().getBaseType() != Type.BaseType.VOID) {
             /*-------->*/ printValue(value);
                        }
                    } else {
                        stmt.accept(interpreter);
                    }
                }

// typspezifische ausgabe durch printValue
private static void printValue(de.hsbi.interpreter.runtime.Value value) {
        switch (value.getType().getBaseType()) {
            case INT:
                System.out.println(value.getIntValue());
                break;
            case BOOL:
                System.out.println(value.getBoolValue());
                break;
            case CHAR:
                System.out.println("'" + value.getCharValue() + "'");
                break;
            case STRING:
                System.out.println("\"" + value.getStringValue() + "\"");
                break;
            case CLASS:
                System.out.println(value.getObjectValue());
                break;
            default:
                System.out.println(value);
                break;
        }
    }


```