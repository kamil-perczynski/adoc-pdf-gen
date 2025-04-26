package io.github.kamilperczynski.adocparser

import io.github.kamilperczynski.adocparser.ast.*
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.TerminalNode

class AdocParser(adoc: String) {

    private val parser = createAdocAntlrParser(adoc)

    fun parseAdocAst(): AdocAST {
        val ast = AdocAST()

        for (child in parser.doc().section()) {
            val currentSection = toAdocSection(child)
            val admonitionSectionType = toAdmonitionSectionType(child)

            if (child.admonition() != null) {
                val admonition = child.admonition()
                val paragraph = toAdocParagraph(admonition.paragraph_line())
                val admonitionType = toAdmonitionTypeInlineToken(admonition.ADMONITION_INLINE().text.trim())

                ast.push(
                    currentSection.copy(content = AdocAdmonition(admonitionType, paragraph))
                )
            } else if (child.paragraph_line().isNotEmpty()) {
                val paragraph = toAdocParagraph(child.paragraph_line())

                if (admonitionSectionType != null) {
                    ast.push(
                        currentSection.copy(content = AdocAdmonition(admonitionSectionType, paragraph))
                    )
                } else {
                    currentSection.sectionTitle?.let { ast.push(it) }
                    ast.push(paragraph)
                }
            } else if (child.header() != null) {
                AdocHeaderParser(ast, currentSection).parse(child.header())
            } else if (child.table() != null) {
                val table = child.table()

                AdocTableParser(ast, currentSection).parse(table)
            } else if (child.block() != null) {
                val blockParser = AdocBlockParser(ast, currentSection)

                if (admonitionSectionType == null) {
                    blockParser.parse(child.block())
                } else {
                    blockParser.parseAdmonitionBlock(child.block(), admonitionSectionType)
                }

            } else if (child.list_item().isNotEmpty()) {
                val listItems = mutableListOf<AdocListItem>()

                for (listItem in child.list_item()) {
                    val level = listItem.children.first().text.length

                    val listTypeToken = listItem.children.first() as TerminalNode

                    val paragraph = AdocTextChunker()
                        .parseLine { listItem.paragraph_line().children }
                        .finishParagraph()

                    val adocListItem = AdocListItem(
                        level = level,
                        paragraph = paragraph,
                        numbered = isNumberedList(listTypeToken),
                        lettered = isLetteredList(listTypeToken),
                        lowercased = isLowercaseList(listTypeToken)
                    )
                    listItems.add(adocListItem)
                }

                ast.push(AdocList(listItems))
            } else if (child.page_break() != null) {
                ast.push(AdocPageBreak())
            }
        }

        return ast
    }

}

private fun toAdocSection(child: AsciidocParser.SectionContext): AdocSection {
    val params = child.param_line().lastOrNull()
        ?.let { parseAdocParams(it.params()) }
        ?: EMPTY_ADOC_PARAMS

    val id = child.id_line()?.id()?.WORD()?.text
    val sectionTitle = toSectionTitle(child)

    return AdocSection(id, sectionTitle, null, params)
}

private fun toAdmonitionSectionType(child: AsciidocParser.SectionContext): AdmonitionType? {
    if (child.ADMONITION_LINE().isEmpty()) {
        return null
    }

    return toAdmonitionType(child.ADMONITION_LINE().first().text.trim())
}

private fun toSectionTitle(child: AsciidocParser.SectionContext): AdocSectionTitle? {
    if (child.section_title().isEmpty()) {
        return null
    }

    return AdocSectionTitle(
        AdocTextChunker()
            .parseLine { child.section_title().last().children.drop(1) }
            .chunks
    )
}

private fun isNumberedList(listTypeToken: TerminalNode) =
    listTypeToken.text.endsWith('.')

private fun isLetteredList(listTypeToken: TerminalNode) =
    listTypeToken.text[0].isLetter()

private fun isLowercaseList(listTypeToken: TerminalNode) =
    listTypeToken.text[0].isLowerCase()

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
