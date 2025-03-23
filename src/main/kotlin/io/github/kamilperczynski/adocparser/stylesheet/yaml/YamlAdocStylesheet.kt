package io.github.kamilperczynski.adocparser.stylesheet.yaml

import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.Paragraph
import com.lowagie.text.pdf.BaseFont
import io.github.kamilperczynski.adocparser.ast.AdocHeader
import io.github.kamilperczynski.adocparser.ast.AdocParagraph
import io.github.kamilperczynski.adocparser.ast.AdocSectionTitle
import io.github.kamilperczynski.adocparser.stylesheet.AdocStylesheet
import java.awt.Color
import java.nio.file.Path
import java.nio.file.Paths

private const val DEFAULT_BASE_FONT_SIZE = 12f

private val FONT_FALLBACK = YamlFontProps(
    fontFamily = "times-roman",
    fontSize = "12",
    fontColor = "#000000",
    fontStyle = YamlFontStyle.normal,
    textDecoration = YamlTextDecoration.none
)

class YamlAdocStylesheet(
    private val fontsDir: Path = Paths.get("."),
    private val yamlStylesheet: YamlStylesheet
) : AdocStylesheet {

    override fun registerFonts() {
        for (entry in yamlStylesheet.font.catalog) {
            val fontFamily = entry.key
            val fonts = entry.value

            registerFont(fonts, fontFamily)
        }
    }

    override val baseFont: Font
        get() {
            val fontProps = FONT_FALLBACK.merge(yamlStylesheet.base)
            return toFont(fontProps)
        }

    override fun styleParagraph(paragraph: Paragraph, adocParagraph: AdocParagraph) {
        paragraph.font = baseFont

        paragraph.multipliedLeading = 1.25f
        paragraph.spacingBefore = baseFont.size * .5f
        paragraph.spacingAfter = baseFont.size * .75f
    }

    override fun styleSectionTitle(pdfParagraph: Paragraph, node: AdocSectionTitle) {
        val fontProps = FONT_FALLBACK
            .merge(yamlStylesheet.base)
            .merge(yamlStylesheet.sectionTitle)

        pdfParagraph.font = toFont(fontProps)
        pdfParagraph.spacingAfter = baseFont.size * 0.25f
    }

    override fun styleHeader(paragraph: Paragraph, adocHeader: AdocHeader) {
        val fontProps = FONT_FALLBACK
            .merge(yamlStylesheet.base)
            .merge(yamlStylesheet.heading.font)
            .merge(
                toHeaderFontProps(adocHeader.level, yamlStylesheet.heading)
            )

        paragraph.font = toFont(fontProps)
        paragraph.multipliedLeading = 1.5f
        paragraph.spacingAfter = baseFont.size * 1.5f
        paragraph.spacingBefore = baseFont.size
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

private fun toFont(fontProps: YamlFontProps): Font {
    val familyIndex = Font.getFamilyIndex(fontProps.fontFamily ?: FontFactory.TIMES_ROMAN)

    if (familyIndex != Font.UNDEFINED) {
        return Font(
            familyIndex,
            fontProps.fontSize?.toFloat() ?: DEFAULT_BASE_FONT_SIZE,
            toFontStyle(fontProps) ?: Font.NORMAL,
            toColor(fontProps.fontColor)
        )
    }

    val fontName = when (fontProps.fontStyle ?: YamlFontStyle.normal) {
        YamlFontStyle.bold -> fontProps.fontFamily + "-bold"
        YamlFontStyle.italic -> fontProps.fontFamily + "-italic"
        YamlFontStyle.boldItalic -> fontProps.fontFamily + "-bolditalic"
        else -> fontProps.fontFamily
    }

    val fontStyle = if (FontFactory.isRegistered(fontName))
        Font.NORMAL
    else
        toFontStyle(fontProps) ?: Font.NORMAL

    return FontFactory.getFont(
        fontName,
        BaseFont.WINANSI,
        true,
        fontProps.fontSize?.toFloat() ?: DEFAULT_BASE_FONT_SIZE,
        fontStyle,
        toColor(fontProps.fontColor)
    )
}

fun toFontStyle(fontProps: YamlFontProps): Int? {
    if (fontProps.fontStyle == null) {
        return null
    }

    var style: Int = Font.NORMAL

    if (fontProps.fontStyle == YamlFontStyle.bold) {
        style = style or Font.BOLD
    }
    if (fontProps.fontStyle == YamlFontStyle.italic) {
        style = style or Font.ITALIC
    }
    if (fontProps.fontStyle == YamlFontStyle.boldItalic) {
        style = style or Font.BOLDITALIC
    }
    if (fontProps.textDecoration == YamlTextDecoration.underline) {
        style = style or Font.UNDERLINE
    }
    if (fontProps.textDecoration == YamlTextDecoration.linethrough) {
        style = style or Font.STRIKETHRU
    }

    return style
}

fun toColor(hexColor: String?): Color? {
    if (hexColor == null) {
        return null
    }

    val r = Integer.valueOf(hexColor.substring(1, 3), 16)
    val g = Integer.valueOf(hexColor.substring(3, 5), 16)
    val b = Integer.valueOf(hexColor.substring(5, 7), 16)

    return Color(r, g, b)
}

private fun toHeaderFontProps(headerLevel: Int, headingProps: YamlHeadingProperties): YamlFontProps? {
    return when (headerLevel) {
        1 -> headingProps.h1
        2 -> headingProps.h2
        3 -> headingProps.h3
        4 -> headingProps.h4
        5 -> headingProps.h5
        else -> null

    }
}
