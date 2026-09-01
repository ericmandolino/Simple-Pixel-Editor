package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.domain.usecase.MoveDirection

sealed interface PixelImageEditorAction {

    val pixelImage: PixelImageModel

    data class ApplyPixelColorAction(
        override val pixelImage: PixelImageModel,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : PixelImageEditorAction

    data class ApplyBucketColorAction(
        override val pixelImage: PixelImageModel,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : PixelImageEditorAction

    data class MoveImageAction(
        override val pixelImage: PixelImageModel,
        val moveDirection: MoveDirection,
    ) : PixelImageEditorAction
}

