package com.swirlfist.simplepixel.presentation.screen

import kotlinx.serialization.Serializable

@Serializable
sealed interface MainViewModelInteraction {

    @Serializable
    object SelectSavePixelImageLocationInteraction : MainViewModelInteraction

    @Serializable
    object SelectExportPixelImageLocationInteraction : MainViewModelInteraction

    @Serializable
    object SelectOpenPixelImageLocationInteraction : MainViewModelInteraction
}