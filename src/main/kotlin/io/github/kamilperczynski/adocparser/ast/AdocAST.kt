package io.github.kamilperczynski.adocparser.ast

class AdocAST {

    private val _nodes: MutableList<AdocNode> = mutableListOf()

    fun push(node: AdocNode) {
        _nodes.add(node)
    }

    val nodes: List<AdocNode> = _nodes

}

interface AdocNode

data class AdocHeader(
    val level: Int,
    val chunks: List<AdocChunk>,
    val ids: List<AdocId>
) : AdocNode

data class AdocId(val id: String) : AdocNode

data class AdocList(val items: List<AdocListItem>) : AdocNode
data class AdocListItem(val level: Int, val paragraph: AdocParagraph)

data class AdocParagraph(val chunks: List<AdocChunk>) : AdocNode
data class AdocBlock(val lines: List<AdocChunk>) : AdocNode

data class AdocChunk(val type: ChunkType, val text: String)
data class AdocSectionTitle(val chunks: List<AdocChunk>) : AdocNode

data class AdocTable(val colsCount: Int, val cols: List<AdocTableCol>) : AdocNode

data class AdocTableCol(
    val chunks: List<AdocChunk>,
    val colspan: String? = null,
    val alignment: String? = "<",
    val cellFormat: String = "d"
) : AdocNode

enum class ChunkType {
    TEXT, EMPHASIS, PARAMS, LINK
}

