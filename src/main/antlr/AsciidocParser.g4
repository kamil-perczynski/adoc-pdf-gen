parser grammar AsciidocParser;

@header {
package io.github.kamilperczynski.adocparser;
}

options {
    tokenVocab = AsciidocLexer;
}

doc: (id | attribute | header | list | section_title | param_line | bookmark | block | paragraph | table | EOL+)+ EOF;

block: BLOCK_START BLOCK_CONTENT+ BLOCK_END;

section:
 | header
 | id
 | bookmark
 | params
 | paragraph
;

table: TABLE_MARK (table_cell+ T_EOL | T_EOL)+ TABLE_END;
table_cell: TABLE_CELL_START (T_WORD | T_WS | T_INTER)+;
macro: WORD COLON WORD? params;

paragraph : (macro | params | link | WORD | WS | DOT | ESCAPED_CHAR | UNDERSCORE | ACUTE | COLON | INTER | ASTERISK)+ EOL;

link: WORD COLON INTER WORD (INTER | (INTER WORD)+ INTER?)? section_title?;

id : ID_START WORD+ ID_END EOL;

section_title: PARAM_START IDENTIFIER PARAM_END;

params : PARAM_START param_item (COMMA param_item)* PARAM_END;
param_item: IDENTIFIER (EQ ATTR)?;

param_line: params EOL;

bookmark : DOT WORD ~(EOL | BLOCK_START)* EOL;

header : INTER WS WORD ~EOL* EOL;
attribute: COLON WORD COLON WS ~EOL* EOL;

list: list_item+;
list_item: (ASTERISK | DOT)+ WS paragraph;
