package com.swirlfist.simplepixel.presentation.state

import com.swirlfist.simplepixel.domain.error.OpenPixelImageError

data class StartScreenState(
    val isNavigateToMainExpected: Boolean = false,
    val isLoadingImage: Boolean = false,
    val openPixelImageError: OpenPixelImageError? = null,
    val launcherState: StartScreenLauncherState = StartScreenLauncherState(),
)

data class StartScreenLauncherState(
    val launchSelectOpenPixelImage: Boolean = false,
)
