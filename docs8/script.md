## 2.3 Scopes / Symboltabellen

### Scope-Struktur (`Scope.java:13-15`)

 "Scopes sind hierarchisch als verkettete Liste implementiert. Jeder Scope hat einen Pointer auf den Parent und eine HashMap für die Symbole. Das ermöglicht lexikalisches Scoping - innere Scopes können auf äußere zugreifen."

- Zeige: `Scope.java:13-15` - die drei Felder: `name`, `parent`, `symbols`

> hier sehen wir, wie wir einen scope definiert haben, der wird erzeugt mit einem namen und seinem parent

- Zeige: `Scope.java:100-109` - `resolve()`: erst lokal suchen, dann rekursiv im Parent

> bekommt einen namen, schaut dann, ob es den im aktuellen scope kennt und wenn nicht durchsucht es rekursiv den parent bis es das symbol gefunden hat

 "Die SymbolTable verwaltet den globalen Scope und einen Zeiger auf den aktuellen Scope. Mit `enterScope()` pushen wir einen neuen Scope auf den Stack, mit `exitScope()` poppen wir wieder."

- Zeige: `SymbolTable.java:33-46` - `enterScope()`/`exitScope()`
> enter scope bekommt scope und setzt den current scope, exitscope setzt auf den parent vom aktuellen scope

### Zwei-Pass-Architektur (`SymbolTableBuilder.java:59-80`)

> "Das Problem bei C++ sind Forward-Referenzen - ich kann eine Funktion aufrufen bevor sie deklariert wurde. Deshalb verwenden wir einen Zwei-Pass-Algorithmus."

- Zeige: `SymbolTableBuilder.java:59-80` - die `build()`-Methode

> "Im ersten Pass traversieren wir den AST und registrieren nur die Signaturen aller Klassen und Funktionen. Dann lösen wir die Basisklassen auf. Im zweiten Pass verarbeiten wir die Inhalte: Felder, Methodenkörper, lokale Variablen."

- Zeige: `SymbolTableBuilder.java:98-156` - `visitClassDecl()` mit `if (firstPass)` Unterscheidung

> hier wird die firstPass Variable von weiter oben benutzt um zu unterscheiden, ob die klassen erst registriert werden oder ob die inhalte jetzt verarbeitet werden

---

--- 
--- 

## 2.4 Semantische Checks
Passieren im semanticAnalyzer, der macht auf dem AST semantische Checks auf zb typen oder function overload

### Typprüfung (`SemanticAnalyzer.java:724-740`)

> "Die zentrale Methode für Typkompatibilität ist `typesMatch()`. Die prüft erst auf exakte Gleichheit, dann auf Subtyp-Beziehung bei Klassen - ein Derived kann einem Base zugewiesen werden."

- Zeige: `SemanticAnalyzer.java:724-740` - `typesMatch()` mit Vererbungs-Check

>  Wenn beide klassen sind,m wird geschaut, ob diese voneinander erben, damit z.b. ein Derived-Objekt einer Base-Variable zugewiesen werden kann

----

> "Bei binären Operatoren prüfen wir die Operanden-Typen: Arithmetik erfordert `int`, Vergleiche erlauben `int` oder `char`, logische Operatoren brauchen `bool`."

- Zeige: `SemanticAnalyzer.java:312-377` - `visitBinaryExpr()` mit switch über Operatoren

### LValue-Prüfung (`SemanticAnalyzer.java:751-760`)

> "Ein LValue ist ein Ausdruck, der eine Speicheradresse bezeichnet - dem man also zuweisen kann. Bei uns sind das nur Variablen-Referenzen und Feld-Zugriffe, keine Literale oder Funktionsaufrufe."

- Zeige: `SemanticAnalyzer.java:751-760` - `isLValue()`: nur `VarExpr` und `MemberAccessExpr` (ohne Methodenaufruf)

