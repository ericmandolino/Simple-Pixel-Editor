package com.swirlfist.simplepixel.domain.model

data class FloatingPixelImageModel(
    val pixelImageModel: PixelImageModel,
    val offset: Pair<Int, Int> = Pair(0, 0),
)
