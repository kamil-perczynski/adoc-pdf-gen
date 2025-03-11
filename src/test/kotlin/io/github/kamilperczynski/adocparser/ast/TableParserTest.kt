package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.ChunkType.TEXT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TableParserTest {

    @Test
    fun testParseTable() {
        // given
        val parser = AdocParser(
            """
                [#table_countries,reftext='{table-caption} {counter:table-num}']
                .Countries in Europe
                [cols="50e,^25m,>25s",width="75%",options="header",align="center"]
                |===
                | Country | Population | Size
                | Monaco
                | 36371
                | 1.98
                | Gibraltar
                | 29431
                | 6.8
                |===
                
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocSectionTitle(listOf(AdocChunk(TEXT, "Countries in Europe "))),
            AdocTable(
                colsCount = 3,
                cols = listOf(
                    AdocTableCol(listOf(AdocChunk(TEXT, "Country"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Population"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Size"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Monaco"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "36371"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "1.98"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Gibraltar"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "29431"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "6.8")))
                )
            )
        )
    }

    @Test
    fun testParseTableWithRowLayout() {
        // given
        val parser = AdocParser(
            """
                [#table_countries,reftext='{table-caption} {counter:table-num}']
                .Countries in Europe
                [cols="50e,^25m,>25s",width="75%",options="header",align="center"]
                |===
                | Country | Population | Size
                
                
                | Monaco | 36371 | 1.98
                | Gibraltar | 29431 | 6.8
                |===
                
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocSectionTitle(listOf(AdocChunk(TEXT, "Countries in Europe "))),
            AdocTable(
                colsCount = 3,
                cols = listOf(
                    AdocTableCol(listOf(AdocChunk(TEXT, "Country"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Population"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Size"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Monaco"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "36371"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "1.98"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Gibraltar"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "29431"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "6.8")))
                )
            )
        )
    }

    @Test
    fun `test table parsing with colspans and cellformats`() {
        // given
        val parser = AdocParser(
            """
                [cols="50e,^25m,>25s",width="75%",options="header",align="center"]
                |===
                | Country | Population | Size
                
                
                2.3+>| Monaco | 36371 | 1.98
                3+m| Gibraltar | 29431 | 6.8
                | Poland | 38454 | 845.8
                |===
                
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).satisfiesExactly({ it is AdocTable })

        val table = ast.nodes.first() as AdocTable

        assertThat(table.cols).containsExactly(
            AdocTableCol(listOf(AdocChunk(TEXT, "Country"))),
            AdocTableCol(listOf(AdocChunk(TEXT, "Population"))),
            AdocTableCol(listOf(AdocChunk(TEXT, "Size"))),
            AdocTableCol(
                chunks = listOf(AdocChunk(TEXT, "Monaco")),
                colspan = "2.3+",
                alignment = ">"
            ),
            AdocTableCol(listOf(AdocChunk(TEXT, "36371"))),
            AdocTableCol(listOf(AdocChunk(TEXT, "1.98"))),

            AdocTableCol(
                chunks = listOf(AdocChunk(TEXT, "Gibraltar")),
                colspan = "3+",
                alignment = "<",
                cellFormat = "m"
            ),
            AdocTableCol(listOf(AdocChunk(TEXT, "29431"))),
            AdocTableCol(listOf(AdocChunk(TEXT, "6.8"))),

            AdocTableCol(listOf(AdocChunk(TEXT, "Poland"))),
            AdocTableCol(listOf(AdocChunk(TEXT, "38454"))),
            AdocTableCol(listOf(AdocChunk(TEXT, "845.8")))
        )

        assertThat(table.colsCount).isEqualTo(3)
    }
}
