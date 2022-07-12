package io.github.pt2121.collage

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.IntSize
import kotlin.math.ceil

internal class RootNode : CollageNode() {

    val root = GroupNode().apply {
        invalidateListener = {
            doInvalidate()
        }
    }

    private var isDirty: Boolean = true

    private val cacheDrawScope = DrawCache()

    internal var invalidateCallback = {}

    var viewportWidth: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                doInvalidate()
            }
        }

    var viewportHeight: Float = 0f
        set(value) {
            if (field != value) {
                field = value
                doInvalidate()
            }
        }

    private var previousDrawSize: Size = Size.Unspecified

    /**
     * Cached lambda
     */
    private val drawBlock: DrawScope.() -> Unit = {
        with(root) { draw() }
    }

    private fun doInvalidate() {
        isDirty = true
        invalidateCallback.invoke()
    }

    override fun DrawScope.draw() {
        if (isDirty || previousDrawSize != size) {
            root.scaleX = size.width / viewportWidth
            root.scaleY = size.height / viewportHeight
            cacheDrawScope.drawCachedImage(
                IntSize(ceil(size.width).toInt(), ceil(size.height).toInt()),
                this@draw,
                layoutDirection,
                drawBlock
            )
            isDirty = false
            previousDrawSize = size
        }

        cacheDrawScope.drawInto(this)
    }
}
