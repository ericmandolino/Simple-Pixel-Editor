package com.swirlfist.simplepixel.presentation.di

import com.swirlfist.simplepixel.domain.usecase.WriteToFileUseCase
import com.swirlfist.simplepixel.presentation.usecase.WriteToFileUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class SingletonComponentModule {

    @Binds
    abstract fun bindWriteToFileUseCase(
        impl: WriteToFileUseCaseImpl,
    ): WriteToFileUseCase
}