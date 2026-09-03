package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface UpdateBasePixelImageUseCase : UseCase<UpdateBasePixelImageUseCase.Params, Unit> {

    data class Params(
        val pixelImage: PixelImageModel,
    ) : UseCaseParams
}