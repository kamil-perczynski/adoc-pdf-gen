lexer grammar AsciidocLexer;

@header {
    package io.github.kamilperczynski.adocparser;

    import io.github.kamilperczynski.adocparser.BlockParsingCtx;
}

@members {
  public static final BlockParsingCtx blockParsing = BlockParsingCtx.INSTANCE;
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

HORIZONTAL_RULE: BLOCK_LINE {blockParsing.blockStart(HORIZONTAL_RULE, _input, getText())}? -> pushMode(M_BLOCK);

DOT_RULE: '....' '.'* NEW_LINE;
ACUTE : '`';
HEADER: ( '======' | '=====' | '====' | '===' | '==' | '=') {_tokenStartCharPositionInLine == 0}?;

PARAMS: '[' -> pushMode(M_PARAMS);

TABLE_SEPARATOR: '|===';
UP: '|' -> pushMode(M_TABLE);

MACRO : [a-zA-Z]+ (('::' [a-zA-Z0-9/._]+) | ':') '[' -> pushMode(M_PARAMS);

ASTERISK: '*';
UNDERSCORE: '_';

WORD: SIMPLE_WORDS WORD_END? | WORD_START | WORD_START WORD_END | WORD_START [a-zA-Z0-9!@#$%^&*()+{}./<>?="':_-]+ WORD_END;
SIMPLE_WORDS : ([a-zA-Z]+ ([ \\t] [a-zA-Z]+)?)+;
DOT : '.';

fragment WORD_START : [a-zA-Z0-9!@#$%^&()+{},./<>?="';-];
fragment WORD_END : [a-zA-Z0-9!@#$%^&()+{},./<>?="';:-];
fragment BLOCK_LINE: '---' '-'+ NEW_LINE | '===' '='+ NEW_LINE;

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
BLOCK_END: BLOCK_LINE {blockParsing.blockEnd(HORIZONTAL_RULE, _input, getText())}? -> popMode;
BLOCK_CONTENT: ~[\r\t\n]* NEW_LINE;
