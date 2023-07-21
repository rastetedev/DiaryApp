package com.androiddevhispano.diaryapp.ui.utils

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

fun Instant.toLocalDate(): LocalDate = this.atZone(ZoneId.systemDefault()).toLocalDate()

fun Instant.toLocalTime(): LocalTime = this.atZone(ZoneId.systemDefault()).toLocalTime()