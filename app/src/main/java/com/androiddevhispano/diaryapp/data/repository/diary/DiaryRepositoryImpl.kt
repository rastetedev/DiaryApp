package com.androiddevhispano.diaryapp.data.repository.diary

import arrow.core.Either
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.androiddevhispano.diaryapp.BuildConfig
import com.androiddevhispano.diaryapp.data.models.Diary
import com.androiddevhispano.diaryapp.data.repository.DomainException
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.RealmLog
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZonedDateTime

class DiaryRepositoryImpl : DiaryRepository {

    private val app = App.Companion.create(BuildConfig.MONGO_APP_ID)
    private val user = app.currentUser

    private lateinit var realm: Realm

    init {
        configureRealm()
    }

    private fun configureRealm() {
        if (user != null) {
            val config = SyncConfiguration.Builder(user, setOf(Diary::class))
                .initialSubscriptions { subscription ->
                    add(
                        query = subscription.query<Diary>("ownerId == $0", user.id),
                        name = "User's Diaries"
                    )
                }
                .build()
            RealmLog.addDefaultSystemLogger()
            realm = Realm.open(config)
        }
    }

    /** HOME **/

    override fun getAllDiaries(): Flow<Either<DomainException, List<Diary>>> {
        return if (user != null) {
            try {
                realm.query<Diary>(
                    query = "ownerId == $0", user.id
                )
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        Either.Right(result.list)
                    }
            } catch (e: Exception) {
                flow { emit(Either.Left(DomainException.GeneralException(e.message))) }
            }
        } else {
            flow { emit(Either.Left(DomainException.UserNotAuthenticatedException())) }
        }
    }

    override fun getFilteredDiariesByDate(zonedDateTime: ZonedDateTime): Flow<Either<DomainException, List<Diary>>> {
        return if (user != null) {
            try {
                realm.query<Diary>(
                    query = "ownerId == $0 AND date < $1 AND date > $2",
                    user.id,
                    RealmInstant.from(
                        LocalDateTime.of(
                            zonedDateTime.toLocalDate().plusDays(1),
                            LocalTime.MIDNIGHT
                        ).toEpochSecond(zonedDateTime.offset), 0

                    ),
                    RealmInstant.from(
                        LocalDateTime.of(
                            zonedDateTime.toLocalDate(),
                            LocalTime.MIDNIGHT
                        ).toEpochSecond(zonedDateTime.offset), 0

                    )
                )
                    .asFlow()
                    .map { result ->
                        Either.Right(result.list)
                    }
            } catch (e: Exception) {
                flow { emit(Either.Left(DomainException.GeneralException(e.message))) }
            }
        } else {
            flow { emit(Either.Left(DomainException.UserNotAuthenticatedException())) }
        }
    }

    override suspend fun deleteAllDiaries(): Option<DomainException> {
        return if (user != null) {
            realm.write {
                val diaries = query<Diary>("ownerId == $0", user.id).find()
                try {
                    delete(diaries)
                    None
                } catch (e: Exception) {
                    Some(DomainException.GeneralException(e.message))
                }
            }
        } else {
            Some(DomainException.UserNotAuthenticatedException())
        }
    }

    /** WRITE **/

    override fun getDiaryById(diaryId: String): Either<DomainException, Diary> {
        return if (user != null) {
            try {
                val diary = realm.query<Diary>(
                    query = "_id == $0", ObjectId(diaryId)
                )
                    .find()
                    .first()
                Either.Right(diary)
            } catch (e: Exception) {
                Either.Left(DomainException.GeneralException(e.message))
            }
        } else {
            Either.Left(DomainException.UserNotAuthenticatedException())
        }
    }

    override suspend fun insertDiary(diary: Diary): Either<DomainException, Diary> {
        return if (user != null) {
            realm.write {
                try {
                    copyToRealm(diary.apply { ownerId = user.id })
                    Either.Right(diary)
                } catch (e: Exception) {
                    Either.Left(DomainException.GeneralException(e.message))
                }
            }
        } else {
            Either.Left(DomainException.UserNotAuthenticatedException())
        }
    }

    override suspend fun updateDiary(diaryId: String, diary: Diary): Either<DomainException, Diary> {
        return if (user != null) {
            realm.write {
                val diaryQueried = query<Diary>(query = "_id == $0", ObjectId(diaryId))
                    .first()
                    .find()
                if (diaryQueried != null) {
                    with(diaryQueried) {
                        title = diary.title
                        description = diary.description
                        mood = diary.mood
                        images = diary.images
                        date = diary.date
                    }
                    Either.Right(diary)
                } else {
                    Either.Left(DomainException.DiaryNotExistException())
                }
            }
        } else {
            Either.Left(DomainException.UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteDiary(diaryId: String): Option<DomainException> {
        return if (user != null) {
            realm.write {
                try {
                    val diaryQueried =
                        query<Diary>(
                            query = "_id == $0 AND ownerId == $1",
                            ObjectId(diaryId),
                            user.id
                        )
                            .first()
                            .find()
                    if (diaryQueried != null) {
                        delete(diaryQueried)
                        None
                    } else {
                        Some(DomainException.DiaryNotExistException())
                    }
                } catch (e: Exception) {
                    Some(DomainException.GeneralException(e.message))
                }
            }
        } else {
            Some(DomainException.UserNotAuthenticatedException())
        }
    }
}
