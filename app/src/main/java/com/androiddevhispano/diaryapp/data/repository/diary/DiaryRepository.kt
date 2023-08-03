package com.androiddevhispano.diaryapp.data.repository.diary

import arrow.core.Either
import arrow.core.Option
import com.androiddevhispano.diaryapp.data.repository.DomainException
import com.androiddevhispano.diaryapp.data.models.Diary
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface DiaryRepository {

    /** HOME **/
    fun getAllDiaries(): Flow<Either<DomainException, List<Diary>>>

    fun getFilteredDiariesByDate(zonedDateTime: ZonedDateTime): Flow<Either<DomainException, List<Diary>>>

    suspend fun deleteAllDiaries(): Option<DomainException>

    /** WRITE **/
    fun getDiaryById(diaryId: String): Either<DomainException, Diary>

    suspend fun insertDiary(diary: Diary): Option<DomainException>

    suspend fun updateDiary(diaryId: String, diary: Diary): Option<DomainException>

    suspend fun deleteDiary(diaryId: String): Option<DomainException>
}