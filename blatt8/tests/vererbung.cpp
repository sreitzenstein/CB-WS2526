// Einfachvererbung: Tier -> Hund

class Tier {
public:
    int alter;

    void setAlter(int a) {
        alter = a;
    }

    int getAlter() {
        return alter;
    }

    void beschreibung() {
        print_string("Ich bin ein Tier.");
    }
};

class Hund : public Tier {
public:
    string name;

    void setName(string n) {
        name = n;
    }

    string getName() {
        return name;
    }

    void bellen() {
        print_string("Wuff!");
    }
};

int main() {
    print_string("Hund erbt von Tier; Tier hat alter, Hund hat name");
    print_string("Hund definieren: Hund h");
    Hund h;
    print_string("\nHund setzen: h.setName(\"Bello\")");
    h.setName("Bello");
    print_string("\nHund setzen: h.setAlter(3)");
    h.setAlter(3);
    print_string("\nHund ausgeben: h.getName(), h.getAlter(), h.beschreibung(), h.bellen()");

    print_string(h.getName());
    print_int(h.getAlter());
    h.beschreibung();
    h.bellen();

    return 0;
}
