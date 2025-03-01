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
BULLET_ITEM: (ASTERISK+ ' ' | '.'+ ' ') {_tokenStartCharPositionInLine == 0}?;
ID_TOKEN: '[[' -> pushMode(M_ID);

HORIZONTAL_RULE: ('---' '-'+ NEW_LINE | '===' '='+ NEW_LINE )-> pushMode(M_BLOCK);

DOT_RULE: '....' '.'* NEW_LINE;
DOT : '.';
ACUTE : '`';
HEADER: ( '======' | '=====' | '====' | '===' | '==' | '=') {_tokenStartCharPositionInLine == 0}?;

// [cols="1,1"]
PARAMS: '[' -> pushMode(M_PARAMS);

TABLE_SEPARATOR: '|===';
UP: '|' -> pushMode(M_TABLE);

MACRO : [a-zA-Z]+ (('::' [a-zA-Z0-9/._]+) | ':') '[' -> pushMode(M_PARAMS);

ASTERISK: '*';
UNDERSCORE: '_';
COLON: ':';

WORD: [a-zA-Z0-9!@#$%^&()+{},./<>?="';-]
 | [a-zA-Z0-9!@#$%^&()+{},./<>?="';-] [a-zA-Z0-9!@#$%^&*()+{},./<>?="':-]
 | [a-zA-Z0-9!@#$%^&()+{},./<>?="';-] [a-zA-Z0-9!@#$%^&*()+{},./<>?="':_-]+ [a-zA-Z0-9!@#$%^&()+{},/<>?="':-]
;
LINK :  [a-z] ':' WORD ('[' WORD ']')?;

fragment WORDS_MATERIAL : [a-zA-Z0-9!@#$%^&()+{},./<>?="';-];


mode M_PARAMS;
M_PARAMS_WS              : [ \t\r]+ -> skip ;
IDENTIFIER      : [a-zA-Z#] [a-zA-Z0-9_. -]*;
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
