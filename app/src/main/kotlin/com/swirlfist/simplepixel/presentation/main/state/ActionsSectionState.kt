package com.swirlfist.simplepixel.presentation.main.state

import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType

data class ActionsSectionState(
    val actionModels: Map<ActionButtonType, ActionModel> = emptyMap()
)
