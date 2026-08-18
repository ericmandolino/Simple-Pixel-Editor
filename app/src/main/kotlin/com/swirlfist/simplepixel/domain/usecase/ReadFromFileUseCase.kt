package com.swirlfist.simplepixel.domain.usecase

import android.net.Uri

interface ReadFromFileUseCase : UseCase<ReadFromFileUseCase.Params, String> {

    data class Params(
        val uri: Uri,
    ) : UseCaseParams
}