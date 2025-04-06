package io.github.kamilperczynski.adocparser.pdf

import com.lowagie.text.Document
import com.lowagie.text.List
import com.lowagie.text.ListItem
import com.lowagie.text.Paragraph
import io.github.kamilperczynski.adocparser.ast.AdocList
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import java.util.*

class PdfListPrinter(
    private val document: Document,
    private val stylesheet: AdocStylesheet,
    private val pdfParagraphPrinter: PdfParagraphPrinter
) {

    fun printList(adocList: AdocList) {
        val rootList = nextList(stylesheet, adocList, 1)

        val listsStack = ListsStack(rootList)

        for (item in adocList.items) {
            val nestedList =
                if (item.level > listsStack.currentLevel)
                    listsStack.addUntil(item.level) { nestingLevel ->
                        nextList(stylesheet, adocList, nestingLevel)
                    }
                else
                    listsStack.popUntil(item.level)

            val listItem = ListItem()
            stylesheet.styleListItem(listItem, item, item.level)

            pdfParagraphPrinter.printPhraseChunks(item.paragraph.chunks, listItem)
            nestedList.add(listItem)
        }

        listsStack.popUntil(1)

        val paragraph = Paragraph()
        stylesheet.styleListWrapper(paragraph, adocList)

        paragraph.add(rootList)
        document.add(paragraph)
    }

    private fun nextList(stylesheet: AdocStylesheet, list: AdocList, level: Int): List {
        val pdfList = List()
        stylesheet.styleList(pdfList, list, level)
        return pdfList
    }
}

class ListsStack(rootList: List) {
    private val stack = LinkedList<Pair<Int, List>>()
        .also { it.add(1 to rootList) }

    val currentLevel
        get() = stack.peekFirst().first

    fun addUntil(level: Int, fn: (Int) -> List): List {
        val currentLevel = stack.peekFirst().first

        if (currentLevel == level) {
            return stack.peekFirst().second
        }

        for (i in currentLevel until level) {
            stack.addFirst(i + 1 to fn(i + 1))
        }

        return stack.peekFirst().second
    }

    fun popUntil(level: Int): List {
        val currentLevel = stack.peekFirst()

        if (currentLevel.first == level) {
            return currentLevel.second
        }

        for (i in currentLevel.first downTo level + 1) {
            val lastEl = stack.removeFirst()

            stack.peekFirst().second.add(lastEl.second)
        }

        return stack.peekFirst().second
    }

}
