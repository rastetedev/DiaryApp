package com.androiddevhispano.diaryapp.core_ui.models

import java.time.Instant

data class Diary (
    val id: String = "",
    var ownerId: String = "",
    var mood: Mood = Mood.Neutral,
    var title: String = "",
    var description: String = "",
    var imagesUrl: List<String> = emptyList(),
    var date: Instant = Instant.now()
)