package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface MoveImageUseCase : UseCase<MoveImageUseCase.Params, PixelImageModel> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
        val moveDirection: MoveDirection,
    ) : UseCaseParams
}

enum class MoveDirection { UP, DOWN, LEFT, RIGHT }