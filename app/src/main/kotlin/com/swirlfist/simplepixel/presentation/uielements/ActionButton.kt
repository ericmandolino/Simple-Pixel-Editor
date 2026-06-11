package com.swirlfist.simplepixel.presentation.uielements

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
    enabled: Boolean,
    onClick: () -> Unit,
) {
    when (actionButtonType) {
        ActionButtonType.UndoActionButtonType,
        ActionButtonType.RedoActionButtonType,
        ActionButtonType.ZoomInActionButtonType,
        ActionButtonType.ZoomOutActionButtonType,
        ActionButtonType.OpenPaletteActionButtonType -> {
            ActionIconButton(
                modifier = modifier,
                actionIconButtonType = actionButtonType as ActionIconButtonType,
                size = size,
                enabled = enabled,
                onClick = onClick,
            )
        }
        is ActionButtonType.PickPaletteColorActionButtonType -> ActionPickPaletteColorButton(
            modifier = modifier,
            pickPaletteColorActionButtonType = actionButtonType,
            size = size,
            enabled = enabled,
            onClick = onClick,
        )
    }
}

@Composable
private fun ActionIconButton(
    modifier: Modifier = Modifier,
    actionIconButtonType: ActionIconButtonType,
    size: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    IconButton(
        modifier = modifier,
        drawableResId = actionIconButtonType.icon,
        contentDescriptionResId = actionIconButtonType.contentDescription,
        size = size,
        enabled = enabled,
        onClick = onClick,
    )
}

@Composable
fun IconButton(
    modifier: Modifier = Modifier,
    @DrawableRes drawableResId: Int,
    @StringRes contentDescriptionResId: Int,
    size: Dp,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    FilledIconButton(
        modifier = modifier
            .size(size),
        onClick = onClick,
        shape =  RoundedCornerShape(4.dp),
        enabled = enabled,
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
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val paletteIndex = pickPaletteColorActionButtonType.paletteIndex
    val color = pickPaletteColorActionButtonType.palette.colors[paletteIndex]
    val contentDescriptionValue = stringResource(
    R.string.cd_actions_section_button_pick_palette_color,
    paletteIndex
    )
    Button(
        modifier = modifier
            .size(size)
            .semantics {
                contentDescription = contentDescriptionValue
            },
        onClick = onClick,
        shape =  RoundedCornerShape(4.dp),
        enabled = enabled,
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
        IconButton(
            drawableResId = android.R.drawable.ic_menu_save,
            contentDescriptionResId = android.R.string.ok,
            size = 48.dp,
            enabled = true,
        ) {
            android.util.Log.d("ActionIconButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun IconButtonDisabledPreview() {
    SimplePixelTheme {
        IconButton(
            drawableResId = android.R.drawable.ic_menu_save,
            contentDescriptionResId = android.R.string.ok,
            size = 48.dp,
            enabled = false,
        ) {
            android.util.Log.d("ActionIconButton", "clicked!")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionPickPaletteColorButtonPreview() {
    SimplePixelTheme {
        ActionPickPaletteColorButton(
            pickPaletteColorActionButtonType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = 0,
                palette = PaletteModel(colors = listOf(Color.Blue, Color.Red)),
            ),
            size = 40.dp,
            enabled = true,
        ) {
            android.util.Log.d("ActionPickPaletteColorButton", "clicked!")
        }
    }
}