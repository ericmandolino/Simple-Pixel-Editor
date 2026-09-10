package com.swirlfist.simplepixel.presentation.screen

import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.usecase.ApplyBucketUseCase
import com.swirlfist.simplepixel.domain.usecase.ClearEditorActionsUseCase
import com.swirlfist.simplepixel.domain.usecase.ExportPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.GetBasePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.GetNextZoomFactorUseCase
import com.swirlfist.simplepixel.domain.usecase.GetRedoEditorActionAvailableUseCase
import com.swirlfist.simplepixel.domain.usecase.GetUndoEditorActionAvailableUseCase
import com.swirlfist.simplepixel.domain.usecase.MAX_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.MIN_ZOOM_FACTOR
import com.swirlfist.simplepixel.domain.usecase.MoveDirection
import com.swirlfist.simplepixel.domain.usecase.MoveImageUseCase
import com.swirlfist.simplepixel.domain.usecase.OpenPixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.RedoEditorActionUseCase
import com.swirlfist.simplepixel.domain.usecase.SavePixelImageUseCase
import com.swirlfist.simplepixel.domain.usecase.UndoEditorActionUseCase
import com.swirlfist.simplepixel.domain.usecase.UpdatePixelColorUseCase
import com.swirlfist.simplepixel.domain.usecase.UseCaseParams
import com.swirlfist.simplepixel.domain.usecase.execute
import com.swirlfist.simplepixel.presentation.createPaletteButtons
import com.swirlfist.simplepixel.presentation.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.section.ActionSectionEvent
import com.swirlfist.simplepixel.presentation.section.CanvasSectionEvent
import com.swirlfist.simplepixel.presentation.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.state.MainScreenState
import com.swirlfist.simplepixel.presentation.state.PixelImagePreviewSectionState
import com.swirlfist.simplepixel.presentation.state.updateSelectedPreviewBackgroundColor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val DEFAULT_ZOOM_FACTOR = 1F
private const val ERASER_TOOL_PALETTE_INDEX = EMPTY_PIXEL_PALETTE_INDEX

