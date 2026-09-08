package com.swirlfist.simplepixel.domain.error

class LoadPalettePresetsError(
    val innerException: Throwable
) : Throwable()