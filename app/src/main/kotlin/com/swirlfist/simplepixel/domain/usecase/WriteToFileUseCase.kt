package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri

interface WriteToFileUseCase : UseCase<WriteToFileUseCase.Params, Unit> {

    data class Params(
        val content: String,
        val uri: Uri,
    ) : UseCaseParams
}