package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.usecase.error.UpdatePixelException
import com.swirlfist.simplepixel.presentation.getPixelAt
import javax.inject.Inject

class UpdatePixelColorUseCaseImpl @Inject constructor() : UpdatePixelColorUseCase {
    override suspend fun invoke(params: UpdatePixelColorUseCase.Params): Result<PixelImageModel> {
        return try {
            Result.success(
                updatePixel(
                    pixelImage = params.pixelImageModel,
                    x = params.x,
                    y = params.y,
                    paletteIndex = params.paletteIndex,
                )
            )
        } catch (exception: Exception) {
            Result.failure(UpdatePixelException(exception))
        }
    }

    private fun updatePixel(
        pixelImage: PixelImageModel,
        x: Int,
        y: Int,
        paletteIndex: Int,
    ) : PixelImageModel {
        val pixelMatrix = pixelImage.pixelMatrixModel
        val pixel = pixelImage.getPixelAt(x, y)
        val updatedPixel = pixel.copy(
            paletteIndex = paletteIndex,
        )
        val updatedRow = pixelMatrix.content[y].toMutableList().apply {
            set(
                index = x,
                element = updatedPixel,
            )
        }
        val updatedPixelMatrix = pixelMatrix.copy(
            content = pixelMatrix.content.toMutableList().apply {
                set(
                    index = y,
                    element = updatedRow,
                )
            },
        )

        return pixelImage.copy(
            pixelMatrixModel = updatedPixelMatrix,
        )
    }
}