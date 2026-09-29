package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.TogglePixelSelectionError
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import com.swirlfist.simplepixel.presentation.createSelection
import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.presentation.isSelected
import javax.inject.Inject

class TogglePixelSelectionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : TogglePixelSelectionUseCase {
    override suspend fun invoke(params: TogglePixelSelectionUseCase.Params): Result<PixelSelectionModel> {
        return try {
            Result.success(
                togglePixelSelection(
                    pixelImage = params.pixelImageModel,
                    pixelSelection = params.pixelSelectionModel ?:
                        params.pixelImageModel.createSelection(selectedCoordinates = listOf()),
                    x = params.x,
                    y = params.y,
                    isSameAction = params.isSameAction,
                )
            )
        } catch(exception: Exception) {
            Result.failure(TogglePixelSelectionError(exception))
        }
    }

    private suspend fun togglePixelSelection(
        pixelImage: PixelImageModel,
        pixelSelection: PixelSelectionModel,
        x: Int,
        y: Int,
        isSameAction: Boolean,
    ): PixelSelectionModel {
        val pixelInSelection = pixelSelection.isSelected(x, y)
        val selectedPixels = pixelSelection.selected

        val updatedSelectionRow = selectedPixels[y].toMutableList().apply {
            set(
                index = x,
                element = if (pixelInSelection) {
                    null
                } else {
                    pixelImage.getPixelAt(x, y)
                }
            )
        }.toList()
        val updatedPixelSelection = pixelSelection.copy(
            selected = selectedPixels.toMutableList().apply {
                set(
                    index = y,
                    element = updatedSelectionRow,
                )
            }.toList()
        )

        if (isSameAction) {
            pixelImageEditorActionRepository.updateLastAction(
                pixelImageResult = pixelImage,
                pixelSelectionResult = updatedPixelSelection,
            )
        } else {
            pixelImageEditorActionRepository.addAction(
                PixelImageEditorAction.SelectTouchAction(
                    pixelImage,
                    pixelSelection = pixelSelection,
                    x,
                    y,
                    selected = !pixelInSelection,
                ),
                pixelImageResult = pixelImage,
                pixelSelectionResult = updatedPixelSelection,
            )
        }

        return updatedPixelSelection
    }
}