package com.swirlfist.simplepixel.presentation.main.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.uielements.ActionButton

private const val MAX_BUTTON_GROUP_RENDER = 4
private const val BUTTON_SIZE_DP = 48

@Composable
fun ActionsSection(
    modifier: Modifier = Modifier,
    state: ActionsSectionState,
    onEvent: (ActionSectionEvent) -> Unit
) {
    val actionModels = state.actionModels.values

    FlowRow(
        modifier = modifier
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        actionModels.forEach { actionModel ->
            when (actionModel) {
                is ActionModel.ButtonActionModel
                    -> ButtonAction(
                    buttonActionModel = actionModel,
                    onEvent = onEvent,
                )

                is ActionModel.ButtonGroupActionModel
                    -> ButtonGroupAction(
                    actionType = actionModel.actionType,
                    isEnabled = actionModel.isEnabled,
                    childButtonActionModels = actionModel.childButtonActionModels,
                    onEvent,
                )

                is ActionModel.SelectableButtonGroupActionModel
                    -> ButtonGroupAction(
                    actionType = actionModel.actionType,
                    isEnabled = actionModel.isEnabled,
                    childButtonActionModels = actionModel.childButtonActionModels,
                    onEvent,
                )
            }
        }
    }
}

@Composable
private fun ButtonAction(
    buttonActionModel: ActionModel.ButtonActionModel,
    onEvent: (ActionSectionEvent) -> Unit,
) {
    ActionButton(
        actionButtonType = buttonActionModel.actionType,
        size = BUTTON_SIZE_DP.dp,
        isEnabled = buttonActionModel.isEnabled,
        isSelected = buttonActionModel.isSelected,
        onClick = { onEvent(buttonActionModel.actionType.toActionsSectionEvent()) },
    )
}

@Composable
private fun ButtonGroupAction(
    actionType: ActionButtonType,
    isEnabled: Boolean,
    childButtonActionModels: List<ActionModel.ButtonActionModel>,
    onEvent: (ActionSectionEvent) -> Unit,
) {
    if (isEnabled && childButtonActionModels.size in 2..MAX_BUTTON_GROUP_RENDER
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceContainer)
                .border(
                    border = BorderStroke(
                        width = 1.dp,
                        color = IconButtonDefaults.filledIconButtonColors().containerColor
                    ),
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            childButtonActionModels.forEach { buttonActionModel ->
                ButtonAction(
                    buttonActionModel,
                    onEvent = onEvent,
                )
            }
        }
    } else {
        ButtonAction(
            buttonActionModel = ActionModel.ButtonActionModel(
                actionType,
                isEnabled,
            ),
            onEvent = onEvent,
        )
    }
}

@Preview(name = "no-scroll-actions-section", showBackground = true, widthDp = 320, heightDp = 320)
@Preview(name = "scroll-actions-section", showBackground = true, widthDp = 200, heightDp = 120)
@Composable
fun ActionsSectionPreview() {
    val palette = PaletteModel(
        colors = listOf(
            Color.Black.toColorLong(),
            Color.Yellow.toColorLong(),
            Color.Red.toColorLong(),
            Color.Blue.toColorLong(),
            Color.Green.toColorLong(),
        )
    )
    SimplePixelTheme {
        ActionsSection(
            modifier = Modifier.fillMaxSize(),
            state = ActionsSectionState().copy(
                actionModels = mapOf(
                    ActionButtonType.OpenPaletteActionButtonType to ActionModel.SelectableButtonGroupActionModel(
                        actionType = ActionButtonType.OpenPaletteActionButtonType,
                        isEnabled = true,
                        childButtonActionModels = listOf(
                            ActionModel.ButtonActionModel(
                                ActionButtonType.PickPaletteColorActionButtonType(
                                    paletteIndex = 0,
                                    palette = palette,
                                ),
                            ),
                            ActionModel.ButtonActionModel(
                                ActionButtonType.PickPaletteColorActionButtonType(
                                    paletteIndex = 1,
                                    palette = palette,
                                )
                            ),
                            ActionModel.ButtonActionModel(
                                ActionButtonType.PickPaletteColorActionButtonType(
                                    paletteIndex = 2,
                                    palette = palette,
                                )
                            ),
                            ActionModel.ButtonActionModel(
                                ActionButtonType.PickPaletteColorActionButtonType(
                                    paletteIndex = 3,
                                    palette = palette,
                                ),
                            )
                        ),
                    ),
                    ActionButtonType.UndoActionButtonType to ActionModel.ButtonActionModel(
                        actionType = ActionButtonType.UndoActionButtonType,
                        isEnabled = true,
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
        ) { actionType ->
            android.util.Log.d("ActionsSection", "action: $actionType")
        }
    }
}

fun ActionButtonType.toActionsSectionEvent(): ActionSectionEvent = when (this) {
    ActionButtonType.InkBucketActionButtonType -> ActionSectionEvent.InkBucketButtonClicked
    ActionButtonType.InkEraserActionButtonType -> ActionSectionEvent.InkEraserButtonClicked
    ActionButtonType.InkPenActionButtonType -> ActionSectionEvent.InkPenButtonClicked
    ActionButtonType.MoveImageActionButtonType -> ActionSectionEvent.MoveImageActionButtonClicked
    ActionButtonType.MoveImageDownActionButtonType -> ActionSectionEvent.MoveImageDownActionButtonClicked
    ActionButtonType.MoveImageLeftActionButtonType -> ActionSectionEvent.MoveImageLeftActionButtonClicked
    ActionButtonType.MoveImageRightActionButtonType -> ActionSectionEvent.MoveImageRightActionButtonClicked
    ActionButtonType.MoveImageUpActionButtonType -> ActionSectionEvent.MoveImageUpActionButtonClicked
    ActionButtonType.OpenToolsActionButtonType -> ActionSectionEvent.OpenToolsButtonClicked
    ActionButtonType.UndoActionButtonType -> ActionSectionEvent.UndoButtonClicked
    ActionButtonType.RedoActionButtonType -> ActionSectionEvent.RedoButtonClicked
    ActionButtonType.ZoomInActionButtonType -> ActionSectionEvent.ZoomInButtonClicked
    ActionButtonType.ZoomOutActionButtonType -> ActionSectionEvent.ZoomOutButtonClicked
    ActionButtonType.OpenPaletteActionButtonType -> ActionSectionEvent.OpenPaletteButtonClicked
    is ActionButtonType.PickPaletteColorActionButtonType -> ActionSectionEvent.PickPaletteColorButtonClicked(
        pickPaletteColorActionButtonType = this,
    )
    ActionButtonType.SavePixelImageActionButtonType -> ActionSectionEvent.SavePixelImageButtonClicked
    ActionButtonType.OpenPixelImageActionButtonType -> ActionSectionEvent.OpenPixelImageButtonClicked
}
