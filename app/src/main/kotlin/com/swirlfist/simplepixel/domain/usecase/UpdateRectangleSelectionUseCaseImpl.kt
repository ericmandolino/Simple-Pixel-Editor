package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.UpdateRectangleSelectionError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import com.swirlfist.simplepixel.presentation.createSelection
import com.swirlfist.simplepixel.presentation.getPixelHeight
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
                    y = params.pixelImageModel.getPixelHeight() - 1 - params.y,
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
            RectangleSelectionActionType.RESIZE_UPPER_LEFT_CORNER -> TODO()
            RectangleSelectionActionType.RESIZE_UPPER_RIGHT_CORNER -> TODO()
            RectangleSelectionActionType.RESIZE_LOWER_LEFT_CORNER -> TODO()
            RectangleSelectionActionType.RESIZE_LOWER_RIGHT_CORNER -> TODO()
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
}