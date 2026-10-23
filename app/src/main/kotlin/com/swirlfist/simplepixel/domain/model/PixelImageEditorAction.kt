package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.domain.usecase.MoveDirection
import com.swirlfist.simplepixel.domain.usecase.RectangleSelectionActionType

sealed interface PixelImageEditorAction {

    val pixelImage: PixelImageModel
    val pixelSelection: PixelSelectionModel?

    data class ApplyPixelColorAction(
        override val pixelImage: PixelImageModel,
        override val pixelSelection: PixelSelectionModel?,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : PixelImageEditorAction

    data class ApplyBucketColorAction(
        override val pixelImage: PixelImageModel,
        override val pixelSelection: PixelSelectionModel?,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : PixelImageEditorAction

    data class MoveImageAction(
        override val pixelImage: PixelImageModel,
        override val pixelSelection: PixelSelectionModel?,
        val moveDirection: MoveDirection,
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

    data class SelectRectangleAction(
        override val pixelImage: PixelImageModel,
        override val pixelSelection: PixelSelectionModel,
        val actionType: RectangleSelectionActionType,
        val x: Int,
        val y: Int,
    ) : PixelImageEditorAction
}

