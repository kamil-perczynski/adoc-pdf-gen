package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.*
import io.github.kamilperczynski.adocparser.ast.AdocChunk
import io.github.kamilperczynski.adocparser.ast.AdocParagraph
import io.github.kamilperczynski.adocparser.ast.AdocSectionTitle
import io.github.kamilperczynski.adocparser.ast.ChunkType.*
import io.github.kamilperczynski.adocparser.ast.EmphasisType.*
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import java.awt.Color

class PdfParagraphPrinter(
    private val document: Document,
    private val stylesheet: AdocStylesheet
) {

    fun printParagraph(adocParagraph: AdocParagraph) {
        if (adocParagraph.chunks.isEmpty() || adocParagraph.chunks.first().text.isBlank()) {
            return
        }

        val pdfParagraph = toPdfParagraph(adocParagraph)
        document.add(pdfParagraph)
    }

    fun toPdfParagraph(adocParagraph: AdocParagraph): Paragraph {
        val pdfParagraph = Paragraph()
        stylesheet.styleParagraph(pdfParagraph, adocParagraph)

        printPhraseChunks(adocParagraph.chunks, pdfParagraph)

        return pdfParagraph
    }

    fun printPhraseChunks(
        chunks: List<AdocChunk>,
        paragraph: Phrase
    ) {
        for (chunk in chunks) {
            when (chunk.type) {
                TEXT -> {
                    paragraph.add(chunk.text)
                }

                EMPHASIS -> {
                    val emphasisFont = Font(paragraph.font)

                    emphasisFont.style = when (chunk.emphasis) {
                        ITALIC -> Font.ITALIC
                        BOLD -> Font.BOLD
                        BOLD_ITALIC -> Font.BOLDITALIC
                        NONE -> Font.NORMAL
                    }

                    paragraph.add(Chunk(chunk.text, emphasisFont))
                }

                LINK -> {
                    val linkFont = Font(paragraph.font).apply {
                        style = Font.UNDERLINE
                        color = Color.BLUE
                    }

                    val anchor = Anchor(chunk.text, linkFont).apply {
                        reference = chunk.text
                    }
                    paragraph.add(anchor)
                }

                PARAMS -> {
                    // nothing
                }
            }
        }
    }

    fun printSectionTitle(node: AdocSectionTitle) {
        val pdfParagraph = Paragraph()
        stylesheet.styleSectionTitle(pdfParagraph, node)

        printPhraseChunks(node.chunks, pdfParagraph)

        document.add(pdfParagraph)
    }

}