@HiltViewModel
class MainViewModel @Inject constructor(
    private val getBasePixelImageUseCase: GetBasePixelImageUseCase,
    private val savePixelImageUseCase: SavePixelImageUseCase,
    private val exportPixelImageUseCase: ExportPixelImageUseCase,
    private val openPixelImageUseCase: OpenPixelImageUseCase,
    private val getNextZoomFactorUseCase: GetNextZoomFactorUseCase,
    private val updatePixelColorUseCase: UpdatePixelColorUseCase,
    private val applyBucketUseCase: ApplyBucketUseCase,
    private val moveImageUseCaseImpl: MoveImageUseCase,
    private val getUndoEditorActionAvailableUseCase: GetUndoEditorActionAvailableUseCase,
    private val getRedoEditorActionAvailableUseCase: GetRedoEditorActionAvailableUseCase,
    private val undoEditorActionUseCase: UndoEditorActionUseCase,
    private val redoEditorActionUseCase: RedoEditorActionUseCase,
    private val clearEditorActionUseCase: ClearEditorActionsUseCase,
) : ViewModel() {
    private val _mainScreenState = MutableStateFlow(
        value = MainScreenState(
            canvasSectionState = CanvasSectionState(),
            actionsSectionState = ActionsSectionState(),
            pixelImagePreviewSectionState = PixelImagePreviewSectionState(
                onPreviewBackgroundColorSelected = ::onPreviewBackgroundColorSelected,
            ),
        )
    )
    val mainScreenState = _mainScreenState.asStateFlow()

    init {
        viewModelScope.launch {
            val pixelImageModel = getBasePixelImageUseCase(UseCaseParams.NoParams).getOrNull()
                ?: PixelImageModel.createEmpty(
                    width = 24,
                    height = 24,
                    colors = listOf(Color.Black.toColorLong(), Color.White.toColorLong()),
                )

            _mainScreenState.update { mainScreenState ->
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
                                childButtonActionModels = pixelImageModel.paletteModel.createPaletteButtons(),
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
                                isEnabled = true,
                            ),
                            ActionButtonType.ZoomOutActionButtonType to ActionModel.ButtonActionModel(
                                actionType = ActionButtonType.ZoomOutActionButtonType,
                                isEnabled = true,
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
                            ActionButtonType.EditPaletteActionButtonType to ActionModel.ButtonActionModel(
                                actionType = ActionButtonType.EditPaletteActionButtonType,
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

            clearEditorActionUseCase(UseCaseParams.NoParams)

            launch {
                getUndoEditorActionAvailableUseCase(UseCaseParams.NoParams).getOrNull()
                    ?.collect { isUndoAvailable ->
                        _mainScreenState.update { mainScreenState ->
                            val actionsSectionState = mainScreenState.actionsSectionState
                            mainScreenState.copy(
                                actionsSectionState = actionsSectionState.updateUndoButtonState(
                                    isUndoAvailable
                                ),
                            )
                        }
                    }
            }

            launch {
                getRedoEditorActionAvailableUseCase(UseCaseParams.NoParams).getOrNull()
                    ?.collect { isRedoAvailable ->
                        _mainScreenState.update { mainScreenState ->
                            val actionsSectionState = mainScreenState.actionsSectionState
                            mainScreenState.copy(
                                actionsSectionState = actionsSectionState.updateRedoButtonState(
                                    isRedoAvailable
                                ),
                            )
                        }
                    }
            }
        }
    }

    fun showPalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowPalette = true,
            )
        }
    }

    fun hidePalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowPalette = false,
            )
        }
    }

    fun onPreviewBackgroundColorSelected(
        color: Color,
    ) {
        _mainScreenState.update { state ->
            state.copy(
                pixelImagePreviewSectionState = state.pixelImagePreviewSectionState.updateSelectedPreviewBackgroundColor(color)
            )
        }
    }

    fun showEditPalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowEditPalette = true,
            )
        }
    }

    fun hideEditPalette() {
        _mainScreenState.update { state ->
            state.copy(
                isShowEditPalette = false,
            )
        }
    }

    fun onEditPaletteChangesSaved(
        paletteModel: PaletteModel,
        pixelMatrixModel: PixelMatrixModel?,
    ) {
        hideEditPalette()

        if (pixelMatrixModel == null) {
            return
        }

        updatePixelImage(
            pixelImage = PixelImageModel(
                pixelMatrixModel,
                paletteModel,
            ),
            isPaletteUpdate = true,
        )
    }

    fun onCanvasSectionEvent(event: CanvasSectionEvent) {
        when (event) {
            is CanvasSectionEvent.PixelTap -> onPixelTap(event)
        }
    }

    fun onActionsSectionEvent(event: ActionSectionEvent) {
        when (event) {
            ActionSectionEvent.OpenPaletteButtonClicked
                -> showPalette()

            ActionSectionEvent.EditPaletteButtonClicked
                -> showEditPalette()

            is ActionSectionEvent.PickPaletteColorButtonClicked
                -> updateSelectedPaletteIndex(event.pickPaletteColorActionButtonType)

            ActionSectionEvent.RedoButtonClicked
                -> redoEditorAction()

            ActionSectionEvent.UndoButtonClicked
                -> undoEditorAction()

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
                -> updateSelectedPaletteIndex(ActionButtonType.InkEraserActionButtonType)

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
            else -> {}
        }
    }

    private fun updatePixelImage(
        pixelImage: PixelImageModel,
        isPaletteUpdate: Boolean = false,
    ) {
        _mainScreenState.update { mainScreenState ->
            val canvasSectionState = mainScreenState.canvasSectionState
            val actionsSectionState = mainScreenState.actionsSectionState
            val pixelImagePreviewSectionState =
                mainScreenState.pixelImagePreviewSectionState
            mainScreenState.copy(
                canvasSectionState = canvasSectionState.copy(
                    pixelImageModel = pixelImage,
                ),
                actionsSectionState = if (isPaletteUpdate) {
                    actionsSectionState.updatePaletteButtons(
                        palette = pixelImage.paletteModel,
                    )
                } else {
                    actionsSectionState
                },
                pixelImagePreviewSectionState = pixelImagePreviewSectionState.copy(
                    pixelImageModel = pixelImage,
                ),
            )
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
                    updatePixelImage(updatedPixelImage)
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
                    updatePixelImage(updatedPixelImage)
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
                    updatePixelImage(updatedPixelImage)
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
        pickPaletteColorActionButtonType: ActionButtonType,
    ) {
        _mainScreenState.update { mainScreenState ->
            val actionsSectionState = mainScreenState.actionsSectionState
            mainScreenState.copy(
                actionsSectionState = actionsSectionState.updateSelectedChildButton(
                    pickPaletteColorActionButtonType,
                ),
                isShowPalette = false,
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

    private fun undoEditorAction() {
        viewModelScope.launch {
            undoEditorActionUseCase(UseCaseParams.NoParams).fold(
                onSuccess = { pixelImage ->
                    updatePixelImage(pixelImage)
                },
                onFailure = {}, // TODO
            )
        }
    }

    private fun redoEditorAction() {
        viewModelScope.launch {
            redoEditorActionUseCase(UseCaseParams.NoParams).fold(
                onSuccess = { pixelImage ->
                    updatePixelImage(pixelImage)
                },
                onFailure = {}, // TODO
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
                            ),
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
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectSavePixelImage = true,
                ),
            )
        }
    }

    fun onSelectSavePixelImageLocationLaunched() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectSavePixelImage = false,
                ),
            )
        }
    }

    private fun selectExportPixelImageLocation() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectExportPixelImage = true,
                ),
            )
        }
    }

    fun onSelectExportPixelImageLocationLaunched() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectExportPixelImage = false,
                ),
            )
        }
    }

    private fun selectOpenPixelImageLocation() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = true,
                ),
            )
        }
    }

    fun onSelectOpenPixelImageLocationLaunched() {
        _mainScreenState.update { mainScreenState ->
            mainScreenState.copy(
                launcherState = mainScreenState.launcherState.copy(
                    launchSelectOpenPixelImage = false,
                ),
            )
        }
    }

    fun onSelectSavePixelImageLocationResult(
        result: Result<Uri>,
    ) {
        result.fold(
            onSuccess = { uri ->
                val pixelImageModel =
                    _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
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
                successBlock = { },
                failureBlock = { }, // TODO
                params = SavePixelImageUseCase.Params(
                    pixelImageModel,
                    uri,
                ),
            )
        }
    }

    fun onSelectExportPixelImageLocationResult(
        result: Result<Uri>,
    ) {
        result.fold(
            onSuccess = { uri ->
                val pixelImageModel =
                    _mainScreenState.value.canvasSectionState.pixelImageModel ?: return
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

    fun onSelectOpenPixelImageLocationResult(
        result: Result<Uri>,
    ) {
        result.fold(
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

private fun ActionsSectionState.updateUndoButtonState(
    isEnabled: Boolean,
): ActionsSectionState {
    return updateButtonEnabled(
        ActionButtonType.UndoActionButtonType,
        isEnabled = isEnabled,
    )
}

private fun ActionsSectionState.updateRedoButtonState(
    isEnabled: Boolean,
): ActionsSectionState {
    return updateButtonEnabled(
        ActionButtonType.RedoActionButtonType,
        isEnabled = isEnabled,
    )
}

private fun ActionsSectionState.updateButtonEnabled(
    actionButtonType: ActionButtonType,
    isEnabled: Boolean,
): ActionsSectionState {
    val buttonModel = actionModels[actionButtonType] ?: return this

    return if (buttonModel.isEnabled == isEnabled) {
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
    actionModels.values.forEach { actionModel ->
        if (actionModel is ActionModel.SelectableButtonGroupActionModel) {
            actionModel.childButtonActionModels.forEach { childActionModel ->
                if (childActionModel.actionType is ActionButtonType.InkEraserActionButtonType) {
                    return childActionModel.isSelected
                }
            }
        }
    }

    return false
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