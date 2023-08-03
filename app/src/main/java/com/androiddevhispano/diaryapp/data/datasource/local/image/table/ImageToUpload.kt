package com.androiddevhispano.diaryapp.data.datasource.local.image.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageToUpload(
    @PrimaryKey val id: Int = 0,
    val remoteImagePath: String,
    val localImageUri: String,
    val uploadSessionUri: String
)
