package com.swirlfist.simplepixel.data.di

import android.content.Context
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.swirlfist.simplepixel.data.persistence.room.dao.PalettePresetDao
import com.swirlfist.simplepixel.data.persistence.room.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return Room.databaseBuilder<AppDatabase>(context, AppDatabase.NAME)
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    @Singleton
    @Provides
    fun providePalettePresetDao(
        appDatabase: AppDatabase,
    ) : PalettePresetDao {
        return appDatabase.palettePresetDao()
    }
}