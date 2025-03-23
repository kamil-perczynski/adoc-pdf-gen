package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.*
import com.lowagie.text.pdf.ColumnText
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import io.github.kamilperczynski.adocparser.ast.AdocHeader
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import java.awt.Color // awt? my eyes are bleeding

class PdfHeaderPrinter(
    private val document: Document,
    private val stylesheet: AdocStylesheet,
    private var chapterCounter: ChapterCounter,
    private val pdfParagraphPrinter: PdfParagraphPrinter
) {

    fun printHeader(node: AdocHeader, writer: PdfWriter) {
        if (node.level == 1) {
            titlePage(node, writer)
            return
        }

        val pdfParagraph = Paragraph()
        stylesheet.styleHeader(pdfParagraph, node)
        pdfParagraph.keepTogether = true

        pdfParagraphPrinter.printPhraseChunks(node.chunks, pdfParagraph)

        pdfParagraph.add(
            LineSeparator(
                .5f,
                100f,
                Color.lightGray,
                Element.ALIGN_CENTER,
                -stylesheet.baseFont.size * .75f
            )
        )

        val chapter = ChapterSection(pdfParagraph, chapterCounter.nextChapterNumber())
        chapter.isTriggerNewPage = false
        document.add(chapter)
    }

    private fun titlePage(node: AdocHeader, writer: PdfWriter) {
        if (writer.pageNumber != 1) {
            document.newPage()
        }

        // add rectancle covering whole page with pastel green background
        val rectangle = Rectangle(PageSize.A4.width, PageSize.A4.height)
        rectangle.backgroundColor = Color(0x21, 0x33, 0x2d)

        document.add(rectangle)

        val c = writer.directContent

        val titlePageFont = Font(stylesheet.baseFont)
        titlePageFont.size = 54f
        titlePageFont.style = Font.BOLD
        titlePageFont.color = Color(0xFF, 0xFF, 0xFF)

        val columnText = ColumnText(c)
        val phrase = Phrase()
        phrase.font = titlePageFont
        pdfParagraphPrinter.printPhraseChunks(node.chunks, phrase)


        columnText.setText(phrase)
        columnText.setSimpleColumn(
            20f,
            00f,
            PageSize.A4.width - 20f,
            PageSize.A4.height * 0.64f,
            titlePageFont.size,
            Element.ALIGN_CENTER
        )
        columnText.go()

        document.newPage()
    }

}

private class ChapterSection(paragraph: Paragraph, number: Int) : Chapter(paragraph, number) {

    override fun getTitle(): Paragraph {
        return Paragraph(this.title)
    }

}
