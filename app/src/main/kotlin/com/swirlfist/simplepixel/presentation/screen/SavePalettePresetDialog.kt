package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

private const val PRESET_NAME_MAX_LENGTH = 64

@Composable
fun SavePalettePresetDialog(
    viewModel: SavePalettePresetViewModel = hiltViewModel(),
    paletteColors: List<Long>,
    onSaveClick: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val nameTextFieldState = rememberTextFieldState()

    viewModel.updateNameTextFieldState(nameTextFieldState)

    SavePalettePresetDialogContent(
        paletteColors,
        nameTextFieldState,
        onSaveClick,
        onDismiss,
    )
}

@Composable
fun SavePalettePresetDialogContent(
    paletteColors: List<Long>,
    nameTextFieldState: TextFieldState,
    onSaveClick: (String) -> Unit,
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
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    state = nameTextFieldState,
                    modifier = Modifier.fillMaxWidth(),
                    lineLimits = TextFieldLineLimits.SingleLine,
                    label = { Text(stringResource(R.string.palette_preset_name)) },
                    inputTransformation = InputTransformation.then {
                        val charSequence = asCharSequence()
                        if (charSequence.length > PRESET_NAME_MAX_LENGTH) {
                            revertAllChanges()
                        }
                    },
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp),
                    maxLines = 4,
                ) {
                    paletteColors.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(Color.fromColorLong(color)),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        8.dp,
                        Alignment.End,
                    )
                ) {
                    val presetName = nameTextFieldState.text.toString()
                    TextButton(
                        onClick = onDismiss,
                    ) {
                        Text(
                            text = stringResource(android.R.string.cancel),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.End,
                        )
                    }
                    TextButton(
                        enabled = presetName.isNotBlank(),
                        onClick = { onSaveClick(presetName) },
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Start,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun SavePalettePresetDialogPreview() {
    SimplePixelTheme {
        SavePalettePresetDialogContent(
            paletteColors = listOf(
                Color.Red.toColorLong(),
                Color.Green.toColorLong(),
                Color.Blue.toColorLong(),
            ),
            nameTextFieldState = rememberTextFieldState(),
            onSaveClick = {},
            onDismiss = {},
        )
    }
}