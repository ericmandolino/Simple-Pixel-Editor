package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRedoEditorActionAvailableUseCaseImpl @Inject constructor(
    private val pixelImageEditorActionRepository: PixelImageEditorActionRepository,
) : GetRedoEditorActionAvailableUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Flow<Boolean>> {
        return Result.success(pixelImageEditorActionRepository.isRedoAvailable())
    }
}