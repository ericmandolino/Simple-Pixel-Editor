package com.swirlfist.simplepixel.presentation.state

import androidx.compose.foundation.text.input.TextFieldState
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PalettePresetModel

data class NewImageScreenState(
    val isNavigateToMainExpected: Boolean = false,
    val widthTextFieldState: TextFieldState? = null,
    val heightTextFieldState: TextFieldState? = null,
    val isShowPalettePresets: Boolean = false,
    val isShowSavePalettePreset: Boolean = false,
    val paletteState: NewImagePaletteState = NewImagePaletteState(),
    val onCreateImageClick: () -> Unit = {},
    val onPalettePresetSelected: (PaletteModel) -> Unit = {},
    val onDeletePalettePresetClick: (PalettePresetModel) -> Unit = {},
    val onCancelPalettePresetSelection: () -> Unit = {},
    val onCancelSaveAsPalettePreset: () -> Unit = {},
)

data class NewImagePaletteState(
    val paletteEditState: PaletteEditState = PaletteEditState(),
    val isSavingPalettePreset: Boolean = false,
    val onLoadPalettePresetClick: () -> Unit = {},
    val onStartSavePalettePresetClick: () -> Unit = {},
    val onSavePalettePresetClick: (String) -> Unit = {},
)