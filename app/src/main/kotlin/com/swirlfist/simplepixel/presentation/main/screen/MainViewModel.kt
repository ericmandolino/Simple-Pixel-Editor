package com.swirlfist.simplepixel.presentation.main.screen

import android.net.Uri
import androidx.annotation.MainThread
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.MAX_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.MIN_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.execute
import com.swirlfist.simplepixel.presentation.createPaletteButtons
import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.main.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.main.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.main.state.MainScreenState
import com.swirlfist.simplepixel.presentation.main.state.PixelImagePreviewSectionState
import com.swirlfist.simplepixel.presentation.uielements.createEmptyPixelImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_ZOOM_FACTOR = 1F

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savePixelImageUseCase: SavePixelImageUseCase,
    private val openPixelImageUseCase: OpenPixelImageUseCase,
    private val getNextZoomFactorUseCase: GetNextZoomFactorUseCase,
    private val updatePixelColorUseCase: UpdatePixelColorUseCase,
) : ViewModel() {
    private val _mainScreenState = MutableStateFlow(
        value = MainScreenState(
            canvasSectionState = CanvasSectionState(),
            actionsSectionState = ActionsSectionState(),
            pixelImagePreviewSectionState = PixelImagePreviewSectionState(),
        )
    )
    val mainScreenState = _mainScreenState as StateFlow<MainScreenState>

    private val _interactions = MutableLiveData(
        listOf<MainViewModelInteraction>()
    )
    val interactions = _interactions as LiveData<List<MainViewModelInteraction>>

    init {
        _mainScreenState.update { mainScreenState ->
            val palette = PaletteModel(colors = listOf(Color.Black.toColorLong(), Color.White.toColorLong()))
            val pixelImageModel = createEmptyPixelImage(
                width = 16,
                height = 16,
                color1 = palette.colors[0],
                color2 = palette.colors[1],
            )
            val zoomFactor = DEFAULT_ZOOM_FACTOR
            mainScreenState.copy(
                canvasSectionState = mainScreenState.canvasSectionState.copy(
                    pixelImageModel = pixelImageModel,
                    zoomFactor = zoomFactor,
                    isShowCoordinatesEnabled = true,
                    isShowGridEnabled = true,
                ),
                actionsSectionState = mainScreenState.actionsSectionState.copy(
                    actionModels = mapOf(
                        ActionButtonType.OpenPaletteActionButtonType to ActionModel.SelectableButtonGroupActionModel(
                            actionType = ActionButtonType.OpenPaletteActionButtonType,
                            isEnabled = true,
                            childButtonActionModels = palette.createPaletteButtons(),
                        ),
                        ActionButtonType.OpenToolsActionButtonType to ActionModel.SelectableButtonGroupActionModel(
                            actionType = ActionButtonType.OpenToolsActionButtonType,
                            isEnabled = true,
                            childButtonActionModels = listOf(
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.InkPenActionButtonType,
                                    isEnabled = true,
                                    isSelected = true,
                                ),
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.InkBucketActionButtonType,
                                    isEnabled = true,
                                ),
                            ),
                        ),
                        ActionButtonType.InkEraserActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.InkEraserActionButtonType,
                            isEnabled = true,
                        ),
                        ActionButtonType.UndoActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.UndoActionButtonType,
                            isEnabled = false,
                        ),
                        ActionButtonType.RedoActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.RedoActionButtonType,
                            isEnabled = false,
                        ),
                        ActionButtonType.ZoomInActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.ZoomInActionButtonType,
                            isEnabled = zoomFactor < MAX_ZOOM_FACTOR,
                        ),
                        ActionButtonType.ZoomOutActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.ZoomOutActionButtonType,
                            isEnabled = zoomFactor > MIN_ZOOM_FACTOR,
                        ),
                        ActionButtonType.SavePixelImageActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.SavePixelImageActionButtonType,
                            isEnabled = true,
                        ),
                        ActionButtonType.OpenPixelImageActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.OpenPixelImageActionButtonType,
                            isEnabled = true,
                        ),
                    )
                ),
                pixelImagePreviewSectionState = mainScreenState.pixelImagePreviewSectionState.copy(
                    pixelImageModel = pixelImageModel,
                    isFitAvailableSpace = true,
                )
            )
        }
    }

    fun onCanvasSectionEvent(event: CanvasSectionEvent) {
        when (event) {
            is CanvasSectionEvent.PixelTap -> updatePixel(event)
        }
    }

    fun onActionsSectionEvent(event: ActionSectionEvent) {
        when (event) {
            ActionSectionEvent.OpenPaletteButtonClicked -> {}
            is ActionSectionEvent.PickPaletteColorButtonClicked
                -> updateSelectedPaletteIndex(event.pickPaletteColorActionButtonType)
            ActionSectionEvent.RedoButtonClicked -> {}
            ActionSectionEvent.UndoButtonClicked -> {}
            ActionSectionEvent.ZoomInButtonClicked
                -> zoom(isZoomIn = true)
            ActionSectionEvent.ZoomOutButtonClicked
                -> zoom(isZoomIn = false)
            ActionSectionEvent.SavePixelImageButtonClicked
                -> selectSavePixelImageLocation()
            ActionSectionEvent.OpenPixelImageButtonClicked
                -> selectOpenPixelImageLocation()
            ActionSectionEvent.InkEraserButtonClicked
                -> toggleSelectableActionButton(ActionButtonType.InkEraserActionButtonType)
            ActionSectionEvent.InkBucketButtonClicked,
                -> updateSelectedTool(ActionButtonType.InkBucketActionButtonType)
            ActionSectionEvent.InkPenButtonClicked,
                -> updateSelectedTool(ActionButtonType.InkPenActionButtonType)
            ActionSectionEvent.OpenToolsButtonClicked -> {}
        }
    }

    private fun updatePixel(event: CanvasSectionEvent.PixelTap) {
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
                        val pixelImagePreviewSectionState = mainScreenState.pixelImagePreviewSectionState
                        mainScreenState.copy(
                            canvasSectionState = canvasSectionState.copy(
                                pixelImageModel = updatedPixelImage,
                            ),
                            pixelImagePreviewSectionState = pixelImagePreviewSectionState.copy(
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

    private fun updateSelectedPaletteIndex(
        pickPaletteColorActionButtonType: ActionButtonType.PickPaletteColorActionButtonType,
    ) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.updateSelectedChildButton(
                    pickPaletteColorActionButtonType,
                )
            )
        }
    }

    private fun toggleSelectableActionButton(
        actionButtonType: ActionButtonType,
    ) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.toggleSelectableButton(
                    actionButtonType,
                )
            )
        }
    }

    private fun updateSelectedTool(
        selectToolActionButtonType: ActionButtonType,
    ) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.updateSelectedChildButton(
                    selectToolActionButtonType,
                )
            )
        }
    }

    private fun zoom(
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

    private fun selectSavePixelImageLocation() {
        val pixelImageModel = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return

        addInteraction(
            MainViewModelInteraction.SelectSavePixelImageLocationInteraction(
                pixelImageModel,
            )
        )
    }

    private fun selectOpenPixelImageLocation() {
        addInteraction(
            MainViewModelInteraction.SelectOpenPixelImageLocationInteraction
        )
    }

    private fun addInteraction(interaction: MainViewModelInteraction) {
        val interactions = _interactions.value ?: return
        _interactions.value = interactions + interaction
    }

    @MainThread
    fun onInteractionResult(
        interaction: MainViewModelInteraction,
        interactionResult: MainViewModelInteractionResult,
    ) {
        val interactions = _interactions.value ?: return
        _interactions.value = interactions.minus(interaction)

        when (interaction) {
            is MainViewModelInteraction.SelectSavePixelImageLocationInteraction
                -> onSelectSavePixelImageLocationInteractionResult(
                    interaction,
                    interactionResult as MainViewModelInteractionResult.SelectSavePixelImageLocationInteractionResult,
                )

            MainViewModelInteraction.SelectOpenPixelImageLocationInteraction
                -> onSelectOpenPixelImageLocationInteractionResult(
                    interactionResult as MainViewModelInteractionResult.SelectOpenPixelImageLocationInteractionResult,
                )
        }
    }

    private fun onSelectSavePixelImageLocationInteractionResult(
        interaction: MainViewModelInteraction.SelectSavePixelImageLocationInteraction,
        interactionResult: MainViewModelInteractionResult.SelectSavePixelImageLocationInteractionResult,
    ) {
        interactionResult.result.fold(
            onSuccess = { uri ->
                savePixelImage(interaction.pixelImage, uri)
            },
            onFailure = {
                // TODO
            }
        )
    }

    private fun savePixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        viewModelScope.launch {
            savePixelImageUseCase.execute(
                successBlock = { }, // TODO
                failureBlock = { }, // TODO
                params = SavePixelImageUseCase.Params(
                    pixelImageModel,
                    uri,
                ),
            )
        }
    }

    private fun onSelectOpenPixelImageLocationInteractionResult(
        interactionResult: MainViewModelInteractionResult.SelectOpenPixelImageLocationInteractionResult,
    ) {
        interactionResult.result.fold(
            onSuccess = { uri ->
                openPixelImage(uri)
            },
            onFailure = {
                // TODO
            }
        )
    }

    private fun openPixelImage(
        uri: Uri,
    ) {
        viewModelScope.launch {
            openPixelImageUseCase.execute(
                successBlock = { pixelImage ->
                    _mainScreenState.update { mainScreenState ->
                        mainScreenState.copy(
                            canvasSectionState = mainScreenState.canvasSectionState.copy(
                                pixelImageModel = pixelImage,
                                zoomFactor = DEFAULT_ZOOM_FACTOR,
                            ),
                            actionsSectionState = mainScreenState.actionsSectionState.updatePaletteButtons(
                                palette = pixelImage.paletteModel,
                            ),
                            pixelImagePreviewSectionState = mainScreenState.pixelImagePreviewSectionState.copy(
                                pixelImageModel = pixelImage,
                            )
                        )
                    }
                },
                failureBlock = { }, // TODO
                params = OpenPixelImageUseCase.Params(
                    uri,
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
    val buttonModel = actionModels[actionButtonType] ?: return this

    return when (buttonModel) {
        is ActionModel.ButtonActionModel -> {
            if (buttonModel.isEnabled == isEnabled) {
                this
            } else {
                copy(
                    actionModels = actionModels.toMutableMap().also { actionModels ->
                        actionModels[actionButtonType] = buttonModel.copy(isEnabled = isEnabled)
                    }
                )
            }
        }
        is ActionModel.SelectableButtonGroupActionModel -> {
            if (buttonModel.isEnabled == isEnabled) {
                this
            } else {
                copy(
                    actionModels = actionModels.toMutableMap().also { actionModels ->
                        actionModels[actionButtonType] = buttonModel.copy(isEnabled = isEnabled)
                    }
                )
            }
        }
    }
}

private fun ActionsSectionState.toggleSelectableButton(
    actionButtonType: ActionButtonType,
): ActionsSectionState {
    val actionModel = actionModels[actionButtonType] ?: return this

    return when (actionModel) {
        is ActionModel.ButtonActionModel -> copy(
            actionModels = actionModels.toMutableMap().apply {
                put(
                    actionModel.actionType,
                    actionModel.copy(
                        isSelected = !actionModel.isSelected,
                    )
                )
            }
        )

        is ActionModel.SelectableButtonGroupActionModel -> this
    }
}

private fun ActionsSectionState.updateSelectedChildButton(
    actionButtonType: ActionButtonType,
): ActionsSectionState {
    val parentActionModel = actionModels.values.find { actionModel ->
        actionModel is ActionModel.SelectableButtonGroupActionModel &&
                actionModel.childButtonActionModels.find { childButtonActionModel ->
                    childButtonActionModel.actionType == actionButtonType
                } != null
    } ?: return this

    return when (parentActionModel) {
        is ActionModel.ButtonActionModel -> {
            this
        }
        is ActionModel.SelectableButtonGroupActionModel -> {
            copy(
                actionModels = actionModels.toMutableMap().apply {
                    put(
                        parentActionModel.actionType,
                        parentActionModel.copy(
                            childButtonActionModels = parentActionModel.childButtonActionModels.map { child ->
                                child.copy(
                                    isSelected = child.actionType == actionButtonType,
                                )
                            }
                        )
                    )
                }
            )
        }
    }
}

private fun ActionsSectionState.updatePaletteButtons(
    palette: PaletteModel,
): ActionsSectionState {
    val openPaletteActionModel = actionModels[ActionButtonType.OpenPaletteActionButtonType] as ActionModel.SelectableButtonGroupActionModel

    return copy(
        actionModels = actionModels.toMutableMap().apply {
            put(
                ActionButtonType.OpenPaletteActionButtonType,
                openPaletteActionModel.copy(
                    childButtonActionModels = palette.createPaletteButtons(),
                )
            )
        }
    )
}