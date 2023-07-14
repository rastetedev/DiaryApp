package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.models.Mood
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

        val viewModel: WriteViewModel = viewModel()
        val writeUiState = viewModel.uiState

        var deleteDiaryDialogOpenedState by remember {
            mutableStateOf(false)
        }

        val moodPagerState = rememberPagerState()

        WriteScreen(
            uiState = writeUiState,
            moodPagerState = moodPagerState,
            onBackPressed = onBackPressed,
            onMoodChanged = { mood ->
                viewModel.setMood(Mood.valueOf(mood))
            },
            onDeleteDiaryOptionClicked = {
                deleteDiaryDialogOpenedState = true
            },
            onTitleChanged = { title ->
                viewModel.setTitle(title)
            },
            onDescriptionChanged = { description ->
                viewModel.setDescription(description)
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