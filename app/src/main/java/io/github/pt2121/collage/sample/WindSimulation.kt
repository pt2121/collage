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

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.unit.dp
import io.data2viz.color.Colors
import io.data2viz.force.ForceLink
import io.data2viz.force.ForceSimulation
import io.data2viz.force.Link
import io.data2viz.force.forceSimulation
import io.data2viz.geom.Point
import io.data2viz.geom.Vector
import io.data2viz.geom.point
import io.data2viz.math.deg
import io.data2viz.math.pct
import io.data2viz.math.rad
import io.data2viz.random.RandomDistribution
import io.data2viz.viz.LineNode
import io.github.pt2121.collage.d2v.line
import io.github.pt2121.collage.d2v.render
import io.github.pt2121.collage.recomposeHighlighter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.math.atan

// credit to Pierre Mariac. based off of https://play.data2viz.io/sketches/PnLoKL/edit/

private val frictionRate = 4.pct // friction rate
private val defaultIntensity = 70.pct // default fixed intensity (no intensity decay)

// the more iterations = the more "rigid" the curtains
private const val linkForceIterations = 7

// less strength = wind pass through the "curtains"
private val collisionForceStrength = 10.pct

private const val singleCurtainWidth = 1 // width of a curtain
private const val curtainsNumber = 30 // # of curtains drawn

private const val curtainsLength = 32 // length of a curtain
private const val stitchSpace = 3.0 // size between nodes

private const val windRadius = 80.0 // size of the "wind" circles for collision
private const val windSpeed = 4 // speed of the "wind"
private val windAngle = 0.deg // angle of the "wind"

private val gravityValue = 0.16.pct // higher values = heavier "curtains"

// *************************************************************************

private const val curtainsWidth = curtainsNumber * singleCurtainWidth
private const val totalStitches = curtainsWidth * curtainsLength

private const val vizSize = 900.0
private val movement = Vector(windSpeed * windAngle.cos, windSpeed * windAngle.sin)
private val randPos = RandomDistribution.uniform(320.0, 600.0)

// our domain object, storing a default starting position and if it is "fixed" or not
private data class Stitch(val position: Point, val fixed: Boolean = false)

// creating the objects, only the top line is "fixed"
private val stitches = (0 until totalStitches).map {
    val col = it % curtainsWidth
    val row = it / curtainsWidth
    Stitch(
        point(64.0 + (col * stitchSpace), 140.0 + (row * stitchSpace) - col),
        it < curtainsWidth
    )
}.toMutableList().apply {

// adding 3 more nodes to the simulation, these nodes are used to simulate the wind
// these nodes will have a very different behavior
    add(Stitch(point(0, 350), false))
    add(Stitch(point(-450, 600), false))
    add(Stitch(point(-200, 400), false))
}

private var forceLinks: ForceLink<Stitch>? = null
private val simulation: ForceSimulation<Stitch> =
    forceSimulation {
        friction = frictionRate
        intensity = defaultIntensity
        intensityDecay = 0.pct

        // if the Stitch is "fixed", we use its current position has a fixed one (node won't move)
        initForceNode = {
            position = domain.position
            fixedX = if (domain.fixed) domain.position.x else null
            fixedY = if (domain.fixed) domain.position.y else null
        }

        // the force that creates links between the nodes
        // each node is linked to the next one on the right and next one below
        forceLinks = forceLink {
            linkGet = {
                val links = mutableListOf<Link<Stitch>>()
                val currentCol = index % singleCurtainWidth
                val wholeCol = index % curtainsWidth
                val row = index / curtainsWidth

                // only "link" the stitches, not the 3 nodes used for simulating the wind
                if (index < totalStitches) {
                    // check if we had the right-next node
                    if (currentCol != (singleCurtainWidth - 1) && wholeCol < curtainsWidth - 1) {
                        links += Link(this, nodes[index + 1], stitchSpace)
                    }
                    // check if we had the bottom-next node
                    if (row < curtainsLength - 1) {
                        links += Link(this, nodes[index + curtainsWidth], stitchSpace)
                    }
                }
                // return the list of links
                links
            }
            iterations = linkForceIterations
        }

        // create a collision force, only the 3 "wind" nodes have a radius
        forceCollision {
            radiusGet = { if (index < totalStitches) .0 else windRadius }
            strength = collisionForceStrength
            iterations = 1
        }

        // create a "gravity" force, only applies to the "stiches" not the "wind"
        forceY {
            yGet = { vizSize }
            strengthGet = { if (index < totalStitches) gravityValue else 0.pct }
        }

        domainObjects = stitches
    }

@Composable
fun WindSimulation() {

    // storing the visuals of links and wind particles
    val links = remember {
        val list = mutableStateListOf<LineNode>()
        repeat(forceLinks?.links?.size ?: 0) {
            list.add(
                line {
                    strokeColor = Colors.Web.black
                }
            )
        }
        list
    }

    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            withContext(Dispatchers.Default) {
                withInfiniteAnimationFrameMillis {
                    // force move the "wind" particles
                    (totalStitches..totalStitches + 2).forEach {
                        val windNode = simulation.nodes[it]
                        windNode.position += movement
                        if (windNode.x > vizSize) {
                            windNode.x = -50.0
                            windNode.y = randPos()
                        }
                    }

                    // show the new coordinates of each links to visualize the wind effect
                    forceLinks?.links?.forEachIndexed { index, link ->
                        links[index] = line {
                            x1 = link.source.x
                            x2 = link.target.x
                            y1 = link.source.y
                            y2 = link.target.y

                            val angle = (atan((y1 - y2) / (x1 - x2)) * 2).rad
                            strokeColor = Colors.hsl(angle, 100.pct, 40.pct)
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .recomposeHighlighter()
    ) {
        Box(
            Modifier
                .size(vizSize.dp, 600.dp)
                .recomposeHighlighter()
                .drawBehind {
                    links.forEach {
                        render(it)
                    }
                }
        ) {
        }

        Text(
            text = "Wind Simulation",
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
        )
    }
}
