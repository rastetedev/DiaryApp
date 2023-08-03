package com.androiddevhispano.diaryapp.feature.write

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.core_ui.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.core_ui.models.Mood
import com.androiddevhispano.diaryapp.navigation.Screen
import com.androiddevhispano.diaryapp.ui.utils.getType
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalFoundationApi::class)
fun NavGraphBuilder.writeRoute(
    onBackPressed: () -> Unit
) {
    composable(
        route = Screen.Write.route,
        enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) },
        exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right) },
        arguments = listOf(
            navArgument(name = Screen.DIARY_ID_ARGUMENT) {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {

        val viewModel: WriteViewModel = koinViewModel()
        val writeUiState by viewModel.writeUiState.collectAsState()

        val buttonEnabledState = remember(writeUiState.title, writeUiState.description) {
            writeUiState.title.isNotEmpty() && writeUiState.description.isNotEmpty()
        }

        var deleteDiaryDialogOpenedState by remember {
            mutableStateOf(false)
        }

        val moodPagerState = rememberPagerState(
            pageCount = { Mood.values().size }
        )

        val context = LocalContext.current

        WriteScreen(
            uiState = writeUiState,
            moodPagerState = moodPagerState,
            buttonEnabledState = buttonEnabledState,
            onBackPressed = onBackPressed,
            onMoodChanged = {
                viewModel.setMood(it)
            },
            onDeleteDiaryClicked = {
                deleteDiaryDialogOpenedState = true
            },
            onDateTimeUpdated = { newDate ->
                viewModel.updateDate(newDate)
            },
            onRestoreDateClicked = {
                viewModel.returnToPreviousDate()
            },
            onTitleChanged = { title ->
                viewModel.setTitle(title)
            },
            onDescriptionChanged = { description ->
                viewModel.setDescription(description)
            },
            onImagePickedFromGallery = { imageUri ->
                viewModel.addImage(
                    imageUri = imageUri,
                    imageType = imageUri.getType(context)
                )
            },
            isDownloadingImages = writeUiState.isLoadingImages,
            onSaveButtonClicked = {
                viewModel.saveDiary(
                    onSuccess = {
                        onBackPressed()
                        Toast.makeText(
                            context,
                            context.getString(R.string.save_diary_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            "${context.getString(R.string.save_diary_error)}: $it ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onDeleteGalleryImageClicked = { imageUri ->
                viewModel.removeImageFromGallery(imageUri)
            }
        )

        DisplayAlertDialog(
            showDialog = deleteDiaryDialogOpenedState,
            title = stringResource(id = R.string.delete_diary),
            message = stringResource(id = R.string.confirm_delete_diary),
            onDialogClosed = {
                deleteDiaryDialogOpenedState = false
            },
            onConfirmClicked = {
                viewModel.deleteDiary(
                    onSuccess = {
                        onBackPressed()
                        Toast.makeText(
                            context,
                            context.getString(R.string.delete_diary_success),
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onError = {
                        Toast.makeText(
                            context,
                            "${context.getString(R.string.delete_diary_error)}: $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )
    }
}