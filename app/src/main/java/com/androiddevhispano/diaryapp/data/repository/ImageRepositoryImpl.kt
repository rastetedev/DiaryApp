package com.androiddevhispano.diaryapp.data.repository

import com.androiddevhispano.diaryapp.data.localdatabase.ImageToUpload
import com.androiddevhispano.diaryapp.data.localdatabase.ImageToUploadDao
import javax.inject.Inject


class ImageRepositoryImpl @Inject constructor(private val imageToUploadDao: ImageToUploadDao) : ImageRepository {

    override suspend fun addImageToUpload(remoteImagePath: String, imageUri: String, sessionUri: String) {
        imageToUploadDao.insertImage(
            ImageToUpload(
                remoteImagePath = remoteImagePath,
                imageUri = imageUri,
                sessionUri = sessionUri
            )
        )
    }

    override suspend fun getImagesToUpload(): List<ImageToUpload> {
        return imageToUploadDao.getAllImages()
    }

    override suspend fun cleanupImageToUpload(imageId: Int) {
        imageToUploadDao.deleteImage(imageId)
    }
}