package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ImageToUpload::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageToUploadDao
}