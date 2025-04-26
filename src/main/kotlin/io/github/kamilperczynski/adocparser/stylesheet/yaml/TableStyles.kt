package io.github.kamilperczynski.adocparser.stylesheet.yaml

internal val TABLE_PROPS_FALLBACK = YamlTableProps().copy(
    widthPercentage = 80f,
    spacingBefore = ".5rem",
    spacingAfter = ".5rem",
    keepTogether = false,
    horizontalAlignment = YamlTextAlign.left,
    widths = listOf(1)
)
