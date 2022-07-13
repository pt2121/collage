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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Composition
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCompositionContext
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun rememberCollagePainter(
    defaultWidth: Dp,
    defaultHeight: Dp,
    viewportWidth: Float = Float.NaN,
    viewportHeight: Float = Float.NaN,
    content: @Composable (viewportWidth: Float, viewportHeight: Float) -> Unit
): CollagePainter {
    val density = LocalDensity.current
    val widthPx = with(density) { defaultWidth.toPx() }
    val heightPx = with(density) { defaultHeight.toPx() }

    val vpWidth = if (viewportWidth.isNaN()) widthPx else viewportWidth
    val vpHeight = if (viewportHeight.isNaN()) heightPx else viewportHeight

    return remember { CollagePainter() }.apply {
        size = Size(widthPx, heightPx)
        RenderCollage(vpWidth, vpHeight, content)
    }
}

class CollagePainter internal constructor() : Painter() {
    internal var size by mutableStateOf(Size.Zero)
    private var isDirty by mutableStateOf(true)
    private var composition: Composition? = null

    private val rootNode = RootNode().apply {
        invalidateCallback = {
            isDirty = true
        }
    }

    override val intrinsicSize: Size
        get() = size

    override fun DrawScope.onDraw() {
        with(rootNode) {
            draw()
        }
        if (isDirty) { // This conditional is necessary to obtain invalidation callbacks - it adds this callback to the snapshot observation
            isDirty = false
        }
    }

    @Composable
    internal fun RenderCollage(
        viewportWidth: Float,
        viewportHeight: Float,
        content: @Composable (viewportWidth: Float, viewportHeight: Float) -> Unit
    ) {
        rootNode.apply {
            this.viewportWidth = viewportWidth
            this.viewportHeight = viewportHeight
        }
        val composition = composeCollage(
            rememberCompositionContext(),
            content
        )

        DisposableEffect(composition) {
            onDispose {
                composition.dispose()
            }
        }
    }

    private fun composeCollage(
        parent: CompositionContext,
        composable: @Composable (viewportWidth: Float, viewportHeight: Float) -> Unit
    ): Composition {
        val existing = composition
        val next = if (existing == null || existing.isDisposed) {
            Composition(
                CollageApplier(rootNode.root),
                parent
            )
        } else {
            existing
        }
        composition = next
        next.setContent {
            composable(rootNode.viewportWidth, rootNode.viewportHeight)
        }
        return next
    }
}
