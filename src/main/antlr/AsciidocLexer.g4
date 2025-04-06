lexer grammar AsciidocLexer;

@header {
    package io.github.kamilperczynski.adocparser;

    import io.github.kamilperczynski.adocparser.BlockParsingCtx;
}

@members {
  public static final BlockParsingCtx blockParsing = BlockParsingCtx.INSTANCE;
}

BLOCK_START : ('===' '='+ WS? EOL | '---' '-'+ WS? EOL | '___' '_'+ WS? EOL) -> pushMode(M_BLOCK);
ESCAPED_CHAR : '\\' .;
DOUBLE_SLASH : '//';
HEADER_START : '='+ ' ';


TABLE_MARK : ('|===' EOL) -> pushMode(M_TABLE);

WORD : [a-zA-Z0-9]+ ((INTER | UNDERSCORE) [a-zA-Z0-9]+)*;

ADMONITION_LINE: ('[NOTE]' | '[TIP]' | '[IMPORTANT]' | '[CAUTION]' | '[WARNING]') EOL;
ADMONITION_INLINE: ('NOTE: ' | 'TIP: ' | 'IMPORTANT: ' | 'CAUTION: ' | 'WARNING: ');

DOT: '.'+;
COLON: ':'+;
ASTERISK: '*'+;
UNDERSCORE: '_'+;
ACUTE: '`'+;
LIST_START: [0-9]+ DOT | [a-z] DOT | [A-Z] DOT;

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
T_FORMAT_MODE: [adehlms];
T_ALIGNMENT : [^><];
T_COLSPAN : [0-9]* '.'? [0-9]+ '+';
TABLE_CELL_START: '|';


T_WS : [ \t]+;
T_EOL : '\r'? '\n';
TABLE_END : '|===' '\r'? '\n' -> popMode;
T_INTER: ~[a-zA-Z0-9 \r\n^><|]+;
T_WORD : [a-zA-Z0-9]+;

mode M_BLOCK;
// {blockParsing.blockEnd(BLOCK_START, _input, getText())}?
BLOCK_END: BLOCK_START  -> popMode;
BLOCK_CONTENT: ~[\r\t\n]* EOL;
