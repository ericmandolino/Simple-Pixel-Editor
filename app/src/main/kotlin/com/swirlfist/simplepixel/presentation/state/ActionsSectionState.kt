package com.swirlfist.simplepixel.presentation.state

import com.swirlfist.simplepixel.domain.model.ActionModel
import com.swirlfist.simplepixel.presentation.section.ActionButtonType

data class ActionsSectionState(
    val actionModels: Map<ActionButtonType, ActionModel> = emptyMap()
)
