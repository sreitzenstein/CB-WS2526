# Herangehensweise: C++-Interpreter Projekt

## 1. Projektplanung und Vorbereitung ✓ ERLEDIGT

### 1.1 Anforderungen analysieren ✓
**Status: Abgeschlossen**

#### Testfall-Analyse
**Positive Tests (tests/pos/):** 20 Testdateien
- GOLD01_basics.cpp - Grundfunktionen (Variablen, Funktionen, if/while)
- P01_vars.cpp - Variablendeklarationen mit/ohne Initialisierung
- P02_expr.cpp - Ausdrücke und Operatoren
- P03_ifthenelse.cpp - If-Then-Else Kontrollfluss
- P04_while.cpp - While-Schleifen
- P05_operators.cpp - Operatorpräzedenz und -assoziativität
- P06_refs.cpp - Referenzen (Definition, Nutzung, Zuweisung)
- P07_scopes_and_shadowing.cpp - Scope-Hierarchie
- P08_func.cpp - Funktionen (inkl. Rekursion, by-value/by-reference)
- P09_short_circuit.cpp - Short-Circuit-Evaluation (&&, ||)
- P10_class_defaults.cpp - Klassen mit Default-Konstruktor
- P11_class_custom.cpp - Klassen mit Custom-Konstruktoren
- P12_class_mixed.cpp - Klassen mit verschiedenen Konstruktoren
- P13_ambiguous_overload.cpp - Funktionsüberladung
- P14_methods_refs_chaining.cpp - Methodenaufrufe mit Referenzen
- P15_return_object_by_value.cpp - Objekte als Rückgabewert
- P17_inheritance.cpp - Einfachvererbung
- P18_polymorphie_static.cpp - Statischer Dispatch
- P19_polymorphie_static_ref.cpp - Statischer Dispatch mit Referenzen
- P20_polymorphie_dynamic.cpp - Dynamischer Dispatch (virtual)
- GOLD02_ref_params.cpp - Referenz-Parameter
- GOLD03_classes_dispatch.cpp - Klassen und Dispatch
- GOLD04_slicing.cpp - Object Slicing bei Wertzuweisung
- GOLD05_virtual_override.cpp - Virtual Override
- GOLD06_constructors_basic.cpp - Konstruktoren (Basis)
- GOLD07_constructors_inheritance.cpp - Konstruktoren mit Vererbung

**Negative Tests (tests/neg/):** 11 Testdateien
- N01_redeclaration.cpp - Fehler: Mehrfachdefinition im selben Scope
- N02_redeclaration.cpp - Weitere Redeclaration-Fehler
- N03_ref_noinit.cpp - Fehler: Referenz ohne Initialisierung
- N04_ref_init_rvalue.cpp - Fehler: Referenz mit RValue initialisiert
- N05_assign_to_rvalue.cpp - Fehler: Zuweisung an RValue
- N06_wrong_arity.cpp - Fehler: Falsche Argumentanzahl
- N07_void_return_with_value.cpp - Fehler: void-Funktion mit return-Wert
- N08_unknown_member.cpp - Fehler: Unbekanntes Member
- N09_method_not_in_static_type.cpp - Fehler: Methode nicht im statischen Typ
- N10_polymorphie.cpp - Fehler: Polymorphie-Probleme
- N11_ambiguous_overload.cpp - Fehler: Mehrdeutige Überladung (int vs. int&)

#### Wichtige Erkenntnisse aus den Tests

**Kernfeatures:**
1. **Basistypen:** bool, int, char, string (mit Escape-Sequenzen wie '\0')
2. **Variablen:** Deklaration mit/ohne Initialisierung, nur lokale Variablen
3. **Referenzen:**
   - Müssen mit LValue initialisiert werden
   - Keine Neubindung (Zuweisung schreibt ins referenzierte Objekt)
   - Referenzen auf Referenzen möglich
4. **Funktionen:**
   - Rekursion (siehe McCarthy 91, f95)
   - Überladung mit exaktem Match (inkl. & Unterscheidung!)
   - By-value und by-reference Parameter
   - Return by value
