package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import javax.inject.Inject

class DeselectUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : DeselectUseCase {

    override suspend fun invoke(params: DeselectUseCase.Params): Result<PixelSelectionModel?> {
        deselect(
            pixelImage = params.pixelImageModel,
            pixelSelection = params.pixelSelectionModel,
        )

        return Result.success(null)
    }

    private suspend fun deselect(
        pixelImage: PixelImageModel,
        pixelSelection: PixelSelectionModel,
    ) {
        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.DeselectAction(
                pixelImage,
                pixelSelection,
            ),
            pixelImageResult = pixelImage,
            pixelSelectionResult = null,
        )
    }
}