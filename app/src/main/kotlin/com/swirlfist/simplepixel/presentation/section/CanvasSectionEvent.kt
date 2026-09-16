package com.swirlfist.simplepixel.presentation.section

sealed interface CanvasSectionEvent {
    data class PixelVisited(val x: Int, val y: Int) : CanvasSectionEvent
    object PixelVisitEnd : CanvasSectionEvent
    data class ZoomUpdate(val zoom: Float) : CanvasSectionEvent
}