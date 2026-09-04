package com.swirlfist.simplepixel.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.then
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.Dialog
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.presentation.state.NewImagePaletteState
import com.swirlfist.simplepixel.presentation.state.NewImageScreenState
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
        Surface(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(16.dp),
        ) {
            NewImageScreenContent(
                newImageScreenState,
                widthTextFieldState,
                heightTextFieldState,
            )
        }
    }
}

@Composable
fun NewImageScreenContent(
    newImageScreenState: NewImageScreenState,
    widthTextFieldState: TextFieldState,
    heightTextFieldState: TextFieldState,
) {
    if (newImageScreenState.isShowPalettePresets) {
        PalettePresetsDialog(
            palettePresets = getBasePalettePresets(),
            onPalettePresetSelected = newImageScreenState.onPalettePresetSelected,
            onDismiss = newImageScreenState.onCancelPalettePresetSelection,
        )
    }

    val sectionModifier = Modifier
        .border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            shape = RoundedCornerShape(4.dp),
        )
        .padding(8.dp)

    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        ImageSize(
            modifier = sectionModifier,
            widthTextFieldState,
            heightTextFieldState,
        )

        ImagePalette(
            modifier = sectionModifier,
            paletteState = newImageScreenState.paletteState,
        )

        val isCreateButtonEnabled =
            widthTextFieldState.text.isNotBlank() &&
                    heightTextFieldState.text.isNotBlank() &&
                    newImageScreenState.paletteState.paletteColors.isNotEmpty()

        TextButton(
            modifier = Modifier
                .align(Alignment.End),
            enabled = isCreateButtonEnabled,
            onClick = newImageScreenState.onCreateImageClick,
        ) {
            Text(
                text = stringResource(R.string.create),
            )
        }
    }
}

@Composable
fun CreateImageSectionTitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier.padding(4.dp),
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DimensionTextField(
                modifier = Modifier.weight(0.4F),
                label = stringResource(R.string.input_width),
                textFieldState = widthTextFieldState,
            )
            Text(
                text = "x",
            )
            DimensionTextField(
                modifier = Modifier.weight(0.4F),
                label = stringResource(R.string.input_height),
                textFieldState = heightTextFieldState,
            )
        }
    }
}

@Composable
fun DimensionTextField(
    modifier: Modifier = Modifier,
    label: String,
    textFieldState: TextFieldState,
) {
    OutlinedTextField(
        state = textFieldState,
        modifier = modifier,
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
    paletteState: NewImagePaletteState

) {
    val colors = paletteState.paletteColors

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.Top,
        ) {
            CreateImageSectionTitle(
                modifier = Modifier
                    .weight(1F),
                text = stringResource(R.string.create_image_palette),
            )

            TextButton(
                onClick = paletteState.onLoadPalettePresetClick,
            ) {
                Text(
                    text = stringResource(R.string.load_palette_preset),
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        val selectedPaletteIndex = paletteState.selectedPaletteIndex

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
                    isSelected = paletteIndex == paletteState.selectedPaletteIndex,
                    onClick = { paletteState.onPaletteColorClick(paletteIndex) },
                )
            }
            IconButton(
                modifier = Modifier.padding(4.dp),
                drawableResId = R.drawable.ic_new_image_screen_add_palette_color_24dp,
                contentDescriptionResId = R.string.cd_new_image_screen_add_palette_color,
                size = 48.dp,
                isEnabled = true,
                onClick = paletteState.onAddPaletteColorClick,
            )
        }

        val color = if (selectedPaletteIndex != null && selectedPaletteIndex in colors.indices) {
            Color.fromColorLong(colors[selectedPaletteIndex])
        } else {
            null
        }

        Card {
            Column(
                modifier = Modifier
                    .padding(8.dp),
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
                        onClick = paletteState.onDeletePaletteColorClick,
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
                    onSliderChange = paletteState.onColorComponentRedSliderChange,
                )
                PaletteColorComponent(
                    label = stringResource(R.string.green),
                    sliderValue = color?.green,
                    onSliderChange = paletteState.onColorComponentGreenSliderChange,
                )
                PaletteColorComponent(
                    label = stringResource(R.string.blue),
                    sliderValue = color?.blue,
                    onSliderChange = paletteState.onColorComponentBlueSliderChange,
                )
            }
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

@Composable
fun PalettePresetsDialog(
    palettePresets: Map<String, PaletteModel>,
    onPalettePresetSelected: (PaletteModel) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            PalettePresets(
                modifier = Modifier
                    .padding(16.dp),
                palettePresets = palettePresets,
                onPalettePresetSelected = onPalettePresetSelected,
            )
        }
    }
}

@Composable
fun PalettePresets(
    modifier: Modifier = Modifier,
    palettePresets: Map<String, PaletteModel>,
    onPalettePresetSelected: (PaletteModel) -> Unit,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        palettePresets.forEach { (presetName, palette) ->
            item {
                PalettePresetItem(
                    presetName,
                    palette,
                    onClick = { onPalettePresetSelected(palette) }
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

@Preview(showBackground = true, widthDp = 320, heightDp = 640)
@Composable
fun NewImageScreenContentPreview() {
    SimplePixelTheme {
        val widthTextFieldState = rememberTextFieldState()
        val heightTextFieldState = rememberTextFieldState()
        NewImageScreenContent(
            newImageScreenState = NewImageScreenState(
                isNavigateToMainExpected = false,
                widthTextFieldState,
                heightTextFieldState,
            ),
            widthTextFieldState,
            heightTextFieldState,
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
            paletteState = NewImagePaletteState(),
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

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PalettePresetsDialogPreview() {
    SimplePixelTheme {
        PalettePresetsDialog(
            palettePresets = getBasePalettePresets(),
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

// TODO: create repo
private fun getBasePalettePresets(): Map<String, PaletteModel> {
    val presets = mutableMapOf<String, PaletteModel>()

    // Black & White
    presets["B & W"] = PaletteModel(
        colors = listOf(
            Color.Black.toColorLong(),
            Color.White.toColorLong(),
        )
    )

    // Game Boy
    presets["Game Boy"] = PaletteModel(
        colors = listOf(
            Color(155, 188, 15, 255).toColorLong(),
            Color(139, 172, 15, 255).toColorLong(),
            Color(48, 98, 48, 255).toColorLong(),
            Color(15, 56, 15, 255).toColorLong(),
        )
    )

    return presets
}

