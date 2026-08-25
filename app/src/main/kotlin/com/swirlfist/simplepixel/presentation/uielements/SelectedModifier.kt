package com.swirlfist.simplepixel.presentation.uielements

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

fun Modifier.selectedButtonModifier(
    isSelected: Boolean,
    selectedColor: Color,
): Modifier {
    return if (!isSelected) {
        this.then(
            other = Modifier
                .padding(4.dp)
        )
    } else {
        this.then(
            other = Modifier
                .border(
                    border = BorderStroke(width = 2.dp, color = selectedColor),
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(4.dp)
        )
    }
}