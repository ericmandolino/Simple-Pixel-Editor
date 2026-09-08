package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PalettePresetModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.usecase.DeletePalettePresetUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePalettePresetUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdateBasePixelImageUseCase
import com.swirlfist.simplepixel.presentation.state.NewImagePaletteState
import com.swirlfist.simplepixel.presentation.state.NewImageScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewImageViewModel @Inject constructor(
    private val updateBasePixelImageUseCase: UpdateBasePixelImageUseCase,
    private val savePalettePresetUseCase: SavePalettePresetUseCase,
    private val deletePalettePresetUseCase: DeletePalettePresetUseCase,
) : ViewModel() {
    private val _newImageScreenState = MutableStateFlow(
        value = NewImageScreenState(
            paletteState = NewImagePaletteState(
                onLoadPalettePresetClick = ::showPalettePresets,
                onStartSavePalettePresetClick = ::showSaveAsPalettePresetDialog,
                onSavePalettePresetClick = ::saveAsPalettePreset,
                onAddPaletteColorClick = ::addColorToPalette,
                onPaletteColorClick = ::updateSelectedPaletteColor,
                onDeletePaletteColorClick = ::deleteColorFromPalette,
                onColorComponentRedSliderChange = ::updatePaletteColorComponentRed,
                onColorComponentGreenSliderChange = ::updatePaletteColorComponentGreen,
                onColorComponentBlueSliderChange = ::updatePaletteColorComponentBlue,
            ),
            onCreateImageClick = ::createImage,
            onPalettePresetSelected = ::onPalettePresetSelected,
            onDeletePalettePresetClick = ::deletePalettePreset,
            onCancelPalettePresetSelection = ::hidePalettePresets,
            onCancelSaveAsPalettePreset = ::hideSaveAsPalettePresetDialog,
        )
    )
    val newImageScreenState = _newImageScreenState.asStateFlow()

    fun updateTextFieldStates(
        widthTextFieldState: TextFieldState,
        heightTextFieldState: TextFieldState,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                widthTextFieldState = widthTextFieldState,
                heightTextFieldState = heightTextFieldState,
            )
        }
    }

    private fun showPalettePresets() {
        _newImageScreenState.update { state ->
            state.copy(
                isShowPalettePresets = true,
            )
        }
    }

    private fun showSaveAsPalettePresetDialog() {
        val paletteColors = newImageScreenState.value.paletteState.paletteColors
        if (paletteColors.isEmpty()) {
            return
        }

        _newImageScreenState.update { state ->
            state.copy(
                isShowSavePalettePreset = true,
            )
        }
    }

    fun hideSaveAsPalettePresetDialog() {
        _newImageScreenState.update { state ->
            state.copy(
                isShowSavePalettePreset = false,
            )

        }
    }

    private fun saveAsPalettePreset(presetName: String) {
        if (presetName.isBlank()) {
            return
        }

        val paletteColors = newImageScreenState.value.paletteState.paletteColors
        if (paletteColors.isEmpty()) {
            return
        }

        _newImageScreenState.update { state ->
            state.copy(
                isShowSavePalettePreset = false,
                paletteState = state.paletteState.copy(
                    isSavingPalettePreset = true,
                ),
            )
        }

        viewModelScope.launch {
            savePalettePresetUseCase(
                SavePalettePresetUseCase.Params(
                    PalettePresetModel(
                        id = 0,
                        name = presetName,
                        palette = PaletteModel(paletteColors),
                    )
                )
            ).fold(
                onSuccess = {
                    _newImageScreenState.update { state ->
                        state.copy(
                            paletteState = state.paletteState.copy(
                                isSavingPalettePreset = false,
                            ),
                        )
                    }
                },
                onFailure = {
                    _newImageScreenState.update { state ->
                        state.copy(
                            paletteState = state.paletteState.copy(
                                isSavingPalettePreset = false,
                            ),
                        )
                    }
                    // TODO: display error
                },
            )
        }
    }

    fun hidePalettePresets() {
        _newImageScreenState.update { state ->
            state.copy(
                isShowPalettePresets = false,
            )
        }
    }

    fun onPalettePresetSelected(
        paletteModel: PaletteModel,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                isShowPalettePresets = false,
                paletteState = state.paletteState.copy(
                    paletteColors = paletteModel.colors,
                    selectedPaletteIndex = null,
                ),
            )
        }
    }

    private fun deletePalettePreset(
        preset: PalettePresetModel,
    ) {
        viewModelScope.launch {
            deletePalettePresetUseCase(
                DeletePalettePresetUseCase.Params(
                    preset,
                )
            )
        }
    }

    private fun addColorToPalette() {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteColors = state.paletteState.paletteColors + Color.Black.toColorLong(),
                ),
            )
        }
    }

    private fun deleteColorFromPalette() {
        val paletteState = newImageScreenState.value.paletteState
        val paletteIndex = paletteState.selectedPaletteIndex ?: return

        if (paletteIndex !in paletteState.paletteColors.indices) {
            return
        }

        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteColors = state.paletteState.paletteColors.toMutableList().apply { removeAt(paletteIndex) }
                ),
            )
        }
    }

    private fun updatePaletteColorComponentRed(
        value: Float,
    ) {
        updatePaletteColorComponent(
            value,
            ColorComponent.RED,
        )
    }

    private fun updatePaletteColorComponentGreen(
        value: Float,
    ) {
        updatePaletteColorComponent(
            value,
            ColorComponent.GREEN,
        )
    }

    private fun updatePaletteColorComponentBlue(
        value: Float,
    ) {
        updatePaletteColorComponent(
            value,
            ColorComponent.BLUE,
        )
    }

    private fun updateSelectedPaletteColor(
        paletteIndex: Int,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    selectedPaletteIndex = if (state.paletteState.selectedPaletteIndex == paletteIndex)
                        null
                    else
                        paletteIndex
                ),
            )
        }
    }

    private fun updatePaletteColorComponent(
        value: Float,
        colorComponent: ColorComponent,
    ) {
        if (value !in 0F..1F) {
            return
        }

        val paletteState = _newImageScreenState.value.paletteState
        val paletteIndex = paletteState.selectedPaletteIndex ?: return

        val paletteColors = paletteState.paletteColors.toMutableList()
        if (paletteIndex !in paletteColors.indices) {
            return
        }

        val color = Color.fromColorLong(paletteColors[paletteIndex])
        val newColor = when (colorComponent) {
            ColorComponent.RED -> color.copy(
                red = value,
            )

            ColorComponent.GREEN -> color.copy(
                green = value,
            )

            ColorComponent.BLUE -> color.copy(
                blue = value,
            )
        }

        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteColors = paletteColors.apply { set(paletteIndex, newColor.toColorLong()) }
                ),
            )
        }
    }

    fun createImage() {
        val width = newImageScreenState.value.widthTextFieldState?.text?.toString()?.toInt()
        val height = newImageScreenState.value.heightTextFieldState?.text?.toString()?.toInt()
        val colors = newImageScreenState.value.paletteState.paletteColors

        if (width == null || height == null || colors.isEmpty()) {
            return
        }

        viewModelScope.launch {
            val pixelImageModel = PixelImageModel.createEmpty(
                width,
                height,
                colors,
            )

            updateBasePixelImageUseCase(
                UpdateBasePixelImageUseCase.Params(
                    pixelImage = pixelImageModel
                )
            )
            _newImageScreenState.update { state ->
                state.copy(
                    isNavigateToMainExpected = true,
                )
            }
        }
    }

    fun onNavigateToMain() {
        _newImageScreenState.update { state ->
            state.copy(
                isNavigateToMainExpected = false,
            )
        }
    }
}

private enum class ColorComponent {
    RED,
    GREEN,
    BLUE,
}