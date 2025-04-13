package io.github.kamilperczynski.adocparser.ast

data class AdocParams(
    val positionalParams: Map<Int, String> = mapOf(),
    val namedParams: Map<String, String> = mapOf()
) {
    fun isEmpty(): Boolean {
        return positionalParams.isEmpty() && namedParams.isEmpty()
    }
}

val EMPTY_ADOC_PARAMS = AdocParams()
