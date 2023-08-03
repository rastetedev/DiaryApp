package com.androiddevhispano.diaryapp.data.repository.auth

import arrow.core.Either
import arrow.core.raise.either
import com.androiddevhispano.diaryapp.data.datasource.remote.auth.AuthRemoteDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authFirebaseRemoteDataSource: AuthRemoteDataSource,
    private val mongoRemoteDataSource: AuthRemoteDataSource
) : AuthRepository {

    override fun isUserSignIn(): Boolean {
        return authFirebaseRemoteDataSource.isUserSignIn() && mongoRemoteDataSource.isUserSignIn()
    }

    override suspend fun authenticate(tokenId: String): Either<String, Boolean> {
        return either {
            authFirebaseRemoteDataSource.authenticate(tokenId).bind()
            mongoRemoteDataSource.authenticate(tokenId).bind()
        }
    }

    override suspend fun logout() {
        authFirebaseRemoteDataSource.logout()
        mongoRemoteDataSource.logout()
    }
}