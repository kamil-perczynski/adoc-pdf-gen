package io.github.kamilperczynski.adocparser.stylesheet

import com.lowagie.text.pdf.BaseFont

interface FontsCache {

    fun registerFont(fontName: String, fontPath: String)

    fun getFont(fontName: String?): BaseFont?

    fun hits(): Long

    fun misses(): Long

}
