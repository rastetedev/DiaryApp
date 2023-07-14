package com.androiddevhispano.diaryapp.data

import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.utils.RequestState
import kotlinx.coroutines.flow.Flow
import org.mongodb.kbson.ObjectId
import java.time.LocalDate

interface MongoRepository {

    fun configureRealm()

    fun getAllDiaries(): Flow<RequestState<Map<LocalDate, List<Diary>>>>

    fun getDiaryById(diaryId: ObjectId) : RequestState<Diary>

    suspend fun insertDiary(diary: Diary) : RequestState<Diary>
}