package com.androiddevhispano.diaryapp.data.localdatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.androiddevhispano.diaryapp.data.localdatabase.entity.ImageToDelete

@Dao
interface ImageToDeleteDao {
    @Query("SELECT * FROM ImageToDelete ORDER BY id ASC")
    suspend fun getAllImages(): List<ImageToDelete>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(imageToDelete: ImageToDelete)

    @Query("DELETE FROM ImageToDelete WHERE id = :imageId")
    suspend fun deleteImage(imageId: Int)
}