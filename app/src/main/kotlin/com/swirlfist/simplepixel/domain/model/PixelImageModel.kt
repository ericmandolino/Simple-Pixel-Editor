package com.swirlfist.simplepixel.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PixelImageModel(
    val pixelMatrixModel: PixelMatrixModel,
    val paletteModel: PaletteModel,
)