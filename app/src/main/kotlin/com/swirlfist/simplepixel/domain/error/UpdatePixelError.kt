package com.swirlfist.simplepixel.domain.error

class UpdatePixelError(
    val innerException: Throwable
) : Throwable()