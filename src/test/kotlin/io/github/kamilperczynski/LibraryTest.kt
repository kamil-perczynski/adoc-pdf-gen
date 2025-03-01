package io.github.kamilperczynski

import com.lowagie.text.*
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import com.lowagie.text.pdf.draw.LineSeparator
import org.junit.jupiter.api.Test
import java.awt.Color
import java.nio.file.Files
import java.nio.file.Paths


class LibraryTest {

    @Test
    fun test() {
        printPdfStuff(emptyList())
    }

}


fun printPdfStuff(sections: List<AdocSection>) {
    // generate pdf  with itext with Hello world header and lorem ipsum paragraph
    val file = Paths.get("./LibraryTest-test.pdf")

    if (Files.exists(file)) {
        Files.delete(file)
    }


    val document = Document()
    PdfWriter.getInstance(document, Files.newOutputStream(file))
    document.setMargins(64f, 64f, 64f, 64f)
    document.open()

    // Left

    FontFactory.registerDirectory("/Users/kperczynski/fonties/Ubuntu")

    val font = FontFactory.getFont("ubuntu-regular", 10f)


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

                val headerFont = FontFactory.getFont("ubuntu-regular", fontSize)
                headerFont.style = Font.BOLD

                val paragraph = Paragraph(section.text, headerFont)
                paragraph.add(LineSeparator(0.5f, 100f, Color.lightGray, 0, -8f))
                paragraph.add(Paragraph("\n", font))

                document.add(paragraph)
            }

            is AdocParagraph -> {
                document.add(Paragraph(section.text, font))
                document.add(Paragraph("\n", font))
            }

            is AdocListSection -> {
                val pdfList = com.lowagie.text.List(16f)
                pdfList.setListSymbol(Chunk("\u2022", Font(font.family, 18f)).setTextRise(-3f))

                for (item in section.items) {
                    val listItem = ListItem(item.text, font)
                    listItem.spacingAfter = 4f
                    pdfList.add(listItem)
                }

                document.add(pdfList)
                document.add(Paragraph("\n", font))
            }
        }
    }


    val pdfPTable = PdfPTable(4)
    pdfPTable.widthPercentage = 100f

    //Create cells
    val pdfPCell1 = PdfPCell(Paragraph("Cell 1", font))
    val pdfPCell2 = PdfPCell(Paragraph("Cell 2", font))
    val pdfPCell3 = PdfPCell(Paragraph("Cell 3", font))
    val pdfPCell4 = PdfPCell(Paragraph("Cell 4", font))

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
