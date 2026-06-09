package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface UpdatePixelColorUseCase : UseCase<UpdatePixelColorUseCase.Params, PixelImageModel> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
    ) : UseCaseParams
}