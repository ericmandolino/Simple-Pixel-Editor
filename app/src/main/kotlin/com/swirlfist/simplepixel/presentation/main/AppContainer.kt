package com.swirlfist.simplepixel.presentation.main

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import com.swirlfist.simplepixel.presentation.main.screen.MainScreen
import com.swirlfist.simplepixel.presentation.main.screen.NewImageScreen
import com.swirlfist.simplepixel.presentation.main.screen.StartScreen
import kotlinx.serialization.Serializable

@Serializable
object Start

@Serializable
object Main

@Serializable
object NewImage

@Composable
fun SimplePixelApp() {
    val navController = rememberNavController()
    NavHost(
        navController,
        startDestination = Start,
    ) {
        composable<Start> {
            StartScreen(
                navigateToNewImage = {
                    navController.navigate(
                        route = NewImage,
                    )
                },
                navigateToMain = {
                    navController.navigate(
                        route = Main,
                    )
                }
            )
        }
        composable<Main> {
            MainScreen()
        }
        dialog<NewImage> {
            NewImageScreen(
                navigateToMain = {
                    navController.popBackStack()
                    navController.navigate(
                        route = Main,
                    )
                }
            )
        }
    }
}