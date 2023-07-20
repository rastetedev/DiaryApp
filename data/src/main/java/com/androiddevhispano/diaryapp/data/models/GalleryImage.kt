package com.androiddevhispano.diaryapp.data.models

import android.net.Uri

data class GalleryImage(
    val imageUri: Uri,
    val remoteImagePath: String = "",
)