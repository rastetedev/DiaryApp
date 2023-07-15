package com.androiddevhispano.diaryapp.screens.write.gallery

import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import com.androiddevhispano.diaryapp.components.BoxOverlay

@Composable
fun AddImageButton(
    size: Dp,
    shape: CornerBasedShape,
    onClick: () -> Unit
) {
    BoxOverlay(
        size = size,
        shape = shape,
        content = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
            )
        },
        onClick = onClick
    )
}