package com.androiddevhispano.diaryapp.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun simpleTimeFormatter(locale: Locale = Locale.getDefault()): DateTimeFormatter =
    DateTimeFormatter.ofPattern("hh:mm a", locale)
        .withZone(ZoneId.systemDefault())


fun dateSelectedFormatter(locale: Locale = Locale.getDefault()): DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a", locale)
        .withZone(ZoneId.systemDefault())

fun Instant.toLocalDate(): LocalDate = this.atZone(ZoneId.systemDefault()).toLocalDate()

fun Instant.toLocalTime(): LocalTime = this.atZone(ZoneId.systemDefault()).toLocalTime()