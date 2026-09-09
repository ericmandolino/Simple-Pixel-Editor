package com.swirlfist.simplepixel.presentation.section

import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.text.input.then
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.fromColorLong
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.presentation.state.PaletteEditState
import com.swirlfist.simplepixel.presentation.theme.SimplePixelTheme
import com.swirlfist.simplepixel.presentation.toHexCode
import com.swirlfist.simplepixel.presentation.uielements.IconButton
import com.swirlfist.simplepixel.presentation.uielements.PaletteColorSelectButton
import kotlin.math.roundToInt

@Composable
fun PaletteEditSection(
    modifier: Modifier = Modifier,
    paletteEditState: PaletteEditState,
) {
    val colors = paletteEditState.paletteColors
    val selectedPaletteIndex = paletteEditState.selectedPaletteIndex

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FlowColumn(
            modifier = Modifier
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachColumn = 2,
        ) {
            colors.forEachIndexed { paletteIndex, colorLong ->
                PaletteColorSelectButton(
                    Color.fromColorLong(colorLong),
                    size = 48.dp,
                    isEnabled = true,
                    contentDescriptionValue = stringResource(
                        R.string.cd_new_image_screen_edit_palette_color,
                        paletteIndex
                    ),
                    isSelected = paletteIndex == selectedPaletteIndex,
                    onClick = { paletteEditState.onPaletteColorClick(paletteIndex) },
                )
            }
            IconButton(
                modifier = Modifier.padding(4.dp),
                drawableResId = R.drawable.ic_new_image_screen_add_palette_color_24dp,
                contentDescriptionResId = R.string.cd_new_image_screen_add_palette_color,
                size = 48.dp,
                isEnabled = true,
                onClick = paletteEditState.onAddPaletteColorClick,
            )
        }

        val color = if (selectedPaletteIndex != null && selectedPaletteIndex in colors.indices) {
            Color.fromColorLong(colors[selectedPaletteIndex])
        } else {
            null
        }

        PaletteColorEdit(
            color = color,
            onDeleteClick = paletteEditState.onDeletePaletteColorClick,
            onColorComponentRedSliderChange = paletteEditState.onColorComponentRedSliderChange,
            onColorComponentGreenSliderChange = paletteEditState.onColorComponentGreenSliderChange,
            onColorComponentBlueSliderChange = paletteEditState.onColorComponentBlueSliderChange,
        )
    }
}

@Composable
fun PaletteColorEdit(
    modifier: Modifier = Modifier,
    color: Color?,
    onDeleteClick: () -> Unit,
    onColorComponentRedSliderChange: (Float) -> Unit,
    onColorComponentGreenSliderChange: (Float) -> Unit,
    onColorComponentBlueSliderChange: (Float) -> Unit,
) {
    val redTextFieldState = rememberTextFieldState()
    val greenTextFieldState = rememberTextFieldState()
    val blueTextFieldState = rememberTextFieldState()

    LaunchedEffect(color) {
        redTextFieldState.setTextAndPlaceCursorAtEnd(
            color?.red?.toColorComponentString() ?: ""
        )
        greenTextFieldState.setTextAndPlaceCursorAtEnd(
            color?.green?.toColorComponentString() ?: ""
        )
        blueTextFieldState.setTextAndPlaceCursorAtEnd(
            color?.blue?.toColorComponentString() ?: ""
        )
    }

    Column(
        modifier = modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(16.dp),
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
                onClick = onDeleteClick,
                enabled = color != null,
            ) {
                Text(
                    text = stringResource(R.string.delete)
                )
            }
        }
        PaletteColorComponent(
            label = stringResource(R.string.red),
            textFieldState = redTextFieldState,
            onSliderChange = onColorComponentRedSliderChange,
            isEnabled = color != null,
        )
        PaletteColorComponent(
            label = stringResource(R.string.green),
            textFieldState = greenTextFieldState,
            onSliderChange = onColorComponentGreenSliderChange,
            isEnabled = color != null,
        )
        PaletteColorComponent(
            label = stringResource(R.string.blue),
            textFieldState = blueTextFieldState,
            onSliderChange = onColorComponentBlueSliderChange,
            isEnabled = color != null,
        )
    }
}

@Composable
fun PaletteColorComponent(
    label: String,
    textFieldState: TextFieldState,
    onSliderChange: (Float) -> Unit,
    isEnabled: Boolean = true,
) {
    val textValue = textFieldState.text.toString()
    val sliderPosition = if (textValue.isNotBlank()) textValue.toFloat() / 255 else 0F

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.End,
        )
        Slider(
            modifier = Modifier
                .weight(1F),
            value = sliderPosition,
            onValueChange = { value ->
                textFieldState.setTextAndPlaceCursorAtEnd(value.toColorComponentString())
                onSliderChange(value)
            },
            valueRange = 0F..1F,
            steps = 256,
            enabled = isEnabled,
        )
        TextField(
            state = textFieldState,
            modifier = Modifier
                .width(64.dp)
                .onFocusChanged { focusState ->
                    if (!focusState.hasFocus && textFieldState.text.isBlank()) {
                        textFieldState.setTextAndPlaceCursorAtEnd("0")
                        onSliderChange(0F)
                    }
                }
            ,
            enabled = isEnabled,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.End,
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
            ),
            lineLimits = TextFieldLineLimits.SingleLine,
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
                        if (value !in 0..255) {
                            revertAllChanges()
                        } else {
                            onSliderChange(value.toFloat() / 255)
                        }
                    } catch (_: NumberFormatException) {
                        revertAllChanges()
                    }
                }
            },
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 480)
@Composable
fun PaletteEditSectionPreview() {
    SimplePixelTheme {
        PaletteEditSection(
            modifier = Modifier.wrapContentSize(),
            paletteEditState = PaletteEditState(),
        )
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
fun PaletteColorComponentPreview() {
    SimplePixelTheme {
        PaletteColorComponent(
            label = "R",
            textFieldState = rememberTextFieldState("25"),
            onSliderChange = {},
        )
    }
}

private fun Float.toColorComponentString(): String {
    return (this * 255).roundToInt().toString()
}