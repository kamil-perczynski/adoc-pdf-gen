parser grammar AsciidocParser;

@header {
package io.github.kamilperczynski.adocparser;
}

options {
    tokenVocab = AsciidocLexer;
}

document: section (NEW_LINE* section)* NEW_LINE* EOF;

list : list_item (NEW_LINE list_item)*;
list_item : ASTERISK rich_text;

section: id? (attribute+ | BOOKMARK | params+)* (header | params | id | paragraph | list | table | block);

block: HORIZONTAL_RULE BLOCK_CONTENT+ BLOCK_END;

attribute: ATTRIBUTE_IDENTIFIER ATTRIBUTE_VALUE?;

table: TABLE_SEPARATOR NEW_LINE+ (table_row NEW_LINE*)+ TABLE_SEPARATOR NEW_LINE;

params: PARAMS param (COMMA param)* END_PARAMS NEW_LINE;
param: IDENTIFIER (EQ ATTR)?;

table_row : table_col+;
table_col : UP M_TABLE_TEXT UP?;

rich_text: word+;
word: WORD | MODIFIED_WORDS | macro;

macro: MACRO param (COMMA param)* END_PARAMS;

paragraph : rich_text (NEW_LINE rich_text)*;

id: ID_TOKEN ID_TEXT END_ID NEW_LINE;

header: HEADER WORD+;
