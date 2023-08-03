package com.androiddevhispano.diaryapp.feature.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.feature.home.diary.DateHeader
import com.androiddevhispano.diaryapp.feature.home.diary.DiaryHolder
import com.androiddevhispano.diaryapp.ui.theme.Size.screenPadding

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    homeUiState: HomeViewModel.HomeUiState,
    onGalleryClicked: (diaryId: String, imageRemotePathList: List<String>) -> Unit,
    onDiaryClicked: (diaryId: String) -> Unit
) {


    LazyColumn(
        modifier = modifier
            .padding(horizontal = screenPadding),
    ) {
        homeUiState.diaries.forEach { (localDate, diaries) ->
            stickyHeader(key = localDate) {
                DateHeader(localDate = localDate)
            }

            items(
                items = diaries,
                key = { it.id }
            ) {
                DiaryHolder(
                    diary = it,
                    onGalleryClicked = onGalleryClicked,
                    onDiaryClicked = onDiaryClicked
                )
            }
        }
    }
}
