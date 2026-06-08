package com.swirlfist.simplepixel

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.ui.theme.SimplePixelTheme
import kotlin.math.max

@Composable
fun PixelCanvas(
    modifier: Modifier,
    pixelImage: PixelImage,
    zoomFactor: Float = 1F,
    initialImageDeltaX: Float = 0F,
    initialImageDeltaY: Float = 0F,
    isShowCoordinatesEnabled: Boolean = false,
    onPixelTap: (xPixel: Int, yPixel: Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val invertedPalette = remember {  pixelImage.palette.invert() }
    val isShowCoordinates = remember { isShowCoordinatesEnabled && zoomFactor >= 1F }
    val imageDeltaX = rememberSaveable { mutableFloatStateOf(initialImageDeltaX) }
    val imageDeltaY = rememberSaveable { mutableFloatStateOf(initialImageDeltaY) }

    Canvas(
        modifier = modifier
            .pointerInput(Unit) {
                detectDragGestures { _, dragAmount ->
                    val pixelSize = 32.dp.toPx() * zoomFactor
                    val imageWidth = pixelImage.pixelMatrix.width * pixelSize
                    val imageHeight = pixelImage.pixelMatrix.height * pixelSize
                    val minImageDeltaX = 0F
                    val minImageDeltaY = 0F
                    val maxImageDeltaX = max(imageWidth - size.width, 0F)
                    val maxImageDeltaY = max(imageHeight - size.height, 0F)

                    imageDeltaX.floatValue = (imageDeltaX.floatValue - dragAmount.x)
                        .cap(minImageDeltaX, maxImageDeltaX)
                    imageDeltaY.floatValue = (imageDeltaY.floatValue - dragAmount.y)
                        .cap(minImageDeltaY, maxImageDeltaY)
//                    android.util.Log.e(
//                        "gus",
//                        "drag $dragAmount delta: (${imageDeltaX.floatValue},${imageDeltaY.floatValue})"
//                    )
                }
            }
            .pointerInput(Unit) {
                detectTapGestures { tapOffset ->
                    val pixelSize = 32.dp.toPx() * zoomFactor
                    val xPixel = ((imageDeltaX.floatValue + tapOffset.x) / pixelSize).toInt()
                    val yPixel =
                        pixelImage.pixelMatrix.height - 1 - ((imageDeltaY.floatValue + tapOffset.y) / pixelSize).toInt()

                    if (xPixel !in 0..<pixelImage.pixelMatrix.width) return@detectTapGestures
                    if (yPixel !in 0..<pixelImage.pixelMatrix.height) return@detectTapGestures

//                    android.util.Log.e("gus", "tap $tapOffset -> ($xPixel,$yPixel)")
                    onPixelTap(xPixel, yPixel)
                }
            }
        ,
    ) {
        val canvasHeight = size.height
        val canvasWidth = size.width
        val pixelSize = 32.dp.toPx() * zoomFactor
        val halfPixelSize = pixelSize / 2

        var y = 0F
        while (y < canvasHeight) {
            val yPixelMatrix = pixelImage.pixelMatrix.height - 1 - ((imageDeltaY.floatValue + y) / pixelSize).toInt()
            if (yPixelMatrix < 0) break

            val pixelRectHeight = if (y > 0F || imageDeltaY.floatValue == 0F) {
                pixelSize
            } else {
                pixelSize - imageDeltaY.floatValue % pixelSize
            }

            var x = 0F
            while (x < canvasWidth) {
                val xPixelMatrix = ((imageDeltaX.floatValue + x) / pixelSize).toInt()
                if (xPixelMatrix > pixelImage.pixelMatrix.width - 1) break

                val pixelRectWidth = if (x > 0F || imageDeltaX.floatValue == 0F) {
                    pixelSize
                } else {
                    pixelSize - imageDeltaX.floatValue % pixelSize
                }

                val pixel = pixelImage.getPixelAt(
                    x = xPixelMatrix,
                    y = yPixelMatrix
                )
                val pixelColor = pixelImage.palette.getColor(pixel.paletteIndex)

                val pixelRectOffset = Offset(x, y)
                val pixelRectSize = Size(pixelRectWidth, pixelRectHeight)

                drawRect(
                    color = pixelColor,
                    topLeft = pixelRectOffset,
                    size = pixelRectSize,
                )

                if (isShowCoordinates) {
                    val coordinateText = "$xPixelMatrix,$yPixelMatrix"
                    val textColor = invertedPalette.getColor(pixel.paletteIndex)
                    val textSize = textMeasurer.measure(
                        text = coordinateText
                    ).size
                    val coordinateTextOffset = pixelRectOffset + Offset(
                        x = pixelRectWidth - pixelSize + halfPixelSize - textSize.width / 2,
                        y = pixelRectHeight - pixelSize + halfPixelSize - textSize.height / 2,
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
fun PixelCanvasPreview() {
    SimplePixelTheme {
        PixelCanvas(
            modifier = Modifier.fillMaxSize(),
            pixelImage = createCheckersPixelImage(
                width = 5,
                height = 3,
                color1 = Color.Black,
                color2 = Color.Yellow,
            ),
            zoomFactor = 4F,
            initialImageDeltaX = 0F,
            initialImageDeltaY = 0F,
            isShowCoordinatesEnabled = true,
            onPixelTap = { x, y -> }
        )
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
): PixelImage {
    val rows = mutableListOf<Array<Pixel>>()
    for (y in 0..< height) {
        val row = mutableListOf<Pixel>()
        for (x in 0..< width) {
            val isXEven = x % 2 == 0
            val isYEven = y % 2 == 0
            row.add(
                Pixel(if ((isXEven && !isYEven) || (!isXEven && isYEven)) 0 else 1)
            )
        }
        rows.add(row.toTypedArray())
    }

    return PixelImage(
        pixelMatrix = PixelMatrix(
            content = rows.toTypedArray()
        ),
        palette = Palette(listOf(color1, color2)),
    )
}

class PixelImage(
    val pixelMatrix: PixelMatrix,
    val palette: Palette,
) {
    fun getPixelAt(
        x: Int,
        y: Int,
        invertY: Boolean = false,
    ): Pixel {
        return if (invertY) {
            val height = pixelMatrix.content.size
            pixelMatrix.content[height - 1 - y][x]
        } else {
            pixelMatrix.content[y][x]
        }
    }
}

class PixelMatrix(
    val content: Array<Array<Pixel>>,
) {
    val width: Int
        get() = if (content.isEmpty()) 0 else content.first().size

    val height: Int
        get() = content.size
}

data class Pixel(
    val paletteIndex: Int,
)

class Palette(
    private val colors: List<Color>
) {
    fun getColor(index: Int) = colors[index]

    fun invert() : Palette = Palette(
        colors.map { color ->
            color.copy(
                red = 1F - color.red,
                green = 1F - color.green,
                blue = 1F - color.blue,
            )
        }
    )
}