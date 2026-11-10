package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.domain.usecase.MoveDirection
import com.swirlfist.simplepixel.domain.usecase.RectangleSelectionActionType

sealed interface PixelImageEditorAction {

    val actionContext: PixelImageEditorActionContext

    data class ApplyPixelColorAction(
        override val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : PixelImageEditorAction

    data class ApplyBucketColorAction(
        override val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : PixelImageEditorAction

    data class MoveImageAction(
        override val actionContext: PixelImageEditorActionContext,
        val moveDirection: MoveDirection,
    ) : PixelImageEditorAction

    data class SelectTouchAction(
        override val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
        val selected: Boolean,
    ) : PixelImageEditorAction

    data class SelectMagicWandAction(
        override val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
    ) : PixelImageEditorAction

    data class SelectRectangleAction(
        override val actionContext: PixelImageEditorActionContext,
        val actionType: RectangleSelectionActionType,
        val x: Int,
        val y: Int,
    ) : PixelImageEditorAction

    data class DeselectAction(
        override val actionContext: PixelImageEditorActionContext,
    ) : PixelImageEditorAction

    data class ConvertSelectionToFloatingImageAction(
        override val actionContext: PixelImageEditorActionContext,
    ) : PixelImageEditorAction

    data class LandFloatingImageAction(
        override val actionContext: PixelImageEditorActionContext,
    ) : PixelImageEditorAction
}

