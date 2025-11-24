package LexerParser;

import java.util.*;

// Basis-Interfaces
interface Stmt {}
interface Expr {}

// Primitive Typen
enum PrimType {
    INT, STRING, BOOL
}

// Operatoren
enum Operator {
    EQ, NEQ, PLUS, MINUS, MUL, DIV, LT, GT
}

// Parameter
class Param {
    PrimType type;
    String name;
    
    public Param(PrimType type, String name) {
        this.type = type;
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Param(" + type + ", \"" + name + "\")";
    }
}

// Statement-Knoten
class VarDecl implements Stmt {
    PrimType type;
    String name;
    Expr initializer; 
    SymbolTableEntry entry;
    
    public VarDecl(PrimType type, String name, Expr initializer) {
        this.type = type;
        this.name = name;
        this.initializer = initializer;
    }
    
    @Override
    public String toString() {
        return "VarDecl(" + type + ", \"" + name + "\", " + 
               (initializer != null ? initializer : "None") + ")";
    }
}

class Assign implements Stmt {
    String name;
    Expr value;
    
    public Assign(String name, Expr value) {
        this.name = name;
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "Assign(\"" + name + "\", " + value + ")";
    }
}

class FnDecl implements Stmt {
    PrimType returnType;
    String name;
    List<Param> params;
    Block body;
    SymbolTableEntry entry;
    
    public FnDecl(PrimType returnType, String name, List<Param> params, Block body) {
        this.returnType = returnType;
        this.name = name;
        this.params = params;
        this.body = body;
    }
    
    @Override
    public String toString() {
        return "FnDecl(" + returnType + ", \"" + name + "\", " + params + ", " + body + ")";
    }
}

class ReturnStmt implements Stmt {
    Expr value;
    
    public ReturnStmt(Expr value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "ReturnStmt(" + value + ")";
    }
}

class ExprStmt implements Stmt {
    Expr expr;
    
    public ExprStmt(Expr expr) {
        this.expr = expr;
    }
    
    @Override
    public String toString() {
        return "ExprStmt(" + expr + ")";
    }
}

class Block implements Stmt {
    List<Stmt> statements;
    
    public Block(List<Stmt> statements) {
        this.statements = statements;
    }
    
    @Override
    public String toString() {
        return "Block(" + statements + ")";
    }
}

class WhileStmt implements Stmt {
    Expr condition;
    Block body;
    
    public WhileStmt(Expr condition, Block body) {
        this.condition = condition;
        this.body = body;
    }
    
    @Override
    public String toString() {
        return "WhileStmt(" + condition + ", " + body + ")";
    }
}

class IfStmt implements Stmt {
    Expr condition;
    Block thenBranch;
    Block elseBranch;
    
    public IfStmt(Expr condition, Block thenBranch, Block elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
    
    @Override
    public String toString() {
        return "IfStmt(" + condition + ", " + thenBranch + ", " + elseBranch + ")";
    }
}

// Expression-Knoten
class IntLiteral implements Expr {
    int value;
    
    public IntLiteral(int value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "IntLiteral(" + value + ")";
    }
}

class StringLiteral implements Expr {
    String value;
    
    public StringLiteral(String value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "StringLiteral(\"" + value + "\")";
    }
}

class BoolLiteral implements Expr {
    boolean value;
    
    public BoolLiteral(boolean value) {
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "BoolLiteral(" + value + ")";
    }
}

class Variable implements Expr {
    String name;
    SymbolTableEntry entry;
    
    public Variable(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "Variable(\"" + name + "\")";
    }
}

class Binary implements Expr {
    Expr left;
    Operator op;
    Expr right;
    
    public Binary(Expr left, Operator op, Expr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }
    
    @Override
    public String toString() {
        return "Binary(" + left + ", " + op + ", " + right + ")";
    }
}

class Call implements Expr {
    String name;
    List<Expr> args;
    SymbolTableEntry entry;

    public Call(String name, List<Expr> args) {
        this.name = name;
        this.args = args;
    }
    
    @Override
    public String toString() {
        return "Call(\"" + name + "\", " + args + ")";
    }
}
