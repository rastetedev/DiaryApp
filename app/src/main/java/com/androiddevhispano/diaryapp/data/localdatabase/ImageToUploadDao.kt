package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageToUploadDao {
    @Query("SELECT * FROM ImageToUpload ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToUpload>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageToUpload: ImageToUpload)

    @Query("DELETE FROM ImageToUpload WHERE id = :imageId")
    suspend fun deleteImage(imageId: Int)
}