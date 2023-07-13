package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.navigation.Screen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState


@OptIn(ExperimentalPagerApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
) {

    composable(
        route = Screen.Write.route,
        arguments = listOf(
            navArgument(name = Screen.DIARY_ID_ARGUMENT) {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {

        var deleteDiaryDialogOpenedState by remember {
            mutableStateOf(false)
        }

        val moodPagerState = rememberPagerState()

        WriteScreen(
            diary = Diary(),
            moodPagerState = moodPagerState,
            onBackPressed = onBackPressed,
            onDeleteDiaryOptionClicked = {
                deleteDiaryDialogOpenedState = true
            }
        )

        DisplayAlertDialog(
            title = stringResource(id = R.string.delete_diary),
            message = stringResource(id = R.string.confirm_delete_diary),
            dialogOpened = deleteDiaryDialogOpenedState,
            onDialogClosed = {
                deleteDiaryDialogOpenedState = false
            },
            onConfirmClicked = {

            }
        )
    }
}