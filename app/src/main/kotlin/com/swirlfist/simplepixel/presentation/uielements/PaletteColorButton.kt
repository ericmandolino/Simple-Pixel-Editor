package com.swirlfist.simplepixel.presentation.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun PaletteColorButton(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp,
    isEnabled: Boolean,
    contentDescriptionValue: String,
    onClick: () -> Unit,
) {
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

@Composable
fun PaletteColorSelectButton(
    color: Color,
    size: Dp,
    isEnabled: Boolean,
    contentDescriptionValue: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .selectedButtonModifier(
                isSelected,
                IconButtonDefaults.filledIconButtonColors().containerColor
            ),
    ) {
        PaletteColorButton(
            modifier = Modifier,
            color,
            size,
            isEnabled,
            contentDescriptionValue,
            onClick,
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PaletteColorSelectButtonPreview() {
    SimplePixelTheme {
        PaletteColorSelectButton(
            color = Color.Red,
            size = 48.dp,
            isEnabled = true,
            contentDescriptionValue = "",
            isSelected = true,
            onClick = {},
        )
    }
}