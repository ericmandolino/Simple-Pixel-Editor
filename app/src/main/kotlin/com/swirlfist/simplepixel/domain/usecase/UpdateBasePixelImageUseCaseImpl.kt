package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import javax.inject.Inject

class UpdateBasePixelImageUseCaseImpl @Inject constructor(
    private val basePixelImageRepository: BasePixelImageRepository
) : UpdateBasePixelImageUseCase {
    override suspend fun invoke(params: UpdateBasePixelImageUseCase.Params): Result<Unit> {
        basePixelImageRepository.updateBasePixelImage(params.pixelImage)

        return Result.success(Unit)
    }
}