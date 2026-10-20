package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface UpdatePixelColorUseCase : UseCase<UpdatePixelColorUseCase.Params, PixelImageModel> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val pixelSelectionModel: PixelSelectionModel?,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
        val isSameAction: Boolean,
    ) : UseCaseParams
}