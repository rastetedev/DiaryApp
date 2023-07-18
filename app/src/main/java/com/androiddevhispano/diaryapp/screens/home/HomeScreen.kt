package com.androiddevhispano.diaryapp.screens.home

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
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.utils.RequestState
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
    diariesRequestState: RequestState<Map<LocalDate, List<Diary>>>,
    drawerState: DrawerState,
    diariesFilterByDate: Boolean,
    navigateToWrite: (diaryId: String?) -> Unit,
    onMenuClicked: () -> Unit,
    onResetFilterByDateClicked: () -> Unit,
    onSpecificDateClicked: (specificDate: ZonedDateTime) -> Unit,
    onDeleteAllClicked: () -> Unit,
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
                    diariesAreNotEmpty = (diariesRequestState is RequestState.Success && diariesRequestState.data.isNotEmpty()),
                    onMenuClicked = onMenuClicked,
                    diariesFilterByDate = diariesFilterByDate,
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
                diariesRequestState is RequestState.Success && diariesRequestState.data.isNotEmpty() -> {
                    HomeContent(
                        modifier = Modifier
                            .padding(paddingValues),
                        diaryNotes = diariesRequestState.data,
                        onDiaryClicked = { diaryId ->
                            navigateToWrite(diaryId)
                        }
                    )
                }

                diariesRequestState is RequestState.Error ||
                        diariesRequestState is RequestState.Success -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(id = R.string.no_diaries), style = TextStyle(
                                fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }


                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
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