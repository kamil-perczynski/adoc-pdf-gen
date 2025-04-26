package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfWriter
import io.github.kamilperczynski.adocparser.ast.*
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import java.io.OutputStream

class AdocPdf(stylesheet: AdocStylesheet) {

    private val document = Document()
    private val chapterCounter: ChapterCounter = DocumentChapterCounter()

    private val monospaceFont =
        FontFactory.getFont("jetbrainsmono-regular", BaseFont.WINANSI, true, 8f)

    private val pdfParagraphPrinter = PdfParagraphPrinter(document, stylesheet)

    private val headerPrinter =
        PdfHeaderPrinter(document, stylesheet, chapterCounter, pdfParagraphPrinter)

    private val tablePrinter =
        PdfTablePrinter(document, stylesheet, pdfParagraphPrinter)

    private val blockPrinter = PdfBlockPrinter(document, monospaceFont)
    private val listPrinter = PdfListPrinter(document, stylesheet, pdfParagraphPrinter)
    private val admonitionPrinter = AdmonitionPrinter(document, stylesheet.baseFont, pdfParagraphPrinter)

    fun print(ast: AdocAST, out: OutputStream) {
        val writer = PdfWriter.getInstance(document, out)
        writer.setPdfVersion(PdfWriter.VERSION_1_7)

        document.setMargins(64f, 64f, 64f, 64f)
        document.open()

        for (node in ast.nodes) {
            printNode(node, writer)
        }

        document.close()
    }

    private fun printNode(node: AdocNode, writer: PdfWriter, section: AdocSection? = null) {
        when (node) {
            is AdocParagraph -> pdfParagraphPrinter.printParagraph(node)
            is AdocSectionTitle -> pdfParagraphPrinter.printSectionTitle(node)
            is AdocHeader -> headerPrinter.printHeader(node, writer)
            is AdocBlock -> blockPrinter.printBlock(node)
            is AdocList -> listPrinter.printList(node)
            is AdocTable -> tablePrinter.printTable(node, section)
            is AdocAdmonition -> admonitionPrinter.printAdmonition(node)
            is AdocSection -> {
                if (node.sectionTitle != null) {
                    pdfParagraphPrinter.printSectionTitle(node.sectionTitle)
                }

                if (node.content != null) {
                    printNode(node.content, writer, node)
                }
            }

            is AdocPageBreak -> {
                document.newPage()
            }
        }
    }

}

class DocumentChapterCounter : ChapterCounter {
    override var chapterCounter: Int = 0

    override fun nextChapterNumber(): Int {
        return ++chapterCounter
    }
}
