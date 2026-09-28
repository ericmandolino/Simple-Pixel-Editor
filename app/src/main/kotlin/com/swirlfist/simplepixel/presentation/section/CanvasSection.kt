package com.swirlfist.simplepixel.presentation.section

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.presentation.createSelection
import com.swirlfist.simplepixel.presentation.state.CanvasSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.uielements.PixelCanvas
import com.swirlfist.simplepixel.presentation.uielements.createCheckersPixelImage

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
            pixelSelection = state.pixelSelectionModel,
            initialZoomFactor = state.zoomFactor,
            isShowGridEnabled = state.isShowGridEnabled,
            onPixelVisited = { x, y -> onEvent(CanvasSectionEvent.PixelVisited(x, y)) },
            onPixelVisitFinish = { onEvent(CanvasSectionEvent.PixelVisitEnd) },
            onZoomUpdate = { zoom -> onEvent(CanvasSectionEvent.ZoomUpdate(zoom)) },
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionPreview() {
    SimplePixelTheme {
        val pixelImage = createCheckersPixelImage(
            width = 5,
            height = 4,
            color1 = Color.Black.toColorLong(),
            color2 = Color.Yellow.toColorLong(),
        )
        val pixelSelection = pixelImage.createSelection(
            selectedCoordinates = listOf(
                Pair(1, 2),
                Pair(2, 2),
                Pair(1, 1),
                Pair(2, 3),
                Pair(0, 1),
            )
        )

        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = pixelImage,
                pixelSelectionModel = pixelSelection,
                zoomFactor = 1F,
                isShowGridEnabled = false,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun CanvasSectionZoomedPreview() {
    SimplePixelTheme {
        val pixelImage = createCheckersPixelImage(
            width = 5,
            height = 4,
            color1 = Color.Black.toColorLong(),
            color2 = Color.Yellow.toColorLong(),
        )
        val pixelSelection = pixelImage.createSelection(
            selectedCoordinates = listOf(
                Pair(1, 2),
                Pair(2, 2),
                Pair(1, 1),
                Pair(2, 3),
                Pair(0, 1),
            )
        )

        CanvasSection(
            modifier = Modifier.fillMaxSize(),
            state = CanvasSectionState().copy(
                pixelImageModel = pixelImage,
                pixelSelectionModel = pixelSelection,
                zoomFactor = 4F,
                isShowGridEnabled = false,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
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
                pixelImageModel = PixelImageModel.createEmpty(
                    width = 4,
                    height = 4,
                    colors = listOf(Color.Black.toColorLong(), Color.Yellow.toColorLong()),
                ),
                zoomFactor = 1F,
            )
        ) { event ->
            Log.d("CanvasSection", "event: $event")
        }
    }
}