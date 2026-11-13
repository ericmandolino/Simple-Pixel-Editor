package com.swirlfist.simplepixel.presentation.uielements

import android.util.SizeF
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.domain.getPixelAt
import com.swirlfist.simplepixel.domain.getPixelHeight
import com.swirlfist.simplepixel.domain.getPixelWidth
import com.swirlfist.simplepixel.domain.isSelected
import com.swirlfist.simplepixel.domain.model.FloatingPixelImageModel
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.domain.model.PixelSelectionModel
import com.swirlfist.simplepixel.domain.model.RectangleSelectionModel
import com.swirlfist.simplepixel.domain.selectNonEmpty
import com.swirlfist.simplepixel.presentation.getColor
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

private const val NO_ZOOM_FACTOR = 1F
private const val PIXEL_SIZE_DP_CANVAS = 32
private const val PIXEL_SIZE_DP_PREVIEW = 1
private const val FLOATING_STROKE_WIDTH_DP = 3
private const val FLOATING_STROKE_DASH_INTERVAL = 20F
private const val SELECTION_STROKE_WIDTH_DP = 3
private const val SELECTION_STROKE_DASH_INTERVAL = 10F
private const val SELECTION_RESIZE_SQUARE_SIZE_DP = 16
private const val SELECTION_RESIZE_SQUARE_BORDER_WIDTH_DP = 1
private const val PAN_VS_ZOOM_FACTOR = 0.025F
private const val ZOOM_STEP = 0.025F
const val MAX_ZOOM_FACTOR = 2F
const val MIN_ZOOM_FACTOR = 0.25F

