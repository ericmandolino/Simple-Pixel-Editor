package com.swirlfist.simplepixel.domain.usecase

import com.swirlfist.simplepixel.domain.model.PixelImageEditorActionContext
import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface ApplyBucketUseCase : UseCase<ApplyBucketUseCase.Params, PixelImageModel> {

    data class Params(
        val actionContext: PixelImageEditorActionContext,
        val x: Int,
        val y: Int,
        val paletteIndex: Int,
        val isSameAction: Boolean,
    ) : UseCaseParams
}