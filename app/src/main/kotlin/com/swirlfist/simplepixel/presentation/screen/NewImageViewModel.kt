package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.text.input.TextFieldState
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
import com.swirlfist.simplepixel.presentation.state.PaletteEditState
import com.swirlfist.simplepixel.presentation.state.addNewColor
import com.swirlfist.simplepixel.presentation.state.deleteSelectedColor
import com.swirlfist.simplepixel.presentation.state.updateColorComponentBlue
import com.swirlfist.simplepixel.presentation.state.updateColorComponentGreen
import com.swirlfist.simplepixel.presentation.state.updateColorComponentRed
import com.swirlfist.simplepixel.presentation.state.updateSelectedColor
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
                paletteEditState = PaletteEditState(
                    onAddPaletteColorClick = ::addColorToPalette,
                    onPaletteColorClick = ::updateSelectedPaletteColor,
                    onDeletePaletteColorClick = ::deleteColorFromPalette,
                    onColorComponentRedSliderChange = ::updatePaletteColorComponentRed,
                    onColorComponentGreenSliderChange = ::updatePaletteColorComponentGreen,
                    onColorComponentBlueSliderChange = ::updatePaletteColorComponentBlue,
                ),
                onLoadPalettePresetClick = ::showPalettePresets,
                onStartSavePalettePresetClick = ::showSaveAsPalettePresetDialog,
                onSavePalettePresetClick = ::saveAsPalettePreset,
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
        val paletteColors = newImageScreenState.value.paletteState.paletteEditState.paletteColors
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

        val paletteColors = newImageScreenState.value.paletteState.paletteEditState.paletteColors
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
                    paletteEditState = state.paletteState.paletteEditState.copy(
                        paletteColors = paletteModel.colors,
                        selectedPaletteIndex = null,
                    ),
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
                    paletteEditState = state.paletteState.paletteEditState.addNewColor(),
                ),
            )
        }
    }

    private fun deleteColorFromPalette() {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteEditState = state.paletteState.paletteEditState.deleteSelectedColor(),
                ),
            )
        }
    }

    private fun updatePaletteColorComponentRed(
        value: Float,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteEditState = state.paletteState.paletteEditState.updateColorComponentRed(value),
                ),
            )
        }
    }

    private fun updatePaletteColorComponentGreen(
        value: Float,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteEditState = state.paletteState.paletteEditState.updateColorComponentGreen(value),
                ),
            )
        }
    }

    private fun updatePaletteColorComponentBlue(
        value: Float,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteEditState = state.paletteState.paletteEditState.updateColorComponentBlue(value),
                ),
            )
        }
    }

    private fun updateSelectedPaletteColor(
        paletteIndex: Int,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                paletteState = state.paletteState.copy(
                    paletteEditState = state.paletteState.paletteEditState.updateSelectedColor(paletteIndex),
                ),
            )
        }
    }

    fun createImage() {
        val width = newImageScreenState.value.widthTextFieldState?.text?.toString()?.toInt()
        val height = newImageScreenState.value.heightTextFieldState?.text?.toString()?.toInt()
        val colors = newImageScreenState.value.paletteState.paletteEditState.paletteColors

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