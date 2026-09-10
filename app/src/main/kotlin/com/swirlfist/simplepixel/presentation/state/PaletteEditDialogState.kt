package com.swirlfist.simplepixel.presentation.state

data class PaletteEditDialogState(
    val paletteEditState: PaletteEditState = PaletteEditState(),
    val pixelImagePreviewSectionState: PixelImagePreviewSectionState? = null,
)