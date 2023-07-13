package com.androiddevhispano.diaryapp.screens.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.androiddevhispano.diaryapp.models.Diary
import java.time.LocalDate

@Composable
fun HomeScreen(
    drawerState: DrawerState,
    navigateToWrite: () -> Unit,
    onMenuClicked: () -> Unit,
    onSignOutClicked: () -> Unit
) {
    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClicked = onSignOutClicked
    ) {
        Scaffold(
            topBar = {
                HomeTopBar(
                    onMenuClicked = onMenuClicked
                )
            },

            floatingActionButton = {
                FloatingActionButton(onClick = navigateToWrite) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
            }
        ) { paddingValues ->
            HomeContent(
                modifier = Modifier
                    .padding(paddingValues),
                diaryNotes = mapOf(
                    LocalDate.now() to listOf(
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        },
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        }
                    ),
                    LocalDate.now().minusDays(2) to listOf(
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        },
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        }
                    ),
                    LocalDate.now().minusDays(4) to listOf(
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        },
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        }
                    ),
                    LocalDate.now().minusDays(6) to listOf(
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        },
                        Diary().apply {
                            title = "Not empty title"
                            description = "Not empty description"
                        }
                    ),
                ),
                onDiaryClicked = {})
        }
    }
}