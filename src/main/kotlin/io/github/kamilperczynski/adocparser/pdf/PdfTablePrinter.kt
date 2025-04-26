package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.github.kamilperczynski.adocparser.ast.AdocSection
import io.github.kamilperczynski.adocparser.ast.AdocTable
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import java.awt.Color

class PdfTablePrinter(
    private val document: Document,
    private val stylesheet: AdocStylesheet,
    private val paragraphPrinter: PdfParagraphPrinter
) {

    fun printTable(node: AdocTable, section: AdocSection?) {
        if (section == null) {
            throw IllegalArgumentException("Section cannot be null")
        }

        val table = PdfPTable(node.colsCount)
        stylesheet.styleTable(table, node, section)

        for ((idx, col) in node.cols.withIndex()) {
            val pdfPCell = PdfPCell()
            pdfPCell.setPadding(stylesheet.baseFont.size * .75f)
            pdfPCell.paddingTop = 0f

            if (col.colspan != null) {
                pdfPCell.colspan = col.colspan.toInt()
            }
            if (col.rowspan != null) {
                pdfPCell.rowspan = col.rowspan.toInt()
            }

            if (idx < node.colsCount) {
                pdfPCell.backgroundColor = Color(0xF0, 0xF0, 0xF0)
            }

            when (col.horizontalAlignment) {
                "<" -> pdfPCell.horizontalAlignment = PdfPCell.ALIGN_LEFT
                "^" -> pdfPCell.horizontalAlignment = PdfPCell.ALIGN_CENTER
                ">" -> pdfPCell.horizontalAlignment = PdfPCell.ALIGN_RIGHT
            }

            when (col.verticalAlignment) {
                "<" -> pdfPCell.verticalAlignment = PdfPCell.ALIGN_TOP
                "^" -> pdfPCell.verticalAlignment = PdfPCell.ALIGN_MIDDLE
                ">" -> pdfPCell.verticalAlignment = PdfPCell.ALIGN_BOTTOM
            }

            for (chunk in col.chunks) {
                val paragraph = Paragraph()

                when (col.horizontalAlignment) {
                    "<" -> paragraph.alignment = PdfPCell.ALIGN_LEFT
                    "^" -> paragraph.alignment = PdfPCell.ALIGN_CENTER
                    ">" -> paragraph.alignment = PdfPCell.ALIGN_RIGHT
                }

                if (idx < node.colsCount) {
                    paragraph.font = Font(stylesheet.baseFont)
                    paragraph.font.style = Font.BOLD
                } else {
                    paragraph.font = Font(stylesheet.baseFont)
                    paragraph.font.style = Font.ITALIC
                }

                paragraphPrinter.printPhraseChunks(col.chunks, paragraph)
                pdfPCell.addElement(paragraph)
            }

            table.addCell(pdfPCell)
        }

        document.add(table)
    }


}
