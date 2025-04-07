package io.github.kamilperczynski.adocparser.stylesheet

import com.lowagie.text.FontFactory
import com.lowagie.text.pdf.BaseFont
import java.util.concurrent.atomic.AtomicLong

class HashmapFontsCache : FontsCache {

    private val cache = mutableMapOf<String, CacheEntry>()
    private val misses: AtomicLong = AtomicLong(0)

    override fun registerFont(fontName: String, fontPath: String) {
        FontFactory.register(fontPath, fontName)

        cache[fontName] = CacheEntry(
            BaseFont.createFont(fontPath, BaseFont.WINANSI, true, true, null, null, true),
            AtomicLong(0)
        )
    }

    override fun getFont(fontName: String?): BaseFont? {
        if (fontName == null) {
            return null
        }

        val cacheEntry = cache[fontName]

        if (cacheEntry == null) {
            misses.incrementAndGet()
            return null
        }

        cacheEntry.hits.incrementAndGet()
        return cacheEntry.baseFont

    }

    override fun hits(): Long {
        return cache.values.stream()
            .mapToLong { it.hits.get() }
            .sum()
    }

    override fun misses(): Long {
        return misses.get()
    }
}

data class CacheEntry(val baseFont: BaseFont, var hits: AtomicLong)
