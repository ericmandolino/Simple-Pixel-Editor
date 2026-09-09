package com.swirlfist.simplepixel.presentation.state

data class PaletteEditState(
    val paletteColors: List<Long> = listOf(),
    val selectedPaletteIndex: Int? = null,
    val onAddPaletteColorClick: () -> Unit = {},
    val onPaletteColorClick: (Int) -> Unit = {},
    val onDeletePaletteColorClick: () -> Unit = {},
    val onColorComponentRedSliderChange: (Float) -> Unit = {},
    val onColorComponentGreenSliderChange: (Float) -> Unit = {},
    val onColorComponentBlueSliderChange: (Float) -> Unit = {},
)
