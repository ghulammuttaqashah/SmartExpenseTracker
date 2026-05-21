package com.example.madproject.util

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object DateUtils {
    private val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy")
    private val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    fun formatDate(millis: Long): String {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return dateFormatter.format(date)
    }

    fun formatMonth(millis: Long): String {
        val date = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
        return monthFormatter.format(date)
    }

    fun currentMonthRange(): Pair<Long, Long> {
        val now = LocalDate.now()
        return monthRange(now)
    }

    fun monthRange(date: LocalDate): Pair<Long, Long> {
        val start = date.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
        val end = LocalDateTime.of(
            date.withDayOfMonth(date.lengthOfMonth()),
            java.time.LocalTime.of(23, 59, 59, 999_000_000)
        ).atZone(ZoneId.systemDefault()).toInstant()
        return start.toEpochMilli() to end.toEpochMilli()
    }
}
