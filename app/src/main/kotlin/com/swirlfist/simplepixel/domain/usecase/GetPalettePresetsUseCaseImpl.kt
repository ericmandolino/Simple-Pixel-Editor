package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.data.repository.PalettePresetsRepository
import com.swirlfist.simplepixel.domain.error.LoadPalettePresetsError
import com.swirlfist.simplepixel.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPalettePresetsUseCaseImpl @Inject constructor(
    private val palettePresetsRepository: PalettePresetsRepository,
) : GetPalettePresetsUseCase {
    override suspend fun invoke(params: UseCaseParams.NoParams): Result<Flow<List<PalettePresetModel>>> {
        return try {
            Result.success(palettePresetsRepository.getPalettePresets())
        } catch (e: Exception) {
            Result.failure(LoadPalettePresetsError(e))
        }
    }
}