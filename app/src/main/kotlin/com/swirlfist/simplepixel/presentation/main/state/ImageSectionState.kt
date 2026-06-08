package com.swirlfist.simplepixel.presentation.main.state

import com.swirlfist.simplepixel.domain.model.PixelImageModel

data class ImageSectionState(
    val pixelImageModel: PixelImageModel? = null,
    val zoomFactor: Float = 1F,
    val isShowCoordinatesEnabled: Boolean = true,
)