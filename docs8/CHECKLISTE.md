# Blatt 8: C++-Interpreter Checkliste

Diese Checkliste basiert auf der Aufgabenstellung in `docs8/aufgabe.md`.

---

## 1. Grundlegende Komponenten

### 1.1 Lexer
- [x] Erkennt alle Tokens (Schlüsselwörter, Bezeichner, Literale, Operatoren) ✓ implizit durch alle Tests
- [x] Zeilenkommentare `//` werden ignoriert ✓ TEST_17
- [x] Blockkommentare `/* ... */` werden ignoriert ✓ TEST_17
- [x] Präprozessor-Direktiven `#...` werden wie Kommentare behandelt ✓ TEST_01
- [x] Escape-Sequenzen in Literalen (`\n`, `\0`, `\\`, etc.) ✓ TEST_01

### 1.2 Parser
- [x] Korrekte Operatorpräzedenz (unär > multiplikativ > additiv > relational > equality > && > || > assignment) ✓ TEST_02
- [x] Assignment ist rechtsassoziativ ✓ TEST_13
- [x] Klammern zur Gruppierung `(...)` ✓ TEST_02

### 1.3 AST
- [x] Alle Knoten korrekt modelliert ✓ implizit durch alle Tests
- [~] Zeilennummern für Fehlermeldungen (teilweise, Parser-Fehler haben Zeilen)

### 1.4 Semantische Analyse
- [x] Typprüfung für alle Operatoren ✓ implizit durch TEST_02, TEST_03, TEST_04
- [x] Keine Mehrfachdefinitionen im selben Scope ✓ TEST_19
- [x] "define-before-use" für Variablen ✓ TEST_19
- [x] "define-after-use" für Funktionen/Klassen (beim Start) ✓ implizit durch alle Tests
- [x] LValue-Prüfung (Zuweisung nur an LValues) ✓ TEST_19

### 1.5 Interpreter
- [x] Tree-walking Interpreter ✓ implizit durch alle Tests
- [x] Korrekte Auswertung aller Ausdrücke ✓ implizit durch alle Tests
- [x] Fehlerbehandlung (Division durch 0, etc.) ✓ TEST_18

### 1.6 REPL
- [x] Prompt für Eingabe ✓ manuell getestet
- [x] Mehrzeilige Eingabe (Hilfsprompt bei unvollständiger Eingabe) ✓ manuell getestet
- [x] Expression-Statements werden automatisch ausgegeben ✓ manuell getestet
- [x] Neue Variablen/Funktionen/Klassen können definiert werden ✓ manuell getestet
- [x] Beenden der REPL möglich ✓ manuell getestet
- [x] Sitzungs-Scope nach main() bleibt offen ✓ manuell getestet

---

## 2. Typen

### 2.1 Primitive Typen
- [x] `bool` mit Werten `true`, `false` ✓ TEST_01
- [x] `int` (Ziffernfolgen ohne Dezimalpunkt) ✓ TEST_01
- [x] `char` (in einfachen Anführungsstrichen, z.B. `'a'`) ✓ TEST_01
- [x] `string` (in doppelten Anführungsstrichen, z.B. `"foo"`) ✓ TEST_01
- [x] `void` für Funktionen ohne Rückgabewert ✓ TEST_06

### 2.2 Klassentypen
- [x] Benutzerdefinierte Klassen als Typ verwendbar ✓ TEST_08
- [x] Vererbte Typen (Zuweisung Derived → Base erlaubt mit Slicing) ✓ TEST_23

---

## 3. Variablen und Zuweisungen

- [x] Deklaration ohne Initialisierung: `T x;` ✓ TEST_08
- [x] Deklaration mit Initialisierung: `T x = expr;` ✓ TEST_01
- [x] Einfache Zuweisung: `x = expr;` ✓ TEST_08
- [x] Nur lokale Variablen (keine globalen) ✓ Design
- [x] Zuweisung ist ein Ausdruck (hat einen Wert) ✓ TEST_13
- [x] Linke Seite der Zuweisung muss LValue sein ✓ TEST_19

---

## 4. C++-Referenzen

