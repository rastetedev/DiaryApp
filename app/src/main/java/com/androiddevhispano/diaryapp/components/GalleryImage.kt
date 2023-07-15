package com.androiddevhispano.diaryapp.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androiddevhispano.diaryapp.models.GalleryImage

@Composable
fun GalleryImage(
    galleryImage: GalleryImage,
    imageShape: Shape,
    imageSize: Dp,
    onImageClicked: (GalleryImage) -> Unit
) {
    AsyncImage(
        modifier = Modifier
            .clip(imageShape)
            .size(imageSize)
            .clickable { onImageClicked(galleryImage) },
        model = ImageRequest.Builder(LocalContext.current)
            .data(galleryImage.imageUri)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}

@Composable
fun SimpleGalleryImage(
    imageShape: Shape,
    imageSize: Dp,
    imageUrl: String
){
    AsyncImage(
        modifier = Modifier
            .clip(imageShape)
            .size(imageSize),
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentScale = ContentScale.Crop,
        contentDescription = null
    )
}