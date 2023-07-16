package com.androiddevhispano.diaryapp.screens.home.diary

import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.ui.theme.Elevation
import com.androiddevhispano.diaryapp.ui.theme.Size.doubleExtraLarge
import com.androiddevhispano.diaryapp.ui.theme.Size.extraLarge
import com.androiddevhispano.diaryapp.ui.theme.Size.extraSmall
import com.androiddevhispano.diaryapp.ui.theme.Size.tiny
import com.androiddevhispano.diaryapp.utils.fetchImagesFromFirebase
import com.androiddevhispano.diaryapp.utils.toInstant
import io.realm.kotlin.ext.realmListOf

@Composable
fun DiaryHolder(
    diary: Diary,
    onDiaryClicked: (diaryId: String) -> Unit
) {
    val localDensity = LocalDensity.current
    val context = LocalContext.current
    var componentHeight by remember { mutableStateOf(0.dp) }
    var galleryOpened by rememberSaveable { mutableStateOf(false) }
    var galleryLoading by rememberSaveable { mutableStateOf(false) }
    val downloadedImages = remember { mutableStateListOf<Uri>() }

    LaunchedEffect(key1 = galleryOpened) {
        if (galleryOpened && downloadedImages.isEmpty()) {
            galleryLoading = true
            fetchImagesFromFirebase(
                remoteImageUrlList = diary.images,
                onImageDownload = { uriDownloaded ->
                    downloadedImages.add(uriDownloaded)
                },
                onBucketDownloadSuccess = {
                    galleryLoading = false
                },
                onBucketDownloadFail = {
                    galleryLoading = false
                    galleryOpened = false
                    Toast.makeText(
                        context,
                        context.getString(R.string.download_images_error),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
        }
    }

    Row(modifier = Modifier
        .clickable(
            indication = null,
            interactionSource = remember {
                MutableInteractionSource()
            }
        ) {
            onDiaryClicked(diary._id.toHexString())
            galleryOpened = false
        }
    ) {
        Spacer(modifier = Modifier.width(extraLarge))
        Surface(
            modifier = Modifier
                .width(extraSmall)
                .height(componentHeight + doubleExtraLarge),
            tonalElevation = Elevation.Level1
        ) {}
        Spacer(modifier = Modifier.width(doubleExtraLarge))
        Surface(
            modifier = Modifier
                .clip(shape = Shapes().medium)
                .onGloballyPositioned {
                    componentHeight = with(localDensity) { it.size.height.toDp() }
                },
            tonalElevation = Elevation.Level1
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                DiaryHeader(moodName = diary.mood, time = diary.date.toInstant())
                Text(
                    modifier = Modifier.padding(all = extraLarge),
                    text = diary.description,
                    style = TextStyle(fontSize = MaterialTheme.typography.bodyLarge.fontSize),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
                if (diary.images.isNotEmpty()) {
                    ShowGalleryButton(
                        galleryOpened = galleryOpened,
                        onClick = {
                            galleryOpened = !galleryOpened
                        }
                    )
                }
                AnimatedVisibility(
                    visible = galleryLoading,
                    enter = fadeIn() + expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(extraLarge),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = extraSmall
                        )
                    }
                }
                AnimatedVisibility(
                    visible = galleryOpened && !galleryLoading,
                    enter = fadeIn() + expandVertically(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Gallery(
                        modifier = Modifier
                            .padding(
                                top = tiny,
                                bottom = extraLarge,
                                start = extraLarge,
                                end = extraLarge
                            ),
                        imageUriList = downloadedImages
                    )
                }
            }
        }
    }

}

@Composable
fun ShowGalleryButton(
    galleryOpened: Boolean,
    onClick: () -> Unit
) {
    TextButton(onClick = onClick) {
        Text(
            text = if (galleryOpened)
                stringResource(id = R.string.hide_gallery)
            else
                stringResource(id = R.string.show_gallery),
            style = TextStyle(fontSize = MaterialTheme.typography.bodySmall.fontSize)
        )
    }
}

@Composable
@Preview
fun DiaryHolderPreview() {
    DiaryHolder(diary = Diary().apply {
        title = "This a diary title to test"
        description = "This is a diary description to test how looks on the app"
        images = realmListOf("image1.png", "image2.png", "image3.png")
    }, onDiaryClicked = {})
}