lexer grammar AsciidocLexer;

@header {
    package io.github.kamilperczynski.adocparser;

    import io.github.kamilperczynski.adocparser.BlockParsingCtx;
}

@members {
  public static final BlockParsingCtx blockParsing = BlockParsingCtx.INSTANCE;
}

ESCAPED_CHAR : '\\' .;

BLOCK_START : ('===' '='+ EOL | '---' '-'+ EOL) -> pushMode(M_BLOCK);

TABLE_MARK : ('|===' EOL) -> pushMode(M_TABLE);

WORD : [a-zA-Z0-9]+ ((INTER | UNDERSCORE) [a-zA-Z0-9]+)*;

DOT: '.'+;
COLON: ':'+;
ASTERISK: '*'+;
UNDERSCORE: '_'+;
ACUTE: '`'+;

ID_START : '[[';
ID_END : ']]';

PARAM_START : '[' -> pushMode(M_PARAM);

WS : [ \t]+;
EOL : '\r'? '\n';

INTER: ~[a-zA-Z0-9_\\ \r\n:[*]+;

mode M_PARAM;
M_PARAMS_WS              : [ \t\r]+ -> skip ;
IDENTIFIER      : [a-zA-Z#] [a-zA-Z0-9_. -]*;
EQ: '=';
COMMA: ',';
QUOTE: '"' | '\'';
ATTR : QUOTE .*? QUOTE | [0-9a-zA-Z]+;
PARAM_END: ']' -> popMode;

mode M_TABLE;
TABLE_CELL_START         : '|' WS;
T_INTER: ~[a-zA-Z0-9 \r\n\][*]+;
T_WORD : [a-zA-Z0-9]+ (INTER [a-zA-Z0-9]+)*;

T_WS : [ \t]+;
T_EOL : '\r'? '\n';
TABLE_END            : '|===' '\r'? '\n' -> popMode;

mode M_ATTRIBUTE;
ATTRIBUTE_VALUE: ~[:] ~[\r\t\n]* -> popMode;

mode M_BLOCK;
BLOCK_END: BLOCK_START {blockParsing.blockEnd(HORIZONTAL_RULE, _input, getText())}? -> popMode;
BLOCK_CONTENT: ~[\r\t\n]* EOL;
