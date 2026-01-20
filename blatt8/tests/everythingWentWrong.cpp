// Datei voller semantischer Fehler - zum Zeigen der Fehlermeldungen
// Kommentiere einzelne Fehler ein/aus um sie zu demonstrieren

class Tier {
public:
    int alter;
};

class Hund : public Tier {
public:
    string name;
};

int addiere(int a, int b) {
    return a + b;
}

int gibZahl() {
    return 42;
}

int main() {
    // === TYPFEHLER ===

    // Fehler: int erwartet, string gegeben
    int a = "hallo";

    // Fehler: Typen nicht kompatibel bei Vergleich
    bool b = 5 < "text";

    // Fehler: Typen nicht kompatibel bei Arithmetik
    int c = 10 + true;


    // === SCOPE-FEHLER ===

    // Fehler: Variable nicht deklariert
    int d = nichtDefiniert;

    // Fehler: Funktion nicht deklariert
    int e = gibtEsNicht();


    // === FUNKTIONSAUFRUF-FEHLER ===

    // Fehler: Falsche Anzahl Argumente
    int f = addiere(1);

    // Fehler: Falscher Argumenttyp
    int g = addiere("eins", "zwei");


    // === LVALUE-FEHLER ===

    // Fehler: Kann RValue nicht zuweisen
    5 = 10;

    // Fehler: Kann Funktionsergebnis nicht zuweisen
    gibZahl() = 100;


    // === REFERENZ-FEHLER ===

    // Fehler: Referenz auf RValue
    int& ref = 42;

    // Fehler: Referenz auf temporaeren Wert
    int& ref2 = 1 + 2;


    // === KLASSEN-FEHLER ===

    // Fehler: Feld existiert nicht
    Hund h;
    int x = h.gewicht;

    // Fehler: Methode existiert nicht
    h.bellen();

    // Fehler: Feldzugriff auf Nicht-Objekt
    int zahl = 42;
    int y = zahl.feld;


    return 0;
}

// Fehler: Return-Typ stimmt nicht
int falscherReturn() {
    return "string statt int";
}
