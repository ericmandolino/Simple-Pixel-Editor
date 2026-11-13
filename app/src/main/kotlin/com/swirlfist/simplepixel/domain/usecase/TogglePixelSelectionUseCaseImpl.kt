package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.createSelection
import com.swirlfist.simplepixel.domain.error.TogglePixelSelectionError
import com.swirlfist.simplepixel.domain.getPixelAt
import com.swirlfist.simplepixel.domain.isSelected
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import javax.inject.Inject

class TogglePixelSelectionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : TogglePixelSelectionUseCase {
    override suspend fun invoke(params: TogglePixelSelectionUseCase.Params): Result<PixelSelectionModel> {
        return try {
            Result.success(
                togglePixelSelection(
                    actionContext = params.actionContext,
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
        actionContext: PixelImageEditorActionContext,
        x: Int,
        y: Int,
        isSameAction: Boolean,
    ): PixelSelectionModel {
        val pixelImage = actionContext.pixelImage
        val pixelSelection = actionContext.pixelSelection ?:
            pixelImage.createSelection(selectedCoordinates = listOf())
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

        val pixelSelectionResult = pixelSelection.copy(
            selected = selectedPixels.toMutableList().apply {
                set(
                    index = y,
                    element = updatedSelectionRow,
                )
            }.toList()
        )
        val actionContextResult = actionContext.copy(
            pixelSelection = pixelSelectionResult,
        )

        if (isSameAction) {
            pixelImageEditorActionRepository.updateLastAction(
                actionContextResult,
            )
        } else {
            pixelImageEditorActionRepository.addAction(
                PixelImageEditorAction.SelectTouchAction(
                    actionContext,
                    x,
                    y,
                    selected = !pixelInSelection,
                ),
                actionContextResult,
            )
        }

        return pixelSelectionResult
    }
}