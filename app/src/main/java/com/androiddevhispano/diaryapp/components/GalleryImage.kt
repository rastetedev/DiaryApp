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
    shape: Shape,
    size: Dp,
    onGalleryImageClicked: (imageUri: Uri) -> Unit = {}
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUri)
            .crossfade(true)
            .build(),
        modifier = Modifier
            .clip(shape)
            .size(size)
            .clickable { onGalleryImageClicked(imageUri) },
        placeholder = painterResource(R.drawable.logo),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}