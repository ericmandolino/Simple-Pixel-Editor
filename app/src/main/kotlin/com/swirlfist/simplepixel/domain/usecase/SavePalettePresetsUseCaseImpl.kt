package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PalettePresetsRepository
import com.swirlfist.simplepixel.domain.error.SavePalettePresetError
import javax.inject.Inject

class SavePalettePresetsUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : SavePalettePresetsUseCase {
    override suspend fun invoke(params: SavePalettePresetsUseCase.Params): Result<Unit> {
        return try {
            palettePresetsRepository.addPalettePreset(
                presetName = params.presetName,
                palette = params.paletteModel,
            )
            Result.success(Unit)
        } catch(e: Exception) {
            Result.failure(SavePalettePresetError(e))
        }
    }
}