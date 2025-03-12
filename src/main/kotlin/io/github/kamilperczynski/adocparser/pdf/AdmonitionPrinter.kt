package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import io.github.kamilperczynski.adocparser.ast.AdmonitionType.*
import io.github.kamilperczynski.adocparser.ast.AdocAdmonition
import java.awt.Color

class AdmonitionPrinter(
    private val document: Document,
    private val baseFont: Font,
    private val paragraphPrinter: PdfParagraphPrinter
) {

    fun printAdmonition(node: AdocAdmonition) {
        val table = PdfPTable(2)
        table.widthPercentage = 100f
        table.setSpacingBefore(baseFont.size * .667f)
        table.setSpacingAfter(baseFont.size * .667f)


        val colWidths = when (node.admonitionType) {
            NOTE, TIP, DANGER, ERROR -> intArrayOf(4, 20)
            IMPORTANT, WARNING, CAUTION -> intArrayOf(5, 19)

        }

        table.setWidths(colWidths)

        val admonitionColumn = PdfPCell()
        admonitionColumn.setPadding(baseFont.size)
        admonitionColumn.paddingTop = 0f


        val admonitionFont = Font(baseFont)
        admonitionFont.style = Font.BOLDITALIC

        val admonitionParagraph = Paragraph(node.admonitionType.name, admonitionFont)
        admonitionParagraph.alignment = Paragraph.ALIGN_CENTER

        admonitionColumn.border = PdfPCell.NO_BORDER
        admonitionColumn.borderWidthRight = 0.25f
        admonitionColumn.borderColorRight = Color.lightGray
        admonitionColumn.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        admonitionColumn.addElement(admonitionParagraph)

        val contentColumn = PdfPCell()
        contentColumn.setPadding(baseFont.size)
        contentColumn.paddingTop = 0f
        contentColumn.border = PdfPCell.NO_BORDER
        contentColumn.verticalAlignment = PdfPCell.ALIGN_MIDDLE

        val pdfParagraph = paragraphPrinter.toPdfParagraph(node.paragraph)
        contentColumn.addElement(pdfParagraph)

        table.addCell(admonitionColumn)
        table.addCell(contentColumn)

        document.add(table)
    }

}
