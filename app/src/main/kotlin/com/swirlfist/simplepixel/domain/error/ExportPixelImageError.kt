package com.swirlfist.simplepixel.domain.error

class ExportPixelImageError(
    val innerException: Throwable
) : Throwable()