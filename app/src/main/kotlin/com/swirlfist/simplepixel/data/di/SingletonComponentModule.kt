package com.swirlfist.simplepixel.data.di

import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.data.repository.BasePixelImageRepositoryImpl
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
}