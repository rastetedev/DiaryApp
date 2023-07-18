package com.androiddevhispano.diaryapp.data.repository

import com.androiddevhispano.diaryapp.data.localdatabase.ImageToDelete
import com.androiddevhispano.diaryapp.data.localdatabase.ImageToUpload

interface ImageRepository {

    suspend fun addImageToUpload(remoteImagePath: String, imageUri:String, sessionUri: String)

    suspend fun getImagesToUpload() : List<ImageToUpload>

    suspend fun cleanupImageToUpload(imageId: Int)

    suspend fun addImageToDelete(remoteImagePath: String)

    suspend fun getImagesToDelete() : List<ImageToDelete>

    suspend fun cleanupImageToDelete(imageId: Int)
}