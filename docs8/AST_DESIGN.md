# AST-Design Dokumentation

## Was ist ein AST?

Ein **Abstract Syntax Tree (AST)** ist eine Baumstruktur, die den Quellcode in einer vereinfachten, strukturierten Form darstellt. Jeder Knoten im Baum repräsentiert ein Sprachelement (z.B. eine Funktion, eine Variable, einen Ausdruck).

**Wichtig:** "Abstract" bedeutet, dass wir unwichtige Details weglassen:
- Keine Semikolons, Klammern, Kommata (nur syntaktische Hilfen)
- Nur die **semantisch relevanten** Informationen bleiben

## Warum brauchen wir einen AST?

Der ANTLR-Parser gibt uns zwar einen Parse-Tree, aber dieser enthält **zu viele Details**:
- Jedes Token (auch Semikolons, Klammern)
- Alle Zwischenregeln der Grammatik
- Schwer zu navigieren für semantische Analyse

Unser AST ist **minimal** und enthält nur das, was wir wirklich brauchen:
- Welche Typen haben Variablen?
- Welche Ausdrücke werden berechnet?
- Welche Statements werden ausgeführt?

## AST-Hierarchie

### Basis-Knoten

Alle AST-Knoten erben von `ASTNode`:
```
ASTNode (abstrakte Basisklasse)
├── Declaration (Deklarationen)
├── Statement (Anweisungen)
├── Expression (Ausdrücke)
└── Type (Typen)
```

### Positionsinformationen

Jeder AST-Knoten speichert seine **Position im Quellcode** für Fehlermeldungen:

```java
public abstract class ASTNode {
    private int line;    // Zeilennummer (1-basiert)
    private int column;  // Spaltennummer (0-basiert)

    public void setPosition(int line, int column);
    public int getLine();
    public int getColumn();
}
```

**Warum ist das wichtig?**
- Fehlermeldungen können die genaue Position angeben: `"variable 'x' not found (line 42)"`
- Der ASTBuilder setzt die Position beim Erstellen jedes Knotens aus dem ANTLR-Kontext:
  ```java
  ReturnStmt stmt = new ReturnStmt(value);
  stmt.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
  ```

**Hinweis:** Nicht alle Knoten haben garantiert eine Position. Wenn `line == -1`, wurde keine Position gesetzt (z.B. bei synthetisch erzeugten Knoten).

## 1. Declarations (Deklarationen)

**Was gehört dazu:** Alles, was etwas "definiert" (Klassen, Funktionen, Variablen)

### ClassDecl
Repräsentiert eine Klassendeklaration.

**Wichtige Felder:**
- `name` - Klassenname
- `baseClass` - Name der Basisklasse (kann null sein)
- `fields` - Liste der Felder (VarDecl)
- `methods` - Liste der Methoden (MethodDecl)
- `constructors` - Liste der Konstruktoren (ConstructorDecl)

**Was fällt weg:**
- Das Schlüsselwort `class`, `public`, `:`
- Die geschweiften Klammern `{}`
- Das Semikolon am Ende

**Beispiel:**
```cpp
class Dog : public Animal {
public:
    string name;
    void bark() { print_string("Wuff"); }
};
```

Wird zu:
```
ClassDecl(
  name = "Dog",
  baseClass = "Animal",
  fields = [VarDecl(type="string", name="name")],
  methods = [MethodDecl(name="bark", ...)]
)
```

### FunctionDecl
Repräsentiert eine Funktionsdefinition.

**Wichtige Felder:**
- `returnType` - Rückgabetyp (Type)
- `name` - Funktionsname
- `parameters` - Liste von Parametern (Parameter)
- `body` - Funktionskörper (BlockStmt)

**Was fällt weg:**
- Klammern `()`, `{}`
- Kommas zwischen Parametern

### MethodDecl
Wie FunctionDecl, aber zusätzlich:
- `isVirtual` - boolean (ist die Methode virtual?)

### ConstructorDecl
Wie FunctionDecl, aber:
- Kein Rückgabetyp (Konstruktoren haben keinen)
- Name = Klassenname

### VarDecl
Repräsentiert eine Variablendeklaration.

**WICHTIG:** VarDecl erbt von `Statement` (nicht von Declaration), da Variablendeklarationen in C++ überall in einem Block stehen können und somit als Statements behandelt werden müssen.

**Wichtige Felder:**
- `type` - Typ der Variable (Type)
- `isReference` - boolean (ist es eine Referenz mit `&`?)
- `name` - Variablenname
- `initializer` - Initialisierungsausdruck (Expression, kann null sein)

**Was fällt weg:**
- `&` Symbol (wird zu `isReference = true`)
- `=` Symbol
- Semikolon

### Parameter
Repräsentiert einen Funktionsparameter.

**Wichtige Felder:**
- `type` - Typ des Parameters (Type)
- `isReference` - boolean
- `name` - Parametername

## 2. Statements (Anweisungen)

**Was gehört dazu:** Alles, was "ausgeführt" wird

### BlockStmt
Ein Block von Statements `{ ... }`

**Wichtige Felder:**
- `statements` - Liste von Statements

