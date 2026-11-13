package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel

interface SelectWithMagicWandUseCase : UseCase<SelectWithMagicWandUseCase.Params, PixelSelectionModel> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
    ) : UseCaseParams
}