package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.presentation.main.section.ImageSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.createCheckersPixelImage
import com.swirlfist.simplepixel.presentation.main.state.ImageSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {
    private val _mainScreenState = MutableStateFlow(
        value = MainScreenState(
            imageSectionState = ImageSectionState()
        )
    )
    val mainScreenState = _mainScreenState as StateFlow<MainScreenState>

    fun init() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                imageSectionState = mainScreenState.imageSectionState.copy(
                    pixelImageModel = createCheckersPixelImage(
                        width = 5,
                        height = 3,
                        color1 = Color.Black,
                        color2 = Color.Yellow,
                    ),
                    zoomFactor = 4F,
                    isShowCoordinatesEnabled = true,
                )
            )
        }
    }

    fun onImageSectionEvent(event: ImageSectionEvent) {
        when (event) {
            is ImageSectionEvent.PixelTap -> onPixelTap(event)
        }
    }

    private fun onPixelTap(event: ImageSectionEvent.PixelTap) {
        val x = event.x
        val y = event.y

        val pixelImage = _mainScreenState.value.imageSectionState.pixelImageModel ?: return
        val pixelMatrix = pixelImage.pixelMatrixModel
        val pixel = pixelImage.getPixelAt(x, y)
        val updatedRow = pixelMatrix.content[y].toMutableList().apply {
            set(
                index = x,
                element = pixel.copy(
                    paletteIndex = (pixel.paletteIndex + 1) % pixelImage.paletteModel.colors.size
                ),
            )
        }
        val updatedPixelMatrix = pixelMatrix.copy(
            content = pixelMatrix.content.toMutableList().apply {
                set(
                    index = y,
                    element = updatedRow,
                )
            },
        )

        _mainScreenState.update { mainScreenState ->
            val imageSectionState = mainScreenState.imageSectionState
            mainScreenState.copy(
                imageSectionState = imageSectionState.copy(
                    pixelImageModel = imageSectionState.pixelImageModel?.copy(
                        pixelMatrixModel = updatedPixelMatrix,
                    )
                ),
            )
        }
    }
}