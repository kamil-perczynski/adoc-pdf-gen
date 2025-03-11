package io.github.kamilperczynski.adocparser.pdf

interface ChapterCounter {

    val chapterCounter: Int
    fun nextChapterNumber(): Int

}
