package com.androiddevhispano.diaryapp.data.localdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.androiddevhispano.diaryapp.data.localdb.table.ImageToDelete
import com.androiddevhispano.diaryapp.data.localdb.dao.ImageToDeleteDao
import com.androiddevhispano.diaryapp.data.localdb.dao.ImageToUploadDao
import com.androiddevhispano.diaryapp.data.localdb.table.ImageToUpload

@Database(
    entities = [ImageToUpload::class, ImageToDelete::class],
    version = 1
)
abstract class DiaryDatabase : RoomDatabase() {
    abstract fun imageToUploadDao(): ImageToUploadDao

    abstract fun imageToDeleteDao() : ImageToDeleteDao
}