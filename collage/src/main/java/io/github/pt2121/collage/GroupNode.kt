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

import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.vector.DefaultPivotX
import androidx.compose.ui.graphics.vector.DefaultPivotY
import androidx.compose.ui.graphics.vector.DefaultRotation
import androidx.compose.ui.graphics.vector.DefaultScaleX
import androidx.compose.ui.graphics.vector.DefaultScaleY
import androidx.compose.ui.graphics.vector.DefaultTranslationX
import androidx.compose.ui.graphics.vector.DefaultTranslationY

internal class GroupNode : CollageNode() {

    private val children = mutableListOf<CollageNode>()
    private var groupMatrix: Matrix? = null
    private var isMatrixDirty = true

    val numChildren: Int
        get() = children.size

    var rotation: Float = DefaultRotation
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var pivotX: Float = DefaultPivotX
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var pivotY: Float = DefaultPivotY
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var scaleX: Float = DefaultScaleX
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var scaleY: Float = DefaultScaleY
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var translationX: Float = DefaultTranslationX
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    var translationY: Float = DefaultTranslationY
        set(value) {
            field = value
            isMatrixDirty = true
            invalidate()
        }

    override var invalidateListener: (() -> Unit)? = null
        set(value) {
            field = value
            children.forEach { child ->
                child.invalidateListener = value
            }
        }

    fun insertAt(index: Int, instance: CollageNode) {
        if (index < numChildren) {
            children[index] = instance
        } else {
            children.add(instance)
        }
        instance.invalidateListener = invalidateListener
        invalidate()
    }

    fun move(from: Int, to: Int, count: Int) {
        if (from > to) {
            var current = to
            repeat(count) {
                val node = children[from]
                children.removeAt(from)
                children.add(current, node)
                current++
            }
        } else {
            repeat(count) {
                val node = children[from]
                children.removeAt(from)
                children.add(to - 1, node)
            }
        }
        invalidate()
    }

    fun remove(index: Int, count: Int) {
        repeat(count) {
            if (index < children.size) {
                children[index].invalidateListener = null
                children.removeAt(index)
            }
        }
        invalidate()
    }

    override fun DrawScope.draw() {
        if (isMatrixDirty) {
            updateMatrix()
            isMatrixDirty = false
        }

        withTransform({
            groupMatrix?.let { transform(it) }
        }) {
            children.forEach { node ->
                with(node) {
                    this@draw.draw()
                }
            }
        }
    }

    private fun updateMatrix() {
        val matrix: Matrix
        val target = groupMatrix
        if (target == null) {
            matrix = Matrix()
            groupMatrix = matrix
        } else {
            matrix = target
            matrix.reset()
        }
        matrix.translate(translationX + pivotX, translationY + pivotY)
        matrix.rotateZ(degrees = rotation)
        matrix.scale(scaleX, scaleY, 1f)
        matrix.translate(-pivotX, -pivotY)
    }
}
