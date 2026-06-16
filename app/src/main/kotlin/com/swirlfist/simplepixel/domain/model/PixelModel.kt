package com.swirlfist.simplepixel.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PixelModel(
    val paletteIndex: Int,
)