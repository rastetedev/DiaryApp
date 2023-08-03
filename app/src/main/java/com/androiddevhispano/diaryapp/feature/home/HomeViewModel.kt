package com.androiddevhispano.diaryapp.feature.home

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.data.repository.diary_manager.DiaryManager
import com.androiddevhispano.diaryapp.ui.connectivity.ConnectivityObserver
import com.androiddevhispano.diaryapp.data.mapper.toDiaryCard
import com.androiddevhispano.diaryapp.core_ui.models.Mood
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.ZonedDateTime

class HomeViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val diaryManager: DiaryManager
) : ViewModel() {

    private lateinit var allDiariesObserverJob: Job
    private lateinit var filteredDiariesObserverJob: Job

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private val _galleryState = MutableStateFlow(GalleryState())
    val galleryState = _galleryState.asStateFlow()

    private var networkStatus by mutableStateOf(ConnectivityObserver.Status.UNAVAILABLE)

    init {
        getDiaries(null)
        observeNetwork()
    }

    fun getDiaries(specificDateSelected: ZonedDateTime? = null) {
        _homeUiState.update {
            it.copy(
                specificDateSelected = specificDateSelected,
                isLoading = true
            )
        }
        if (specificDateSelected != null) {
            observeFilteredDiaries(specificDateSelected)
        } else {
            observeAllDiaries()
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connectivityObserver.observe().collect {
                networkStatus = it
            }
        }
    }

    private fun observeAllDiaries() {
        allDiariesObserverJob = viewModelScope.launch(Dispatchers.IO) {
            if (::filteredDiariesObserverJob.isInitialized) {
                filteredDiariesObserverJob.cancelAndJoin()
            }
            diaryManager.getAllDiaries()
                .collect { result ->
                    withContext(Dispatchers.Main) {
                        result.fold(
                            {
                                _homeUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        diaries = emptyMap()
                                    )
                                }
                            },
                            { list ->
                                _homeUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        diaries = list
                                            .map { diary ->
                                                diary.toDiaryCard()
                                            }
                                            .groupBy { diaryCard ->
                                                diaryCard.date.toLocalDate()
                                            }
                                    )
                                }
                            }
                        )
                    }
                }
        }
    }

    private fun observeFilteredDiaries(specificDateToShowDiaries: ZonedDateTime) {
        filteredDiariesObserverJob = viewModelScope.launch(Dispatchers.IO) {
            if (::allDiariesObserverJob.isInitialized) {
                allDiariesObserverJob.cancelAndJoin()
            }
            diaryManager.getFilteredDiaries(specificDateToShowDiaries)
                .collect { result ->
                    withContext(Dispatchers.Main) {
                        result.fold(
                            {
                                _homeUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        diaries = emptyMap()
                                    )
                                }
                            },
                            { list ->
                                _homeUiState.update {
                                    it.copy(
                                        isLoading = false,
                                        diaries = list
                                            .map { diary ->
                                                diary.toDiaryCard()
                                            }
                                            .groupBy { diaryCard ->
                                                diaryCard.date.toLocalDate()
                                            }
                                    )
                                }
                            }
                        )
                    }
                }
        }
    }

    fun deleteAllDiaries(
        onSuccess: () -> Unit,
        onError: (message: String) -> Unit
    ) {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = diaryManager.deleteAllDiaries()
                withContext(Dispatchers.Main) {
                    result.fold(
                        ifEmpty = {
                            _homeUiState.update {
                                it.copy(
                                    diaries = emptyMap()
                                )
                            }
                            onSuccess()
                        },
                        ifSome = {
                            onError(it.message)
                        }
                    )
                }
            }
        }
    }

    fun fetchImageUrlListOfDiary(diaryId: String, imageRemotePathList: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                _galleryState.update {
                    it.copy(
                        diaryId = diaryId,
                        isLoading = true,
                        isOpen = true,
                    )
                }
            }

            diaryManager.fetchRemoteImageUriList(imageRemotePathList)
                .fold(
                    ifLeft = {
                        withContext(Dispatchers.Main) {
                            _galleryState.update {
                                it.copy(
                                    isLoading = false,
                                    isOpen = false,
                                )
                            }
                        }
                    },
                    ifRight = { urlList ->
                        withContext(Dispatchers.Main) {
                            _galleryState.update {
                                it.copy(
                                    isLoading = false,
                                    urlList = urlList
                                )
                            }
                        }
                    }
                )
        }
    }

    fun closeGallery() {
        _galleryState.update {
            it.copy(
                diaryId = "",
                isLoading = false,
                isOpen = false,
                urlList = emptyList()
            )
        }
    }

    data class HomeUiState(
        val diaries: Map<LocalDate, List<DiaryCard>> = emptyMap(),
        val isLoading: Boolean = false,
        val specificDateSelected: ZonedDateTime? = null,
    )

    data class DiaryCard(
        val id: String = "",
        val mood: Mood = Mood.Neutral,
        val description: String = "",
        val date: ZonedDateTime = ZonedDateTime.now(),
        val imagesUrl: List<String> = emptyList(),
        val cardGalleryState: CardGalleryState = CardGalleryState()
    )

    data class CardGalleryState(
        val isLoading: Boolean = false,
        val isOpen: Boolean = false,
        val hasError: Boolean = false,
        val imagesUriList: List<Uri> = emptyList()
    )
}