package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser

class AdocHeaderParser(private val ast: AdocAST) {

    fun parse(header: AsciidocParser.HeaderContext) {
        val headerMarker = header.children.first()

        val parser = AdocTextChunker().parseLine { header.children.drop(1) }

        val level = headerMarker.text.length - 1
        ast.push(AdocHeader(level, parser.chunks, emptyList()))
    }

}
