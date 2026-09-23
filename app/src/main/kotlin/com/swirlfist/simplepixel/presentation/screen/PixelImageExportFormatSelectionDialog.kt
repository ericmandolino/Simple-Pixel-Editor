package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.section.CancelButtonDialogFooter
import com.swirlfist.simplepixel.presentation.state.PixelImageExportFormat
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun PixelImageExportFormatSelectionDialog(
    onFormatSelected: (PixelImageExportFormat) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                LazyColumn(
                    modifier = Modifier
                        .weight(1F)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PixelImageExportFormat.entries.forEach { exportFormat ->
                        item {
                            TextButton(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = { onFormatSelected(exportFormat) },
                            ) {
                                Text(
                                    text = when(exportFormat) {
                                        PixelImageExportFormat.PNG -> stringResource(R.string.pixel_image_export_format_png)
                                        PixelImageExportFormat.SVG -> stringResource(R.string.pixel_image_export_format_svg)
                                    }
                                )
                            }
                        }
                    }
                }

                CancelButtonDialogFooter(
                    onCancelClick = onDismiss,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PixelImageExportFormatSelectionDialogPreview() {
    SimplePixelTheme {
        PixelImageExportFormatSelectionDialog(
            onFormatSelected = {},
            onDismiss = {},
        )
    }
}
