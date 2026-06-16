package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface SavePixelImageUseCase : UseCase<SavePixelImageUseCase.Params, Unit> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val uri: Uri,
    ) : UseCaseParams
}