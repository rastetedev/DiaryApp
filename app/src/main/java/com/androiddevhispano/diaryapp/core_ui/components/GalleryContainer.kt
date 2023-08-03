package com.androiddevhispano.diaryapp.core_ui.components

import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import kotlin.math.max

@Composable
fun GalleryContainer(
    modifier: Modifier = Modifier,
    isVisible: Boolean,
    isLoading: Boolean,
    progressIndicatorSize: Dp = 24.dp,
    progressIndicatorStrokeWidth: Dp = ProgressIndicatorDefaults.CircularStrokeWidth,
    images: List<Uri>,
    imageSize: Dp = 40.dp,
    spaceBetween: Dp = 10.dp,
    imageShape: Shape = Shapes().small,
    onGalleryImageClicked: (Uri) -> Unit = {},
) {

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ),
        exit = fadeOut() + shrinkVertically(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    ) {
        BoxWithConstraints(
            modifier = modifier
        ) {
            val numberOfSlots by remember {
                derivedStateOf {
                    max(
                        a = 0,
                        b = this.maxWidth.div(spaceBetween + imageSize).toInt()
                    )
                }
            }

            val remainingImages by remember {
                derivedStateOf {
                    if (images.size == numberOfSlots) 0
                    else (images.size - numberOfSlots + 1)
                }
            }

            val showLastImageOverlay by remember {
                derivedStateOf {
                    remainingImages > 1
                }
            }

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(progressIndicatorSize),
                        strokeWidth = progressIndicatorStrokeWidth
                    )
                }
            }

            AnimatedVisibility(
                visible = !isLoading,
                enter = fadeIn()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spaceBetween),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    images.take(
                        if (showLastImageOverlay)
                            numberOfSlots - 1 //The last slot is reserved to show LastImageOverlay
                        else
                            numberOfSlots
                    ).forEach { imageUri ->
                        GalleryImage(
                            imageUri = imageUri,
                            size = imageSize,
                            shape = imageShape,
                            onGalleryImageClicked = onGalleryImageClicked
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

@Preview
@Composable
fun GalleryContainerPreview() {
    GalleryContainer(
        modifier = Modifier.width(190.dp),
        isVisible = true,
        isLoading = false,
        images = listOf(
            "image1.png".toUri(),
            "image2.png".toUri(),
            "image3.png".toUri(),
            "image4.png".toUri()
        )
    )
}