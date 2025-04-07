package io.github.kamilperczynski.adocparser.stylesheet.yaml

import com.lowagie.text.Font
import com.lowagie.text.FontFactory
import com.lowagie.text.pdf.BaseFont
import io.github.kamilperczynski.adocparser.stylesheet.FontsCache

internal const val DEFAULT_BASE_FONT_SIZE = 12f

internal val FONT_FALLBACK = YamlFontProps(
    fontFamily = "times-roman",
    fontSize = "12",
    fontColor = "#000000",
    fontStyle = YamlFontStyle.normal,
    textDecoration = YamlTextDecoration.none
)

internal val PARAGRAPH_FALLBACK = YamlParagraphProps(
    font = null,
    textAlign = YamlTextAlign.left,
    spacingAfter = "1rem",
    spacingBefore = "1rem",
    firstLineIndent = "0",
    lineHeight = "1.25rem"
)

internal fun toFont(fontProps: YamlFontProps,
                    baseFontSize: Float = DEFAULT_BASE_FONT_SIZE,
                    fontsCache: FontsCache): Font {
    val familyIndex = Font.getFamilyIndex(fontProps.fontFamily ?: FontFactory.TIMES_ROMAN)

    if (familyIndex != Font.UNDEFINED) {
        return Font(
            familyIndex,
            parseUnit(fontProps.fontSize, baseFontSize) ?: baseFontSize,
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

    val cachedBaseFont = fontsCache.getFont(fontName)
    if (cachedBaseFont != null) {
        return Font(
            cachedBaseFont,
            parseUnit(fontProps.fontSize, baseFontSize) ?: baseFontSize,
            fontStyle,
            toColor(fontProps.fontColor)
        )
    }

    return FontFactory.getFont(
        fontName,
        BaseFont.WINANSI,
        true,
        parseUnit(fontProps.fontSize, baseFontSize) ?: baseFontSize,
        fontStyle,
        toColor(fontProps.fontColor)
    )
}

internal fun toFontStyle(fontProps: YamlFontProps): Int? {
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
