package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PalettePresetModel

interface SavePalettePresetsUseCase : UseCase<SavePalettePresetsUseCase.Params, Unit> {

    data class Params(
        val preset: PalettePresetModel,
    ) : UseCaseParams
}