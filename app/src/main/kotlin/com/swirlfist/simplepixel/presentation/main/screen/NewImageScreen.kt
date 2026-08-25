package com.swirlfist.simplepixel.presentation.main.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.graphics.toColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.toHexCode
import com.swirlfist.simplepixel.presentation.uielements.IconButton
import com.swirlfist.simplepixel.presentation.uielements.PaletteColorButton
import com.swirlfist.simplepixel.presentation.uielements.selectedButtonModifier
import kotlin.math.roundToInt

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

    val widthTextFieldState =
        rememberTextFieldState(initialText = DIMENSION_VALUE_DEFAULT.toString())
    val heightTextFieldState =
        rememberTextFieldState(initialText = DIMENSION_VALUE_DEFAULT.toString())

    viewModel.updateTextFieldStates(
        widthTextFieldState,
        heightTextFieldState,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center,
    ) {
        NewImageScreenContent(
            widthTextFieldState,
            heightTextFieldState,
            paletteColors = newImageScreenState.paletteColors,
            selectedPaletteIndex = newImageScreenState.selectedPaletteIndex,
            onCreateImageClick = viewModel::createImage,
            onAddPaletteColorClick = viewModel::addColorToPalette,
            onPaletteColorClick = viewModel::updateSelectedPaletteColor,
            onDeletePaletteColorClick = viewModel::deleteColorFromPalette,
            onColorComponentRedSliderChange = viewModel::updatePaletteColorComponentRed,
            onColorComponentGreenSliderChange = viewModel::updatePaletteColorComponentGreen,
            onColorComponentBlueSliderChange = viewModel::updatePaletteColorComponentBlue,
        )
    }
}

@Composable
fun NewImageScreenContent(
    widthTextFieldState: TextFieldState,
    heightTextFieldState: TextFieldState,
    paletteColors: List<Long>,
    selectedPaletteIndex: Int?,
    onCreateImageClick: () -> Unit,
    onAddPaletteColorClick: () -> Unit,
    onPaletteColorClick: (Int) -> Unit,
    onDeletePaletteColorClick: () -> Unit,
    onColorComponentRedSliderChange: (Float) -> Unit,
    onColorComponentGreenSliderChange: (Float) -> Unit,
    onColorComponentBlueSliderChange: (Float) -> Unit,
) {
    val sectionModifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(4.dp),
        )
        .padding(8.dp)

    Column(
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ImageSize(
            modifier = sectionModifier,
            widthTextFieldState,
            heightTextFieldState,
        )

        ImagePalette(
            modifier = sectionModifier,
            colors = paletteColors,
            selectedPaletteIndex,
            onAddPaletteColorClick,
            onPaletteColorClick,
            onDeletePaletteColorClick,
            onColorComponentRedSliderChange,
            onColorComponentGreenSliderChange,
            onColorComponentBlueSliderChange,
        )

        val isCreateButtonEnabled =
            widthTextFieldState.text.isNotBlank() &&
                    heightTextFieldState.text.isNotBlank() &&
                    paletteColors.isNotEmpty()

        TextButton(
            modifier = Modifier
                .align(Alignment.End),
            enabled = isCreateButtonEnabled,
            onClick = onCreateImageClick,
        ) {
            Text(
                text = stringResource(R.string.create),
            )
        }
    }
}

@Composable
fun CreateImageSectionTitle(
    text: String,
) {
    Text(
        modifier = Modifier.padding(4.dp),
        text = text,
        style = MaterialTheme.typography.titleMedium
    )
}

