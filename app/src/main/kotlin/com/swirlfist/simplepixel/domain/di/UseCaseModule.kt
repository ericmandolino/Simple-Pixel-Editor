package com.swirlfist.simplepixel.domain.di

import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    abstract fun bindUpdatePixelColorUseCase(
        useCaseImpl: UpdatePixelColorUseCaseImpl
    ) : UpdatePixelColorUseCase

    @Binds
    abstract fun bindGetNextZoomFactorUseCase(
        useCaseImpl: GetNextZoomFactorUseCaseImpl,
    ) : GetNextZoomFactorUseCase
}