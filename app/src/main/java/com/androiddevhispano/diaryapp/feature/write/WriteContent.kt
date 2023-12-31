package com.androiddevhispano.diaryapp.feature.write

import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.androiddevhispano.diaryapp.R
import com.androiddevhispano.diaryapp.core_ui.models.Mood
import com.androiddevhispano.diaryapp.feature.write.gallery.GalleryUploader
import com.androiddevhispano.diaryapp.ui.theme.Size.doubleExtraLarge
import com.androiddevhispano.diaryapp.ui.theme.Size.extraLarge

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WriteContent(
    modifier: Modifier = Modifier,
    moodPagerState: PagerState,
    imagesToShow: List<WriteViewModel.GalleryImage>,
    buttonEnabledState: Boolean,
    title: String,
    onTitleChanged: (title: String) -> Unit,
    description: String,
    onDescriptionChanged: (description: String) -> Unit,
    onImagePickedFromGallery: (imageUri: Uri) -> Unit,
    onGalleryImageClicked: (imageUri: Uri) -> Unit,
    isDownloadingImages: Boolean,
    onSaveButtonClicked: () -> Unit
) {

    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(key1 = scrollState.maxValue) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(doubleExtraLarge))
            HorizontalPager(
                state = moodPagerState
            ) { page ->
                AsyncImage(
                    modifier = Modifier.height(120.dp).fillMaxWidth(),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Mood.values()[page].icon)
                        .crossfade(true)
                        .build(),
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            TextField(
                modifier = Modifier
                    .fillMaxWidth(),
                value = title,
                onValueChange = onTitleChanged,
                placeholder = {
                    Text(text = stringResource(id = R.string.title))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.moveFocus(FocusDirection.Down)
                    }
                ),
                maxLines = 1,
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = description,
                onValueChange = onDescriptionChanged,
                placeholder = {
                    Text(text = stringResource(id = R.string.description_placeholder))
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Unspecified,
                    disabledIndicatorColor = Color.Unspecified,
                    unfocusedIndicatorColor = Color.Unspecified,
                    unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    focusedPlaceholderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Text,
                    capitalization = KeyboardCapitalization.Sentences
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        focusManager.clearFocus()
                    }
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = doubleExtraLarge),
            verticalArrangement = Arrangement.spacedBy(extraLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GalleryUploader(
                modifier = Modifier.fillMaxWidth(),
                //galleryState = galleryState,
                imagesToShow = imagesToShow,
                onAddClicked = {
                    focusManager.clearFocus()
                },
                onImagePickedFromGallery = onImagePickedFromGallery,
                onGalleryImageClicked = onGalleryImageClicked,
                isDownloadingImages = isDownloadingImages
            )
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = buttonEnabledState,
                shape = Shapes().small,
                onClick = onSaveButtonClicked
            ) {
                Text(text = stringResource(id = R.string.save))
            }
        }
    }
}