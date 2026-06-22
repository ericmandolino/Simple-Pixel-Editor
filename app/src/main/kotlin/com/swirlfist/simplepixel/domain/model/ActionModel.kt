package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType

sealed interface ActionModel {

    data class ButtonActionModel(
        val actionType: ActionButtonType,
        val isEnabled: Boolean = true,
    ) : ActionModel

    data class SelectableButtonGroupActionModel(
        val actionType: ActionButtonType,
        val isEnabled: Boolean = true,
        val childButtonActionModels: List<ButtonActionModel> = listOf(),
        val selectedIndex: Int = -1,
    ) : ActionModel
}

