grammar PrettyParser;


// Parser
program :  stmt* EOF ;


stmt    :  vardecl
        |  expr
        |  while
        |  cond
        ;

vardecl :  ID ':=' expr  ;


while   :  'while'  expr  'do'  stmt* 'end';
cond    :  'if'  expr 'do' stmt* ('else do' stmt*)? 'end' ;

expr    :  expr '*' expr
        |  expr '/' expr
        |  expr '+' expr
        |  expr '-' expr
        |  expr '==' expr
        |  expr '!=' expr
        |  expr '>' expr
        |  expr '<' expr
        |  ID
        |  NUMBER
        |  STRING
        ;



// Lexer
ID      :  [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER  :  [0-9]+ ;
STRING  :  '"' (~[\n\r"])* '"' ;

COMMENT :  '#' ~[\n\r]* -> skip ;
WS      :  [ \t\n\r]+ -> skip ;
