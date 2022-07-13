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

package io.github.pt2121.collage.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.data2viz.color.Colors
import io.data2viz.viz.FontFamily
import io.data2viz.viz.FontPosture
import io.data2viz.viz.FontWeight
import io.data2viz.viz.TextHAlign
import io.data2viz.viz.TextNode
import io.data2viz.viz.TextVAlign
import io.github.pt2121.collage.CollageElements
import io.github.pt2121.collage.CollageGroup
import io.github.pt2121.collage.CollagePainter
import io.github.pt2121.collage.d2v.circle
import io.github.pt2121.collage.d2v.line
import io.github.pt2121.collage.d2v.rect
import io.github.pt2121.collage.d2v.text
import io.github.pt2121.collage.rememberCollagePainter

@Composable
fun BasicSample() {
    Image(
        painter = rememberCollageCircleSamplePainter(),
        contentDescription = "Test Collage",
        modifier = Modifier.wrapContentSize()
    )
}

@Composable
private fun rememberCollageCircleSamplePainter(): CollagePainter =
    rememberCollagePainter(360.0.dp, 360.0.dp) { _, _ ->
        CollageGroup(translationX = 16.dp, translationY = 16.dp) {
            CollageElements(
                listOf(
                    circle {
                        x = 0.0
                        y = 0.0
                        radius = 32.0
                        fill = Colors.rgb(0, 0, 255)
                    },
                    circle {
                        x = 36.0
                        y = 36.0
                        radius = 24.0
                        fill = Colors.rgb(255, 0, 0)
                    },
                    line {
                        x1 = 1.0
                        y1 = 1.0
                        x2 = 70.0
                        y2 = 70.0
                        strokeColor = Colors.Web.cyan
                        strokeWidth = 12.0
                    },
                    rect {
                        width = 200.0
                        height = width
                        strokeColor = Colors.Web.steelblue
                        strokeWidth = 8.0
                    }
                )
            )

            CollageGroup(translationX = 64.dp, translationY = 160.dp) {
                CollageElements(
                    textNodes
                )
            }
        }
    }

private val textNodes: List<TextNode> by lazy {
    val lines = listOf(
        "From childhood’s hour I have not been",
        "As others were—I have not seen",
        "As others saw—I could not bring",
        "My passions from a common spring—",
        "From the same source I have not taken",
        "My sorrow—I could not awaken",
        "My heart to joy at the same tone—",
        "And all I lov’d—I lov’d alone—"
    )
    val textSize = 12.0
    val textNodes = mutableListOf<TextNode>()
    for ((index, line) in lines.withIndex()) {
        textNodes += text {
            fontFamily = FontFamily.MONOSPACE
            fontStyle = FontPosture.ITALIC
            fontWeight = FontWeight.BOLD
            hAlign = TextHAlign.LEFT
            vAlign = TextVAlign.BASELINE
            textContent = line
            y = textSize + textSize * index
            textColor = Colors.Web.gold
            fontSize = textSize
        }
    }
    textNodes
}
