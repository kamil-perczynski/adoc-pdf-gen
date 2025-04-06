package io.github.kamilperczynski.adocparser.stylesheet

import com.lowagie.text.Font
import com.lowagie.text.ListItem
import com.lowagie.text.Paragraph
import io.github.kamilperczynski.adocparser.ast.*

interface AdocStylesheet {

    fun registerFonts()

    val baseFont: Font

    fun styleParagraph(paragraph: Paragraph, adocParagraph: AdocParagraph)

    fun styleListItem(listItem: ListItem, item: AdocListItem, idx: Int)

    fun styleHeader(paragraph: Paragraph, adocHeader: AdocHeader)

    fun styleSectionTitle(pdfParagraph: Paragraph, node: AdocSectionTitle)

    fun styleAdmonition(heading: Paragraph, content: Paragraph, adocAdmonition: AdocAdmonition)

}
