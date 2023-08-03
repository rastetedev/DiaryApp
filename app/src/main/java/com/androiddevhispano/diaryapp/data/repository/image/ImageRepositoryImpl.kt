package com.androiddevhispano.diaryapp.data.repository.image

import android.net.Uri
import androidx.core.net.toUri
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import com.androiddevhispano.diaryapp.data.datasource.local.image.table.ImageToDelete
import com.androiddevhispano.diaryapp.data.datasource.local.image.dao.ImageToDeleteDao
import com.androiddevhispano.diaryapp.data.datasource.local.image.table.ImageToUpload
import com.androiddevhispano.diaryapp.data.datasource.local.image.dao.ImageToUploadDao
import com.androiddevhispano.diaryapp.data.repository.DomainException
import com.androiddevhispano.diaryapp.ui.utils.FIREBASE_IMAGES_DIRECTORY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageRepositoryImpl @Inject constructor(
    private val imageToUploadDao: ImageToUploadDao,
    private val imageToDeleteDao: ImageToDeleteDao
) : ImageRepository {

    /** HOME **/
    override suspend fun deleteAllImages(): Option<DomainException> {
        return try {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val imagesDirectory = "$FIREBASE_IMAGES_DIRECTORY/$userId"
            val storage = FirebaseStorage.getInstance().reference

            val imagesResult = suspendCoroutine { continuation ->
                storage.child(imagesDirectory).listAll()
                    .addOnSuccessListener { images ->
                        continuation.resume(images.items)
                    }
                    .addOnFailureListener {
                        continuation.resume(emptyList<StorageReference>())
                    }
            }

            val imagesToDeleteResult = suspendCoroutine<List<String>> { continuation ->
                val imagesToDelete = mutableListOf<String>()

                imagesResult.forEach { reference ->
                    val imageRemotePath = "$FIREBASE_IMAGES_DIRECTORY/$userId/${reference.name}"
                    storage.child(imageRemotePath).delete()
                        .addOnFailureListener {
                            imagesToDelete.add(imageRemotePath)
                        }
                }
                continuation.resume(imagesToDelete)
            }

            imagesToDeleteResult.forEach {
                imageToDeleteDao.insertImage(ImageToDelete(remoteImagePath = it))
            }
            None
        } catch (e: Exception) {
            Some(DomainException.GeneralException(e.message))
        }
    }

    override suspend fun uploadImages(
        imageUriList: List<Uri>,
        imageRemotePathList: List<String>
    ): Option<DomainException> {

        return try {
            val storage = FirebaseStorage.getInstance().reference
            val imagesToUploadResult = suspendCoroutine<List<ImageToUpload>> { continuation ->
                val imagesToUpload = mutableListOf<ImageToUpload>()
                imageRemotePathList.forEachIndexed { index, remotePath ->
                    val imagePath = storage.child(remotePath)
                    imagePath.putFile(imageUriList[index])
                        .addOnProgressListener {
                            val sessionUri = it.uploadSessionUri
                            if (sessionUri != null) {
                                imagesToUpload.add(
                                    ImageToUpload(
                                        remoteImagePath = remotePath,
                                        localImageUri = imageUriList[index].toString(),
                                        uploadSessionUri = sessionUri.toString()
                                    )
                                )
                            }
                        }
                }
                continuation.resume(imagesToUpload)
            }

            imagesToUploadResult.forEach {
                addImageToUpload(it)
            }
            None
        } catch (e: Exception) {
            Some(DomainException.GeneralException(e.message))
        }
    }

    override suspend fun deleteImages(imageRemotePathList: List<String>): Option<DomainException> {
        return try {
            val storage = FirebaseStorage.getInstance().reference
            val imagesToDeleteResult = suspendCoroutine<List<ImageToDelete>> { continuation ->
                val imagesToDelete = mutableListOf<ImageToDelete>()
                imageRemotePathList.forEach { remoteImagePath ->
                    storage.child(remoteImagePath).delete()
                        .addOnFailureListener {
                            imagesToDelete.add(ImageToDelete(remoteImagePath = remoteImagePath))
                        }
                }
                continuation.resume(imagesToDelete)
            }

            imagesToDeleteResult.forEach {
                addImageToDelete(it)
            }
            None
        } catch (e: Exception) {
            Some(DomainException.GeneralException(e.message))
        }
    }

    override suspend fun retryImagesToUpload() {
        val imagesToUpload = getImagesToUpload()
        val storage = FirebaseStorage.getInstance().reference

        val imagesToUploadResult = suspendCoroutine<List<ImageToUpload>> { continuation ->
            val imagesUploadedSuccess = mutableListOf<ImageToUpload>()
            imagesToUpload.forEach { imageToUpload ->
                storage.child(imageToUpload.remoteImagePath).putFile(
                    imageToUpload.localImageUri.toUri(),
                    storageMetadata {},
                    imageToUpload.uploadSessionUri.toUri()
                ).addOnSuccessListener {
                    imagesUploadedSuccess.add(imageToUpload)
                }
            }
            continuation.resume(imagesUploadedSuccess)
        }

        imagesToUploadResult.forEach {
            removeImageToUpload(it.id)
        }
    }

    override suspend fun retryImagesToDelete() {
        val imagesToDelete = getImagesToDelete()
        val storage = FirebaseStorage.getInstance().reference

        val imagesToDeleteResult = suspendCoroutine<List<ImageToDelete>> { continuation ->
            val imagesDeletedSuccess = mutableListOf<ImageToDelete>()
            imagesToDelete.forEach { imageToDelete ->
                storage.child(imageToDelete.remoteImagePath).delete()
                    .addOnSuccessListener {
                        imagesDeletedSuccess.add(imageToDelete)
                    }
            }
            continuation.resume(imagesDeletedSuccess)
        }

        imagesToDeleteResult.forEach {
            removeImageToDelete(it.id)
        }
    }

    override suspend fun addImageToUpload(imageToUpload: ImageToUpload) {
        imageToUploadDao.insertImage(imageToUpload)
    }

    override suspend fun removeImageToUpload(imageToUploadId: Int) {
        imageToUploadDao.deleteImage(imageToUploadId)
    }

    override suspend fun getImagesToUpload(): List<ImageToUpload> {
        return imageToUploadDao.getAllImages()
    }

    override suspend fun addImageToDelete(imageToDelete: ImageToDelete) {
        imageToDeleteDao.insertImage(imageToDelete)
    }

    override suspend fun removeImageToDelete(imageToDeleteId: Int) {
        imageToDeleteDao.deleteImage(imageToDeleteId)
    }

    override suspend fun getImagesToDelete(): List<ImageToDelete> {
        return imageToDeleteDao.getAllImages()
    }
}
