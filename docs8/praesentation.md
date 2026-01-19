# Präsentation: C++-Interpreter (15 Minuten)

**Format:** Walk-Through am Code in der IDE, keine Folien!

---

## 1. Spezifikation (~3 Min)

Kurze Sprachbeschreibung mit allen Designentscheidungen:

- [ ] Unterstützte Typen: `bool`, `int`, `char`, `string`, `void`, Klassentypen
- [ ] Variablen und Zuweisungen (nur lokal, keine globalen)
- [ ] Ausdrücke: Arithmetik, Vergleich, Logik, Funktionsaufrufe, Feldzugriff
- [ ] Kontrollfluss: `if`/`else`, `while`, Blöcke, `return`
- [ ] Funktionen mit Überladung (exakter Match)
- [ ] C++-Referenzen (`T& x`)
- [ ] Klassen mit Einfachvererbung und `virtual`-Methoden
- [ ] Getroffene Designentscheidungen erklären (z.B. wie Slicing umgesetzt)

---

## 2. Implementierung (~8 Min)

### 2.1 ANTLR-Grammatik
- [ ] Grammatikdatei zeigen
- [ ] Lexer-Regeln (Tokens, Schlüsselwörter, Kommentare, Präprozessor)
- [ ] Parser-Regeln (Expressions mit Präzedenz, Statements, Klassen)

### 2.2 AST
- [ ] AST-Knotenklassen zeigen
- [ ] Wie wird der AST aus dem Parse-Tree gebaut?
-------------------------------
### 2.3 Scopes / Symboltabellen
- [ ] Scope-Struktur erklären (global, Klassen, Funktionen, Blöcke)
- [ ] Symboltabellen-Implementierung zeigen
- [ ] "define-before-use" vs. "define-after-use" (Mehrpass)

### 2.4 Semantische Checks
- [ ] Typprüfung
- [ ] LValue-Prüfung (Zuweisungen, Referenzen)
- [ ] Überladungsauflösung
- [ ] Vererbungsprüfung (keine Zyklen, Slicing)

### 2.5 Interpreter
- [ ] Tree-Walking Interpreter Struktur
- [ ] Dynamischer Dispatch bei `virtual`-Methoden
- [ ] Referenz-Semantik (Aliasing)
- [ ] Runtime-Werte (wie werden Objekte gespeichert?)

### 2.6 REPL
- [ ] Prompt und Hilfsprompt bei mehrzeiligen Eingaben
- [ ] Sitzungs-Scope (Zugriff auf main-Variablen)
- [ ] Expression-Statements geben Ergebnis aus
- [ ] Beenden der REPL

---

## 3. Anleitung (~2 Min)

- [ ] Wie wird das Projekt gebaut? (Build-Befehl)
- [ ] Wie wird der Interpreter gestartet?
- [ ] Wie lädt man eine Datei?
- [ ] Wie nutzt man die REPL?

---

## 4. Demo (~2 Min)

Live-Demonstration mit C++-Codebeispielen:

- [ ] Einfaches Programm ausführen
- [ ] Funktionen und Überladung zeigen
- [ ] Klassen mit Vererbung
- [ ] Polymorphie mit `virtual` und Referenzen
- [ ] REPL-Interaktion

---

## Checkliste vor der Präsentation

- [ ] IDE vorbereitet mit relevanten Dateien geöffnet
- [ ] Testbeispiele bereit
- [ ] Interpreter läuft fehlerfrei
- [ ] Zeit geprobt (~15 Min)
