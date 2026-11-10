package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext

interface ConvertSelectionToFloatingImageUseCase : UseCase<ConvertSelectionToFloatingImageUseCase.Params, PixelImageEditorActionContext> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
    ) : UseCaseParams
}