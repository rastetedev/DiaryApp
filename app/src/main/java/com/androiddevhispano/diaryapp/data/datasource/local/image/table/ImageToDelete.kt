package com.androiddevhispano.diaryapp.data.datasource.local.image.table

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageToDelete(
    @PrimaryKey val id: Int = 0,
    val remoteImagePath: String
)
