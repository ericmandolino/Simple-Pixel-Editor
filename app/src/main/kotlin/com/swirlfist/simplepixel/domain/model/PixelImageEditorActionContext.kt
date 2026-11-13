package com.swirlfist.simplepixel.domain.model

data class PixelImageEditorActionContext(
    val pixelImage: PixelImageModel,
    val pixelSelection: PixelSelectionModel? = null,
    val floatingPixelImage: FloatingPixelImageModel? = null,
)
