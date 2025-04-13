package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser
import io.github.kamilperczynski.adocparser.createAdocAntlrParser
import io.github.kamilperczynski.adocparser.toAdocParagraph

class AdocBlockParser(private val ast: AdocAST, private val currentSection: AdocSection) {

    fun parse(block: AsciidocParser.BlockContext) {
        val chunks = block.BLOCK_CONTENT()
            .map { AdocChunk(ChunkType.TEXT, it.text) }
            .toList()

        ast.push(AdocBlock(chunks))
    }

    fun parseAdmonitionBlock(block: AsciidocParser.BlockContext, admonitionType: AdmonitionType) {
        val blockContent = block.BLOCK_CONTENT()
            .joinToString(separator = "\n", postfix = "\n") { it.text }

        val section = createAdocAntlrParser(blockContent).section()
        val adocParagraph = toAdocParagraph(section.paragraph_line())

        ast.push(
            currentSection.copy(content = AdocAdmonition(admonitionType, adocParagraph))
        )
    }

}
