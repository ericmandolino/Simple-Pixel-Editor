package com.swirlfist.simplepixel.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.swirlfist.simplepixel.domain.model.PaletteModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PalettePresetsRepositoryImpl @Inject constructor() : PalettePresetsRepository {
    private val palettePresets = MutableStateFlow(
        mutableMapOf<String, PaletteModel>().apply {
            put(
                "B & W", PaletteModel(
                    colors = listOf(
                        Color.Black.toColorLong(),
                        Color.White.toColorLong(),
                    )
                )
            )
        }.toMap()
    )

    override suspend fun addPalettePreset(
        presetName: String,
        palette: PaletteModel
    ) {
        palettePresets.update { presets ->
            presets.toMutableMap().apply {
                put(presetName, palette)
            }.toMap()
        }
    }

    override suspend fun getPalettePresets(): Flow<Map<String, PaletteModel>> {
        return palettePresets.asStateFlow()
    }
}