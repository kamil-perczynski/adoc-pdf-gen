package io.github.kamilperczynski.adocparser.stylesheet.yaml

import com.lowagie.text.Chunk
import com.lowagie.text.Element.*
import com.lowagie.text.ListItem
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.PdfPTable
import io.github.kamilperczynski.adocparser.ast.*
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import io.github.kamilperczynski.adocparser.stylesheet.FontsCache
import io.github.kamilperczynski.adocparser.stylesheet.PdfList
import io.github.kamilperczynski.adocparser.stylesheet.yaml.YamlFontStyle.*
import io.github.kamilperczynski.adocparser.stylesheet.yaml.YamlTextAlign.*
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.LazyThreadSafetyMode.NONE

class YamlAdocStylesheet(
    private val fontsDir: Path = Paths.get("."),
    private val yamlStylesheet: YamlStylesheet,
    private val fontsCache: FontsCache
) : AdocStylesheet {

    private val baseFontProps = FONT_FALLBACK.merge(yamlStylesheet.base)

    override fun styleTable(table: PdfPTable, adocTable: AdocTable, section: AdocSection) {
        val tableProps = TABLE_PROPS_FALLBACK
            .merge(yamlStylesheet.table)

        table.horizontalAlignment = toTableHorizontalAlignment(tableProps)
        table.widthPercentage = tableProps.widthPercentage!!

        tableProps.spacingBefore
            ?.let { parseUnit(it, baseFont.size) }
            ?.let { table.setSpacingBefore(it) }

        tableProps.spacingAfter
            ?.let { parseUnit(it, baseFont.size) }
            ?.let { table.setSpacingAfter(it) }

        val colWidthsProps = adocTable.colWidths ?: tableProps.widths ?: listOf(1)

        val colWidths = IntArray(adocTable.colsCount) { 1 }
            .mapIndexed { idx, _ -> colWidthsProps[idx % colWidthsProps.size] }
            .toIntArray()
        table.setWidths(colWidths)
    }

    override fun registerFonts() {
        for (entry in yamlStylesheet.font.catalog) {
            val fontFamily = entry.key
            val fonts = entry.value

            registerFontFamily(fontFamily, fonts)
        }
    }

    override val baseFont by lazy(NONE) {
        toFont(fontProps = baseFontProps, fontsCache = fontsCache)
    }

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
        pdfParagraph.font = toFont(paragraphFont, baseFont.size, fontsCache)
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

    override fun styleListItem(listItem: ListItem, item: AdocListItem, nestingLevel: Int) {
        val paragraphProps = PARAGRAPH_FALLBACK
            .merge(yamlStylesheet.paragraph)
            .merge(yamlStylesheet.list.defaults.paragraph)
            .merge(
                when (nestingLevel) {
                    1 -> yamlStylesheet.list.level1.paragraph
                    2 -> yamlStylesheet.list.level2.paragraph
                    3 -> yamlStylesheet.list.level3.paragraph
                    4 -> yamlStylesheet.list.level4.paragraph
                    else -> yamlStylesheet.list.level4.paragraph
                }
            )

        applyParagraphStyles(
            listItem,
            FONT_FALLBACK
                .merge(yamlStylesheet.base)
                .merge(yamlStylesheet.paragraph.font)
                .merge(yamlStylesheet.list.defaults.paragraph.font)
                .merge(paragraphProps.font),
            paragraphProps
        )
    }

    override fun styleList(pdfList: PdfList, adocList: AdocList, nestingLevel: Int) {
        val firstItem = adocList.items.first()

        val listProps = LIST_FALLBACK
            .merge(yamlStylesheet.list.defaults)
            .merge(
                when (nestingLevel) {
                    1 -> yamlStylesheet.list.level1
                    2 -> yamlStylesheet.list.level2
                    3 -> yamlStylesheet.list.level3
                    4 -> yamlStylesheet.list.level4
                    else -> yamlStylesheet.list.level4
                }
            )

        val fontProps = FONT_FALLBACK
            .merge(yamlStylesheet.base)
            .merge(yamlStylesheet.paragraph.font)
            .merge(yamlStylesheet.list.defaults.paragraph.font)
            .merge(listProps.paragraph.font)

        pdfList.isAutoindent = true
        pdfList.isLettered = listProps.lettered ?: firstItem.lettered
        pdfList.isLowercase = listProps.lowercased ?: firstItem.lowercased
        pdfList.isNumbered = listProps.numbered ?: firstItem.numbered

        pdfList.setListSymbol(Chunk(listProps.listSymbol, toFont(fontProps, fontsCache = fontsCache)))

        pdfList.indentationLeft = listProps.indentationLeft?.let { parseUnit(it, baseFont.size) }
            ?: pdfList.indentationLeft

        pdfList.indentationRight = listProps.indentationRight?.let { parseUnit(it, baseFont.size) }
            ?: pdfList.indentationRight

        pdfList.symbolIndent = listProps.symbolIndent?.let { parseUnit(it, baseFont.size) }
            ?: pdfList.symbolIndent
    }

    override fun styleListWrapper(pdfParagraph: Paragraph, adocList: AdocList) {
        applyParagraphStyles(
            pdfParagraph,
            FONT_FALLBACK
                .merge(yamlStylesheet.paragraph.font)
                .merge(yamlStylesheet.list.defaults.paragraph.font),
            PARAGRAPH_FALLBACK
                .merge(yamlStylesheet.paragraph)
                .merge(yamlStylesheet.list.defaults.paragraph)
        )
    }

    private fun registerFontFamily(fontFamily: String, fonts: YamlFontCatalogItem) {
        fonts.bold?.let { fontFile ->
            registerFontFamily(fontFamily, bold, fontFile)
        }
        fonts.italic?.let { fontFile ->
            registerFontFamily(fontFamily, italic, fontFile)
        }
        fonts.boldItalic?.let { fontFile ->
            registerFontFamily(fontFamily, boldItalic, fontFile)
        }
        fonts.normal?.let { fontFile ->
            registerFontFamily(fontFamily, normal, fontFile)
        }
    }

    private fun registerFontFamily(fontFamily: String, fontStyle: YamlFontStyle, fontFile: String) {
        val fontName = if (fontStyle == normal)
            fontFamily.lowercase()
        else
            "${fontFamily.lowercase()}-${fontStyle.value}"

        val fontPath = fontsDir.resolve(fontFamily).resolve(fontFile)
        fontsCache.registerFont(fontName, fontPath.toString())
    }
}

private fun toTableHorizontalAlignment(tableProps: YamlTableProps): Int {
    return when (tableProps.horizontalAlignment) {
        left -> ALIGN_LEFT
        center -> ALIGN_CENTER
        right -> ALIGN_RIGHT
        justify -> ALIGN_JUSTIFIED
        else -> ALIGN_LEFT
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
        left -> ALIGN_LEFT
        center -> ALIGN_CENTER
        right -> ALIGN_RIGHT
        justify -> ALIGN_JUSTIFIED
        else -> ALIGN_LEFT
    }
}
