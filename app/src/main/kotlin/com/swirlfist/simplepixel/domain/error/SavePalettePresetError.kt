package com.swirlfist.simplepixel.domain.error

class SavePalettePresetError(
    val innerException: Throwable
) : Throwable()