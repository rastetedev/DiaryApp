package com.androiddevhispano.diaryapp.data.localdatabase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageToUpload(
    @PrimaryKey val id: Int = 0,
    val remoteImagePath: String,
    val imageUri: String,
    val sessionUri: String
)
