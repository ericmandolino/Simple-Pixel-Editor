package com.swirlfist.simplepixel.presentation.main.section

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.main.state.ActionsSectionState
import com.swirlfist.simplepixel.presentation.model.ActionButtonModel
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.uielements.ActionButton

private const val MAX_CHILD_ACTION_BUTTON_RENDER = 4

@Composable
fun ActionsSection(
    modifier: Modifier = Modifier,
    state: ActionsSectionState,
    onEvent: (ActionSectionEvent) -> Unit
) {
    val actionButtonModels = state.actionButtonModels.values
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        actionButtonModels.forEach { actionButtonModel ->
            if (actionButtonModel.enabled && actionButtonModel.childActionTypes.size in 2..MAX_CHILD_ACTION_BUTTON_RENDER) {
                Row(
                    modifier = Modifier.border(
                        border = BorderStroke(width = 1.dp, color = Color.Gray),
                        shape = RoundedCornerShape(4.dp),
                    ).padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    actionButtonModel.childActionTypes.forEach { childActionButtonType ->
                        ActionButton(
                            actionButtonType = childActionButtonType,
                            size = 40.dp,
                            enabled = true,
                            onClick = { onEvent(childActionButtonType.toActionsSectionEvent()) },
                        )
                    }
                }
            } else {
                ActionButton(
                    actionButtonType = actionButtonModel.actionType,
                    size = 48.dp,
                    enabled = actionButtonModel.enabled,
                    onClick = { onEvent(actionButtonModel.actionType.toActionsSectionEvent()) },
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ActionsSectionPreview() {
    val palette = PaletteModel(
        colors = listOf(
            Color.Black,
            Color.Yellow,
            Color.Red,
            Color.Blue,
            Color.Green,
        )
    )
    SimplePixelTheme {
        ActionsSection(
            modifier = Modifier.fillMaxSize(),
            state = ActionsSectionState().copy(
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
                            ActionButtonType.PickPaletteColorActionButtonType(
                                paletteIndex = 2,
                                palette = palette,
                            ),
                            ActionButtonType.PickPaletteColorActionButtonType(
                                paletteIndex = 3,
                                palette = palette,
                            ),
                        ),
                    ),
                    ActionButtonType.UndoActionButtonType to ActionButtonModel(
                        actionType = ActionButtonType.UndoActionButtonType,
                        enabled = true,
                    ),
                    ActionButtonType.RedoActionButtonType to ActionButtonModel(
                        actionType = ActionButtonType.RedoActionButtonType,
                        enabled = false,
                    ),
                    ActionButtonType.ZoomInActionButtonType to ActionButtonModel(
                        actionType = ActionButtonType.ZoomInActionButtonType,
                        enabled = true,
                    ),
                    ActionButtonType.ZoomOutActionButtonType to ActionButtonModel(
                        actionType = ActionButtonType.ZoomOutActionButtonType,
                        enabled = true,
                    ),
                )
            ),
        ) { actionType ->
            android.util.Log.d("ActionsSection", "action: $actionType")
        }
    }
}

fun ActionButtonType.toActionsSectionEvent(): ActionSectionEvent = when(this) {
    ActionButtonType.UndoActionButtonType -> ActionSectionEvent.UndoButtonClicked
    ActionButtonType.RedoActionButtonType -> ActionSectionEvent.RedoButtonClicked
    ActionButtonType.ZoomInActionButtonType -> ActionSectionEvent.ZoomInButtonClicked
    ActionButtonType.ZoomOutActionButtonType -> ActionSectionEvent.ZoomOutButtonClicked
    ActionButtonType.OpenPaletteActionButtonType -> ActionSectionEvent.OpenPaletteButtonClicked
    is ActionButtonType.PickPaletteColorActionButtonType -> ActionSectionEvent.PickPaletteColorButtonClicked(
        paletteIndex = paletteIndex,
    )
}
