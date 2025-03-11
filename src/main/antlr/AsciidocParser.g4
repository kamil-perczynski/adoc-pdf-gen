parser grammar AsciidocParser;

@header {
package io.github.kamilperczynski.adocparser;
}

options {
    tokenVocab = AsciidocLexer;
}

doc: section (EOL* section)* EOL* EOF;

block: ADMONITION_LINE? section_title? BLOCK_START BLOCK_CONTENT+ BLOCK_END;
comment: DOUBLE_SLASH ~EOL* EOL;
header : HEADER_START (id | ~EOL)+ EOL;

section:
 (param_line+ | comment+ | section_title+ | ADMONITION_LINE+ | EOL+)*  (list_item+ | id_line | attribute+ | header | block | paragraph_line+ | table | admonition)
;

table: TABLE_MARK table_cell+ TABLE_END;
table_cell: T_COLSPAN? T_ALIGNMENT? T_FORMAT_MODE? TABLE_CELL_START (T_WORD | T_WS | T_ALIGNMENT | T_INTER | T_COLSPAN | T_FORMAT_MODE | T_EOL)* (T_WORD | T_WS | T_INTER | T_EOL);
macro: WORD COLON WORD? params;

list_item: ASTERISK WS paragraph_line | DOT WS paragraph_line | LIST_START WS paragraph_line;

admonition: ADMONITION_INLINE paragraph_line+;

link: WORD COLON DOUBLE_SLASH WORD (INTER | (INTER WORD)+ INTER?)? params?;

id_line : id EOL;
id: ID_START WORD ID_END;

params : PARAM_START PARAM_CONTENT? PARAM_END;

param_line: params EOL;

section_title : DOT ~WS ~(DOT | EOL)* EOL;
attribute: COLON WORD COLON (WS ~EOL*)? EOL;

paragraph_line :
  (macro | link | WORD | WS | ESCAPED_CHAR | DOUBLE_SLASH | UNDERSCORE | ACUTE | INTER | ASTERISK)
  (macro | link | params | WORD | WS | ID_START | ID_END | DOT | HEADER_START | ESCAPED_CHAR | DOUBLE_SLASH | UNDERSCORE | ACUTE | COLON | LIST_START | INTER | ASTERISK)*
  EOL
;
