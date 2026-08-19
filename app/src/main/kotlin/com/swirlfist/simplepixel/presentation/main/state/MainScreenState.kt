package com.swirlfist.simplepixel.presentation.main.state

data class MainScreenState(
    val canvasSectionState: CanvasSectionState,
    val actionsSectionState: ActionsSectionState,
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState,
    val launcherState: LauncherState = LauncherState(),
)

data class LauncherState(
    val launchSelectSavePixelImage: Boolean = false,
    val launchSelectExportPixelImage: Boolean = false,
    val launchSelectOpenPixelImage: Boolean = false,
)
