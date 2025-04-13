package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.IMPORTANT
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.NOTE
import io.github.kamilperczynski.adocparser.ast.ChunkType.EMPHASIS
import io.github.kamilperczynski.adocparser.ast.ChunkType.TEXT
import io.github.kamilperczynski.adocparser.ast.EmphasisType.BOLD
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
        assertThat(ast.nodes).satisfiesExactly(
            { assertThat(it).isInstanceOf(AdocSection::class.java) }
        )

        val section = ast.nodes.first() as AdocSection
        assertThat(section.sectionTitle).isEqualTo(AdocSectionTitle(listOf(AdocChunk(TEXT, "Instructions "))))
        assertThat(section.content).isEqualTo(
            AdocAdmonition(
                NOTE,
                AdocParagraph(
                    listOf(
                        AdocChunk(TEXT, "This section "),
                        AdocChunk(EMPHASIS, "explains", BOLD),
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
        assertThat(ast.nodes).satisfiesExactly(
            { assertThat(it).isInstanceOf(AdocSection::class.java) },
            { assertThat(it).isInstanceOf(AdocSection::class.java) }
        )

        val section1 = ast.nodes[0] as AdocSection
        assertThat(section1.content).isEqualTo(AdocHeader(1, listOf(AdocChunk(TEXT, "Foo Bar ")), emptyList()))

        val section2 = ast.nodes[1] as AdocSection
        assertThat(section2.sectionTitle).isEqualTo(AdocSectionTitle(listOf(AdocChunk(TEXT, "Instructions "))))
        assertThat(section2.content).isEqualTo(
            AdocAdmonition(
                IMPORTANT,
                AdocParagraph(listOf(AdocChunk(TEXT, "This is important. ")))
            )
        )
    }

}
