package com.androiddevhispano.diaryapp.screens.write.gallery

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.androiddevhispano.diaryapp.components.GalleryImage
import com.androiddevhispano.diaryapp.components.LastImageOverlay
import com.androiddevhispano.diaryapp.models.GalleryImage
import kotlin.math.max

@Composable
fun GalleryUploader(
    modifier: Modifier = Modifier,
    galleryState: GalleryState,
    imageSize: Dp = 60.dp,
    imageShape: CornerBasedShape = Shapes().medium,
    spaceBetween: Dp = 12.dp,
    onAddClicked: () -> Unit,
    onImageSelected: (Uri) -> Unit,
    onImageClicked: (GalleryImage) -> Unit,
    isDownloadingImages: Boolean
) {
    val multiplePhotoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 8),
    ) { images ->
        images.forEach {
            onImageSelected(it)
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val numberOfSlots by remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt() - 1
                )
            }
        }

        val remainingImages by remember {
            derivedStateOf {
                if (galleryState.images.size == numberOfSlots) 0
                else (galleryState.images.size - numberOfSlots + 1)
            }
        }

        val showLastImageOverlay by remember {
            derivedStateOf {
                remainingImages > 1
            }
        }

        Row(
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
            if (isDownloadingImages) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    CircularProgressIndicator()
                }
            } else {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(spaceBetween)
                ) {
                    galleryState.images.take(if (showLastImageOverlay) numberOfSlots - 1 else numberOfSlots)
                        .forEach { galleryImage ->
                            GalleryImage(
                                galleryImage = galleryImage,
                                imageShape = imageShape,
                                imageSize = imageSize,
                                onImageClicked = onImageClicked
                            )
                        }
                    if (showLastImageOverlay) {
                        LastImageOverlay(
                            imageSize = imageSize,
                            imageShape = imageShape,
                            remainingImages = remainingImages
                        )
                    }
                }
            }

        }
    }
}