package com.androiddevhispano.diaryapp.screens.authentication

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.BuildConfig
import com.stevdzasan.messagebar.ContentWithMessageBar
import com.stevdzasan.messagebar.MessageBarState
import com.stevdzasan.onetap.OneTapSignInState
import com.stevdzasan.onetap.OneTapSignInWithGoogle

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AuthenticationScreen(
    messageBarState: MessageBarState,
    loadingState: Boolean,
    authenticatedState: Boolean,
    oneTapSignInState: OneTapSignInState,
    onSignInClicked: () -> Unit,
    onGoogleTokenIdReceived: (tokenId: String) -> Unit,
    onSignInDialogDismissed: (message: String) -> Unit,
    navigateToHome: () -> Unit
) {

    Scaffold(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .systemBarsPadding(),
        content = {
            ContentWithMessageBar(
                messageBarState = messageBarState,
                showToastOnCopy = false,
                errorMaxLines = 2,
            ) {
                AuthenticationContent(
                    modifier = Modifier
                        .fillMaxSize(),
                    loadingState = loadingState,
                    onButtonClicked = onSignInClicked
                )
            }
        })


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

