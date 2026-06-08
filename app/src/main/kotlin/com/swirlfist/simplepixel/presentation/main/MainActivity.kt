package com.swirlfist.simplepixel.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.swirlfist.simplepixel.presentation.main.screen.MainScreen
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SimplePixelTheme {
                MainScreen()
            }
        }
    }
}