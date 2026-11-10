package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.LandFloatingImageError
import com.swirlfist.simplepixel.domain.getPixelAt
import com.swirlfist.simplepixel.domain.getPixelHeight
import com.swirlfist.simplepixel.domain.getPixelWidth
import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import javax.inject.Inject

class LandFloatingImageUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : LandFloatingImageUseCase {
    override suspend fun invoke(params: LandFloatingImageUseCase.Params): Result<PixelImageEditorActionContext> {
        return try {
            Result.success(
                landFloatingImage(
                    actionContext = params.actionContext,
                )
            )
        } catch (exception: Exception) {
            Result.failure(LandFloatingImageError(exception))
        }
    }

    private suspend fun landFloatingImage(
        actionContext: PixelImageEditorActionContext,
    ) : PixelImageEditorActionContext {
        val pixelImage = actionContext.pixelImage
        val pixelMatrix = pixelImage.pixelMatrixModel
        val floatingPixelImage = actionContext.floatingPixelImage

        val imageContentResult = if (floatingPixelImage == null) {
            pixelMatrix.content
        } else {
            val offsetX = floatingPixelImage.offset.first
            val offsetY = floatingPixelImage.offset.second
            val floatingImageWidth = floatingPixelImage.pixelImageModel.getPixelWidth()
            val floatingImageHeight = floatingPixelImage.pixelImageModel.getPixelHeight()

            pixelMatrix.content.mapIndexed { y, row ->
                val floatingY = y - offsetY
                row.mapIndexed { x, pixel ->
                    val floatingX = x - offsetX
                    when {
                        floatingX in (0..<floatingImageWidth) && floatingY in (0..<floatingImageHeight) -> {
                            val floatingPixel = floatingPixelImage.pixelImageModel.getPixelAt(floatingX, floatingY)
                            if (floatingPixel.paletteIndex != EMPTY_PIXEL_PALETTE_INDEX) {
                                floatingPixel
                            } else {
                                pixel
                            }
                        }
                        else -> pixel
                    }
                }
            }
        }

        val pixelImageResult = pixelImage.copy(
            pixelMatrixModel = pixelImage.pixelMatrixModel.copy(
                content = imageContentResult,
            ),
        )

        val actionContextResult = actionContext.copy(
            pixelImage = pixelImageResult,
            floatingPixelImage = null,
        )

        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.LandFloatingImageAction(
                actionContext,
            ),
            actionContextResult,
        )

        return actionContextResult
    }
}