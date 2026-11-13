package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext

interface MoveFloatingImageUseCase : UseCase<MoveFloatingImageUseCase.Params, PixelImageEditorActionContext> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
        val offsetDelta: Pair<Int, Int>,
        val isSameAction: Boolean,
    ) : UseCaseParams
}