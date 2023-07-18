package com.androiddevhispano.diaryapp.screens.write.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androiddevhispano.diaryapp.components.GalleryContainer

@Composable
fun GalleryUploader(
    modifier: Modifier = Modifier,
    galleryState: GalleryState,
    imageSize: Dp = 60.dp,
    imageShape: CornerBasedShape = Shapes().medium,
    spaceBetween: Dp = 12.dp,
    onAddClicked: () -> Unit,
    onImagePickedFromGallery: (imageUri: Uri) -> Unit,
    onGalleryImageClicked: (imageUri: Uri) -> Unit,
    isDownloadingImages: Boolean
) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
    ) { images ->
        images.forEach {
            onImagePickedFromGallery(it)
        }
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spaceBetween),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AddImageButton(
            size = imageSize,
            shape = imageShape,
            onClick = {
                onAddClicked()
                multiplePhotoPicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            }
        )
        GalleryContainer(
            modifier = Modifier.weight(1f),
            isVisible = true,
            isLoading = isDownloadingImages,
            progressIndicatorSize = 48.dp,
            images = galleryState.images.map { it.imageUri },
            imageSize = imageSize,
            spaceBetween = spaceBetween,
            onGalleryImageClicked = onGalleryImageClicked
        )
    }
}