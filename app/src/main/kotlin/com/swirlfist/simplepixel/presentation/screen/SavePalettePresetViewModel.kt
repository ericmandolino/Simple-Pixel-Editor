package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import com.swirlfist.simplepixel.presentation.state.SavePalettePresetDialogState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SavePalettePresetViewModel @Inject constructor() : ViewModel() {
    private val _savePalettePresetDialogState = MutableStateFlow(
        value = SavePalettePresetDialogState()
    )
    val savePalettePresetDialogState = _savePalettePresetDialogState.asStateFlow()

    fun updateNameTextFieldState(
        nameTextFieldState: TextFieldState,
    ) {
        _savePalettePresetDialogState.update { state ->
            state.copy(
                nameTextFieldState = nameTextFieldState
            )
        }
    }
}