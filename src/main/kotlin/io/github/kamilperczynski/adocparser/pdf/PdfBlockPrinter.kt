package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.github.kamilperczynski.adocparser.ast.AdocBlock
import java.awt.Color

class PdfBlockPrinter(private val document: Document, private val monospaceFont: Font) {

    fun printBlock(node: AdocBlock) {
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

}
