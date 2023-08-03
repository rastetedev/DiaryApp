package com.androiddevhispano.diaryapp.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androiddevhispano.diaryapp.data.datasource.local.image.table.ImageToDelete
import com.androiddevhispano.diaryapp.data.datasource.local.image.dao.ImageToDeleteDao
import com.androiddevhispano.diaryapp.data.datasource.local.image.dao.ImageToUploadDao
import com.androiddevhispano.diaryapp.data.datasource.local.image.table.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao

    abstract fun imageToDeleteDao() : ImageToDeleteDao
}