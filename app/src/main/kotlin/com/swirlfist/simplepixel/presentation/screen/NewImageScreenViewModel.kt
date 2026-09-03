package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.usecase.UpdateBasePixelImageUseCase
import com.swirlfist.simplepixel.presentation.state.NewImageScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewImageScreenViewModel @Inject constructor(
    private val updateBasePixelImageUseCase: UpdateBasePixelImageUseCase,
) : ViewModel() {
    private val _newImageScreenState = MutableStateFlow(
        value = NewImageScreenState()
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

    fun addColorToPalette() {
        _newImageScreenState.update { state ->
            state.copy(
                paletteColors = state.paletteColors + Color.Black.toColorLong(),
            )
        }
    }

    fun deleteColorFromPalette() {
        val paletteIndex = newImageScreenState.value.selectedPaletteIndex ?: return

        if (paletteIndex !in newImageScreenState.value.paletteColors.indices) {
            return
        }

        _newImageScreenState.update { state ->
            state.copy(
                paletteColors = state.paletteColors.toMutableList().apply { removeAt(paletteIndex) }
            )
        }
    }

    fun updatePaletteColorComponentRed(
        value: Float,
    ) {
        updatePaletteColorComponent(
            value,
            ColorComponent.RED,
        )
    }

    fun updatePaletteColorComponentGreen(
        value: Float,
    ) {
        updatePaletteColorComponent(
            value,
            ColorComponent.GREEN,
        )
    }

    fun updatePaletteColorComponentBlue(
        value: Float,
    ) {
        updatePaletteColorComponent(
            value,
            ColorComponent.BLUE,
        )
    }

    fun updateSelectedPaletteColor(
        paletteIndex: Int,
    ) {
        _newImageScreenState.update { state ->
            state.copy(
                selectedPaletteIndex = if (state.selectedPaletteIndex == paletteIndex) null else paletteIndex
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

        val paletteIndex = _newImageScreenState.value.selectedPaletteIndex ?: return

        val paletteColors = _newImageScreenState.value.paletteColors.toMutableList()
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
                paletteColors = paletteColors.apply { set(paletteIndex, newColor.toColorLong()) }
            )
        }
    }

    fun createImage() {
        val width = newImageScreenState.value.widthTextFieldState?.text?.toString()?.toInt()
        val height = newImageScreenState.value.heightTextFieldState?.text?.toString()?.toInt()
        val colors = newImageScreenState.value.paletteColors

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