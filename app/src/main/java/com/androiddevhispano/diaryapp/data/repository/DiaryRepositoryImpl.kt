package com.androiddevhispano.diaryapp.data.repository

import com.androiddevhispano.diaryapp.BuildConfig
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.utils.RequestState
import com.androiddevhispano.diaryapp.utils.toInstant
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.log.LogLevel
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.query.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId
import java.time.LocalDate
import java.time.ZoneId

object DiaryRepositoryImpl : DiaryRepository {

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
                .log(LogLevel.ALL)
                .build()
            realm = Realm.open(config)
        }
    }

    override fun getAllDiaries(): Flow<RequestState<Map<LocalDate, List<Diary>>>> {
        return if (user != null) {
            try {
                realm.query<Diary>(
                    query = "ownerId == $0", user.id
                )
                    .sort(property = "date", sortOrder = Sort.DESCENDING)
                    .asFlow()
                    .map { result ->
                        RequestState.Success(
                            data = result.list
                                .groupBy {
                                    it.date.toInstant().atZone(ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                        )
                    }
            } catch (e: Exception) {
                flow { emit(RequestState.Error(e)) }
            }
        } else {
            flow { emit(RequestState.Error(UserNotAuthenticatedException())) }
        }
    }

    override fun getDiaryById(diaryId: ObjectId): RequestState<Diary> {
        return if (user != null) {
            try {
                val diary = realm.query<Diary>(
                    query = "_id == $0", diaryId
                )
                    .find()
                    .first()
                RequestState.Success(diary)
            } catch (e: Exception) {
                RequestState.Error(e)
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun insertDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                try {
                    val diaryInserted = copyToRealm(diary.apply { ownerId = user.id })
                    RequestState.Success(diaryInserted)
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun updateDiary(diary: Diary): RequestState<Diary> {
        return if (user != null) {
            realm.write {
                val diaryQueried = query<Diary>(query = "_id == $0", diary._id)
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
                    RequestState.Success(diaryQueried)
                } else {
                    RequestState.Error(DiaryNotExistException())
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }

    override suspend fun deleteDiary(diaryId: ObjectId): RequestState<Unit> {
        return if (user != null) {
            realm.write {
                try {
                    val diaryQueried =
                        query<Diary>(query = "_id == $0 AND ownerId == $1", diaryId, user.id)
                            .first()
                            .find()
                    if (diaryQueried != null) {
                        delete(diaryQueried)
                        RequestState.Success(Unit)
                    } else {
                        RequestState.Error(DiaryNotExistException())
                    }
                } catch (e: Exception) {
                    RequestState.Error(e)
                }
            }
        } else {
            RequestState.Error(UserNotAuthenticatedException())
        }
    }
}


private class UserNotAuthenticatedException : Exception("User is not logged in")
private class DiaryNotExistException : Exception("Queried Diary does not exist.")