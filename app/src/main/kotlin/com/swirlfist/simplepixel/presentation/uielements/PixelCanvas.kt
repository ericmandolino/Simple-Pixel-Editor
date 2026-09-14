package com.swirlfist.simplepixel.presentation.uielements

import android.util.SizeF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.swirlfist.simplepixel.presentation.invert
import com.swirlfist.simplepixel.presentation.invertColors
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val COORDINATE_TEXT_FORMAT = "%s,%s"
private const val NO_ZOOM_FACTOR = 1F
private const val PIXEL_SIZE_DP_CANVAS = 32
private const val PIXEL_SIZE_DP_PREVIEW = 1
const val MAX_ZOOM_FACTOR = 2F
const val MIN_ZOOM_FACTOR = 0.25F
const val PAN_VS_ZOOM_FACTOR = 0.1F
const val PAN_VS_ZOOM_COUNT = 3

@Composable
fun PixelCanvas(
    modifier: Modifier,
    pixelImage: PixelImageModel,
    initialZoomFactor: Float = NO_ZOOM_FACTOR,
    isShowGridEnabled: Boolean = true,
    isShowCoordinatesEnabled: Boolean = false,
    backgroundColor: Color? = null,
    backGroundCheckersColors: Pair<Color, Color>? = Pair(Color.Gray, Color.LightGray),
    onPixelTap: (xPixel: Int, yPixel: Int) -> Unit,
    onZoomUpdate: (Float) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val offsetSaver = createOffsetSaver()
    val intSizeSaver = createIntSizeSaver()
    val imageOffset = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset.Zero) }
    val margin = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset.Zero) }
    val lastCanvasSize =
        rememberSaveable(stateSaver = intSizeSaver) { mutableStateOf(IntSize(-1, -1)) }
    var zoomFactor by rememberSaveable { mutableFloatStateOf(initialZoomFactor) }
    var lastPanDistance by rememberSaveable { mutableFloatStateOf(0F) }
    var lastZoomDistance by rememberSaveable { mutableFloatStateOf(0F) }
    var panCount by rememberSaveable { mutableIntStateOf(0) }
    var zoomCount by rememberSaveable { mutableIntStateOf(0) }
    var isPanning by rememberSaveable { mutableStateOf(false) }
    var isZooming by rememberSaveable { mutableStateOf(false) }

    val imagePixelSize = IntSize(pixelImage.getPixelWidth(), pixelImage.getPixelHeight())

    LaunchedEffect(initialZoomFactor) {
        zoomFactor = initialZoomFactor
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                onCanvasSizeChanged(size, lastCanvasSize, imageOffset)
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val pixelSizeDp = PIXEL_SIZE_DP_CANVAS
                    val canvasSize = Size(
                        lastCanvasSize.value.width.toFloat(),
                        lastCanvasSize.value.height.toFloat()
                    )
                    onCanvasTap(
                        tapOffset,
                        pixelSizeDp,
                        imagePixelSize,
                        zoomFactor,
                        canvasSize,
                        imageOffset.value,
                        margin.value,
                        onPixelTap
                    )
                }
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.size != 2) {
                            lastPanDistance = 0F
                            lastZoomDistance = 0F
                            isPanning = false
                            isZooming = false
                            panCount = 0
                            zoomCount = 0
                            continue
                        }

                        val pan = event.calculatePan()
                        val panDistance = pan.getDistance()
                        val zoomDistance = (event.changes[0].position - event.changes[1].position).getDistance()

                        if (lastPanDistance != 0F || lastZoomDistance != 0F) {
                            val panDistanceDelta = abs(panDistance - lastPanDistance)
                            val zoomDistanceDelta = abs(zoomDistance - lastZoomDistance)

                            if (panDistanceDelta >= zoomDistanceDelta * PAN_VS_ZOOM_FACTOR) {
                                panCount++
                                zoomCount = 0
                            } else {
                                zoomCount++
                                panCount = 0
                            }

                            if (panCount > PAN_VS_ZOOM_COUNT) {
                                isPanning = true
                                isZooming = false
                                panCount = 0
                                zoomCount = 0
                            } else if (zoomCount > PAN_VS_ZOOM_COUNT) {
                                isPanning = false
                                isZooming = true
                                panCount = 0
                                zoomCount = 0
                            }
                        }

                        lastPanDistance = panDistance
                        lastZoomDistance = zoomDistance

                        if (isPanning) {
                            val pan = event.calculatePan()
                            onCanvasPan(
                                pan,
                                PIXEL_SIZE_DP_CANVAS,
                                imagePixelSize,
                                zoomFactor,
                                imageOffset
                            )
                        } else if (isZooming) {
                            val zoom = event.calculateZoom()
                            val zoomDelta = abs(1F - zoom)
                            zoomFactor = if (zoom >= 1F) {
                                min(MAX_ZOOM_FACTOR, zoomFactor + zoomDelta)
                            } else {
                                max(MIN_ZOOM_FACTOR, zoomFactor - zoomDelta)
                            }
                            onZoomUpdate(zoomFactor)
                        }

                        event.changes.forEach { pointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
    ) {
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor)
        val twoThirdsPixelSize = pixelSizeInt * 2F / 3F

        val coordinateTextTemplate = "%s,%s"
        val maxVisibleCoordinateTextSize = textMeasurer.measure(
            text = String.format(
                coordinateTextTemplate,
                imagePixelSize.width,
                imagePixelSize.height
            )
        ).size
        val isShowCoordinates =
            isShowCoordinatesEnabled &&
                    maxVisibleCoordinateTextSize.width < twoThirdsPixelSize &&
                    maxVisibleCoordinateTextSize.height < twoThirdsPixelSize

        drawCanvas(
            pixelImage,
            pixelSizeInt,
            imagePixelSize,
            imageOffset,
            margin,
            isShowBorderEnabled = false,
            isShowGridEnabled,
            isShowCoordinates,
            backgroundColor,
            backGroundCheckersColors,
            textMeasurer,
        )
    }
}

