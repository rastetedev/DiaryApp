package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.models.Diary

@Composable
fun WriteScreen(
    diary: Diary?,
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
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

            }
        }
    )
}
