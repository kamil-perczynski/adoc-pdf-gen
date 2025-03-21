package io.github.kamilperczynski.adocparser

import io.github.kamilperczynski.adocparser.ast.*
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class AdocParser(adoc: String) {

    private val parser = createAdocAntlrParser(adoc)

    fun parseAdocAst(): AdocAST {
        val ast = AdocAST()

        for (child in parser.doc().section()) {
            val admonitionSectionType = if (child.ADMONITION_LINE().isNotEmpty())
                toAdmonitionType(child.ADMONITION_LINE().first().text.trim())
            else null


            if (child.section_title().isNotEmpty()) {
                val chunks = AdocTextChunker()
                    .parseLine { child.section_title().last().children.drop(1) }
                    .chunks

                ast.push(AdocSectionTitle(chunks))
            }

            if (child.admonition() != null) {
                val admonition = child.admonition()
                val paragraph = toAdocParagraph(admonition.paragraph_line())
                val admonitionType = toAdmonitionTypeInlineToken(admonition.ADMONITION_INLINE().text.trim())

                ast.push(AdocAdmonition(admonitionType, paragraph))
            }
            else if (child.paragraph_line().isNotEmpty()) {
                val paragraph = toAdocParagraph(child.paragraph_line())

                if (admonitionSectionType != null) {
                    ast.push(AdocAdmonition(admonitionSectionType, paragraph))
                } else {
                    ast.push(paragraph)
                }
            } else if (child.header() != null) {
                AdocHeaderParser(ast).parse(child.header())
            } else if (child.table() != null) {
                val table = child.table()
                AdocTableParser(ast).parse(table)
            } else if (child.block() != null) {
                val blockParser = AdocBlockParser(ast)

                if (admonitionSectionType == null) {
                    blockParser.parse(child.block())
                }
                else {
                    blockParser.parseAdmonitionBlock(child.block(), admonitionSectionType)
                }

            } else if (child.list_item().isNotEmpty()) {
                val listItems = mutableListOf<AdocListItem>()

                for (listItem in child.list_item()) {
                    val level = listItem.children.first().text.length

                    val paragraph = AdocTextChunker()
                        .parseLine { listItem.paragraph_line().children }
                        .finishParagraph()

                    listItems.add(AdocListItem(level, paragraph))
                }

                ast.push(AdocList(listItems))
            }
        }

        return ast
    }
}

internal fun toAdocParagraph(paragraphLines: List<AsciidocParser.Paragraph_lineContext>): AdocParagraph {
    val paragraphParser = AdocTextChunker()

    for (paragraphLine in paragraphLines) {
        paragraphParser.parseLine { paragraphLine.children }
    }

    val paragraph = paragraphParser.finishParagraph()
    return paragraph
}

internal fun createAdocAntlrParser(adoc: String): AsciidocParser {
    val asciidocLexer = AsciidocLexer(CharStreams.fromString(adoc))

    return AsciidocParser(CommonTokenStream(asciidocLexer))
}

fun toAdmonitionType(type: String): AdmonitionType {
    return when (type) {
        "[NOTE]" -> NOTE
        "[TIP]" -> TIP
        "[IMPORTANT]" -> IMPORTANT
        "[WARNING]" -> WARNING
        "[CAUTION]" -> CAUTION
        "[DANGER]" -> DANGER
        "[ERROR]" -> ERROR
        else -> throw IllegalArgumentException("Unknown admonition type: $type")
    }
}
fun toAdmonitionTypeInlineToken(type: String): AdmonitionType {
    return when (type) {
        "NOTE:" -> NOTE
        "TIP:" -> TIP
        "IMPORTANT:" -> IMPORTANT
        "WARNING:" -> WARNING
        "CAUTION:" -> CAUTION
        "DANGER:" -> DANGER
        "ERROR:" -> ERROR
        else -> throw IllegalArgumentException("Unknown admonition type: $type")
    }
}
