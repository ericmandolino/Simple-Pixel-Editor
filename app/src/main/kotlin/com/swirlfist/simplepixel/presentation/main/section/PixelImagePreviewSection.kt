package com.swirlfist.simplepixel.presentation.main.section

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import com.swirlfist.simplepixel.presentation.main.state.PixelImagePreviewSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.uielements.PixelCanvasSnapshot
import com.swirlfist.simplepixel.presentation.uielements.createCheckersPixelImage

@Composable
fun PixelImagePreviewSection(
    modifier: Modifier = Modifier,
    state: PixelImagePreviewSectionState,
) {
    val pixelImage = state.pixelImageModel

    if (pixelImage != null) {
        PixelCanvasSnapshot(
            modifier = modifier,
            pixelImage = pixelImage,
            isFitAvailableSpace = state.isFitAvailableSpace,
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PixelImagePreviewSectionFitPreview() {
    SimplePixelTheme {
        PixelImagePreviewSection(
            modifier = Modifier.fillMaxSize(),
            state = PixelImagePreviewSectionState().copy(
                pixelImageModel = createCheckersPixelImage(
                    width = 64,
                    height = 64,
                    color1 = Color.Black.toColorLong(),
                    color2 = Color.Yellow.toColorLong(),
                ),
                isFitAvailableSpace = true,
            ),
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PixelImagePreviewSectionNoFitPreview() {
    SimplePixelTheme {
        PixelImagePreviewSection(
            modifier = Modifier.fillMaxSize(),
            state = PixelImagePreviewSectionState().copy(
                pixelImageModel = createCheckersPixelImage(
                    width = 64,
                    height = 64,
                    color1 = Color.Black.toColorLong(),
                    color2 = Color.Yellow.toColorLong(),
                ),
                isFitAvailableSpace = false,
            ),
        )
    }
}