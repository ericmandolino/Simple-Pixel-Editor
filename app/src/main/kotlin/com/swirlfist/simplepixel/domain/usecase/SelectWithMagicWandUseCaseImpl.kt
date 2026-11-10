package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.createSelection
import com.swirlfist.simplepixel.domain.error.MagicWandSelectionError
import com.swirlfist.simplepixel.domain.getPixelAt
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import javax.inject.Inject

class SelectWithMagicWandUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : SelectWithMagicWandUseCase {
    override suspend fun invoke(params: SelectWithMagicWandUseCase.Params): Result<PixelSelectionModel> {
        return try {
            Result.success(
                selectWithMagicWand(
                    actionContext = params.actionContext,
                    x = params.x,
                    y = params.y,
                )
            )
        } catch (exception: Exception) {
            Result.failure(MagicWandSelectionError(exception))
        }
    }

    private suspend fun selectWithMagicWand(
        actionContext: PixelImageEditorActionContext,
        x: Int,
        y: Int,
    ): PixelSelectionModel {
        val pixelImage = actionContext.pixelImage
        val pixelSelection = actionContext.pixelSelection ?:
            pixelImage.createSelection(selectedCoordinates = listOf())
        val pixel = pixelImage.getPixelAt(x, y)
        val mutableSelectionMatrix = mutableListOf<MutableList<PixelModel?>>().apply {
            pixelSelection.selected.forEach { row ->
                add(row.toMutableList())
            }
        }
        val pixelMatrix = pixelImage.pixelMatrixModel.content
        val visitedPixels = mutableListOf<MutableList<Boolean>>().apply {
            pixelMatrix.forEach { row ->
                add(row.map { false }.toMutableList())
            }
        }

        applySelection(
            pixelMatrix,
            visitedPixels,
            pixelSelection = mutableSelectionMatrix,
            x,
            y,
            paletteIndexToSelect = pixel.paletteIndex,
        )

        val pixelSelectionResult = pixelSelection.copy(
            selected = mutableSelectionMatrix.toList()
        )
        val actionContextResult = actionContext.copy(
            pixelSelection = pixelSelectionResult,
        )

        if (pixelSelectionResult != pixelSelection) {
            pixelImageEditorActionRepository.addAction(
                PixelImageEditorAction.SelectMagicWandAction(
                    actionContext,
                    x,
                    y,
                ),
                actionContextResult,
            )
        }

        return pixelSelectionResult
    }

    private fun applySelection(
        pixelMatrix: List<List<PixelModel>>,
        visitedPixels: MutableList<MutableList<Boolean>>,
        pixelSelection: MutableList<MutableList<PixelModel?>>,
        x: Int,
        y: Int,
        paletteIndexToSelect: Int,
    ) {
        val size = pixelMatrix.size

        if (x !in 0..<size || y !in 0..<size || visitedPixels[y][x]) {
            return
        }

        visitedPixels[y][x] = true
        val addedToSelection = addToSelection(pixelMatrix, pixelSelection, x, y, paletteIndexToSelect)

        if (!addedToSelection) {
            return
        }

        applySelection(pixelMatrix, visitedPixels, pixelSelection, x = x - 1, y, paletteIndexToSelect)
        applySelection(pixelMatrix, visitedPixels, pixelSelection, x = x + 1, y, paletteIndexToSelect)
        applySelection(pixelMatrix, visitedPixels, pixelSelection, x, y = y - 1, paletteIndexToSelect)
        applySelection(pixelMatrix, visitedPixels, pixelSelection, x, y = y + 1, paletteIndexToSelect)
    }

    private fun addToSelection(
        pixelMatrix: List<List<PixelModel>>,
        pixelSelection: MutableList<MutableList<PixelModel?>>,
        x: Int,
        y: Int,
        paletteIndexToSelect: Int,
    ): Boolean {
        val pixel = pixelMatrix[y][x]

        return if (pixel.paletteIndex == paletteIndexToSelect) {
            pixelSelection[y][x] = pixel
            true
        } else {
            false
        }
    }
}