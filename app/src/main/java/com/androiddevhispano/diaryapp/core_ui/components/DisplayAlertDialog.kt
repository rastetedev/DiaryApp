package com.androiddevhispano.diaryapp.core_ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.androiddevhispano.diaryapp.R

@Composable
fun DisplayAlertDialog(
    showDialog: Boolean,
    title: String,
    message: String,
    onConfirmClicked: () -> Unit,
    onDialogClosed: () -> Unit,
) {
    if (showDialog) {
        AlertDialog(
            title = {
                Text(
                    text = title,
                    fontSize = MaterialTheme.typography.headlineSmall.fontSize,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = message,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    fontWeight = FontWeight.Normal
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClicked()
                        onDialogClosed()
                    })
                {
                    Text(text = stringResource(id = R.string.yes))
                }
            },
            dismissButton = {
                OutlinedButton(onClick = onDialogClosed)
                {
                    Text(text = stringResource(id = R.string.no))
                }
            },
            onDismissRequest = onDialogClosed
        )
    }
}

@Composable
@Preview
fun DisplayAlertDialogPreview() {
    DisplayAlertDialog(
        showDialog = true,
        title = "This is a title",
        message = "This is the main message in alert dialog. So what do you think?",
        onConfirmClicked = { }) {

    }
}