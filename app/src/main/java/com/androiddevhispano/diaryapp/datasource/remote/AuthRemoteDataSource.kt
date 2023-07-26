package com.androiddevhispano.diaryapp.datasource.remote

import arrow.core.Either

interface AuthRemoteDataSource {
    suspend fun authenticate(tokenId: String): Either<String, Boolean>

    suspend fun logout()
}