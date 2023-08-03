package com.androiddevhispano.diaryapp.feature.home

import android.net.Uri
import androidx.compose.runtime.compositionLocalOf

data class GalleryState(
    val diaryId: String = "",
    val isOpen: Boolean = false,
    val isLoading: Boolean = false,
    val urlList: List<Uri> = emptyList()
)

val LocalGalleryState = compositionLocalOf { GalleryState() }
