package com.swirlfist.simplepixel.domain.model

data class PixelSelectionModel(
    val selected: List<List<PixelModel?>>,
    val rectangleSelection: RectangleSelectionModel? = null,
)
