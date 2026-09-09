package com.swirlfist.simplepixel.presentation.screen

import androidx.lifecycle.ViewModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.state.PaletteEditDialogState
import com.swirlfist.simplepixel.presentation.state.PaletteEditState
import com.swirlfist.simplepixel.presentation.state.addNewColor
import com.swirlfist.simplepixel.presentation.state.deleteSelectedColor
import com.swirlfist.simplepixel.presentation.state.updateColorComponentBlue
import com.swirlfist.simplepixel.presentation.state.updateColorComponentGreen
import com.swirlfist.simplepixel.presentation.state.updateColorComponentRed
import com.swirlfist.simplepixel.presentation.state.updateSelectedColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

class PaletteEditViewModel @Inject constructor(
) : ViewModel() {
    private val _paletteEditDialogState = MutableStateFlow(
        value = PaletteEditDialogState(
            paletteEditState = PaletteEditState(
                onAddPaletteColorClick = ::addColorToPalette,
                onPaletteColorClick = ::updateSelectedPaletteColor,
                onDeletePaletteColorClick = ::deleteColorFromPalette,
                onColorComponentRedSliderChange = ::updatePaletteColorComponentRed,
                onColorComponentGreenSliderChange = ::updatePaletteColorComponentGreen,
                onColorComponentBlueSliderChange = ::updatePaletteColorComponentBlue,
            )
        )
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

    private fun addColorToPalette() {
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.addNewColor(),
            )
        }
    }

    private fun updateSelectedPaletteColor(
        paletteIndex: Int,
    ) {
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.updateSelectedColor(paletteIndex),
            )
        }
    }

    private fun deleteColorFromPalette() {
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.deleteSelectedColor(),
            )
        }
    }

    private fun updatePaletteColorComponentRed(
        value: Float,
    ) {
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.updateColorComponentRed(value),
            )
        }
    }

    private fun updatePaletteColorComponentGreen(
        value: Float,
    ) {
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.updateColorComponentGreen(value),
            )
        }
    }

    private fun updatePaletteColorComponentBlue(
        value: Float,
    ) {
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.updateColorComponentBlue(value),
            )
        }
    }
}