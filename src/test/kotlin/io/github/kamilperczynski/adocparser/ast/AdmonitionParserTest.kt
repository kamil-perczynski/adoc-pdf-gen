package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.IMPORTANT
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.NOTE
import io.github.kamilperczynski.adocparser.ast.ChunkType.EMPHASIS
import io.github.kamilperczynski.adocparser.ast.ChunkType.TEXT
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AdmonitionParserTest {

    @Test
    fun `test parse admonition block`() {
        // given
        val parser = AdocParser(
            """
            [NOTE]
            .Instructions
            ===============================================
            This section *explains* some concepts.
            ===============================================
           
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocSectionTitle(listOf(AdocChunk(TEXT, "Instructions "))),
            AdocAdmonition(
                NOTE,
                AdocParagraph(
                    listOf(
                        AdocChunk(TEXT, "This section "),
                        AdocChunk(EMPHASIS, "explains"),
                        AdocChunk(TEXT, " some concepts. ")
                    )
                )
            )
        )
    }

    @Test
    fun `test parse inline admonition`() {
        // given
        val parser = AdocParser(
            """
            = Foo Bar
            
            .Instructions
            IMPORTANT: This is important.
            
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocHeader(1, listOf(AdocChunk(TEXT, "Foo Bar ")), emptyList()),
            AdocSectionTitle(listOf(AdocChunk(TEXT, "Instructions "))),
            AdocAdmonition(
                IMPORTANT,
                AdocParagraph(listOf(AdocChunk(TEXT, "This is important. ")))
            )
        )
    }

}
