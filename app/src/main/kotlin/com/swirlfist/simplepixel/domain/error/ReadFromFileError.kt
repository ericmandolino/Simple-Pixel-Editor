package com.swirlfist.simplepixel.domain.error

class ReadFromFileError(
    val innerException: Throwable
) : Throwable()