package io.github.kamilperczynski.adocparser.ast

import io.github.kamilperczynski.adocparser.AsciidocParser.*
import org.antlr.v4.runtime.tree.TerminalNode

fun parseAdocParams(context: ParamsContext?): AdocParams {
    if (context == null) {
        return EMPTY_ADOC_PARAMS
    }

    return AdocParamsParser(context).parse()
}

class AdocParamsParser(context: ParamsContext) {

    private var pos = 0

    private val tokens = context.children.subList(1, context.children.size - 1)

    private val positionalParams: MutableMap<Int, String> = mutableMapOf()
    private val namedParams: MutableMap<String, String> = mutableMapOf()

    fun parse(): AdocParams {
        while (pos < tokens.size) {
            val token = tokens[pos] as TerminalNode

            if (token.symbol.type == M_PARAM_NAME) {
                val eq = tokenAt(pos + 1, M_PARAM_EQ)

                if (eq != null) {
                    // likely a named param
                    val quoteStart = tokenAt(pos + 2, M_PARAM_QUOTE)

                    if (quoteStart != null) {
                        val (nextPos, valueText) = takeUntil(pos + 3, M_PARAM_QUOTE, quoteStart.text)
                        addNamedParam(token.text, valueText)
                        pos = nextPos + 1
                    } else {
                        parsePositionalParam()
                    }
                } else {
                    parsePositionalParam()
                }
            } else if (token.symbol.type == M_PARAM_VALUE) {
                val (nextPos, valueText) = takeUntil(pos, M_PARAM_COMMA, ",")
                addPositionalParam(valueText)
                pos = nextPos + 1
                continue
            } else if (token.symbol.type == M_PARAM_WS || token.symbol.type == M_PARAM_COMMA) {
                // ignore
            } else {
                throw IllegalArgumentException("Unexpected token type: ${token.text} at position $pos")
            }

            pos++
        }

        return AdocParams(
            positionalParams = positionalParams,
            namedParams = namedParams
        )
    }

    private fun parsePositionalParam() {
        val (nextPos, valueText) = takeUntil(pos, M_PARAM_COMMA, ",")
        addPositionalParam(valueText)
        pos = nextPos
    }

    private fun takeUntil(startFrom: Int, tokenType: Int, tokenText: String?): Pair<Int, String> {
        var i = startFrom

        val result = StringBuilder()

        while (i < tokens.size) {
            val token = tokens[i] as TerminalNode

            if (token.symbol.type == tokenType && token.text == tokenText) {
                return i to result.toString()
            }

            result.append(token.text)
            i++
        }

        return i to result.toString()
    }

    private fun tokenAt(pos: Int, tokenType: Int): TerminalNode? {
        val token = tokens.getOrNull(pos) ?: return null

        if (token !is TerminalNode) {
            return null
        }

        if (token.symbol.type != tokenType) {
            return null
        }

        return token
    }

    fun addNamedParam(text: String, valueText: String) {
        namedParams[text] = valueText
    }

    fun addPositionalParam(valueText: String) {
        positionalParams[positionalParams.size + namedParams.size] = valueText
    }

}
