package io.github.pt2121.collage.sample

import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.data2viz.math.pct
import io.data2viz.scale.ScalesChromatic
import io.data2viz.scale.StrictlyContinuous
import io.data2viz.viz.PathNode
import io.github.pt2121.collage.CollageElements
import io.github.pt2121.collage.d2v.path
import io.github.pt2121.collage.rememberCollagePainter
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive

// based off of https://play.data2viz.io/sketches/wYqPKL/edit/
// credit to Pierre Mariac

@Composable
fun GlobalTemperature() {
    val size = 392.0
    val margin = 8.0
    val center = size / 2

    var previousIndex = 0
    var currentIndex = 0
    val autoAnimation = true

    // MIN ans MAX temperatures
    val minTemp = temps.minOfOrNull { it.tempDeltas.minOrNull()!! }!!
    val maxTemp = temps.maxOfOrNull { it.tempDeltas.maxOrNull()!! }!!

    val totalSurveys = temps.sumOf { it.tempDeltas.size }

    // The temperature color scale (min = blue, max = red)
    val tempColorScale = ScalesChromatic.Sequential.Diverging.red_blue {
        domain = StrictlyContinuous(maxTemp, minTemp)
    }

    // Setup of the spiral viz
    val radius = size / 2.0 - margin
    val holeRadiusProportion = 18.pct
    val arcsPerCoil = 12 // 12 months for a complete circle
    val startAngle = -kotlin.math.PI / 2 // starts at 12 o'clock

    val arcAngle = 2 * kotlin.math.PI / arcsPerCoil
    val startRadius = radius * holeRadiusProportion.value
    val radiusIncrement = (radius - startRadius) / totalSurveys

    val pathNodes = remember {
        mutableStateListOf<PathNode>()
    }
    var yearText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(key1 = Unit) {
        while (isActive) {
            withInfiniteAnimationFrameMillis {
                if (currentIndex >= totalSurveys) {
                    cancel()
                }
                if (currentIndex < previousIndex) {
                    previousIndex = 0
                }

                (previousIndex..currentIndex).forEach { index ->
                    val fromAngle = startAngle + ((index % arcsPerCoil) * arcAngle)
                    val toAngle = fromAngle + arcAngle
                    val year = index / 12
                    val month = index % 12
                    pathNodes += path {
                        val tempSurvey = temps[year]
                        yearText = tempSurvey.year.toString()
                        strokeColor = tempColorScale(tempSurvey.tempDeltas[month])
                        strokeWidth = 3.0
                        arc(
                            center,
                            center,
                            startRadius + (index * radiusIncrement),
                            fromAngle,
                            toAngle
                        )
                    }
                }

                previousIndex = currentIndex
                if (autoAnimation) currentIndex++
            }
        }
    }

    Column {
        Text(
            text = "World Global Temperatures\nfrom 1880 to 2019",
            modifier = Modifier
                .padding(
                    horizontal = 16.dp,
                    vertical = 40.dp
                )
                .align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center
        )

        Box(
            Modifier
                .size(size.dp)
        ) {
            val painter = rememberCollagePainter(
                size.dp,
                size.dp
            ) { _, _ ->
                CollageElements(pathNodes.toList())
            }
            Image(
                painter = painter,
                contentDescription = "Global Temperature",
                modifier = Modifier
                    .wrapContentSize()
            )

            Text(
                text = yearText,
                Modifier.align(Alignment.Center),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

// data class used to store NASA readings
private data class TempSurvey(val year: Int, val tempDeltas: List<Double>)

// dataset from https://data.giss.nasa.gov/gistemp/
private val temps =
    listOf(
        TempSurvey(
            1880,
            listOf(-.18, -.24, -.09, -.16, -.10, -.21, -.18, -.10, -.15, -.24, -.22, -.18)
        ),
        TempSurvey(
            1881,
            listOf(-.20, -.14, .03, .05, .06, -.18, .00, -.03, -.15, -.22, -.18, -.07)
        ),
        TempSurvey(
            1882,
            listOf(.17, .14, .05, -.16, -.14, -.22, -.16, -.07, -.14, -.24, -.16, -.36)
        ),
        TempSurvey(
            1883,
            listOf(-.29, -.36, -.12, -.18, -.18, -.07, -.07, -.14, -.22, -.11, -.24, -.11)
        ),
        TempSurvey(
            1884,
            listOf(-.13, -.08, -.37, -.40, -.34, -.35, -.33, -.27, -.27, -.25, -.33, -.31)
        ),
        TempSurvey(
            1885,
            listOf(-.58, -.33, -.26, -.42, -.45, -.43, -.33, -.30, -.28, -.23, -.23, -.09)
        ),
        TempSurvey(
            1886,
            listOf(-.43, -.50, -.43, -.27, -.24, -.34, -.17, -.30, -.24, -.27, -.27, -.25)
        ),
        TempSurvey(
            1887,
            listOf(-.71, -.56, -.35, -.34, -.30, -.24, -.25, -.34, -.25, -.35, -.26, -.33)
        ),
        TempSurvey(
            1888,
            listOf(-.33, -.35, -.41, -.20, -.22, -.17, -.10, -.15, -.12, .02, .04, -.04)
        ),
        TempSurvey(
            1889,
            listOf(-.08, .18, .06, .10, -.01, -.10, -.07, -.20, -.24, -.26, -.33, -.29)
        ),
        TempSurvey(
            1890,
            listOf(-.42, -.45, -.40, -.30, -.40, -.25, -.28, -.39, -.37, -.25, -.44, -.31)
        ),
        TempSurvey(
            1891,
            listOf(-.33, -.47, -.18, -.27, -.16, -.20, -.17, -.17, -.15, -.22, -.31, -.04)
        ),
        TempSurvey(
            1892,
            listOf(-.28, -.10, -.40, -.33, -.23, -.22, -.31, -.27, -.16, -.14, -.41, -.38)
        ),
        TempSurvey(
            1893,
            listOf(-.80, -.56, -.22, -.27, -.34, -.25, -.14, -.25, -.23, -.19, -.19, -.32)
        ),
        TempSurvey(
            1894,
            listOf(-.52, -.28, -.22, -.44, -.30, -.40, -.24, -.23, -.28, -.22, -.25, -.21)
        ),
        TempSurvey(
            1895,
            listOf(-.40, -.42, -.32, -.21, -.27, -.21, -.16, -.16, -.12, -.10, -.16, -.13)
        ),
        TempSurvey(
            1896,
            listOf(-.21, -.12, -.26, -.30, -.16, -.11, -.02, -.05, -.07, .06, -.04, -.04)
        ),
        TempSurvey(
            1897,
            listOf(-.15, -.17, -.13, -.02, -.02, -.11, -.02, -.09, -.09, -.12, -.19, -.20)
        ),
        TempSurvey(
            1898,
            listOf(-.01, -.29, -.50, -.29, -.30, -.18, -.22, -.26, -.21, -.35, -.37, -.23)
        ),
        TempSurvey(
            1899,
            listOf(-.16, -.38, -.36, -.21, -.24, -.32, -.16, -.08, -.05, -.04, .12, -.26)
        ),
        TempSurvey(
            1900,
            listOf(-.35, -.05, .01, -.09, -.08, -.10, -.13, -.09, -.05, .10, -.07, -.06)
        ),
        TempSurvey(
            1901,
            listOf(-.23, -.11, .06, -.02, -.16, -.12, -.15, -.20, -.22, -.30, -.18, -.28)
        ),
        TempSurvey(
            1902,
            listOf(-.19, -.07, -.28, -.28, -.32, -.31, -.29, -.30, -.30, -.30, -.36, -.42)
        ),
        TempSurvey(
            1903,
            listOf(-.24, -.06, -.23, -.41, -.40, -.42, -.36, -.45, -.50, -.49, -.43, -.51)
        ),
        TempSurvey(
            1904,
            listOf(-.64, -.58, -.49, -.50, -.52, -.49, -.52, -.49, -.56, -.40, -.18, -.34)
        ),
        TempSurvey(
            1905,
            listOf(-.35, -.60, -.22, -.33, -.30, -.28, -.27, -.21, -.20, -.25, -.07, -.14)
        ),
        TempSurvey(
            1906,
            listOf(-.28, -.31, -.19, -.05, -.26, -.20, -.24, -.20, -.29, -.21, -.38, -.15)
        ),
        TempSurvey(
            1907,
            listOf(-.43, -.52, -.28, -.38, -.47, -.42, -.35, -.34, -.35, -.24, -.48, -.48)
        ),
        TempSurvey(
            1908,
            listOf(-.45, -.33, -.56, -.45, -.38, -.38, -.36, -.46, -.36, -.45, -.52, -.50)
        ),
        TempSurvey(
            1909,
            listOf(-.73, -.47, -.55, -.59, -.55, -.52, -.46, -.34, -.39, -.40, -.32, -.57)
        ),
        TempSurvey(
            1910,
            listOf(-.43, -.43, -.51, -.43, -.35, -.39, -.35, -.37, -.39, -.42, -.56, -.68)
        ),
        TempSurvey(
            1911,
            listOf(-.63, -.58, -.62, -.54, -.52, -.50, -.42, -.44, -.41, -.27, -.21, -.22)
        ),
        TempSurvey(
            1912,
            listOf(-.26, -.14, -.38, -.18, -.22, -.24, -.43, -.55, -.58, -.59, -.39, -.44)
        ),
        TempSurvey(
            1913,
            listOf(-.40, -.46, -.43, -.40, -.45, -.46, -.37, -.33, -.36, -.33, -.21, -.03)
        ),
        TempSurvey(
            1914,
            listOf(.04, -.10, -.25, -.30, -.22, -.26, -.24, -.17, -.18, -.04, -.16, -.05)
        ),
        TempSurvey(
            1915,
            listOf(-.21, -.04, -.10, .05, -.06, -.22, -.13, -.22, -.21, -.25, -.13, -.22)
        ),
        TempSurvey(
            1916,
            listOf(-.13, -.15, -.28, -.31, -.35, -.50, -.37, -.28, -.37, -.33, -.47, -.82)
        ),
        TempSurvey(
            1917,
            listOf(-.57, -.63, -.65, -.56, -.56, -.44, -.26, -.23, -.24, -.45, -.34, -.68)
        ),
        TempSurvey(
            1918,
            listOf(-.48, -.35, -.26, -.45, -.44, -.37, -.32, -.32, -.19, -.07, -.12, -.30)
        ),
        TempSurvey(
            1919,
            listOf(-.21, -.25, -.22, -.13, -.29, -.37, -.30, -.34, -.26, -.21, -.42, -.43)
        ),
        TempSurvey(
            1920,
            listOf(-.25, -.27, -.13, -.25, -.28, -.36, -.31, -.28, -.23, -.28, -.28, -.47)
        ),
        TempSurvey(
            1921,
            listOf(-.05, -.18, -.24, -.31, -.31, -.28, -.15, -.26, -.20, -.04, -.14, -.18)
        ),
        TempSurvey(
            1922,
            listOf(-.33, -.45, -.15, -.23, -.34, -.31, -.28, -.33, -.37, -.33, -.15, -.19)
        ),
        TempSurvey(
            1923,
            listOf(-.29, -.40, -.34, -.42, -.34, -.30, -.31, -.33, -.32, -.14, -.03, -.05)
        ),
        TempSurvey(
            1924,
            listOf(-.24, -.24, -.09, -.32, -.19, -.27, -.30, -.36, -.33, -.36, -.22, -.43)
        ),
        TempSurvey(
            1925,
            listOf(-.39, -.41, -.28, -.26, -.30, -.33, -.27, -.20, -.20, -.18, .04, .06)
        ),
        TempSurvey(
            1926,
            listOf(.20, .02, .10, -.13, -.24, -.26, -.28, -.14, -.16, -.12, -.07, -.30)
        ),
        TempSurvey(
            1927,
            listOf(-.28, -.19, -.39, -.31, -.26, -.28, -.20, -.24, -.13, -.02, -.06, -.34)
        ),
        TempSurvey(
            1928,
            listOf(-.03, -.09, -.26, -.29, -.30, -.39, -.20, -.23, -.22, -.20, -.09, -.17)
        ),
        TempSurvey(
            1929,
            listOf(-.46, -.59, -.34, -.42, -.39, -.44, -.37, -.32, -.26, -.15, -.12, -.55)
        ),
        TempSurvey(
            1930,
            listOf(-.30, -.27, -.12, -.26, -.25, -.22, -.22, -.16, -.16, -.12, .17, -.06)
        ),
        TempSurvey(
            1931,
            listOf(-.10, -.21, -.11, -.23, -.20, -.08, -.04, -.04, -.07, .04, -.06, -.06)
        ),
        TempSurvey(
            1932,
            listOf(.14, -.18, -.18, -.06, -.18, -.29, -.25, -.22, -.11, -.09, -.28, -.26)
        ),
        TempSurvey(
            1933,
            listOf(-.24, -.30, -.30, -.25, -.30, -.35, -.21, -.24, -.30, -.26, -.31, -.45)
        ),
        TempSurvey(
            1934,
            listOf(-.22, -.03, -.30, -.31, -.10, -.16, -.11, -.13, -.16, -.07, .03, -.03)
        ),
        TempSurvey(
            1935,
            listOf(-.34, .14, -.15, -.37, -.30, -.27, -.22, -.22, -.22, -.06, -.27, -.18)
        ),
        TempSurvey(
            1936,
            listOf(-.28, -.39, -.22, -.20, -.17, -.22, -.10, -.13, -.10, -.04, .01, -.02)
        ),
        TempSurvey(
            1937,
            listOf(-.08, .02, -.21, -.16, -.06, -.05, -.04, .01, .08, .08, .08, -.07)
        ),
        TempSurvey(
            1938,
            listOf(.08, .03, .09, .05, -.10, -.18, -.10, -.06, .00, .14, .07, -.13)
        ),
        TempSurvey(
            1939,
            listOf(-.06, -.07, -.18, -.10, -.05, -.07, -.06, -.06, -.08, -.04, .06, .43)
        ),
        TempSurvey(
            1940,
            listOf(-.01, .08, .09, .17, .10, .11, .12, .07, .15, .11, .16, .31)
        ),
        TempSurvey(
            1941,
            listOf(.18, .31, .09, .16, .16, .12, .21, .15, .02, .34, .22, .21)
        ),
        TempSurvey(
            1942,
            listOf(.29, .02, .05, .09, .10, .04, .00, -.04, -.04, .01, .09, .12)
        ),
        TempSurvey(
            1943,
            listOf(-.01, .17, -.04, .10, .06, -.06, .08, .01, .05, .22, .19, .23)
        ),
        TempSurvey(
            1944,
            listOf(.36, .24, .26, .19, .18, .15, .17, .18, .27, .26, .10, .03)
        ),
        TempSurvey(
            1945,
            listOf(.09, .00, .05, .19, .05, .00, .03, .26, .20, .18, .06, -.07)
        ),
        TempSurvey(
            1946,
            listOf(.15, .02, .01, .05, -.08, -.22, -.13, -.21, -.09, -.08, -.06, -.31)
        ),
        TempSurvey(
            1947,
            listOf(-.07, -.08, .06, .06, -.02, -.02, -.05, -.07, -.13, .07, .02, -.14)
        ),
        TempSurvey(
            1948,
            listOf(.06, -.15, -.25, -.12, -.01, -.05, -.12, -.12, -.15, -.06, -.13, -.24)
        ),
        TempSurvey(
            1949,
            listOf(.06, -.15, -.03, -.11, -.11, -.28, -.13, -.13, -.15, -.07, -.11, -.18)
        ),
        TempSurvey(
            1950,
            listOf(-.26, -.27, -.08, -.21, -.12, -.05, -.09, -.16, -.12, -.21, -.34, -.22)
        ),
        TempSurvey(
            1951,
            listOf(-.34, -.42, -.21, -.14, -.01, -.07, -.01, .06, .05, .07, -.01, .15)
        ),
        TempSurvey(
            1952,
            listOf(.11, .11, -.08, .03, -.03, -.03, .04, .05, .06, -.01, -.13, -.02)
        ),
        TempSurvey(
            1953,
            listOf(.07, .14, .10, .19, .11, .11, .00, .05, .04, .07, -.03, .04)
        ),
        TempSurvey(
            1954,
            listOf(-.24, -.10, -.15, -.14, -.20, -.19, -.19, -.18, -.10, -.02, .08, -.18)
        ),
        TempSurvey(
            1955,
            listOf(.13, -.16, -.32, -.22, -.20, -.14, -.12, .02, -.11, -.06, -.25, -.28)
        ),
        TempSurvey(
            1956,
            listOf(-.13, -.24, -.21, -.28, -.29, -.16, -.09, -.26, -.19, -.23, -.15, -.06)
        ),
        TempSurvey(
            1957,
            listOf(-.09, -.03, -.04, .01, .09, .16, .03, .13, .07, .00, .07, .14)
        ),
        TempSurvey(
            1958,
            listOf(.39, .21, .09, .01, .05, -.07, .06, -.03, -.02, .04, .02, .01)
        ),
        TempSurvey(
            1959,
            listOf(.08, .07, .17, .14, .05, .03, .03, .00, -.06, -.06, -.08, -.01)
        ),
        TempSurvey(
            1960,
            listOf(.00, .14, -.34, -.15, -.08, -.05, -.04, .03, .06, .05, -.11, .19)
        ),
        TempSurvey(
            1961,
            listOf(.07, .19, .10, .12, .13, .11, .01, .01, .08, .02, .03, -.16)
        ),
        TempSurvey(
            1962,
            listOf(.06, .16, .11, .06, -.07, .05, .02, -.02, .02, .02, .06, -.03)
        ),
        TempSurvey(
            1963,
            listOf(-.03, .19, -.13, -.07, -.06, .03, .07, .22, .18, .14, .14, -.03)
        ),
        TempSurvey(
            1964,
            listOf(-.09, -.10, -.22, -.32, -.25, -.05, -.05, -.21, -.30, -.31, -.22, -.30)
        ),
        TempSurvey(
            1965,
            listOf(-.08, -.17, -.13, -.20, -.11, -.08, -.14, -.05, -.14, -.06, -.06, -.08)
        ),
        TempSurvey(
            1966,
            listOf(-.19, -.05, .03, -.14, -.12, -.01, .08, -.08, -.03, -.16, -.01, -.03)
        ),
        TempSurvey(
            1967,
            listOf(-.08, -.21, .05, -.04, .11, -.08, .03, -.01, -.05, .10, -.04, -.05)
        ),
        TempSurvey(
            1968,
            listOf(-.26, -.15, .20, -.06, -.13, -.07, -.12, -.08, -.17, .09, -.05, -.14)
        ),
        TempSurvey(
            1969,
            listOf(-.11, -.17, .01, .17, .18, .04, -.04, .04, .08, .10, .12, .24)
        ),
        TempSurvey(
            1970,
            listOf(.08, .22, .06, .05, -.04, -.03, .01, -.10, .11, .03, .02, -.12)
        ),
        TempSurvey(
            1971,
            listOf(-.03, -.16, -.18, -.07, -.05, -.17, -.08, -.01, -.06, -.04, -.07, -.08)
        ),
        TempSurvey(
            1972,
            listOf(-.22, -.18, .02, .00, -.03, .04, .01, .16, .02, .08, .03, .17)
        ),
        TempSurvey(
            1973,
            listOf(.29, .33, .29, .28, .23, .19, .13, .05, .09, .10, .05, -.06)
        ),
        TempSurvey(
            1974,
            listOf(-.10, -.27, -.05, -.11, -.04, -.05, -.03, .11, -.07, -.05, -.07, -.08)
        ),
        TempSurvey(
            1975,
            listOf(.11, .08, .13, .04, .16, -.01, -.01, -.17, -.03, -.11, -.17, -.17)
        ),
        TempSurvey(
            1976,
            listOf(-.03, -.06, -.21, -.07, -.20, -.12, -.10, -.12, -.06, -.24, -.06, .11)
        ),
        TempSurvey(
            1977,
            listOf(.18, .23, .25, .27, .33, .27, .20, .18, .02, .03, .16, .03)
        ),
        TempSurvey(
            1978,
            listOf(.06, .10, .19, .17, .09, -.01, .05, -.13, .06, .03, .14, .08)
        ),
        TempSurvey(
            1979,
            listOf(.09, -.10, .19, .15, .04, .14, .04, .17, .25, .26, .29, .48)
        ),
        TempSurvey(
            1980,
            listOf(.30, .40, .30, .31, .35, .20, .22, .19, .21, .13, .30, .22)
        ),
        TempSurvey(
            1981,
            listOf(.53, .42, .48, .33, .25, .29, .32, .35, .15, .12, .23, .41)
        ),
        TempSurvey(
            1982,
            listOf(.05, .16, .03, .15, .18, .06, .15, .04, .14, .13, .18, .42)
        ),
        TempSurvey(
            1983,
            listOf(.53, .43, .42, .28, .33, .23, .18, .35, .37, .16, .30, .17)
        ),
        TempSurvey(
            1984,
            listOf(.31, .14, .26, .06, .33, .02, .19, .19, .21, .14, .07, -.04)
        ),
        TempSurvey(
            1985,
            listOf(.22, -.03, .17, .12, .15, .15, .04, .17, .13, .11, .05, .13)
        ),
        TempSurvey(
            1986,
            listOf(.28, .37, .30, .23, .21, .12, .11, .16, .03, .15, .11, .14)
        ),
        TempSurvey(
            1987,
            listOf(.33, .43, .18, .25, .25, .35, .40, .25, .35, .32, .29, .47)
        ),
        TempSurvey(
            1988,
            listOf(.57, .45, .51, .43, .44, .40, .33, .39, .36, .38, .12, .28)
        ),
        TempSurvey(
            1989,
            listOf(.12, .30, .36, .29, .17, .17, .34, .34, .35, .29, .19, .37)
        ),
        TempSurvey(
            1990,
            listOf(.42, .44, .80, .57, .46, .39, .46, .35, .24, .45, .47, .41)
        ),
        TempSurvey(
            1991,
            listOf(.43, .50, .35, .51, .34, .53, .47, .40, .44, .28, .30, .32)
        ),
        TempSurvey(
            1992,
            listOf(.47, .41, .48, .27, .30, .26, .09, .08, .00, .06, .03, .21)
        ),
        TempSurvey(
            1993,
            listOf(.34, .37, .36, .28, .29, .23, .25, .11, .12, .23, .04, .18)
        ),
        TempSurvey(
            1994,
            listOf(.26, .03, .30, .41, .28, .44, .30, .22, .30, .42, .45, .38)
        ),
        TempSurvey(
            1995,
            listOf(.52, .79, .47, .47, .28, .43, .46, .46, .34, .48, .45, .26)
        ),
        TempSurvey(
            1996,
            listOf(.23, .47, .33, .32, .28, .26, .36, .48, .26, .20, .39, .37)
        ),
        TempSurvey(
            1997,
            listOf(.30, .42, .52, .35, .36, .54, .34, .43, .53, .60, .64, .58)
        ),
        TempSurvey(
            1998,
            listOf(.59, .88, .64, .64, .67, .76, .68, .66, .42, .43, .44, .56)
        ),
        TempSurvey(
            1999,
            listOf(.49, .65, .33, .33, .28, .36, .39, .33, .39, .35, .38, .41)
        ),
        TempSurvey(
            2000,
            listOf(.26, .57, .55, .58, .36, .40, .38, .43, .40, .28, .31, .29)
        ),
        TempSurvey(
            2001,
            listOf(.46, .45, .56, .51, .58, .52, .60, .51, .53, .51, .73, .57)
        ),
        TempSurvey(
            2002,
            listOf(.78, .80, .88, .59, .63, .53, .61, .53, .62, .55, .59, .43)
        ),
        TempSurvey(
            2003,
            listOf(.76, .59, .60, .56, .62, .48, .58, .66, .63, .74, .54, .75)
        ),
        TempSurvey(
            2004,
            listOf(.59, .73, .64, .62, .39, .45, .27, .48, .52, .62, .73, .51)
        ),
        TempSurvey(
            2005,
            listOf(.74, .61, .75, .68, .63, .65, .62, .62, .71, .75, .74, .69)
        ),
        TempSurvey(
            2006,
            listOf(.57, .73, .63, .49, .50, .66, .55, .71, .66, .69, .73, .79)
        ),
        TempSurvey(
            2007,
            listOf(1.02, .71, .73, .76, .69, .61, .60, .60, .60, .60, .59, .50)
        ),
        TempSurvey(
            2008,
            listOf(.30, .39, .74, .54, .50, .50, .61, .48, .61, .66, .70, .55)
        ),
        TempSurvey(
            2009,
            listOf(.65, .54, .55, .61, .66, .65, .72, .68, .73, .66, .81, .68)
        ),
        TempSurvey(
            2010,
            listOf(.76, .84, .92, .85, .76, .69, .64, .67, .64, .71, .83, .45)
        ),
        TempSurvey(
            2011,
            listOf(.52, .48, .65, .65, .52, .61, .71, .73, .58, .66, .59, .61)
        ),
        TempSurvey(
            2012,
            listOf(.49, .49, .58, .72, .78, .65, .59, .64, .72, .79, .79, .53)
        ),
        TempSurvey(
            2013,
            listOf(.71, .63, .68, .57, .62, .71, .62, .71, .78, .70, .85, .70)
        ),
        TempSurvey(
            2014,
            listOf(.76, .56, .80, .81, .86, .67, .59, .81, .85, .80, .67, .80)
        ),
        TempSurvey(
            2015,
            listOf(.86, .90, .96, .77, .79, .82, .75, .83, .84, 1.09, 1.06, 1.16)
        ),
        TempSurvey(
            2016,
            listOf(1.17, 1.37, 1.36, 1.12, .96, .82, .85, 1.02, .92, .88, .91, .86)
        ),
        TempSurvey(
            2017,
            listOf(1.04, 1.14, 1.16, .94, .90, .73, .82, .87, .79, .90, .89, .95)
        ),
        TempSurvey(
            2018,
            listOf(.83, .85, .90, .90, .82, .78, .82, .77, .81, 1.00, .83, .92)
        ),
        TempSurvey(
            2019,
            listOf(.94, .95, 1.18, 1.02, .87, .93, .94, .90)
        )
    )
