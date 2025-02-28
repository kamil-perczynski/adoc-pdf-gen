package io.github.kamilperczynski

import io.github.kamilperczynski.adocparser.AsciidocLexer
import io.github.kamilperczynski.adocparser.AsciidocParser
import io.github.kamilperczynski.adocparser.AsciidocParserListener
import org.antlr.v4.runtime.ANTLRInputStream
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.TerminalNode
import org.junit.jupiter.api.Test

class AdocParserTest {

    @Test
    fun test() {
        val adoc = loadResource("doc.adoc")

        // execute antlr4 parser AsciidocParser, generate code

        val asciidocLexer = AsciidocLexer(
            ANTLRInputStream(adoc)
        )
        val parser = AsciidocParser(CommonTokenStream(asciidocLexer))
        parser.addParseListener(AsciidocInterpreter())


        val docCtx = parser.document()

        println(docCtx.toStringTree())
    }

}

fun loadResource(resource: String): String {
    return AdocParserTest::class.java.classLoader.getResource(resource)!!.readText()
}


internal class AsciidocInterpreter() : AsciidocParserListener {


    override fun visitTerminal(p0: TerminalNode?) {
    }

    override fun visitErrorNode(p0: ErrorNode?) {
    }

    override fun enterEveryRule(p0: ParserRuleContext?) {
    }

    override fun exitEveryRule(p0: ParserRuleContext?) {
    }

    override fun enterDocument(ctx: AsciidocParser.DocumentContext?) {
    }

    override fun exitDocument(ctx: AsciidocParser.DocumentContext?) {
    }

    override fun enterLine(ctx: AsciidocParser.LineContext?) {
    }

    override fun exitLine(ctx: AsciidocParser.LineContext?) {
    }

    override fun enterTable(ctx: AsciidocParser.TableContext?) {

    }

    override fun exitTable(ctx: AsciidocParser.TableContext?) {

    }

    override fun enterParams(ctx: AsciidocParser.ParamsContext?) {

    }

    override fun exitParams(ctx: AsciidocParser.ParamsContext?) {
        ctx!!
        for (param in ctx.children) {
            println(param)
        }
    }

    override fun enterParam(ctx: AsciidocParser.ParamContext?) {

    }

    override fun exitParam(ctx: AsciidocParser.ParamContext?) {

    }

    override fun enterTable_row(ctx: AsciidocParser.Table_rowContext?) {

    }

    override fun exitTable_row(ctx: AsciidocParser.Table_rowContext?) {

    }

    override fun enterTable_col(ctx: AsciidocParser.Table_colContext?) {

    }

    override fun exitTable_col(ctx: AsciidocParser.Table_colContext?) {

    }

    override fun enterBold_text(ctx: AsciidocParser.Bold_textContext?) {

    }

    override fun exitBold_text(ctx: AsciidocParser.Bold_textContext?) {

    }

    override fun enterUnderline_text(ctx: AsciidocParser.Underline_textContext?) {

    }

    override fun exitUnderline_text(ctx: AsciidocParser.Underline_textContext?) {

    }

    override fun enterWord(ctx: AsciidocParser.WordContext?) {

    }

    override fun exitWord(ctx: AsciidocParser.WordContext?) {

    }

    override fun enterParagraph(ctx: AsciidocParser.ParagraphContext?) {

    }

    override fun exitParagraph(ctx: AsciidocParser.ParagraphContext?) {

    }

    override fun enterText(ctx: AsciidocParser.TextContext?) {
    }

    override fun exitText(ctx: AsciidocParser.TextContext?) {

    }

    override fun enterId(ctx: AsciidocParser.IdContext?) {

    }

    override fun exitId(ctx: AsciidocParser.IdContext?) {

    }

    override fun enterHorizontal_rule(ctx: AsciidocParser.Horizontal_ruleContext?) {

    }

    override fun exitHorizontal_rule(ctx: AsciidocParser.Horizontal_ruleContext?) {

    }

    override fun enterHeader(ctx: AsciidocParser.HeaderContext?) {
    }

    override fun exitHeader(ctx: AsciidocParser.HeaderContext?) {
    }

    override fun enterList(ctx: AsciidocParser.ListContext?) {

    }

    override fun exitList(ctx: AsciidocParser.ListContext?) {

    }


}
