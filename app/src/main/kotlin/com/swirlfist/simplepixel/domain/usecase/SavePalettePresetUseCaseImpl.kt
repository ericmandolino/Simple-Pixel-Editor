package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PalettePresetsRepository
import com.swirlfist.simplepixel.domain.error.SavePalettePresetError
import javax.inject.Inject

class SavePalettePresetUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : SavePalettePresetUseCase {
    override suspend fun invoke(params: SavePalettePresetUseCase.Params): Result<Unit> {
        return try {
            palettePresetsRepository.addPalettePreset(params.preset)
            Result.success(Unit)
        } catch(e: Exception) {
            Result.failure(SavePalettePresetError(e))
        }
    }
}