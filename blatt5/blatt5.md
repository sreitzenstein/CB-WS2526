# Blatt 05: Semantische Analyse


### A5.1: Grammatik und Sprache (1P)

Analysieren Sie die obige ANTLR-Grammatik. Welche Sprache wird durch
diese Grammatik akzeptiert/generiert?

* Datentypen: int, string, bool
* Variablen: Deklaration mit optionaler Initialisierung
* Funktionen: Definition mit Parametern und Rückgabewerten
* Kontrollstrukturen: if-else, while-Schleifen
* Operatoren: Arithmetik (+, -, *, /), Vergleich (>, <, ==, !=)
* Literale: Zahlen, Strings, Booleans (T, F)
* Kommentare: Mit #

Beispiele in der Sprache

### 1. Syntaktisch korrekte Beispiele

#### Beispiel 1: Einfaches Programm mit Variablen
```c
# Einfache Variablendeklaration und Zuweisung
int x = 5;
int y = 10;
int sum;
sum = x + y;
```

#### Beispiel 2: Funktion mit Arithmetik
```c
int add(int a, int b) {
    int result = a + b;
    return result;
}

int main() {
    int x = 5;
    int y = 3;
    int z = add(x, y);
    return z;
}
```

#### Beispiel 3: Kontrollstrukturen
```c
int factorial(int n) {
    int result = 1;
    int i = 1;
    
    while (i < n + 1) {
        result = result * i;
        i = i + 1;
    }
    
    return result;
}
```

#### Beispiel 4: If-Else mit Vergleichen
```c
int max(int a, int b) {
    if (a > b) {
        return a;
    } else {
        return b;
    }
}

int test() {
    int x = 10;
    int y = 20;
    int bigger = max(x, y);
    return bigger;
}
```

#### Beispiel 5: String-Operationen und Bool
```c
string greet(string name) {
    string hello = "Hello, ";
    string message = hello + name;
    return message;
}

bool isEqual(int a, int b) {
    bool result;
    
    if (a == b) {
        result = T;
    } else {
        result = F;
    }
    
    return result;
}
```

#### Beispiel 6: Verschachtelte Scopes
```c
int complexFunction(int x) {
    int outer = 10;
    
    {
        int inner = 20;
        x = x + inner;
        
        {
            int deepInner = 5;
            x = x + deepInner + outer;
        }
        
        x = x + inner;
    }
    
    return x;
}
```

#### Beispiel 7: Rekursion
```c
int fibonacci(int n) {
    if (n < 2) {
        return n;
    } else {
        int a = fibonacci(n - 1);
        int b = fibonacci(n - 2);
        return a + b;
    }
}
```

---

### 2. Syntaktisch inkorrekte Beispiele

#### Fehler 1: Fehlendes Semikolon
```c
int x = 5  # FEHLER: Semikolon fehlt
int y = 10;
```

#### Fehler 2: Ungültige Parameterdeklaration
```c
int add(a, b) {  # FEHLER: Typen fehlen bei Parametern
    return a + b;
}
```

#### Fehler 3: Falsche Klammerung
```c
int test() {
    if (x > 5  # FEHLER: Schließende Klammer fehlt
        return x;
    }
}
```

#### Fehler 4: Ungültiger Identifier
```c
int 123abc = 5;  # FEHLER: Identifier darf nicht mit Zahl beginnen
```

#### Fehler 5: Fehlende geschweifte Klammern
```c
int test()  # FEHLER: Block fehlt
    return 5;
```

#### Fehler 6: Ungültiges Literal
```c
string s = 'single quotes';  # FEHLER: Nur " erlaubt für Strings
```

---

### 3. Semantisch inkorrekte Beispiele

#### Fehler 1: Typfehler bei Operatoren
```c
int add() {
    int x = 5;
    string y = "10";
    return x + y;  # FEHLER: int + string nicht erlaubt
}
```

#### Fehler 2: Variable nicht deklariert
```c
int test() {
    x = 5;  # FEHLER: x wurde nicht deklariert
    return x;
}
```

#### Fehler 3: Mehrfache Deklaration im selben Scope
```c
int duplicate() {
    int x = 5;
    int x = 10;  # FEHLER: x bereits im Scope definiert
    return x;
}
```

#### Fehler 4: Falsche Parameteranzahl
```c
int add(int a, int b) {
    return a + b;
}

int test() {
    return add(5);  # FEHLER: add benötigt 2 Parameter, nur 1 gegeben
}
```

#### Fehler 5: Rückgabetyp passt nicht
```c
int getNumber() {
    return "text";  # FEHLER: Funktion gibt int zurück, aber string geliefert
}
```

#### Fehler 6: Division/Multiplikation mit String
```c
int calculate() {
    string x = "5";
    return x * 2;  # FEHLER: * nur für int erlaubt
}
```

#### Fehler 7: Variable als Funktion aufrufen
```c
int test() {
    int x = 5;
    return x();  # FEHLER: x ist Variable, keine Funktion
}
```

#### Fehler 8: Zugriff außerhalb des Scopes
```c
int outer() {
    {
        int inner = 5;
    }
    return inner;  # FEHLER: inner nicht im aktuellen Scope sichtbar
}
```

#### Fehler 9: Bool-Werte in Arithmetik
```c
int wrong() {
    bool a = T;
    bool b = F;
    return a + b;  # FEHLER: + nicht für bool-Typen definiert
}
```

#### Fehler 10: Vergleich mit falschem Typ
```c
bool compare() {
    bool x = T;
    bool y = F;
    return x > y;  # FEHLER: > nur für int und string erlaubt
}
```

[Lexer/Parser](blatt5/LexerParser)

### A5.1: AST (2P)

Stmt = VarDecl(type: PrimType, name: string, initializer?: Expr)
     | Assign(name: string, value: Expr)
     | FnDecl(returnType: PrimType, name: string, params: Param*, body: Block)
     | ReturnStmt(value: Expr)
     | ExprStmt(expr: Expr)
     | Block(statements: Stmt*)
     | WhileStmt(condition: Expr, body: Block)
     | IfStmt(condition: Expr, thenBranch: Block, elseBranch: Block)

Expr = IntLiteral(value: int)
     | StringLiteral(value: string)
     | BoolLiteral(value: bool)
     | Variable(name: string)
     | Binary(left: Expr, op: Operator, right: Expr)
     | Call(name: string, args: Expr*)

Programmierung: [Main.java](LexerParser/Main.java)

