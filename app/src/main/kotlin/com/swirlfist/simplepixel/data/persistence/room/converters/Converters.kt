package com.swirlfist.simplepixel.data.persistence.room.converters

import androidx.room3.ColumnTypeConverter

private const val LONG_LIST_SEPARATOR = ","

object Converters {
    @ColumnTypeConverter
    fun longListToString(value: List<Long>): String {
        val stringBuilder = StringBuilder()

        value.forEach { longValue ->
            stringBuilder.append(longValue)
            stringBuilder.append(LONG_LIST_SEPARATOR)
        }

        return stringBuilder.toString()
    }

    @ColumnTypeConverter
    fun longListFromLongString(value: String): List<Long> {
        return value.split(LONG_LIST_SEPARATOR).filter { value ->
            value.isNotBlank()
        }.map { value ->
            value.toLong()
        }
    }
}