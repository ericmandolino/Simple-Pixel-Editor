package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.state.PalettePresetsDialogState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import kotlin.collections.forEach
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.domain.model.PalettePresetModel
import com.swirlfist.simplepixel.presentation.uielements.LoadingIndeterminateProgress

@Composable
fun PalettePresetsDialog(
    viewModel: PalettePresetsViewModel = hiltViewModel(),
    onPalettePresetSelected: (PaletteModel) -> Unit,
    onDismiss: () -> Unit,
) {
    val palettePresetsDialogState = viewModel.palettePresetsDialogState.collectAsStateWithLifecycle().value

    PalettePresetsDialogContent(
        palettePresetsDialogState,
        onPalettePresetSelected,
        onDismiss,
    )
}

@Composable
fun PalettePresetsDialogContent(
    palettePresetsDialogState: PalettePresetsDialogState,
    onPalettePresetSelected: (PaletteModel) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            if (palettePresetsDialogState.isLoadingPresets) {
                LoadingIndeterminateProgress(
                    message = stringResource(R.string.loading_palette_presets)
                )
            } else {
                PalettePresets(
                    modifier = Modifier
                        .padding(16.dp),
                    palettePresets = palettePresetsDialogState.palettePresets,
                    onPalettePresetSelected = onPalettePresetSelected,
                )
            }
        }
    }
}

@Composable
fun PalettePresets(
    modifier: Modifier = Modifier,
    palettePresets: List<PalettePresetModel>,
    onPalettePresetSelected: (PaletteModel) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        palettePresets.forEach { palettePreset ->
            item {
                PalettePresetItem(
                    presetName = palettePreset.name,
                    palette = palettePreset.palette,
                    onClick = { onPalettePresetSelected(palettePreset.palette) }
                )
            }
        }
    }
}

@Composable
fun PalettePresetItem(
    presetName: String,
    palette: PaletteModel,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClickLabel = stringResource(R.string.cd_new_image_screen_select_palette_preset),
                onClick = onClick,
            ),
    ) {
        Text(
            text = presetName,
            style = MaterialTheme.typography.titleSmall,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp),
            maxLines = 4,
        ) {
            palette.colors.forEach { color ->
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.fromColorLong(color)),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PalettePresetsDialogPreview() {
    SimplePixelTheme {
        PalettePresetsDialogContent(
            palettePresetsDialogState = PalettePresetsDialogState(
                palettePresets = getPreviewPalettePresets(),
                isLoadingPresets = false,
            ),
            onPalettePresetSelected = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PalettePresetsDialogLoadingPreview() {
    SimplePixelTheme {
        PalettePresetsDialogContent(
            palettePresetsDialogState = PalettePresetsDialogState(
                isLoadingPresets = true,
            ),
            onPalettePresetSelected = {},
            onDismiss = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 96)
@Composable
fun PalettePresetItemPreview() {
    SimplePixelTheme {
        val colors = listOf(
            Color.Red.toColorLong(),
            Color.Blue.toColorLong(),
            Color.Green.toColorLong(),
        )
        PalettePresetItem(
            presetName = "PRESET",
            palette = PaletteModel(colors),
            onClick = {},
        )
    }
}

private fun getPreviewPalettePresets(): List<PalettePresetModel> {
    val presets = mutableListOf<PalettePresetModel>()

    // Black & White
    presets.add(
        PalettePresetModel(
            id = 0,
            name = "B & W",
            palette = PaletteModel(
                colors = listOf(
                    Color.Black.toColorLong(),
                    Color.White.toColorLong(),
                )
            )
        )
    )

    // Game Boy
    presets.add(
        PalettePresetModel(
            id = 0,
            name = "Game Boy",
            palette = PaletteModel(
                colors = listOf(
                    Color(155, 188, 15, 255).toColorLong(),
                    Color(139, 172, 15, 255).toColorLong(),
                    Color(48, 98, 48, 255).toColorLong(),
                    Color(15, 56, 15, 255).toColorLong(),
                )
            )
        )
    )

    return presets
}