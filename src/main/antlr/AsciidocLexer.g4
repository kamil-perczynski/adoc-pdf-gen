lexer grammar AsciidocLexer;

@header {
    package io.github.kamilperczynski.adocparser;
}



NEW_LINE: '\n';
LINE_BREAK      : '+' ;

ATTRIBUTE      : ':' '!'? (~[\n]+?) '!'? ':' (~[\n]*?) NEW_LINE;


WS              : [ \t\r]+ -> skip ;
COMMENT         : '//' .*? -> skip ;

BOLD: '**';
UNDERLINE: '__';
ASTERISK: '*' | '.';
ID_TOKEN: '[[' -> pushMode(M_ID);

HORIZONTAL_RULE: '----' '-'* NEW_LINE | '====' '='* NEW_LINE;
DOT_RULE: '....' '.'* NEW_LINE;
DOT : '.';
HEADER: '='| '==' | '===' | '====' | '=====' | '======';

// [cols="1,1"]
PARAMS: '[' -> pushMode(M_PARAMS);

TABLE_SEPARATOR: '|===';
UP: '|' -> pushMode(M_TABLE);

TEXT           : ~[\t\r\n*_=[.|] ~[\t\r\n]+ ;

fragment HEXDIGIT: [a-fA-F0-9];
fragment DIGIT: [0-9];


mode M_HEADER;
M_HEADER_WS              : [ \t\r]+ -> skip ;
WORD: ~[\n]+ -> popMode;

mode M_PARAMS;
M_PARAMS_WS              : [ \t\r]+ -> skip ;
IDENTIFIER      : [a-zA-Z#_]+;
EQ: '=';
COMMA: ',';
QUOTE: '"' | '\'';
ATTR : QUOTE .*? QUOTE | [0-9a-zA-Z]+;
END_PARAMS: ']' -> popMode;

fragment ATTCHAR: '{' | '}' | '-' | '_' | '.' | '/' | '+' | ',' | '?' | '=' | ':' | ';' | '#' | [0-9a-zA-Z];

mode M_TABLE;
M_TABLE_TEXT           : ~[\t\r\n|]+ -> popMode;

mode M_ID;
ID_TEXT           : ~[\t\r\n\]]+;
END_ID : ']]' -> popMode;