- [x] Deklaration Referenz-Variable: `T& x = expr;` ✓ TEST_07
- [x] Deklaration Referenz-Parameter: `T& p` ✓ TEST_07
- [x] Initialisierung für Referenz-Variablen obligatorisch ✓ TEST_20
- [x] Referenz kann nur mit LValue initialisiert werden ✓ TEST_20
- [x] Zuweisung an Referenz schreibt ins referenzierte Ziel (keine Neubindung) ✓ TEST_07
- [x] Keine Referenzen als Felder ✓ TEST_20
- [x] Keine Referenzen als Rückgabetyp ✓ TEST_20

---

## 5. Ausdrücke

### 5.1 Arithmetik (nur `int`)
- [x] Addition `+` ✓ TEST_02
- [x] Subtraktion `-` ✓ TEST_02
- [x] Multiplikation `*` ✓ TEST_02
- [x] Division `/` ✓ TEST_02
- [x] Modulo `%` ✓ TEST_02
- [x] Unäres Plus `+` ✓ TEST_02
- [x] Unäres Minus `-` ✓ TEST_02

### 5.2 Vergleiche
- [x] `==` (alle Typen) ✓ TEST_03
- [x] `!=` (alle Typen) ✓ TEST_03
- [x] `<` (int, char) ✓ TEST_03
- [x] `<=` (int, char) ✓ TEST_03
- [x] `>` (int, char) ✓ TEST_03
- [x] `>=` (int, char) ✓ TEST_03

### 5.3 Logik (nur `bool`)
- [x] `&&` mit Short-Circuit ✓ TEST_04
- [x] `||` mit Short-Circuit ✓ TEST_04
- [x] `!` (unäre Negation) ✓ TEST_04

### 5.4 Weitere
- [x] Klammern `(...)` ✓ TEST_02
- [x] Funktionsaufrufe `f(args)` ✓ TEST_06
- [x] Feld-/Methodenzugriff `obj.f`, `obj.m(args)` ✓ TEST_08

---

## 6. Kontrollfluss

- [x] `if` ohne `else` ✓ TEST_05
- [x] `if` mit `else` ✓ TEST_05
- [x] `while`-Schleife ✓ TEST_05
- [x] Block `{ ... }` ✓ TEST_05
- [x] `return` (mit und ohne Wert) ✓ TEST_05

---

## 7. Funktionen

- [x] Funktionsdefinition ✓ TEST_06
- [x] Funktionsaufruf ✓ TEST_06
- [x] Parameter (by-value und by-reference) ✓ TEST_06, TEST_07
- [x] Rückgabewert ✓ TEST_06
- [x] `void`-Funktionen ✓ TEST_06
- [x] Überladung (Overloading) nach Signatur (Name + Anzahl + exakte Typen inkl. `&`) ✓ TEST_06
- [x] Fehler bei mehrdeutiger Überladung ✓ implizit (SemanticAnalyzer prüft)

---

## 8. Klassen

### 8.1 Grundlagen
- [x] Klassendefinition: `class A { public: ... };` ✓ TEST_08
- [x] Felder (primitive Typen und Klassentypen) ✓ TEST_08
- [x] Methoden ✓ TEST_08
- [x] Methoden-Überladung (Overloading) ✓ TEST_15
- [x] Alles `public` sichtbar ✓ TEST_08

### 8.2 Konstruktoren
- [x] Parameterloser Default-Konstruktor (wird synthetisiert wenn keiner definiert) ✓ TEST_09
- [x] Parametrisierte Konstruktoren ✓ TEST_09
- [x] `T x;` ruft `T()` auf ✓ TEST_09
- [x] `T x = T(args);` ruft passenden Konstruktor ✓ TEST_09
- [x] Default-Initialisierung: `bool`→`false`, `int`→`0`, `char`→`'\0'`, `string`→`""` ✓ TEST_09
- [x] Felder von Klassentyp werden per Default-Konstruktor initialisiert ✓ TEST_09

### 8.3 Vererbung
- [x] Einfach-Vererbung: `class D : public B { ... }` ✓ TEST_10
- [x] Keine Zyklen in Vererbungshierarchie ✓ TEST_21
- [x] Erben aller Felder und Methoden ✓ TEST_10
- [x] Impliziter Aufruf des parameterlosen Basiskonstruktors ✓ TEST_10
- [x] Slicing bei Wertkopie: `Base b = derived;` ✓ TEST_12

### 8.4 Polymorphie
- [x] `virtual`-Methoden ✓ TEST_11
- [x] Dynamischer Dispatch bei Aufruf über Referenz ✓ TEST_11
- [x] Statischer Dispatch bei nicht-virtuellen Methoden ✓ TEST_11
- [x] Überschreiben ohne erneutes `virtual` bleibt virtuell ✓ TEST_11

