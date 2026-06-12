package com.swirlfist.simplepixel.presentation.main.state

import com.swirlfist.simplepixel.domain.model.PixelImageModel

data class CanvasSectionState(
    val pixelImageModel: PixelImageModel? = null,
    val zoomFactor: Float = 1F,
    val isShowGridEnabled: Boolean = true,
    val isShowCoordinatesEnabled: Boolean = true,
)