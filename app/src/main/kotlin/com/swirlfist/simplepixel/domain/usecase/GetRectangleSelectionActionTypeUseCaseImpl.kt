package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import com.swirlfist.simplepixel.presentation.bottom
import com.swirlfist.simplepixel.presentation.bottomLeft
import com.swirlfist.simplepixel.presentation.bottomRight
import com.swirlfist.simplepixel.presentation.left
import com.swirlfist.simplepixel.presentation.right
import com.swirlfist.simplepixel.presentation.top
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
    ): RectangleSelectionActionType {
        if (rectangleSelection == null) {
            return RectangleSelectionActionType.NEW_SELECTION
        }

        val pixelsDelta = if (zoomFactor >= 1) 1 else (1 / zoomFactor).toInt()

        return when {
            rectangleSelection.topLeft().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_TOP_LEFT_CORNER
            rectangleSelection.topRight().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_TOP_RIGHT_CORNER
            rectangleSelection.bottomLeft().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_BOTTOM_LEFT_CORNER
            rectangleSelection.bottomRight().isCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_BOTTOM_RIGHT_CORNER
            rectangleSelection.isTopCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_VERTICAL_TOP
            rectangleSelection.isBottomCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_VERTICAL_BOTTOM
            rectangleSelection.isLeftCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_HORIZONTAL_LEFT
            rectangleSelection.isRightCloseTo(x, y, pixelsDelta) -> RectangleSelectionActionType.RESIZE_HORIZONTAL_RIGHT

            else -> RectangleSelectionActionType.NEW_SELECTION
        }
    }

    private fun Pair<Int, Int>.isCloseTo(
        x: Int,
        y: Int,
        delta: Int,
    ) = x in (first - delta..first + delta) && y in (second - delta..second + delta)

    private fun RectangleSelectionModel.isTopCloseTo(
        x: Int,
        y: Int,
        delta: Int,
    ): Boolean {
        val top = top()
        val left = left()
        val right = right()

        return x in (left - delta..right + delta) && y in (top - delta..top + delta)
    }

    private fun RectangleSelectionModel.isBottomCloseTo(
        x: Int,
        y: Int,
        delta: Int,
    ): Boolean {
        val bottom = bottom()
        val left = left()
        val right = right()

        return x in (left - delta..right + delta) && y in (bottom - delta..bottom + delta)
    }

    private fun RectangleSelectionModel.isLeftCloseTo(
        x: Int,
        y: Int,
        delta: Int,
    ): Boolean {
        val top = top()
        val bottom = bottom()
        val left = left()

        return x in (left - delta..left + delta) && y in (top - delta..bottom + delta)
    }

    private fun RectangleSelectionModel.isRightCloseTo(
        x: Int,
        y: Int,
        delta: Int,
    ): Boolean {
        val top = top()
        val bottom = bottom()
        val right = right()

        return x in (right - delta..right + delta) && y in (top - delta..bottom + delta)
    }
}