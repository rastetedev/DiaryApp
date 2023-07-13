package com.androiddevhispano.diaryapp.utils

import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

fun simpleTimeFormatter(locale: Locale = Locale.getDefault()): DateTimeFormatter =
    DateTimeFormatter.ofPattern("hh:mm a", locale)
        .withZone(ZoneId.systemDefault())