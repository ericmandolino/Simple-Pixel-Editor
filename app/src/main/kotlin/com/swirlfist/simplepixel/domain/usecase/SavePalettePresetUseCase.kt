package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PalettePresetModel

interface SavePalettePresetUseCase : UseCase<SavePalettePresetUseCase.Params, Unit> {

    data class Params(
        val preset: PalettePresetModel,
    ) : UseCaseParams
}