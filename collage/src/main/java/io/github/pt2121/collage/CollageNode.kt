package io.github.pt2121.collage

import androidx.compose.ui.graphics.drawscope.DrawScope

sealed class CollageNode {

    internal open var invalidateListener: (() -> Unit)? = null

    fun invalidate() {
        invalidateListener?.invoke()
    }

    abstract fun DrawScope.draw()
}
