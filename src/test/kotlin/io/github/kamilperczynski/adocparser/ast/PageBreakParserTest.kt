package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.ChunkType.TEXT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class PageBreakParserTest {

    @Test
    fun `test parse break lines`() {
        // given
        val parser = AdocParser(
            """
                .List <<< of items
                Paragraph before page break <<<

                [%force]
                <<<

                <<< Paragraph after page break

            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocSection(
                sectionTitle = AdocSectionTitle(listOf(AdocChunk(TEXT, "List <<< of items "))),
                content = AdocParagraph(listOf(AdocChunk(TEXT, "Paragraph before page break <<< ")))
            ),
            AdocPageBreak(),
            AdocSection(
                content = AdocParagraph(listOf(AdocChunk(TEXT, "<<< Paragraph after page break ")))
            ),
        )
    }

    @Test
    fun `test parse break lines in list item`() {
        // given
        val parser = AdocParser(
            """
                * <<< Item 1
                * <<< Item 2

                [%force]
                <<<

                . List <<< of items

            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocList(
                items = listOf(
                    AdocListItem(
                        level = 1,
                        paragraph = AdocParagraph(listOf(AdocChunk(TEXT, "<<< Item 1 "))),
                        numbered = false,
                        lettered = false,
                        lowercased = false
                    ),
                    AdocListItem(
                        level = 1,
                        paragraph = AdocParagraph(listOf(AdocChunk(TEXT, "<<< Item 2 "))),
                        numbered = false,
                        lettered = false,
                        lowercased = false
                    )
                )
            ),
            AdocPageBreak(),
            AdocList(
                items = listOf(
                    AdocListItem(
                        level = 1,
                        paragraph = AdocParagraph(listOf(AdocChunk(TEXT, "List <<< of items "))),
                        numbered = true,
                        lettered = false,
                        lowercased = false
                    )
                )
            )
        )
    }

}
