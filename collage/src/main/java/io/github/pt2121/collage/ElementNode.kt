/*
 * Copyright 2022-2022 Prat Tana
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
