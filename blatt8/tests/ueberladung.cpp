// =============================================================================
// DEMO: Funktionsueberladung
// =============================================================================
//
// Was wird getestet:
// - Mehrere Funktionen mit gleichem Namen aber unterschiedlichen Parametertypen
// - Mehrere Funktionen mit gleichem Namen aber unterschiedlicher Parameteranzahl
// - Der Compiler waehlt die richtige Funktion basierend auf den Argumenttypen
//
// Erwartete Ausgabe:
//   42        <- verdopple(int) mit 21
//   true      <- verdopple(bool) mit true
//   7         <- addiere(int, int) mit 3, 4
//   6         <- addiere(int, int, int) mit 1, 2, 3
// =============================================================================

// Ueberladung nach Typ
int verdopple(int x) {
    return x * 2;
}

bool verdopple(bool b) {
    return b;
}


// Ueberladung nach Parameteranzahl
int addiere(int a, int b) {
    return a + b;
}

int addiere(int a, int b, int c) {
    return a + b + c;
}

int main() {
    print_string("Ueberladung nach Typ:\n    int verdopple(int x) {\n        return x * 2;\n    }\n\n    bool verdopple(bool b) {\n        return b;\n    }");

    print_string("\nverdopple(21):");
    print_int(verdopple(21));      // 42 - ruft verdopple(int)
    print_string("\nverdopple(true):");
    print_bool(verdopple(true));   // true - ruft verdopple(bool)

    print_string("\n---\n");

    print_string("Ueberladung nach Parameteranzahl:\n    int addiere(int a, int b) {\n        return a + b;\n    }\n\n    int addiere(int a, int b, int c) {\n        return a + b + c;\n    }");
    print_string("\naddiere(3, 4):");
    print_int(addiere(3, 4));      // 7 - ruft addiere(int, int)
    print_string("\naddiere(1, 2, 3):");
    print_int(addiere(1, 2, 3));   // 6 - ruft addiere(int, int, int)

    return 0;
}
