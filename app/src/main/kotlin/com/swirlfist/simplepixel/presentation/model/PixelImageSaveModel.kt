package com.swirlfist.simplepixel.presentation.model

import kotlinx.serialization.Serializable

@Serializable
data class PixelImageSaveModel(
    val pixels: List<List<Int>>,
    val palette: List<Long>,
)