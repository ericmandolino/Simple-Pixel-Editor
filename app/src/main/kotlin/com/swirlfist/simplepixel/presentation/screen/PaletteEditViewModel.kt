package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.presentation.deletePaletteColor
import com.swirlfist.simplepixel.presentation.state.PaletteEditDialogState
import com.swirlfist.simplepixel.presentation.state.PaletteEditState
import com.swirlfist.simplepixel.presentation.state.PixelImagePreviewSectionState
import com.swirlfist.simplepixel.presentation.state.addNewColor
import com.swirlfist.simplepixel.presentation.state.deleteSelectedColor
import com.swirlfist.simplepixel.presentation.state.updateColorComponentBlue
import com.swirlfist.simplepixel.presentation.state.updateColorComponentGreen
import com.swirlfist.simplepixel.presentation.state.updateColorComponentRed
import com.swirlfist.simplepixel.presentation.state.updateSelectedColor
import com.swirlfist.simplepixel.presentation.state.updateSelectedPreviewBackgroundColor
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
        _paletteEditDialogState.update { state ->
            state.copy(
                paletteEditState = state.paletteEditState.copy(
                    paletteColors = palette.colors,
                    selectedPaletteIndex = null,
                ),
            )
        }
    }

    fun setOriginalPixelMatrix(
        pixelMatrix: PixelMatrixModel,
    ) {
        val paletteColors = _paletteEditDialogState.value.paletteEditState.paletteColors

        _paletteEditDialogState.update { state ->
            state.copy(
                pixelImagePreviewSectionState = PixelImagePreviewSectionState(
                    pixelImageModel = PixelImageModel(
                        pixelMatrixModel = pixelMatrix,
                        paletteModel = PaletteModel(paletteColors),
                    ),
                    isFitAvailableSpace = true,
                    onPreviewBackgroundColorSelected = ::onPreviewBackgroundColorSelected,
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
            val newPixelImagePreviewSectionState = state.pixelImagePreviewSectionState?.let { previewState ->
                state.paletteEditState.selectedPaletteIndex?.let { paletteIndex ->
                    previewState.copy(
                        pixelImageModel = previewState.pixelImageModel?.deletePaletteColor(paletteIndex)
                    )
                }
            }

            state.copy(
                paletteEditState = state.paletteEditState.deleteSelectedColor(),
                pixelImagePreviewSectionState = newPixelImagePreviewSectionState,
            )
        }
    }

    private fun updatePaletteColorComponentRed(
        value: Float,
    ) {
        _paletteEditDialogState.updateColorComponent { paletteEditState ->
            paletteEditState.updateColorComponentRed(value)
        }
    }

    private fun updatePaletteColorComponentGreen(
        value: Float,
    ) {
        _paletteEditDialogState.updateColorComponent { paletteEditState ->
            paletteEditState.updateColorComponentGreen(value)
        }
    }

    private fun updatePaletteColorComponentBlue(
        value: Float,
    ) {
        _paletteEditDialogState.updateColorComponent { paletteEditState ->
            paletteEditState.updateColorComponentBlue(value)
        }
    }

    private fun MutableStateFlow<PaletteEditDialogState>.updateColorComponent(
        updatePaletteEditStateComponent: (PaletteEditState) -> PaletteEditState,
    ) = update { state ->
        val newPaletteEditState = updatePaletteEditStateComponent(state.paletteEditState)
        val newPaletteColors = newPaletteEditState.paletteColors
        state.copy(
            paletteEditState = newPaletteEditState,
            pixelImagePreviewSectionState = state.pixelImagePreviewSectionState?.replacePalette(
                newPaletteColors
            ),
        )
    }

    private fun PixelImagePreviewSectionState.replacePalette(
        newPaletteColors: List<Long>,
    ) = copy(
        pixelImageModel = pixelImageModel?.let { pixelImage ->
            PixelImageModel(
                pixelMatrixModel = pixelImage.pixelMatrixModel,
                paletteModel = PaletteModel(newPaletteColors),
            )
        }
    )

    private fun onPreviewBackgroundColorSelected(
        color: Color,
    ) {
        _paletteEditDialogState.update { state ->
            state.copy(
                pixelImagePreviewSectionState = state.pixelImagePreviewSectionState?.updateSelectedPreviewBackgroundColor(color)
            )
        }
    }
}