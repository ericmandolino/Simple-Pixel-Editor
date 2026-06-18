package com.swirlfist.simplepixel.presentation.uielements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.presentation.getColor
import com.swirlfist.simplepixel.presentation.getPixelAt
import com.swirlfist.simplepixel.presentation.getPixelHeight
import com.swirlfist.simplepixel.presentation.getPixelWidth
import com.swirlfist.simplepixel.presentation.invertColors
import kotlin.math.max
import kotlin.math.min

private const val COORDINATE_TEXT_FORMAT = "%s,%s"
private const val NO_ZOOM_FACTOR = 1F
private const val PIXEL_SIZE_DP_CANVAS = 32
private const val PIXEL_SIZE_DP_PREVIEW = 1

@Composable
fun PixelCanvas(
    modifier: Modifier,
    pixelImage: PixelImageModel,
    zoomFactor: Float = NO_ZOOM_FACTOR,
    isShowGridEnabled: Boolean = true,
    isShowCoordinatesEnabled: Boolean = false,
    onPixelTap: (xPixel: Int, yPixel: Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val imageOffsetX = rememberSaveable { mutableFloatStateOf(0F) }
    val imageOffsetY = rememberSaveable { mutableFloatStateOf(0F) }
    val marginX = rememberSaveable { mutableFloatStateOf(0F) }
    val marginY = rememberSaveable { mutableFloatStateOf(0F) }
    val lastCanvasWidth = rememberSaveable { mutableIntStateOf(-1) }
    val lastCanvasHeight = rememberSaveable { mutableIntStateOf(-1) }

    val imagePixelWidth = pixelImage.getPixelWidth()
    val imagePixelHeight = pixelImage.getPixelHeight()

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                onCanvasSizeChanged(size, lastCanvasWidth, lastCanvasHeight, imageOffsetX , imageOffsetY)
            }
            .pointerInput(zoomFactor) {
                detectDragGestures { _, dragAmount ->
                    val pixelSizeDp = PIXEL_SIZE_DP_CANVAS
                    onCanvasDrag(dragAmount, pixelSizeDp, imagePixelWidth, imagePixelHeight, zoomFactor, imageOffsetX, imageOffsetY)
                }
            }
            .pointerInput(zoomFactor) {
                detectTapGestures { tapOffset ->
                    val pixelSizeDp = PIXEL_SIZE_DP_CANVAS
                    val canvasSize = Size(
                        lastCanvasWidth.intValue.toFloat(),
                        lastCanvasHeight.intValue.toFloat()
                    )
                    onCanvasTap(tapOffset, pixelSizeDp, imagePixelWidth, imagePixelHeight, zoomFactor, canvasSize, imageOffsetX, imageOffsetY, marginX, marginY, onPixelTap)
                }
            },
    ) {
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor)
        val twoThirdsPixelSize = pixelSizeInt * 2F / 3F

        val coordinateTextTemplate = "%s,%s"
        val maxVisibleCoordinateTextSize = textMeasurer.measure(
            text = String.format(coordinateTextTemplate, imagePixelWidth, imagePixelHeight)
        ).size
        val isShowCoordinates =
            isShowCoordinatesEnabled &&
                    maxVisibleCoordinateTextSize.width < twoThirdsPixelSize &&
                    maxVisibleCoordinateTextSize.height < twoThirdsPixelSize

        drawCanvas(
            pixelImage,
            pixelSizeInt,
            imagePixelWidth,
            imagePixelHeight,
            imageOffsetX,
            imageOffsetY,
            marginX,
            marginY,
            isShowGridEnabled,
            isShowCoordinates,
            textMeasurer,
        )
    }
}

