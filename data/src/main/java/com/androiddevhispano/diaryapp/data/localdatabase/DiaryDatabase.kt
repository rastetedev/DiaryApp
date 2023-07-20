package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androiddevhispano.diaryapp.data.localdatabase.dao.ImageToDeleteDao
import com.androiddevhispano.diaryapp.data.localdatabase.dao.ImageToUploadDao
import com.androiddevhispano.diaryapp.data.localdatabase.entity.ImageToDelete
import com.androiddevhispano.diaryapp.data.localdatabase.entity.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao

    abstract fun imageToDeleteDao() : ImageToDeleteDao
}