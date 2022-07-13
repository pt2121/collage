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
import io.data2viz.axis.Orient
import io.data2viz.color.Colors
import io.data2viz.geom.Point
import io.data2viz.scale.Scales
import io.data2viz.viz.Margins
import io.github.pt2121.collage.Axis
import io.github.pt2121.collage.CollageElements
import io.github.pt2121.collage.CollageGroup
import io.github.pt2121.collage.d2v.path
import io.github.pt2121.collage.rememberCollagePainter
import kotlin.math.E
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.round

private const val superscript = "⁰¹²³⁴⁵⁶⁷⁸⁹"
private val margins = Margins(
    top = 12.0,
    right = 32.0,
    bottom = 20.0,
    left = 32.0
)

private val width = 640 - margins.hMargins
private val height = 360 - margins.vMargins

private val xScale = Scales.Continuous.linear {
    domain = listOf(.0, 100.0)
    range = listOf(.0, width)
}
private val yScale = Scales.Continuous.log(E) {
    domain = listOf(exp(0.0), exp(9.0))
    range = listOf(height, .0)
}

private val functionToPlot = { x: Double -> x.pow(1.6) + x + 1 }
private val points = (0 until 100).map { i ->
    Point(i.toDouble(), functionToPlot(i.toDouble()))
}

@Composable
fun SampleXYChart() {
    val painter = rememberCollagePainter(
        defaultWidth = 640.dp,
        defaultHeight = 360.dp
    ) { _, _ ->

        CollageGroup(
            translationX = margins.left.dp,
            translationY = -margins.bottom.dp
        ) {
            Axis(
                orient = Orient.LEFT,
                scale = yScale,
                axisStroke = Colors.Web.darkgray,
                tickFormat = {
                    "e${superscript[round(ln(it)).toInt()]}"
                }
            )

            CollageElements(
                nodes = listOf(
                    path {
                        fill = null
                        strokeColor = Colors.Web.steelblue
                        strokeWidth = 1.5
                        moveTo(xScale(points[0].x), yScale(points[0].y))
                        (1 until 100).forEach {
                            lineTo(xScale(points[it].x), yScale(points[it].y))
                        }
                    }
                )
            )
        }
        CollageGroup(
            translationX = margins.left.dp,
            translationY = (height - margins.bottom).dp
        ) {
            Axis(
                orient = Orient.BOTTOM,
                axisStroke = Colors.Web.darkslategray,
                scale = xScale,
                tickPadding = 16.0
            )
        }
    }
    Image(
        painter = painter,
        contentDescription = "Test Collage",
        modifier = Modifier.wrapContentSize()
    )
}
