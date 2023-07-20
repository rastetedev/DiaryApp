package com.androiddevhispano.write

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androiddevhispano.diaryapp.data.models.GalleryImage
import com.androiddevhispano.diaryapp.ui.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.utils.getType
import com.androiddevhispano.diaryapp.utils.navigation.Screen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.rememberPagerState
import com.androiddevhispano.diaryapp.ui.R
import com.androiddevhispano.diaryapp.ui.models.Mood

@OptIn(ExperimentalPagerApi::class, ExperimentalAnimationApi::class)
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

        val viewModel: WriteViewModel = hiltViewModel()
        val writeUiState = viewModel.uiState
        val galleryState = viewModel.galleryState

        val buttonEnabledState = remember(writeUiState.title, writeUiState.description) {
            writeUiState.title.isNotEmpty() && writeUiState.description.isNotEmpty()
        }

        var deleteDiaryDialogOpenedState by remember {
            mutableStateOf(false)
        }

        val moodPagerState = rememberPagerState()
        val context = LocalContext.current

        WriteScreen(
            uiState = writeUiState,
            moodPagerState = moodPagerState,
            galleryState = galleryState,
            buttonEnabledState = buttonEnabledState,
            onBackPressed = onBackPressed,
            onMoodChanged = { mood ->
                viewModel.setMood(Mood.valueOf(mood))
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
            isDownloadingImages = viewModel.uiState.isDownloadingImages,
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
                            context.getString(R.string.save_diary_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onDeleteGalleryImageClicked = { imageUri ->
                viewModel.galleryState.removeImage(
                    GalleryImage(
                        imageUri = imageUri,
                        remoteImagePath = imageUri.toString()
                    )
                )
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
                            context.getString(R.string.delete_diary_error),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        )
    }
}