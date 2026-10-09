package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.domain.usecase.MoveDirection

sealed interface PixelImageEditorAction {

    val pixelImage: PixelImageModel
    val pixelSelection: PixelSelectionModel?

    data class ApplyPixelColorAction(
        override val pixelImage: PixelImageModel,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
        override val pixelSelection: PixelSelectionModel? = null,
    ) : PixelImageEditorAction

    data class ApplyBucketColorAction(
        override val pixelImage: PixelImageModel,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
        override val pixelSelection: PixelSelectionModel? = null,
    ) : PixelImageEditorAction

    data class MoveImageAction(
        override val pixelImage: PixelImageModel,
        val moveDirection: MoveDirection,
        override val pixelSelection: PixelSelectionModel? = null,
    ) : PixelImageEditorAction

    data class SelectTouchAction(
        override val pixelImage: PixelImageModel,
        override val pixelSelection: PixelSelectionModel,
        val x: Int,
        val y: Int,
        val selected: Boolean,
    ) : PixelImageEditorAction

    data class SelectMagicWandAction(
        override val pixelImage: PixelImageModel,
        override val pixelSelection: PixelSelectionModel,
        val x: Int,
        val y: Int,
    ) : PixelImageEditorAction
}

