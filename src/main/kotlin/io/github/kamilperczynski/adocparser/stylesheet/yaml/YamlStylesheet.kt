package io.github.kamilperczynski.adocparser.stylesheet.yaml


data class YamlStylesheet(
    var font: YamlFontCatalog = YamlFontCatalog(),
    var base: YamlFontProps? = null,
    var sectionTitle: YamlFontProps? = null,
    var heading: YamlHeadingProperties = YamlHeadingProperties(),
)

data class YamlFontCatalog(
    /**
     * Map of font families to their paths
     */
    var catalog: Map<String, YamlFontCatalogItem> = mutableMapOf()
)

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
    var textAlign: YamlTextAlign? = null
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
            textAlign = props.textAlign ?: textAlign
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
    var font: YamlFontProps? = null,
    var h1: YamlFontProps? = null,
    var h2: YamlFontProps? = null,
    var h3: YamlFontProps? = null,
    var h4: YamlFontProps? = null,
    var h5: YamlFontProps? = null,
    var h6: YamlFontProps? = null
)
