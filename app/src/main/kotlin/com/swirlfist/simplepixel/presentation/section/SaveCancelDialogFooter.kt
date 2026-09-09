package com.swirlfist.simplepixel.presentation.section

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun SaveCancelDialogFooter(
    isSaveEnabled: Boolean,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(
            8.dp,
            Alignment.End,
        )
    ) {
        TextButton(
            onClick = onCancelClick,
        ) {
            Text(
                text = stringResource(android.R.string.cancel),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
        TextButton(
            enabled = isSaveEnabled,
            onClick = onSaveClick,
        ) {
            Text(
                text = stringResource(R.string.save),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Start,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SaveCancelDialogFooterPreview() {
    SimplePixelTheme {
        SaveCancelDialogFooter(
            isSaveEnabled = true,
            onSaveClick = {},
            onCancelClick = {},
        )
    }
}