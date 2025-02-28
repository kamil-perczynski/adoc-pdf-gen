parser grammar AsciidocParser;

@header {
package io.github.kamilperczynski.adocparser;
}

options {
    tokenVocab = AsciidocLexer;
}

document: line+;

line: header | ATTRIBUTE | params | id | paragraph | table | list | horizontal_rule | DOT_RULE | NEW_LINE;

table: TABLE_SEPARATOR NEW_LINE+ table_row+ TABLE_SEPARATOR;

params: PARAMS param (COMMA param)* END_PARAMS NEW_LINE;
param: IDENTIFIER (EQ ATTR)?;

table_row : table_col+ NEW_LINE+;
table_col : UP M_TABLE_TEXT UP?;

bold_text: BOLD TEXT BOLD;
underline_text: UNDERLINE TEXT UNDERLINE;
word: text | bold_text | underline_text;


paragraph : (word+ NEW_LINE)+;

text: TEXT;

id: ID_TOKEN ID_TEXT END_ID NEW_LINE;

horizontal_rule: HORIZONTAL_RULE;
header: HEADER TEXT NEW_LINE;

list : ASTERISK+ text NEW_LINE;
