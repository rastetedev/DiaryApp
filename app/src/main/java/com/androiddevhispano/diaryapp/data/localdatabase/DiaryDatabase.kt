package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao

    abstract fun imageToDeleteDao() : ImageToDeleteDao
}