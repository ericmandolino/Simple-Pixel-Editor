package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.model.PixelImageEditorAction
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import javax.inject.Inject

class MoveFloatingImageUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : MoveFloatingImageUseCase {
    override suspend fun invoke(params: MoveFloatingImageUseCase.Params): Result<PixelImageEditorActionContext> {
        return Result.success(
            moveFloatingImage(
                actionContext = params.actionContext,
                offsetDelta = params.offsetDelta,
            )
        )
    }

    private suspend fun moveFloatingImage(
        actionContext: PixelImageEditorActionContext,
        offsetDelta: Pair<Int, Int>,
    ): PixelImageEditorActionContext {
        val floatingImage = actionContext.floatingPixelImage ?: return actionContext
        val offset = floatingImage.offset

        val offsetResult = Pair(
            offset.first + offsetDelta.first,
            offset.second + offsetDelta.second,
        )

        val floatingImageResult = floatingImage.copy(
            offset = offsetResult
        )

        val actionContextResult = actionContext.copy(
            floatingPixelImage = floatingImageResult,
        )

        pixelImageEditorActionRepository.addAction(
            PixelImageEditorAction.MoveFloatingImageAction(
                actionContext,
                offsetDelta,
            ),
            actionContextResult,
        )

        return actionContextResult
    }
}