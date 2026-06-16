package com.swirlfist.simplepixel.domain.error

class OpenPixelImageError(
    val innerException: Throwable
) : Throwable()