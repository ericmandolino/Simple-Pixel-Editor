package com.swirlfist.simplepixel.presentation.main.section

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.swirlfist.simplepixel.R
import com.swirlfist.simplepixel.domain.model.PaletteModel

sealed interface ActionButtonType {

    data object UndoActionButtonType : ActionIconButtonType(
        icon = R.drawable.ic_actions_section_undo_24,
        contentDescription = R.string.cd_actions_section_button_undo,
    ), ActionButtonType

    data object RedoActionButtonType : ActionIconButtonType(
        icon = R.drawable.ic_actions_section_redo_24,
        contentDescription = R.string.cd_actions_section_button_redo,
    ), ActionButtonType

    data object ZoomInActionButtonType : ActionIconButtonType(
        icon = R.drawable.ic_actions_section_zoom_in_24,
        contentDescription = R.string.cd_actions_section_button_zoom_in,
    ), ActionButtonType

    data object ZoomOutActionButtonType : ActionIconButtonType(
        icon = R.drawable.ic_actions_section_zoom_out_24,
        contentDescription = R.string.cd_actions_section_button_zoom_out,
    ), ActionButtonType

    data object OpenPaletteActionButtonType : ActionIconButtonType(
        icon = R.drawable.ic_actions_section_open_palette_24dp,
        contentDescription = R.string.cd_actions_section_button_open_palette,
    ), ActionButtonType

    data class PickPaletteColorActionButtonType(
        val paletteIndex: Int,
        val palette: PaletteModel,
    ) : ActionButtonType
}

abstract class ActionIconButtonType(
    @DrawableRes val icon: Int,
    @StringRes val contentDescription: Int,
)