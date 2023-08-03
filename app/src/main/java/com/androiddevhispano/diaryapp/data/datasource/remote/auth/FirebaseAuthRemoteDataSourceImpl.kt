package com.androiddevhispano.diaryapp.data.datasource.remote.auth

import arrow.core.Either
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class FirebaseAuthRemoteDataSourceImpl @Inject constructor() : AuthRemoteDataSource {

    override fun isUserSignIn() =
        Firebase.auth.currentUser != null

    override suspend fun authenticate(tokenId: String): Either<String, Boolean> {
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        return suspendCoroutine { continuation ->
            FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener { result ->
                    if (result.isSuccessful) {
                        continuation.resume(Either.Right(true))
                    } else {
                        result.exception?.let { continuation.resume(Either.Left(it.message ?: "")) }
                            .run { continuation.resume(Either.Right(false)) }
                    }
                }
        }
    }

    override suspend fun logout() {
        Firebase.auth.signOut()
    }
}