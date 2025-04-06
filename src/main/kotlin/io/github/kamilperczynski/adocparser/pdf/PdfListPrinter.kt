package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.*
import com.lowagie.text.List
import io.github.kamilperczynski.adocparser.ast.AdocList
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet

class PdfListPrinter(
    private val document: Document,
    private val stylesheet: AdocStylesheet,
    private val pdfParagraphPrinter: PdfParagraphPrinter
) {

    fun printList(node: AdocList) {
        val rootList = nextList()

        val listsIdx = arrayOfNulls<List>(20)
        listsIdx[0] = rootList

        for ((idx, item) in node.items.withIndex()) {
            val list = provideNestedList(listsIdx, item.level)

            val listItem = ListItem()
            stylesheet.styleListItem(listItem, item, idx)

            pdfParagraphPrinter.printPhraseChunks(item.paragraph.chunks, listItem)
            list.add(listItem)
        }

        // TODO: should this be styled with custom rule?
        val paragraph = Paragraph()
        paragraph.add(rootList)
        paragraph.spacingBefore = stylesheet.baseFont.size * 0.75f
        paragraph.spacingAfter = stylesheet.baseFont.size * 0.75f

        document.add(paragraph)
    }

    private fun provideNestedList(listsIdx: Array<List?>, itemLevel: Int): List {
        val neededList = listsIdx[itemLevel - 1]

        if (neededList != null) {
            return neededList
        }

        val parentList = provideNestedList(listsIdx, itemLevel - 1)

        val nestedList = nextList()
        parentList.add(nestedList)

        listsIdx[itemLevel] = nestedList

        return nestedList
    }

    private fun nextList(): List {
        val pdfList = List()
        pdfList.isAutoindent = false
        pdfList.symbolIndent = stylesheet.baseFont.size

        pdfList.setListSymbol(Chunk("\u2022", stylesheet.baseFont))
        return pdfList
    }
}
