package com.swirlfist.simplepixel.domain.di

import com.swirlfist.simplepixel.domain.usecase.ApplyBucketUseCase
import com.swirlfist.simplepixel.domain.usecase.ApplyBucketUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.ClearEditorActionsUseCase
import com.swirlfist.simplepixel.domain.usecase.ClearEditorActionsUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetBasePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.GetBasePixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetPalettePresetsUseCase
import com.swirlfist.simplepixel.domain.usecase.GetPalettePresetsUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetRedoEditorActionAvailableUseCase
import com.swirlfist.simplepixel.domain.usecase.GetRedoEditorActionAvailableUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.GetUndoEditorActionAvailableUseCase
import com.swirlfist.simplepixel.domain.usecase.GetUndoEditorActionAvailableUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCase
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.RedoEditorActionUseCase
import com.swirlfist.simplepixel.domain.usecase.RedoEditorActionUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.SavePalettePresetsUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePalettePresetsUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.UndoEditorActionUseCase
import com.swirlfist.simplepixel.domain.usecase.UndoEditorActionUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.UpdateBasePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdateBasePixelImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCaseImpl
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
    ): UpdatePixelColorUseCase

    @Binds
    abstract fun bindGetNextZoomFactorUseCase(
        impl: GetNextZoomFactorUseCaseImpl,
    ): GetNextZoomFactorUseCase

    @Binds
    abstract fun bindSavePixelImageUseCase(
        impl: SavePixelImageUseCaseImpl,
    ): SavePixelImageUseCase

    @Binds
    abstract fun bindExportPixelImageUseCase(
        impl: ExportPixelImageUseCaseImpl,
    ): ExportPixelImageUseCase

    @Binds
    abstract fun bindOpenPixelImageUseCase(
        impl: OpenPixelImageUseCaseImpl,
    ): OpenPixelImageUseCase

    @Binds
    abstract fun bindMoveImageUseCase(
        impl: MoveImageUseCaseImpl,
    ): MoveImageUseCase

    @Binds
    abstract fun bindApplyBucketUseCase(
        impl: ApplyBucketUseCaseImpl,
    ): ApplyBucketUseCase

    @Binds
    abstract fun bindGetUndoEditorActionAvailableUseCase(
        impl: GetUndoEditorActionAvailableUseCaseImpl,
    ): GetUndoEditorActionAvailableUseCase

    @Binds
    abstract fun bindGetRedoEditorActionAvailableUseCase(
        impl: GetRedoEditorActionAvailableUseCaseImpl,
    ): GetRedoEditorActionAvailableUseCase

    @Binds
    abstract fun bindUndoEditorActionUseCase(
        impl: UndoEditorActionUseCaseImpl,
    ): UndoEditorActionUseCase

    @Binds
    abstract fun bindRedoEditorActionUseCase(
        impl: RedoEditorActionUseCaseImpl,
    ): RedoEditorActionUseCase

    @Binds
    abstract fun bindClearEditorActionsUseCase(
        impl: ClearEditorActionsUseCaseImpl,
    ): ClearEditorActionsUseCase

    @Binds
    abstract fun bindGetBasePixelImageUseCase(
        impl: GetBasePixelImageUseCaseImpl,
    ): GetBasePixelImageUseCase

    @Binds
    abstract fun bindUpdateBasePixelImageUseCase(
        impl: UpdateBasePixelImageUseCaseImpl,
    ): UpdateBasePixelImageUseCase

    @Binds
    abstract fun bindGetPalettePresetsUseCase(
        impl: GetPalettePresetsUseCaseImpl,
    ): GetPalettePresetsUseCase

    @Binds
    abstract fun bindSavePalettePresetsUseCase(
        impl: SavePalettePresetsUseCaseImpl,
    ): SavePalettePresetsUseCase
}