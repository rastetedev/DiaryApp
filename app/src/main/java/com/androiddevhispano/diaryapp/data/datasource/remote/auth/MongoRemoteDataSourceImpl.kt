package com.androiddevhispano.diaryapp.data.datasource.remote.auth

import arrow.core.Either
import com.androiddevhispano.diaryapp.BuildConfig
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import javax.inject.Inject

class MongoRemoteDataSourceImpl @Inject constructor() : AuthRemoteDataSource {

    override fun isUserSignIn(): Boolean {
        val user = App.create(BuildConfig.MONGO_APP_ID).currentUser
        return user != null && user.loggedIn
    }

    override suspend fun authenticate(tokenId: String): Either<String, Boolean> {
        return try {
            val isLoggedIn = App.create(BuildConfig.MONGO_APP_ID).login(
                Credentials.google(
                    token = tokenId,
                    type = GoogleAuthType.ID_TOKEN
                )
            ).loggedIn
            Either.Right(isLoggedIn)
        } catch (e: Exception) {
            Either.Left(e.message ?: "")
        }
    }

    override suspend fun logout() {
        App.create(BuildConfig.MONGO_APP_ID).currentUser?.logOut()
    }
}