@Composable
fun PixelCanvasSnapshot(
    modifier: Modifier,
    pixelImage: PixelImageModel,
    isFitAvailableSpace: Boolean = false,
) {
    val textMeasurer = rememberTextMeasurer()
    val imageOffsetX = rememberSaveable { mutableFloatStateOf(0F) }
    val imageOffsetY = rememberSaveable { mutableFloatStateOf(0F) }
    val marginX = rememberSaveable { mutableFloatStateOf(0F) }
    val marginY = rememberSaveable { mutableFloatStateOf(0F) }
    val lastCanvasWidth = rememberSaveable { mutableIntStateOf(-1) }
    val lastCanvasHeight = rememberSaveable { mutableIntStateOf(-1) }
    val zoomFactor = rememberSaveable { mutableFloatStateOf(NO_ZOOM_FACTOR) }

    val imagePixelWidth = pixelImage.getPixelWidth()
    val imagePixelHeight = pixelImage.getPixelHeight()

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                onCanvasSizeChanged(size, lastCanvasWidth, lastCanvasHeight, imageOffsetX , imageOffsetY)
            }
            .pointerInput(zoomFactor.floatValue) {
                detectDragGestures { _, dragAmount ->
                    val pixelSizeDp = PIXEL_SIZE_DP_PREVIEW
                    onCanvasDrag(dragAmount, pixelSizeDp, imagePixelWidth, imagePixelHeight, zoomFactor.floatValue, imageOffsetX, imageOffsetY)
                }
            },
    ) {
        zoomFactor.floatValue = if (isFitAvailableSpace) {
            val pixelSizeNoZoom = PIXEL_SIZE_DP_PREVIEW.dp.toPx()
            val fitWidthZoomFactor = (size.width / (imagePixelWidth * pixelSizeNoZoom)).toInt()
            val fitHeightZoomFactor = (size.height / (imagePixelHeight * pixelSizeNoZoom)).toInt()
            max(NO_ZOOM_FACTOR, min(fitWidthZoomFactor, fitHeightZoomFactor).toFloat())
        } else {
            NO_ZOOM_FACTOR
        }
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_PREVIEW, zoomFactor.floatValue)

        drawCanvas(
            pixelImage,
            pixelSizeInt,
            imagePixelWidth,
            imagePixelHeight,
            imageOffsetX,
            imageOffsetY,
            marginX,
            marginY,
            isShowGridEnabled = false,
            isShowCoordinates = false,
            textMeasurer,
        )
    }
}

private fun onCanvasSizeChanged(
    size: IntSize,
    lastCanvasWidth: MutableIntState,
    lastCanvasHeight: MutableIntState,
    imageOffsetX: MutableFloatState,
    imageOffsetY: MutableFloatState,
) {
    val lastWidth = lastCanvasWidth.intValue
    val lastHeight = lastCanvasHeight.intValue
    val currentWidth = size.width
    val currentHeight = size.height

    if ((lastWidth >= 0) && (currentWidth > lastWidth)) {
        val widthIncrease = currentWidth - lastWidth
        imageOffsetX.floatValue = max(0F, imageOffsetX.floatValue - widthIncrease)
    }

    if ((lastHeight >= 0) && (currentHeight > lastHeight)) {
        val heightIncrease = currentHeight - lastHeight
        imageOffsetY.floatValue = max(0F, imageOffsetY.floatValue - heightIncrease)
    }

    lastCanvasWidth.intValue = currentWidth
    lastCanvasHeight.intValue = currentHeight
}

private fun PointerInputScope.onCanvasDrag(
    dragAmount: Offset,
    pixelSizeDp: Int,
    imagePixelWidth: Int,
    imagePixelHeight: Int,
    zoomFactor: Float,
    imageOffsetX: MutableFloatState,
    imageOffsetY: MutableFloatState,
) {
    val pixelSizeInt = getPixelSizeInt(pixelSizeDp, zoomFactor)
    val imageWidthInt = imagePixelWidth * pixelSizeInt
    val imageHeightInt = imagePixelHeight * pixelSizeInt

    val minImageOffset = Offset(
        x = 0F,
        y = 0F,
    )
    val maxImageOffset = Offset(
        x = max(imageWidthInt - size.width, 0).toFloat(),
        y = max(imageHeightInt - size.height, 0).toFloat(),
    )

    imageOffsetX.floatValue = (imageOffsetX.floatValue - dragAmount.x)
        .cap(minImageOffset.x, maxImageOffset.x)
    imageOffsetY.floatValue = (imageOffsetY.floatValue - dragAmount.y)
        .cap(minImageOffset.y, maxImageOffset.y)
}

