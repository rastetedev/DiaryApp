package com.androiddevhispano.diaryapp.screens.authentication

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.BuildConfig
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@Composable
fun AuthenticationScreen(
    messageBarState: MessageBarState,
    loadingState: Boolean,
    authenticatedState: Boolean,
    oneTapSignInState: OneTapSignInState,
    onButtonClicked: () -> Unit,
    onGoogleTokenIdReceived: (String) -> Unit,
    onSignInDialogDismissed: (String) -> Unit,
    navigateToHome: () -> Unit
) {

    ContentWithMessageBar(
        messageBarState = messageBarState,
        showToastOnCopy = false,
        errorMaxLines = 2,
    ) {
        Scaffold(
            content = { paddingValues ->
                AuthenticationContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    loadingState = loadingState,
                    onButtonClicked = onButtonClicked
                )
            })
    }

    OneTapSignInWithGoogle(
        state = oneTapSignInState,
        clientId = BuildConfig.CLIENT_ID_WEB,
        onTokenIdReceived = { tokenId ->
            onGoogleTokenIdReceived(tokenId)
        },
        onDialogDismissed = { message ->
            onSignInDialogDismissed(message)
        }
    )

    LaunchedEffect(key1 = authenticatedState) {
        if (authenticatedState) {
            navigateToHome()
        }
    }
}

