package com.androiddevhispano.diaryapp.data.repository.diary_manager

import android.net.Uri
import arrow.core.Either
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.raise.either
import arrow.core.raise.zipOrAccumulate
import com.androiddevhispano.diaryapp.data.models.Diary
import com.androiddevhispano.diaryapp.data.repository.DomainException
import com.androiddevhispano.diaryapp.data.repository.diary.DiaryRepository
import com.androiddevhispano.diaryapp.data.repository.image.ImageRepository
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import java.time.ZonedDateTime

class DiaryManagerImpl(
    private val diaryRepository: DiaryRepository,
    private val imageRepository: ImageRepository
) : DiaryManager {

    override suspend fun fetchRemoteImageUriList(remoteImagePathList: List<String>): Either<String, List<Uri>> {
        val remoteImageUriList = mutableListOf<Uri>()
        try {
            remoteImagePathList.forEach { remoteImagePath ->
                if (remoteImagePath.trim().isNotEmpty()) {
                    val imageUri = FirebaseStorage.getInstance().reference
                        .child(remoteImagePath.trim())
                        .downloadUrl
                        .await()
                    remoteImageUriList.add(imageUri)
                }
            }
        } catch (e: Exception) {
            return Either.Left(e.message ?: "")
        }
        return Either.Right(remoteImageUriList)
    }

    /** HOME **/
    override suspend fun getAllDiaries(): Flow<Either<DomainException, List<Diary>>> {
        return diaryRepository.getAllDiaries()
    }

    override suspend fun getFilteredDiaries(zonedDateTime: ZonedDateTime): Flow<Either<DomainException, List<Diary>>> {
        return diaryRepository.getFilteredDiariesByDate(zonedDateTime)
    }

    override suspend fun deleteAllDiaries(): Option<DomainException> {

        return imageRepository.deleteAllImages().fold(
            ifEmpty = {
                diaryRepository.deleteAllDiaries().fold(
                    ifEmpty = { None },
                    ifSome = { Some(it) }
                )
            },
            ifSome = {
                Some(it)
            }
        )
    }

    /** WRITE **/

    override suspend fun getDiaryById(diaryId: String): Either<DomainException, Diary> {
        return diaryRepository.getDiaryById(diaryId)
    }

    override suspend fun upsertDiary(
        diaryId: String?,
        diary: Diary,
        imageUriList: List<Uri>,
        imageRemotePathList: List<String>,
        imagesToRemoveRemotePathList: List<String>
    ): Either<Nel<DomainException>, Unit> {
        val upsertResult = if (diaryId.isNullOrEmpty()) {
            diaryRepository.insertDiary(diary)
        } else {
            diaryRepository.updateDiary(diaryId, diary)
        }
        return upsertResult.fold(
            { exception ->
                Either.Left(NonEmptyList.fromListUnsafe(listOf(exception)))
            },
            {
                either {
                    zipOrAccumulate(
                        {
                            imageRepository.uploadImages(imageUriList, imageRemotePathList)
                                .bind()
                        },
                        {
                            imageRepository.deleteImages(imagesToRemoveRemotePathList).bind()

                        }
                    ) { _, _ ->
                        Either.Right(Unit)
                    }
                }
            }
        )
    }

    override suspend fun deleteDiary(
        diaryId: String,
        imagesToRemoveRemotePathList: List<String>
    ): Either<DomainException, Unit> {
        return diaryRepository.deleteDiary(diaryId)
            .fold(
                { imageRepository.deleteImages(imagesToRemoveRemotePathList) },
                { Either.Left(it) }
            )
    }
}