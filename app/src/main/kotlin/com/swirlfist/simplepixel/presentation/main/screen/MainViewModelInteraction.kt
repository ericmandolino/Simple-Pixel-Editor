package com.swirlfist.simplepixel.presentation.main.screen

import com.swirlfist.simplepixel.domain.model.PixelImageModel
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainViewModelInteraction {

    @Serializable
    data class SelectSavePixelImageLocationInteraction(
        val pixelImage: PixelImageModel,
    ) : MainViewModelInteraction

    @Serializable
    object SelectOpenPixelImageLocationInteraction : MainViewModelInteraction
}