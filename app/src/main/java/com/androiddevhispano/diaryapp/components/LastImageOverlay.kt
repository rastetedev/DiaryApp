package com.androiddevhispano.diaryapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun LastImageOverlay(
    imageSize: Dp,
    imageShape: CornerBasedShape,
    remainingImages: Int
) {
    Box(
        modifier = Modifier
            .size(imageSize)
            .clip(imageShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "+$remainingImages",
            style = TextStyle(
                fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                fontWeight = FontWeight.Medium
            ),
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Preview
@Composable
fun LastImageOverlayPreview() {
    LastImageOverlay(imageSize = 40.dp, imageShape = Shapes().small, remainingImages = 2)
}