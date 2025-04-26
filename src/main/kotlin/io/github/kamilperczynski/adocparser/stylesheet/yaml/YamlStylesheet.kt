package io.github.kamilperczynski.adocparser.stylesheet.yaml


data class YamlStylesheet(
    var font: YamlFontCatalog = YamlFontCatalog(),
    var base: YamlFontProps? = null,
    var sectionTitle: YamlParagraphProps = YamlParagraphProps(),
    var heading: YamlHeadingProperties = YamlHeadingProperties(),
    var paragraph: YamlParagraphProps = YamlParagraphProps(),
    var admonition: YamlAdmonitionProperties = YamlAdmonitionProperties(),
    var list: YamlListsProps = YamlListsProps(),
    var table: YamlTableProps = YamlTableProps(),
)

data class YamlTableProps(
    var font: YamlFontProps? = YamlFontProps(),
    var horizontalAlignment: YamlTextAlign? = null,
    var widthPercentage: Float? = null,
    var spacingBefore: String? = null,
    var spacingAfter: String? = null,
    var keepTogether: Boolean? = null,
    var widths: List<Int>? = null
) {
    fun merge(other: YamlTableProps?): YamlTableProps {
        if (other == null) {
            return this
        }

        return YamlTableProps(
            font = other.font ?: font,
            widthPercentage = other.widthPercentage ?: widthPercentage,
            spacingBefore = other.spacingBefore ?: spacingBefore,
            spacingAfter = other.spacingAfter ?: spacingAfter,
            keepTogether = other.keepTogether ?: keepTogether,
            widths = other.widths ?: widths
        )
    }
}

data class YamlFontCatalog(
    /**
     * Map of font families to their paths
     */
    var catalog: Map<String, YamlFontCatalogItem> = mutableMapOf()
)

data class YamlListsProps(
    var paragraph: YamlParagraphProps = YamlParagraphProps(),
    var defaults: YamlListProps = YamlListProps(),
    var level1: YamlListProps = YamlListProps(),
    var level2: YamlListProps = YamlListProps(),
    var level3: YamlListProps = YamlListProps(),
    var level4: YamlListProps = YamlListProps()
)

data class YamlListProps(
    var paragraph: YamlParagraphProps = YamlParagraphProps(),
    var listSymbol: String? = null,
    var numbered: Boolean? = null,
    var lettered: Boolean? = null,
    var lowercased: Boolean? = null,
    var autoIndented: Boolean? = null,
    var symbolIndent: String? = null,
    var indentationLeft: String? = null,
    var indentationRight: String? = null
) {
    fun merge(other: YamlListProps?): YamlListProps {
        if (other == null) {
            return this
        }

        return YamlListProps(
            paragraph = other.paragraph.merge(paragraph),
            listSymbol = other.listSymbol ?: listSymbol,
            numbered = other.numbered ?: numbered,
            lettered = other.lettered ?: lettered,
            lowercased = other.lowercased ?: lowercased,
            autoIndented = other.autoIndented ?: autoIndented,
            symbolIndent = other.symbolIndent ?: symbolIndent,
            indentationLeft = other.indentationLeft ?: indentationLeft,
            indentationRight = other.indentationRight ?: indentationRight
        )
    }
}

data class YamlFontCatalogItem(
    var normal: String? = null,
    var bold: String? = null,
    var italic: String? = null,
    var boldItalic: String? = null
)

data class YamlFontProps(
    var fontFamily: String? = null,
    var fontSize: String? = null,
    var fontColor: String? = null,
    var fontStyle: YamlFontStyle? = null,
    var textDecoration: YamlTextDecoration? = null,
) {
    fun merge(props: YamlFontProps?): YamlFontProps {
        if (props == null) {
            return this
        }

        return YamlFontProps(
            fontFamily = props.fontFamily ?: fontFamily,
            fontSize = props.fontSize ?: fontSize,
            fontColor = props.fontColor ?: fontColor,
            fontStyle = props.fontStyle ?: fontStyle,
            textDecoration = props.textDecoration ?: textDecoration,
        )
    }
}

enum class YamlTextAlign(var value: String) {
    left("left"),
    center("center"),
    right("right"),
    justify("justify")
}

enum class YamlTextDecoration(var value: String) {
    none("none"),
    underline("underline"),
    linethrough("line-through"),
    overline("overline"),
}

enum class YamlFontStyle(var value: String) {
    normal("normal"),
    bold("bold"),
    italic("italic"),
    boldItalic("boldItalic")
}

data class YamlHeadingProperties(
    var defaults: YamlParagraphProps = YamlParagraphProps(),
    var h1: YamlParagraphProps = YamlParagraphProps(),
    var h2: YamlParagraphProps = YamlParagraphProps(),
    var h3: YamlParagraphProps = YamlParagraphProps(),
    var h4: YamlParagraphProps = YamlParagraphProps(),
    var h5: YamlParagraphProps = YamlParagraphProps(),
    var h6: YamlParagraphProps = YamlParagraphProps()
)

data class YamlAdmonitionProperties(
    var text: YamlParagraphProps? = null,
    var heading: YamlParagraphProps? = null,
)

data class YamlParagraphProps(
    var font: YamlFontProps? = null,
    var lineHeight: String? = null,
    var spacingBefore: String? = null,
    var spacingAfter: String? = null,
    var firstLineIndent: String? = null,
    var textAlign: YamlTextAlign? = null
) {
    fun merge(paragraph: YamlParagraphProps?): YamlParagraphProps {
        if (paragraph == null) {
            return this
        }

        return YamlParagraphProps(
            font = paragraph.font ?: font,
            lineHeight = paragraph.lineHeight ?: lineHeight,
            spacingBefore = paragraph.spacingBefore ?: spacingBefore,
            spacingAfter = paragraph.spacingAfter ?: spacingAfter,
            firstLineIndent = paragraph.firstLineIndent ?: firstLineIndent,
            textAlign = paragraph.textAlign ?: textAlign
        )
    }
}

fun parseUnit(value: String?, baseFontSize: Float): Float? {
    if (value == null) {
        return null
    }

    try {
        val trimmed = value.trim()

        if (trimmed.endsWith("rem")) {
            val digits = trimmed.substringBefore("rem")
            return digits.toFloat() * baseFontSize
        }

        return trimmed.toFloat()
    } catch (e: NumberFormatException) {
        throw IllegalArgumentException(
            "Invalid value for font size: $value. Only floats and rems are allowed",
            e
        )
    }
}
