package com.androiddevhispano.diaryapp.data

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
import java.time.LocalDate
import java.time.ZoneId

object MongoDB : MongoRepository {

    private val app = App.Companion.create(BuildConfig.MONGO_APP_ID)
    private val user = app.currentUser

    private lateinit var realm: Realm

    init {
        configureRealm()
    }

    override fun configureRealm() {
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
}

private class UserNotAuthenticatedException : Exception("User is not logged in")