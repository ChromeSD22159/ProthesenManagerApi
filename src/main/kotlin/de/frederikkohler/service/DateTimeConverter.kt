package de.frederikkohler.service

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DateTimeConverter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

    fun parseStringToDate(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, formatter)
    }

    fun formatDateToString(dateTime: LocalDateTime): String {
        return dateTime.format(formatter)
    }
}