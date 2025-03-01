parser grammar AsciidocParser;

@header {
package io.github.kamilperczynski.adocparser;
}

options {
    tokenVocab = AsciidocLexer;
}

doc: section (EOL* section)* EOL* EOF;

block: param_line? section_title? BLOCK_START BLOCK_CONTENT+ BLOCK_END;

section:
 id_line | attribute | header | list | block | paragraph | table | param_line
;

table: TABLE_MARK (table_cell+ T_EOL | T_EOL)+ TABLE_END;
table_cell: TABLE_CELL_START (T_WORD | T_WS | T_INTER)+;
macro: WORD COLON WORD? params;

paragraph : (macro | params | link | WORD | WS | DOT | ESCAPED_CHAR | UNDERSCORE | ACUTE | COLON | INTER | ASTERISK)+ EOL;

link: WORD COLON INTER WORD (INTER | (INTER WORD)+ INTER?)? params?;

id_line : id EOL;
id: ID_START WORD+ ID_END;

params : PARAM_START PARAM_CONTENT? PARAM_END;

param_line: params EOL;

section_title : DOT WORD (WS WORD)* EOL;

header : INTER WS (id | ~EOL)+ EOL;
attribute: COLON WORD COLON WS ~EOL* EOL;

list: list_item+;
list_item: (ASTERISK | DOT)+ WS paragraph;
