package com.androiddevhispano.diaryapp.feature.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.androiddevhispano.diaryapp.R
import java.time.ZonedDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(
    scrollBehavior: TopAppBarScrollBehavior,
    diariesAreNotEmpty: Boolean,
    onMenuClicked: () -> Unit,
    specificDateSelected: ZonedDateTime?,
    onSpecificDateClicked: () -> Unit,
    onResetFilterByDateClicked: () -> Unit,
    onDeleteAllClicked: () -> Unit
) {
    TopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(onClick = onMenuClicked) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = null)
            }
        },
        title = {
            Text(text = stringResource(id = R.string.diary))
        },
        actions = {
            if(diariesAreNotEmpty){
                if (specificDateSelected != null) {
                    IconButton(onClick = onResetFilterByDateClicked) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                } else {
                    IconButton(onClick = onSpecificDateClicked) {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = null)
                    }
                }
                IconButton(onClick = onDeleteAllClicked) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    )
}
