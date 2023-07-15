package com.androiddevhispano.diaryapp.models

import android.net.Uri

data class GalleryImage(
    val imageUri: Uri,
    val remoteImagePath: String = "",
)