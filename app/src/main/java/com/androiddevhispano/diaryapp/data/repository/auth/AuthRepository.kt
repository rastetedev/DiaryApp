package com.androiddevhispano.diaryapp.data.repository.auth

import arrow.core.Either

interface AuthRepository {

    suspend fun authenticate(tokenId: String): Either<String, Boolean>

    suspend fun logout()
}