package io.github.kamilperczynski.adocparser.stylesheet

import com.lowagie.text.Font
import com.lowagie.text.ListItem
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import io.github.kamilperczynski.adocparser.ast.*

interface AdocStylesheet {

    fun registerFonts()

    val baseFont: Font

    fun styleParagraph(paragraph: Paragraph, adocParagraph: AdocParagraph)

    fun styleListItem(listItem: ListItem, item: AdocListItem, nestingLevel: Int)

    fun styleHeader(paragraph: Paragraph, adocHeader: AdocHeader)

    fun styleSectionTitle(pdfParagraph: Paragraph, node: AdocSectionTitle)

    fun styleAdmonition(heading: Paragraph, content: Paragraph, adocAdmonition: AdocAdmonition)

    fun styleList(pdfList: PdfList, adocList: AdocList, nestingLevel: Int)

    fun styleListWrapper(pdfParagraph: Paragraph, adocList: AdocList)

    fun styleTable(table: PdfPTable, section: AdocTable, section1: AdocSection)

}

typealias PdfList = com.lowagie.text.List
