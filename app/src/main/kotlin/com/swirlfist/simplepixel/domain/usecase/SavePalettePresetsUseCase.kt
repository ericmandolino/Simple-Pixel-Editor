package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PaletteModel

interface SavePalettePresetsUseCase : UseCase<SavePalettePresetsUseCase.Params, Unit> {

    data class Params(
        val presetName: String,
        val paletteModel: PaletteModel,
    ) : UseCaseParams
}