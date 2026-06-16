package com.swirlfist.simplepixel.presentation.main.section

sealed interface ActionSectionEvent {

    data object UndoButtonClicked : ActionSectionEvent

    data object RedoButtonClicked : ActionSectionEvent

    data object ZoomInButtonClicked : ActionSectionEvent

    data object ZoomOutButtonClicked : ActionSectionEvent

    data object OpenPaletteButtonClicked : ActionSectionEvent

    data class PickPaletteColorButtonClicked(
        val paletteIndex: Int,
    ) : ActionSectionEvent

    data object SavePixelImageButtonClicked : ActionSectionEvent

    data object OpenPixelImageButtonClicked : ActionSectionEvent
}