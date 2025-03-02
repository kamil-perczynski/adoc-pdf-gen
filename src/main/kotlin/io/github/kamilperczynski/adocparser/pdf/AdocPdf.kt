package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import io.github.kamilperczynski.adocparser.ast.*
import io.github.kamilperczynski.adocparser.ast.ChunkType.*
import java.awt.Color
import java.io.OutputStream

class AdocPdf {

    private val document: Document

    private val monospaceFont: Font
    private val baseHeaderFont: Font
    private val baseFont: Font

    init {
        FontFactory.registerDirectory("/Users/kperczynski/fonties/Ubuntu")
        FontFactory.registerDirectory("/Users/kperczynski/fonties/JetBrains_Mono")

        this.document = Document()
        this.baseFont = Font(Font.TIMES_ROMAN, 11f, Font.NORMAL)
        this.baseHeaderFont = FontFactory.getFont("ubuntu-regular", BaseFont.WINANSI, true, 16f)
        baseHeaderFont.style = Font.BOLD
        this.monospaceFont = FontFactory.getFont("jetbrainsmono-regular", BaseFont.WINANSI, true, 8f)
    }


    fun print(ast: AdocAST, out: OutputStream) {
        val writer = PdfWriter.getInstance(document, out)
        writer.setPdfVersion(PdfWriter.VERSION_1_7)

        document.setMargins(64f, 64f, 64f, 64f)
        document.open()


        for (node in ast.nodes) {
            when (node) {
                is AdocParagraph -> printParagraph(node)
                is AdocHeader -> printHeader(node)
                is AdocBlock -> printBlock(node)
                is AdocList -> printList(node)
                is AdocSectionTitle -> printSectionTitle(node)
            }
        }

        document.close()
    }

    private fun printSectionTitle(node: AdocSectionTitle) {
        val pdfParagraph = Paragraph()
        pdfParagraph.font = Font(baseFont)
        pdfParagraph.font.style = Font.BOLDITALIC

        printPhraseChunks(node.chunks, pdfParagraph)

        document.add(pdfParagraph)
    }

    private fun printList(node: AdocList) {
        // who in the world names classes like this?
        @Suppress("RemoveRedundantQualifierName")
        val pdfList = com.lowagie.text.List(16f)
        pdfList.setListSymbol(Chunk("\u2022", baseFont))

        for (item in node.items) {
            val listItem = ListItem()
            listItem.font = baseFont
            listItem.spacingAfter = 4f

            printPhraseChunks(item.paragraph.chunks, listItem)
            pdfList.add(listItem)
        }

        document.add(pdfList)
    }

    private fun printBlock(node: AdocBlock) {
        val table = PdfPTable(1)
        table.widthPercentage = 100f
        table.setSpacingBefore(8f)

        val pdfPCell = PdfPCell()
        pdfPCell.setPadding(12f)
        pdfPCell.backgroundColor = Color(0xF0, 0xF0, 0xF0)
        pdfPCell.border = 0

        for (line in node.lines) {
            val paragraph = Paragraph(line.text, monospaceFont)
            paragraph.spacingAfter = 4f
            pdfPCell.addElement(paragraph)
        }

        table.addCell(pdfPCell)

        document.add(table)
    }

    private fun printHeader(node: AdocHeader) {
        val fontSize = when (node.level) {
            1 -> 32f
            2 -> 20f
            3 -> 16f
            4 -> 12f
            else -> 10f
        }

        val pdfParagraph = Paragraph()
        pdfParagraph.font = Font(baseHeaderFont).also { it.size = fontSize }
        pdfParagraph.multipliedLeading = 1.5f
        pdfParagraph.spacingAfter = baseFont.size * 1.5f
        pdfParagraph.spacingBefore = baseFont.size

        printPhraseChunks(node.chunks, pdfParagraph)

        document.add(pdfParagraph)

        val separator = if (node.level == 1) Color.darkGray else Color.lightGray
        document.add(LineSeparator(.5f, 100f, separator, Element.ALIGN_CENTER, baseFont.size - 4f))
    }


    private fun printParagraph(adocParagraph: AdocParagraph) {
        if (adocParagraph.chunks.isEmpty() || adocParagraph.chunks.first().text.isBlank()) {
            return
        }

        val pdfParagraph = Paragraph()
        pdfParagraph.font = baseFont
        pdfParagraph.multipliedLeading = 1.5f
        pdfParagraph.spacingAfter = baseFont.size * 1.5f

        printPhraseChunks(adocParagraph.chunks, pdfParagraph)

        document.add(pdfParagraph)
    }

    private fun printPhraseChunks(
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

}
