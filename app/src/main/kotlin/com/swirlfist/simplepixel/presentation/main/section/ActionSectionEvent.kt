package com.swirlfist.simplepixel.presentation.main.section

sealed interface ActionSectionEvent {

    data object InkBucketButtonClicked : ActionSectionEvent

    data object InkPenButtonClicked : ActionSectionEvent

    data object InkEraserButtonClicked : ActionSectionEvent

    data object OpenToolsButtonClicked : ActionSectionEvent

    data object UndoButtonClicked : ActionSectionEvent

    data object RedoButtonClicked : ActionSectionEvent

    data object ZoomInButtonClicked : ActionSectionEvent

    data object ZoomOutButtonClicked : ActionSectionEvent

    data object OpenPaletteButtonClicked : ActionSectionEvent

    data class PickPaletteColorButtonClicked(
        val pickPaletteColorActionButtonType: ActionButtonType.PickPaletteColorActionButtonType,
    ) : ActionSectionEvent

    data object SavePixelImageButtonClicked : ActionSectionEvent

    data object OpenPixelImageButtonClicked : ActionSectionEvent
}