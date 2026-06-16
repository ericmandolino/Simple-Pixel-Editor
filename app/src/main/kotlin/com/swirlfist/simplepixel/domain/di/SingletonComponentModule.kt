package com.swirlfist.simplepixel.domain.di

import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentModule {

    @Binds
    abstract fun bindUpdatePixelColorUseCase(
        impl: UpdatePixelColorUseCaseImpl,
    ) : UpdatePixelColorUseCase

    @Binds
    abstract fun bindGetNextZoomFactorUseCase(
        impl: GetNextZoomFactorUseCaseImpl,
    ) : GetNextZoomFactorUseCase

    @Binds
    abstract fun bindSavePixelImageUseCase(
        impl: SavePixelImageUseCaseImpl,
    ) : SavePixelImageUseCase

    @Binds
    abstract fun bindOpenPixelImageUseCase(
        impl: OpenPixelImageUseCaseImpl,
    ) : OpenPixelImageUseCase
}