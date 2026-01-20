// =============================================================================
// DEMO: Polymorphie mit virtual und Referenzen
// =============================================================================
//
// Was wird getestet:
// - virtual-Methoden ermoeglichen dynamischen Dispatch
// - Beim Aufruf ueber eine Basisklassen-Referenz wird die Methode der
//   tatsaechlichen (abgeleiteten) Klasse aufgerufen, nicht die der Basisklasse
//
// Erwartete Ausgabe:
//   Form      <- direkter Aufruf auf Form
//   O         <- direkter Aufruf auf Kreis
//   []        <- direkter Aufruf auf Rechteck
//   ---
//   Form      <- zeigeForm(f): f ist Form, ruft Form::zeichne()
//   O         <- zeigeForm(k): k ist Kreis, ruft Kreis::zeichne() (POLYMORPHIE!)
//   []        <- zeigeForm(r): r ist Rechteck, ruft Rechteck::zeichne() (POLYMORPHIE!)
//
// Ohne "virtual" wuerde zeigeForm() immer Form::zeichne() aufrufen,
// weil der statische Typ des Parameters Form& ist.
// Mit "virtual" wird zur Laufzeit die richtige Methode basierend auf dem
// dynamischen Typ des Objekts ausgewaehlt.
// =============================================================================

class Form {
public:
    virtual void zeichne() {
        print_string("Form");
    }

    void info() {
        print_string("Ich bin eine Form.");
    }
};

class Kreis : public Form {
public:
    virtual void zeichne() {
        print_string("O");
    }
};

class Rechteck : public Form {
public:
    virtual void zeichne() {
        print_string("[]");
    }
};

void zeigeForm(Form& f) {
    f.zeichne();
}

int main() {

    print_string("Formen definieren: Form f, Kreis k, Rechteck r\nKreis und Rechteck erben von Form und ueberschreiben die zeichne() Methode");
    Form f;
    Kreis k;
    Rechteck r;
    
    print_string("\n---\n");

    print_string("f.zeichne():");
    f.zeichne();
    print_string("\nk.zeichne():");
    k.zeichne();
    print_string("\nr.zeichne():");
    r.zeichne();

    print_string("\n---\n");

    print_string("zeigeForm(Form& f) ruft f.zeichne() auf:\n\n");
    print_string("zeigeForm(f):");
    zeigeForm(f);
    print_string("zeigeForm(k):");
    zeigeForm(k);
    print_string("zeigeForm(r):");
    zeigeForm(r);

    return 0;
}
