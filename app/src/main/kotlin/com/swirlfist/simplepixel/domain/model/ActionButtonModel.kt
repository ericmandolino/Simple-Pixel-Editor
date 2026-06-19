package com.swirlfist.simplepixel.domain.model

import com.swirlfist.simplepixel.presentation.main.section.ActionButtonType

data class ActionButtonModel(
    val actionType: ActionButtonType,
    val enabled: Boolean,
    val childActionTypes: List<ActionButtonType> = listOf(),
)