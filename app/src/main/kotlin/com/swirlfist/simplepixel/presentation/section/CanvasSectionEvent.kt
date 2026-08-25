package com.swirlfist.simplepixel.presentation.section

sealed interface CanvasSectionEvent {
    data class PixelTap(val x: Int, val y: Int) : CanvasSectionEvent
}