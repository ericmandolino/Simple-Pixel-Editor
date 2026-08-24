package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme

private const val DIMENSION_VALUE_DEFAULT = 24
private const val DIMENSION_VALUE_MIN = 1
private const val DIMENSION_VALUE_MAX = 1024

@Composable
fun NewImageScreen(
    viewModel: NewImageScreenViewModel = hiltViewModel(),
    navigateToMain: () -> Unit,
) {
    val newImageScreenState = viewModel.newImageScreenState.collectAsStateWithLifecycle().value

    LaunchedEffect(newImageScreenState) {
        if (newImageScreenState.isNavigateToMainExpected) {
            navigateToMain()
            viewModel.onNavigateToMain()
        }
    }

    val widthTextFieldState = rememberTextFieldState(initialText = DIMENSION_VALUE_DEFAULT.toString())
    val heightTextFieldState = rememberTextFieldState(initialText = DIMENSION_VALUE_DEFAULT.toString())

    viewModel.updateTextFieldStates(
        widthTextFieldState,
        heightTextFieldState,
    )

    NewImageScreenContent(
        widthTextFieldState,
        heightTextFieldState,
        onCreateImageClick = viewModel::createImage,
    )
}

@Composable
fun NewImageScreenContent(
    widthTextFieldState: TextFieldState,
    heightTextFieldState: TextFieldState,
    onCreateImageClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .wrapContentSize()
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(4.dp),
            )
            .safeContentPadding()
            .padding(16.dp)
    ) {
        DimensionTextField(
            label = stringResource(R.string.input_width),
            textFieldState = widthTextFieldState,
        )
        DimensionTextField(
            label = stringResource(R.string.input_height),
            textFieldState = heightTextFieldState,
        )
        Spacer(Modifier.height(8.dp))
        TextButton(
            modifier = Modifier
                .align(Alignment.End),
            onClick = onCreateImageClick,
        ) {
            Text(
                text = stringResource(R.string.create)
            )
        }
    }
}

@Composable
fun DimensionTextField(
    label: String,
    textFieldState: TextFieldState,
) {
    OutlinedTextField(
        state = textFieldState,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Number,
        ),
        lineLimits = TextFieldLineLimits.SingleLine,
        label = { Text(label) },
        inputTransformation = InputTransformation.then {
            val charSequence = asCharSequence()
            if (!charSequence.isDigitsOnly()) {
                revertAllChanges()
            } else {
                try {
                    val value = charSequence.toString().toInt()
                    if (value !in DIMENSION_VALUE_MIN..DIMENSION_VALUE_MAX) {
                        revertAllChanges()
                    }
                } catch (_: NumberFormatException) {
                    revertAllChanges()
                }
            }
        },
    )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun NewImageScreenContentPreview() {
    SimplePixelTheme {
        NewImageScreenContent(
            widthTextFieldState = rememberTextFieldState(),
            heightTextFieldState = rememberTextFieldState(),
            onCreateImageClick = {},
        )
    }
}

