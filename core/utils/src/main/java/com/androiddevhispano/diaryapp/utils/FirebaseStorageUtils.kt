package com.androiddevhispano.diaryapp.utils

import android.net.Uri
import androidx.core.net.toUri
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
    remoteImagePath: String,
    imageUri: String,
    sessionUri: String,
    onSuccess: () -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference
    storage.child(remoteImagePath).putFile(
        imageUri.toUri(),
        storageMetadata {},
        sessionUri.toUri()
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
    imageUriList: List<Uri>,
    imageRemotePathList: List<String>,
    onUploadFail: (imageUri: Uri, imageRemotePath: String, sessionUri: Uri?) -> Unit
) {
    val storage = FirebaseStorage.getInstance().reference

    imageRemotePathList.forEachIndexed { index, remotePath ->
        val storageReference = storage.child(remotePath)
        storageReference.putFile(imageUriList[index])
            .addOnProgressListener {
                val sessionUri = it.uploadSessionUri
                if (sessionUri != null) {
                    onUploadFail(imageUriList[index], imageRemotePathList[index], sessionUri)
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