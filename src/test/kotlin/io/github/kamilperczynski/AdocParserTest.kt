package io.github.kamilperczynski

import io.github.kamilperczynski.adocparser.AsciidocLexer
import io.github.kamilperczynski.adocparser.AsciidocParser
import io.github.kamilperczynski.adocparser.AsciidocParser.SentenceContext
import io.github.kamilperczynski.adocparser.AsciidocParserBaseListener
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.tree.TerminalNode
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class AdocParserTest {

    @Test
    fun boo() {

    }

    @RepeatedTest(20)
    fun test() {
        val adoc = loadResource("doc2.adoc")

        // execute antlr4 parser AsciidocParser, generate code

        val t00 = OffsetDateTime.now();
        val asciidocLexer = AsciidocLexer(
            ANTLRInputStream(adoc)
        )
        val parser = AsciidocParser(CommonTokenStream(asciidocLexer))

        val interpreter = AsciidocInterpreter()

        parser.addParseListener(interpreter)
        parser.document()
        val t11 = OffsetDateTime.now()

        // show difference in millis
        println("Time: ${t11.toInstant().toEpochMilli() - t00.toInstant().toEpochMilli()} ms")


        val t0 = OffsetDateTime.now()
        printPdfStuff(interpreter.sections)
        val t1 = OffsetDateTime.now()

        // show difference in millis
        println("Time: ${t1.toInstant().toEpochMilli() - t0.toInstant().toEpochMilli()} ms")
        println("---")
    }

}

fun loadResource(resource: String): String {
    return AdocParserTest::class.java.classLoader.getResource(resource)!!.readText()
}


internal class AsciidocInterpreter : AsciidocParserBaseListener() {

    val sections get(): List<AdocSection> = _sections

    private val _sections = mutableListOf<AdocSection>()

    private val currentParagraph = StringBuilder()

    override fun exitSection(ctx: AsciidocParser.SectionContext?) {
        ctx!!
        val sectionId = ctx.id().firstOrNull()?.ID_TEXT()?.text

        if (ctx.paragraph() != null) {
            for (child in ctx.paragraph().rich_text().flatMap { it.children }) {
                if (child is TerminalNode) {
                    child.text
                    continue
                }
                val word = child as SentenceContext

//                if (word.macro() != null) {
//                    println("\n !!!! macro ${child.macro().MACRO().text} ${
//                        child.macro().param()
//                            .joinToString(separator = ", ") { "${it.IDENTIFIER().text}=${it.ATTR()?.text}" }
//                    }")
//                }


                word.text.let(currentParagraph::append)


            }
            if (currentParagraph.isNotEmpty()) {
                _sections.add(
                    AdocParagraph(sectionId, currentParagraph.toString())
                )
            }
            currentParagraph.clear()
        }

        if (ctx.list() != null) {
            val items = ctx.list().list_item().map { listItemcontext ->
                val itemText = listItemcontext.rich_text()
                    .flatMap { it.sentence() }
                    .map { it.text }
                    .joinToString(separator = "") { it }

                AdocListItem(itemText)
            }

            _sections.add(AdocListSection(sectionId, items))
        }

        if (ctx.block() != null) {
            val blockText = ctx.block().BLOCK_CONTENT().joinToString(separator = "") { it.text }
            _sections.add(AdocBlock(sectionId, blockText))
        }

        if (ctx.header() != null) {
            val level = ctx.header().HEADER().text.length
            val title = ctx.header().rich_text().sentence().joinToString(separator = "") { it.text }

            _sections.add(
                AdocHeader(sectionId, level, title)
            )
        }
    }
}

interface AdocSection {
    val id: String?
}

data class AdocParagraph(
    override val id: String?,
    val text: String
) : AdocSection

data class AdocBlock(
    override val id: String?,
    val text: String
) : AdocSection


data class AdocListSection(
    override val id: String?,
    val items: List<AdocListItem>
) : AdocSection

data class AdocListItem(val text: String)

data class AdocHeader(
    override val id: String?,
    val level: Int,
    val text: String
) : AdocSection
