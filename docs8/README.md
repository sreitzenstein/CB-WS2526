# C++ Interpreter

## Features

Der Interpreter unterstützt:

- **Typen:** `bool`, `int`, `char`, `string`, `void`, Klassen
- **Variablen:** Deklaration, Initialisierung, Zuweisungen
- **C++-Referenzen:** `int& ref = x;` mit korrekter Referenz-Semantik
- **Ausdrücke:** Arithmetik (`+`, `-`, `*`, `/`, `%`), Vergleich (`<`, `<=`, `>`, `>=`, `==`, `!=`), Logik (`&&`, `||`, `!`)
- **Kontrollfluss:** `if`-`else`, `while`, `return`, Blöcke `{ }`
- **Funktionen:** Definition, Aufruf, Überladung (exakte Signatur)
- **Klassen:** Felder, Methoden, Konstruktoren, Einfach-Vererbung
- **Polymorphie:** `virtual` Methoden mit dynamischem Dispatch
- **Built-in-Funktionen:** `print_bool()`, `print_int()`, `print_char()`, `print_string()`
- **Kommentare:** `//` und `/* ... */`

## Einmalige Einrichtung

**HINWEIS:** Die Parser-Dateien sind bereits generiert und im Repository enthalten. Dieser Schritt ist nur notwendig, wenn die Grammatik (`src/main/antlr4/CPP.g4`) geändert wird.

### ANTLR Parser neu generieren (nur bei Grammatik-Änderungen)

```bash
cd interpreter
java -jar lib/antlr-4.13.1-complete.jar -visitor -package de.hsbi.interpreter.parser \
  -o src/main/java/de/hsbi/interpreter/parser src/main/antlr4/CPP.g4
```

Dies generiert:
- `CPPLexer.java` - Tokenizer
- `CPPParser.java` - Parser
- `CPPVisitor.java` - Visitor Interface
- `CPPBaseVisitor.java` - Basis-Visitor-Implementierung

## Kompilierung

**Windows:**
```bash
javac -cp "lib/antlr-4.13.1-complete.jar;src/main/java" -d out/production src/main/java/de/hsbi/interpreter/**/*.java src/main/java/de/hsbi/interpreter/*.java
```

**Linux/Mac:**
```bash
javac -cp "lib/antlr-4.13.1-complete.jar:src/main/java" -d out/production src/main/java/de/hsbi/interpreter/**/*.java src/main/java/de/hsbi/interpreter/*.java
```

## Ausführung

### REPL-Modus (interaktiv)

**Windows:**
```bash
java -cp "lib/antlr-4.13.1-complete.jar;out/production" de.hsbi.interpreter.Main
```

**Linux/Mac:**
```bash
java -cp "lib/antlr-4.13.1-complete.jar:out/production" de.hsbi.interpreter.Main
```

Der REPL startet mit:
```
C++ Interpreter
===============

REPL Mode
=========
Enter C++ statements. Type 'exit' or 'quit' to exit.

>>>
```

### Datei-Modus

**Windows:**
```bash
java -cp "lib/antlr-4.13.1-complete.jar;out/production" de.hsbi.interpreter.Main <datei.cpp>
```

**Linux/Mac:**
```bash
java -cp "lib/antlr-4.13.1-complete.jar:out/production" de.hsbi.interpreter.Main <datei.cpp>
```

Beispiel:
```bash
java -cp "lib/antlr-4.13.1-complete.jar;out/production" de.hsbi.interpreter.Main ../test.cpp
```

## REPL Bedienung

### Einzelne Statements

```
>>> int x = 42;
>>> print_int(x);
42
```

### Mehrzeilige Eingaben

Der REPL erkennt automatisch, wenn eine Eingabe noch nicht vollständig ist (unbalancierte Klammern):

```
>>> int add(int a, int b) {
...     return a + b;
... }
>>> print_int(add(5, 3));
8
```

### Expression Auto-Print

Einzelne Ausdrücke werden automatisch ausgegeben:

```
>>> 5 + 3;
8
>>> true && false;
false
>>> "Hello";
"Hello"
```

### REPL beenden

```
>>> exit
Goodbye!
```

Oder:
- `quit` eingeben
- Ctrl+D (EOF)

## Beispiel-Programme

### Einfaches Programm

```cpp
int add(int a, int b) {
    return a + b;
}

int main() {
    int x = 5;
    int y = 10;
    int result = add(x, y);

    print_string("Result: ");
    print_int(result);

    return 0;
}
```

### Klassen und Vererbung

