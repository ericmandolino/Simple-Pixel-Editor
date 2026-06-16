package com.swirlfist.simplepixel.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PixelMatrixModel(
    val content: List<List<PixelModel>>,
)