**Was fällt weg:**
- Die geschweiften Klammern

### IfStmt
If-Anweisung (mit optionalem else)

**Wichtige Felder:**
- `condition` - Bedingung (Expression)
- `thenStmt` - Then-Zweig (Statement)
- `elseStmt` - Else-Zweig (Statement, kann null sein)

**Was fällt weg:**
- `if`, `else` Keywords
- Klammern `()`

### WhileStmt
While-Schleife

**Wichtige Felder:**
- `condition` - Bedingung (Expression)
- `body` - Schleifenkörper (Statement)

### ReturnStmt
Return-Anweisung

**Wichtige Felder:**
- `value` - Rückgabewert (Expression, kann null sein bei void)

### ExprStmt
Ein Ausdruck als Statement (z.B. Funktionsaufruf)

**Wichtige Felder:**
- `expression` - Der Ausdruck (Expression)

**Was fällt weg:**
- Das Semikolon

## 3. Expressions (Ausdrücke)

**Was gehört dazu:** Alles, was einen **Wert berechnet**

### BinaryExpr
Binärer Operator (z.B. `a + b`, `x < y`)

**Wichtige Felder:**
- `operator` - Der Operator (+, -, *, /, %, ==, !=, <, <=, >, >=, &&, ||)
- `left` - Linker Operand (Expression)
- `right` - Rechter Operand (Expression)

**Beispiel:** `5 + 3` wird zu `BinaryExpr(PLUS, LiteralExpr(5), LiteralExpr(3))`

### UnaryExpr
Unärer Operator (z.B. `-x`, `!flag`)

**Wichtige Felder:**
- `operator` - Der Operator (+, -, !)
- `operand` - Operand (Expression)

### AssignExpr
Zuweisung (`x = 5`)

**Wichtige Felder:**
- `target` - Ziel der Zuweisung (Expression, muss LValue sein)
- `value` - Wert (Expression)

**Was fällt weg:**
- Das `=` Symbol (ergibt sich aus dem Knotentyp)

### VarExpr
Variablenzugriff (z.B. `x`)

**Wichtige Felder:**
- `name` - Variablenname

### CallExpr
Funktions- oder Methodenaufruf

**Wichtige Felder:**
- `functionName` - Funktionsname
- `arguments` - Liste von Argumenten (Expression)

**Beispiel:** `add(5, 3)` wird zu `CallExpr("add", [LiteralExpr(5), LiteralExpr(3)])`

### MemberAccessExpr
Zugriff auf Feld oder Methode (`obj.field` oder `obj.method()`)

**Wichtige Felder:**
- `object` - Objekt (Expression)
- `memberName` - Name des Members
- `isMethodCall` - boolean (ist es ein Methodenaufruf?)
- `arguments` - Argumente (falls Methodenaufruf)

**Was fällt weg:**
- Der Punkt `.`
- Klammern bei Methodenaufrufen

### ConstructorCallExpr
Konstruktoraufruf (z.B. `Dog("Bello")`)

**Wichtige Felder:**
- `className` - Name der Klasse
- `arguments` - Argumente

### LiteralExpr
Literal-Wert (Konstante)

**Wichtige Felder:**
- `type` - Typ des Literals (INT, BOOL, CHAR, STRING)
- `value` - Der Wert (als Object)

**Beispiel:**
- `42` → `LiteralExpr(INT, 42)`
- `true` → `LiteralExpr(BOOL, true)`
- `'A'` → `LiteralExpr(CHAR, 'A')`
- `"foo"` → `LiteralExpr(STRING, "foo")`

## 4. Types (Typen)

### Type
Repräsentiert einen Typ.

**Wichtige Felder:**
- `baseType` - Der Basistyp (BOOL, INT, CHAR, STRING, VOID, oder Klassenname)
- `isReference` - boolean (nur bei Rückgabetypen immer false, da keine Referenz-Rückgaben erlaubt)

**Hinweis:** Referenzen werden in VarDecl und Parameter separat mit `isReference` gespeichert.

## Unterschiede: Parse-Tree vs. AST

### Parse-Tree (von ANTLR)
```
program
└── functionDecl
    ├── type: "int"
    ├── IDENTIFIER: "add"
    ├── LPAREN: "("
    ├── parameterList
    │   ├── parameter
    │   │   ├── type: "int"
    │   │   └── IDENTIFIER: "a"
    │   ├── COMMA: ","
    │   └── parameter
    │       ├── type: "int"
    │       └── IDENTIFIER: "b"
    ├── RPAREN: ")"
    └── block
        └── ...
```

### Unser AST (vereinfacht)
```
FunctionDecl(
  returnType = Type(INT),
  name = "add",
  parameters = [
    Parameter(Type(INT), false, "a"),
    Parameter(Type(INT), false, "b")
  ],
  body = BlockStmt(...)
)
```

**Viel übersichtlicher!** Alle unwichtigen Details (Klammern, Kommas) sind weg.

## Warum ist das praktisch?

### Für semantische Analyse:
- Einfach prüfen: "Hat diese Variable den richtigen Typ?"
- Direkt navigieren: "Welche Parameter hat diese Funktion?"
- Keine Ablenkung durch Syntaxdetails