@Composable
fun PixelCanvasSnapshot(
    modifier: Modifier = Modifier,
    pixelImage: PixelImageModel,
    isFitAvailableSpace: Boolean = false,
    backgroundColor: Color? = Color.White,
) {
    val textMeasurer = rememberTextMeasurer()
    val offsetSaver = createOffsetSaver()
    val intSizeSaver = createIntSizeSaver()
    val imageOffset = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset(0F, 0F)) }
    val margin = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset(0F, 0F)) }
    val lastCanvasSize =
        rememberSaveable(stateSaver = intSizeSaver) { mutableStateOf(IntSize(-1, -1)) }
    val zoomFactor = rememberSaveable { mutableFloatStateOf(NO_ZOOM_FACTOR) }

    val imagePixelSize = IntSize(pixelImage.getPixelWidth(), pixelImage.getPixelHeight())

    Canvas(
        modifier = modifier
            .onSizeChanged { size ->
                onCanvasSizeChanged(size, lastCanvasSize, imageOffset)
            }
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val pixelSizeDp = PIXEL_SIZE_DP_PREVIEW
                    onCanvasPan(
                        dragAmount,
                        pixelSizeDp,
                        imagePixelSize,
                        zoomFactor.floatValue,
                        imageOffset
                    )
                }
            },
    ) {
        zoomFactor.floatValue = if (isFitAvailableSpace) {
            val pixelSizeNoZoom = PIXEL_SIZE_DP_PREVIEW.dp.toPx()
            val fitWidthZoomFactor = (size.width / (imagePixelSize.width * pixelSizeNoZoom)).toInt()
            val fitHeightZoomFactor =
                (size.height / (imagePixelSize.height * pixelSizeNoZoom)).toInt()
            max(NO_ZOOM_FACTOR, min(fitWidthZoomFactor, fitHeightZoomFactor).toFloat())
        } else {
            NO_ZOOM_FACTOR
        }
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_PREVIEW, zoomFactor.floatValue)

        drawCanvas(
            pixelImage,
            pixelSizeInt,
            imagePixelSize,
            imageOffset,
            margin,
            isShowBorderEnabled = true,
            isShowGridEnabled = false,
            isShowCoordinates = false,
            backgroundColor,
            backGroundCheckersColors = null,
            textMeasurer,
        )
    }
}

