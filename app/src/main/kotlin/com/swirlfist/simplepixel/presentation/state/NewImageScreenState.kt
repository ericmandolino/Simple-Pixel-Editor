package com.swirlfist.simplepixel.presentation.state

import androidx.compose.foundation.text.input.TextFieldState

data class NewImageScreenState(
    val isNavigateToMainExpected: Boolean = false,
    val widthTextFieldState: TextFieldState? = null,
    val heightTextFieldState: TextFieldState? = null,
    val paletteColors: List<Long> = listOf(),
    val selectedPaletteIndex: Int? = null,
)