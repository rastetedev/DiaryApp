package com.androiddevhispano.diaryapp.screens.home.diary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.ui.theme.Elevation
import com.androiddevhispano.diaryapp.ui.theme.Size.extraLarge
import com.androiddevhispano.diaryapp.utils.toInstant

@Composable
fun DiaryHolder(
    diary: Diary,
    onDiaryClicked: (diaryId: String) -> Unit
) {
    Surface(
        modifier = Modifier
            .clip(shape = Shapes().medium)
            .clickable {
                onDiaryClicked(diary._id.toHexString())
            },
        tonalElevation = Elevation.Level1
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DiaryHeader(moodName = diary.mood, time = diary.date.toInstant())
            Text(
                modifier = Modifier.padding(all = extraLarge),
                text = diary.description,
                style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
@Preview
fun DiaryHolderPreview() {
    DiaryHolder(diary = Diary().apply {
        title = "This a diary title to test"
        description = "This is a diary description to test how looks on the app"
    }, onDiaryClicked = {})
}