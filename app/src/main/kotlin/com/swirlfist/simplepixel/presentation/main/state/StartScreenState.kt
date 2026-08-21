package com.swirlfist.simplepixel.presentation.main.state

data class StartScreenState(
    val isNavigateToMainExpected: Boolean = false,
    val isLoadingImage: Boolean = false,
    val launcherState: StartScreenLauncherState = StartScreenLauncherState(),
)

data class StartScreenLauncherState(
    val launchSelectOpenPixelImage: Boolean = false,
)
