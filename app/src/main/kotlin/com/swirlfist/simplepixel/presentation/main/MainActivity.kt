package com.swirlfist.simplepixel.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.swirlfist.simplepixel.presentation.PixelCanvas
import com.swirlfist.simplepixel.presentation.createCheckersPixelImage
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimplePixelTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PixelCanvas(
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        pixelImage = createCheckersPixelImage(
                            width = 5,
                            height = 3,
                            color1 = Color.Black,
                            color2 = Color.Yellow,
                        ),
                        zoomFactor = 4F,
                        initialImageDeltaX = 0F,
                        initialImageDeltaY = 0F,
                        isShowCoordinatesEnabled = true,
                        onPixelTap = { x, y -> }
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun GreetingPreview() {
    SimplePixelTheme {
        Greeting("Android")
    }
}