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

PAGE_BREAK : '<<<';

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
M_PARAM_COMMA: ',';
M_PARAM_QUOTE: ["'];
M_PARAM_EQ: '=';
M_PARAM_NAME: [#\p{L}\p{N}_] [\p{L}\p{N}_]*;
M_PARAM_WS : [ \t]+;
M_PARAM_VALUE : ~[\r\n ,"'\][=]+;
PARAM_END: ']' -> popMode;

mode M_TABLE;
TABLE_CELL_START: ([0-9]* '.'? [0-9]+ '+')? [^><]? '.'? [^><]? [adehlms]? '|';

T_WS : [ \t]+;
T_EOL : '\r'? '\n';
TABLE_END : '|===' '\r'? '\n' -> popMode;
T_INTER: ~[\p{L}\p{N} \r\n]+;
T_WORD : [\p{L}\p{N}]+;

mode M_BLOCK;
// {blockParsing.blockEnd(BLOCK_START, _input, getText())}?
BLOCK_END: BLOCK_START  -> popMode;
BLOCK_CONTENT: ~[\r\t\n]* EOL;
