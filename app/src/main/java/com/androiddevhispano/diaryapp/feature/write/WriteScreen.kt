package com.androiddevhispano.diaryapp.feature.write

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.Dialog
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.core_ui.components.DisplayAlertDialog
import com.androiddevhispano.diaryapp.core_ui.components.ZoomableImage
import com.androiddevhispano.diaryapp.core_ui.models.Mood
import com.androiddevhispano.diaryapp.ui.utils.toLocalDate
import com.androiddevhispano.diaryapp.ui.utils.toLocalTime
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.maxkeppeler.sheets.clock.ClockDialog
import com.maxkeppeler.sheets.clock.models.ClockConfig
import com.maxkeppeler.sheets.clock.models.ClockSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WriteScreen(
    uiState: WriteViewModel.WriteUiState,
    moodPagerState: PagerState,
    buttonEnabledState: Boolean,
    onBackPressed: () -> Unit,
    onMoodChanged: (Mood) -> Unit,
    onDeleteDiaryClicked: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onRestoreDateClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    isDownloadingImages: Boolean,
    onSaveButtonClicked: () -> Unit,
    onImagePickedFromGallery: (Uri) -> Unit,
    onDeleteGalleryImageClicked: (Uri) -> Unit
) {

    val dateSheetPickerState = rememberUseCaseState()
    val timeSheetPickerState = rememberUseCaseState()

    var isFirstTime by remember { mutableStateOf(true) }

    var dateInDialog by remember {
        mutableStateOf(uiState.date.toLocalDate())
    }
    var timeInDialog by remember {
        mutableStateOf(uiState.date.toLocalTime())
    }

    var selectedGalleryImageUri by remember {
        mutableStateOf<Uri?>(null)
    }

    var showDeleteGalleryImageDialogState by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(uiState.mood) {
        moodPagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }

    LaunchedEffect(moodPagerState) {
        snapshotFlow { moodPagerState.currentPage }
            .collect { page ->
                if (!isFirstTime) {
                    onMoodChanged(Mood.values()[page])
                }
                isFirstTime = false
            }
    }

    Scaffold(
        topBar = {
            WriteTopBar(
                uiState = uiState,
                onBackPressed = onBackPressed,
                onCalendarClicked = {
                    dateSheetPickerState.show()
                },
                onRestoreDateClicked = onRestoreDateClicked,
                onDeleteDiaryClicked = onDeleteDiaryClicked
            )
        },
        content = { paddingValues ->
            WriteContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                moodPagerState = moodPagerState,
                imagesToShow = uiState.imagesToShow,
                buttonEnabledState = buttonEnabledState,
                title = uiState.title,
                onTitleChanged = { title ->
                    onTitleChanged(title)
                },
                description = uiState.description,
                onDescriptionChanged = { description ->
                    onDescriptionChanged(description)
                },
                onImagePickedFromGallery = onImagePickedFromGallery,
                onGalleryImageClicked = {
                    selectedGalleryImageUri = it
                },
                isDownloadingImages = isDownloadingImages,
                onSaveButtonClicked = onSaveButtonClicked
            )
        }
    )

    AnimatedVisibility(visible = selectedGalleryImageUri != null) {
        Dialog(
            onDismissRequest = {
                selectedGalleryImageUri = null
            }) {

            if (selectedGalleryImageUri != null) {
                ZoomableImage(
                    imageUri = selectedGalleryImageUri!!,
                    onCloseClicked = {
                        selectedGalleryImageUri = null
                    },
                    onDeleteClicked = {
                        showDeleteGalleryImageDialogState = true
                    }
                )
            }
        }
    }

    DisplayAlertDialog(
        showDialog = showDeleteGalleryImageDialogState,
        title = stringResource(id = R.string.delete_gallery_image),
        message = stringResource(id = R.string.confirm_delete_gallery_image),
        onDialogClosed = {
            showDeleteGalleryImageDialogState = false
        },
        onConfirmClicked = {
            onDeleteGalleryImageClicked(selectedGalleryImageUri!!)
            selectedGalleryImageUri = null
        }
    )

    CalendarDialog(
        state = dateSheetPickerState,
        selection = CalendarSelection.Date(selectedDate = dateInDialog) { date: LocalDate ->
            dateInDialog = date
            timeSheetPickerState.show()
        },
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
    )

    ClockDialog(
        state = timeSheetPickerState,
        selection = ClockSelection.HoursMinutes() { hours: Int, minutes: Int ->
            timeInDialog = LocalTime.of(hours, minutes)
            onDateTimeUpdated(
                ZonedDateTime.of(dateInDialog, timeInDialog, ZoneId.systemDefault())
            )
        },
        config = ClockConfig(
            defaultTime = timeInDialog
        )
    )
}
