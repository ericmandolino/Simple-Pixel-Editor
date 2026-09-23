package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri
import com.swirlfist.simplepixel.domain.model.PixelImageModel

interface ExportPixelImageToPngUseCase : UseCase<ExportPixelImageToPngUseCase.Params, Unit> {

    data class Params(
        val pixelImageModel: PixelImageModel,
        val uri: Uri,
        val scale: Float = 1F,
    ) : UseCaseParams
}
