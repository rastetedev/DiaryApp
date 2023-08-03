package com.androiddevhispano.diaryapp.data.repository.diary_manager

import android.net.Uri
import arrow.core.Either
import arrow.core.Option
import com.androiddevhispano.diaryapp.data.models.Diary
import com.androiddevhispano.diaryapp.data.repository.DomainException
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

interface DiaryManager {

    suspend fun fetchRemoteImageUriList(remoteImagePathList: List<String>): Either<String, List<Uri>>


    /** HOME **/
    suspend fun getAllDiaries(): Flow<Either<DomainException, List<Diary>>>

    suspend fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Either<DomainException, List<Diary>>>

    suspend fun deleteAllDiaries(): Option<DomainException>


    /** WRITE **/

    suspend fun getDiaryById(diaryId: String): Either<DomainException, Diary>

    suspend fun upsertDiary(
        diaryId: String?,
        diary: Diary,
        imageUriList: List<Uri>,
        imageRemotePathList: List<String>,
        imagesToRemoveRemotePathList: List<String>
    ): Option<DomainException>

    suspend fun deleteDiary(
        diaryId: String,
        imagesToRemoveRemotePathList: List<String>
    ): Option<DomainException>

}