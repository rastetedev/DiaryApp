package com.androiddevhispano.diaryapp.screens.home.diary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androiddevhispano.diaryapp.components.LastImageOverlay
import kotlin.math.max

@Composable
fun Gallery(
    modifier: Modifier = Modifier,
    images: List<String>,
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
                images.size - numberOfSlots.value + 1
            }
        }

        val showLastImageOverlay by remember {
            derivedStateOf {
                remainingImages.value > 1
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(spaceBetween)) {
            images.take(
                if (showLastImageOverlay)
                    numberOfSlots.value - 1 //The last slot is reserved to show LastImageOverlay
                else
                    numberOfSlots.value
            ).forEach { image ->
                AsyncImage(
                    modifier = Modifier
                        .clip(imageShape)
                        .size(imageSize),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(image)
                        .crossfade(true)
                        .build(),
                    contentScale = ContentScale.Crop,
                    contentDescription = null
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
        images = listOf("image1.png", "image2.png", "image3.png", "image4.png")
    )
}

