package com.swirlfist.simplepixel.presentation.state

import com.swirlfist.simplepixel.domain.model.PalettePresetModel

data class PalettePresetsDialogState(
    val palettePresets: List<PalettePresetModel> = listOf(),
    val isLoadingPresets: Boolean = true,
)
