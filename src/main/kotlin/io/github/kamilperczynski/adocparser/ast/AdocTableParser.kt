package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser
import io.github.kamilperczynski.adocparser.AsciidocParser.T_EOL
import org.antlr.v4.runtime.tree.TerminalNode

class AdocTableParser(private val ast: AdocAST) {

    private var inferredColsCount: Int = 0

    fun parse(table: AsciidocParser.TableContext) {
        val cols = mutableListOf<AdocTableCol>()

        for (child in table.children) {
            when (child) {
                is AsciidocParser.Table_cellContext -> {
                    parseColumn(child, cols)
                }

                is TerminalNode -> {
                    if (child.symbol.type == T_EOL && inferredColsCount == 0) {
                        inferredColsCount = cols.size
                        continue
                    }
                }
            }

        }

        ast.push(AdocTable(inferredColsCount, cols))
    }

    private fun parseColumn(
        col: AsciidocParser.Table_cellContext,
        cols: MutableList<AdocTableCol>
    ) {
        // TODO: Parse recursively with paragraph rule?
        val chunk = AdocChunk(ChunkType.TEXT, col.children.drop(1).joinToString(separator = "") { it.text })

        cols.add(AdocTableCol(listOf(chunk)))
    }
}
