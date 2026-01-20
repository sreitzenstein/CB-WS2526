// Einfaches Programm: Fakultät berechnen
int fakultaet(int n) {
    if (n <= 1) {
        return 1;
    }
    return n * fakultaet(n - 1);
}

int main() {
    int zahl = 5;
    int ergebnis = fakultaet(zahl);
    print_int(ergebnis);  // 120

    // Einfache Schleife
    int summe = 0;
    int i = 1;
    while (i <= 10) {
        summe = summe + i;
        i = i + 1;
    }
    print_int(summe);  // 55

    return 0;
}
