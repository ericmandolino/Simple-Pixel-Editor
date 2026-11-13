package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel

interface GetRectangleSelectionActionTypeUseCase : UseCase<GetRectangleSelectionActionTypeUseCase.Params, RectangleSelectionActionType> {

    data class Params(
        val rectangleSelectionModel: RectangleSelectionModel?,
        val x: Int,
        val y: Int,
        val zoomFactor: Float,
    ) : UseCaseParams
}

enum class RectangleSelectionActionType {
    NEW_SELECTION,
    RESIZE_VERTICAL_TOP,
    RESIZE_VERTICAL_BOTTOM,
    RESIZE_HORIZONTAL_LEFT,
    RESIZE_HORIZONTAL_RIGHT,
    RESIZE_TOP_LEFT_CORNER,
    RESIZE_TOP_RIGHT_CORNER,
    RESIZE_BOTTOM_LEFT_CORNER,
    RESIZE_BOTTOM_RIGHT_CORNER,
}