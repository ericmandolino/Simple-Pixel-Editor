package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PaletteModel
import kotlinx.coroutines.flow.Flow

interface PalettePresetsRepository {
    suspend fun addPalettePreset(
        presetName: String,
        palette: PaletteModel,
    )
    suspend fun getPalettePresets(): Flow<Map<String, PaletteModel>>
}