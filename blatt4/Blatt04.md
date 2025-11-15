## Aufgaben

### A4.1: Kontextfreie Grammatik (1P)

```
G = ({S, A}, {1,2,3}, P, S) mit 
P={
    (S -> 1 A S | 3),
    (A -> 2 A S | epsilon)
}
```

#### First(x):(die möglichen 1. Terminale für startwert "x")

First(S) = {1, 3} 

First(A) = {2, epsilon}

---
#### Follow(x): (welche Terminale können direkt hinter x stehen)
(Bsp: X -> A B C -> Follow(B) enhält alles aus First(C) ohne epsilon. Wenn C zu epsilon ableitbar ist, oder nach B nichts mehr kommt, enthält Follow(B) alles aus Follow(X))

S ist Startsymbol -> Startsymbol hat immer EOF in Followmenge

Produktion S -> 1AS -> hinter S kommt nichts, Follow(S) = {EOF}

First(A):

S -> 1AS  -> hinter A kommt S -> alles aus First(S) ohne epsilon gehört zu Follow(A) = {1,3}

A -> 2 A S -> wieder S nach A, es bleibt unverändert

A -> epsilon -> wenn A "verschwindet" folgt wieder ein S in allen Produktionen mit A, es bleibt also Follow(A) = {1,3}

### LL(1) Nachweis

#### Nichtterminal S

S -> 1AS -> First = {1}

S -> 3 -> First = {3}

-> Disjunkt -> LL(1)

---
#### Nichtterminal A

S -> 2AS -> First = {2}

A -> epsilon -> First = {epsilon}

epsilon in First Menge: die First Menge von A (!= epsilon) muss disjunkt mit der Follow Menge von A sein

First(2AS) = {2} != {1,3} = Follow(A)

-> Disjunkt, LL(1)


### A4.2: Grammatik (2P)

1.  [Programme](programme.md)

    Es gibt hier Syntaktische (fehlende Zeichen, falsche Struktur, ungültige Zeichen...) und semantische Fehler (Falsche Typen, nicht definierte Funktionen...)

2.  [Grammatik](grammatik.txt).

### A4.3: Lexer (2P)

[Lexer](src/Lexer/Lexer.java)

### A4.4: Parser mit *recursive descent* (3P)

[Parser](src/Parser/Parser.java)

### A4.5: AST (1P)

* Programm - Sequenz von Expressions
* Allgemeine Ausdrücke - ListExpression (funktionsaufrufe, spezielle definitionen)
* Literale - Id(wert), Number(wert), String(Wert)
* Definitionen - let, defn

### A4.6: Recherche und Diskussion (1P)

* Open Source Projekte
* * Go (seit kurzem, aus Performance und Qualitätsgründen, Code besser les- und wartbar)
* * C# Compiler Roslyn (Präzisere Fehlermeldungen möglich, schnelles Re-Parsing)

ANTLR?

| Pro                                                                     |                                              Con |
|:------------------------------------------------------------------------|-------------------------------------------------:|
| Man muss sich nur um die Grammatik kümmern, der Rest wird einem gegeben | "schlechtere" Fehlerinformationen, bzw generisch |
| Dadurch schnelles Vorankommen                                           |               Unübersichtlicher generierter code |
| unterstützt z.B. auch Linksrekursion                                    |                                                  |

Ich persönlich würde wenns geht aber immer ANTLR nehmen, da ich viel zu Faul bin, mich um das alles selber zu kümmern...