@Composable
fun ImageSize(
    modifier: Modifier = Modifier,
    widthTextFieldState: TextFieldState,
    heightTextFieldState: TextFieldState,
) {
    Column(
        modifier = modifier,
    ) {
        CreateImageSectionTitle(
            text = stringResource(R.string.create_image_size),
        )
        DimensionTextField(
            label = stringResource(R.string.input_width),
            textFieldState = widthTextFieldState,
        )
        DimensionTextField(
            label = stringResource(R.string.input_height),
            textFieldState = heightTextFieldState,
        )
        Spacer(Modifier.height(8.dp))
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
            if (charSequence.isEmpty()) {
                return@then
            }
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

@Composable
fun ImagePalette(
    modifier: Modifier = Modifier,
    colors: List<Long>,
    selectedPaletteIndex: Int?,
    onAddPaletteColorClick: () -> Unit,
    onPaletteColorClick: (Int) -> Unit,
    onDeletePaletteColorClick: () -> Unit,
    onColorComponentRedSliderChange: (Float) -> Unit,
    onColorComponentGreenSliderChange: (Float) -> Unit,
    onColorComponentBlueSliderChange: (Float) -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        CreateImageSectionTitle(
            text = stringResource(R.string.create_image_palette),
        )

        FlowColumn(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachColumn = 2,
        ) {
            colors.forEachIndexed { paletteIndex, colorLong ->
                PaletteColorSelectButton(
                    colorLong,
                    paletteIndex,
                    isSelected = paletteIndex == selectedPaletteIndex,
                    onClick = { onPaletteColorClick(paletteIndex) },
                )
            }
            IconButton(
                modifier = Modifier.padding(4.dp),
                drawableResId = R.drawable.ic_new_image_screen_add_palette_color_24dp,
                contentDescriptionResId = R.string.cd_new_image_screen_add_palette_color,
                size = 48.dp,
                isEnabled = true,
                onClick = onAddPaletteColorClick,
            )
        }

        val color = if (selectedPaletteIndex != null && selectedPaletteIndex in colors.indices) {
            Color.fromColorLong(colors[selectedPaletteIndex])
        } else {
            null
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    modifier = Modifier
                        .weight(1F),
                    text = color?.toHexCode()?.uppercase() ?: "",
                    style = MaterialTheme.typography.titleMedium,
                )
                TextButton(
                    onClick = onDeletePaletteColorClick,
                    enabled = color != null,
                ) {
                    Text(
                        text = stringResource(R.string.delete)
                    )
                }
            }
            PaletteColorComponent(
                label = stringResource(R.string.red),
                sliderValue = color?.red,
                onSliderChange = onColorComponentRedSliderChange,
            )
            PaletteColorComponent(
                label = stringResource(R.string.green),
                sliderValue = color?.green,
                onSliderChange = onColorComponentGreenSliderChange,
            )
            PaletteColorComponent(
                label = stringResource(R.string.blue),
                sliderValue = color?.blue,
                onSliderChange = onColorComponentBlueSliderChange,
            )
        }
    }
}

@Composable
fun PaletteColorSelectButton(
    colorLong: Long,
    paletteIndex: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .selectedButtonModifier(
                isSelected,
                IconButtonDefaults.filledIconButtonColors().containerColor
            ),
    ) {
        PaletteColorButton(
            color = Color.fromColorLong(colorLong),
            size = 48.dp,
            isEnabled = true,
            contentDescriptionValue = stringResource(
                R.string.cd_new_image_screen_edit_palette_color,
                paletteIndex
            ),
            onClick = onClick,
        )
    }
}

@Composable
fun PaletteColorComponent(
    label: String,
    sliderValue: Float?,
    onSliderChange: (Float) -> Unit,
) {
    val sliderPosition = sliderValue ?: 0F
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier
                .width(24.dp),
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End,
        )
        Slider(
            modifier = Modifier
                .weight(1F),
            value = sliderPosition,
            onValueChange = onSliderChange,
            valueRange = 0F..1F,
            steps = 256,
            enabled = sliderValue != null,
        )
        Text(
            modifier = Modifier
                .width(48.dp),
            text = (sliderPosition * 255).roundToInt().toString(),
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun NewImageScreenContentPreview() {
    SimplePixelTheme {
        NewImageScreenContent(
            widthTextFieldState = rememberTextFieldState(),
            heightTextFieldState = rememberTextFieldState(),
            paletteColors = listOf(),
            selectedPaletteIndex = null,
            onCreateImageClick = {},
            onAddPaletteColorClick = {},
            onPaletteColorClick = {},
            onDeletePaletteColorClick = {},
            onColorComponentRedSliderChange = {},
            onColorComponentGreenSliderChange = {},
            onColorComponentBlueSliderChange = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ImageSizePreview() {
    SimplePixelTheme {
        ImageSize(
            widthTextFieldState = rememberTextFieldState(),
            heightTextFieldState = rememberTextFieldState(),
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun ImagePalettePreview() {
    SimplePixelTheme {
        ImagePalette(
            colors = listOf(
                Color.Red.toColorLong(),
                Color.Blue.toColorLong(),
                Color.Green.toColorLong(),
            ),
            selectedPaletteIndex = 1,
            onAddPaletteColorClick = {},
            onPaletteColorClick = {},
            onDeletePaletteColorClick = {},
            onColorComponentRedSliderChange = {},
            onColorComponentGreenSliderChange = {},
            onColorComponentBlueSliderChange = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PaletteColorComponentPreview() {
    SimplePixelTheme {
        PaletteColorComponent(
            label = "R",
            sliderValue = null,
            onSliderChange = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PaletteColorSelectButtonPreview() {
    SimplePixelTheme {
        PaletteColorSelectButton(
            colorLong = Color.Red.toColorLong(),
            paletteIndex = 0,
            isSelected = true,
            onClick = {},
        )
    }
}

