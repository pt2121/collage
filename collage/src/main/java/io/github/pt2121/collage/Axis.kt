package io.github.pt2121.collage

import io.data2viz.axis.AxisElement
import io.data2viz.axis.Orient
import io.data2viz.scale.BandedScale
import io.data2viz.scale.FirstLastRange
import io.data2viz.scale.Scale
import io.data2viz.scale.Tickable
import io.data2viz.viz.LineNode
import io.data2viz.viz.PathNode
import io.data2viz.viz.TextHAlign
import io.data2viz.viz.TextNode
import io.data2viz.viz.TextVAlign
import io.github.pt2121.collage.d2v.line
import io.github.pt2121.collage.d2v.path
import io.github.pt2121.collage.d2v.text
import kotlin.math.round

fun <D> axis(
    orient: Orient,
    scale: FirstLastRange<D, Double>,
    init: AxisElement<D>.() -> Unit = {}
): AxisElement<D> =
    AxisElement(orient, scale)
        .apply {
            init(this)
        }

data class Annotation<D>(
    val d: D,
    val tick: LineNode?,
    val text: TextNode?
)

internal fun <D> AxisElement<D>.toAxisAnnotations(): List<Annotation<D>> {
    val sign = if (orient == Orient.TOP || orient == Orient.LEFT) -1 else 1
    val values: List<D> = if (tickValues.isEmpty() && scale is Tickable<*>) {
        (scale as Tickable<*>).ticks() as List<D>
    } else {
        tickValues
    }
    val spacing = tickSizeInner.coerceAtLeast(0.0) + tickPadding

    return values.map { data ->
        var line: LineNode? = null
        var textNode: TextNode? = null
        if (tickStroke != null && tickStrokeWidth != null) {
            line = if (orient.isHorizontal()) {
                line {
                    y2 = sign * tickSizeInner
                    strokeColor = tickStroke
                    strokeWidth = tickStrokeWidth
                }
            } else {
                line {
                    x2 = sign * tickSizeInner
                    strokeColor = tickStroke
                    strokeWidth = tickStrokeWidth
                }
            }
        }
        if (fontColor != null) {
            textNode = text {
                textColor = fontColor
                fontWeight = this@toAxisAnnotations.fontWeight
                fontSize = this@toAxisAnnotations.fontSize
                fontFamily = this@toAxisAnnotations.fontFamily
                fontStyle = this@toAxisAnnotations.fontStyle
                hAlign = when (orient) {
                    Orient.LEFT -> TextHAlign.RIGHT
                    Orient.RIGHT -> TextHAlign.LEFT
                    else -> TextHAlign.MIDDLE
                }

                vAlign = when (orient) {
                    Orient.TOP -> TextVAlign.BASELINE
                    Orient.BOTTOM -> TextVAlign.HANGING
                    else -> TextVAlign.MIDDLE
                }
                if (orient.isHorizontal()) {
                    y = spacing * sign
                } else {
                    x = spacing * sign
                }
                textContent = tickFormat(data)
            }
        }
        Annotation(data, line, textNode)
    }
}

private fun <D> center(
    scale: BandedScale<D>
): (D) -> Double {
    var offset = (scale.bandwidth - 1).coerceAtLeast(0.0) / 2 // Adjust for 0.5px offset.
    if (scale.round) offset = round(offset)
    return { d: D -> +scale(d) + offset }
}

private fun <D> number(
    scale: Scale<D, Double>
): (D) -> Double = { scale(it) }

internal fun <D> AxisElement<D>.position(d: D): Double =
    (scale as? BandedScale)?.let { center(it)(d) }
        ?: number(scale)(d)

internal fun <D> AxisElement<D>.toAxisLine(): PathNode? {
    val start = scale.start()
    val end = scale.end()
    val sign = if (orient == Orient.TOP || orient == Orient.LEFT) -1 else 1
    return if (axisStroke != null && axisStrokeWidth != null) {
        path {
            strokeColor = axisStroke
            strokeWidth = axisStrokeWidth
            fill = null

            if (orient.isVertical()) {
                moveTo(tickSizeOuter * sign, start)
                lineTo(.0, start)
                lineTo(.0, end)
                lineTo(tickSizeOuter * sign, end)
            } else {
                moveTo(start, tickSizeOuter * sign)
                lineTo(start, .0)
                lineTo(end, .0)
                lineTo(end, tickSizeOuter * sign)
            }
        }
    } else {
        null
    }
}
