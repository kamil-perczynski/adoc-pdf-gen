package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.ast.ChunkType.*
import io.github.kamilperczynski.adocparser.runAsciiDocAntlr
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class AdocKtTest {

    @Test
    fun testParseSimpleParagraph() {
        // given
        val parser = runAsciiDocAntlr(
            """
                This is a *bold* statement, and this is _italicized_.
                
            """.trimIndent()
        )

        // when
        val ast = parseAsciiDocAST(parser)

        // then
        assertThat(ast.nodes).containsExactly(
            AdocParagraph(
                listOf(
                    AdocChunk(TEXT, "This is a "),
                    AdocChunk(EMPHASIS, "bold"),
                    AdocChunk(TEXT, " statement, and this is "),
                    AdocChunk(EMPHASIS, "italicized"),
                    AdocChunk(TEXT, ".")
                )
            )
        )
    }

    @Test
    fun testParseParagraphWithLink() {
        // given
        val parser = runAsciiDocAntlr(
            """
                Is that a link? http://example.com?param1=value1&param2=value2
                
                // FooBar
                
                Yes it is
                
            """.trimIndent()
        )

        // when
        val ast = parseAsciiDocAST(parser)

        // then
        assertThat(ast.nodes).containsExactly(
            AdocParagraph(
                listOf(
                    AdocChunk(TEXT, "Is that a link? "),
                    AdocChunk(LINK, "http://example.com?param1=value1&param2=value2")
                )
            ),
            AdocParagraph(listOf(AdocChunk(TEXT, "Yes it is")))
        )
    }
}
