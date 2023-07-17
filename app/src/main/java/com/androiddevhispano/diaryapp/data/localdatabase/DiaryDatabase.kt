package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [Image::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun imageDao(): ImageDao
}