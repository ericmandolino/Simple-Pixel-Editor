package com.swirlfist.simplepixel.domain.model

import kotlinx.serialization.Serializable

const val EMPTY_PIXEL_PALETTE_INDEX = -1

@Serializable
data class PixelModel(
    val paletteIndex: Int,
)