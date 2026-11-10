package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface DeselectUseCase : UseCase<DeselectUseCase.Params, PixelSelectionModel?> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
    ) : UseCaseParams
}