package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import com.swirlfist.simplepixel.data.repository.BasePixelImageRepository
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.presentation.main.state.NewImageScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class NewImageScreenViewModel @Inject constructor(
    private val basePixelImageRepository: BasePixelImageRepository,
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

    fun createImage() {
        val width = newImageScreenState.value.widthTextFieldState?.text?.toString()?.toInt()
        val height = newImageScreenState.value.heightTextFieldState?.text?.toString()?.toInt()

        if (width == null || height == null) {
            return
        }

        val colors = listOf(Color.Black.toColorLong(), Color.White.toColorLong())
        val pixelImageModel = PixelImageModel.createEmpty(
            width,
            height,
            colors,
        )
        basePixelImageRepository.updateBasePixelImage(pixelImageModel)
        _newImageScreenState.update { state ->
            state.copy(
                isNavigateToMainExpected = true,
            )
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