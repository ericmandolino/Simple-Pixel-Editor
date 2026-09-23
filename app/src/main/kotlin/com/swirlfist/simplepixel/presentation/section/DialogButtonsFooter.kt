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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

@Composable
fun ActionCancelButtonsDialogFooter(
    isActionEnabled: Boolean,
    actionText: String,
    cancelText: String,
    onActionClick: () -> Unit,
    onCancelClick: () -> Unit,
) {
    BaseDialogButtonsFooter(
        leftText = cancelText,
        onLeftClick = onCancelClick,
        isRightEnabled = isActionEnabled,
        rightText = actionText,
        onRightClick = onActionClick,
    )
}

@Composable
fun ConfirmButtonDialogFooter(
    confirmText: String,
    onConfirmClick: () -> Unit,
) {
    BaseDialogButtonsFooter(
        leftText = confirmText,
        onLeftClick = onConfirmClick,
    )
}

@Composable
fun BaseDialogButtonsFooter(
    leftText: String,
    onLeftClick: () -> Unit,
    isRightEnabled: Boolean = true,
    rightText: String? = null,
    onRightClick: () -> Unit = {},

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
            onClick = onLeftClick,
        ) {
            Text(
                text = leftText,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.End,
            )
        }
        rightText?.let { text ->
            TextButton(
                enabled = isRightEnabled,
                onClick = onRightClick,
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Start,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ActionCancelButtonsDialogFooterPreview() {
    SimplePixelTheme {
        ActionCancelButtonsDialogFooter(
            isActionEnabled = true,
            actionText = "Save",
            cancelText = "Cancel",
            onActionClick = {},
            onCancelClick = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ConfirmButtonDialogFooterPreview() {
    SimplePixelTheme {
        ConfirmButtonDialogFooter(
            confirmText = "Ok",
            onConfirmClick = {},
        )
    }
}