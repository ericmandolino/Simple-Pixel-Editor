package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface SelectWithMagicWandUseCase : UseCase<SelectWithMagicWandUseCase.Params, PixelSelectionModel> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val pixelSelectionModel: PixelSelectionModel?,
        val x: Int,
        val y: Int,
    ) : UseCaseParams
}