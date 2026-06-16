package com.swirlfist.simplepixel.domain.model

import androidx.annotation.ColorLong
import kotlinx.serialization.Serializable

@Serializable
data class PaletteModel(
    @ColorLong val colors: List<Long>
)