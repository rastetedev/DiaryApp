package com.androiddevhispano.diaryapp.data.repository.image

import android.net.Uri
import arrow.core.Either
import arrow.core.Option
import com.androiddevhispano.diaryapp.data.datasource.local.image.table.ImageToDelete
import com.androiddevhispano.diaryapp.data.datasource.local.image.table.ImageToUpload
import com.androiddevhispano.diaryapp.data.repository.DomainException

interface ImageRepository {

    /** HOME **/
    suspend fun deleteAllImages(): Option<DomainException>

    /** WRITE **/
    suspend fun uploadImages(
        imageUriList: List<Uri>,
        imageRemotePathList: List<String>
    ): Either<DomainException, Unit>

    suspend fun deleteImages(imageRemotePathList: List<String>):  Either<DomainException, Unit>

    suspend fun retryImagesToUpload()

    suspend fun retryImagesToDelete()

    suspend fun addImageToUpload(imageToUpload: ImageToUpload)

    suspend fun removeImageToUpload(imageToUploadId: Int)

    suspend fun getImagesToUpload(): List<ImageToUpload>

    suspend fun addImageToDelete(imageToDelete: ImageToDelete)

    suspend fun removeImageToDelete(imageToDeleteId: Int)

    suspend fun getImagesToDelete(): List<ImageToDelete>

}