package com.swirlfist.simplepixel.presentation

import androidx.compose.ui.graphics.Color
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import kotlin.collections.map

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
            ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = index,
                palette = this,
            )
        )
    }
}