package com.androiddevhispano.diaryapp.data.utils

import android.net.Uri
import androidx.core.net.toUri
import com.androiddevhispano.diaryapp.data.localdb.table.ImageToUpload
import com.androiddevhispano.diaryapp.models.GalleryImage
import com.androiddevhispano.diaryapp.screens.write.gallery.GalleryState
import com.androiddevhispano.diaryapp.ui.utils.FIREBASE_IMAGES_DIRECTORY
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storageMetadata

fun fetchImagesFromFirebase(
    remoteImageUrlList: List<String>,
    onImageDownload: (Uri) -> Unit,
    onBucketDownloadSuccess: (() -> Unit) = {},
    onBucketDownloadFail: ((Exception) -> Unit) = {}
) {
    if (remoteImageUrlList.isNotEmpty()) {
        remoteImageUrlList.forEachIndexed { index, remoteImageUrl ->
            if (remoteImageUrl.trim().isNotEmpty()) {
                FirebaseStorage.getInstance().reference.child(remoteImageUrl.trim()).downloadUrl
                    .addOnSuccessListener { uri ->
                        onImageDownload(uri)
                        if (remoteImageUrlList.lastIndexOf(remoteImageUrlList.last()) == index) {
                            onBucketDownloadSuccess()
                        }
                    }
                    .addOnFailureListener { exception ->
                        onBucketDownloadFail(exception)
                    }
            }
        }
    }
}

fun retryUploadImageToFirebase(
    image: ImageToUpload,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(image.remoteImagePath).putFile(
        image.imageUri.toUri(),
        storageMetadata {},
        image.sessionUri.toUri()
    ).addOnSuccessListener { onSuccess() }
}

fun deleteImagesFromFirebase(
    remoteImagePathList: List<String>,
    onDeleteFail: (remoteImagePath: String) -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    remoteImagePathList.forEach { remoteImagePath ->
        storage.child(remoteImagePath).delete()
            .addOnFailureListener {
                onDeleteFail(remoteImagePath)
            }
    }
}

fun retryDeletingImageFromFirebase(
    remoteImagePath: String,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(remoteImagePath).delete()
        .addOnSuccessListener { onSuccess() }
}


fun uploadImagesToFirebase(
    galleryState: GalleryState,
    onUploadFail: (GalleryImage, Uri?) -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    galleryState.images.forEach { galleryImage ->
        val imagePath = storage.child(galleryImage.remoteImagePath)
        imagePath.putFile(galleryImage.imageUri)
            .addOnProgressListener {
                val sessionUri = it.uploadSessionUri
                if (sessionUri != null) {
                    onUploadFail(galleryImage, sessionUri)
                }
            }
    }
}

fun deleteAllImagesForAllDiaries(
    onDeleteImageFail: (imageRemotePath: String) -> Unit,
    onError: () -> Unit
) {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val imagesDirectory = "$FIREBASE_IMAGES_DIRECTORY/$userId"
    val storage = FirebaseStorage.getInstance().reference
    storage.child(imagesDirectory).listAll()
        .addOnSuccessListener {
            it.items.forEach { ref ->
                val imageRemotePath = "$FIREBASE_IMAGES_DIRECTORY/$userId/${ref.name}"
                storage.child(imageRemotePath).delete()
                    .addOnFailureListener {
                        onDeleteImageFail(imageRemotePath)
                    }
            }
        }
        .addOnFailureListener {
            onError()
        }
}