> "Das ist wichtig für Zuweisungen und besonders für Referenz-Parameter. Wenn eine Funktion `void f(int& x)` erwartet, muss das Argument ein LValue sein - `f(5)` wäre ein Fehler."

- Zeige: `SemanticAnalyzer.java:206-209` - Check bei Referenz-Initialisierung

### Überladungsauflösung (`SemanticAnalyzer.java:959-1054`)

> "Funktionsüberladung erlaubt mehrere Funktionen mit gleichem Namen aber unterschiedlicher Signatur. Die Überladungen werden als Liste im Scope gespeichert. Bei gleicher Signatur werden Fehler geworfen"

- Zeige: `Scope.java:40-57` - spezielle Behandlung von `FunctionSymbol` in `define()`

Überladungsauflösung (code zu lang):
> bei einem funktionsaufruf muss dann die passende Überladung gefunden werden. dafür haben wir im SemanticAnalyzer eine Funktion "`findBestFunctionOverload()`, die sammelt alle 'viable candidates', also Überladungen deren Parametertypen zu den Argumenten passen. Da wird dann auf Anzahl, argument und parametertyp und referenz gecheckt. Bei genau einem Kandidaten ist es eindeutig. Bei mehreren prüfen wir auf Ambiguität. das ist zb, wenn ich die funktionen f(int) und f(int&) habeund f(x) mit einem lvalue aufrufe -> fehler"


### Vererbungsprüfung (`SemanticAnalyzer.java:762-771`)

> "Wir prüfen auf zyklische Vererbung: Eine Klasse darf nicht direkt oder indirekt von sich selbst erben. Dafür traversieren wir die Vererbungskette nach oben (basisklasse zu basisklasse) und prüfen, ob wir irgendwann wieder bei der ausgangsklasse landen, zb A extends B extends C extends A." Bei Zyklus -> Fehler

- Zeige: `SemanticAnalyzer.java:762-771` - `hasInheritanceCycle()` mit while-Schleife

---
---
---
---
## 2.5 Interpreter

### Tree-Walking Struktur (`Interpreter.java:16`)

> "Der Interpreter ist ein klassischer Tree-Walking-Interpreter. Er implementiert das Visitor-Pattern über `ASTVisitor<Value>` - für jeden AST-Knoten gibt es eine visit-Methode die einen Runtime-Value zurückgibt."

- Zeige: `Interpreter.java:58-92` - `executeFunction()`: neue Environment, Parameter binden, Body ausführen

> Als Beispiel: executeFunction: Wenn eine Funktion aufgerufen wird, erstellen wir eine neue Environment für den Funktions-Scope, binden die Parameter an die übergebenen Argumente, und führen dann den Body aus. Das Return-Statement wirft eine Exception, die wir hier abfangen um den Rückgabewert zu bekommen


### Dynamischer Dispatch (`Interpreter.java:536-546`)

> "Ein wichtiges Feature ist der dynamische Dispatch bei virtuellen Methoden - das ist ja der Kern von Polymorphie in C++.

>Das Problem: Wenn ich eine Variable vom Typ Base habe, die aber ein Derived-Objekt enthält, und ich base.method() aufrufe - welche Methode wird ausgeführt? Die von Base oder die überschriebene von Derived?

>Bei virtuellen Methoden muss es die von Derived sein. Wir entscheiden das zur Laufzeit anhand des tatsächlichen Objekttyps, nicht zur Compile-Zeit anhand des deklarierten Typs.


- Zeige: `Interpreter.java:517-546` - `visitMemberAccessExpr()` für Methodenaufrufe

> "Wir holen den statischen Typ aus der Deklaration und prüfen mit `isMethodVirtual()` ob die Methode irgendwo in der Vererbungskette als virtual markiert ist. Falls ja, verwenden wir den Laufzeit-Typ des Objekts - `obj.getClassSymbol()` - um die tatsächlich aufzurufende Methode zu finden."

