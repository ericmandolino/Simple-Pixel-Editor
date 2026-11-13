package com.swirlfist.simplepixel.domain

import androidx.compose.ui.graphics.Color
import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import kotlin.math.max
import kotlin.math.min

private const val HEX_FORMAT = "#%02x%02x%02x"

fun PixelMatrixModel.width(): Int = if (content.isEmpty()) 0 else content.first().size

fun PixelMatrixModel.height(): Int = content.size

fun PixelMatrixModel.isBlank(): Boolean {
    content.forEach { row ->
        row.forEach { pixel ->
            if (pixel.paletteIndex != EMPTY_PIXEL_PALETTE_INDEX) {
                return false
            }
        }
    }

    return true
}

fun PixelImageModel.getPixelAt(x: Int, y: Int): PixelModel = pixelMatrixModel.content[y][x]

fun PixelImageModel.getPixelWidth(): Int = pixelMatrixModel.width()

fun PixelImageModel.getPixelHeight(): Int = pixelMatrixModel.height()

fun PixelSelectionModel.isSelected(
    x: Int,
    y: Int,
): Boolean {
    if (y !in selected.indices) {
        return false
    }

    val row = selected[y]
    if (x !in row.indices) {
        return false
    }

    return row[x] != null
}

fun PixelImageModel.createSelection(
    selectedCoordinates: List<Pair<Int, Int>>,
): PixelSelectionModel {
    val selection = mutableListOf<MutableList<PixelModel?>>()
    val matrixHeight = pixelMatrixModel.height()
    val matrixWidth = pixelMatrixModel.width()

    repeat(matrixHeight) {
        val selectionRow = mutableListOf<PixelModel?>()
        repeat(matrixWidth) {
            selectionRow.add(null)
        }
        selection.add(selectionRow)
    }

    selectedCoordinates.forEach { (x, y) ->
        selection[y][x] = pixelMatrixModel.content[y][x]
    }

    return PixelSelectionModel(selected = selection)
}

fun RectangleSelectionModel.topLeft() = Pair(
    min(start.first, end.first),
    min(start.second, end.second),
)

fun RectangleSelectionModel.topRight() = Pair(
    max(start.first, end.first),
    min(start.second, end.second),
)

fun RectangleSelectionModel.bottomLeft() = Pair(
    min(start.first, end.first),
    max(start.second, end.second),
)

fun RectangleSelectionModel.bottomRight() = Pair(
    max(start.first, end.first),
    max(start.second, end.second),
)

fun RectangleSelectionModel.top() = min(start.second, end.second)

fun RectangleSelectionModel.bottom() = max(start.second, end.second)

fun RectangleSelectionModel.left() = min(start.first, end.first)

fun RectangleSelectionModel.right() = max(start.first, end.first)

fun RectangleSelectionModel.isEmpty() = start.first == end.first || start.second == end.second

fun PixelImageModel.selectNonEmpty(): PixelSelectionModel {
    return PixelSelectionModel(
        selected = pixelMatrixModel.content.map { row ->
            row.map { pixel ->
                if (pixel.paletteIndex == EMPTY_PIXEL_PALETTE_INDEX) {
                    null
                } else {
                    pixel
                }
            }
        }
    )
}

fun Color.toHexCode(): String {
    val red = this.red * 255
    val green = this.green * 255
    val blue = this.blue * 255

    return String.format(HEX_FORMAT, red.toInt(), green.toInt(), blue.toInt())
}