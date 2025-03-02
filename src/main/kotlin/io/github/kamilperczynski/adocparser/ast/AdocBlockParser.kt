package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser

class AdocBlockParser(private val ast: AdocAST) {

    fun parse(block: AsciidocParser.BlockContext) {
        val chunks = block.BLOCK_CONTENT()
            .map { AdocChunk(ChunkType.TEXT, it.text) }
            .toList()

        ast.push(AdocBlock(chunks))
    }

}
