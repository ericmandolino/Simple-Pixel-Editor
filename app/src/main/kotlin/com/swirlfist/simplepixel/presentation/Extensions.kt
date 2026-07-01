package com.swirlfist.simplepixel.presentation

import androidx.compose.ui.graphics.Color
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import kotlin.collections.map

private const val HEX_FORMAT = "#%02x%02x%02x"

fun List<Color>.invertColors(): List<Color> = map { color ->
    color.invert()
}

fun Color.invert() = copy(
    red = 1F - red,
    green = 1F - green,
    blue = 1F - blue
)

fun List<Color>.getColor(pixel: PixelModel): Color? {
    val paletteIndex = pixel.paletteIndex

    return if (paletteIndex in indices) {
        get(paletteIndex)
    } else {
        null
    }
}

fun PixelMatrixModel.width() : Int = if (content.isEmpty()) 0 else content.first().size

fun PixelMatrixModel.height() : Int = content.size

fun PixelImageModel.getPixelAt(x: Int, y: Int): PixelModel = pixelMatrixModel.content[y][x]

fun PixelImageModel.getPixelWidth(): Int = pixelMatrixModel.width()

fun PixelImageModel.getPixelHeight(): Int = pixelMatrixModel.height()

fun PaletteModel.createPaletteButtons(): List<ActionModel.ButtonActionModel> {
    return colors.indices.map { index ->
        ActionModel.ButtonActionModel(
            actionType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = index,
                palette = this,
            ),
            isSelected = index == 0,
        )
    }
}

fun Color.toHexCode(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255

    return String.format(HEX_FORMAT, red.toInt(), green.toInt(), blue.toInt())
}