### Für Interpretation:
- Direkt ausführen: "Führe dieses Statement aus"
- Werte berechnen: "Berechne diesen Ausdruck"
- Klar strukturiert: Jeder Knotentyp macht genau eine Sache

## Beispiel: Komplettes Programm

### C++ Code:
```cpp
class Dog {
public:
    string name;
    Dog(string n) { name = n; }
    void bark() { print_string(name); }
};

int main() {
    Dog d = Dog("Bello");
    d.bark();
    return 0;
}
```

### AST (vereinfacht):
```
Program([
  ClassDecl(
    name = "Dog",
    baseClass = null,
    fields = [
      VarDecl(Type(STRING), false, "name", null)
    ],
    constructors = [
      ConstructorDecl("Dog", [Parameter(Type(STRING), false, "n")],
        BlockStmt([
          ExprStmt(AssignExpr(
            VarExpr("name"),
            VarExpr("n")
          ))
        ])
      )
    ],
    methods = [
      MethodDecl(false, Type(VOID), "bark", [],
        BlockStmt([
          ExprStmt(CallExpr("print_string", [VarExpr("name")]))
        ])
      )
    ]
  ),
  FunctionDecl(Type(INT), "main", [],
    BlockStmt([
      VarDecl(Type("Dog"), false, "d",
        ConstructorCallExpr("Dog", [LiteralExpr(STRING, "Bello")])
      ),
      ExprStmt(
        MemberAccessExpr(VarExpr("d"), "bark", true, [])
      ),
      ReturnStmt(LiteralExpr(INT, 0))
    ])
  )
])
```

## Zusammenfassung

**AST = Abstrakte Darstellung des Codes**

- **Abstrakt:** Keine syntaktischen Details (Klammern, Semikolons, Keywords)
- **Strukturiert:** Baumform, leicht zu traversieren
- **Semantisch:** Nur das, was für Bedeutung wichtig ist

**Vorteile:**
- Einfacher für semantische Analyse
- Einfacher für Interpretation
- Klare Trennung von Syntax und Semantik

**Was fällt weg:**
- Alle Tokens die nur syntaktisch sind (Klammern, Kommas, Semikolons, Keywords)
- Zwischenregeln der Grammatik

**Was bleibt:**
- Typen, Namen, Werte
- Struktur (welches Statement in welchem Block?)
- Operatoren (als Enum, nicht als String)
- **Positionsinformationen** (Zeile, Spalte) für Fehlermeldungen

## 5. ASTBuilder: Parse-Tree → AST

Der `ASTBuilder` konvertiert den ANTLR Parse-Tree in unseren AST. Er erweitert `CPPBaseVisitor<ASTNode>` und überschreibt die `visit*`-Methoden.

### Wichtige Aufgaben:

1. **Knoten erstellen:** Für jede Grammatikregel einen passenden AST-Knoten erzeugen
2. **Position setzen:** Zeilennummer aus dem ANTLR-Kontext übernehmen
3. **Kinder besuchen:** Rekursiv in Unterknoten absteigen

### Beispiel:

```java
@Override
public ASTNode visitReturnStmt(CPPParser.ReturnStmtContext ctx) {
    Expression value = ctx.expression() != null
            ? (Expression) visit(ctx.expression())
            : null;
    ReturnStmt stmt = new ReturnStmt(value);
    stmt.setPosition(ctx.getStart().getLine(), ctx.getStart().getCharPositionInLine());
    return stmt;
}
```

### Knoten mit Positionsinformationen:

Folgende Knoten setzen ihre Position im ASTBuilder:
- **Declarations:** `ClassDecl`, `FunctionDecl`, `MethodDecl`, `ConstructorDecl`, `VarDecl` (als Feld)
- **Statements:** `ReturnStmt`
- **Expressions:** `AssignExpr`, `ConstructorCallExpr`, `VarExpr`, `MemberAccessExpr`

## 6. Visitor-Pattern

Der AST verwendet das **Visitor-Pattern** für Traversierung. Jeder Knoten hat eine `accept`-Methode:

```java
public abstract <T> T accept(ASTVisitor<T> visitor);
```

### Verwendung:

```java
// Im Knoten (z.B. ReturnStmt):
public <T> T accept(ASTVisitor<T> visitor) {
    return visitor.visitReturnStmt(this);
}

// Im Visitor (z.B. SemanticAnalyzer):
@Override
public Type visitReturnStmt(ReturnStmt node) {
    // Analysiere das Return-Statement
    if (node.getValue() != null) {
        Type returnType = node.getValue().accept(this);
        // ...
    }
}
```

### Vorteile:
- **Separation of Concerns:** AST-Struktur ist getrennt von den Operationen
- **Erweiterbar:** Neue Operationen ohne Änderung der AST-Klassen
- **Typsicher:** Compiler prüft, dass alle Knoten behandelt werden

### Unsere Visitors:
1. `SymbolTableBuilder` - Baut die Symboltabelle auf
2. `SemanticAnalyzer` - Prüft Typen und semantische Regeln
3. `Interpreter` - Führt das Programm aus
