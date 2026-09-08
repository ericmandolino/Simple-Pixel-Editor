package com.swirlfist.simplepixel.data.persistence.room.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import com.swirlfist.simplepixel.data.entity.PalettePresetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PalettePresetDao {
    @Query("SELECT * FROM palette_presets")
    fun observePalettePresets(): Flow<List<PalettePresetEntity>>

    @Insert
    suspend fun addPalettePreset(palettePreset: PalettePresetEntity)
}