package io.github.pt2121.collage

import androidx.compose.runtime.AbstractApplier

internal class CollageApplier(root: CollageNode) : AbstractApplier<CollageNode>(root) {
    override fun onClear() {
        root.asGroup().let { it.remove(0, it.numChildren) }
    }

    override fun insertBottomUp(index: Int, instance: CollageNode) {
    }

    override fun insertTopDown(index: Int, instance: CollageNode) {
        current.asGroup().insertAt(index, instance)
    }

    override fun move(from: Int, to: Int, count: Int) {
        current.asGroup().move(from, to, count)
    }

    override fun remove(index: Int, count: Int) {
        current.asGroup().remove(index, count)
    }

    private fun CollageNode.asGroup(): GroupNode {
        return when (this) {
            is GroupNode -> this
            else -> error("Failed to apply CollageNode")
        }
    }
}