private fun PointerInputScope.onCanvasTap(
    tapOffset: Offset,
    pixelSizeDp: Int,
    imagePixelWidth: Int,
    imagePixelHeight: Int,
    zoomFactor: Float,
    canvasSize: Size,
    imageOffsetX: MutableFloatState,
    imageOffsetY: MutableFloatState,
    marginX: MutableFloatState,
    marginY: MutableFloatState,
    onPixelTap: (Int, Int) -> Unit,
) {
    if (tapOffset.x - marginX.floatValue !in 0F..canvasSize.width ||
        tapOffset.y - marginY.floatValue !in 0F..canvasSize.height) {
        return
    }

    val pixelSizeInt = getPixelSizeInt(pixelSizeDp, zoomFactor)
    val xPixel = ((imageOffsetX.floatValue + tapOffset.x - marginX.floatValue) / pixelSizeInt).toInt()
    val yPixel = imagePixelHeight - 1 - ((imageOffsetY.floatValue + tapOffset.y - marginY.floatValue) / pixelSizeInt).toInt()

    if (xPixel !in 0..<imagePixelWidth) return
    if (yPixel !in 0..<imagePixelHeight) return

    onPixelTap(xPixel, yPixel)
}

private fun Density.getPixelSizeInt(
    dpSize: Int,
    zoomFactor: Float,
) = (dpSize.dp.toPx() * zoomFactor).toInt()

private fun adjustImageOffset(
    imageOffsetX: MutableFloatState,
    imageOffsetY: MutableFloatState,
    marginX: MutableFloatState,
    marginY: MutableFloatState,
    canvasSize: Size,
    imageSize: Size,
) {
    if (canvasSize.width > imageSize.width) {
        imageOffsetX.floatValue = 0F
        marginX.floatValue = (canvasSize.width - imageSize.width) / 2
    } else {
        marginX.floatValue = 0F

        if (imageOffsetX.floatValue > 0) {
            val canvasWidth = canvasSize.width
            val imageWidth = imageSize.width

            if (imageWidth <= canvasWidth) {
                imageOffsetX.floatValue = 0F
            } else {
                val xOffsetSurplus = canvasWidth + imageOffsetX.floatValue - imageWidth
                if (xOffsetSurplus > 0) {
                    imageOffsetX.floatValue -= xOffsetSurplus
                }
            }
        }
    }

    if (canvasSize.height > imageSize.height) {
        imageOffsetY.floatValue = 0F
        marginY.floatValue = (canvasSize.height - imageSize.height) / 2
    } else {
        marginY.floatValue = 0F

        if (imageOffsetY.floatValue > 0) {
            val canvasHeight = canvasSize.height
            val imageHeight = imageSize.height

            if (imageHeight <= canvasHeight) {
                imageOffsetY.floatValue = 0F
            } else {
                val deltaYSurplus = canvasHeight + imageOffsetY.floatValue - imageHeight
                if (deltaYSurplus > 0) {
                    imageOffsetY.floatValue -= deltaYSurplus
                }
            }
        }
    }
}

