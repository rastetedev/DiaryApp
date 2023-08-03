package com.androiddevhispano.diaryapp.feature.write

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.data.repository.diary_manager.DiaryManager
import com.androiddevhispano.diaryapp.core_ui.models.Mood
import com.androiddevhispano.diaryapp.navigation.Screen.Companion.DIARY_ID_ARGUMENT
import com.androiddevhispano.diaryapp.ui.utils.createRemoteName
import com.androiddevhispano.diaryapp.data.mapper.createDiary
import com.androiddevhispano.diaryapp.data.mapper.toDiaryModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZonedDateTime

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val diaryManager: DiaryManager,
) : ViewModel() {

    private val _writeUiState = MutableStateFlow(WriteUiState())
    val writeUiState = _writeUiState.asStateFlow()

    init {
        getDiaryIdArgument()
        fetchSelectedDiary()
    }

    private fun getDiaryIdArgument() {
        _writeUiState.update {
            it.copy(
                diaryId = savedStateHandle[DIARY_ID_ARGUMENT]
            )
        }
    }

    private fun fetchSelectedDiary() {
        if (_writeUiState.value.diaryId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val diaryRequest =
                    diaryManager.getDiaryById(diaryId = _writeUiState.value.diaryId!!)
                        .map { it.toDiaryModel() }

                diaryRequest.fold(
                    { exception ->
                        withContext(Dispatchers.Main) {
                            _writeUiState.update {
                                it.copy(
                                    isLoadingImages = false,
                                    errorLoadingInfo = exception.message
                                )
                            }
                        }
                    }, {

                        withContext(Dispatchers.Main) {
                            setTitle(it.title)
                            setDescription(it.description)
                            setMood(it.mood)
                            setDate(it.date)

                            if (it.imagesUrl.isNotEmpty()) {
                                withContext(Dispatchers.IO) {
                                    diaryManager.fetchRemoteImageUriList(it.imagesUrl)
                                        .fold(
                                            { errorMessage ->
                                                withContext(Dispatchers.Main) {
                                                    _writeUiState.update {
                                                        it.copy(
                                                            isLoadingImages = false,
                                                            errorLoadingInfo = errorMessage
                                                        )
                                                    }
                                                }
                                            }, { list ->
                                                withContext(Dispatchers.Main) {
                                                    _writeUiState.update {
                                                        it.copy(
                                                            isLoadingImages = false,
                                                            imagesToShow = list.map { imageUri ->
                                                                GalleryImage(
                                                                    imageUri = imageUri,
                                                                    remoteImagePath = imageUri.toString()
                                                                )
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        )
                                }

                            } else {
                                _writeUiState.update {
                                    it.copy(
                                        isLoadingImages = false,
                                    )
                                }
                            }
                        }
                    }
                )
            }
        } else {
            _writeUiState.update {
                it.copy(
                    isLoadingImages = false,
                )
            }
        }
    }

    fun setTitle(title: String) {
        _writeUiState.update {
            it.copy(
                title = title
            )
        }
    }

    fun setDescription(description: String) {
        _writeUiState.update {
            it.copy(
                description = description
            )
        }
    }

    fun setMood(mood: Mood) {
        _writeUiState.update {
            it.copy(
                mood = mood
            )
        }
    }

    private fun setDate(date: Instant) {
        _writeUiState.update {
            it.copy(
                date = date
            )
        }
    }

    fun updateDate(date: ZonedDateTime) {
        _writeUiState.update {
            it.copy(
                updatedDate = date.toInstant()
            )
        }
    }

    fun returnToPreviousDate() {
        _writeUiState.update {
            it.copy(
                updatedDate = null
            )
        }
    }

    fun saveDiary(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        upsertDiary(onSuccess, onError)
    }

    private fun upsertDiary(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            val result = diaryManager.upsertDiary(
                diaryId = _writeUiState.value.diaryId,
                diary = createDiary(
                    id = _writeUiState.value.diaryId,
                    title = _writeUiState.value.title,
                    description = _writeUiState.value.description,
                    mood = _writeUiState.value.mood,
                    date = _writeUiState.value.updatedDate ?: run { _writeUiState.value.date },
                    imagesUrl = _writeUiState.value.imagesToShow.map { it.remoteImagePath }
                ),
                imageUriList = _writeUiState.value.imagesToShow.map { it.imageUri },
                imageRemotePathList = _writeUiState.value.imagesToShow.map { it.remoteImagePath },
                imagesToRemoveRemotePathList = _writeUiState.value.imagesToDelete.map { it.remoteImagePath }
            )

            withContext(Dispatchers.Main) {
                result.fold(
                    { onSuccess() },
                    { onError(it.message) }
                )
            }

        }
    }

    fun deleteDiary(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            if (_writeUiState.value.diaryId != null) {
                val diaryDeletedResult =
                    diaryManager.deleteDiary(
                        _writeUiState.value.diaryId!!,
                        _writeUiState.value.imagesToShow.map { it.remoteImagePath }
                    )

                withContext(Dispatchers.Main) {
                    diaryDeletedResult.fold(
                        { onSuccess() },
                        { onError(it.message) }
                    )
                }
            }
        }
    }

    fun addImage(
        imageUri: Uri,
        imageType: String
    ) {
        val remoteImagePath = imageUri.createRemoteName(imageType)
        _writeUiState.update {
            it.copy(
                imagesToShow = it.imagesToShow + GalleryImage(
                    imageUri = imageUri,
                    remoteImagePath = remoteImagePath
                )
            )
        }
    }

    fun removeImageFromGallery(imageUri: Uri) {
        _writeUiState.update {
            it.copy(
                imagesToDelete = it.imagesToDelete +
                        GalleryImage(
                            imageUri = imageUri,
                            remoteImagePath = imageUri.toString()
                        ),
                imagesToShow = it.imagesToShow.filter { gallery ->
                    gallery.imageUri != imageUri
                }
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
        val isLoadingImages: Boolean = true,
        val imagesToShow: List<GalleryImage> = emptyList(),
        val imagesToDelete: List<GalleryImage> = emptyList(),
        val errorLoadingInfo: String = ""
    )

    data class GalleryImage(
        val imageUri: Uri,
        val remoteImagePath: String = ""
    )
}




