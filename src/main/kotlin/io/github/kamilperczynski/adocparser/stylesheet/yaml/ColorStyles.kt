package io.github.kamilperczynski.adocparser.stylesheet.yaml

import java.awt.Color

internal fun toColor(hexColor: String?): Color? {
    if (hexColor == null) {
        return null
    }

    val r = Integer.valueOf(hexColor.substring(1, 3), 16)
    val g = Integer.valueOf(hexColor.substring(3, 5), 16)
    val b = Integer.valueOf(hexColor.substring(5, 7), 16)

    return Color(r, g, b)
}
