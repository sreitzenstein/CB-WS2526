# Gültig

```
42
"hello"
true
false
foo             ;; Variable / Identifier

```

```
(+ 1 2 3)                    ;; 6
(/ (+ 10 2) (+ 2 4))         ;; (10+2)/(2+4)
(* (- 5 2) (+ 1 1 1))        ;; (5-2) * (1+1+1)

```
```
(print "hello\nworld")
(str "one: " 1 ", two: " 2)
```
```
(list 1 2 3)                 ;; Erzeugt eine Liste (1 2 3)
(nth (list "a" "b" "c") 2)   ;; "c" (wenn nth 0-basiert oder 1-basiert je nach Semantik)
(head (list 1 2 3))          ;; 1
(tail (list 1 2 3))          ;; (2 3)

```
```
(def x 42)

(let (x 1 y 2)
    (+ x y  ;; ergibt 3, z ist hier nicht sichtbar
    )
)

(do
    (print "step 1")
    (print "step 2"))

```

```
(defn fac (n)
    (if (< n 2)
        1
        (* n (fac (- n 1)))))

```
```
(defn length (lst)
    (if (empty? lst)
        0
        (+ 1 (length (tail lst)))))

```
```
(defn print-length (lst)
    (let (n (length lst))
         (print (str "Länge: " n))))

(print-length (list 10 20 30))

```

# Ungültig
```
(+ 1 2        ;; fehlende ')'
```

```
(1 2 3)       ;; Syntax: erster Eintrag einer Liste muss Head (Operator/Keyword/ID) sein,
              ;;       hier wäre '1' als Head kein erlaubtes Head-Symbol
```
```
(print "hello)   ;; unterbrochener String: kein schließendes "

```
```
def x 42        ;; 'def' ist ein Keyword/Head, darf nicht als freier Atom-Ausdruck stehen

```
```
123abc          ;; Identifier darf nicht mit Ziffer beginnen — lexikalischer Fehler

```
```
(defn foo x (print x))   ;; erwartet: (defn foo (params) body) → hier fehlen die Klammern um params

```
```
(+ 1 "a")           ;; syntaktisch ok, semantisch: Addition von Integer und String — Typfehler

```
```
(+ 1)               ;; evtl. semantisch unzulässig, wenn '+' mindestens 2 Operanden verlangt
(if 1 (print "x"))  ;; semantisch falsch: Bedingung ist kein Boolean (Parser erlaubt es, semantische Phase nicht)

```
```
(+ x 1)             ;; x nicht definiert → Laufzeit/Semantikfehler

```

```
(head 42)           ;; syntaktisch ok, semantisch: head erwartet eine Liste

``` 
```
(def 123 456)       ;; syntaktisch ok (Head 'def', dann NUMBER), aber semantisch: linker Parameter muss ID sein

``` 
