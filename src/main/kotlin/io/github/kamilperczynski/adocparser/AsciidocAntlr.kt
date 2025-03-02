package io.github.kamilperczynski.adocparser

import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

fun runAsciiDocAntlr(adoc: String): AsciidocParser {
    val asciidocLexer = AsciidocLexer(CharStreams.fromString(adoc))

    return AsciidocParser(CommonTokenStream(asciidocLexer))
}
