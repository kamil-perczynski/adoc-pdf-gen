package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser.*
import io.github.kamilperczynski.adocparser.ast.EmphasisType.*
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode
import java.net.MalformedURLException
import java.net.URI
import java.net.URISyntaxException
import java.util.function.Supplier

class AdocTextChunker {

    private var currentChunk: ChunkBuilder = ChunkBuilder()

    internal val chunks = mutableListOf<AdocChunk>()

    fun parseLine(fn: Supplier<List<ParseTree>>): AdocTextChunker {
        for ((pos, child) in fn.get().withIndex()) {
            when (child) {
                is MacroContext -> {
                    currentChunk.str.append(child.text)
                    // println("..MACRO..: ${child.text}")
                }

                is ParamsContext -> {
                    finishChunk()
                    // println("..PARAMS..: ${child.text}")
                }

                is LinkContext -> {
                    finishChunk()
                    parseLink(child)
                }

                is TerminalNode -> {
                    appendToken(pos, child)
                }
            }
        }

        return this
    }

    fun finishParagraph(): AdocParagraph {
        return AdocParagraph(chunks.toList())
    }

    private fun parseLink(link: LinkContext) {
        val str = StringBuilder()
        link.children.forEach { child ->
            when (child) {
                is TerminalNode -> {
                    str.append(child.text)
                }
            }
        }

        if (isValidUrl(str.toString())) {
            chunks.add(AdocChunk(ChunkType.LINK, str.toString()))
        } else {
            chunks.add(AdocChunk(ChunkType.TEXT, str.toString()))
        }
    }

    private fun appendToken(pos: Int, child: TerminalNode) {
        when (child.symbol.type) {
            EOL -> {
                currentChunk.str.append(' ')
                finishChunk()
                return
            }

            WORD -> {
                currentChunk.str.append(child.text)
            }

            WS -> {
                currentChunk.str.append(' ')
            }

            DOT -> {
                currentChunk.str.append('.')
            }

            ESCAPED_CHAR -> {
                currentChunk.str.append(child.text[1])
            }

            UNDERSCORE -> {
                if (!currentChunk.underlineStarted) {
                    finishChunk()
                    currentChunk.emphasise(ITALIC)
                } else {
                    currentChunk.emphasise(ITALIC)
                    finishChunk()
                }
            }

            ASTERISK -> {
                if (!currentChunk.underlineStarted) {
                    finishChunk()
                    currentChunk.emphasise(BOLD)
                } else {
                    currentChunk.emphasise(BOLD)
                    finishChunk()
                }
            }

            else -> currentChunk.str.append(child.text)
        }
    }

    private fun finishChunk() {
        if (currentChunk.str.isEmpty()) {
            return
        }

        chunks.add(currentChunk.finish())
        currentChunk = ChunkBuilder()
    }

}

class ChunkBuilder {
    internal val str = StringBuilder()

    private var emphasisType: EmphasisType? = null
    private var underlineFinished: Boolean = false

    var underlineStarted: Boolean = false

    fun finish(): AdocChunk {
        val currentEmphasisType = emphasisType

        if (underlineStarted && underlineFinished && currentEmphasisType != null) {
            return AdocChunk(ChunkType.EMPHASIS, str.toString(), currentEmphasisType)
        }

        return AdocChunk(ChunkType.TEXT, str.toString(), NONE)
    }

    fun emphasise(emphasisType: EmphasisType) {
        if (underlineStarted) {
            underlineFinished = true
        } else {
            this.emphasisType = emphasisType
            underlineStarted = true
        }
    }

}

fun isValidUrl(url: String): Boolean {
    try {
        URI.create(url)
        return true
    } catch (e: MalformedURLException) {
        return false
    } catch (e: URISyntaxException) {
        return false
    }
}
