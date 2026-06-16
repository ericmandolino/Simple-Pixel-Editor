package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface OpenPixelImageUseCase : UseCase<OpenPixelImageUseCase.Params, PixelImageModel> {

    data class Params(
        val uri: Uri,
    ) : UseCaseParams
}