package com.androiddevhispano.diaryapp.data.repository.image

import com.androiddevhispano.diaryapp.data.localdatabase.entity.ImageToDelete
import com.androiddevhispano.diaryapp.data.localdatabase.entity.ImageToUpload

interface ImageRepository {

    suspend fun addImageToUpload(remoteImagePath: String, imageUri:String, sessionUri: String)

    suspend fun getImagesToUpload() : List<ImageToUpload>

    suspend fun removeImageToUpload(imageId: Int)

    suspend fun addImageToDelete(remoteImagePath: String)

    suspend fun getImagesToDelete() : List<ImageToDelete>

    suspend fun removeImageToDelete(imageId: Int)
}