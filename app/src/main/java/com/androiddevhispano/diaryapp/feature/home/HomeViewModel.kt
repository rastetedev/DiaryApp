package com.androiddevhispano.diaryapp.feature.home

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.core_ui.models.Mood
import com.androiddevhispano.diaryapp.data.mapper.toDiaryCard
import com.androiddevhispano.diaryapp.data.repository.diary_manager.DiaryManager
import com.androiddevhispano.diaryapp.ui.connectivity.ConnectivityObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.ZonedDateTime

class HomeViewModel(
    private val connectivityObserver: ConnectivityObserver,
    private val diaryManager: DiaryManager
) : ViewModel() {

    private lateinit var allDiariesObserverJob: Job
    private lateinit var filteredDiariesObserverJob: Job

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

    private var _diaryCardListState = MutableStateFlow<List<DiaryCard>>(emptyList())

    val diaries = _diaryCardListState.map { list ->
        list.groupBy { it.date.toLocalDate() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), initialValue = emptyMap())

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
                                        isLoading = false
                                    )
                                }
                            },
                            { list ->
                                _diaryCardListState.value = list.map { it.toDiaryCard() }
                                _homeUiState.update {
                                    it.copy(
                                        isLoading = false
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
                                        isLoading = false
                                    )
                                }
                            },
                            { list ->
                                _diaryCardListState.value = list.map { it.toDiaryCard() }
                                _homeUiState.update {
                                    it.copy(
                                        isLoading = false
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
                            _diaryCardListState.value = emptyList()
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

    private fun fetchImageUrlListOfDiary(diaryId: String, imageRemotePathList: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {

            val diaryCard = findDiaryCard(diaryId)

            withContext(Dispatchers.Main) {

                val modifiedDiary = diaryCard?.copy(
                    isLoading = true,
                    isOpen = true
                )
                modifiedDiary?.let {
                    replaceDiaryCard(oldDiaryCard = diaryCard, newDiaryCard = it)
                }
            }

            diaryManager.fetchRemoteImageUriList(imageRemotePathList)
                .fold(
                    ifLeft = {
                        withContext(Dispatchers.Main) {
                            val modifiedDiary = diaryCard?.copy(
                                isLoading = false,
                                isOpen = false,
                                hasError = true
                            )
                            modifiedDiary?.let {
                                replaceDiaryCard(oldDiaryCard = diaryCard, newDiaryCard = it)
                            }
                        }
                    },
                    ifRight = { urlList ->
                        withContext(Dispatchers.Main) {
                            val modifiedDiary = diaryCard?.copy(
                                isLoading = false,
                                isOpen = true,
                                imagesUriList = urlList
                            )
                            modifiedDiary?.let {
                                replaceDiaryCard(oldDiaryCard = diaryCard, newDiaryCard = it)
                            }
                        }
                    }
                )
        }
    }

    fun toggleGalleryDiaryCard(diaryId: String) {
        val diaryCard = findDiaryCard(diaryId)

        diaryCard?.let {
            if (diaryCard.isOpen) {
                val modifiedDiary = diaryCard.copy(
                    isOpen = false,
                    isLoading = false
                )
                replaceDiaryCard(oldDiaryCard = diaryCard, newDiaryCard = modifiedDiary)
            } else {
                fetchImageUrlListOfDiary(diaryId, diaryCard.imagesUrl)
            }
        }
    }

    private fun findDiaryCard(diaryId: String): DiaryCard? {
        return _diaryCardListState.value.find {
            it.diaryId == diaryId
        }
    }

    private fun replaceDiaryCard(oldDiaryCard: DiaryCard, newDiaryCard: DiaryCard) {
        _diaryCardListState.value = _diaryCardListState.value.map { diaryCard ->
            if (diaryCard.diaryId == oldDiaryCard.diaryId) {
                newDiaryCard
            } else {
                diaryCard
            }
        }
    }

    data class HomeUiState(
        val isLoading: Boolean = true,
        val specificDateSelected: ZonedDateTime? = null,
    )

    data class DiaryCard(
        val diaryId: String = "",
        val mood: Mood = Mood.Neutral,
        val description: String = "",
        val date: ZonedDateTime = ZonedDateTime.now(),
        val imagesUrl: List<String> = emptyList(),
        val isLoading: Boolean = false,
        val isOpen: Boolean = false,
        val hasError: Boolean = false,
        val imagesUriList: List<Uri> = emptyList(),
    )
}