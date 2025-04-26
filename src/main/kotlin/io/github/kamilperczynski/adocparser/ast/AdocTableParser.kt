package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser.*
import org.antlr.v4.runtime.tree.TerminalNode

private val COLS_ATTRIBUTE_REGEX = "\\d+".toRegex()

private val TABLE_CELL_REGEX = Regex("^([0-9])?(\\.?[0-9]+\\+)?([>^<])?\\.?([>^<])?([adehlms])?\\|$")

class AdocTableParser(private val ast: AdocAST, private val currentSection: AdocSection) {

    private var inferredColsCount: Int = 0

    fun parse(table: TableContext) {
        val cols = mutableListOf<AdocTableCol>()

        for (child in table.children) {
            when (child) {
                is Table_cellContext -> {
                    parseColumn(child, cols)

                    // if the column ends with EOL, then we can infer the number of columns
                    val lastToken = child.children.last()
                    if (inferredColsCount == 0 && lastToken is TerminalNode && lastToken.symbol.type == T_EOL) {
                        inferredColsCount = cols.size
                    }
                }
            }

        }

        val colWidths = currentSection.params.namedParams["cols"]
            ?.let { colsAttribute ->
                colsAttribute
                    .split(',')
                    .mapNotNull { COLS_ATTRIBUTE_REGEX.find(it)?.value?.toInt() }
            }

        ast.push(
            currentSection.copy(
                content = AdocTable(
                    colWidths = colWidths,
                    colsCount = colWidths?.size ?: inferredColsCount,
                    cols = cols
                )
            )
        )
    }

    // TODO: Rewrite this method so it doesn't use regexes
    private fun parseColumn(col: Table_cellContext, cols: MutableList<AdocTableCol>) {
        val celltoken = col.TABLE_CELL_START().text

        val match = TABLE_CELL_REGEX.find(celltoken)

        if (match == null) {
            throw IllegalArgumentException("Invalid table cell token: $celltoken")
        }

        val span1 = match.groupValues[1]
            .ifEmpty { null }
            ?.let { COLS_ATTRIBUTE_REGEX.find(it)?.value }

        val span2 = match.groupValues[2]
            .ifEmpty { null }
            ?.let { COLS_ATTRIBUTE_REGEX.find(it)?.value }

        val colspan = span1 ?: span2
        val rowspan = if (span1 != null) span2 else span1


        val horizontalAlignment = match.groupValues[3].ifEmpty { null }
        val verticalAlignment = match.groupValues[4].ifEmpty { null }
        val mod = match.groupValues[5].ifEmpty { null }

        val chunk = AdocChunk(
            type = ChunkType.TEXT,
            text = col.children
                .drop(1)
                .joinToString("") { it.text }
                .trim(),
        )

        cols.add(
            AdocTableCol(
                chunks = listOf(chunk),
                rowspan = rowspan,
                colspan = colspan,
                horizontalAlignment = horizontalAlignment,
                verticalAlignment = verticalAlignment,
                cellFormat = mod
            )
        )
    }
}
