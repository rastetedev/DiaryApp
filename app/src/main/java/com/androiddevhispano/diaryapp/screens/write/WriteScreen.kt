package com.androiddevhispano.diaryapp.screens.write

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.androiddevhispano.diaryapp.models.Mood
import com.androiddevhispano.diaryapp.screens.write.gallery.GalleryState
import com.androiddevhispano.diaryapp.utils.toLocalDate
import com.androiddevhispano.diaryapp.utils.toLocalTime
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState
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

@OptIn(ExperimentalPagerApi::class, ExperimentalMaterial3Api::class)
@Composable
fun WriteScreen(
    uiState: WriteUiState,
    moodPagerState: PagerState,
    galleryState: GalleryState,
    buttonEnabledState: Boolean,
    onBackPressed: () -> Unit,
    onMoodChanged: (String) -> Unit,
    onDeleteDiaryClicked: () -> Unit,
    onDateTimeUpdated: (ZonedDateTime) -> Unit,
    onRestoreDateClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    isDownloadingImages: Boolean,
    onSaveButtonClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit
) {

    val dateSheetPickerState = rememberUseCaseState()
    val timeSheetPickerState = rememberUseCaseState()

    var dateInDialog by remember {
        mutableStateOf(uiState.date.toLocalDate())
    }
    var timeInDialog by remember {
        mutableStateOf(uiState.date.toLocalTime())
    }

    LaunchedEffect(key1 = uiState.mood) {
        moodPagerState.scrollToPage(Mood.valueOf(uiState.mood.name).ordinal)
    }

    LaunchedEffect(moodPagerState) {
        snapshotFlow { moodPagerState.currentPage }.collect { page ->
            onMoodChanged(Mood.values()[page].name)
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
                galleryState = galleryState,
                buttonEnabledState = buttonEnabledState,
                title = uiState.title,
                onTitleChanged = { title ->
                    onTitleChanged(title)
                },
                description = uiState.description,
                onDescriptionChanged = { description ->
                    onDescriptionChanged(description)
                },
                onImageSelected = onImageSelected,
                onImageClicked = {},
                isDownloadingImages = isDownloadingImages,
                onSaveButtonClicked = onSaveButtonClicked
            )
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
