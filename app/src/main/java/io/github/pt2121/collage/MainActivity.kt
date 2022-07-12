package io.github.pt2121.collage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import io.github.pt2121.collage.sample.GlobalTemperature
import io.github.pt2121.collage.sample.WindSimulation
import io.github.pt2121.collage.ui.theme.CollageTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CollageTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .recomposeHighlighter(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WindSimulation()
                }
            }
        }
    }
}
