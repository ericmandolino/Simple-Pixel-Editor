package com.swirlfist.simplepixel.domain.error

class MagicWandSelectionError(
    val innerException: Throwable
) : Throwable()