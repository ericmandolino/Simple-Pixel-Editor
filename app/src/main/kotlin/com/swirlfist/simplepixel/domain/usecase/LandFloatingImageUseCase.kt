package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext

interface LandFloatingImageUseCase : UseCase<LandFloatingImageUseCase.Params, PixelImageEditorActionContext> {
    data class Params(
        val actionContext: PixelImageEditorActionContext,
    ) : UseCaseParams
}