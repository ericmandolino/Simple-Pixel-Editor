package com.swirlfist.simplepixel.data.persistence.room.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.swirlfist.simplepixel.data.entity.PalettePresetEntity
import com.swirlfist.simplepixel.data.persistence.room.converters.Converters
import com.swirlfist.simplepixel.data.persistence.room.dao.PalettePresetDao

@Database(entities = [PalettePresetEntity::class], version = 1)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun palettePresetDao(): PalettePresetDao

    companion object {
        const val NAME = "app-database"
    }
}