package io.github.pt2121.collage

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ComposeNode
import androidx.compose.ui.graphics.vector.DefaultRotation
import androidx.compose.ui.graphics.vector.DefaultScaleX
import androidx.compose.ui.graphics.vector.DefaultScaleY
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.data2viz.axis.Orient
import io.data2viz.color.ColorOrGradient
import io.data2viz.color.Colors
import io.data2viz.scale.FirstLastRange
import io.data2viz.viz.FontFamily
import io.data2viz.viz.FontPosture
import io.data2viz.viz.FontWeight
import io.data2viz.viz.Node

@Composable
fun <D> Axis(
    orient: Orient,
    scale: FirstLastRange<D, Double>,
    tickValues: List<D> = listOf(),
    tickSizeInner: Double = 6.0,
    tickSizeOuter: Double = 6.0,
    tickPadding: Double = 3.0,
    axisStroke: ColorOrGradient? = Colors.Web.black,
    axisStrokeWidth: Double? = 1.0,
    tickStroke: ColorOrGradient? = Colors.Web.black,
    tickStrokeWidth: Double? = 1.0,
    fontSize: Double = 12.0,
    fontColor: ColorOrGradient? = Colors.Web.black,
    fontFamily: FontFamily = FontFamily.SANS_SERIF,
    fontWeight: FontWeight = FontWeight.NORMAL,
    fontStyle: FontPosture = FontPosture.NORMAL,
    tickFormat: (D) -> String = { n: D -> n.toString() }
) {
    val axisElement = axis(orient, scale) {
        this.tickValues = tickValues
        this.tickSizeInner = tickSizeInner
        this.tickSizeOuter = tickSizeOuter
        this.tickPadding = tickPadding
        this.axisStroke = axisStroke
        this.axisStrokeWidth = axisStrokeWidth
        this.tickStroke = tickStroke
        this.tickStrokeWidth = tickStrokeWidth
        this.fontSize = fontSize
        this.fontColor = fontColor
        this.fontFamily = fontFamily
        this.fontWeight = fontWeight
        this.fontStyle = fontStyle
        this.tickFormat = tickFormat
    }
    val annotations = axisElement.toAxisAnnotations()
    val lines = axisElement.toAxisLine()
    if (lines != null) {
        CollageElements(
            nodes = listOf(lines)
        )
    }
    val isHorizontal = axisElement.orient.isHorizontal()
    annotations.forEach { (data, tick, text) ->
        val translation = axisElement.position(data).toFloat()
        CollageGroup(
            translationX = if (isHorizontal) translation.dp else 0.dp,
            translationY = if (!isHorizontal) translation.dp else 0.dp
        ) {
            CollageElements(listOfNotNull(tick, text))
        }
    }
}

@Composable
fun CollageGroup(
    rotation: Float = DefaultRotation,
    pivotX: Dp = 0.dp,
    pivotY: Dp = 0.dp,
    scaleX: Float = DefaultScaleX,
    scaleY: Float = DefaultScaleY,
    translationX: Dp = 0.dp,
    translationY: Dp = 0.dp,
    content: @Composable () -> Unit
) {
    val (pX, pY) = with(LocalDensity.current) {
        pivotX.toPx() to pivotY.toPx()
    }
    val (x, y) = with(LocalDensity.current) {
        translationX.toPx() to translationY.toPx()
    }
    ComposeNode<GroupNode, CollageApplier>(
        factory = { GroupNode() },
        update = {
            set(rotation) { this.rotation = it }
            set(pivotX) { this.pivotX = pX }
            set(pivotY) { this.pivotY = pY }
            set(scaleX) { this.scaleX = it }
            set(scaleY) { this.scaleY = it }
            set(translationX) { this.translationX = x }
            set(translationY) { this.translationY = y }
        }
    ) {
        content()
    }
}

@Composable
fun CollageElements(
    nodes: List<Node>
) {
    ComposeNode<ElementNode, CollageApplier>(
        factory = { ElementNode() },
        update = {
            set(nodes) { this.nodes = it }
        }
    )
}
