package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType

sealed interface ActionModel {
    val actionType: ActionButtonType
    val isEnabled: Boolean

    data class ButtonActionModel(
        override val actionType: ActionButtonType,
        override val isEnabled: Boolean = true,
        val isSelected: Boolean = false,
    ) : ActionModel

    data class ButtonGroupActionModel(
        override val actionType: ActionButtonType,
        override val isEnabled: Boolean = true,
        val childButtonActionModels: List<ButtonActionModel> = listOf(),
    ) : ActionModel

    data class SelectableButtonGroupActionModel(
        override val actionType: ActionButtonType,
        override val isEnabled: Boolean = true,
        val childButtonActionModels: List<ButtonActionModel> = listOf(),
    ) : ActionModel
}