@Composable
fun PixelCanvas(
    modifier: Modifier,
    pixelImage: PixelImageModel,
    pixelSelection: PixelSelectionModel? = null,
    floatingPixelImage: FloatingPixelImageModel? = null,
    initialZoomFactor: Float = NO_ZOOM_FACTOR,
    isShowGridEnabled: Boolean = true,
    backgroundColor: Color? = null,
    backGroundCheckersColors: Pair<Color, Color>? = Pair(Color.Gray, Color.LightGray),
    onPixelVisited: (xPixel: Int, yPixel: Int) -> Unit,
    onPixelVisitCancel: () -> Unit,
    onPixelVisitFinish: () -> Unit,
    onZoomUpdate: (Float) -> Unit,
) {
    val offsetSaver = createOffsetSaver()
    val intSizeSaver = createIntSizeSaver()
    val imageOffset = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset.Zero) }
    val margin = rememberSaveable(stateSaver = offsetSaver) { mutableStateOf(Offset.Zero) }
    val lastCanvasSize =
        rememberSaveable(stateSaver = intSizeSaver) { mutableStateOf(IntSize(-1, -1)) }
    var zoomFactor by rememberSaveable { mutableFloatStateOf(initialZoomFactor) }
    var zoomAcc by rememberSaveable { mutableFloatStateOf(0F) }
    var lastPixelVisited by rememberSaveable { mutableStateOf<Pair<Int, Int>?>(null) }

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
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.size != 1) {
                            if (lastPixelVisited != null) {
                                lastPixelVisited = null
                                onPixelVisitCancel()
                            }

                            do {
                                val eventToConsume = awaitPointerEvent()
                            } while (eventToConsume.changes.any { it.pressed })

                            break
                        }

                        val pixelVisited = getPixelCoordinatesAt(
                            offset = event.changes[0].position,
                            pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor),
                            imagePixelSize,
                            canvasSize = Size(
                                lastCanvasSize.value.width.toFloat(),
                                lastCanvasSize.value.height.toFloat()
                            ),
                            imageOffset.value,
                            margin.value,
                        )

                        if (pixelVisited != null && pixelVisited != lastPixelVisited) {
                            lastPixelVisited = pixelVisited
                            onPixelVisited(
                                pixelVisited.first,
                                pixelVisited.second,
                            )
                        }

                        event.changes.forEach { pointerInputChange ->
                            pointerInputChange.consume()
                        }

                    } while (event.changes.any { it.pressed })

                    lastPixelVisited = null
                    onPixelVisitFinish()
                }
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        if (event.changes.size != 2) {
                            zoomAcc = 0F
                            continue
                        }

                        val pan = event.calculatePan()

                        onCanvasPan(
                            pan,
                            PIXEL_SIZE_DP_CANVAS,
                            imagePixelSize,
                            zoomFactor,
                            imageOffset,
                        )

                        val zoomDistance = (event.changes[0].position - event.changes[1].position).getDistance()
                        if (pan.getDistance() < zoomDistance * PAN_VS_ZOOM_FACTOR) {
                            val zoom = event.calculateZoom()
                            zoomAcc += zoom - 1F

                            if (abs(zoomAcc) > ZOOM_STEP) {
                                if (zoomAcc > 0) {
                                    zoomAcc -= ZOOM_STEP
                                    zoomFactor = min(MAX_ZOOM_FACTOR, zoomFactor + ZOOM_STEP)
                                } else {
                                    zoomAcc += ZOOM_STEP
                                    zoomFactor = max(MIN_ZOOM_FACTOR, zoomFactor - ZOOM_STEP)
                                }

                                if (zoomFactor > MIN_ZOOM_FACTOR && zoomFactor < MAX_ZOOM_FACTOR) {
                                    val centroid = event.calculateCentroid()
                                    val centroidFactor = if (zoomAcc > 0) -ZOOM_STEP else ZOOM_STEP

                                    val zoomPan = Offset(
                                        x = centroid.x * centroidFactor,
                                        y = centroid.y * centroidFactor,
                                    )
                                    onCanvasPan(
                                        pan = zoomPan,
                                        pixelSizeDp = PIXEL_SIZE_DP_CANVAS,
                                        imagePixelSize,
                                        zoomFactor,
                                        imageOffset,
                                    )
                                }

                                onZoomUpdate(zoomFactor)
                            }
                        }

                        event.changes.forEach { pointerInputChange ->
                            pointerInputChange.consume()
                        }
                    } while (event.changes.any { it.pressed })
                }
            },
    ) {
        val pixelSizeInt = getPixelSizeInt(PIXEL_SIZE_DP_CANVAS, zoomFactor)

        drawCanvas(
            pixelImage,
            pixelSelection,
            floatingPixelImage,
            pixelSizeInt,
            imagePixelSize,
            imageOffset,
            margin,
            isShowBorderEnabled = false,
            isShowGridEnabled,
            backgroundColor,
            backGroundCheckersColors,
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
            pixelSelection = null,
            floatingPixelImage = null,
            pixelSizeInt,
            imagePixelSize,
            imageOffset,
            margin,
            isShowBorderEnabled = true,
            isShowGridEnabled = false,
            backgroundColor,
            backGroundCheckersColors = null,
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
    onCanvasPan(
        pan,
        pixelSizeInt = getPixelSizeInt(pixelSizeDp, zoomFactor),
        size,
        imagePixelSize,
        imageOffset,
    )
}

private fun onCanvasPan(
    pan: Offset,
    pixelSizeInt: Int,
    canvasSize: IntSize,
    imagePixelSize: IntSize,
    imageOffset: MutableState<Offset>,
) {
    val imageWidthInt = imagePixelSize.width * pixelSizeInt
    val imageHeightInt = imagePixelSize.height * pixelSizeInt

    val minImageOffset = Offset(
        x = 0F,
        y = 0F,
    )
    val maxImageOffset = Offset(
        x = max(imageWidthInt - canvasSize.width, 0).toFloat(),
        y = max(imageHeightInt - canvasSize.height, 0).toFloat(),
    )

    val imageOffsetX = imageOffset.value.x
    val imageOffsetY = imageOffset.value.y

    imageOffset.value = Offset(
        (imageOffsetX - pan.x).cap(minImageOffset.x, maxImageOffset.x),
        (imageOffsetY - pan.y).cap(minImageOffset.y, maxImageOffset.y),
    )
}

private fun getPixelCoordinatesAt(
    offset: Offset,
    pixelSizeInt: Int,
    imagePixelSize: IntSize,
    canvasSize: Size,
    imageOffset: Offset,
    margin: Offset,
): Pair<Int, Int>? {
    if (offset.x - margin.x !in 0F..canvasSize.width ||
        offset.y - margin.y !in 0F..canvasSize.height
    ) {
        return null
    }

    val xPixel = ((imageOffset.x + offset.x - margin.x) / pixelSizeInt).toInt()
    val yPixel =
        imagePixelSize.height - 1 - ((imageOffset.y + offset.y - margin.y) / pixelSizeInt).toInt()

    return if (xPixel !in 0..<imagePixelSize.width || yPixel !in 0..<imagePixelSize.height) {
        null
    } else {
        Pair(xPixel, yPixel)
    }
}

private fun Density.getPixelSizeInt(
    dpSize: Int,
    zoomFactor: Float,
) = (dpSize.dp.toPx() * zoomFactor).toInt()

private fun DrawScope.drawCanvas(
    pixelImage: PixelImageModel,
    pixelSelection: PixelSelectionModel?,
    floatingPixelImage: FloatingPixelImageModel?,
    pixelSizeInt: Int,
    imagePixelSize: IntSize,
    imageOffset: MutableState<Offset>,
    margin: MutableState<Offset>,
    isShowBorderEnabled: Boolean,
    isShowGridEnabled: Boolean,
    backgroundColor: Color?,
    backGroundCheckersColors: Pair<Color, Color>?,
) {
    val canvasSize = size
    val imageSize = Size(
        width = (imagePixelSize.width * pixelSizeInt).toFloat(),
        height = (imagePixelSize.height * pixelSizeInt).toFloat(),
    )
    val gridLineWidth = 1.dp.toPx()
    val palette = pixelImage.paletteModel.colors.map { color -> Color.fromColorLong(color) }

    adjustImageOffset(imageOffset, margin, canvasSize, imageSize)

    backgroundColor?.let { color ->
        drawRect(
            color,
            topLeft = margin.value,
            size = imageSize,
        )
    }

    val imageLines = drawImageInCanvas(
        pixelImage,
        pixelSelection,
        pixelSizeInt,
        imagePixelSize,
        imageOffset.value,
        margin.value,
        palette,
        backGroundCheckersColors,
    )

    val floatingImageLines = drawFloatingImage(
        floatingPixelImage,
        pixelSizeInt,
        imagePixelSize,
        imageOffset.value,
        margin.value,
    )

    if (isShowGridEnabled) {
        imageLines.horizontalGridLineYCoordinates.forEach { y ->
            drawHorizontalGridLine(y, canvasSize, imageSize, gridLineWidth, margin.value.x)
        }
        imageLines.verticalGridLineXCoordinates.forEach { x ->
            drawVerticalGridLine(x, canvasSize, imageSize, gridLineWidth, margin.value.y)
        }
    }

    if (isShowBorderEnabled) {
        drawBorders(imageSize, canvasSize, margin.value, gridLineWidth)
    }

    drawSelectionLines(
        imageLines.selectionLines,
        isFloatingImage = false,
    )
    drawRectangleSelection(
        rectangleSelection = pixelSelection?.rectangleSelection,
        pixelSizeInt,
        canvasSize,
        imageOffset.value,
        margin.value,
    )
    drawSelectionLines(
        floatingImageLines.selectionLines,
        isFloatingImage = true,
    )
}

private fun DrawScope.drawImageInCanvas(
    pixelImage: PixelImageModel,
    pixelSelection: PixelSelectionModel?,
    pixelSizeInt: Int,
    imagePixelSize: IntSize,
    imageOffset: Offset,
    margin: Offset,
    palette: List<Color>,
    backGroundCheckersColors: Pair<Color, Color>?,
    floatingOffset: Pair<Int, Int> = Pair(0, 0),
): ImageLines {
    val canvasSize = size
    val horizontalGridLineYCoordinates = mutableListOf<Float>()
    val verticalGridLineXCoordinates = mutableListOf<Float>()
    val selectionLines = mutableListOf<Pair<Offset, Offset>>()

    var y = margin.y
    var yMatrixCoordinate =
        imagePixelSize.height - 1 - ((imageOffset.y + y - margin.y) / pixelSizeInt).toInt()
    while (y < canvasSize.height && yMatrixCoordinate >= 0) {
        val pixelHeight = if (y - margin.y > 0F || imageOffset.y == 0F) {
            pixelSizeInt
        } else {
            pixelSizeInt - imageOffset.y.toInt() % pixelSizeInt
        }

        val adjustedYMatrixCoordinate = yMatrixCoordinate - floatingOffset.second
        if (adjustedYMatrixCoordinate in 0..< pixelImage.getPixelHeight()) {

            var x = margin.x
            var xMatrixCoordinate = ((imageOffset.x + x - margin.x) / pixelSizeInt).toInt()
            while (x < canvasSize.width && xMatrixCoordinate < imagePixelSize.width) {
                val pixelWidth = if (x - margin.x > 0F || imageOffset.x == 0F) {
                    pixelSizeInt
                } else {
                    pixelSizeInt - imageOffset.x.toInt() % pixelSizeInt
                }

                val adjustedXMatrixCoordinate = xMatrixCoordinate - floatingOffset.first
                if (adjustedXMatrixCoordinate in 0..<pixelImage.getPixelWidth()) {
                    val pixel = pixelImage.getPixelAt(
                        x = adjustedXMatrixCoordinate,
                        y = adjustedYMatrixCoordinate,
                    )

                    val pixelOffset = Offset(x, y)
                    val pixelRectSize = Size(pixelWidth.toFloat(), pixelHeight.toFloat())
                    val visiblePixelWidth =
                        min(pixelRectSize.width, canvasSize.width - pixelOffset.x)
                    val visiblePixelHeight =
                        min(pixelRectSize.height, canvasSize.height - pixelOffset.y)

                    drawPixel(
                        pixel,
                        palette = palette,
                        visibleWidth = visiblePixelWidth,
                        visibleHeight = visiblePixelHeight,
                        xMatrixCoordinate,
                        yMatrixCoordinate,
                        offset = pixelOffset,
                        backGroundCheckersColors,
                    )

                    addSelectionLines(
                        pixelSelection,
                        selectionLines,
                        pixelSizeInt,
                        visiblePixelWidth,
                        visiblePixelHeight,
                        adjustedXMatrixCoordinate,
                        adjustedYMatrixCoordinate,
                        pixelOffset,
                        canvasSize,
                    )

                    verticalGridLineXCoordinates.add(x)
                }

                x += pixelWidth
                xMatrixCoordinate++
            }

            if (xMatrixCoordinate == imagePixelSize.width && x <= canvasSize.width) {
                verticalGridLineXCoordinates.add(x)
            }

            horizontalGridLineYCoordinates.add(y)
        }

        y += pixelHeight
        yMatrixCoordinate--
    }

    if (yMatrixCoordinate == -1 && y <= canvasSize.height) {
        horizontalGridLineYCoordinates.add(y)
    }

    return ImageLines(
        verticalGridLineXCoordinates.toList(),
        horizontalGridLineYCoordinates.toList(),
        selectionLines.toList(),
    )
}

private fun DrawScope.drawBorders(
    imageSize: Size,
    canvasSize: Size,
    margin: Offset,
    gridLineWidth: Float,
) {
    if (imageSize.width <= canvasSize.width) {
        drawVerticalGridLine(
            margin.x,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.y,
        )
        drawVerticalGridLine(
            canvasSize.width - margin.x,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.y,
        )
    }
    if (imageSize.height <= canvasSize.height) {
        drawHorizontalGridLine(
            margin.y,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.x,
        )
        drawHorizontalGridLine(
            canvasSize.height - margin.y,
            canvasSize,
            imageSize,
            gridLineWidth,
            margin.x,
        )
    }
}

private fun DrawScope.drawSelectionLines(
    selectionLines: List<Pair<Offset, Offset>>,
    isFloatingImage: Boolean,
) {
    if (selectionLines.isEmpty()) {
        return
    }

    val strokeWidth = if (isFloatingImage) {
        FLOATING_STROKE_WIDTH_DP.dp.toPx()
    } else {
        SELECTION_STROKE_WIDTH_DP.dp.toPx()
    }

    val interval = if (isFloatingImage) {
        FLOATING_STROKE_DASH_INTERVAL
    } else {
        SELECTION_STROKE_DASH_INTERVAL
    }

    selectionLines.forEach { (start, end) ->
        drawSelectionLine(start, end, strokeWidth, interval)
    }
}

private fun DrawScope.drawSelectionLine(
    start: Offset,
    end: Offset,
    strokeWidth: Float,
    interval: Float,
    color1: Color = Color.Black,
    color2: Color = Color.White,
) {
    drawLine(
        color = color1,
        strokeWidth = strokeWidth,
        start = start,
        end = end,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(interval, interval),
            phase = 0F
        )
    )
    drawLine(
        color = color2,
        strokeWidth = strokeWidth,
        start = start,
        end = end,
        pathEffect = PathEffect.dashPathEffect(
            intervals = floatArrayOf(interval, interval),
            phase = 3 * interval
        )
    )
}

