package com.androiddevhispano.diaryapp.mapper

import com.androiddevhispano.diaryapp.data.utils.toInstant
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.models.Mood
import com.androiddevhispano.diaryapp.screens.home.HomeViewModel
import java.time.ZoneId

fun Diary.toDiaryCard() =
        HomeViewModel.DiaryCard(
                id = this._id.toHexString(),
                mood = Mood.valueOf(this.mood),
                description = this.description,
                date = this.date.toInstant().atZone(ZoneId.systemDefault()),
                imagesUrl = this.images.toList()
        )