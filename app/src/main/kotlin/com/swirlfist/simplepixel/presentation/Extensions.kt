package com.swirlfist.simplepixel.presentation

import androidx.compose.ui.graphics.Color
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import kotlin.collections.map

fun PaletteModel.invert(): PaletteModel = PaletteModel(
    colors.map { color ->
        color.copy(
            red = 1F - color.red,
            green = 1F - color.green,
            blue = 1F - color.blue,
        )
    }
)

fun PaletteModel.getColor(pixel: PixelModel): Color? {
    val paletteIndex = pixel.paletteIndex

    return if (paletteIndex in colors.indices) {
        colors[paletteIndex]
    } else {
        null
    }
}

fun PixelMatrixModel.width() : Int = if (content.isEmpty()) 0 else content.first().size

fun PixelMatrixModel.height() : Int = content.size

fun PixelImageModel.getPixelAt(x: Int, y: Int): PixelModel = pixelMatrixModel.content[y][x]

fun PixelImageModel.getPixelWidth(): Int = pixelMatrixModel.width()

fun PixelImageModel.getPixelHeight(): Int = pixelMatrixModel.height()