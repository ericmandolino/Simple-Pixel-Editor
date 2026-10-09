package com.swirlfist.simplepixel.presentation.state

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

data class CanvasSectionState(
    val pixelImageModel: PixelImageModel? = null,
    val pixelSelectionModel: PixelSelectionModel? = null,
    val zoomFactor: Float = 1F,
    val isShowGridEnabled: Boolean = true,
    val isVisitingPixels: Boolean = false,
    val lastVisitedPixel: Pair<Int, Int>? = null,
)