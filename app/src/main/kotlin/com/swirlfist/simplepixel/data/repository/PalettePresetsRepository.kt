package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow

interface PalettePresetsRepository {
    suspend fun addPalettePreset(
        preset: PalettePresetModel,
    )
    suspend fun getPalettePresets(): Flow<List<PalettePresetModel>>
}