package com.androiddevhispano.diaryapp.screens.home.diary

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.androiddevhispano.diaryapp.components.GalleryImage
import com.androiddevhispano.diaryapp.components.LastImageOverlay
import kotlin.math.max

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    imageUriList: List<Uri>,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
    imageShape: CornerBasedShape = Shapes().small
) {
    BoxWithConstraints(modifier = modifier) {
        val numberOfSlots = remember {
            derivedStateOf {
                max(
                    a = 0,
                    b = this.maxWidth.div(spaceBetween + imageSize).toInt()
                )
            }
        }

        val remainingImages = remember {
            derivedStateOf {
                imageUriList.size - numberOfSlots.value + 1
            }
        }

        val showLastImageOverlay by remember {
            derivedStateOf {
                remainingImages.value > 1
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spaceBetween)) {
            imageUriList.take(
                if (showLastImageOverlay)
                    numberOfSlots.value - 1 //The last slot is reserved to show LastImageOverlay
                else
                    numberOfSlots.value
            ).forEach { imageUri ->
                GalleryImage(
                    imageUri = imageUri,
                    imageShape = imageShape,
                    imageSize = imageSize
                )
            }
            if (showLastImageOverlay) {
                LastImageOverlay(
                    imageSize = imageSize,
                    imageShape = imageShape,
                    remainingImages = remainingImages.value
                )
            }
        }
    }
}

@Preview
@Composable
fun GalleryPreview() {
    Gallery(
        modifier = Modifier.width(120.dp),
        imageUriList = listOf(
            "image1.png".toUri(),
            "image2.png".toUri(),
            "image3.png".toUri(),
            "image4.png".toUri()
        )
    )
}

