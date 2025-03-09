package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfWriter
import io.github.kamilperczynski.adocparser.ast.*
import java.io.OutputStream

class AdocPdf {

    companion object {
        init {
            FontFactory.registerDirectory("/Users/kperczynski/fonties/Ubuntu")
            FontFactory.registerDirectory("/Users/kperczynski/fonties/JetBrains_Mono")
        }
    }

    private val document = Document()
    private val chapterCounter: ChapterCounter = DocumentChapterCounter()

    private val baseFont =
        Font(Font.TIMES_ROMAN, 11f, Font.NORMAL)

    private val monospaceFont =
        FontFactory.getFont("jetbrainsmono-regular", BaseFont.WINANSI, true, 8f)

    private val baseHeaderFont =
        FontFactory.getFont("ubuntu-regular", BaseFont.WINANSI, true, 16f).also { it.style = Font.BOLD }

    private val pdfParagraphPrinter =
        PdfParagraphPrinter(document, baseFont)

    private val headerPrinter =
        PdfHeaderPrinter(document, baseFont, baseHeaderFont, chapterCounter, pdfParagraphPrinter)

    private val tablePrinter =
        PdfTablePrinter(document, baseFont, pdfParagraphPrinter)

    private val blockPrinter = PdfBlockPrinter(document, monospaceFont)
    private val listPrinter = PdfListPrinter(document, baseFont, pdfParagraphPrinter)


    fun print(ast: AdocAST, out: OutputStream) {
        val writer = PdfWriter.getInstance(document, out)
        writer.setPdfVersion(PdfWriter.VERSION_1_7)

        document.setMargins(64f, 64f, 64f, 64f)
        document.open()

        for (node in ast.nodes) {
            when (node) {
                is AdocParagraph -> pdfParagraphPrinter.printParagraph(node)
                is AdocSectionTitle -> pdfParagraphPrinter.printSectionTitle(node)
                is AdocHeader -> headerPrinter.printHeader(node, writer)
                is AdocBlock -> blockPrinter.printBlock(node)
                is AdocList -> listPrinter.printList(node)
                is AdocTable -> tablePrinter.printTable(node)
            }
        }

        document.close()
    }

}

class DocumentChapterCounter : ChapterCounter {
    override var chapterCounter: Int = 0

    override fun nextChapterNumber(): Int {
        return ++chapterCounter
    }
}
