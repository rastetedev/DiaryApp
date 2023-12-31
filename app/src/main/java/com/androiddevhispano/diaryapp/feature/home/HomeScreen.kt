package com.androiddevhispano.diaryapp.feature.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.androiddevhispano.diaryapp.R
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    homeUiState: HomeViewModel.HomeUiState,
    diaries: Map<LocalDate, List<HomeViewModel.DiaryCard>>,
    drawerState: DrawerState,
    navigateToWrite: (diaryId: String?) -> Unit,
    onMenuClicked: () -> Unit,
    onResetFilterByDateClicked: () -> Unit,
    onSpecificDateClicked: (specificDate: ZonedDateTime) -> Unit,
    onDeleteAllClicked: () -> Unit,
    onGalleryClicked: (diaryId: String) -> Unit,
    onSignOutClicked: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val dateSheetPickerState = rememberUseCaseState()

    NavigationDrawer(
        drawerState = drawerState,
        onSignOutClicked = onSignOutClicked
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                HomeTopBar(
                    scrollBehavior = scrollBehavior,
                    diariesAreNotEmpty = diaries.isNotEmpty(),
                    onMenuClicked = onMenuClicked,
                    specificDateSelected = homeUiState.specificDateSelected,
                    onSpecificDateClicked = {
                        dateSheetPickerState.show()
                    },
                    onResetFilterByDateClicked = onResetFilterByDateClicked,
                    onDeleteAllClicked = onDeleteAllClicked
                )
            },

            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navigateToWrite(null)
                }) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                }
            }
        ) { paddingValues ->
            when {
                diaries.isNotEmpty() -> {
                    HomeContent(
                        modifier = Modifier
                            .padding(paddingValues),
                        diaries = diaries,
                        onGalleryClicked = onGalleryClicked,
                        onDiaryClicked = { diaryId ->
                            navigateToWrite(diaryId)
                        }
                    )
                }

                homeUiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_diaries),
                            style = TextStyle(
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }

    CalendarDialog(
        state = dateSheetPickerState,
        selection = CalendarSelection.Date { date: LocalDate ->
            onSpecificDateClicked(
                ZonedDateTime.of(
                    date,
                    LocalTime.now(),
                    ZoneId.systemDefault()
                )
            )
        },
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
    )
}