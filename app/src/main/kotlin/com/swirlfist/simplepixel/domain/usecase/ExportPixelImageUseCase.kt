package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface ExportPixelImageUseCase : UseCase<ExportPixelImageUseCase.Params, Unit> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val uri: Uri,
    ) : UseCaseParams
}