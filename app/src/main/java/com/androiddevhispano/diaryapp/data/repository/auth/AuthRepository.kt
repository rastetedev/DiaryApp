package com.androiddevhispano.diaryapp.data.repository.auth

import arrow.core.Either

interface AuthRepository {

    fun isUserSignIn(): Boolean

    suspend fun authenticate(tokenId: String): Either<String, Boolean>

    suspend fun logout()
}