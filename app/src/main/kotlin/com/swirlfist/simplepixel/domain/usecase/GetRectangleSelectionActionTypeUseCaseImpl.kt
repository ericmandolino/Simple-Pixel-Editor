package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import com.swirlfist.simplepixel.presentation.bottomLeft
import com.swirlfist.simplepixel.presentation.bottomRight
import com.swirlfist.simplepixel.presentation.topLeft
import com.swirlfist.simplepixel.presentation.topRight
import javax.inject.Inject

class GetRectangleSelectionActionTypeUseCaseImpl @Inject constructor() : GetRectangleSelectionActionTypeUseCase {
    override suspend fun invoke(params: GetRectangleSelectionActionTypeUseCase.Params): Result<RectangleSelectionActionType> {
        return Result.success(
            getRectangleSelectionActionType(
                rectangleSelection = params.rectangleSelectionModel,
                x = params.x,
                y = params.y,
                zoomFactor = params.zoomFactor,
            )
        )
    }

    private fun getRectangleSelectionActionType(
        rectangleSelection: RectangleSelectionModel?,
        x: Int,
        y: Int,
        zoomFactor: Float,
    ) : RectangleSelectionActionType {
        if (rectangleSelection == null) {
            return RectangleSelectionActionType.NEW_SELECTION
        }

        val pixelsDelta = if (zoomFactor >= 1) 1 else (1 / zoomFactor).toInt()

        return when {
            rectangleSelection.topLeft().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_TOP_LEFT_CORNER
            rectangleSelection.topRight().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_TOP_RIGHT_CORNER
            rectangleSelection.bottomLeft().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_BOTTOM_LEFT_CORNER
            rectangleSelection.bottomRight().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_BOTTOM_RIGHT_CORNER
            else -> RectangleSelectionActionType.NEW_SELECTION
        }
    }

    private fun Pair<Int, Int>.isCloseTo(
        x: Int,
        y: Int,
        delta: Int,
    ) = x in (first - delta..first + delta) && y in (second - delta .. second + delta)
}