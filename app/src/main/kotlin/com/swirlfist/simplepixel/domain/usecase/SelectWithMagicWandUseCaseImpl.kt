package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.MagicWandSelectionError
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import com.swirlfist.simplepixel.presentation.createSelection
import com.swirlfist.simplepixel.presentation.getPixelAt
import javax.inject.Inject

class SelectWithMagicWandUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : SelectWithMagicWandUseCase {
    override suspend fun invoke(params: SelectWithMagicWandUseCase.Params): Result<PixelSelectionModel> {
        return try {
            Result.success(
                selectWithMagicWand(
                    pixelImage = params.pixelImageModel,
                    pixelSelection = params.pixelSelectionModel ?:
                    params.pixelImageModel.createSelection(selectedCoordinates = listOf()),
                    x = params.x,
                    y = params.y,
                )
            )
        } catch (exception: Exception) {
            Result.failure(MagicWandSelectionError(exception))
        }
    }

    private suspend fun selectWithMagicWand(
        pixelImage: PixelImageModel,
        pixelSelection: PixelSelectionModel,
        x: Int,
        y: Int,
    ): PixelSelectionModel {
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

        val updatedPixelSelection = PixelSelectionModel(mutableSelectionMatrix.toList())

        if (updatedPixelSelection != pixelSelection) {
            pixelImageEditorActionRepository.addAction(
                PixelImageEditorAction.SelectMagicWandAction(
                    pixelImage,
                    pixelSelection,
                    x,
                    y,
                ),
                pixelImageResult = pixelImage,
                pixelSelectionResult = updatedPixelSelection,
            )
        }

        return updatedPixelSelection
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