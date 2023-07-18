package com.androiddevhispano.diaryapp.screens.write.gallery

import androidx.compose.runtime.mutableStateListOf
import com.androiddevhispano.diaryapp.models.GalleryImage

class GalleryState {
    val images = mutableStateListOf<GalleryImage>()
    val imagesToBeDeleted = mutableStateListOf<GalleryImage>()

    fun addImage(galleryImage: GalleryImage) {
        images.add(galleryImage)
    }

    fun removeImage(galleryImage: GalleryImage) {
        images.removeIf { galleryImage.imageUri == it.imageUri }
        imagesToBeDeleted.add(galleryImage)
    }
}