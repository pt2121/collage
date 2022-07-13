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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.data2viz.chord.Chord
import io.data2viz.chord.ChordGroup
import io.data2viz.chord.ChordLayout
import io.data2viz.chord.Chords
import io.data2viz.color.Colors
import io.data2viz.geom.Path
import io.data2viz.geom.Point
import io.data2viz.geom.Size
import io.data2viz.math.Percent
import io.data2viz.math.pct
import io.data2viz.shape.arcBuilder
import io.github.pt2121.collage.CollageElements
import io.github.pt2121.collage.CollageGroup
import io.github.pt2121.collage.d2v.path
import io.github.pt2121.collage.rememberCollagePainter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private data class Movie(val name: String, val avengers: List<Avenger>)

private data class Avenger(val name: String)

private val blackWidow = Avenger("Black Widow")
private val captainAmerica = Avenger("Captain America")
private val theHulk = Avenger("the Hulk")
private val ironMan = Avenger("Iron Man")
private val thor = Avenger("Thor")
private val hawkeye = Avenger("hawkeye")

private val movies =
    listOf(
        Movie(
            "Avengers",
            listOf(ironMan, captainAmerica, theHulk, thor, hawkeye, blackWidow)
        ),
        Movie(
            "Avengers, L'ère d'Ultron",
            listOf(ironMan, captainAmerica, theHulk, thor, hawkeye, blackWidow)
        ),
        Movie(
            "Avengers, Infinity War",
            listOf(ironMan, captainAmerica, theHulk, hawkeye, blackWidow)
        ),
        Movie(
            "Captain America, First Avenger",
            listOf(captainAmerica)
        ),
        Movie(
            "Captain America, Le Soldat de l'hiver",
            listOf(captainAmerica, blackWidow)
        ),
        Movie(
            "Captain America, Civil War",
            listOf(captainAmerica, ironMan, hawkeye, blackWidow)
        ),
        Movie(
            "Iron Man 1",
            listOf(ironMan)
        ),
        Movie(
            "Iron Man 2",
            listOf(ironMan, blackWidow)
        ),
        Movie(
            "Iron Man 3",
            listOf(ironMan, theHulk)
        ),
        Movie(
            "Thor",
            listOf(thor, hawkeye)
        ),
        Movie(
            "Thor, le monde des ténèbres",
            listOf(thor, captainAmerica)
        ),
        Movie(
            "Thor, Ragnarok",
            listOf(thor, theHulk)
        )
    )

private val avengers = listOf(blackWidow, captainAmerica, hawkeye, theHulk, ironMan, thor)
private val colors = listOf(0x301E1E, 0x083E77, 0x342350, 0x567235, 0x8B161C, 0xDF7C00)
    .map { Colors.rgb(it) }

private val chordSize = Size(400.0, 400.0)
private val outer = minOf(chordSize.width, chordSize.height) * 0.5 - 40.0
private val inner = outer - 30

private val chord = ChordLayout<Avenger>().apply {
    padAngle = .15
}

private fun collaborations(avengers: List<Avenger>) =
    movies.filter { it.avengers.containsAll(avengers) }.size.toDouble()

private val avengersChords: Chords =
    chord.chord(avengers) { a, b -> if (a == b) .0 else collaborations(listOf(a, b)) }

private val avengersArcBuilder = arcBuilder<ChordGroup> {
    innerRadius = { inner + 3 }
    outerRadius = { outer }
    startAngle = { it.startAngle }
    endAngle = { it.endAngle }
}

private val ribbon: (Chord, Path) -> Unit = io.data2viz.chord.ribbon(inner)

private fun Chord.toGradient(alpha: Percent) = Colors.Gradient.linear(
    Point(
        inner * cos((source.endAngle - source.startAngle) / 2 + source.startAngle - PI / 2),
        inner * sin((source.endAngle - source.startAngle) / 2 + source.startAngle - PI / 2)
    ),
    Point(
        inner * cos((target.endAngle - target.startAngle) / 2 + target.startAngle - PI / 2),
        inner * sin((target.endAngle - target.startAngle) / 2 + target.startAngle - PI / 2)
    )
).withColor(colors[source.index].withAlpha(alpha))
    .andColor(colors[target.index].withAlpha(alpha))

@Composable
fun AvengersChords() {
    // Drawing external groups representing avengers
    val pathNodes = remember {
        avengersChords.groups.mapIndexed { index, chordGroup ->
            path {
                fill = colors[index]
                avengersArcBuilder.buildArcForDatum(chordGroup, this)
                stroke = null
            }
        } + avengersChords.chords.map { chord ->
            path {
                fill = chord.toGradient(60.pct)
                stroke = null
                ribbon(chord, this)
            }
        }
    }

    val painter = rememberCollagePainter(
        chordSize.width.dp,
        chordSize.height.dp
    ) { _, _ ->
        CollageGroup(
            translationX = chordSize.width.dp / 2,
            translationY = chordSize.height.dp / 2
        ) {
            CollageElements(pathNodes)
        }
    }
    Image(
        painter = painter,
        contentDescription = "Test Collage",
        modifier = Modifier.wrapContentSize()
    )
}
