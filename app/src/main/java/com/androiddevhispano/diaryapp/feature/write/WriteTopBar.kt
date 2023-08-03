package com.androiddevhispano.diaryapp.feature.write

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.ui.utils.dateSelectedFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteTopBar(
    uiState: WriteViewModel.WriteUiState,
    onBackPressed: () -> Unit,
    onCalendarClicked: () -> Unit,
    onRestoreDateClicked: () -> Unit,
    onDeleteDiaryClicked: () -> Unit
) {
    var showDeleteDiaryOption by remember { mutableStateOf(false) }

    val diaryDate = remember(uiState.date) {
        dateSelectedFormatter().format(uiState.date)
    }

    val updatedDate = remember(uiState.updatedDate) {
        uiState.updatedDate?.let {
            dateSelectedFormatter().format(it)
        }
    }

    CenterAlignedTopAppBar(
        navigationIcon = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null
                )
            }
        },
        title = {
            Column {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = uiState.mood.name,
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.titleLarge.fontSize,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = updatedDate ?: run { diaryDate },
                    textAlign = TextAlign.Center,
                    style = TextStyle(
                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                )
            }
        },
        actions = {
            if (uiState.updatedDate != null) {
                IconButton(onClick = onRestoreDateClicked) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = null)
                }
            } else {
                IconButton(onClick = onCalendarClicked) {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                }
            }


            uiState.diaryId?.let {
                IconButton(
                    onClick = {
                        showDeleteDiaryOption = true
                    }
                ) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)
                }
                DropdownMenu(
                    expanded = showDeleteDiaryOption,
                    onDismissRequest = {
                        showDeleteDiaryOption = false
                    }) {
                    DropdownMenuItem(
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                        },
                        text = {
                            Text(
                                text = stringResource(id = R.string.delete_diary)
                            )
                        },
                        onClick = {
                            showDeleteDiaryOption = false
                            onDeleteDiaryClicked()
                        }
                    )
                }
            }
        }
    )
}