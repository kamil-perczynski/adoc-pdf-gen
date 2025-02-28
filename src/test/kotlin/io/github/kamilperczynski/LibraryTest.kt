package io.github.kamilperczynski

import com.lowagie.text.Document
import com.lowagie.text.FontFactory
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPCell
import com.lowagie.text.pdf.PdfPTable
import com.lowagie.text.pdf.PdfWriter
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.nio.file.Paths


class LibraryTest {

    @Test
    fun test() {
        // generate pdf  with itext with Hello world header and lorem ipsum paragraph
        val file = Paths.get("./LibraryTest-test.pdf")

        if (Files.exists(file)) {
            Files.delete(file)
        }


        val document = Document()
        PdfWriter.getInstance(document, Files.newOutputStream(file))
        document.open()

        // Left
        Paragraph(
            """
            Lorem ipsum dolor sit amet, consectetur adipiscing elit.
            Nullam nec purus nec nunc ultricies tincidunt.
            
        """.trimIndent()
        )

        FontFactory.registerDirectory("/Users/kperczynski/fonties/Ubuntu")

        val font = FontFactory.getFont("ubuntu-regular", 10f)
        val p1 = Paragraph("Hello World", font)
        document.add(p1)
        document.add(p1)


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

}
