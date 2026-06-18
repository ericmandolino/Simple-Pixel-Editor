package com.swirlfist.simplepixel.presentation.main.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import com.swirlfist.simplepixel.presentation.main.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.uielements.PixelCanvas
import com.swirlfist.simplepixel.presentation.uielements.createCheckersPixelImage
import com.swirlfist.simplepixel.presentation.uielements.createEmptyPixelImage

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