private fun DrawScope.drawRectangleSelection(
    rectangleSelection: RectangleSelectionModel?,
    pixelSizeInt: Int,
    canvasSize: Size,
    imageOffset: Offset,
    margin: Offset,
) {
    if (rectangleSelection == null) {
        return
    }

    val strokeWidth = SELECTION_STROKE_WIDTH_DP.dp.toPx()
    val resizeSquareSize = SELECTION_RESIZE_SQUARE_SIZE_DP.dp.toPx()
    val resizeSquareBorderWidth = SELECTION_RESIZE_SQUARE_BORDER_WIDTH_DP.dp.toPx()

    val rectStart = rectangleSelection.start
    val rectEnd = rectangleSelection.end
    val selectionRect = Rect(
        topLeft = Offset(
            x = min(rectStart.first, rectEnd.first).toFloat(),
            y = min(rectStart.second, rectEnd.second).toFloat(),
        ),
        bottomRight = Offset(
            x = max(rectStart.first, rectEnd.first).toFloat(),
            y = max(rectStart.second, rectEnd.second).toFloat(),
        )
    )
    val xSelectionOffset = selectionRect.topLeft.x * pixelSizeInt
    val ySelectionOffset = selectionRect.topLeft.y * pixelSizeInt
    val rectMinX = xSelectionOffset - imageOffset.x + margin.x
    val rectMinY = ySelectionOffset - imageOffset.y + margin.y
    val rectMaxX = rectMinX + selectionRect.width * pixelSizeInt
    val rectMaxY = rectMinY + selectionRect.height * pixelSizeInt

    val minXVisible = rectMinX >= 0 && rectMinX < canvasSize.width
    val maxXVisible = rectMaxX >= 0 && rectMaxX < canvasSize.width
    val minYVisible = rectMinY >= 0 && rectMinY < canvasSize.height
    val maxYVisible = rectMaxY >= 0 && rectMaxY < canvasSize.height

    val rectangleSelectionContext = RectangleSelectionContext(
        rectMinX,
        rectMaxX,
        rectMinY,
        rectMaxY,
        minXVisible,
        maxXVisible,
        minYVisible,
        maxYVisible,
        canvasSize,
    )

    drawRectangleSelectionLines(
        rectangleSelectionContext,
        strokeWidth,
    )

    drawRectangleSelectionCornerSquares(
        rectangleSelectionContext,
        resizeSquareSize,
        resizeSquareBorderWidth,
    )

    drawRectangleSelectionMiddleSquares(
        rectangleSelectionContext,
        resizeSquareSize,
        resizeSquareBorderWidth
    )
}

