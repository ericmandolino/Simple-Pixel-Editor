package com.swirlfist.simplepixel.presentation.main.section

sealed interface CanvasSectionEvent {
    data class PixelTap(val x: Int, val y: Int) : CanvasSectionEvent
}