package com.androiddevhispano.diaryapp.data.localdatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ImageDao {
    @Query("SELECT * FROM image ORDER BY id ASC")
    suspend fun getAllImages(): List<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: Image)

    @Query("DELETE FROM image WHERE id = :imageId")
    suspend fun deleteImage(imageId: Int)
}