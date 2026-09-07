package com.swirlfist.simplepixel.presentation.state

import com.swirlfist.simplepixel.domain.model.PaletteModel

data class PalettePresetsDialogState(
    val palettePresets: Map<String, PaletteModel> = mapOf(),
    val isLoadingPresets: Boolean = true,
)
