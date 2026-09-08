package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PalettePresetModel

interface DeletePalettePresetUseCase : UseCase<DeletePalettePresetUseCase.Params, Unit> {

    data class Params(
        val preset: PalettePresetModel,
    ) : UseCaseParams
}