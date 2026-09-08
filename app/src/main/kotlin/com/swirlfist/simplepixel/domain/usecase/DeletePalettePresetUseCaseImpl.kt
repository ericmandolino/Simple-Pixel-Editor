package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PalettePresetsRepository
import com.swirlfist.simplepixel.domain.error.DeletePalettePresetError
import javax.inject.Inject

class DeletePalettePresetUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : DeletePalettePresetUseCase {
    override suspend fun invoke(params: DeletePalettePresetUseCase.Params): Result<Unit> {
        return try {
            palettePresetsRepository.deletePalettePreset(params.preset)
            Result.success(Unit)
        } catch(e: Exception) {
            Result.failure(DeletePalettePresetError(e))
        }
    }
}