private fun DrawScope.drawRectangleSelectionLines(
    context: RectangleSelectionContext,
    strokeWidth: Float,
) {
    if (context.minYVisible) {
        drawRectangleSelectionHorizontalLine(
            startX = max(0F, context.rectMinX),
            endX = min(context.canvasSize.width, context.rectMaxX),
            y = context.rectMinY,
            strokeWidth,
        )
    }

    if (context.maxYVisible) {
        drawRectangleSelectionHorizontalLine(
            startX = max(0F, context.rectMinX),
            endX = min(context.canvasSize.width, context.rectMaxX),
            y = context.rectMaxY,
            strokeWidth,
        )
    }

    if (context.minXVisible) {
        drawRectangleSelectionVerticalLine(
            startY = max(0F, context.rectMinY),
            endY = min(context.canvasSize.height, context.rectMaxY),
            x = context.rectMinX,
            strokeWidth,
        )
    }

    if (context.maxXVisible) {
        drawRectangleSelectionVerticalLine(
            startY = max(0F, context.rectMinY),
            endY = min(context.canvasSize.height, context.rectMaxY),
            x = context.rectMaxX,
            strokeWidth,
        )
    }
}

private fun DrawScope.drawRectangleSelectionCornerSquares(
    context: RectangleSelectionContext,
    resizeSquareSize: Float,
    resizeSquareBorderWidth: Float,
) {
    val lowerLeftCornerVisible = context.minXVisible && context.maxYVisible
    val upperLeftCornerVisible = context.minXVisible && context.minYVisible
    val lowerRightCornerVisible = context.maxXVisible && context.maxYVisible
    val upperRightCornerVisible = context.maxXVisible && context.minYVisible

    if (lowerLeftCornerVisible) {
        drawRectangleSelectionResizeSquare(
            x = context.rectMinX,
            y = context.rectMaxY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (upperLeftCornerVisible) {
        drawRectangleSelectionResizeSquare(
            x = context.rectMinX,
            y = context.rectMinY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (lowerRightCornerVisible) {
        drawRectangleSelectionResizeSquare(
            x = context.rectMaxX,
            y = context.rectMaxY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (upperRightCornerVisible) {
        drawRectangleSelectionResizeSquare(
            x = context.rectMaxX,
            y = context.rectMinY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }
}

private fun DrawScope.drawRectangleSelectionMiddleSquares(
    context: RectangleSelectionContext,
    resizeSquareSize: Float,
    resizeSquareBorderWidth: Float,
) {
    val rectMiddleX = context.rectMinX + (context.rectMaxX - context.rectMinX) / 2
    val rectMiddleY = context.rectMinY + (context.rectMaxY - context.rectMinY) / 2
    val middleXVisible = rectMiddleX >= 0 && rectMiddleX < context.canvasSize.width
    val middleYVisible = rectMiddleY >= 0 && rectMiddleY < context.canvasSize.height
    val lowerMiddleVisible = middleXVisible && context.maxYVisible
    val upperMiddleVisible = middleXVisible && context.minYVisible
    val leftMiddleVisible = context.minXVisible && middleYVisible
    val rightMiddleVisible = context.maxXVisible && middleYVisible

    if (lowerMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            x = rectMiddleX,
            y = context.rectMaxY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (upperMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            x = rectMiddleX,
            y = context.rectMinY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (leftMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            x = context.rectMinX,
            y = rectMiddleY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }

    if (rightMiddleVisible) {
        drawRectangleSelectionResizeSquare(
            x = context.rectMaxX,
            y = rectMiddleY,
            resizeSquareSize,
            resizeSquareBorderWidth,
        )
    }
}

private fun DrawScope.drawRectangleSelectionHorizontalLine(
    startX: Float,
    endX: Float,
    y: Float,
    strokeWidth: Float,
) {
    val start = Offset(
        x = startX,
        y,
    )
    val end = Offset(
        x = endX,
        y,
    )
    drawSelectionLine(start, end, strokeWidth, SELECTION_STROKE_DASH_INTERVAL)
}

private fun DrawScope.drawRectangleSelectionVerticalLine(
    startY: Float,
    endY: Float,
    x: Float,
    strokeWidth: Float,
) {
    val start = Offset(
        x,
        y = startY,
    )
    val end = Offset(
        x,
        y = endY,
    )
    drawSelectionLine(start, end, strokeWidth, SELECTION_STROKE_DASH_INTERVAL)
}

private fun DrawScope.drawRectangleSelectionResizeSquare(
    x: Float,
    y: Float,
    size: Float,
    borderWidth: Float,
    color: Color = Color.White,
    borderColor: Color = Color.Black,
) {
    val halfSize = size / 2
    drawRect(
        borderColor,
        topLeft = Offset(x - halfSize, y - halfSize),
        size = Size(size, size),
    )

    val borderlessSize = size - 2 * borderWidth
    val halfBorderlessSize = borderlessSize / 2

    drawRect(
        color,
        topLeft = Offset(x - halfBorderlessSize, y - halfBorderlessSize),
        size = Size(borderlessSize, borderlessSize),
    )
}

private fun DrawScope.drawFloatingImage(
    floatingPixelImage: FloatingPixelImageModel?,
    pixelSizeInt: Int,
    imagePixelSize: IntSize,
    imageOffset: Offset,
    margin: Offset,
): ImageLines {
    if (floatingPixelImage == null) {
        return ImageLines()
    }

    val imageLines = drawImageInCanvas(
        pixelImage = floatingPixelImage.pixelImageModel,
        pixelSelection = floatingPixelImage.pixelImageModel.selectNonEmpty(),
        pixelSizeInt,
        imagePixelSize,
        imageOffset,
        margin,
        palette = floatingPixelImage.pixelImageModel.paletteModel.colors.map { color -> Color.fromColorLong(color) },
        backGroundCheckersColors = null,
        floatingOffset = floatingPixelImage.offset,
    )

    return imageLines
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
    visibleWidth: Float,
    visibleHeight: Float,
    xMatrixCoordinate: Int,
    yMatrixCoordinate: Int,
    offset: Offset,
    backGroundCheckersColors: Pair<Color, Color>?,
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
        drawRect(
            color,
            topLeft = offset,
            size = Size(visibleWidth, visibleHeight),
        )
    }
}

private fun addSelectionLines(
    pixelSelection: PixelSelectionModel?,
    selectionLines: MutableList<Pair<Offset, Offset>>,
    pixelSizeInt: Int,
    visiblePixelWidth: Float,
    visiblePixelHeight: Float,
    xMatrixCoordinate: Int,
    yMatrixCoordinate: Int,
    offset: Offset,
    canvasSize: Size,
) {
    val isSelected = pixelSelection?.isSelected(
        x = xMatrixCoordinate,
        y = yMatrixCoordinate
    ) ?: false

    if (!isSelected) {
        return
    }

    val fullWidth = visiblePixelWidth == pixelSizeInt.toFloat()
    val fullHeight = visiblePixelHeight == pixelSizeInt.toFloat()
    val widthOffset = offset.x + visiblePixelWidth
    val heightOffset = offset.y + visiblePixelHeight

    val isSelectedAbove = pixelSelection.isSelected(
        x = xMatrixCoordinate,
        y = yMatrixCoordinate + 1,
    )
    val isSelectedBelow = pixelSelection.isSelected(
        x = xMatrixCoordinate,
        y = yMatrixCoordinate - 1,
    )
    val isSelectedLeft = pixelSelection.isSelected(
        x = xMatrixCoordinate - 1,
        y = yMatrixCoordinate,
    )
    val isSelectedRight = pixelSelection.isSelected(
        x = xMatrixCoordinate + 1,
        y = yMatrixCoordinate,
    )

    if (!isSelectedAbove && (fullHeight || heightOffset > pixelSizeInt)) {
        selectionLines.add(Pair(offset, offset.copy(x = widthOffset)))
    }

    if (!isSelectedBelow && (fullHeight || heightOffset < canvasSize.height)) {
        selectionLines.add(Pair(offset.copy(y = heightOffset), offset.copy(y = heightOffset, x = widthOffset)))
    }

    if (!isSelectedLeft && (fullWidth || widthOffset > pixelSizeInt)) {
        selectionLines.add(Pair(offset, offset.copy(y = heightOffset)))
    }

    if (!isSelectedRight && (fullWidth || widthOffset < canvasSize.width)) {
        selectionLines.add(Pair(offset.copy(x = widthOffset), offset.copy(x = widthOffset, y = heightOffset)))
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

private data class ImageLines(
    val verticalGridLineXCoordinates: List<Float> = listOf(),
    val horizontalGridLineYCoordinates: List<Float> = listOf(),
    val selectionLines: List<Pair<Offset, Offset>> = listOf(),
)

private data class RectangleSelectionContext(
    val rectMinX: Float,
    val rectMaxX: Float,
    val rectMinY: Float,
    val rectMaxY: Float,
    val minXVisible: Boolean,
    val maxXVisible: Boolean,
    val minYVisible: Boolean,
    val maxYVisible: Boolean,
    val canvasSize: Size,
)