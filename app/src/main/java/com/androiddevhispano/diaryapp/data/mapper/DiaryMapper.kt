package com.androiddevhispano.diaryapp.data.mapper

import com.androiddevhispano.diaryapp.core_ui.models.Mood
import com.androiddevhispano.diaryapp.data.models.Diary
import com.androiddevhispano.diaryapp.data.utils.toInstant
import com.androiddevhispano.diaryapp.data.utils.toRealmInstant
import com.androiddevhispano.diaryapp.feature.home.HomeViewModel
import io.realm.kotlin.ext.toRealmList
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.ZoneId
import com.androiddevhispano.diaryapp.core_ui.models.Diary as DiaryModel

fun Diary.toDiaryCard() =
    HomeViewModel.DiaryCard(
        diaryId = this._id.toHexString(),
        mood = Mood.valueOf(this.mood),
        description = this.description,
        date = this.date.toInstant().atZone(ZoneId.systemDefault()),
        imagesUrl = this.images.toList()
    )


fun Diary.toDiaryModel() =
    DiaryModel(
        id = this._id.toHexString(),
        ownerId = this.ownerId,
        mood = Mood.valueOf(this.mood),
        title = this.title,
        description = this.description,
        date = this.date.toInstant(),
        imagesUrl = this.images.toList()
    )

fun createDiary(
    id: String? = null,
    title: String,
    description: String,
    mood: Mood,
    date: Instant,
    imagesUrl: List<String>
): Diary {
    return Diary().apply {
        this.title = title
        this.description = description
        this.mood = mood.name
        this.date = date.toRealmInstant()
        this.images = imagesUrl.toRealmList()

        id?.let { _id = ObjectId(it) }
    }
}

