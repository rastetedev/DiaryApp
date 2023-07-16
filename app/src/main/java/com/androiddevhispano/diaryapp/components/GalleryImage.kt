package com.androiddevhispano.diaryapp.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androiddevhispano.diaryapp.R

@Composable
fun GalleryImage(
    imageUri: Uri,
    imageShape: Shape,
    imageSize: Dp,
    onImageClicked: (imageUri: Uri) -> Unit = {}
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .crossfade(true)
            .build(),
        modifier = Modifier
            .clip(imageShape)
            .size(imageSize)
            .clickable { onImageClicked(imageUri) },
        placeholder = painterResource(R.drawable.logo),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}