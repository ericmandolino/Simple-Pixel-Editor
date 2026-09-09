package com.swirlfist.simplepixel.presentation.state

data class MainScreenState(
    val canvasSectionState: CanvasSectionState,
    val actionsSectionState: ActionsSectionState,
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState,
    val isShowPalette: Boolean = false,
    val isShowEditPalette: Boolean = false,
    val launcherState: MainScreenLauncherState = MainScreenLauncherState(),
)

data class MainScreenLauncherState(
    val launchSelectSavePixelImage: Boolean = false,
    val launchSelectExportPixelImage: Boolean = false,
    val launchSelectOpenPixelImage: Boolean = false,
)
