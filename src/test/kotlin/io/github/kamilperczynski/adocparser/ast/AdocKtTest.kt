package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AdocParser
import io.github.kamilperczynski.adocparser.ast.ChunkType.*
import io.github.kamilperczynski.adocparser.ast.EmphasisType.BOLD
import io.github.kamilperczynski.adocparser.ast.EmphasisType.ITALIC
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AdocKtTest {

    @Test
    fun testParseSimpleParagraph() {
        // given
        val parser = AdocParser(
            """
                This is a *bold* statement, and this is _italicized_.
                
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocParagraph(
                listOf(
                    AdocChunk(TEXT, "This is a "),
                    AdocChunk(EMPHASIS, "bold", BOLD),
                    AdocChunk(TEXT, " statement, and this is "),
                    AdocChunk(EMPHASIS, "italicized", ITALIC),
                    AdocChunk(TEXT, ". ")
                )
            )
        )
    }

    @Test
    fun testParseParagraphWithLink() {
        // given
        val parser = AdocParser(
            """
                Is that a link? http://example.com?param1=value1&param2=value2
                
                // FooBar
                
                Yes it is
                
            """.trimIndent()
        )

        // when
        val ast = parser.parseAdocAst()

        // then
        assertThat(ast.nodes).containsExactly(
            AdocParagraph(
                listOf(
                    AdocChunk(TEXT, "Is that a link? "),
                    AdocChunk(
                        type = LINK,
                        text = "http://example.com?param1=value1&param2=value2",
                        params = EMPTY_ADOC_PARAMS
                    ),
                    AdocChunk(TEXT, " ")
                )
            ),
            AdocParagraph(listOf(AdocChunk(TEXT, "Yes it is ")))
        )
    }
}
