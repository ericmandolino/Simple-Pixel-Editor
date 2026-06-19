package com.swirlfist.simplepixel.presentation.main.state

data class MainScreenState(
    val canvasSectionState: CanvasSectionState,
    val actionsSectionState: ActionsSectionState,
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState
)
