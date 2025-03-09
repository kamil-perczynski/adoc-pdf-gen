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
                
                
                2.3+>| Monaco | 36371 | 1.98
                3+| Gibraltar | 29431 | 6.8
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
                    AdocTableCol(listOf(AdocChunk(TEXT, "Monaco")), "2.3+", ">"),
                    AdocTableCol(listOf(AdocChunk(TEXT, "36371"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "1.98"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "Gibraltar")), "3+"),
                    AdocTableCol(listOf(AdocChunk(TEXT, "29431"))),
                    AdocTableCol(listOf(AdocChunk(TEXT, "6.8")))
                )
            )
        )
    }
}