```cpp
class Animal {
public:
    int age;

    Animal() {
        age = 0;
    }

    virtual void speak() {
        print_string("...");
    }
};

class Dog : public Animal {
public:
    string name;

    Dog() {
        name = "Unknown";
    }

    Dog(string n) {
        name = n;
    }

    void speak() {
        print_string("Woof! My name is ");
        print_string(name);
    }
};

int main() {
    Dog d = Dog("Bello");
    d.age = 3;

    Animal& a = d;  // Referenz auf Dog
    a.speak();       // Ruft Dog::speak() auf (dynamic dispatch!)

    return 0;
}
```

### C++-Referenzen

```cpp
void swap(int& a, int& b) {
    int temp = a;
    a = b;
    b = temp;
}

int main() {
    int x = 5;
    int y = 10;

    print_int(x);  // 5
    print_int(y);  // 10

    swap(x, y);

    print_int(x);  // 10
    print_int(y);  // 5

    return 0;
}
```

## Architektur

Der Interpreter durchläuft folgende Phasen:

```
Source Code
    ↓
[Lexer] → Tokens
    ↓
[Parser] → Parse-Tree
    ↓
[ASTBuilder] → AST (Abstract Syntax Tree)
    ↓
[SymbolTableBuilder] → Symbol Table (Two-Pass)
    ↓
[SemanticAnalyzer] → Type Checking, Validation
    ↓
[Interpreter] → Execution (Tree-Walking)
```

### Komponenten

1. **Lexer & Parser:** Tokenisierung und Parse-Tree-Erstellung
2. **ASTBuilder:** Konvertiert Parse-Tree zu sauberem AST
3. **SymbolTableBuilder:** Erstellt Symboltabelle (Two-Pass für Forward-References)
4. **SemanticAnalyzer:** Prüft Typen, LValues, Referenzen, Funktionsaufrufe, etc.
5. **Interpreter:** Tree-Walking Interpreter mit Runtime-Environment
6. **REPL:** Read-Eval-Print-Loop mit Multi-Line-Support

## Technische Details

### Two-Pass Symbol Building

Der SymbolTableBuilder macht zwei Durchläufe:

1. **Pass 1:** Registriert alle Klassen und Funktionen (nur Namen)
2. **Pass 2:** Verarbeitet Bodies, Member, Felder

Dies ermöglicht Forward-References (Klassen/Funktionen können vor ihrer Definition verwendet werden).

### Virtual Dispatch

Methoden mit `virtual` werden dynamisch zur Laufzeit aufgelöst:

- Statischer Typ: `Animal& a`
- Runtime-Typ: `Dog`
- Bei `a.speak()`: Ruft `Dog::speak()` auf (nicht `Animal::speak()`)

### Slicing

Bei Zuweisung von abgeleiteter zu Basis-Klasse:

```cpp
Dog d;
Animal a = d;  // Slicing: nur Animal-Teil wird kopiert
```

### Referenz-Semantik

Referenzen sind echte Aliase (wie in C++):

```cpp
int x = 5;
int& ref = x;
ref = 10;      // x ist jetzt auch 10!
```

Implementiert durch indirekte Wert-Speicherung in der `Value`-Klasse.

## Einschränkungen

Der Interpreter unterstützt NICHT:

- Pointer (`*`, `&` als Adressoperator, `->`)
- Arrays
- Globale Variablen
- `break`/`continue` in Schleifen
- Multiple Vererbung
- Templates
- `static`, `const`, `friend`
- Namespaces
- Casts
- Compound-Assignments (`+=`, `-=`, etc.)
- Inkrement/Dekrement (`++`, `--`)

## Entwicklungsstand

- ✅ Grammatik (ANTLR)
- ✅ AST-Klassen (24 Klassen)
- ✅ Parser/ASTBuilder
- ✅ Symboltabellen (8 Klassen)
- ✅ Semantische Analyse
- ✅ Tree-Walking Interpreter
- ✅ Runtime-System (Values, Objects, Environment)
- ✅ REPL mit Multi-Line-Support
- ✅ Alle Features implementiert

**Gesamtumfang:** ~4.000 Zeilen Java-Code

## Tests

Beispiel-Tests befinden sich in `../test.cpp` und `../tests/`.

Test ausführen:
```bash
java -cp "lib/antlr-4.13.1-complete.jar;out/production" de.hsbi.interpreter.Main ../test.cpp
```

## Abhängigkeiten

- **Java:** Version 21 oder höher
- **ANTLR Runtime:** 4.13.1 (im `lib/` Verzeichnis enthalten, wird zur Ausführung benötigt)

## Dokumentation

Weitere Dokumentation:
- `AST_DESIGN.md` - Detaillierte AST-Struktur
- `../aufGutDeutsch.md` - Erklärung auf Deutsch, wie alles funktioniert
- `../probleme.md` - Gelöste Probleme während der Entwicklung
