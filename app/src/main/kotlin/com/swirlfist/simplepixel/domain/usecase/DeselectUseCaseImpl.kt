package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import javax.inject.Inject

class DeselectUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : DeselectUseCase {

    override suspend fun invoke(params: DeselectUseCase.Params): Result<PixelSelectionModel?> {
        deselect(
            actionContext = params.actionContext,
        )

        return Result.success(null)
    }

    private suspend fun deselect(
        actionContext: PixelImageEditorActionContext,
    ) {
        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.DeselectAction(
                actionContext,
            ),
            actionContextResult = actionContext.copy(
                pixelSelection = null,
                floatingPixelImage = null,
            ),
        )
    }
}