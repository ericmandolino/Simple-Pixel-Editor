package com.swirlfist.simplepixel.domain.error

class UpdateRectangleSelectionError(
    val innerException: Throwable
) : Throwable()