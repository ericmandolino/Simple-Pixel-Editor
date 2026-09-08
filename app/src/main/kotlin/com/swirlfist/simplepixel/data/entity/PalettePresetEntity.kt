package com.swirlfist.simplepixel.data.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PalettePresetModel

@Entity(tableName = "palette_presets")
data class PalettePresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "preset_name") val presetName: String,
    @ColumnInfo(name = "colors") val colors: List<Long>,
)

fun PalettePresetEntity.toModel(): PalettePresetModel {
    return PalettePresetModel(
        id = this.id,
        name = this.presetName,
        palette = PaletteModel(this.colors),
    )
}

fun PalettePresetModel.toEntity(): PalettePresetEntity {
    return PalettePresetEntity(
        id = this.id,
        presetName = this.name,
        colors = this.palette.colors,
    )
}