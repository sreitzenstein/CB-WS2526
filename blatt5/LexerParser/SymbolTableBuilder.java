package LexerParser;

import java.util.*;

public class SymbolTableBuilder {

    Scope root;
    Scope current;

    public void build(List<Stmt> program) {

        // FIRST PASS: ONLY GATHER DECLARATIONS
        root = new Scope(null);
        current = root;

        for (Stmt stmt : program) {
            collectDeclarations(stmt);
        }

        // SECOND PASS: CHECK REFERENCES
        current = root;
        for (Stmt stmt : program) {
            checkReferences(stmt);
        }
    }

    // ======================= FIRST PASS ==============================

    private void collectDeclarations(Stmt stmt) {

        if (stmt instanceof VarDecl varDecl) {
            SymbolTableEntry e =
                    new SymbolTableEntry(SymbolTableEntry.Child.VARIABLE, varDecl.type, varDecl.name);

            if (!current.define(e)) {
                System.err.println("Fehler: Variable '" + varDecl.name +
                        "' mehrfach im selben Scope definiert.");
            }
            varDecl.entry = e;
        }

        else if (stmt instanceof FnDecl fn) {
            SymbolTableEntry e =
                    new SymbolTableEntry(SymbolTableEntry.Child.FUNCTION, fn.returnType, fn.name);
            e.params = fn.params;

            if (!current.define(e)) {
                System.err.println("Fehler: Funktion '" + fn.name +
                        "' mehrfach definiert.");
            }

            enterScope();

            for (Param p : fn.params) {
                SymbolTableEntry pEntry =
                        new SymbolTableEntry(SymbolTableEntry.Child.VARIABLE, p.type, p.name);

                if (!current.define(pEntry)) {
                    System.err.println("Fehler: Parameter '" + p.name +
                            "' mehrfach definiert.");
                }
            }

            collectDeclarations(fn.body);
            exitScope();
        }

        else if (stmt instanceof Block block) {
            enterScope();
            for (Stmt s : block.statements) collectDeclarations(s);
            exitScope();
        }

        else if (stmt instanceof IfStmt is) {
            collectDeclarations(is.thenBranch);
            if (is.elseBranch != null)
                collectDeclarations(is.elseBranch);
        }

        else if (stmt instanceof WhileStmt ws) {
            collectDeclarations(ws.body);
        }

        // Assign, ExprStmt, ReturnStmt: only references → handled later
    }

    // ======================= SECOND PASS =============================

    private void checkReferences(Stmt stmt) {

        if (stmt instanceof VarDecl varDecl) {
            if (varDecl.initializer != null)
                checkExpr(varDecl.initializer);
        }

        else if (stmt instanceof FnDecl fn) {
            enterScope();

            for (Param p : fn.params) {
                // Params already defined in first pass
            }

            checkReferences(fn.body);
            exitScope();
        }

        else if (stmt instanceof Block block) {
            enterScope();
            for (Stmt s : block.statements) checkReferences(s);
            exitScope();
        }

        else if (stmt instanceof Assign assign) {
            checkExpr(new Variable(assign.name)); // ensure variable exists
            checkExpr(assign.value);
        }

        else if (stmt instanceof ExprStmt es) {
            checkExpr(es.expr);
        }

        else if (stmt instanceof IfStmt is) {
            checkExpr(is.condition);
            checkReferences(is.thenBranch);
            if (is.elseBranch != null)
                checkReferences(is.elseBranch);
        }

        else if (stmt instanceof WhileStmt ws) {
            checkExpr(ws.condition);
            checkReferences(ws.body);
        }

        else if (stmt instanceof ReturnStmt rs) {
            if (rs.value != null)
                checkExpr(rs.value);
        }
    }

    private void checkExpr(Expr expr) {

        if (expr instanceof Variable v) {
            SymbolTableEntry e = current.resolve(v.name);
            if (e == null) {
                System.err.println("Fehler: Variable '" + v.name +
                        "' nicht definiert oder nicht sichtbar.");
            }
            v.entry = e;
        }

        else if (expr instanceof Binary b) {
            checkExpr(b.left);
            checkExpr(b.right);
        }

        else if (expr instanceof Call c) {

            SymbolTableEntry e = current.resolve(c.name);

            if (e == null) {
                System.err.println("Fehler: Funktion '" + c.name +
                        "' ist nicht definiert oder nicht sichtbar.");
            } else if (e.child != SymbolTableEntry.Child.FUNCTION) {
                System.err.println("Fehler: '" + c.name + "' ist keine Funktion.");
            }

            c.entry = e;

            for (Expr arg : c.args) checkExpr(arg);
        }

    }


    private void enterScope() {
        current = new Scope(current);
    }

    private void exitScope() {
        current = current.parent;
    }

    public Scope getRoot() {
        return root;
    }
}
