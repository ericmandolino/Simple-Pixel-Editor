package com.swirlfist.simplepixel.presentation.main.state

import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import com.swirlfist.simplepixel.presentation.model.ActionButtonModel

data class ActionsSectionState(
    val actionButtonModels: Map<ActionButtonType, ActionButtonModel> = emptyMap()
)
