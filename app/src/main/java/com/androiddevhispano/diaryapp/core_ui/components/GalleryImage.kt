package com.androiddevhispano.diaryapp.core_ui.components

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androiddevhispano.diaryapp.R

@Composable
fun GalleryImage(
    imageUri: Uri,
    shape: Shape = Shapes().small,
    size: Dp = 40.dp,
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

@Composable
@Preview
fun GalleryImagePreview() {
    GalleryImage(
        imageUri = "".toUri()
    )
}