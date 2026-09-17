package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.domain.model.ActionModel.ButtonActionModel
import com.swirlfist.simplepixel.presentation.section.ActionButtonType

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
        override val childButtonActionModels: List<ButtonActionModel> = listOf(),
    ) : BaseButtonGroupActionModel

    data class SelectableButtonGroupActionModel(
        override val actionType: ActionButtonType,
        override val isEnabled: Boolean = true,
        override val childButtonActionModels: List<ButtonActionModel> = listOf(),
    ) : BaseButtonGroupActionModel
}

sealed interface BaseButtonGroupActionModel : ActionModel {
    val childButtonActionModels: List<ButtonActionModel>
}

