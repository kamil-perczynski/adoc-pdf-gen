lexer grammar AsciidocLexer;

@header {
    package io.github.kamilperczynski.adocparser;

    import io.github.kamilperczynski.adocparser.BlockParsingCtx;
}

@members {
  public static final BlockParsingCtx blockParsing = BlockParsingCtx.INSTANCE;
}

ESCAPED_CHAR : '\\' .;
DOUBLE_SLASH : '//';
HEADER_START : '='+ ' ';

BLOCK_START : ('===' '='+ EOL | '---' '-'+ EOL | '___' '_'+ EOL) -> pushMode(M_BLOCK);

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
PARAM_CONTENT : ~[\r\t\n\]]+;
PARAM_END: ']' -> popMode;

mode M_TABLE;
TABLE_CELL_START         : '|' WS;
T_INTER: ~[a-zA-Z0-9 \r\n\][*]+;
T_WORD : [a-zA-Z0-9]+ (INTER [a-zA-Z0-9]+)*;

T_WS : [ \t]+;
T_EOL : '\r'? '\n';
TABLE_END            : '|===' '\r'? '\n' -> popMode;

mode M_BLOCK;
// {blockParsing.blockEnd(BLOCK_START, _input, getText())}?
BLOCK_END: BLOCK_START  -> popMode;
BLOCK_CONTENT: ~[\r\t\n]* EOL;
