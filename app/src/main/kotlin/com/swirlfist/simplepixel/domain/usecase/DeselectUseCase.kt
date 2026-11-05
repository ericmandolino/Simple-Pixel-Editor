package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface DeselectUseCase : UseCase<DeselectUseCase.Params, PixelSelectionModel?> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val pixelSelectionModel: PixelSelectionModel,
    ) : UseCaseParams
}