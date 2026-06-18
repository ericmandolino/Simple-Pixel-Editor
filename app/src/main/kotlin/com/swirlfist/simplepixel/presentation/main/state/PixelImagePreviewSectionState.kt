package com.swirlfist.simplepixel.presentation.main.state

import com.swirlfist.simplepixel.domain.model.PixelImageModel

data class PixelImagePreviewSectionState (
    val pixelImageModel: PixelImageModel? = null,
    val isFitAvailableSpace: Boolean = false,
)