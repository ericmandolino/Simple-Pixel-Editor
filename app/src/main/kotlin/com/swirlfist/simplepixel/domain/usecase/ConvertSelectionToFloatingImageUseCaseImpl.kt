package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.bottom
import com.swirlfist.simplepixel.domain.createSelection
import com.swirlfist.simplepixel.domain.error.SelectionToFloatingImageError
import com.swirlfist.simplepixel.domain.getPixelHeight
import com.swirlfist.simplepixel.domain.isSelected
import com.swirlfist.simplepixel.domain.left
import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.FloatingPixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import com.swirlfist.simplepixel.domain.right
import com.swirlfist.simplepixel.domain.top
import javax.inject.Inject

class ConvertSelectionToFloatingImageUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : ConvertSelectionToFloatingImageUseCase {
    override suspend fun invoke(params: ConvertSelectionToFloatingImageUseCase.Params): Result<PixelImageEditorActionContext> {
        return try {
            Result.success(
                convertSelectionToFloatingImage(
                    actionContext = params.actionContext,
                )
            )
        } catch (exception: Exception) {
            Result.failure(SelectionToFloatingImageError(exception))
        }
    }

    private suspend fun convertSelectionToFloatingImage(
        actionContext: PixelImageEditorActionContext,
    ) : PixelImageEditorActionContext {
        val pixelImage = actionContext.pixelImage
        val pixelMatrix = pixelImage.pixelMatrixModel
        val imageHeight = pixelImage.getPixelHeight()
        val pixelSelection = actionContext.pixelSelection ?: pixelImage.createSelection(emptyList())
        val rect = pixelSelection.rectangleSelection?.let { rectangleSelection ->
            Rectangle(rectangleSelection, imageHeight)
        }

        val updatedImageContent = mutableListOf<List<PixelModel>>()
        val floatingImageContent = mutableListOf<List<PixelModel>>()

        pixelMatrix.content.forEachIndexed { y, row ->
            val updatedImageRow = mutableListOf<PixelModel>()
            val floatingImageRow = mutableListOf<PixelModel>()

            row.forEachIndexed { x, pixel ->
                if (pixelSelection.isSelected(x, y) || (rect?.contains(x, y) ?: false)) {
                    updatedImageRow.add(PixelModel(EMPTY_PIXEL_PALETTE_INDEX))
                    floatingImageRow.add(pixel)
                } else {
                    updatedImageRow.add(pixel)
                    floatingImageRow.add(PixelModel(EMPTY_PIXEL_PALETTE_INDEX))
                }
            }

            updatedImageContent.add(updatedImageRow.toList())
            floatingImageContent.add(floatingImageRow.toList())
        }

        val floatingPixelImage = FloatingPixelImageModel(
            pixelImageModel = pixelImage.copy(
                pixelMatrixModel = pixelMatrix.copy(
                    content = floatingImageContent.toList(),
                )
            ),
            offset = Pair(0, 0),
        )

        val pixelImageResult = pixelImage.copy(
            pixelMatrixModel = pixelMatrix.copy(
                content = updatedImageContent.toList(),
            )
        )

        val actionContextResult = actionContext.copy(
            pixelImage = pixelImageResult,
            pixelSelection = null,
            floatingPixelImage = floatingPixelImage,
        )

        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.ConvertSelectionToFloatingImageAction(
                actionContext,
            ),
            actionContextResult,
        )

        return actionContextResult
    }
}

internal class Rectangle(
    rectangleSelection: RectangleSelectionModel,
    imageHeight: Int,
) {
    private val left = rectangleSelection.left()
    private val top = imageHeight - rectangleSelection.top()
    private val right = rectangleSelection.right()
    private val bottom = imageHeight - rectangleSelection.bottom()

    fun contains(
        x: Int,
        y: Int,
    ) : Boolean {
        return x in left..<right && y in bottom..<top
    }
}