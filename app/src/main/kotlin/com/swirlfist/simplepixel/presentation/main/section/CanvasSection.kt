package com.swirlfist.simplepixel.presentation.main.section

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
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
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import kotlin.math.max
import kotlin.math.min

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
    isShowCoordinatesEnabled: Boolean = false,
    onPixelTap: (xPixel: Int, yPixel: Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val invertedPalette = remember {  pixelImage.paletteModel.invert() }
    val imagePixelWidth = remember { pixelImage.getPixelWidth() }
    val imagePixelHeight = remember { pixelImage.getPixelHeight() }
    val imageDeltaX = rememberSaveable { mutableFloatStateOf(0F) }
    val imageDeltaY = rememberSaveable { mutableFloatStateOf(0F) }
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
                    imageDeltaX.floatValue = max(0F, imageDeltaX.floatValue - widthIncrease)
                }

                if ((lastHeight >= 0) && (currentHeight > lastHeight)) {
                    val heightIncrease = currentHeight - lastHeight
                    imageDeltaY.floatValue = max(0F, imageDeltaY.floatValue - heightIncrease)
                }

                lastCanvasWidth.intValue = currentWidth
                lastCanvasHeight.intValue = currentHeight
            }
            .pointerInput(zoomFactor) {
                detectDragGestures { _, dragAmount ->
                    val pixelSize = 32.dp.toPx() * zoomFactor
                    val imageWidth = imagePixelWidth * pixelSize
                    val imageHeight = imagePixelHeight * pixelSize
                    val minImageDeltaX = 0F
                    val minImageDeltaY = 0F
                    val maxImageDeltaX = max(imageWidth - size.width, 0F)
                    val maxImageDeltaY = max(imageHeight - size.height, 0F)

                    imageDeltaX.floatValue = (imageDeltaX.floatValue - dragAmount.x)
                        .cap(minImageDeltaX, maxImageDeltaX)
                    imageDeltaY.floatValue = (imageDeltaY.floatValue - dragAmount.y)
                        .cap(minImageDeltaY, maxImageDeltaY)
                }
            }
            .pointerInput(zoomFactor) {
                detectTapGestures { tapOffset ->
                    val pixelSize = 32.dp.toPx() * zoomFactor
                    val xPixel = ((imageDeltaX.floatValue + tapOffset.x) / pixelSize).toInt()
                    val yPixel =  imagePixelHeight - 1 - ((imageDeltaY.floatValue + tapOffset.y) / pixelSize).toInt()

                    if (xPixel !in 0 ..< imagePixelWidth) return@detectTapGestures
                    if (yPixel !in 0 ..< imagePixelHeight) return@detectTapGestures

                    onPixelTap(xPixel, yPixel)
                }
            },
    ) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val pixelSize = (32.dp.toPx() * zoomFactor).toInt()
        val imageWidth = imagePixelWidth * pixelSize
        val imageHeight = imagePixelHeight * pixelSize
        val halfPixelSize = pixelSize / 2
        val twoThirdsPixelSize = pixelSize * 2 / 3

        val coordinateTextTemplate = "%s,%s"
        val maxVisibleCoordinateTextSize = textMeasurer.measure(
            text = String.format(coordinateTextTemplate, imagePixelWidth, imagePixelHeight)
        ).size
        val isShowCoordinates =
            isShowCoordinatesEnabled &&
            maxVisibleCoordinateTextSize.width < twoThirdsPixelSize &&
            maxVisibleCoordinateTextSize.height < twoThirdsPixelSize

        if (imageDeltaX.floatValue > 0) {
            if (imageWidth <= canvasWidth) {
                imageDeltaX.floatValue = 0F
            } else {
                val deltaXSurplus = canvasWidth + imageDeltaX.floatValue - imageWidth
                if (deltaXSurplus > 0) {
                    imageDeltaX.floatValue -= deltaXSurplus
                }
            }
        }

        if (imageDeltaY.floatValue > 0) {
            if (imageHeight <= canvasHeight) {
                imageDeltaY.floatValue = 0F
            } else {
                val deltaYSurplus = canvasHeight + imageDeltaY.floatValue - imageHeight
                if (deltaYSurplus > 0) {
                    imageDeltaY.floatValue -= deltaYSurplus
                }
            }
        }

        var y = 0F
        while (y < canvasHeight) {
            val yPixelMatrix = imagePixelHeight - 1 - ((imageDeltaY.floatValue + y) / pixelSize).toInt()
            if (yPixelMatrix < 0) break

            val pixelRectHeight = if (y > 0F || imageDeltaY.floatValue == 0F) {
                pixelSize
            } else {
                pixelSize - imageDeltaY.floatValue.toInt() % pixelSize
            }

            var x = 0F
            while (x < canvasWidth) {
                val xPixelMatrix = ((imageDeltaX.floatValue + x) / pixelSize).toInt()
                if (xPixelMatrix > imagePixelWidth - 1) break

                val pixelRectWidth = if (x > 0F || imageDeltaX.floatValue == 0F) {
                    pixelSize
                } else {
                    pixelSize - imageDeltaX.floatValue.toInt() % pixelSize
                }

                val pixel = pixelImage.getPixelAt(
                    x = xPixelMatrix,
                    y = yPixelMatrix
                )
                val pixelColor = pixelImage.getColor(pixel)

                val pixelRectOffset = Offset(x, y)
                val pixelRectSize = Size(pixelRectWidth.toFloat(), pixelRectHeight.toFloat())

                drawRect(
                    color = pixelColor,
                    topLeft = pixelRectOffset,
                    size = Size(
                        width = min(pixelRectSize.width, canvasWidth - pixelRectOffset.x),
                        height = min(pixelRectSize.height, canvasHeight - pixelRectOffset.y),
                    ),
                )

                if (isShowCoordinates) {
                    val coordinateText = "$xPixelMatrix,$yPixelMatrix"
                    val textSize = textMeasurer.measure(
                        text = coordinateText
                    ).size

                    val textColor = invertedPalette.getColor(pixel)
                    val coordinateTextOffset = pixelRectOffset + Offset(
                        x = pixelRectWidth - halfPixelSize.toFloat() - textSize.width / 2,
                        y = pixelRectHeight - halfPixelSize.toFloat() - textSize.height / 2,
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
                x += pixelRectWidth
            }
            y += pixelRectHeight
        }
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
                    color1 = Color.Black,
                    color2 = Color.Yellow,
                ),
                zoomFactor = 4F,
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
    color1: Color,
    color2: Color,
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