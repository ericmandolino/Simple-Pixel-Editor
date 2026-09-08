package com.swirlfist.simplepixel.domain.error

class DeletePalettePresetError(
    val innerException: Throwable
) : Throwable()