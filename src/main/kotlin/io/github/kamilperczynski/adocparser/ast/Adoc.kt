package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser


fun parseAsciiDocAST(parser: AsciidocParser): AdocAST {
    val ast = AdocAST()

    for (child in parser.doc().section()) {
        if (child.section_title().isNotEmpty()) {
            val chunks = AdocTextChunker()
                .parseLine { child.section_title().last().children.drop(1) }
                .chunks

            ast.push(AdocSectionTitle(chunks))
        }

        if (child.paragraph_line().isNotEmpty()) {
            val paragraphParser = AdocTextChunker()

            for (paragraphLine in child.paragraph_line()) {
                paragraphParser.parseLine { paragraphLine.children }
            }

            val paragraph = paragraphParser.finishParagraph()
            ast.push(paragraph)
        } else if (child.header() != null) {
            AdocHeaderParser(ast).parse(child.header())
        } else if (child.block() != null) {
            BlockParser(ast).parse(child.block())
        } else if (child.list_item().isNotEmpty()) {
            val listItems = mutableListOf<AdocListItem>()

            for (listItem in child.list_item()) {
                val level = listItem.children.first().text.length

                val paragraph = AdocTextChunker()
                    .parseLine { listItem.paragraph_line().flatMap { it.children } }
                    .finishParagraph()

                listItems.add(AdocListItem(level, paragraph))
            }

            ast.push(AdocList(listItems))
        }
        // TODO: add cases for header, list, id, params, attributes
    }

    return ast
}

