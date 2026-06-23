package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import javax.inject.Inject

class MoveImageUseCaseImpl @Inject constructor() : MoveImageUseCase {
    override suspend fun invoke(params: MoveImageUseCase.Params): Result<PixelImageModel> {
        return Result.success(
            moveImage(
                pixelImage = params.pixelImageModel,
                direction = params.moveDirection,
            )
        )
    }

    private fun moveImage(
        pixelImage: PixelImageModel,
        direction: MoveDirection,
    ): PixelImageModel {
        return when (direction) {
            MoveDirection.UP -> pixelImage.moveImageUp()
            MoveDirection.DOWN -> pixelImage.moveImageDown()
            MoveDirection.LEFT -> pixelImage.moveImageLeft()
            MoveDirection.RIGHT -> pixelImage.moveImageRight()
        }
    }

    private fun PixelImageModel.moveImageUp(): PixelImageModel {
        return copy(
            pixelMatrixModel = pixelMatrixModel.moveUp()
        )
    }

    private fun PixelImageModel.moveImageDown(
    ): PixelImageModel {
        return copy(
            pixelMatrixModel = pixelMatrixModel.moveDown()
        )
    }

    private fun PixelImageModel.moveImageLeft(
    ): PixelImageModel {
        return copy(
            pixelMatrixModel = pixelMatrixModel.moveLeft()
        )
    }

    private fun PixelImageModel.moveImageRight(
    ): PixelImageModel {
        return copy(
            pixelMatrixModel = pixelMatrixModel.moveRight()
        )
    }

    private fun PixelMatrixModel.moveUp(): PixelMatrixModel {
        val matrix = content
        val size = matrix.size
        val newRow = mutableListOf<PixelModel>().apply {
            repeat(size) {
                add(PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX))
            }
        }

        return copy(
            content = listOf(newRow) + matrix.dropLast(1)
        )
    }

    private fun PixelMatrixModel.moveDown(): PixelMatrixModel {
        val matrix = content
        val size = matrix.size
        val newRow = mutableListOf<PixelModel>().apply {
            repeat(size) {
                add(PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX))
            }
        }

        return copy(
            content = matrix.drop(1).plusElement(newRow)
        )
    }

    private fun PixelMatrixModel.moveLeft(): PixelMatrixModel {
        val newMatrix = mutableListOf<List<PixelModel>>()
        val emptyPixel = PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX)
        content.forEach { row ->
            newMatrix.add(row.drop(1).plus(emptyPixel))
        }

        return copy(
            content = newMatrix
        )
    }

    private fun PixelMatrixModel.moveRight(): PixelMatrixModel {
        val newMatrix = mutableListOf<List<PixelModel>>()
        val emptyPixel = PixelModel(paletteIndex = EMPTY_PIXEL_PALETTE_INDEX)
        content.forEach { row ->
            newMatrix.add(listOf(emptyPixel) + row.dropLast(1))
        }

        return copy(
            content = newMatrix
        )
    }
}