package io.github.kamilperczynski

import com.lowagie.text.*
import com.lowagie.text.pdf.BaseFont
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Paths


fun printPdfStuff(sections: List<AdocSection>) {
    // generate pdf  with itext with Hello world header and lorem ipsum paragraph
    val file = Paths.get("./LibraryTest-test.pdf")

    if (Files.exists(file)) {
        Files.delete(file)
    }


    val document = Document()
    val writer = PdfWriter.getInstance(document, Files.newOutputStream(file))
    writer.setPdfVersion(PdfWriter.VERSION_1_7);
    document.setMargins(64f, 64f, 64f, 64f)
    document.open()

    // Left
    FontFactory.registerDirectory("/Users/kperczynski/fonties/Ubuntu")

    val baseFont = FontFactory.getFont("times-roman", BaseFont.WINANSI, false, 10f)
    val baseHeaderFont = FontFactory.getFont("ubuntu-regular", BaseFont.WINANSI, true, 10f)


    // A4 SIZE
    val rectangle = Rectangle(PageSize.A4.left, PageSize.A4.bottom, PageSize.A4.right, PageSize.A4.top)
    // delicate, pastel like green color
    rectangle.backgroundColor = Color(0x60, 0x90, 0x70)
    document.add(rectangle)

    document.newPage()
    for (section in sections) {
        when (section) {
            is AdocHeader -> {
                val fontSize = when (section.level) {
                    1 -> 32f
                    2 -> 24f
                    3 -> 14f
                    4 -> 12f
                    else -> 10f
                }

                val headerFont = Font(baseHeaderFont)
                headerFont.size = fontSize
                headerFont.style = Font.BOLD

                val paragraph = Paragraph(section.text, headerFont)
                paragraph.add(LineSeparator(0.5f, 100f, Color.lightGray, 0, -8f))
                paragraph.spacingAfter = 16f
                document.add(paragraph)
            }

            is AdocParagraph -> {
                document.add(Paragraph(section.text, baseFont))
                document.add(Paragraph("\n", baseFont))
            }

            is AdocListSection -> {
                val pdfList = com.lowagie.text.List(16f)
                pdfList.setListSymbol(Chunk("\u2022", Font(baseFont.family, 18f)).setTextRise(-3f))

                for (item in section.items) {
                    val listItem = ListItem(item.text, baseFont)
                    listItem.spacingAfter = 4f
                    pdfList.add(listItem)
                }

                document.add(pdfList)
                document.add(Paragraph("\n", baseFont))
            }
        }
    }


    val pdfPTable = PdfPTable(4)
    pdfPTable.widthPercentage = 100f

    //Create cells
    val pdfPCell1 = PdfPCell(Paragraph("Cell 1", baseFont))
    val pdfPCell2 = PdfPCell(Paragraph("Cell 2", baseFont))
    val pdfPCell3 = PdfPCell(Paragraph("Cell 3", baseFont))
    val pdfPCell4 = PdfPCell(Paragraph("Cell 4", baseFont))

    pdfPCell4.paddingBottom = 6f

    //Add cells to table
    pdfPTable.addCell(pdfPCell1)
    pdfPTable.addCell(pdfPCell2)
    pdfPTable.addCell(pdfPCell3)
    pdfPTable.addCell(pdfPCell4)


    //Add content to the document using Table objects.
    document.add(pdfPTable)



    document.close()
}