### 8.5 Namensauflösung in Methoden
- [x] Lokale Variablen/Parameter vor eigenen Members ✓ TEST_15
- [x] Eigene Members vor geerbten Members ✓ TEST_15
- [x] Geerbte Members vor globalem Scope ✓ TEST_15

---

## 9. Implizite Typkonversionen

- [x] Keine impliziten Konversionen (außer im booleschen Kontext) ✓ Design
- [x] In `if`/`while`-Bedingungen: `int`/`char`/`string` → `bool` ✓ TEST_05
  - [x] `0` / `'\0'` / `""` → `false` ✓ TEST_05
  - [x] Alles andere → `true` ✓ TEST_05

---

## 10. Eingebaute Funktionen

- [x] `print_bool(b)` - gibt bool aus ✓ TEST_01, TEST_16
- [x] `print_int(i)` - gibt int aus ✓ TEST_01, TEST_16
- [x] `print_char(c)` - gibt char aus ✓ TEST_01, TEST_16
- [x] `print_string(s)` - gibt string aus ✓ TEST_01, TEST_16

---

## 11. Reservierte Schlüsselwörter

- [x] `int`, `bool`, `char`, `string`, `true`, `false` ✓ implizit (Lexer/Parser)
- [x] `if`, `else`, `while`, `return` ✓ implizit (Lexer/Parser)
- [x] `class`, `void`, `public`, `virtual` ✓ implizit (Lexer/Parser)

---

## 12. Fehlerbehandlung

- [x] Lexer-Fehler werden sauber gemeldet ✓ implizit (ANTLR)
- [x] Parser-Fehler werden sauber gemeldet ✓ TEST_21 (mit Zeilennummer)
- [x] Typ-Fehler werden sauber gemeldet ✓ TEST_19, TEST_20
- [x] Laufzeitfehler (z.B. Division durch 0) werden sauber gemeldet ✓ TEST_18

---

## 13. Programmorganisation

- [x] Einzelnes Source-File ✓ Design
- [x] Optionale `main()`-Funktion ✓ TEST_22c
- [x] `int main()` und `void main()` erlaubt ✓ TEST_22, TEST_22b
- [x] `#include`-Zeilen werden ignoriert ✓ TEST_01

---

## Zusammenfassung: Kritische Features

| Feature | Priorität | Getestet? |
|---------|-----------|-----------|
| Primitive Typen (bool, int, char, string) | Hoch | ✓ TEST_01 |
| Arithmetische Operatoren | Hoch | ✓ TEST_02 |
| Vergleichsoperatoren | Hoch | ✓ TEST_03 |
| Logische Operatoren mit Short-Circuit | Hoch | ✓ TEST_04 |
| if/else/while | Hoch | ✓ TEST_05 |
| Funktionen mit Überladung | Hoch | ✓ TEST_06 |
| C++-Referenzen | Hoch | ✓ TEST_07 |
| Klassen mit Feldern/Methoden | Hoch | ✓ TEST_08 |
| Konstruktoren (Default + parametrisiert) | Hoch | ✓ TEST_09 |
| Einfach-Vererbung | Hoch | ✓ TEST_10 |
| Virtual/Polymorphie | Hoch | ✓ TEST_11 |
| Slicing | Hoch | ✓ TEST_12 |
| Zuweisung als Ausdruck | Mittel | ✓ TEST_13 |
| Scope/Shadowing | Mittel | ✓ TEST_14 |
| Namensauflösung in Methoden | Hoch | ✓ TEST_15 |
| Eingebaute Funktionen (print_*) | Hoch | ✓ TEST_16 |
| Kommentare (// und /* */) | Mittel | ✓ TEST_17 |
| Fehlerbehandlung (Division/0) | Mittel | ✓ TEST_18 |
| Semantische Fehler | Mittel | ✓ TEST_19 |
| Referenz-Fehler | Mittel | ✓ TEST_20 |
| Vererbungs-Zyklen | Mittel | ✓ TEST_21 |
| main()-Varianten | Mittel | ✓ TEST_22 |
| Derived→Base Zuweisung | Hoch | ✓ TEST_23 |
| REPL | Hoch | ✓ manuell |
