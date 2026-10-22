package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface GetRectangleSelectionActionTypeUseCase : UseCase<GetRectangleSelectionActionTypeUseCase.Params, RectangleSelectionActionType> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val x: Int,
        val y: Int,
    ) : UseCaseParams
}

enum class RectangleSelectionActionType {
    NEW_SELECTION,
    RESIZE_VERTICAL_TOP,
    RESIZE_VERTICAL_BOTTOM,
    RESIZE_HORIZONTAL_LEFT,
    RESIZE_HORIZONTAL_BOTTOM,
    RESIZE_UPPER_LEFT_CORNER,
    RESIZE_UPPER_RIGHT_CORNER,
    RESIZE_LOWER_LEFT_CORNER,
    RESIZE_LOWER_RIGHT_CORNER,
}