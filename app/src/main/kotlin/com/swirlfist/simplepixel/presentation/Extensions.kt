package com.swirlfist.simplepixel.presentation

import androidx.compose.ui.graphics.Color
import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.domain.model.BaseButtonGroupActionModel
import com.swirlfist.simplepixel.domain.model.EMPTY_PIXEL_PALETTE_INDEX
import com.swirlfist.simplepixel.domain.model.PaletteModel
import com.swirlfist.simplepixel.domain.model.PixelImageModel
import com.swirlfist.simplepixel.domain.model.PixelMatrixModel
import com.swirlfist.simplepixel.domain.model.PixelModel
import com.swirlfist.simplepixel.presentation.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.state.ActionsSectionState

fun List<Color>.invertColors(): List<Color> = map { color ->
    color.invert()
}

fun Color.invert() = copy(
    red = 1F - red,
    green = 1F - green,
    blue = 1F - blue
)

fun List<Color>.getColor(pixel: PixelModel): Color? {
    val paletteIndex = pixel.paletteIndex

    return if (paletteIndex in indices) {
        get(paletteIndex)
    } else {
        null
    }
}

fun PaletteModel.createPaletteButtons(): List<ActionModel.ButtonActionModel> {
    return colors.indices.map { index ->
        ActionModel.ButtonActionModel(
            actionType = ActionButtonType.PickPaletteColorActionButtonType(
                paletteIndex = index,
                palette = this,
            ),
            isSelected = index == 0,
        )
    } + ActionModel.ButtonActionModel(
            actionType = ActionButtonType.InkEraserActionButtonType,
    )
}

fun PixelImageModel.deletePaletteColor(
    paletteIndex: Int
): PixelImageModel {
    val currentPaletteColors = paletteModel.colors

    if (paletteIndex !in currentPaletteColors.indices) {
        return this
    }

    val newPalette = PaletteModel(
        colors = currentPaletteColors.toMutableList().apply { removeAt(paletteIndex) }.toList()
    )
    val currentPixelMatrixContent = pixelMatrixModel.content
    val newPixelMatrixContent = mutableListOf<List<PixelModel>>()

    currentPixelMatrixContent.forEach { row ->
        val newRow = mutableListOf<PixelModel>()
        row.forEach { pixel ->
            val currentPaletteIndex = pixel.paletteIndex
            val newPixel = when {
                currentPaletteIndex == paletteIndex -> PixelModel(
                    paletteIndex = EMPTY_PIXEL_PALETTE_INDEX,
                )
                currentPaletteIndex > paletteIndex -> PixelModel(
                    paletteIndex = currentPaletteIndex- 1
                )
                else -> pixel
            }
            newRow.add(newPixel)
        }
        newPixelMatrixContent.add(newRow)
    }

    return PixelImageModel(
        pixelMatrixModel = PixelMatrixModel(newPixelMatrixContent.toList()),
        paletteModel = newPalette,
    )
}

fun ActionsSectionState.isSelected(
    actionButtonType: ActionButtonType
): Boolean {
    return actionModels[actionButtonType]?.let { actionModel ->
        when (actionModel) {
            is ActionModel.ButtonActionModel -> actionModel.isSelected
            is BaseButtonGroupActionModel -> false
        }
    } ?: isSelectedChild(actionButtonType)
}

private fun ActionsSectionState.isSelectedChild(
    actionButtonType: ActionButtonType
): Boolean {
    actionModels.values.forEach { actionModel ->
        (actionModel as? BaseButtonGroupActionModel)?.let { buttonGroup ->
            buttonGroup.childButtonActionModels.forEach { childButton ->
                if (childButton.actionType == actionButtonType) {
                    return childButton.isSelected
                }
            }
        }
    }

    return false
}

fun BaseButtonGroupActionModel.isSelected(
    actionButtonType: ActionButtonType
): Boolean {
    return childButtonActionModels.firstOrNull{ actionModel ->
        actionModel.actionType == actionButtonType
    }?.isSelected ?: false
}
