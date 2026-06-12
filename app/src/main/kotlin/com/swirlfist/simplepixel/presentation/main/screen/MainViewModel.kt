package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.MAX_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.MIN_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.execute
import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.main.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.createEmptyPixelImage
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
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
    private val getNextZoomFactorUseCase: GetNextZoomFactorUseCase,
) : ViewModel() {
    private val _mainScreenState = MutableStateFlow(
        value = MainScreenState(
            canvasSectionState = CanvasSectionState(),
            actionsSectionState = ActionsSectionState(),
        )
    )
    val mainScreenState = _mainScreenState as StateFlow<MainScreenState>

    init {
        _mainScreenState.update { mainScreenState ->
            val palette = PaletteModel(colors = listOf(Color.Black, Color.Yellow))
            val zoomFactor = 1F
            mainScreenState.copy(
                canvasSectionState = mainScreenState.canvasSectionState.copy(
                    pixelImageModel = createEmptyPixelImage(
                        width = 16,
                        height = 16,
                        color1 = palette.colors[0],
                        color2 = palette.colors[1],
                    ),
                    zoomFactor = zoomFactor,
                    isShowCoordinatesEnabled = true,
                ),
                actionsSectionState = mainScreenState.actionsSectionState.copy(
                    actionButtonModels = mapOf(
                        ActionButtonType.OpenPaletteActionButtonType to ActionButtonModel(
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
                        ActionButtonType.UndoActionButtonType to ActionButtonModel(
                            actionType = ActionButtonType.UndoActionButtonType,
                            enabled = false,
                        ),
                        ActionButtonType.RedoActionButtonType to ActionButtonModel(
                            actionType = ActionButtonType.RedoActionButtonType,
                            enabled = false,
                        ),
                        ActionButtonType.ZoomInActionButtonType to ActionButtonModel(
                            actionType = ActionButtonType.ZoomInActionButtonType,
                            enabled = zoomFactor < MAX_ZOOM_FACTOR,
                        ),
                        ActionButtonType.ZoomOutActionButtonType to ActionButtonModel(
                            actionType = ActionButtonType.ZoomOutActionButtonType,
                            enabled = zoomFactor > MIN_ZOOM_FACTOR,
                        ),
                    )
                )
            )
        }
    }

    fun onCanvasSectionEvent(event: CanvasSectionEvent) {
        when (event) {
            is CanvasSectionEvent.PixelTap -> onPixelTap(event)
        }
    }

    fun onActionsSectionEvent(event: ActionSectionEvent) {
        when (event) {
            ActionSectionEvent.OpenPaletteButtonClicked -> {}
            is ActionSectionEvent.PickPaletteColorButtonClicked -> {}
            ActionSectionEvent.RedoButtonClicked -> {}
            ActionSectionEvent.UndoButtonClicked -> {}
            ActionSectionEvent.ZoomInButtonClicked -> onZoom(isZoomIn = true)
            ActionSectionEvent.ZoomOutButtonClicked -> onZoom(isZoomIn = false)
        }
    }

    private fun onPixelTap(event: CanvasSectionEvent.PixelTap) {
        val pixelImage = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
        val x = event.x
        val y = event.y
        val pixel = pixelImage.getPixelAt(x, y)
        val newPaletteIndex = if (pixel.paletteIndex == pixelImage.paletteModel.colors.size - 1) {
            -1
        } else {
            (pixel.paletteIndex + 1) % pixelImage.paletteModel.colors.size
        }

        viewModelScope.launch {
            updatePixelColorUseCase.execute(
                successBlock = { updatedPixelImage ->
                    _mainScreenState.update { mainScreenState ->
                        val canvasSectionState = mainScreenState.canvasSectionState
                        mainScreenState.copy(
                            canvasSectionState = canvasSectionState.copy(
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

    private fun onZoom(
        isZoomIn: Boolean,
    ) {
        viewModelScope.launch {
            getNextZoomFactorUseCase.execute(
                successBlock = { zoomFactor ->
                    _mainScreenState.update { mainScreenState ->
                        val canvasSectionState = mainScreenState.canvasSectionState
                        val actionsSectionState = mainScreenState.actionsSectionState
                        mainScreenState.copy(
                            canvasSectionState = canvasSectionState.copy(
                                zoomFactor = zoomFactor,
                            ),
                            actionsSectionState = actionsSectionState.updateZoomButtonState(zoomFactor)
                        )
                    }
                },
                failureBlock = { },
                params = GetNextZoomFactorUseCase.Params(
                    currentZoomFactor = _mainScreenState.value.canvasSectionState.zoomFactor,
                    isZoomIn = isZoomIn,
                ),
            )
        }
    }
}

private fun ActionsSectionState.updateZoomButtonState(
    zoomFactor: Float,
): ActionsSectionState {
    return updateButtonEnabled(
        ActionButtonType.ZoomInActionButtonType,
        isEnabled = zoomFactor < MAX_ZOOM_FACTOR,
    ).updateButtonEnabled(
        ActionButtonType.ZoomOutActionButtonType,
        isEnabled = zoomFactor > MIN_ZOOM_FACTOR,
    )
}

private fun ActionsSectionState.updateButtonEnabled(
    actionButtonType: ActionButtonType,
    isEnabled: Boolean,
): ActionsSectionState {
    val buttonModel = actionButtonModels[actionButtonType] ?: return this

    if (buttonModel.enabled == isEnabled) {
        return this
    }

    return copy(
        actionButtonModels = actionButtonModels.toMutableMap().also {  buttonModels ->
            buttonModels[actionButtonType] = buttonModel.copy(enabled = isEnabled)
        }
    )
}