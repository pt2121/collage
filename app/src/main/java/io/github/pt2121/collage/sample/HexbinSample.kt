package io.github.pt2121.collage.sample

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.data2viz.color.Colors
import io.data2viz.geom.Point
import io.data2viz.hexbin.hexbinGenerator
import io.data2viz.random.RandomDistribution
import io.data2viz.scale.ScalesChromatic
import io.data2viz.viz.PathNode
import io.github.pt2121.collage.CollageElements
import io.github.pt2121.collage.rememberCollagePainter

private const val POINT_COUNT = 2000
private const val defaultSize = 400.0

private val generator = RandomDistribution.normal(defaultSize / 2, 80.0)

private val allX = (1..POINT_COUNT).map { generator() }
private val allY = (1..POINT_COUNT).map { generator() }
private val points = allX.zip(allY).map { Point(it.first, it.second) }

private val hexbin = hexbinGenerator {
    width = defaultSize
    height = defaultSize
    radius = 8.0
}

private val bins by lazy { hexbin(points) }

private val linearScale = ScalesChromatic.Continuous.linearLAB {
    domain = listOf(0.0, bins.maxOf { it.points.count().toDouble() })
    range = listOf(Colors.Web.white, Colors.rgb(0x0141F79))
}

// credit to Gaetan Zoritchak. based off of https://play.data2viz.io/sketches/BgaOBY/edit/
private val hexbinNodes: List<PathNode> by lazy {
    hexbin(points).map { bin ->
        val path = PathNode()
        hexbin.hexagon(path, Point(bin.x, bin.y))
        path.fill = linearScale(bin.points.size.toDouble())
        path.strokeColor = Colors.Web.white
        path.strokeWidth = 1.0
        path
    }
}

@Composable
fun HexbinSample() {
    val painter = rememberCollagePainter(
        defaultSize.dp,
        defaultSize.dp
    ) { _, _ ->
        CollageElements(hexbinNodes)
    }

    Image(
        painter = painter,
        contentDescription = "Test Collage",
        modifier = Modifier.wrapContentSize()
    )
}
