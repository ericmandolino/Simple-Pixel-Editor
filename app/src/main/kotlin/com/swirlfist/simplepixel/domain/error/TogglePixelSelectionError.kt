package com.swirlfist.simplepixel.domain.error

class TogglePixelSelectionError(
    val innerException: Throwable
) : Throwable()