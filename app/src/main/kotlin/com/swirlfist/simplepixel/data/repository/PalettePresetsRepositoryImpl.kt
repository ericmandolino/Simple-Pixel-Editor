package com.swirlfist.simplepixel.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.swirlfist.simplepixel.domain.model.PaletteModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PalettePresetsRepositoryImpl @Inject constructor() : PalettePresetsRepository {
    override suspend fun addPalettePreset(
        presetName: String,
        palette: PaletteModel
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getPalettePresets(): Map<String, PaletteModel> {
        val presets = mutableMapOf<String, PaletteModel>()

        // Black & White
        presets["B & W"] = PaletteModel(
            colors = listOf(
                Color.Black.toColorLong(),
                Color.White.toColorLong(),
            )
        )

        return presets
    }
}