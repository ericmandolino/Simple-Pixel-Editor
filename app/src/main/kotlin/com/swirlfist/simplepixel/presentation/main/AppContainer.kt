package com.swirlfist.simplepixel.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.swirlfist.simplepixel.presentation.main.screen.MainScreen
import com.swirlfist.simplepixel.presentation.main.screen.StartScreen
import kotlinx.serialization.Serializable

@Serializable
object Start

@Serializable
object Main

@Composable
fun SimplePixelApp() {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = Start,
    ) {
        composable<Start> {
            StartScreen(
                onNavigateToMain = {
                    navController.navigate(
                        route = Main,
                    )
                }
            )
        }
        composable<Main> {
            MainScreen()
        }
    }
}