private fun onCanvasSizeChanged(
    size: IntSize,
    lastCanvasSize: MutableState<IntSize>,
    imageOffset: MutableState<Offset>,
) {
    val lastWidth = lastCanvasSize.value.width
    val lastHeight = lastCanvasSize.value.height
    val currentWidth = size.width
    val currentHeight = size.height

    if ((lastWidth >= 0) && (currentWidth > lastWidth)) {
        val widthIncrease = currentWidth - lastWidth
        val imageOffsetX = imageOffset.value.x
        imageOffset.value = Offset(
            max(0F, imageOffsetX - widthIncrease),
            imageOffset.value.y,
        )
    }

    if ((lastHeight >= 0) && (currentHeight > lastHeight)) {
        val heightIncrease = currentHeight - lastHeight
        val imageOffsetY = imageOffset.value.y
        imageOffset.value = Offset(
            imageOffset.value.x,
            max(0F, imageOffsetY - heightIncrease),
        )
    }

    lastCanvasSize.value = IntSize(currentWidth, currentHeight)
}

private fun PointerInputScope.onCanvasPan(
    pan: Offset,
    pixelSizeDp: Int,
    imagePixelSize: IntSize,
    zoomFactor: Float,
    imageOffset: MutableState<Offset>,
) {
    val pixelSizeInt = getPixelSizeInt(pixelSizeDp, zoomFactor)
    val imageWidthInt = imagePixelSize.width * pixelSizeInt
    val imageHeightInt = imagePixelSize.height * pixelSizeInt

    val minImageOffset = Offset(
        x = 0F,
        y = 0F,
    )
    val maxImageOffset = Offset(
        x = max(imageWidthInt - size.width, 0).toFloat(),
        y = max(imageHeightInt - size.height, 0).toFloat(),
    )

    val imageOffsetX = imageOffset.value.x
    val imageOffsetY = imageOffset.value.y

    imageOffset.value = Offset(
        (imageOffsetX - pan.x).cap(minImageOffset.x, maxImageOffset.x),
        (imageOffsetY - pan.y).cap(minImageOffset.y, maxImageOffset.y),
    )
}

private fun PointerInputScope.onCanvasTap(
    tapOffset: Offset,
    pixelSizeDp: Int,
    imagePixelSize: IntSize,
    zoomFactor: Float,
    canvasSize: Size,
    imageOffset: Offset,
    margin: Offset,
    onPixelTap: (Int, Int) -> Unit,
) {
    if (tapOffset.x - margin.x !in 0F..canvasSize.width ||
        tapOffset.y - margin.y !in 0F..canvasSize.height
    ) {
        return
    }

    val pixelSizeInt = getPixelSizeInt(pixelSizeDp, zoomFactor)
    val xPixel = ((imageOffset.x + tapOffset.x - margin.x) / pixelSizeInt).toInt()
    val yPixel =
        imagePixelSize.height - 1 - ((imageOffset.y + tapOffset.y - margin.y) / pixelSizeInt).toInt()

    if (xPixel !in 0..<imagePixelSize.width) return
    if (yPixel !in 0..<imagePixelSize.height) return

    onPixelTap(xPixel, yPixel)
}

private fun Density.getPixelSizeInt(
    dpSize: Int,
    zoomFactor: Float,
) = (dpSize.dp.toPx() * zoomFactor).toInt()