5. **Klassen:**
   - Felder und Methoden (alles public)
   - Default-Konstruktor (synthetisiert falls nicht definiert)
   - Custom-Konstruktoren
   - Einfachvererbung (class D : public B)
6. **Polymorphie:**
   - **Statisch:** Non-virtual Methoden -> statischer Typ entscheidet
   - **Dynamisch:** Virtual Methoden + Referenz -> dynamischer Typ entscheidet
   - **Slicing:** Bei Wertzuweisung Base b = der; wird nur Base-Anteil kopiert
7. **Kontrollfluss:** if-then-else, while, return, Blöcke
8. **Operatoren:** Arithmetik, Vergleich, Logik mit Short-Circuit
9. **Built-ins:** print_bool, print_int, print_char, print_string
10. **Kommentare:** //, /* */, und # (Präprozessor)

**Kritische Semantik-Details:**
- Overload-Resolution: `f(int)` und `f(int&)` sind VERSCHIEDENE Signaturen!
- Mehrdeutigkeit bei Überladung führt zu Fehler (siehe N11)
- Virtual-Methoden in Basisklasse bleiben virtual in abgeleiteten Klassen (auch ohne erneutes `virtual`)
- Bei Vererbung: Basisklassen-Default-Konstruktor wird implizit vor dem Body aufgerufen
- Referenzen dürfen NICHT als Felder oder Rückgabetypen verwendet werden
- Kein Shadowing: Parameter/lokale Variablen dürfen nicht gleich wie Felder heißen

**Test-Format:**
- Alle Tests haben `/* EXPECT: ... */` Kommentare mit erwarteter Ausgabe
- Positive Tests: Zeilenweise erwartete Ausgabe
- Negative Tests: Sollten Fehlermeldung erzeugen

### 1.2 Technologie-Entscheidung ✓
**Status: Entschieden**

- **Parser-Framework:** ANTLR 4.13.1
- **Programmiersprache:** Java 21
- **Build-System:** Manuell (kein Maven/Gradle)
- **Begründung:** ANTLR installiert, Java-Kenntnisse vorhanden, gute Tool-Unterstützung

**Projektstruktur erstellt:**
```
interpreter/
├── lib/
│   └── antlr-4.13.1-complete.jar    # ANTLR Runtime
├── src/main/
│   ├── antlr4/                       # Hier kommt die Grammatik hin
│   └── java/de/hsbi/interpreter/
│       ├── Main.java                 # Haupteinstiegspunkt
│       ├── ast/                      # AST-Klassen
│       ├── parser/                   # ANTLR-generierter Code
│       ├── semantic/                 # Semantische Analyse
│       ├── interpreter/              # Tree-Walking Interpreter
│       └── repl/                     # REPL
└── README.md
```

## 2. Grammatik-Design ✓ ERLEDIGT

### 2.1 Lexikalische Grammatik definieren ✓
**Status: Abgeschlossen**

**Implementierte Tokens in CPP.g4:**
- **Schlüsselwörter:** class, public, virtual, if, else, while, return
- **Typen:** bool, int, char, string, void
- **Bool-Literale:** true, false
- **Operatoren:**
  - Arithmetik: +, -, *, /, %
  - Vergleich: ==, !=, <, <=, >, >=
  - Logik: &&, ||, !
  - Sonstiges: =, &, .
