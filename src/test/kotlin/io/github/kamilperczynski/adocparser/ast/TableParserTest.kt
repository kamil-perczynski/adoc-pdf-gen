package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.ChunkType.TEXT
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.entry
import org.junit.jupiter.api.Test

class TableParserTest {

    @Test
    fun testParseTable() {
        // given
        val parser = AdocParser(
            """
                .Countries in Europe
                [#table_countries, cols="50e,^25m,>25s",width="75%",options="header",align="center", FooBar]
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
        val astNode = ast.nodes.first()
        assertThat(astNode).isInstanceOf(AdocSection::class.java)

        val adocSection = astNode as AdocSection
        assertThat(adocSection.params.namedParams).containsExactly(
            entry("cols", "50e,^25m,>25s"),
            entry("width", "75%"),
            entry("options", "header"),
            entry("align", "center"),
        )
        assertThat(adocSection.params.positionalParams).containsExactly(
            entry(0, "#table_countries"),
            entry(5, "FooBar")
        )

        assertThat(adocSection.sectionTitle)
            .isEqualTo(AdocSectionTitle(listOf(AdocChunk(TEXT, "Countries in Europe "))))

        assertThat(adocSection.content).isEqualTo(
            AdocTable(
                colWidths = listOf(50, 25, 25),
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
        val astNode = ast.nodes.first()
        assertThat(astNode).isInstanceOf(AdocSection::class.java)

        val adocSection = astNode as AdocSection
        assertThat(adocSection.params).isEqualTo(
            AdocParams(
                mapOf(),
                mapOf(
                    "cols" to "50e,^25m,>25s",
                    "width" to "75%",
                    "options" to "header",
                    "align" to "center"
                )
            )
        )

        assertThat(adocSection.sectionTitle)
            .isEqualTo(AdocSectionTitle(listOf(AdocChunk(TEXT, "Countries in Europe "))))
        assertThat(adocSection.content).isEqualTo(
            AdocTable(
                colWidths = listOf(50, 25, 25),
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
        assertThat(ast.nodes).satisfiesExactly({ it is AdocSection })

        val adocSection = ast.nodes.first() as AdocSection

        assertThat(adocSection.params.isEmpty()).isTrue()
        assertThat(adocSection.sectionTitle).isNull()
        assertThat(adocSection.content).isInstanceOf(AdocTable::class.java)

        val table = adocSection.content as AdocTable
        assertThat(table.colWidths).isNull()
        assertThat(table.colsCount).isEqualTo(3)
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
    }
}
