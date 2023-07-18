package com.androiddevhispano.diaryapp.screens.authentication

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.navigation.Screen
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
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
        val viewModel: AuthenticationViewModel = viewModel()
        val loadingState by viewModel.loadingState

        val oneTapSignInState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        val context = LocalContext.current

        LaunchedEffect(key1 = Unit) {
            onDataLoaded()
        }

        AuthenticationScreen(
            messageBarState = messageBarState,
            loadingState = loadingState,
            authenticatedState = viewModel.authenticatedState,
            oneTapSignInState = oneTapSignInState,
            onSignInClicked = {
                viewModel.setLoading(true)
                oneTapSignInState.open()
            },
            onGoogleTokenIdReceived = { tokenId ->
                viewModel.signInWithFirebase(
                    tokenId,
                    onSuccess = {
                        viewModel.signInWithMongoDBAtlas(
                            tokenId,
                            onSuccess = {
                                messageBarState.addSuccess(context.getString(R.string.auth_success))
                                viewModel.setLoading(false)
                            }, onError = { exception ->
                                messageBarState.addError(exception)
                                viewModel.setLoading(false)
                            }
                        )
                    },
                    onError = { exception ->
                        messageBarState.addError(exception)
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