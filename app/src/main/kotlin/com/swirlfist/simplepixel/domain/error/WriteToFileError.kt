package com.swirlfist.simplepixel.domain.error

class WriteToFileError(
    val innerException: Throwable
) : Throwable()