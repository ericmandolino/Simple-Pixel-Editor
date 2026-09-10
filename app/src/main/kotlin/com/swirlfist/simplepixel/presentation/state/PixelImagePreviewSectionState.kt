package com.swirlfist.simplepixel.presentation.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import com.swirlfist.simplepixel.domain.model.PixelImageModel

data class PixelImagePreviewSectionState(
    val pixelImageModel: PixelImageModel? = null,
    val isFitAvailableSpace: Boolean = false,
    val selectedPreviewBackgroundColorLong: Long = Color.White.toColorLong(),
    val onPreviewBackgroundColorSelected: (Color) -> Unit,
)

fun PixelImagePreviewSectionState.updateSelectedPreviewBackgroundColor(color: Color) =
    copy(
        selectedPreviewBackgroundColorLong = color.toColorLong(),
    )