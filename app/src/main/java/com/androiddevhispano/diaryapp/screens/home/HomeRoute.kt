package com.androiddevhispano.diaryapp.screens.home

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.androiddevhispano.diaryapp.BuildConfig.MONGO_APP_ID
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.navigation.Screen
import com.androiddevhispano.diaryapp.utils.RequestState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

fun NavGraphBuilder.homeRoute(
    navigateToWrite: (diaryId: String?) -> Unit,
    navigateToAuthentication: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(
        route = Screen.Home.route,
        enterTransition = {
            when (initialState.destination.route) {
                Screen.Authentication.route -> {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                }

                else -> {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                }
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                Screen.Authentication.route -> {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
                }

                else -> {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
                }
            }
        }
    ) {
        val viewModel: HomeViewModel = hiltViewModel()

        val diariesRequestState by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        var signOutDialogOpenedState by remember {
            mutableStateOf(false)
        }
        var deleteAllDialogOpenedState by remember {
            mutableStateOf(false)
        }
        val coroutineScope = rememberCoroutineScope()
        val context = LocalContext.current

        LaunchedEffect(key1 = diariesRequestState) {
            if (diariesRequestState !is RequestState.Idle) {
                onDataLoaded()
            }
        }

        HomeScreen(
            diariesRequestState = diariesRequestState,
            drawerState = drawerState,
            diariesFilterByDate = viewModel.isSpecificDateSelected,
            navigateToWrite = { diaryId ->
                navigateToWrite(diaryId)
            },
            onMenuClicked = {
                coroutineScope.launch {
                    drawerState.open()
                }
            },
            onResetFilterByDateClicked = {
                viewModel.getDiaries()
            },
            onSpecificDateClicked = { specificDateToShowDiaries ->
                viewModel.getDiaries(specificDateToShowDiaries)
            },
            onDeleteAllClicked = {
                deleteAllDialogOpenedState = true
            },
            onSignOutClicked = {
                signOutDialogOpenedState = true
            }
        )

        DisplayAlertDialog(
            showDialog = signOutDialogOpenedState,
            title = stringResource(id = R.string.sign_out),
            message = stringResource(id = R.string.confirm_sign_out),
            onDialogClosed = {
                signOutDialogOpenedState = false
            },
            onConfirmClicked = {
                coroutineScope.launch(Dispatchers.IO) {
                    App.create(MONGO_APP_ID).currentUser?.logOut()
                    Firebase.auth.signOut()
                    withContext(Dispatchers.Main) {
                        navigateToAuthentication()
                    }
                }
            }
        )

        DisplayAlertDialog(
            showDialog = deleteAllDialogOpenedState,
            title = stringResource(id = R.string.delete_all_diaries),
            message = stringResource(id = R.string.confirm_delete_all_diaries),
            onDialogClosed = {
                deleteAllDialogOpenedState = false
            },
            onConfirmClicked = {
                viewModel.deleteAllDiaries(
                    onSuccess = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.delete_all_diaries_successful),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            context.getString(R.string.delete_all_diaries_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )
    }
}