package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser.*
import org.antlr.v4.runtime.tree.TerminalNode

class AdocTableParser(private val ast: AdocAST) {

    private var inferredColsCount: Int = 0

    fun parse(table: TableContext) {
        val cols = mutableListOf<AdocTableCol>()

        for (child in table.children) {
            when (child) {
                is Table_cellContext -> {
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

    private fun parseColumn(col: Table_cellContext, cols: MutableList<AdocTableCol>) {
        var pos = 0
        val firstChild = col.children[pos]

        var colspan: String? = null
        var alignment: String? = "<"

        if (firstChild is TerminalNode && firstChild.symbol.type == T_COLSPAN) {
            pos++
            colspan = firstChild.text
        }

        val secondChild = col.children[pos]
        if (secondChild is TerminalNode && secondChild.symbol.type == T_ALIGNMENT) {
            pos++
            alignment = secondChild.text
        }

        // TODO: Parse recursively with paragraph rule?
        val chunk = AdocChunk(
            ChunkType.TEXT,
            col.children.drop(pos + 1).joinToString(separator = "") { it.text }.trim()
        )

        cols.add(AdocTableCol(listOf(chunk), colspan, alignment))
    }
}
