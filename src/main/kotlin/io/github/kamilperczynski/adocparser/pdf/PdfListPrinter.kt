package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Chunk
import com.lowagie.text.Document
import com.lowagie.text.Font
import com.lowagie.text.ListItem
import io.github.kamilperczynski.adocparser.ast.AdocList

class PdfListPrinter(
    private val document: Document,
    private val baseFont: Font,
    private val pdfParagraphPrinter: PdfParagraphPrinter
) {

    fun printList(node: AdocList) {
        // `List`? who in the world names classes like this?
        val pdfList = com.lowagie.text.List(16f)
        pdfList.setListSymbol(Chunk("\u2022", baseFont))
        pdfList.indentationLeft = baseFont.size * .5f

        for ((idx, item) in node.items.withIndex()) {
            val listItem = ListItem()
            listItem.font = baseFont

            if (idx == 0) {
                listItem.spacingBefore = baseFont.size * .25f
            }

            listItem.spacingAfter = baseFont.size * .25f

            pdfParagraphPrinter.printPhraseChunks(item.paragraph.chunks, listItem)
            pdfList.add(listItem)
        }

        document.add(pdfList)
    }
}
