package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.section.PaletteEditSection
import com.swirlfist.simplepixel.presentation.section.SaveCancelDialogFooter
import com.swirlfist.simplepixel.presentation.state.PaletteEditDialogState
import com.swirlfist.simplepixel.presentation.state.PaletteEditState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun PaletteEditDialog(
    originalPalette: PaletteModel,
    viewModel: PaletteEditViewModel = hiltViewModel(),
    onSaveChangesClick: (PaletteModel) -> Unit,
    onDismiss: () -> Unit,
) {
    val paletteEditDialogState = viewModel.paletteEditDialogState.collectAsStateWithLifecycle().value

    viewModel.setOriginalPalette(originalPalette)

    PaletteEditDialogContent(
        paletteEditDialogState,
        onSaveChangesClick,
        onDismiss,
    )
}

@Composable
fun PaletteEditDialogContent(
    paletteEditDialogState: PaletteEditDialogState,
    onSaveChangesClick: (PaletteModel) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                PaletteEditSection(
                    modifier = Modifier
                        .padding(16.dp),
                    paletteEditState = paletteEditDialogState.paletteEditState,
                )

                SaveCancelDialogFooter(
                    isSaveEnabled = true,
                    onSaveClick = {
                        onSaveChangesClick(
                            PaletteModel(paletteEditDialogState.paletteEditState.paletteColors)
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
fun PaletteEditDialogContentPreview() {
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
            onSaveChangesClick = {},
            onDismiss = {},
        )
    }
}