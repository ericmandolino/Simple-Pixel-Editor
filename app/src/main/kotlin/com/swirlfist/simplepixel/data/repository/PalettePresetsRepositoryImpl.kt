package com.swirlfist.simplepixel.data.repository

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.swirlfist.simplepixel.data.entity.toEntity
import com.swirlfist.simplepixel.data.entity.toModel
import com.swirlfist.simplepixel.data.persistence.room.dao.PalettePresetDao
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PalettePresetModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PalettePresetsRepositoryImpl @Inject constructor(
    private val palettePresetDao: PalettePresetDao,
) : PalettePresetsRepository {

    private val basePalettePresets = MutableStateFlow(
        mutableListOf(
            PalettePresetModel(
                id = -1,
                name = "B & W",
                palette = PaletteModel(
                    colors = listOf(
                        Color.Black.toColorLong(),
                        Color.White.toColorLong(),
                    )
                )
            )
        )
    ).asStateFlow()

    override suspend fun addPalettePreset(
        preset: PalettePresetModel
    ) {
        palettePresetDao.addPalettePreset(
            preset.toEntity()
        )
    }

    override suspend fun getPalettePresets(): Flow<List<PalettePresetModel>> {
        return basePalettePresets.combine(
            palettePresetDao.observePalettePresets().map { palettePresetList ->
                palettePresetList.map { entity ->
                    entity.toModel()
                }
            }
        ) { basePresets, loadedPresets ->
            basePresets + loadedPresets
        }
    }
}