package com.androiddevhispano.diaryapp.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.screens.home.diary.DateHeader
import com.androiddevhispano.diaryapp.screens.home.diary.DiaryHolder
import com.androiddevhispano.diaryapp.ui.theme.Size.screenPadding
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    diaryNotes: Map<LocalDate, List<Diary>>,
    onDiaryClicked: (String) -> Unit
) {

    LazyColumn(
        modifier = modifier
            .padding(horizontal = screenPadding),
    ) {
        diaryNotes.forEach { (localDate, diaries) ->
            stickyHeader(key = localDate) {
                DateHeader(localDate = localDate)
            }

            items(
                items = diaries,
                key = { it._id.toString() }
            ) {
                DiaryHolder(diary = it, onDiaryClicked = onDiaryClicked)
            }
        }
    }
}