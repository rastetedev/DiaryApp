package com.androiddevhispano.diaryapp.data.datasource.remote.auth

import arrow.core.Either

interface AuthRemoteDataSource {

    fun isUserSignIn(): Boolean

    suspend fun authenticate(tokenId: String): Either<String, Boolean>

    suspend fun logout()
}