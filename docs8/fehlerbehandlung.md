# Fehlerbehandlung im C++ Interpreter

## Übersicht

Der Interpreter erkennt Fehler in drei Phasen:

1. **Parsing (Syntaxfehler)** - ANTLR-Parser
2. **Symboltabellen-Aufbau** - SymbolTableBuilder
3. **Semantische Analyse** - SemanticAnalyzer

Laufzeitfehler werden vom Interpreter selbst behandelt.

---

## 1. Syntaxfehler (Parsing)

**Technische Umsetzung:** ANTLR-generierter Parser (`CPPParser.java`)

Der Parser erkennt Verstöße gegen die Grammatik (`CPP.g4`).

**Beispiele:**
- Fehlende Semikolons
- Unbalancierte Klammern
- Ungültige Token-Reihenfolge

---

## 2. Symboltabellen-Fehler (SymbolTableBuilder)

**Technische Umsetzung:** `SymbolTableBuilder.java` sammelt Fehler in einer `List<String> errors`

### Erkannte Fehler

| Fehler | Beschreibung |
|--------|--------------|
| Variablen-Redefinition | `variable 'x' is already defined in this scope` |
| Unbekannte Basisklasse | `base class 'Foo' not found for class 'Bar'` |

### Code-Struktur

```java
public class SymbolTableBuilder implements ASTVisitor<Void> {
    private List<String> errors = new ArrayList<>();

    private void error(String message) {
        errors.add(message);
    }

    @Override
    public Void visitVarDecl(VarDecl node) {
        if (symbolTable.getCurrentScope().isDefined(node.getName())) {
            error("variable '" + node.getName() + "' is already defined in this scope");
        } else {
            symbolTable.define(new VarSymbol(...));
        }
        return null;
    }
}
```

---

## 3. Semantische Fehler (SemanticAnalyzer)

**Technische Umsetzung:** `SemanticAnalyzer.java` sammelt Fehler in einer `List<String> errors`

### Erkannte Fehler

#### Typfehler
| Fehler | Beispiel |
|--------|----------|
| Typ-Mismatch bei Zuweisung | `int x = "hello";` |
| Typ-Mismatch bei Rückgabe | `int f() { return true; }` |
| Typ-Mismatch bei Vergleich | `if (5 + "x") ...` |
| Falscher Argumenttyp | `print_int("hello");` |

#### Referenz-Fehler
| Fehler | Beispiel |
|--------|----------|
| Referenz ohne Initialisierer | `int& r;` |
| Referenz auf Nicht-LValue | `int& r = 5;` |
| Referenz-Argument nicht LValue | `void f(int& x); f(42);` |

#### Funktions-/Methoden-Fehler
| Fehler | Beispiel |
|--------|----------|
| Unbekannte Funktion | `foo();` (nicht definiert) |
| Falsche Argumentanzahl | `void f(int x); f(1, 2);` |
| Mehrdeutiger Aufruf | `void f(int x); void f(int& x); int a; f(a);` |
| Keine passende Überladung | `void f(int x); f("hello");` |

#### Klassen-Fehler
| Fehler | Beispiel |
|--------|----------|
| Unbekannte Klasse | `Foo x;` (Klasse nicht definiert) |
| Kein passender Konstruktor | `class C { C(int x); }; C c;` |
| Unbekanntes Feld | `obj.unknownField` |
| Unbekannte Methode | `obj.unknownMethod()` |
| Rückgabetyp-Mismatch bei Override | Überschriebene Methode hat anderen Rückgabetyp |

#### LValue-Fehler
| Fehler | Beispiel |
|--------|----------|
| Zuweisung an Nicht-LValue | `5 = x;` |
| Inkrement/Dekrement auf Nicht-LValue | `5++;` |

### Code-Struktur

```java
public class SemanticAnalyzer implements ASTVisitor<Type> {
    private List<String> errors = new ArrayList<>();

    private void error(String message) {
        errors.add(message);
    }

    @Override
    public Type visitAssignExpr(AssignExpr node) {
        if (!isLValue(node.getLeft())) {
            error("left side of assignment must be an lvalue");
        }
        // ... Typ-Prüfung
    }
}
```

### Funktionsüberladung

Die Methode `findBestFunctionOverload()` löst überladene Funktionen auf:

1. **Sammelt alle viable Überladungen** - Funktionen mit passender Signatur
2. **Prüft Referenz-Kompatibilität** - `f(int&)` braucht LValue-Argument
3. **Erkennt Mehrdeutigkeit** - Wenn `f(int)` und `f(int&)` beide passen

```java
private FunctionSymbol findBestFunctionOverload(List<FunctionSymbol> overloads, List<Expression> arguments) {
    List<FunctionSymbol> viableOverloads = new ArrayList<>();

    for (FunctionSymbol func : overloads) {
        // Prüfe ob alle Argumente passen
        // Referenz-Parameter brauchen LValue-Argument
    }

    if (viableOverloads.size() > 1) {
        // Prüfe auf Mehrdeutigkeit (z.B. int vs int&)
        error("call is ambiguous between: f(int), f(int&)");
        return null;
    }

    return viableOverloads.get(0);
}
```

---

## 4. Laufzeitfehler (Interpreter)

**Technische Umsetzung:** `RuntimeError` Exception in `Interpreter.java`

### Erkannte Fehler

| Fehler | Beschreibung |
|--------|--------------|
| Division durch Null | `x / 0` oder `x % 0` |
| Null-Referenz | Zugriff auf nicht initialisiertes Objekt |
| Unbekannte Variable | Variable zur Laufzeit nicht gefunden |

```java
case DIV:
    if (rightInt == 0) {
        throw new RuntimeError("division by zero");
    }
    return new Value(intType, leftInt / rightInt);
```

---

## Fehlerausgabe in Main.java

```java
// 1. Symbol Table Fehler prüfen
SymbolTableBuilder symbolTableBuilder = new SymbolTableBuilder();
symbolTable = symbolTableBuilder.build(program);

if (symbolTableBuilder.hasErrors()) {
    System.err.println("Semantic errors:");
    for (String error : symbolTableBuilder.getErrors()) {
        System.err.println("  " + error);
    }
    System.exit(1);
}

// 2. Semantische Fehler prüfen
SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable);
boolean success = semanticAnalyzer.analyze(program);

if (!success) {
    System.err.println("Semantic errors:");
    for (String error : semanticAnalyzer.getErrors()) {
        System.err.println("  " + error);
    }
    System.exit(1);
}

// 3. Ausführen (Laufzeitfehler als Exception)
interpreter.execute(program);
```

---

## Beispiel-Ausgaben

**Variablen-Redefinition:**
```
Semantic errors:
  variable 'a' is already defined in this scope
```

**Mehrdeutiger Funktionsaufruf:**
```
Semantic errors:
  call is ambiguous between: f(int), f(int&)
```

**Typ-Mismatch:**
```
Semantic errors:
  variable 'x': initializer type 'string' does not match variable type 'int'
```

**Referenz ohne LValue:**
```
Semantic errors:
  function 'f': argument 1 must be an lvalue (parameter is a reference)
```
