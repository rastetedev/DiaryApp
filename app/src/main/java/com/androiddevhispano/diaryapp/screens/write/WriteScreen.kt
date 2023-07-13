package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.models.Diary
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.PagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun WriteScreen(
    diary: Diary?,
    moodPagerState: PagerState,
    onBackPressed: () -> Unit,
    onDeleteDiaryOptionClicked: () -> Unit
) {

    Scaffold(
        topBar = {
            WriteTopBar(
                diary = diary,
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
                title = "",
                onTitleChanged = {

                },
                description = "",
                onDescriptionChanged = {

                }
            )
        }
    )
}
