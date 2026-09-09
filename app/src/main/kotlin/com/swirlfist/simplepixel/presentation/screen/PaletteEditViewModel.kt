package com.swirlfist.simplepixel.presentation.screen

import androidx.lifecycle.ViewModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.state.PaletteEditDialogState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PaletteEditViewModel @Inject constructor(
) : ViewModel() {
    private val _paletteEditDialogState = MutableStateFlow(
        value = PaletteEditDialogState()
    )
    val paletteEditDialogState = _paletteEditDialogState.asStateFlow()

    fun setOriginalPalette(
        palette: PaletteModel,
    ) {
        if (_paletteEditDialogState.value.paletteEditState.paletteColors.isNotEmpty()) {
            return
        }

        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.copy(
                    paletteColors = palette.colors,
                ),
            )
        }
    }
}