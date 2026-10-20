package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface MoveImageUseCase : UseCase<MoveImageUseCase.Params, PixelImageModel> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val pixelSelectionModel: PixelSelectionModel?,
        val moveDirection: MoveDirection,
    ) : UseCaseParams
}

enum class MoveDirection { UP, DOWN, LEFT, RIGHT }