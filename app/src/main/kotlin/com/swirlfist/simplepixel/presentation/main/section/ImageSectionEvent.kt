package com.swirlfist.simplepixel.presentation.main.section

sealed interface ImageSectionEvent {
    data class PixelTap(val x: Int, val y: Int) : ImageSectionEvent
}