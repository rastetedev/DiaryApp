package com.androiddevhispano.diaryapp.screens.home

import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.androiddevhispano.diaryapp.BuildConfig.MONGO_APP_ID
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.navigation.Screen
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit,
    navigateToAuthentication: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()

        val diariesRequestState by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var signOutDialogOpenedState by remember {
            mutableStateOf(false)
        }

        val coroutineScope = rememberCoroutineScope()

        HomeScreen(
            diariesRequestState = diariesRequestState,
            drawerState = drawerState,
            navigateToWrite = navigateToWrite,
            onMenuClicked = {
                coroutineScope.launch {
                    drawerState.open()
                }
            },
            onSignOutClicked = {
                signOutDialogOpenedState = true
            }
        )

        DisplayAlertDialog(
            title = stringResource(id = R.string.sign_out),
            message = stringResource(id = R.string.confirm_sign_out),
            dialogOpened = signOutDialogOpenedState,
            onDialogClosed = {
                signOutDialogOpenedState = false
            },
            onConfirmClicked = {
                coroutineScope.launch(Dispatchers.IO) {
                    App.create(MONGO_APP_ID).currentUser?.logOut()
                    withContext(Dispatchers.Main) {
                        navigateToAuthentication()
                    }
                }
            }
        )

    }
}