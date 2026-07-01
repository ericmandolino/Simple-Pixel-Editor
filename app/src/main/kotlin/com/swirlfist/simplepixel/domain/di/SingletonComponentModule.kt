package com.swirlfist.simplepixel.domain.di

import com.swirlfist.simplepixel.domain.usecase.ApplyBucketUseCase
import com.swirlfist.simplepixel.domain.usecase.ApplyBucketUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCase
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCaseImpl
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
    abstract fun bindExportPixelImageUseCase(
        impl: ExportPixelImageUseCaseImpl,
    ) : ExportPixelImageUseCase

    @Binds
    abstract fun bindOpenPixelImageUseCase(
        impl: OpenPixelImageUseCaseImpl,
    ) : OpenPixelImageUseCase

    @Binds
    abstract fun bindMoveImageUseCase(
        impl: MoveImageUseCaseImpl,
    ) : MoveImageUseCase

    @Binds
    abstract fun bindApplyBucketUseCase(
        impl: ApplyBucketUseCaseImpl,
    ) : ApplyBucketUseCase
}