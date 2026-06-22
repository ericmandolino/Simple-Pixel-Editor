package com.swirlfist.simplepixel.presentation.uielements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.main.section.ActionIconButtonType
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun ActionButton(
    modifier: Modifier = Modifier,
    actionButtonType: ActionButtonType,
    size: Dp = 48.dp,
    isEnabled: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box (
        modifier = modifier
            .selectedButtonModifier(
                isSelected,
                IconButtonDefaults.filledIconButtonColors().containerColor
            ),
    ) {
        when (actionButtonType) {
            ActionButtonType.InkBucketActionButtonType,
            ActionButtonType.InkEraserActionButtonType,
            ActionButtonType.InkPenActionButtonType,
            ActionButtonType.OpenToolsActionButtonType,
            ActionButtonType.UndoActionButtonType,
            ActionButtonType.RedoActionButtonType,
            ActionButtonType.ZoomInActionButtonType,
            ActionButtonType.ZoomOutActionButtonType,
            ActionButtonType.OpenPaletteActionButtonType,
            ActionButtonType.SavePixelImageActionButtonType,
            ActionButtonType.OpenPixelImageActionButtonType,
                -> {
                ActionIconButton(
                    modifier,
                    actionIconButtonType = actionButtonType as ActionIconButtonType,
                    size,
                    isEnabled,
                    onClick,
                )
            }

            is ActionButtonType.PickPaletteColorActionButtonType -> ActionPickPaletteColorButton(
                modifier,
                pickPaletteColorActionButtonType = actionButtonType,
                size,
                isEnabled,
                onClick,
            )
        }
    }
}

@Composable
private fun ActionIconButton(
    modifier: Modifier = Modifier,
    actionIconButtonType: ActionIconButtonType,
    size: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        modifier,
        drawableResId = actionIconButtonType.icon,
        contentDescriptionResId = actionIconButtonType.contentDescription,
        size,
        isEnabled,
        onClick,
    )
}

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableResId: Int,
    @StringRes contentDescriptionResId: Int,
    size: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    FilledIconButton(
        modifier = modifier
            .size(size),
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        enabled = isEnabled,
    ) {
        Icon(
            painter = painterResource(drawableResId),
            contentDescription = stringResource(contentDescriptionResId),
        )
    }
}

@Composable
private fun ActionPickPaletteColorButton(
    modifier: Modifier = Modifier,
    pickPaletteColorActionButtonType: ActionButtonType.PickPaletteColorActionButtonType,
    size: Dp,
    isEnabled: Boolean,
    onClick: () -> Unit,
) {
    val paletteIndex = pickPaletteColorActionButtonType.paletteIndex
    val color = Color.fromColorLong(pickPaletteColorActionButtonType.palette.colors[paletteIndex])
    val contentDescriptionValue = stringResource(
        R.string.cd_actions_section_button_pick_palette_color,
        paletteIndex
    )

    Button(
        modifier = modifier
            .size(size)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = IconButtonDefaults.filledIconButtonColors().containerColor,
                ),
                shape = RoundedCornerShape(4.dp),
            )
            .semantics {
                contentDescription = contentDescriptionValue
            },
        onClick = onClick,
        shape = RoundedCornerShape(4.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = color,
            contentColor = color,
        ),
    ) {
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonEnabledPreview() {
    SimplePixelTheme {
        ActionButton(
            actionButtonType = ActionButtonType.RedoActionButtonType,
            size = 48.dp,
            isEnabled = true,
            isSelected = false,
        ) {
            android.util.Log.d("ActionIconButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonSelectedPreview() {
    SimplePixelTheme {
        ActionButton(
            actionButtonType = ActionButtonType.RedoActionButtonType,
            size = 48.dp,
            isEnabled = true,
            isSelected = true,
        ) {
            android.util.Log.d("ActionIconButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonDisabledPreview() {
    SimplePixelTheme {
        SimplePixelTheme {
            ActionButton(
                actionButtonType = ActionButtonType.RedoActionButtonType,
                size = 48.dp,
                isEnabled = false,
                isSelected = false,
            ) {
                android.util.Log.d("ActionIconButton", "clicked!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionPickPaletteColorButtonPreview() {
    SimplePixelTheme {
        ActionButton(
            actionButtonType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 0,
                palette = PaletteModel(
                    colors = listOf(
                        Color.Blue.toColorLong(),
                        Color.Red.toColorLong()
                    )
                ),
            ),
            size = 48.dp,
            isEnabled = true,
            isSelected = false,
        ) {
            android.util.Log.d("ActionPickPaletteColorButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionPickPaletteColorButtonSelectedPreview() {
    SimplePixelTheme {
        ActionButton(
            actionButtonType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 0,
                palette = PaletteModel(
                    colors = listOf(
                        Color.Blue.toColorLong(),
                        Color.Red.toColorLong()
                    )
                ),
            ),
            size = 48.dp,
            isEnabled = true,
            isSelected = true,
        ) {
            android.util.Log.d("ActionPickPaletteColorButton", "clicked!")
        }
    }
}

private fun Modifier.selectedButtonModifier(
    isSelected: Boolean,
    selectedColor: Color,
): Modifier {
    return if (!isSelected) {
        this.then( other = Modifier
            .padding(4.dp)
        )
    } else {
        this.then( other = Modifier
            .border(
                border = BorderStroke(width = 2.dp, color = selectedColor),
                shape = RoundedCornerShape(4.dp),
            )
            .padding(4.dp)
        )
    }
}