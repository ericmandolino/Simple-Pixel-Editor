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
import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.usecase.ApplyBucketUseCase
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.MAX_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.MIN_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.MoveDirection
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCase
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCaseImpl
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.execute
import com.swirlfist.simplepixel.presentation.createPaletteButtons
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
private const val ERASER_TOOL_PALETTE_INDEX = EMPTY_PIXEL_PALETTE_INDEX

@HiltViewModel
class MainViewModel @Inject constructor(
    private val savePixelImageUseCase: SavePixelImageUseCase,
    private val exportPixelImageUseCase: ExportPixelImageUseCase,
    private val openPixelImageUseCase: OpenPixelImageUseCase,
    private val getNextZoomFactorUseCase: GetNextZoomFactorUseCase,
    private val updatePixelColorUseCase: UpdatePixelColorUseCase,
    private val applyBucketUseCase: ApplyBucketUseCase,
    private val moveImageUseCaseImpl: MoveImageUseCaseImpl,
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
                width = 24,
                height = 24,
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
                            childButtonActionModels = palette.createPaletteButtons(),
                        ),
                        ActionButtonType.OpenToolsActionButtonType to ActionModel.SelectableButtonGroupActionModel(
                            actionType = ActionButtonType.OpenToolsActionButtonType,
                            childButtonActionModels = listOf(
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.InkPenActionButtonType,
                                    isSelected = true,
                                ),
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.InkBucketActionButtonType,
                                ),
                            ),
                        ),
                        ActionButtonType.InkEraserActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.InkEraserActionButtonType,
                        ),
                        ActionButtonType.UndoActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.UndoActionButtonType,
                            isEnabled = false,
                        ),
                        ActionButtonType.RedoActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.RedoActionButtonType,
                        ),
                        ActionButtonType.ZoomInActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.ZoomInActionButtonType,
                            isEnabled = zoomFactor < MAX_ZOOM_FACTOR,
                        ),
                        ActionButtonType.ZoomOutActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.ZoomOutActionButtonType,
                            isEnabled = zoomFactor > MIN_ZOOM_FACTOR,
                        ),
                        ActionButtonType.MoveImageActionButtonType to ActionModel.ButtonGroupActionModel(
                            actionType = ActionButtonType.MoveImageActionButtonType,
                            childButtonActionModels = listOf(
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.MoveImageUpActionButtonType,
                                ),
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.MoveImageDownActionButtonType,
                                ),
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.MoveImageLeftActionButtonType,
                                ),
                                ActionModel.ButtonActionModel(
                                    actionType = ActionButtonType.MoveImageRightActionButtonType,
                                ),
                            ),
                        ),
                        ActionButtonType.SavePixelImageActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.SavePixelImageActionButtonType,
                        ),
                        ActionButtonType.OpenPixelImageActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.OpenPixelImageActionButtonType,
                        ),
                        ActionButtonType.ExportPixelImageActionButtonType to ActionModel.ButtonActionModel(
                            actionType = ActionButtonType.ExportPixelImageActionButtonType,
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
            is CanvasSectionEvent.PixelTap -> onPixelTap(event)
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

            ActionSectionEvent.ExportPixelImageButtonClicked
                -> selectExportPixelImageLocation()

            ActionSectionEvent.InkEraserButtonClicked
                -> toggleSelectableActionButton(ActionButtonType.InkEraserActionButtonType)

            ActionSectionEvent.InkBucketButtonClicked,
                -> updateSelectedTool(ActionButtonType.InkBucketActionButtonType)

            ActionSectionEvent.InkPenButtonClicked,
                -> updateSelectedTool(ActionButtonType.InkPenActionButtonType)

            ActionSectionEvent.OpenToolsButtonClicked -> {}
            ActionSectionEvent.MoveImageActionButtonClicked -> {}
            ActionSectionEvent.MoveImageDownActionButtonClicked
                -> moveImage(MoveDirection.DOWN)

            ActionSectionEvent.MoveImageLeftActionButtonClicked
                -> moveImage(MoveDirection.LEFT)

            ActionSectionEvent.MoveImageRightActionButtonClicked
                -> moveImage(MoveDirection.RIGHT)

            ActionSectionEvent.MoveImageUpActionButtonClicked
                -> moveImage(MoveDirection.UP)
        }
    }

    private fun onPixelTap(event: CanvasSectionEvent.PixelTap) {
        val x = event.x
        val y = event.y

        when (_mainScreenState.value.getSelectedPaintTool()) {
            is ActionButtonType.InkPenActionButtonType -> updatePixelColor(x, y)
            is ActionButtonType.InkBucketActionButtonType -> applyBucket(x, y)
            else -> { }
        }
    }

    private fun updatePixelColor(
        x: Int,
        y: Int,
    ) {
        val pixelImage = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
        val paletteIndex = _mainScreenState.value.getPaletteIndex()

        viewModelScope.launch {
            updatePixelColorUseCase.execute(
                successBlock = { updatedPixelImage ->
                    _mainScreenState.update { mainScreenState ->
                        val canvasSectionState = mainScreenState.canvasSectionState
                        val pixelImagePreviewSectionState =
                            mainScreenState.pixelImagePreviewSectionState
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
                    paletteIndex,
                ),
            )
        }
    }

    private fun applyBucket(
        x: Int,
        y: Int,
    ) {
        val pixelImage = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
        val paletteIndex = _mainScreenState.value.getPaletteIndex()

        viewModelScope.launch {
            applyBucketUseCase.execute(
                successBlock = { updatedPixelImage ->
                    _mainScreenState.update { mainScreenState ->
                        val canvasSectionState = mainScreenState.canvasSectionState
                        val pixelImagePreviewSectionState =
                            mainScreenState.pixelImagePreviewSectionState
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
                params = ApplyBucketUseCase.Params(
                    pixelImageModel = pixelImage,
                    x = x,
                    y = y,
                    paletteIndex,
                ),
            )
        }
    }

    private fun moveImage(
        direction: MoveDirection
    ) {
        val pixelImage = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return

        viewModelScope.launch {
            moveImageUseCaseImpl.execute(
                successBlock = { updatedPixelImage ->
                    _mainScreenState.update { mainScreenState ->
                        val canvasSectionState = mainScreenState.canvasSectionState
                        val pixelImagePreviewSectionState =
                            mainScreenState.pixelImagePreviewSectionState
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
                params = MoveImageUseCase.Params(
                    pixelImageModel = pixelImage,
                    moveDirection = direction,
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
        if (_mainScreenState.value.isEraserSelected()) {
            toggleSelectableActionButton(ActionButtonType.InkEraserActionButtonType)
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
                            actionsSectionState = actionsSectionState.updateZoomButtonState(
                                zoomFactor
                            )
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
        addInteraction(MainViewModelInteraction.SelectSavePixelImageLocationInteraction)
    }

    private fun selectExportPixelImageLocation() {
        addInteraction(MainViewModelInteraction.SelectExportPixelImageLocationInteraction)
    }

    private fun selectOpenPixelImageLocation() {
        addInteraction(MainViewModelInteraction.SelectOpenPixelImageLocationInteraction)
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
            MainViewModelInteraction.SelectSavePixelImageLocationInteraction
                -> onSelectSavePixelImageLocationInteractionResult(
                interactionResult as MainViewModelInteractionResult.SelectSavePixelImageLocationInteractionResult,
            )

            MainViewModelInteraction.SelectExportPixelImageLocationInteraction
                -> onSelectExportPixelImageLocationInteractionResult(
                    interactionResult as MainViewModelInteractionResult.SelectExportPixelImageLocationInteractionResult,
                )

            MainViewModelInteraction.SelectOpenPixelImageLocationInteraction
                -> onSelectOpenPixelImageLocationInteractionResult(
                interactionResult as MainViewModelInteractionResult.SelectOpenPixelImageLocationInteractionResult,
            )
        }
    }

    private fun onSelectSavePixelImageLocationInteractionResult(
        interactionResult: MainViewModelInteractionResult.SelectSavePixelImageLocationInteractionResult,
    ) {
        interactionResult.result.fold(
            onSuccess = { uri ->
                val pixelImageModel = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
                savePixelImage(pixelImageModel, uri)
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

    private fun onSelectExportPixelImageLocationInteractionResult(
        interactionResult: MainViewModelInteractionResult.SelectExportPixelImageLocationInteractionResult,
    ) {
        interactionResult.result.fold(
            onSuccess = { uri ->
                val pixelImageModel = _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
                exportPixelImage(pixelImageModel, uri)
            },
            onFailure = {
                // TODO
            }
        )
    }

    private fun exportPixelImage(
        pixelImageModel: PixelImageModel,
        uri: Uri,
    ) {
        viewModelScope.launch {
            exportPixelImageUseCase.execute(
                successBlock = { }, // TODO
                failureBlock = { }, // TODO
                params = ExportPixelImageUseCase.Params(
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

    return if (buttonModel.isEnabled) {
        this
    } else {
        val updatedModel = when (buttonModel) {
            is ActionModel.ButtonActionModel -> buttonModel.copy(isEnabled = isEnabled)
            is ActionModel.ButtonGroupActionModel -> buttonModel.copy(isEnabled = isEnabled)
            is ActionModel.SelectableButtonGroupActionModel -> buttonModel.copy(isEnabled = isEnabled)
        }
        copy(
            actionModels = actionModels.toMutableMap().also { actionModels ->
                actionModels[actionButtonType] = updatedModel
            }
        )
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

        is ActionModel.ButtonGroupActionModel,
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
        is ActionModel.ButtonActionModel,
        is ActionModel.ButtonGroupActionModel -> this

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
    val openPaletteActionModel =
        actionModels[ActionButtonType.OpenPaletteActionButtonType] as ActionModel.SelectableButtonGroupActionModel

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

private fun MainScreenState.getPaletteIndex(): Int {
    return if (isEraserSelected()) {
        ERASER_TOOL_PALETTE_INDEX
    } else {
        actionsSectionState.getPaletteIndex()
    }
}

private fun MainScreenState.isEraserSelected() = actionsSectionState.isEraserSelected()

private fun ActionsSectionState.isEraserSelected(): Boolean {
    return (actionModels[ActionButtonType.InkEraserActionButtonType] as ActionModel.ButtonActionModel).isSelected
}

private fun MainScreenState.getSelectedPaintTool() = actionsSectionState.getSelectedPaintTool()

private fun ActionsSectionState.getSelectedPaintTool(): ActionButtonType {
    return getSelectedChildButton(
        buttonGroup = ActionButtonType.OpenToolsActionButtonType
    )
}

private fun ActionsSectionState.getPaletteIndex(): Int {
    return (
            getSelectedChildButton(
                buttonGroup = ActionButtonType.OpenPaletteActionButtonType
            ) as ActionButtonType.PickPaletteColorActionButtonType
            ).paletteIndex
}

private fun ActionsSectionState.getSelectedChildButton(
    buttonGroup: ActionButtonType,
): ActionButtonType {
    return (actionModels[buttonGroup] as ActionModel.SelectableButtonGroupActionModel).childButtonActionModels.first { child ->
        child.isSelected
    }.actionType
}