private fun DrawScope.drawCanvas(
    pixelImage: PixelImageModel,
    pixelSizeInt: Int,
    imagePixelWidth: Int,
    imagePixelHeight: Int,
    imageOffsetX: MutableFloatState,
    imageOffsetY: MutableFloatState,
    marginX: MutableFloatState,
    marginY: MutableFloatState,
    isShowGridEnabled: Boolean,
    isShowCoordinates: Boolean,
    textMeasurer: TextMeasurer,
) {
    val canvasSize = Size(size.width, size.height)
    val imageSize = Size(
        width = (imagePixelWidth * pixelSizeInt).toFloat(),
        height = (imagePixelHeight * pixelSizeInt).toFloat(),
    )
    val halfPixelSize = pixelSizeInt / 2F
    val gridLineWidth = 1.dp.toPx()
    val palette = pixelImage.paletteModel.colors.map { color -> Color.fromColorLong(color) }
    val invertedPalette = palette.invertColors()

    adjustImageOffset(imageOffsetX, imageOffsetY, marginX, marginY, canvasSize, imageSize)

    var y = marginY.floatValue
    var yMatrixCoordinate = imagePixelHeight - 1 - ((imageOffsetY.floatValue + y - marginY.floatValue) / pixelSizeInt).toInt()
    while (y < canvasSize.height && yMatrixCoordinate >= 0) {
        val pixelHeight = if (y - marginY.floatValue > 0F || imageOffsetY.floatValue == 0F) {
            pixelSizeInt
        } else {
            pixelSizeInt - imageOffsetY.floatValue.toInt() % pixelSizeInt
        }

        var x = marginX.floatValue
        var xMatrixCoordinate = ((imageOffsetX.floatValue + x - marginX.floatValue) / pixelSizeInt).toInt()
        while (x < canvasSize.width && xMatrixCoordinate < imagePixelWidth) {
            val pixelWidth = if (x - marginX.floatValue > 0F || imageOffsetX.floatValue == 0F) {
                pixelSizeInt
            } else {
                pixelSizeInt - imageOffsetX.floatValue.toInt() % pixelSizeInt
            }

            val pixel = pixelImage.getPixelAt(
                x = xMatrixCoordinate,
                y = yMatrixCoordinate
            )

            drawPixel(
                pixel,
                palette = palette,
                width = pixelWidth,
                height = pixelHeight,
                xMatrixCoordinate,
                yMatrixCoordinate,
                offset = Offset(x, y),
                canvasSize,
                isShowCoordinates,
                textMeasurer,
                coordinateTextPalette = invertedPalette,
                halfPixelSize,
            )

            if (isShowGridEnabled) {
                drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth, marginY.floatValue)
            }

            x += pixelWidth
            xMatrixCoordinate++
        }

        if (isShowGridEnabled && xMatrixCoordinate == imagePixelWidth) {
            drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth, marginY.floatValue)
        }

        if (isShowGridEnabled) {
            drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth, marginX.floatValue)
        }

        y += pixelHeight
        yMatrixCoordinate--
    }

    if (isShowGridEnabled && yMatrixCoordinate == -1) {
        drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth, marginX.floatValue)
    }
}

private fun DrawScope.drawPixel(
    pixel: PixelModel,
    palette: List<Color>,
    width: Int,
    height: Int,
    xMatrixCoordinate: Int,
    yMatrixCoordinate: Int,
    offset: Offset,
    canvasSize: Size,
    isShowCoordinates: Boolean,
    textMeasurer: TextMeasurer,
    coordinateTextPalette: List<Color>,
    halfPixelSize: Float,
) {
    val isXMatrixCoordinateEven = xMatrixCoordinate % 2 == 0
    val isYMatrixCoordinateEven = yMatrixCoordinate % 2 == 0
    val pixelColor = getColor(
        pixel,
        palette,
        isXMatrixCoordinateEven,
        isYMatrixCoordinateEven,
        Pair(Color.Gray, Color.LightGray),
    )
    val pixelRectSize = Size(width.toFloat(), height.toFloat())

    drawRect(
        color = pixelColor,
        topLeft = offset,
        size = Size(
            width = min(pixelRectSize.width, canvasSize.width - offset.x),
            height = min(pixelRectSize.height, canvasSize.height - offset.y),
        ),
    )

    if (isShowCoordinates) {
        val coordinateText = COORDINATE_TEXT_FORMAT.format(
            xMatrixCoordinate + 1, yMatrixCoordinate + 1)
        val textSize = textMeasurer.measure(
            text = coordinateText
        ).size

        val textColor = getColor(
            pixel,
            coordinateTextPalette,
            isXMatrixCoordinateEven,
            isYMatrixCoordinateEven,
            Pair(Color.LightGray, Color.Gray),
        )
        val coordinateTextOffset = offset + Offset(
            x = width - halfPixelSize - textSize.width / 2,
            y = height - halfPixelSize - textSize.height / 2,
        )

        drawText(
            textMeasurer,
            topLeft = coordinateTextOffset,
            text = coordinateText,
            style = TextStyle.Default.copy(
                color = textColor,
            ),
            softWrap = false,
        )
    }
}

