package io.github.kamilperczynski.adocparser

import org.antlr.v4.runtime.CharStream
import java.util.LinkedList

enum class BlockParsingCtx {

    INSTANCE;

    private var stack: LinkedList<String> = LinkedList()

    fun blockStart(horizontalRule: Int, _input: CharStream, text: String): Boolean {
        println("opening block: $text")
        stack.addFirst(text)

        return true
    }

    fun blockEnd(horizontalRule: Int, _input: CharStream, text: String): Boolean {
        val currContext = stack.firstOrNull()

        val closingTagMatch = text == currContext

        if (closingTagMatch) {
            stack.removeFirst()
        }

        return closingTagMatch
    }

}
