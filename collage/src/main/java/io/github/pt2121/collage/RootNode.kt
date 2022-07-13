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
