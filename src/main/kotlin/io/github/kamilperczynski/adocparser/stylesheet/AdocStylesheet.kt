package io.github.kamilperczynski.adocparser.stylesheet

import com.lowagie.text.Font
import com.lowagie.text.Paragraph
import io.github.kamilperczynski.adocparser.ast.AdocHeader
import io.github.kamilperczynski.adocparser.ast.AdocParagraph
import io.github.kamilperczynski.adocparser.ast.AdocSectionTitle

interface AdocStylesheet {

    fun registerFonts()

    val baseFont: Font

    fun styleParagraph(paragraph: Paragraph, adocParagraph: AdocParagraph)

    fun styleHeader(paragraph: Paragraph, adocHeader: AdocHeader)

    fun styleSectionTitle(pdfParagraph: Paragraph, node: AdocSectionTitle)

}
