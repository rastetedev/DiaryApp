package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.models.Mood
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WriteScreen(
    uiState: WriteUiState,
    moodPagerState: PagerState,
    buttonEnabledState: Boolean,
    onBackPressed: () -> Unit,
    onMoodChanged: (String) -> Unit,
    onDeleteDiaryOptionClicked: () -> Unit,
    onTitleChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onSaveButtonClicked : () -> Unit
) {

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
                onDeleteDiaryOptionClicked = onDeleteDiaryOptionClicked
            )
        },
        content = { paddingValues ->
            WriteContent(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                moodPagerState = moodPagerState,
                buttonEnabledState = buttonEnabledState,
                title = uiState.title,
                onTitleChanged = { title ->
                    onTitleChanged(title)
                },
                description = uiState.description,
                onDescriptionChanged = { description ->
                    onDescriptionChanged(description)
                },
                onSaveButtonClicked = onSaveButtonClicked
            )
        }
    )
}
