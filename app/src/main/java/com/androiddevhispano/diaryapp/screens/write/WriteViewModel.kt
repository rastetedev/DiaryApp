package com.androiddevhispano.diaryapp.screens.write

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.data.repository.DiaryRepositoryImpl
import com.androiddevhispano.diaryapp.data.repository.ImageRepository
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.models.GalleryImage
import com.androiddevhispano.diaryapp.models.Mood
import com.androiddevhispano.diaryapp.navigation.Screen.Companion.DIARY_ID_ARGUMENT
import com.androiddevhispano.diaryapp.screens.write.gallery.GalleryState
import com.androiddevhispano.diaryapp.utils.RequestState
import com.androiddevhispano.diaryapp.utils.createRemoteNameWith
import com.androiddevhispano.diaryapp.utils.deleteImagesFromFirebase
import com.androiddevhispano.diaryapp.utils.extractImagePath
import com.androiddevhispano.diaryapp.utils.fetchImagesFromFirebase
import com.androiddevhispano.diaryapp.utils.toInstant
import com.androiddevhispano.diaryapp.utils.toRealmInstant
import com.androiddevhispano.diaryapp.utils.uploadImagesToFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import io.realm.kotlin.ext.toRealmList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.Instant
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class WriteViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val imageRepository: ImageRepository
) : ViewModel() {

    var uiState by mutableStateOf(WriteUiState())
        private set

    val galleryState = GalleryState()

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        uiState = uiState.copy(
            diaryId = savedStateHandle[DIARY_ID_ARGUMENT]
        )
    }

    private fun fetchSelectedDiary() {
        if (uiState.diaryId != null) {
            viewModelScope.launch {
                val diaryRequest =
                    DiaryRepositoryImpl.getDiaryById(diaryId = ObjectId.invoke(uiState.diaryId!!))
                if (diaryRequest is RequestState.Success) {
                    with(diaryRequest.data) {
                        setTitle(title)
                        setDescription(description)
                        setMood(Mood.valueOf(mood))
                        setDate(date.toInstant())

                        if (images.isNotEmpty()) {
                            uiState = uiState.copy(
                                isDownloadingImages = true
                            )
                        }

                        fetchImagesFromFirebase(
                            remoteImageUrlList = images,
                            onImageDownload = { uriDownloaded ->
                                galleryState.addImage(
                                    GalleryImage(
                                        imageUri = uriDownloaded,
                                        remoteImagePath = uriDownloaded.toString()
                                            .extractImagePath()
                                    )
                                )
                            },
                            onBucketDownloadSuccess = {
                                uiState = uiState.copy(
                                    isDownloadingImages = false
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    fun setTitle(title: String) {
        uiState = uiState.copy(title = title)
    }

    fun setDescription(description: String) {
        uiState = uiState.copy(description = description)
    }

    fun setMood(mood: Mood) {
        uiState = uiState.copy(mood = mood)
    }

    private fun setDate(date: Instant) {
        uiState = uiState.copy(date = date)
    }

    fun updateDate(date: ZonedDateTime) {
        uiState = uiState.copy(
            updatedDate = date.toInstant()
        )
    }

    fun returnToPreviousDate() {
        uiState = uiState.copy(
            updatedDate = null
        )
    }

    fun saveDiary(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        upsertDiary(onSuccess, onError)
    }

    private fun upsertDiary(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val diarySavedResult = DiaryRepositoryImpl.insertDiary(
                Diary().apply {
                    title = uiState.title
                    description = uiState.description
                    mood = uiState.mood.name
                    date = uiState.updatedDate?.toRealmInstant()
                        ?: run { uiState.date.toRealmInstant() }
                    images = galleryState.images.map { it.remoteImagePath }.toRealmList()

                    if (uiState.diaryId != null) {
                        _id = ObjectId.invoke(uiState.diaryId!!)
                    }
                }

            )
            withContext(Dispatchers.Main) {
                if (diarySavedResult is RequestState.Success) {
                    withContext(Dispatchers.IO) {
                        uploadImagesToFirebase(
                            galleryState = galleryState,
                            onUploadFail = { galleryImage, sessionUri ->
                                viewModelScope.launch(Dispatchers.IO) {
                                    imageRepository.addImageToUpload(
                                        remoteImagePath = galleryImage.remoteImagePath,
                                        imageUri = galleryImage.imageUri.toString(),
                                        sessionUri = sessionUri.toString()
                                    )
                                }
                            }
                        )
                        if(uiState.diaryId != null){
                            deleteImagesFromFirebase(
                                galleryState.imagesToBeDeleted.map { it.remoteImagePath }
                            )
                        }
                    }
                    onSuccess()
                } else onError()
            }
        }
    }

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (uiState.diaryId != null) {
                val diaryDeletedResult =
                    DiaryRepositoryImpl.deleteDiary(ObjectId.invoke(uiState.diaryId!!))
                withContext(Dispatchers.Main) {
                    if (diaryDeletedResult is RequestState.Success) {
                        deleteImagesFromFirebase(
                            images = galleryState.images.map { it.remoteImagePath }
                        )
                        onSuccess()
                    } else onError()
                }
            }
        }
    }

    fun addImage(
        imageUri: Uri,
        imageType: String
    ) {
        val remoteImagePath = imageUri createRemoteNameWith imageType
        galleryState.addImage(
            GalleryImage(
                imageUri = imageUri,
                remoteImagePath = remoteImagePath
            )
        )
    }
}


data class WriteUiState(
    val diaryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val date: Instant = Instant.now(),
    val updatedDate: Instant? = null,
    val isDownloadingImages: Boolean = false
)