private fun DrawScope.drawHorizontalGridLine(
    y: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
    margin: Float,
) {
    drawGridLine(
        isVertical = false,
        dimension = y,
        canvasSize,
        imageSize,
        width,
        margin
    )
}

private fun DrawScope.drawVerticalGridLine(
    x: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
    margin: Float,
) {
    drawGridLine(
        isVertical = true,
        dimension = x,
        canvasSize,
        imageSize,
        width,
        margin,
    )
}

private fun DrawScope.drawGridLine(
    isVertical: Boolean,
    dimension: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
    margin: Float,
) {
    val start: Offset
    val end: Offset

    if (isVertical) {
        start = Offset(
            dimension,
            margin,
        )
        end = Offset(
            dimension,
            min(canvasSize.height, imageSize.height + margin),
        )
    } else {
        start = Offset(
            margin,
            dimension,
        )
        end = Offset(
            min(canvasSize.width, imageSize.width + margin),
            dimension,
        )
    }

    drawLine(Color.DarkGray, start, end, width)
}

private fun getColor(
    pixel: PixelModel,
    palette: List<Color>,
    isXMatrixCoordinateEven: Boolean,
    isYMatrixCoordinateEven: Boolean,
    defaultColors: Pair<Color, Color>,
): Color {
    palette.getColor(pixel)?.let { color -> return color }

    return if (
        (isXMatrixCoordinateEven && !isYMatrixCoordinateEven) ||
        (!isXMatrixCoordinateEven && isYMatrixCoordinateEven)
    ) {
        defaultColors.first
    } else {
        defaultColors.second
    }
}

private fun Float.cap(min: Float, max: Float): Float = when {
    this < min -> min
    this > max -> max
    else -> this
}

fun createCheckersPixelImage(
    width: Int,
    height: Int,
    color1: Long,
    color2: Long,
): PixelImageModel {
    val rows = mutableListOf<List<PixelModel>>()
    for (y in 0..< height) {
        val row = mutableListOf<PixelModel>()
        for (x in 0..< width) {
            val isXEven = x % 2 == 0
            val isYEven = y % 2 == 0
            row.add(
                PixelModel(if ((isXEven && !isYEven) || (!isXEven && isYEven)) 0 else 1)
            )
        }
        rows.add(row)
    }

    return PixelImageModel(
        pixelMatrixModel = PixelMatrixModel(
            content = rows
        ),
        paletteModel = PaletteModel(listOf(color1, color2)),
    )
}

fun createEmptyPixelImage(
    width: Int,
    height: Int,
    color1: Long,
    color2: Long,
): PixelImageModel {
    val rows = mutableListOf<List<PixelModel>>()
    repeat(height) {
        val row = mutableListOf<PixelModel>()
        repeat(width) {
            row.add(
                PixelModel(-1)
            )
        }
        rows.add(row)
    }

    return PixelImageModel(
        pixelMatrixModel = PixelMatrixModel(
            content = rows
        ),
        paletteModel = PaletteModel(listOf(color1, color2)),
    )
}