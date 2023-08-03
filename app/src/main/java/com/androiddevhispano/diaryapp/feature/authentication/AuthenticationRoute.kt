package com.androiddevhispano.diaryapp.feature.authentication

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.navigation.Screen
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import org.koin.androidx.compose.koinViewModel
import kotlin.Exception

fun NavGraphBuilder.authenticationRoute(
    navigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(
        route = Screen.Authentication.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left) }
    ) {
        val viewModel: AuthenticationViewModel = koinViewModel()
        val authenticationUiState by viewModel.authenticatedState.collectAsState()

        val oneTapSignInState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        val context = LocalContext.current

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            messageBarState = messageBarState,
            loadingState = authenticationUiState.signInButtonIsLoading,
            authenticatedState = authenticationUiState.isAuthenticated,
            oneTapSignInState = oneTapSignInState,
            onSignInClicked = {
                viewModel.setLoading(true)
                oneTapSignInState.open()
            },
            onGoogleTokenIdReceived = { tokenId ->
                viewModel.signIn(
                    tokenId,
                    onSuccess = {
                        messageBarState.addSuccess(context.getString(R.string.auth_success))
                        viewModel.setLoading(false)
                    },
                    onError = { message ->
                        messageBarState.addError(Exception(message))
                        viewModel.setLoading(false)
                    }
                )
            },
            onSignInDialogDismissed = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = navigateToHome
        )
    }
}