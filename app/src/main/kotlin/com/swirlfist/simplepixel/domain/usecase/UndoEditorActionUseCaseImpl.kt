package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.domain.error.UndoEditorActionError
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import javax.inject.Inject

class UndoEditorActionUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : UndoEditorActionUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<PixelImageModel> {
        val pixelImage = pixelImageEditorActionRepository.undoAction()
        return if (pixelImage == null) {
            Result.failure(UndoEditorActionError())
        } else {
            Result.success(pixelImage)
        }
    }

}