private fun DrawScope.drawCanvas(
    pixelImage: PixelImageModel,
    pixelSizeInt: Int,
    imagePixelSize: IntSize,
    imageOffset: MutableState<Offset>,
    margin: MutableState<Offset>,
    isShowBorderEnabled: Boolean,
    isShowGridEnabled: Boolean,
    isShowCoordinates: Boolean,
    backgroundColor: Color?,
    backGroundCheckersColors: Pair<Color, Color>?,
    textMeasurer: TextMeasurer,
) {
    val canvasSize = Size(size.width, size.height)
    val imageSize = Size(
        width = (imagePixelSize.width * pixelSizeInt).toFloat(),
        height = (imagePixelSize.height * pixelSizeInt).toFloat(),
    )
    val halfPixelSize = pixelSizeInt / 2F
    val gridLineWidth = 1.dp.toPx()
    val palette = pixelImage.paletteModel.colors.map { color -> Color.fromColorLong(color) }
    val invertedPalette = palette.invertColors()

    adjustImageOffset(imageOffset, margin, canvasSize, imageSize)

    val imageOffsetX = imageOffset.value.x
    val imageOffsetY = imageOffset.value.y
    val marginX = margin.value.x
    val marginY = margin.value.y

    backgroundColor?.let { color ->
        drawRect(
            color,
            topLeft = Offset(marginX, marginY),
            size = imageSize,
        )
    }

    var y = marginY
    var yMatrixCoordinate =
        imagePixelSize.height - 1 - ((imageOffsetY + y - marginY) / pixelSizeInt).toInt()
    while (y < canvasSize.height && yMatrixCoordinate >= 0) {
        val pixelHeight = if (y - marginY > 0F || imageOffsetY == 0F) {
            pixelSizeInt
        } else {
            pixelSizeInt - imageOffsetY.toInt() % pixelSizeInt
        }

        var x = marginX
        var xMatrixCoordinate = ((imageOffsetX + x - marginX) / pixelSizeInt).toInt()
        while (x < canvasSize.width && xMatrixCoordinate < imagePixelSize.width) {
            val pixelWidth = if (x - marginX > 0F || imageOffsetX == 0F) {
                pixelSizeInt
            } else {
                pixelSizeInt - imageOffsetX.toInt() % pixelSizeInt
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
                backgroundColor,
                backGroundCheckersColors,
                isShowCoordinates,
                textMeasurer,
                coordinateTextPalette = invertedPalette,
                halfPixelSize,
            )

            if (isShowGridEnabled) {
                drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth, marginY)
            }

            x += pixelWidth
            xMatrixCoordinate++
        }

        if (isShowGridEnabled && xMatrixCoordinate == imagePixelSize.width) {
            drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth, marginY)
        }

        if (isShowGridEnabled) {
            drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth, marginX)
        }

        y += pixelHeight
        yMatrixCoordinate--
    }

    if (isShowGridEnabled && yMatrixCoordinate == -1) {
        drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth, marginX)
    }

    if (isShowBorderEnabled) {
        if (marginX >= 0) {
            drawVerticalGridLine(marginX, canvasSize, imageSize, gridLineWidth, marginY)
            drawVerticalGridLine(
                canvasSize.width - marginX,
                canvasSize,
                imageSize,
                gridLineWidth,
                marginY
            )
        }
        if (marginY >= 0) {
            drawHorizontalGridLine(marginY, canvasSize, imageSize, gridLineWidth, marginX)
            drawHorizontalGridLine(
                canvasSize.height - marginY,
                canvasSize,
                imageSize,
                gridLineWidth,
                marginX
            )
        }
    }
}

