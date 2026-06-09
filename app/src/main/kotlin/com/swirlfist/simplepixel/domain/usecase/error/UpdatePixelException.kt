package com.swirlfist.simplepixel.domain.usecase.error

class UpdatePixelException(
    val innerException: Throwable
) : Throwable()