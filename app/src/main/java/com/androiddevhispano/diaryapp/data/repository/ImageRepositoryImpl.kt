package com.androiddevhispano.diaryapp.data.repository

import com.androiddevhispano.diaryapp.data.localdatabase.ImageToDelete
import com.androiddevhispano.diaryapp.data.localdatabase.ImageToDeleteDao
import com.androiddevhispano.diaryapp.data.localdatabase.ImageToUpload
import com.androiddevhispano.diaryapp.data.localdatabase.ImageToUploadDao
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ImageRepository {

    override suspend fun addImageToUpload(
        remoteImagePath: String,
        imageUri: String,
        sessionUri: String
    ) {
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

    override suspend fun removeImageToUpload(imageId: Int) {
        imageToUploadDao.deleteImage(imageId)
    }

    override suspend fun addImageToDelete(remoteImagePath: String) {
        imageToDeleteDao.insertImage(ImageToDelete(remoteImagePath = remoteImagePath))
    }

    override suspend fun getImagesToDelete(): List<ImageToDelete> {
        return imageToDeleteDao.getAllImages()
    }

    override suspend fun removeImageToDelete(imageId: Int) {
        imageToDeleteDao.deleteImage(imageId)
    }
}