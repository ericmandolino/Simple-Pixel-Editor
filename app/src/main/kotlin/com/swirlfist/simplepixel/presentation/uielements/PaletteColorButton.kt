package com.swirlfist.simplepixel.presentation.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

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