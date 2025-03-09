package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.github.kamilperczynski.adocparser.ast.AdocTable
import java.awt.Color

class PdfTablePrinter(
    private val document: Document,
    private val baseFont: Font,
    private val paragraphPrinter: PdfParagraphPrinter
) {

    fun printTable(node: AdocTable) {
        val table = PdfPTable(node.colsCount)
        table.widthPercentage = 100f
        table.setSpacingBefore(baseFont.size * .667f)
        table.setSpacingAfter(baseFont.size * .667f)

        table.setWidths(
            java.util.stream.IntStream.range(0, node.colsCount).map { if (it == 0) 2 else 1 }.toArray()
        )

        for ((idx, col) in node.cols.withIndex()) {
            val pdfPCell = PdfPCell()
            pdfPCell.setPadding(baseFont.size)
            pdfPCell.paddingTop = 0f

            if (idx < node.colsCount) {
                pdfPCell.backgroundColor = Color(0xF0, 0xF0, 0xF0)
            }

            for (chunk in col.chunks) {
                val paragraph = Paragraph()

                if (idx < node.colsCount) {
                    paragraph.font = Font(baseFont)
                    paragraph.font.style = Font.BOLD
                } else {
                    paragraph.font = Font(baseFont)
                    paragraph.font.style = Font.ITALIC
                }

                if (idx % node.colsCount != 0) {
                    paragraph.alignment = Paragraph.ALIGN_RIGHT
                }


                paragraphPrinter.printPhraseChunks(col.chunks, paragraph)
                pdfPCell.addElement(paragraph)
            }

            table.addCell(pdfPCell)
        }

        document.add(table)
    }


}
