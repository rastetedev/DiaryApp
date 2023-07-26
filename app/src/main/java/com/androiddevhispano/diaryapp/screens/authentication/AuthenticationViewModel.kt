package com.androiddevhispano.diaryapp.screens.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.Either
import com.androiddevhispano.diaryapp.data.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class AuthenticationViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authenticationUiState = MutableStateFlow(AuthenticationUiState())
    val authenticatedState = _authenticationUiState.asStateFlow()

    fun setLoading(loadingState: Boolean) {
        _authenticationUiState.update {
            it.copy(
                signInButtonIsLoading = loadingState
            )
        }
    }

    fun signIn(
        tokenId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                when (val result = authRepository.authenticate(tokenId)) {
                    is Either.Left -> onError(result.value)
                    is Either.Right -> {
                        if (result.value) {
                            onSuccess()
                            delay(600)
                            _authenticationUiState.update {
                                it.copy(
                                    isAuthenticated = true
                                )
                            }
                        } else {
                            onError("Error trying to authenticate")
                        }
                    }
                }
            }
        }
    }

    data class AuthenticationUiState(
        val signInButtonIsLoading: Boolean = false,
        val isAuthenticated: Boolean = false
    )
}