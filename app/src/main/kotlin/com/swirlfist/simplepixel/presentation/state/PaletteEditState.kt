package com.swirlfist.simplepixel.presentation.state

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import kotlin.collections.plus

data class PaletteEditState(
    val paletteColors: List<Long> = listOf(),
    val selectedPaletteIndex: Int? = null,
    val onAddPaletteColorClick: () -> Unit = {},
    val onPaletteColorClick: (Int) -> Unit = {},
    val onDeletePaletteColorClick: () -> Unit = {},
    val onColorComponentRedSliderChange: (Float) -> Unit = {},
    val onColorComponentGreenSliderChange: (Float) -> Unit = {},
    val onColorComponentBlueSliderChange: (Float) -> Unit = {},
)

fun PaletteEditState.addNewColor() = copy(
    paletteColors = paletteColors + Color.Black.toColorLong(),
)


fun PaletteEditState.updateSelectedColor(
    paletteIndex: Int,
) = copy(
    selectedPaletteIndex = if (selectedPaletteIndex == paletteIndex) {
        null
    } else {
        paletteIndex
    },
)

fun PaletteEditState.deleteSelectedColor() = copy(
    paletteColors = paletteColors.toMutableList().apply {
        if (selectedPaletteIndex != null && selectedPaletteIndex in paletteColors.indices) {
            removeAt(selectedPaletteIndex)
        }
    }
)

fun PaletteEditState.updateColorComponentRed(
    value: Float,
) = updateColorComponent(value, ColorComponent.RED)

fun PaletteEditState.updateColorComponentGreen(
    value: Float,
) = updateColorComponent(value, ColorComponent.GREEN)

fun PaletteEditState.updateColorComponentBlue(
    value: Float,
) = updateColorComponent(value, ColorComponent.BLUE)

private fun PaletteEditState.updateColorComponent(
    value: Float,
    colorComponent: ColorComponent,
) = copy(
    paletteColors = paletteColors.toMutableList().apply {
        if (value in 0F..1F && selectedPaletteIndex != null && selectedPaletteIndex in paletteColors.indices) {
            val color = Color.fromColorLong(paletteColors[selectedPaletteIndex])

            val newColor = when (colorComponent) {
                ColorComponent.RED -> color.copy(
                    red = value,
                )

                ColorComponent.GREEN -> color.copy(
                    green = value,
                )

                ColorComponent.BLUE -> color.copy(
                    blue = value,
                )
            }
            set(selectedPaletteIndex, newColor.toColorLong())
        }
    }
)

private enum class ColorComponent {
    RED,
    GREEN,
    BLUE,
}

