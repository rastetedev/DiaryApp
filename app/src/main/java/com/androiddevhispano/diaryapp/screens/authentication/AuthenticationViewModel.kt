package com.androiddevhispano.diaryapp.screens.authentication

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.BuildConfig
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.GoogleAuthType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthenticationViewModel : ViewModel() {

    var loadingState = mutableStateOf(false)
        private set

    var authenticatedState by mutableStateOf(false)
        private set

    fun setLoading(loading: Boolean) {
        loadingState.value = loading
    }

    fun signInWithMongoDBAtlas(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val result = withContext(Dispatchers.IO) {
                    App.create(BuildConfig.MONGO_APP_ID).login(
                        Credentials.google(
                            token = tokenId,
                            type = GoogleAuthType.ID_TOKEN
                        )
                    ).loggedIn
                }
                if (result) {
                    onSuccess()
                    delay(600)
                    authenticatedState = true
                }
            } catch (e: Exception) {
                onError(e)
            }
        }
    }

    fun signInWithFirebase(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val credential = GoogleAuthProvider.getCredential(tokenId, null)
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    onSuccess()
                } else {
                    it.exception?.let { onError }
                }
            }
    }
}