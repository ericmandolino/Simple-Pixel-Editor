package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.presentation.getPixelAt
import javax.inject.Inject

class ApplyBucketUseCaseImpl @Inject constructor() : ApplyBucketUseCase {
    override suspend fun invoke(params: ApplyBucketUseCase.Params): Result<PixelImageModel> {
        return Result.success(
            applyBucket(
                pixelImage = params.pixelImageModel,
                x = params.x,
                y = params.y,
                paletteIndex = params.paletteIndex,
            )
        )
    }

    private fun applyBucket(
        pixelImage: PixelImageModel,
        x: Int,
        y: Int,
        paletteIndex: Int,
    ): PixelImageModel {
        val pixel = pixelImage.getPixelAt(x, y)
        if (pixel.paletteIndex == paletteIndex) {
            return pixelImage
        }

        val mutablePixelMatrix = mutableListOf<MutableList<PixelModel>>().apply {
            pixelImage.pixelMatrixModel.content.forEach { row ->
                add(row.toMutableList())
            }
        }

        applyBucket(
            pixelMatrix = mutablePixelMatrix,
            x,
            y,
            paletteIndexToReplace = pixel.paletteIndex,
            paletteIndexNew = paletteIndex,
        )

        return pixelImage.copy(
            pixelMatrixModel = pixelImage.pixelMatrixModel.copy(
                content = mutablePixelMatrix,
            )
        )
    }

    private fun applyBucket(
        pixelMatrix: MutableList<MutableList<PixelModel>>,
        x: Int,
        y: Int,
        paletteIndexToReplace: Int,
        paletteIndexNew: Int,
    ) {
        val size = pixelMatrix.size

        if (x !in 0..< size || y !in 0..< size) {
            return
        }

        val colorUpdated = applyColor(pixelMatrix, x, y, paletteIndexToReplace, paletteIndexNew)

        if (!colorUpdated) {
            return
        }

        applyBucket(pixelMatrix, x = x - 1, y, paletteIndexToReplace, paletteIndexNew)
        applyBucket(pixelMatrix, x = x + 1, y, paletteIndexToReplace, paletteIndexNew)
        applyBucket(pixelMatrix, x, y = y - 1, paletteIndexToReplace, paletteIndexNew)
        applyBucket(pixelMatrix, x, y = y + 1, paletteIndexToReplace, paletteIndexNew)
    }

    private fun applyColor(
        pixelMatrix: MutableList<MutableList<PixelModel>>,
        x: Int,
        y: Int,
        paletteIndexToReplace: Int,
        paletteIndexNew: Int,
    ) : Boolean {
        val pixel = pixelMatrix[y][x]
        val pixelPaletteIndex = pixel.paletteIndex

        return if (pixelPaletteIndex == paletteIndexNew || pixelPaletteIndex != paletteIndexToReplace) {
            false
        } else {
            pixelMatrix[y][x] = pixel.copy(paletteIndex = paletteIndexNew)
            true
        }
    }
}