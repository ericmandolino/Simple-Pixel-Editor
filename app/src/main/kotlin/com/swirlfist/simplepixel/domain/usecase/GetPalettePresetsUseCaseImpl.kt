package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PalettePresetsRepository
import com.swirlfist.simplepixel.domain.model.PaletteModel
import javax.inject.Inject

class GetPalettePresetsUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : GetPalettePresetsUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Map<String, PaletteModel>> {
        return Result.success(palettePresetsRepository.getPalettePresets())
    }
}