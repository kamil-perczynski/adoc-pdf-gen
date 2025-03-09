package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.*
import io.github.kamilperczynski.adocparser.ast.AdocChunk
import io.github.kamilperczynski.adocparser.ast.AdocParagraph
import io.github.kamilperczynski.adocparser.ast.AdocSectionTitle
import io.github.kamilperczynski.adocparser.ast.ChunkType.*
import java.awt.Color

class PdfParagraphPrinter(private val document: Document, private val baseFont: Font) {

    fun printParagraph(adocParagraph: AdocParagraph) {
        if (adocParagraph.chunks.isEmpty() || adocParagraph.chunks.first().text.isBlank()) {
            return
        }

        val pdfParagraph = Paragraph()
        pdfParagraph.font = baseFont
        pdfParagraph.multipliedLeading = 1.25f
        pdfParagraph.spacingBefore = baseFont.size * .5f
        pdfParagraph.spacingAfter = baseFont.size * .75f

        printPhraseChunks(adocParagraph.chunks, pdfParagraph)

        document.add(pdfParagraph)
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
                    val emphasisFont = Font(baseFont).apply { style = Font.BOLD }
                    paragraph.add(Chunk(chunk.text, emphasisFont))
                }

                LINK -> {
                    val linkFont = Font(baseFont).apply {
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
        pdfParagraph.font = Font(baseFont)
        pdfParagraph.font.style = Font.BOLDITALIC
        pdfParagraph.spacingAfter = baseFont.size * 0.25f

        printPhraseChunks(node.chunks, pdfParagraph)

        document.add(pdfParagraph)
    }

}
