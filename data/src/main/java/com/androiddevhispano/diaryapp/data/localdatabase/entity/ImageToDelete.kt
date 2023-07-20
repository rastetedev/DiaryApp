package com.androiddevhispano.diaryapp.data.localdatabase.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ImageToDelete(
    @PrimaryKey val id: Int = 0,
    val remoteImagePath: String
)
