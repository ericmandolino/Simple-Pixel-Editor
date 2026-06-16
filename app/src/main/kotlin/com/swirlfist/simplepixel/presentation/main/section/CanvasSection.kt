package com.swirlfist.simplepixel.presentation.main.section

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
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
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import kotlin.math.max
import kotlin.math.min

private const val COORDINATE_TEXT_FORMAT = "%s,%s"

@Composable
fun CanvasSection(
    modifier: Modifier = Modifier,
    state: CanvasSectionState,
    onEvent: (CanvasSectionEvent) -> Unit,
) {
    val pixelImage = state.pixelImageModel

    if (pixelImage != null) {
        PixelCanvas(
            modifier = modifier,
            pixelImage = pixelImage,
            zoomFactor = state.zoomFactor,
            isShowGridEnabled = state.isShowGridEnabled,
            isShowCoordinatesEnabled = state.isShowCoordinatesEnabled,
            onPixelTap = { x, y -> onEvent(CanvasSectionEvent.PixelTap(x, y)) },
        )
    }
}

@Composable
private fun PixelCanvas(
    modifier: Modifier,
    pixelImage: PixelImageModel,
    zoomFactor: Float = 1F,
    isShowGridEnabled: Boolean = true,
    isShowCoordinatesEnabled: Boolean = false,
    onPixelTap: (xPixel: Int, yPixel: Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val palette = remember { pixelImage.paletteModel.colors.map { color -> Color.fromColorLong(color) } }
    val invertedPalette = remember {  palette.invertColors() }
    val imagePixelWidth = remember { pixelImage.getPixelWidth() }
    val imagePixelHeight = remember { pixelImage.getPixelHeight() }
    val imageOffsetX = rememberSaveable { mutableFloatStateOf(0F) }
    val imageOffsetY = rememberSaveable { mutableFloatStateOf(0F) }
    val lastCanvasWidth = rememberSaveable { mutableIntStateOf(-1) }
    val lastCanvasHeight = rememberSaveable { mutableIntStateOf(-1) }

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
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
            .pointerInput(zoomFactor) {
                detectDragGestures { _, dragAmount ->
                    val pixelSizeInt = getPixelSizeInt(zoomFactor)
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
            }
            .pointerInput(zoomFactor) {
                detectTapGestures { tapOffset ->
                    val pixelSizeInt = getPixelSizeInt(zoomFactor)
                    val xPixel = ((imageOffsetX.floatValue + tapOffset.x) / pixelSizeInt).toInt()
                    val yPixel = imagePixelHeight - 1 - ((imageOffsetY.floatValue + tapOffset.y) / pixelSizeInt).toInt()

                    if (xPixel !in 0..<imagePixelWidth) return@detectTapGestures
                    if (yPixel !in 0..<imagePixelHeight) return@detectTapGestures

                    onPixelTap(xPixel, yPixel)
                }
            },
    ) {
        val canvasSize = Size(size.width, size.height)
        val pixelSizeInt = getPixelSizeInt(zoomFactor)
        val imageSize = Size(
            width = (imagePixelWidth * pixelSizeInt).toFloat(),
            height = (imagePixelHeight * pixelSizeInt).toFloat(),
        )
        val halfPixelSize = pixelSizeInt / 2F
        val twoThirdsPixelSize = pixelSizeInt * 2F / 3F
        val gridLineWidth = 1.dp.toPx()

        val coordinateTextTemplate = "%s,%s"
        val maxVisibleCoordinateTextSize = textMeasurer.measure(
            text = String.format(coordinateTextTemplate, imagePixelWidth, imagePixelHeight)
        ).size
        val isShowCoordinates =
            isShowCoordinatesEnabled &&
            maxVisibleCoordinateTextSize.width < twoThirdsPixelSize &&
            maxVisibleCoordinateTextSize.height < twoThirdsPixelSize

        adjustImageOffset(imageOffsetX, imageOffsetY, canvasSize, imageSize)

        var y = 0F
        var yMatrixCoordinate = imagePixelHeight - 1 - ((imageOffsetY.floatValue + y) / pixelSizeInt).toInt()
        while (y < canvasSize.height && yMatrixCoordinate >= 0) {
            val pixelHeight = if (y > 0F || imageOffsetY.floatValue == 0F) {
                pixelSizeInt
            } else {
                pixelSizeInt - imageOffsetY.floatValue.toInt() % pixelSizeInt
            }

            var x = 0F
            var xMatrixCoordinate = ((imageOffsetX.floatValue + x) / pixelSizeInt).toInt()
            while (x < canvasSize.width && xMatrixCoordinate < imagePixelWidth) {
                val pixelWidth = if (x > 0F || imageOffsetX.floatValue == 0F) {
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
                    drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth)
                }

                x += pixelWidth
                xMatrixCoordinate++
            }

            if (isShowGridEnabled && xMatrixCoordinate == imagePixelWidth) {
                drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth)
            }

            if (isShowGridEnabled) {
                drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth)
            }

            y += pixelHeight
            yMatrixCoordinate--
        }

        if (isShowGridEnabled && yMatrixCoordinate == -1) {
            drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth)
        }
    }
}

private fun Density.getPixelSizeInt(
    zoomFactor: Float,
) = (32.dp.toPx() * zoomFactor).toInt()

private fun adjustImageOffset(
    imageOffsetX: MutableFloatState,
    imageOffsetY: MutableFloatState,
    canvasSize: Size,
    imageSize: Size,
) {
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
) {
    drawGridLine(
        isVertical = false,
        dimension = y,
        canvasSize,
        imageSize,
        width,
    )
}

private fun DrawScope.drawVerticalGridLine(
    x: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
) {
    drawGridLine(
        isVertical = true,
        dimension = x,
        canvasSize,
        imageSize,
        width,
    )
}

private fun DrawScope.drawGridLine(
    isVertical: Boolean,
    dimension: Float,
    canvasSize: Size,
    imageSize: Size,
    width: Float,
) {
    val start: Offset
    val end: Offset

    if (isVertical) {
        start = Offset(
            dimension,
            0F,
        )
        end = Offset(
            dimension,
            min(canvasSize.height, imageSize.height),
        )
    } else {
        start = Offset(
            0F,
            dimension,
        )
        end = Offset(
            min(canvasSize.width, imageSize.width),
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

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionPreview() {
    SimplePixelTheme {
        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = createCheckersPixelImage(
                    width = 5,
                    height = 3,
                    color1 = Color.Black.toColorLong(),
                    color2 = Color.Yellow.toColorLong(),
                ),
                zoomFactor = 4F,
                isShowCoordinatesEnabled = true,
            )
        ) { event ->
            android.util.Log.d("CanvasSection", "event: $event")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionEmptyImagePreview() {
    SimplePixelTheme {
        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = createEmptyPixelImage(
                    width = 4,
                    height = 4,
                    color1 = Color.Black.toColorLong(),
                    color2 = Color.Yellow.toColorLong(),
                ),
                zoomFactor = 1F,
                isShowCoordinatesEnabled = true,
            )
        ) { event ->
            android.util.Log.d("CanvasSection", "event: $event")
        }
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