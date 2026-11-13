package com.swirlfist.simplepixel.domain.error

class SelectionToFloatingImageError(
    val innerException: Throwable
) : Throwable()