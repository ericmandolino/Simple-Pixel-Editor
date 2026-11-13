package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.RedoEditorActionError
import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import javax.inject.Inject

class RedoEditorActionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : RedoEditorActionUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<PixelImageEditorActionContext> {
        val redoResult = pixelImageEditorActionRepository.redoAction()
        return if (redoResult == null) {
            Result.failure(RedoEditorActionError())
        } else {
            Result.success(redoResult)
        }
    }
}