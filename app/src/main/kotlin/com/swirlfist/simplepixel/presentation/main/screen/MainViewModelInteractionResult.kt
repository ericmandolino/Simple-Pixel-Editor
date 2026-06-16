package com.swirlfist.simplepixel.presentation.main.screen

import android.net.Uri

sealed interface MainViewModelInteractionResult {

    data class SelectSavePixelImageLocationInteractionResult(
        val result: Result<Uri>,
    ) : MainViewModelInteractionResult

    data class SelectOpenPixelImageLocationInteractionResult(
        val result: Result<Uri>,
    ) : MainViewModelInteractionResult
}