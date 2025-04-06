package io.github.kamilperczynski.adocparser.stylesheet.yaml

import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.ListItem
import com.lowagie.text.Paragraph
import io.github.kamilperczynski.adocparser.ast.*
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.yaml.YamlTextAlign.*
import java.nio.file.Path
import java.nio.file.Paths

class YamlAdocStylesheet(
    private val fontsDir: Path = Paths.get("."),
    private val yamlStylesheet: YamlStylesheet
) : AdocStylesheet {

    private val baseFontProps = FONT_FALLBACK.merge(yamlStylesheet.base)

    override fun registerFonts() {
        for (entry in yamlStylesheet.font.catalog) {
            val fontFamily = entry.key
            val fonts = entry.value

            registerFont(fonts, fontFamily)
        }
    }

    override val baseFont: Font
        get() = toFont(baseFontProps)

    override fun styleParagraph(paragraph: Paragraph, adocParagraph: AdocParagraph) {
        applyParagraphStyles(
            paragraph,
            baseFontProps.merge(yamlStylesheet.paragraph.font),
            PARAGRAPH_FALLBACK.merge(yamlStylesheet.paragraph)
        )
    }

    override fun styleSectionTitle(pdfParagraph: Paragraph, node: AdocSectionTitle) {
        applyParagraphStyles(
            pdfParagraph,
            baseFontProps
                .merge(yamlStylesheet.paragraph.font)
                .merge(yamlStylesheet.sectionTitle.font),
            PARAGRAPH_FALLBACK
                .merge(yamlStylesheet.paragraph)
                .merge(yamlStylesheet.sectionTitle)
        )
    }

    private fun applyParagraphStyles(
        pdfParagraph: Paragraph,
        paragraphFont: YamlFontProps,
        paragraphProps: YamlParagraphProps
    ) {

        pdfParagraph.font = toFont(paragraphFont, baseFont.size)
        pdfParagraph.leading = paragraphProps.lineHeight?.let { parseUnit(it, baseFont.size) }!!
        pdfParagraph.spacingBefore = paragraphProps.spacingBefore?.let { parseUnit(it, baseFont.size) }!!
        pdfParagraph.spacingAfter = paragraphProps.spacingAfter?.let { parseUnit(it, baseFont.size) }!!
        pdfParagraph.alignment = toTextAlign(paragraphProps.textAlign)
    }

    override fun styleAdmonition(heading: Paragraph, content: Paragraph, adocAdmonition: AdocAdmonition) {
        TODO("Not yet implemented")
    }

    override fun styleHeader(paragraph: Paragraph, adocHeader: AdocHeader) {
        val headerProps = toHeaderProps(adocHeader.level, yamlStylesheet.heading)

        applyParagraphStyles(
            paragraph,
            baseFontProps
                .merge(yamlStylesheet.paragraph.font)
                .merge(yamlStylesheet.heading.defaults.font)
                .merge(headerProps?.font),
            PARAGRAPH_FALLBACK
                .merge(yamlStylesheet.paragraph)
                .merge(yamlStylesheet.heading.defaults)
                .merge(headerProps)
        )
    }

    override fun styleListItem(listItem: ListItem, item: AdocListItem, idx: Int) {
        applyParagraphStyles(
            listItem,
            baseFontProps
                .merge(yamlStylesheet.paragraph.font)
                .merge(yamlStylesheet.listItem.font),
            PARAGRAPH_FALLBACK
                .merge(yamlStylesheet.paragraph)
                .merge(yamlStylesheet.listItem)
        )
    }

    private fun registerFont(fonts: YamlFontCatalogItem, fontFamily: String) {
        fonts.bold?.let {
            FontFactory.register(
                fontsDir.resolve(fontFamily).resolve(it).toString(),
                "${fontFamily.lowercase()}-bold",
            )
        }
        fonts.italic?.let {
            FontFactory.register(
                fontsDir.resolve(fontFamily).resolve(it).toString(),
                "${fontFamily.lowercase()}-italic",
            )
        }
        fonts.boldItalic?.let {
            FontFactory.register(
                fontsDir.resolve(fontFamily).resolve(it).toString(),
                "${fontFamily.lowercase()}-bolditalic",
            )
        }
        fonts.normal?.let {
            FontFactory.register(
                fontsDir.resolve(fontFamily).resolve(it).toString(),
                fontFamily,
            )
        }
    }
}


private fun toHeaderProps(headerLevel: Int, headingProps: YamlHeadingProperties): YamlParagraphProps? {
    return when (headerLevel) {
        1 -> headingProps.h1
        2 -> headingProps.h2
        3 -> headingProps.h3
        4 -> headingProps.h4
        5 -> headingProps.h5
        else -> null

    }
}

private fun toTextAlign(textAlign: YamlTextAlign?): Int {
    return when (textAlign) {
        left -> Paragraph.ALIGN_LEFT
        center -> Paragraph.ALIGN_CENTER
        right -> Paragraph.ALIGN_RIGHT
        justify -> Paragraph.ALIGN_JUSTIFIED
        else -> Paragraph.ALIGN_LEFT
    }
}
