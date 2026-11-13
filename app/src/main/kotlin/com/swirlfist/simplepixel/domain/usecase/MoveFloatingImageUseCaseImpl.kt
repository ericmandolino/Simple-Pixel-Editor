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
                isSameAction = params.isSameAction,
            )
        )
    }

    private suspend fun moveFloatingImage(
        actionContext: PixelImageEditorActionContext,
        offsetDelta: Pair<Int, Int>,
        isSameAction: Boolean,
    ): PixelImageEditorActionContext {
        val floatingImage = actionContext.floatingPixelImage

        val actionContextResult =
            if (floatingImage == null || (offsetDelta.first == 0 && offsetDelta.second == 0)) {
                actionContext
            } else {
                val offset = floatingImage.offset

                val offsetResult = Pair(
                    offset.first + offsetDelta.first,
                    offset.second + offsetDelta.second,
                )

                val floatingImageResult = floatingImage.copy(
                    offset = offsetResult
                )

                actionContext.copy(
                    floatingPixelImage = floatingImageResult,
                )
            }

        if (isSameAction) {
            pixelImageEditorActionRepository.updateLastAction(actionContextResult)
        } else {
            pixelImageEditorActionRepository.addAction(
                PixelImageEditorAction.MoveFloatingImageAction(
                    actionContext,
                    offsetDelta,
                ),
                actionContextResult,
            )
        }

        return actionContextResult
    }
}