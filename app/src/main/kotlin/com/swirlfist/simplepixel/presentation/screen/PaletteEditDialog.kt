package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.presentation.section.PaletteEditSection
import com.swirlfist.simplepixel.presentation.section.PixelImagePreviewSection
import com.swirlfist.simplepixel.presentation.section.SaveCancelDialogFooter
import com.swirlfist.simplepixel.presentation.state.PaletteEditDialogState
import com.swirlfist.simplepixel.presentation.state.PaletteEditState
import com.swirlfist.simplepixel.presentation.state.PixelImagePreviewSectionState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.uielements.createCheckersPixelImage

@Composable
fun PaletteEditDialog(
    originalPalette: PaletteModel,
    pixelMatrix: PixelMatrixModel? = null,
    viewModel: PaletteEditViewModel = hiltViewModel(),
    onSaveChangesClick: (PaletteModel, PixelMatrixModel?) -> Unit,
    onDismiss: () -> Unit,
) {
    val paletteEditDialogState = viewModel.paletteEditDialogState.collectAsStateWithLifecycle().value
    var viewModelInitialized by rememberSaveable { mutableStateOf(false) }

    if (!viewModelInitialized) {
        viewModel.setOriginalPalette(originalPalette)
        if (pixelMatrix != null) {
            viewModel.setOriginalPixelMatrix(pixelMatrix)
        }
        viewModelInitialized = true
    }

    PaletteEditDialogContent(
        paletteEditDialogState,
        onSaveChangesClick,
        onDismiss,
    )
}

@Composable
fun PaletteEditDialogContent(
    paletteEditDialogState: PaletteEditDialogState,
    onSaveChangesClick: (PaletteModel, PixelMatrixModel?) -> Unit,
    onDismiss: () -> Unit,
) {
    val pixelImagePreviewSectionState = paletteEditDialogState.pixelImagePreviewSectionState

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                PaletteEditSection(
                    paletteEditState = paletteEditDialogState.paletteEditState,
                )

                if (pixelImagePreviewSectionState != null) {
                    PixelImagePreviewSection(
                        modifier = Modifier
                            .height(128.dp)
                            .fillMaxWidth(),
                        state = pixelImagePreviewSectionState,
                    )
                }

                SaveCancelDialogFooter(
                    isSaveEnabled = true,
                    onSaveClick = {
                        onSaveChangesClick(
                            PaletteModel(paletteEditDialogState.paletteEditState.paletteColors),
                            pixelImagePreviewSectionState?.pixelImageModel?.pixelMatrixModel,
                        )
                    },
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun PaletteEditDialogContentNoImagePreview() {
    SimplePixelTheme {
        PaletteEditDialogContent(
            paletteEditDialogState = PaletteEditDialogState(
                paletteEditState = PaletteEditState(
                    paletteColors = listOf(
                        Color.Red.toColorLong(),
                        Color.Green.toColorLong(),
                        Color.Blue.toColorLong(),
                    ),
                    selectedPaletteIndex = 1,
                ),
            ),
            onSaveChangesClick = { _, _ -> },
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 640, heightDp = 1080)
@Composable
fun PaletteEditDialogContentPreview() {
    val pixelImage = createCheckersPixelImage(
        width = 32,
        height = 32,
        color1 = Color.Black.toColorLong(),
        color2 = Color.Yellow.toColorLong(),
    )

    SimplePixelTheme {
        PaletteEditDialogContent(
            paletteEditDialogState = PaletteEditDialogState(
                paletteEditState = PaletteEditState(
                    paletteColors = pixelImage.paletteModel.colors,
                    selectedPaletteIndex = 1,
                ),
                pixelImagePreviewSectionState = PixelImagePreviewSectionState(
                    pixelImageModel = pixelImage,
                    isFitAvailableSpace = true,
                    onPreviewBackgroundColorSelected = {},
                ),
            ),
            onSaveChangesClick = { _, _ -> },
            onDismiss = {},
        )
    }
}