private fun adjustImageOffset(
    imageOffset: MutableState<Offset>,
    margin: MutableState<Offset>,
    canvasSize: Size,
    imageSize: Size,
) {
    var imageOffsetX = imageOffset.value.x
    var imageOffsetY = imageOffset.value.y
    val marginX: Float
    val marginY: Float

    if (canvasSize.width > imageSize.width) {
        imageOffsetX = 0F
        marginX = (canvasSize.width - imageSize.width) / 2
    } else {
        marginX = 0F

        if (imageOffsetX > 0) {
            val canvasWidth = canvasSize.width
            val imageWidth = imageSize.width

            if (imageWidth <= canvasWidth) {
                imageOffsetX = 0F
            } else {
                val xOffsetSurplus = canvasWidth + imageOffsetX - imageWidth
                if (xOffsetSurplus > 0) {
                    imageOffsetX -= xOffsetSurplus
                }
            }
        }
    }

    if (canvasSize.height > imageSize.height) {
        imageOffsetY = 0F
        marginY = (canvasSize.height - imageSize.height) / 2
    } else {
        marginY = 0F

        if (imageOffsetY > 0) {
            val canvasHeight = canvasSize.height
            val imageHeight = imageSize.height

            if (imageHeight <= canvasHeight) {
                imageOffsetY = 0F
            } else {
                val deltaYSurplus = canvasHeight + imageOffsetY - imageHeight
                if (deltaYSurplus > 0) {
                    imageOffsetY -= deltaYSurplus
                }
            }
        }
    }

    imageOffset.value = Offset(imageOffsetX, imageOffsetY)
    margin.value = Offset(marginX, marginY)
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
    backgroundColor: Color?,
    backGroundCheckersColors: Pair<Color, Color>?,
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
        backGroundCheckersColors,
    )

    pixelColor?.let { color ->
        val pixelRectSize = Size(width.toFloat(), height.toFloat())
        drawRect(
            color,
            topLeft = offset,
            size = Size(
                width = min(pixelRectSize.width, canvasSize.width - offset.x),
                height = min(pixelRectSize.height, canvasSize.height - offset.y),
            ),
        )
    }

    if (isShowCoordinates) {
        val textColor = getColor(
            pixel,
            coordinateTextPalette,
            isXMatrixCoordinateEven,
            isYMatrixCoordinateEven,
            backGroundCheckersColors?.let { checkerBackgroundColors ->
                Pair(
                    checkerBackgroundColors.second,
                    checkerBackgroundColors.first,
                )
            },
        ) ?: backgroundColor?.invert()

        textColor?.let { color ->
            val coordinateText = COORDINATE_TEXT_FORMAT.format(
                xMatrixCoordinate + 1, yMatrixCoordinate + 1
            )
            val textSize = textMeasurer.measure(
                text = coordinateText
            ).size

            val coordinateTextOffset = offset + Offset(
                x = width - halfPixelSize - textSize.width / 2,
                y = height - halfPixelSize - textSize.height / 2,
            )

            drawText(
                textMeasurer,
                topLeft = coordinateTextOffset,
                text = coordinateText,
                style = TextStyle.Default.copy(
                    color = color,
                ),
                softWrap = false,
            )
        }
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
            min(canvasSize.height - margin, imageSize.height + margin),
        )
    } else {
        start = Offset(
            margin,
            dimension,
        )
        end = Offset(
            min(canvasSize.width - margin, imageSize.width + margin),
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
    backGroundCheckersColors: Pair<Color, Color>?,
): Color? {
    return palette.getColor(pixel)
        ?: backGroundCheckersColors?.let { (first, second) ->
            if (
                (isXMatrixCoordinateEven && !isYMatrixCoordinateEven) ||
                (!isXMatrixCoordinateEven && isYMatrixCoordinateEven)
            ) {
                first
            } else {
                second
            }
        }
}

private fun createOffsetSaver() = Saver<Offset, SizeF>(
    save = { offset -> SizeF(offset.x, offset.y) },
    restore = { sizeF -> Offset(sizeF.width, sizeF.height) },
)

private fun createIntSizeSaver() = Saver<IntSize, android.util.Size>(
    save = { intSize -> android.util.Size(intSize.width, intSize.height) },
    restore = { size -> IntSize(size.width, size.height) },
)

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
    for (y in 0..<height) {
        val row = mutableListOf<PixelModel>()
        for (x in 0..<width) {
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