- Zeige: `Interpreter.java:903-917` - `isMethodVirtual()` traversiert Vererbungskette
- Zeige: `Interpreter.java:922-941` - `findMatchingMethod()` sucht Signatur im Runtime-Typ

### Referenz-Semantik (`Value.java:22-27`)

> "Referenzen sind als Wrapper um andere Values implementiert. Die `Value`-Klasse hat dafür ein `isReference`-Flag und einen Zeiger auf den referenzierten Value."

> Value hat Methoden wie getData, setData und assign und diese Folgen dann wenn nötig der referenz

- Zeige: `Value.java:22-27` - Referenz-Konstruktor
- Zeige: `Value.java:43-66` - `getData()`/`setData()`/`assign()` folgen der Referenz

> "Aliasing haben wir auch, das passiert beim Parameter-Binding: Für Referenz-Parameter erstellen wir `new Value(arg)` - einen Alias. Für normale Parameter kopieren wir den Wert."

- Zeige: `Interpreter.java:66-76` - Parameter-Binding mit `if (param.isReference())`

### Objekt-Repräsentation (`ObjectValue.java:11-18`)

> "Objekte werden bei uns als `ObjectValue` gespeichert: Ein `ClassSymbol` für die Typinformation und eine HashMap von Feldnamen zu Values. Das ClassSymbol ist wichtig für den dynamischen Dispatch."

- Zeige: `ObjectValue.java:11-18` - Felder `classSymbol` und `fields`

> "Beim Object-Slicing, also der Zuweisung eines Derived an eine Base-Variable - kopieren wir nur die Felder der Zielklasse und setzen das ClassSymbol auf die Basisklasse."

- Zeige: `Interpreter.java:725-752` - `copyValueWithSlicing()` erstellt neues ObjectValue mit Ziel-ClassSymbol

---

## 2.6 REPL


### Mehrzeilige Eingaben (`Main.java:172-176`)

> "Die REPL zeigt `>>>` als Primary-Prompt. Bei unvollständigen Eingaben - offene Klammern, fehlendes Semikolon - wechselt sie zum Secondary-Prompt `...` für Fortsetzungszeilen."

> Die Klammern werden mit Hilfe der isInputComplete Methode gezählt, wobei String Literale und Kommentare berücksichtigt werden. 

- Zeige: `Main.java:172-176` - Prompt-Logik
- Zeige: `Main.java:220-304` - `isInputComplete()`: Klammer-Zählung unter Berücksichtigung von String-Literalen und Kommentaren

### Session-Persistenz (`Main.java:76-78`)

>"SymbolTable und Interpreter bleiben über die gesamte Session erhalten. Beim Starten ohne File wird eine neue Symboltabelle erstellt und diese an den interpreter übergeben um mit einem leeren environment zu starten  Neue Definitionen werden inkrementell hinzugefügt - der `SymbolTableBuilder` bekommt die existierende SymbolTable." 

- Zeige: `Main.java:76-78` - Initialisierung bei REPL-Start
- Zeige: `Main.java:354-356` - Inkrementelles Symbol-Table-Building

### Dummy-Funktions-Wrapping (`Main.java:314-320`)

> Um Statements in der REPL zu nutzen oder insgesamt nicht für alles eine Funktion oder Klasse zu schreiben benutzen wir eine dummy wrap funktion `void __repl_dummy__() { ... }`. So können wir den normalen Parser verwenden. Nach der Ausführung wird die Dummy-Funktion wieder entfernt."

- Zeige: `Main.java:314-320` - Wrapping-Logik
- Zeige: `Main.java:360-362` - Cleanup der Dummy-Funktion

### Expression-Auto-Print (`Main.java:396-409`)

> "Wenn die Eingabe ein einzelnes Expression-Statement ist, wird das Ergebnis automatisch ausgegeben - wie bei Python. Void-Expressions werden nicht gedruckt."

- Zeige: `Main.java:396-409` - Erkennung und Ausgabe
- Zeige: `Main.java:423-444` - `printValue()` mit typ-spezifischer Formatierung