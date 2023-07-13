package com.androiddevhispano.diaryapp.data

import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.utils.RequestState
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface MongoRepository {

    fun configureRealm()

    fun getAllDiaries(): Flow<RequestState<Map<LocalDate, List<Diary>>>>
}