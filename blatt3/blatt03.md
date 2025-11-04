# Blatt 03: ANTLR

## Aufgaben

### A3.1: Grammatik (4P)

[grammatik](AufgabeEins.g4)  (Ich hab genau diese genommen und umbenannt für aufgabe 2)

### A3.2: Pretty Printer (3P)

ich hasse java, ich hab mich also dran versucht das mit js zu machen. 
Befehle dafür: 
```bash
cd OrdnerMitDerGrammatik
curl -O https://www.antlr.org/download/antlr-4.13.2-complete.jar #oder man merkt sich, wo man das abgelegt hat 
java -jar antlr-4.13.2-complete.jar -Dlanguage=JavaScript -o js-gen GrammatikName.g4
npm init -y #als node projekt initialisieren
npm install antlr4 #braucht man, also man braucht auch npm haha

#in package-json "type":"module", ergänzen

#main bauen

node prettyparser.js
```

also das war ein wenig auch um festzuhalten, wie ich das wiederhole. die anleitung zum ausführen ist in [prettyparser.js](prettyparser/prettyparser.js)

### example1

```
# Badly formatted nested structures
x:=10 y:=20 if x<y do result:=0 temp:=5 while result<10 do result:=result+1 temp:=temp+1
end final:=result+temp else do result:=100  final:=result*2
end output:=final+50


------------------pretty------------

x := 10
y := 20
if x < y do
    result := 0
    temp := 5
    while result < 10 do
        result := result + 1
        temp := temp + 1
    end
    final := result + temp
    result := 100
    final := result * 2
end
output := final + 50

```

### example2

```
if total>500 do discount:=total*10/100 final:=total-discount

while final>100 do if final!=0 do final:=final-1 end end  else do if total<100 do bonus:=50 else do bonus:=25 end final:=total+bonus end

# String assignment
message:="Calculation complete" status:="OK"

# Nested madness
a:=1  b:=2 c:=3 if a<b do if b<c do  result:=a+b+c  while result>0 do result:=result-1 end  end end



------------------pretty------------

x := 10
y := 20
z := 30
total := x + y * z - 5
if total > 500 do
    discount := total * 10 / 100
    final := total - discount
    while final > 100 do
        if final != 0 do
            final := final - 1
        end
    end
    if total < 100 do
        bonus := 50
        bonus := 25
    end
    final := total + bonus
end
message := "Calculation complete"
status := "OK"
a := 1
b := 2
c := 3
if a < b do
    if b < c do
        result := a + b + c
        while result > 0 do
            result := result - 1
        end
    end
end

```


### A3.3: AST (3P)

Klassen:

|Name|Beschreibung|
|---|---|
|Program| das gesamte Programm bzw. die gesamte eingabe|
|Assignment| Variablenzuweisungen|
|IfStatements| If Bedingungen mit optionalem else|
|WhileLoop|While schleifen|
|BinaryOp| Binäre Operationen wie z.B. a + b|
|Variable, NumberLiteral, StringLiteral| Werte|

Umsetzung in [prettyast.js](prettyparser/prettyast.js)

macht aus dem beispiel [unpretty](prettyparser/unpretty)
folgendes:

```
a     := 0
    if    10 < 1
       do
a    :=     42      # Zuweisung des Wertes 42 an die Variable a
else do
        a :=      7
  end

------------------pretty------------

a := 0
if 10 < 1 do
    a := 42
else do
    a := 7
end

------------------AST tree--------------

└── Program
    ├── Assignment: a
    │   └── Number: 0
    └── IfStatement
        ├── Condition
        │   └── BinaryOp: <
        │       ├── Number: 10
        │       └── Number: 1
        ├── Then
        │   └── Assignment: a
        │       └── Number: 42
        └── Else
            └── Assignment: a
                └── Number: 7
```
