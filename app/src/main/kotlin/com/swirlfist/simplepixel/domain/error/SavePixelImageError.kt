package com.swirlfist.simplepixel.domain.error

class SavePixelImageError(
    val innerException: Throwable
) : Throwable()