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
