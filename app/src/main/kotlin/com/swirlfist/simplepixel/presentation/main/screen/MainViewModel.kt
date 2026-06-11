package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.execute
import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.main.section.ImageSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.createCheckersPixelImage
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.ImageSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState
import com.swirlfist.simplepixel.presentation.model.ActionButtonModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val updatePixelColorUseCase: UpdatePixelColorUseCase,
) : ViewModel() {
    private val _mainScreenState = MutableStateFlow(
        value = MainScreenState(
            imageSectionState = ImageSectionState(),
            actionsSectionState = ActionsSectionState(),
        )
    )
    val mainScreenState = _mainScreenState as StateFlow<MainScreenState>

    fun init() {
        _mainScreenState.update { mainScreenState ->
            val palette = PaletteModel(colors = listOf(Color.Black, Color.Yellow))
            mainScreenState.copy(
                imageSectionState = mainScreenState.imageSectionState.copy(
                    pixelImageModel = createCheckersPixelImage(
                        width = 5,
                        height = 3,
                        color1 = palette.colors[0],
                        color2 = palette.colors[1],
                    ),
                    zoomFactor = 4F,
                    isShowCoordinatesEnabled = true,
                ),
                actionsSectionState = mainScreenState.actionsSectionState.copy(
                    actionButtonModels = listOf(
                        ActionButtonModel(
                            actionType = ActionButtonType.OpenPaletteActionButtonType,
                            enabled = true,
                            childActionTypes = listOf(
                                ActionButtonType.PickPaletteColorActionButtonType(
                                    paletteIndex = 0,
                                    palette = palette,
                                ),
                                ActionButtonType.PickPaletteColorActionButtonType(
                                    paletteIndex = 1,
                                    palette = palette,
                                ),
                            ),
                        ),
                        ActionButtonModel(
                            actionType = ActionButtonType.UndoActionButtonType,
                            enabled = false,
                        ),
                        ActionButtonModel(
                            actionType = ActionButtonType.RedoActionButtonType,
                            enabled = false,
                        ),
                        ActionButtonModel(
                            actionType = ActionButtonType.ZoomInActionButtonType,
                            enabled = true,
                        ),
                        ActionButtonModel(
                            actionType = ActionButtonType.ZoomOutActionButtonType,
                            enabled = true,
                        ),
                    )
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
        val pixelImage = _mainScreenState.value.imageSectionState.pixelImageModel ?: return
        val x = event.x
        val y = event.y
        val pixel = pixelImage.getPixelAt(x, y)
        val newPaletteIndex = (pixel.paletteIndex + 1) % pixelImage.paletteModel.colors.size

        viewModelScope.launch {
            updatePixelColorUseCase.execute(
                successBlock = { updatedPixelImage ->
                    _mainScreenState.update { mainScreenState ->
                        val imageSectionState = mainScreenState.imageSectionState
                        mainScreenState.copy(
                            imageSectionState = imageSectionState.copy(
                                pixelImageModel = updatedPixelImage,
                            ),
                        )
                    }
                },
                failureBlock = { },
                params = UpdatePixelColorUseCase.Params(
                    pixelImageModel = pixelImage,
                    x = x,
                    y = y,
                    paletteIndex = newPaletteIndex,
                ),
            )
        }
    }
}