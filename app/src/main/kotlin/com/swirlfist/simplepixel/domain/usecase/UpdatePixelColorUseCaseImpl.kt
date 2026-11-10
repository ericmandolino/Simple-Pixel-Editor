package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.UpdatePixelError
import com.swirlfist.simplepixel.domain.getPixelAt
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import javax.inject.Inject

class UpdatePixelColorUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : UpdatePixelColorUseCase {
    override suspend fun invoke(params: UpdatePixelColorUseCase.Params): Result<PixelImageModel> {
        return try {
            Result.success(
                updatePixel(
                    actionContext = params.actionContext,
                    x = params.x,
                    y = params.y,
                    paletteIndex = params.paletteIndex,
                    isSameAction = params.isSameAction,
                )
            )
        } catch (exception: Exception) {
            Result.failure(UpdatePixelError(exception))
        }
    }

    private suspend fun updatePixel(
        actionContext: PixelImageEditorActionContext,
        x: Int,
        y: Int,
        paletteIndex: Int,
        isSameAction: Boolean,
    ): PixelImageModel {
        val pixelImage = actionContext.pixelImage
        val pixelMatrix = pixelImage.pixelMatrixModel
        val pixel = pixelImage.getPixelAt(x, y)

        if (pixel.paletteIndex == paletteIndex) {
            return pixelImage
        }

        val updatedPixel = pixel.copy(
            paletteIndex = paletteIndex,
        )
        val updatedRow = pixelMatrix.content[y].toMutableList().apply {
            set(
                index = x,
                element = updatedPixel,
            )
        }.toList()
        val updatedPixelMatrix = pixelMatrix.copy(
            content = pixelMatrix.content.toMutableList().apply {
                set(
                    index = y,
                    element = updatedRow,
                )
            },
        )

        val pixelImageResult = pixelImage.copy(
            pixelMatrixModel = updatedPixelMatrix,
        )
        val actionContextResult = actionContext.copy(
            pixelImage = pixelImageResult,
        )

        if (isSameAction) {
            pixelImageEditorActionRepository.updateLastAction(actionContextResult)
        } else {
            pixelImageEditorActionRepository.addAction(
                PixelImageEditorAction.ApplyPixelColorAction(
                    actionContext,
                    x,
                    y,
                    paletteIndex,
                ),
                actionContextResult,
            )
        }

        return pixelImageResult
    }
}