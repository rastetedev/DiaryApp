package com.androiddevhispano.diaryapp.ui.utils

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

const val FIREBASE_IMAGES_DIRECTORY = "images"

fun Uri.getType(context: Context): String {
    return context.contentResolver
        .getType(this)
        ?.split("/")
        ?.last() ?: "jpg"
}

fun Uri.createRemoteName(imageType: String): String {
    return "$FIREBASE_IMAGES_DIRECTORY/" +
            "${Firebase.auth.currentUser?.uid}/" +
            "${this.lastPathSegment}-${System.currentTimeMillis()}.$imageType"
}

fun Uri.extractImagePath(): String {
    val chunks = this.toString().split("%2F")
    val imageName = chunks[2].split("?").first()
    return "$FIREBASE_IMAGES_DIRECTORY/${Firebase.auth.currentUser?.uid}/$imageName"
}
