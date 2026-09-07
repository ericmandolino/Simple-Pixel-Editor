package com.swirlfist.simplepixel.data.repository

import com.swirlfist.simplepixel.domain.model.PaletteModel

interface PalettePresetsRepository {
    suspend fun addPalettePreset(
        presetName: String,
        palette: PaletteModel,
    )
    suspend fun getPalettePresets(): Map<String, PaletteModel>
}