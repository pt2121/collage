package io.github.pt2121.collage.d2v

import io.data2viz.viz.CircleNode
import io.data2viz.viz.LineNode
import io.data2viz.viz.PathNode
import io.data2viz.viz.RectNode
import io.data2viz.viz.TextNode

inline fun circle(init: CircleNode.() -> Unit): CircleNode =
    CircleNode().apply(init)

inline fun line(init: LineNode.() -> Unit): LineNode =
    LineNode().apply(init)

inline fun path(init: PathNode.() -> Unit): PathNode =
    PathNode().apply(init)

inline fun rect(init: RectNode.() -> Unit): RectNode =
    RectNode().apply(init)

inline fun text(init: TextNode.() -> Unit): TextNode =
    TextNode().apply(init)
