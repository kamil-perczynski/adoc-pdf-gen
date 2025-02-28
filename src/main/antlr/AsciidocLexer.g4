lexer grammar AsciidocLexer;

@header {
    package io.github.kamilperczynski.adocparser;
}



NEW_LINE: '\n';
LINE_BREAK      : '+' ;

ATTRIBUTE_IDENTIFIER: ':' [a-zA-Z-]+ ':' -> pushMode(M_ATTRIBUTE);


WS              : [ \t\r]+ -> skip ;
COMMENT         : '//' .*? -> skip ;
BOOKMARK: '.' [a-zA-Z] .*? NEW_LINE;
BOLD: '**';
UNDERLINE: '__';
ASTERISK: '*'+ ' ' | '.'+ ' ';
ID_TOKEN: '[[' -> pushMode(M_ID);

HORIZONTAL_RULE: ('---' '-'+ NEW_LINE | '===' '='+ NEW_LINE )-> pushMode(M_BLOCK);

DOT_RULE: '....' '.'* NEW_LINE;
DOT : '.';
HEADER: ('='| '==' | '===' | '====' | '=====' | '======') ' ';

// [cols="1,1"]
PARAMS: '[' -> pushMode(M_PARAMS);

TABLE_SEPARATOR: '|===';
UP: '|' -> pushMode(M_TABLE);

WORD           : (~[\t\r\n*_=[.| ] (~[ \t\r\n:]* | ~[ \t\r\n]* ':' ~'[') );
MODIFIED_WORDS: '*' WORDS_MATERIAL+ '*' | '_' WORDS_MATERIAL+ '_';
MACRO : [a-zA-Z]+ ':[' -> pushMode(M_PARAMS);
fragment WORDS_MATERIAL : ~[\t\r\n];


mode M_PARAMS;
M_PARAMS_WS              : [ \t\r]+ -> skip ;
IDENTIFIER      : [a-zA-Z#] [a-zA-Z0-9_]*;
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

mode M_ATTRIBUTE;
ATTRIBUTE_VALUE: ~[:] ~[\r\t\n]* -> popMode;

mode M_BLOCK;
BLOCK_CONTENT: ~[=-] ~[\r\t\n]* NEW_LINE;
BLOCK_END: HORIZONTAL_RULE -> popMode;