- **Delimiters:** (, ), {, }, ;, ,, :
- **Literale:**
  - INT_LITERAL: [0-9]+
  - CHAR_LITERAL: mit Escape-Sequenzen (\0, \', \", \\, \n, \r, \t)
  - STRING_LITERAL: mit Escape-Sequenzen
- **Identifier:** [a-zA-Z_][a-zA-Z0-9_]*
- **Kommentare:** //, /* */, # (werden geskippt)

### 2.2 Syntaktische Grammatik definieren ✓
**Status: Abgeschlossen**

**Implementierte Parser-Regeln:**
- **Program:** Sequenz von Klassen- und Funktionsdefinitionen
- **Klassendeklaration:**
  - Mit optionaler Vererbung (: public BaseClass)
  - Members: Felder, Methoden, Konstruktoren
  - Alles in public-Sektion
- **Funktionsdeklaration:** Typ, Name, Parameter, Block
- **Parameterliste:** Typen mit optionaler & Referenz-Markierung
- **Statements:** block, varDecl, ifStmt, whileStmt, returnStmt, exprStmt
- **Expressions mit Präzedenz (korrekt implementiert):**
  1. Assignment (=, rechtsassoziativ)
  2. Logical OR (||)
  3. Logical AND (&&)
  4. Equality (==, !=)
  5. Relational (<, <=, >, >=)
  6. Additive (+, -)
  7. Multiplicative (*, /, %)
  8. Unary (+, -, !)
  9. Postfix (Funktionsaufrufe, Member-Zugriff)
  10. Primary (Literale, Variablen, Klammern, Konstruktoraufrufe)

### 2.3 ANTLR-Grammatik generieren ✓
**Status: Abgeschlossen**

**Generierte Dateien:**
```bash
java -jar lib/antlr-4.13.1-complete.jar \
  -Dlanguage=Java -visitor -no-listener \
  -package de.hsbi.interpreter.parser \
  -o src/main/java/de/hsbi/interpreter/parser \
  src/main/antlr4/CPP.g4
```

**Erstellt:**
- CPPLexer.java (19KB)
- CPPParser.java (79KB)
- CPPVisitor.java (6.5KB)
- CPPBaseVisitor.java (8.4KB)
- Token-Dateien (.tokens, .interp)

## 3. AST-Design ✓ ERLEDIGT

### 3.1 AST-Knotenklassen definieren ✓
**Status: Abgeschlossen**

**Dokumentation erstellt:** [AST_DESIGN.md](AST_DESIGN.md)
- Ausführliche Erklärung des AST-Konzepts
- Was ist ein AST und warum brauchen wir ihn?
- Unterschied Parse-Tree vs. AST
- Beispiele für jede Knotenart

**Implementierte Klassen:**

**Basis:**
- `ASTNode` - Basisklasse mit Positionsinformationen
- `ASTVisitor<T>` - Visitor-Interface

**Deklarationen:**
- `Program` - Wurzelknoten (Klassen + Funktionen)
- `ClassDecl` - Klasse (Name, Basis, Felder, Methoden, Konstruktoren)
- `FunctionDecl` - Funktion (Rückgabetyp, Name, Parameter, Body)
- `MethodDecl` - Methode (wie Function, aber mit isVirtual)
- `ConstructorDecl` - Konstruktor (Name, Parameter, Body)
- `VarDecl` - Variable (Typ, isReference, Name, Initializer)
- `Parameter` - Funktionsparameter (Typ, isReference, Name)
- `Type` - Typ (BaseType Enum, Klassenname für CLASS-Typ)

**Statements:**
- `Statement` - Basisklasse für Statements
- `BlockStmt` - Block von Statements
- `IfStmt` - If-Statement (Condition, Then, optionales Else)
- `WhileStmt` - While-Schleife (Condition, Body)
- `ReturnStmt` - Return (optionaler Value)
- `ExprStmt` - Expression-Statement

**Expressions:**
- `Expression` - Basisklasse mit Type-Feld (für semantische Analyse)
- `BinaryExpr` - Binärer Operator (Operator-Enum, Left, Right)
- `UnaryExpr` - Unärer Operator (Operator-Enum, Operand)
- `AssignExpr` - Zuweisung (Target, Value)
- `VarExpr` - Variablenzugriff (Name)
- `CallExpr` - Funktionsaufruf (FunctionName, Arguments)
- `MemberAccessExpr` - Member-Zugriff (Object, MemberName, isMethodCall, Arguments)
- `ConstructorCallExpr` - Konstruktoraufruf (ClassName, Arguments)
- `LiteralExpr` - Literal (LiteralType-Enum, Value)

**Design-Entscheidungen:**
- Referenzen werden als boolean-Flag in VarDecl/Parameter gespeichert (nicht als eigener Typ)
- Alle syntaktischen Details (Klammern, Semikolons, Keywords) werden weggelassen
- Operatoren als Enums (nicht als Strings) für Typ-Sicherheit
- Visitor-Pattern für flexible Traversierung

### 3.2 Kompilierung testen ✓
**Status: Erfolgreich**

```bash
javac -cp "lib/antlr-4.13.1-complete.jar" -d build \
  src/main/java/de/hsbi/interpreter/ast/*.java \
  src/main/java/de/hsbi/interpreter/parser/*.java
```

**Ergebnis:** Alle Klassen kompilieren ohne Fehler!

## 4. Stand der Implementierung

### Was haben wir jetzt?

✅ **Grammatik (CPP.g4)** - Definiert die Syntax unserer Sprache
✅ **ANTLR-generierter Parser** - Kann Code in Parse-Tree umwandeln
✅ **AST-Klassen (24 Stück)** - Saubere Datenstruktur für Code-Repräsentation
✅ **AST-Dokumentation** - Ausführliche Erklärung in AST_DESIGN.md und aufGutDeutsch.md

### Was fehlt noch?

❌ **AST-Builder** - Konvertiert ANTLR Parse-Tree → unser AST (komplex, später)
❌ **Symboltabellen** - Speichert Variablen, Funktionen, Klassen
❌ **Semantische Analyse** - Prüft Typen, Scopes, etc.
❌ **Interpreter** - Führt den Code aus
❌ **REPL** - Interaktive Shell

**Nächster Schritt:** Symboltabellen und Scopes implementieren (dann können wir Namen auflösen)

## 4. Lexer und Parser implementieren

### 4.1 Lexer implementieren
- Bei ANTLR: Generieren aus Grammatik
- Kommentare und Präprozessor-Direktiven filtern
- Fehlerbehandlung für ungültige Zeichen

### 4.2 Parser implementieren
- Bei ANTLR: Generieren aus Grammatik, Listener/Visitor für AST-Konstruktion
- AST während des Parsens aufbauen
- Fehlerbehandlung für Syntaxfehler mit klaren Meldungen

### 4.3 Parser testen
- Mit einfachen Testfällen beginnen
- Schrittweise komplexere Tests hinzufügen
- AST visualisieren/ausgeben zur Kontrolle

## 5. Symboltabellen und Scopes

### 5.1 Scope-Hierarchie implementieren
- Globaler Scope (Klassen, Funktionen)
- Funktions-/Methoden-Scope (lokale Variablen)
- Block-Scope (verschachtelte Blöcke)
- Sitzungs-Scope für REPL
- Klassen-Scope (Felder, Methoden, geerbte Members)

### 5.2 Symbol-Klassen definieren
- VarSymbol (Name, Typ, ist-Referenz, Scope)
- FunctionSymbol (Name, Parameter mit Typen, Rückgabetyp, Overloads)
- ClassSymbol (Name, Basisklasse, Felder, Methoden, Konstruktoren)
- MethodSymbol (wie FunctionSymbol, zusätzlich: ist-virtual, überschreibt-Methode)

### 5.3 Name Resolution implementieren
- Lookup-Mechanismus für Variablen, Funktionen, Klassen
- Sichtbarkeitsregeln beachten:
  - Variablen: define-before-use (in Funktionen/REPL)
  - Funktionen/Klassen: define-after-use (nur bei Initialisierung, nicht in REPL)
- Overload-Resolution für Funktionen (exakter Match)
- Member-Lookup in Klassen mit Vererbung

## 6. Semantische Analyse

### 6.1 Mehrpass-Analyse für Initialisierung
- Pass 1: Klassen registrieren
- Pass 2: Vererbungshierarchie aufbauen und prüfen (keine Zyklen)
- Pass 3: Felder und Methoden registrieren
- Pass 4: Funktionen registrieren
- Pass 5: Funktions-/Methoden-Bodies und main() prüfen

### 6.2 Typ-Checking implementieren
- Typen für alle Expressions ableiten
- Typ-Kompatibilität prüfen:
  - Binäre Operatoren: beide Seiten gleicher Typ
  - Zuweisungen: kompatible Typen (mit Slicing-Ausnahme)
  - Funktions-/Methodenaufrufe: Argumenttypen passen zu Parametern
  - Return-Statements: Typ passt zu Funktionsrückgabetyp
- Implizite Konversionen in booleschen Kontexten (if/while)

### 6.3 LValue-Prüfung
- LValues: benannte Variablen, Feldzugriffe
- Prüfen bei:
  - Zuweisungen (linke Seite muss LValue sein)
  - Referenz-Initialisierung (nur LValues erlaubt)

### 6.4 Weitere semantische Prüfungen
- Keine Mehrfachdefinitionen im selben Scope
- Variablen nicht aufrufbar, Funktionen nicht zuweisbar
- Referenz-Variablen müssen initialisiert werden
- Keine Referenzen als Felder oder Rückgabetypen
- Keine Referenzen als globale Variablen
- Kein Shadowing in Methoden (Parameter/lokale Variablen vs. Felder)
- Default-Konstruktor synthetisieren falls nötig
- Bei Vererbung: Basisklassen-Konstruktor wird implizit aufgerufen

### 6.5 Fehlerbehandlung
- Aussagekräftige Fehlermeldungen mit Zeilennummer
- Bei Fehler: Analyse abbrechen

## 7. Interpreter (Tree-Walking)

### 7.1 Umgebung (Environment) für Laufzeitwerte
- Environment-Klasse mit Scope-Kette
- Speichert Variablen-Werte zur Laufzeit
- Unterstützt verschachtelte Scopes

### 7.2 Wert-Repräsentation
- Value-Klasse für Laufzeitwerte (int, bool, char, string, Objekt-Referenz)
- Objekt-Repräsentation für Klasseninstanzen (Felder-Map)

### 7.3 Expression-Evaluation
- Literale auswerten
- Variablen-Lookup in Environment
- Binäre/Unäre Operatoren ausführen
- Zuweisungen durchführen (inkl. Referenz-Semantik)
- Short-Circuit-Evaluation für && und ||
- Funktions-/Methodenaufrufe mit Argument-Übergabe

### 7.4 Statement-Execution
- If/While mit Bedingungsauswertung
- Return-Wert behandeln (Exception oder spezieller Rückgabemechanismus)
- Blöcke mit neuem Scope ausführen

### 7.5 Funktions-/Methodenaufrufe
- Parameter-Bindung (by-value vs. by-reference)
- Neuer Scope für Funktions-Body
- Return-Wert zurückgeben

### 7.6 Klassen und Objekte
- Konstruktor-Aufruf: Felder initialisieren, Basisklassen-Konstruktor, Body
- Objekt-Erzeugung und Feldzugriff
- Methoden-Dispatch:
  - Statisch (non-virtual): Methode basierend auf statischem Typ
  - Dynamisch (virtual): Methode basierend auf dynamischem Typ (nur bei Referenzen)
- Slicing bei Wertzuweisungen

### 7.7 Vererbung und Polymorphie
- Dynamischer Dispatch über vtable oder ähnlichen Mechanismus
- Überschriebene virtuelle Methoden korrekt aufrufen
- Referenzen: dynamischer Typ vs. statischer Typ tracken

### 7.8 Built-in-Funktionen
- print_bool, print_int, print_char, print_string implementieren

### 7.9 Laufzeit-Fehlerbehandlung
- Division durch 0
- Andere Laufzeitfehler mit klaren Meldungen

## 8. REPL-Implementierung

### 8.1 Initialisierungsphase
- Optional: Datei einlesen
- Mehrpass-Analyse durchführen
- Globalen Scope aufbauen
- main()-Funktion im Sitzungs-Scope ausführen (falls vorhanden)

### 8.2 REPL-Loop
- Prompt ausgeben (z.B. `>>> `)
- Eingabe einlesen:
  - Bei vollständiger Eingabe: parsen und ausführen
  - Bei unvollständiger Eingabe (z.B. offene geschweifte Klammer): Hilfsprompt (z.B. `... `) und weitere Zeilen einlesen
- Statement/Expression parsen (Single-Pass: define-before-use)
- Semantische Analyse
- Interpretieren:
  - Neue Klassen/Funktionen: in globalen Scope
  - Neue Variablen: in Sitzungs-Scope
  - Expression-Statements: Ergebnis automatisch ausgeben
  - Andere Statements: keine automatische Ausgabe
- Fehler abfangen und anzeigen, REPL weiterlaufen lassen

### 8.3 REPL beenden
- Spezielle Eingabe (z.B. `exit`, `quit`, Strg+D)

## 9. Testing

### 9.1 Unit-Tests
- Lexer-Tests (Token-Erkennung)
- Parser-Tests (AST-Konstruktion)
- Semantische Analyse-Tests (Fehler erkennen)
- Interpreter-Tests (korrekte Ausführung)

### 9.2 Integrationstests
- Testfälle aus Starter-Projekt durchführen
- Positive Tests: erwartete Ausgabe prüfen
- Negative Tests: Fehlermeldung prüfen

### 9.3 Eigene Tests entwickeln
- Edge Cases testen
- Komplexe Vererbungshierarchien
- Überladene Funktionen
- Virtual vs. Non-Virtual Dispatch
- Referenz-Semantik
- Slicing

## 10. Dokumentation

### 10.1 Spezifikation (README_SPEC.md)
- Kurze Sprachbeschreibung
- Alle getroffenen Designentscheidungen dokumentieren:
  - ANTLR vs. handgeschriebener Parser
  - AST-Struktur
  - Scope-Modell
  - Mehrpass-Analyse-Strategie
  - Dynamischer Dispatch-Mechanismus
  - Referenz-Implementierung
- Einschränkungen und Annahmen

### 10.2 Implementierung (README_IMPL.md)
- ANTLR-Grammatik (oder Parser-Beschreibung)
- AST-Design
- Scopes/Symboltabellen
- Semantische Checks
- Interpreter mit dynamischem Dispatch
- REPL-Implementierung

### 10.3 Benutzeranleitung (README.md)
- Wie bauen (z.B. ANTLR-Generierung, Maven/Gradle)
- Wie ausführen (z.B. `java -jar interpreter.jar [optional-file.cpp]`)
- Wie REPL nutzen:
  - Prompt-Erklärung
  - Beispiel-Eingaben
  - Wie beenden
- Beispiel-Code-Snippets

### 10.4 Anleitung für Demonstration
- Vorbereitung der C++-Codebeispiele:
  - Einfache Variablen und Arithmetik
  - Funktionen mit Überladung
  - Klassen mit Vererbung
  - Virtuelle Methoden und dynamischer Dispatch
  - Referenzen
  - Slicing-Beispiel
  - REPL-Interaktion
- Schritt-für-Schritt-Ablauf der Demo
- Erwartete Ausgaben dokumentieren

## 11. Walk-Through vorbereiten

### 11.1 Demo-Szenarien planen
- Szenario 1: Einfache Berechnungen und Variablen
- Szenario 2: Funktionen und Überladung
- Szenario 3: Klassen ohne Vererbung
- Szenario 4: Einfachvererbung und Polymorphie
- Szenario 5: Virtual vs. Non-Virtual Dispatch
- Szenario 6: Referenzen und Slicing
- Szenario 7: REPL-Features (inkrementelle Definitionen)

### 11.2 Testdateien erstellen
- Für jedes Szenario eine `.cpp`-Datei
- Mit Kommentaren die erwartete Ausgabe dokumentieren

### 11.3 Präsentation üben
- Ablauf durchgehen
- Timing prüfen
- Mögliche Fragen antizipieren
