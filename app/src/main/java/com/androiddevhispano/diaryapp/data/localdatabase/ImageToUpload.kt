package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Entity

@Entity
data class ImageToUpload(
    val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)
