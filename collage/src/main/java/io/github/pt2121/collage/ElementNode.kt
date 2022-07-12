package io.github.pt2121.collage

import androidx.compose.ui.graphics.drawscope.DrawScope
import io.data2viz.viz.CircleNode
import io.data2viz.viz.LineNode
import io.data2viz.viz.Node
import io.data2viz.viz.PathNode
import io.data2viz.viz.RectNode
import io.data2viz.viz.TextNode
import io.github.pt2121.collage.d2v.render

internal class ElementNode : CollageNode() {

    var nodes: List<Node> = emptyList()
        set(value) {
            field = value
            invalidate()
        }

    override fun DrawScope.draw() {
        nodes.forEach { node ->
            when (node) {
                is CircleNode -> render(node)
                is LineNode -> render(node)
                is PathNode -> render(node)
                is RectNode -> render(node)
                is TextNode -> render(node)
                else -> TODO()
            }
        }
    }
}
