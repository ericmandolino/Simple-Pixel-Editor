package com.swirlfist.simplepixel.presentation.section

sealed interface CanvasSectionEvent {
    data class PixelTap(val x: Int, val y: Int) : CanvasSectionEvent
    data class ZoomUpdate(val zoom: Float) : CanvasSectionEvent
}