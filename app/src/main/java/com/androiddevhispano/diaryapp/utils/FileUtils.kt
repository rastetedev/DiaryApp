package com.androiddevhispano.diaryapp.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

const val FIREBASE_IMAGES_DIRECTORY = "images"

fun Uri.getType(context: Context): String {
    return context.contentResolver
        .getType(this)
        ?.split("/")
        ?.last() ?: "jpg"
}

infix fun Uri.createNameWith(imageType: String): String {
    return "$FIREBASE_IMAGES_DIRECTORY/" +
            "${Firebase.auth.currentUser?.uid}/" +
            "${this.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
}

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

fun String.extractImagePath(): String {
    val chunks = split("%2F")
    val imageName = chunks[2].split("?").first()
    return "$FIREBASE_IMAGES_DIRECTORY/${Firebase.auth.currentUser?.uid}/$imageName"
}
