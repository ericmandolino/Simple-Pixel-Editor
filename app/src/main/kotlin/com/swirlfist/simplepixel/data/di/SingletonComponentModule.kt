package com.swirlfist.simplepixel.data.di

import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.data.repository.BasePixelImageRepositoryImpl
import com.swirlfist.simplepixel.data.repository.PalettePresetsRepository
import com.swirlfist.simplepixel.data.repository.PalettePresetsRepositoryImpl
import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepository
import com.swirlfist.simplepixel.data.repository.PixelImageEditorActionRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentModule {

    @Binds
    abstract fun bindBasePixelImageRepository(
        impl: BasePixelImageRepositoryImpl,
    ): BasePixelImageRepository

    @Binds
    abstract fun bindPixelImageEditorActionRepository(
        impl: PixelImageEditorActionRepositoryImpl,
    ): PixelImageEditorActionRepository

    @Binds
    abstract fun bindPalettePresetsRepository(
        impl: PalettePresetsRepositoryImpl,
    ): PalettePresetsRepository
}