package de.hsbi.interpreter.symbols;

import de.hsbi.interpreter.ast.ClassDecl;
import de.hsbi.interpreter.ast.Type;

import java.util.HashMap;
import java.util.Map;

/**
 * represents a class in the symbol table
 */
public class ClassSymbol extends Symbol {
    private String baseClassName;
    private ClassSymbol baseClass;
    private Map<String, VarSymbol> fields;
    private Map<String, MethodSymbol> methods;
    private Map<String, ConstructorSymbol> constructors;
    private ClassDecl declaration;

    public ClassSymbol(String name, String baseClassName, ClassDecl declaration) {
        super(name, new Type(name));
        this.baseClassName = baseClassName;
        this.baseClass = null;
        this.fields = new HashMap<>();
        this.methods = new HashMap<>();
        this.constructors = new HashMap<>();
        this.declaration = declaration;
    }

    public String getBaseClassName() {
        return baseClassName;
    }

    public ClassSymbol getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(ClassSymbol baseClass) {
        this.baseClass = baseClass;
    }

    public Map<String, VarSymbol> getFields() {
        return fields;
    }

    public Map<String, MethodSymbol> getMethods() {
        return methods;
    }

    public Map<String, ConstructorSymbol> getConstructors() {
        return constructors;
    }

    public ClassDecl getDeclaration() {
        return declaration;
    }

    public void addField(VarSymbol field) {
        fields.put(field.getName(), field);
    }

    public void addMethod(MethodSymbol method) {
        methods.put(method.getName(), method);
    }

    public void addConstructor(ConstructorSymbol constructor) {
        constructors.put(constructor.getName(), constructor);
    }

    // lookup field including inherited fields
    public VarSymbol lookupField(String name) {
        VarSymbol field = fields.get(name);
        if (field != null) {
            return field;
        }
        if (baseClass != null) {
            return baseClass.lookupField(name);
        }
        return null;
    }

    // lookup method including inherited methods
    public MethodSymbol lookupMethod(String name) {
        MethodSymbol method = methods.get(name);
        if (method != null) {
            return method;
        }
        if (baseClass != null) {
            return baseClass.lookupMethod(name);
        }
        return null;
    }

    @Override
    public SymbolKind getKind() {
        return SymbolKind.CLASS;
    }
}
