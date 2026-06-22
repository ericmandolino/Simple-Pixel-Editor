package com.swirlfist.simplepixel.presentation.main.state

import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType
import com.swirlfist.simplepixel.domain.model.ActionModel

data class ActionsSectionState(
    val actionModels: Map<ActionButtonType, ActionModel> = emptyMap()
)
