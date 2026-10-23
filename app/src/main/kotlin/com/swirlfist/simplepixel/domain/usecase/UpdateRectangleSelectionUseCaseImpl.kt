package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.UpdateRectangleSelectionError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import com.swirlfist.simplepixel.presentation.bottomLeft
import com.swirlfist.simplepixel.presentation.bottomRight
import com.swirlfist.simplepixel.presentation.createSelection
import com.swirlfist.simplepixel.presentation.topLeft
import com.swirlfist.simplepixel.presentation.topRight
import javax.inject.Inject

class UpdateRectangleSelectionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : UpdateRectangleSelectionUseCase {
    override suspend fun invoke(params: UpdateRectangleSelectionUseCase.Params): Result<PixelSelectionModel> {
        return try {
            Result.success(
                updateRectangleSelection(
                    pixelImage = params.pixelImageModel,
                    pixelSelection = params.pixelSelectionModel ?:
                    params.pixelImageModel.createSelection(selectedCoordinates = listOf()),
                    x = params.x,
                    y = params.y,
                    actionType = params.actionType,
                    isSameAction = params.isSameAction,
                )
            )
        } catch(exception: Exception) {
            Result.failure(UpdateRectangleSelectionError(exception))
        }
    }

    private fun updateRectangleSelection(
        pixelImage: PixelImageModel,
        pixelSelection: PixelSelectionModel,
        x: Int,
        y: Int,
        actionType: RectangleSelectionActionType,
        isSameAction: Boolean,
    ): PixelSelectionModel {
        return when (actionType) {
            RectangleSelectionActionType.NEW_SELECTION -> updateNewSelection(pixelSelection, x, y, isSameAction)
            RectangleSelectionActionType.RESIZE_VERTICAL_TOP -> TODO()
            RectangleSelectionActionType.RESIZE_VERTICAL_BOTTOM -> TODO()
            RectangleSelectionActionType.RESIZE_HORIZONTAL_LEFT -> TODO()
            RectangleSelectionActionType.RESIZE_HORIZONTAL_BOTTOM -> TODO()
            RectangleSelectionActionType.RESIZE_TOP_LEFT_CORNER,
            RectangleSelectionActionType.RESIZE_TOP_RIGHT_CORNER,
            RectangleSelectionActionType.RESIZE_BOTTOM_LEFT_CORNER,
            RectangleSelectionActionType.RESIZE_BOTTOM_RIGHT_CORNER,
                -> resizeSelectionFromCorner(pixelSelection, x, y, actionType, isSameAction)
        }
    }

    private fun updateNewSelection(
        pixelSelection: PixelSelectionModel,
        x: Int,
        y: Int,
        isSameAction: Boolean,
    ): PixelSelectionModel {
        val updatedRectangleSelection = if (!isSameAction) {
            RectangleSelectionModel(
                start = Pair(x, y),
                end = Pair(x, y),
            )
        } else {
            pixelSelection.rectangleSelection?.copy(
                end = Pair(x, y)
            ) ?: RectangleSelectionModel(
                start = Pair(x, y),
                end = Pair(x, y),
            )
        }


        val updatedPixelSelection = pixelSelection.copy(
            rectangleSelection = updatedRectangleSelection,
        )

        return updatedPixelSelection
    }

    private fun resizeSelectionFromCorner(
        pixelSelection: PixelSelectionModel,
        x: Int,
        y: Int,
        actionType: RectangleSelectionActionType,
        isSameAction: Boolean,
    ): PixelSelectionModel {
        val rectangleSelection = pixelSelection.rectangleSelection ?: return pixelSelection

        val adjustedRectangleSelection = if (!isSameAction) {
            when (actionType) {
                RectangleSelectionActionType.RESIZE_TOP_LEFT_CORNER -> rectangleSelection.startFromBottomRight()
                RectangleSelectionActionType.RESIZE_TOP_RIGHT_CORNER -> rectangleSelection.startFromBottomLeft()
                RectangleSelectionActionType.RESIZE_BOTTOM_LEFT_CORNER -> rectangleSelection.startFromTopRight()
                RectangleSelectionActionType.RESIZE_BOTTOM_RIGHT_CORNER -> rectangleSelection.startFromTopLeft()
                else -> null
            }
        } else {
            rectangleSelection
        } ?: return pixelSelection

        val updatedRectangleSelection = resizeSelection(
            rectangleSelection = adjustedRectangleSelection,
            x,
            y,
        )

        return pixelSelection.copy(
            rectangleSelection = updatedRectangleSelection,
        )
    }

    private fun resizeSelection(
        rectangleSelection: RectangleSelectionModel,
        x: Int,
        y: Int,
    ): RectangleSelectionModel {
        return rectangleSelection.copy(
            end = Pair(x, y),
        )
    }

    private fun RectangleSelectionModel.startFromTopLeft() = copy(
        start = topLeft(),
        end = bottomRight(),
    )

    private fun RectangleSelectionModel.startFromTopRight() = copy(
        start = topRight(),
        end = bottomLeft(),
    )

    private fun RectangleSelectionModel.startFromBottomLeft() = copy(
        start = bottomLeft(),
        end = topRight(),
    )

    private fun RectangleSelectionModel.startFromBottomRight() = copy(
        start = bottomRight(),
        end = topLeft(),
    )
}