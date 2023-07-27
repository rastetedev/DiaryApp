package com.androiddevhispano.diaryapp.screens.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.ui.connectivity.ConnectivityObserver
import com.androiddevhispano.diaryapp.data.repository.DiaryRepositoryImpl
import com.androiddevhispano.diaryapp.data.repository.ImageRepository
import com.androiddevhispano.diaryapp.utils.RequestState
import com.androiddevhispano.diaryapp.data.utils.deleteAllImagesForAllDiaries
import com.androiddevhispano.diaryapp.mapper.toDiaryCard
import com.androiddevhispano.diaryapp.models.Mood
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val connectivityObserver: ConnectivityObserver,
        private val imageRepository: ImageRepository
) : ViewModel() {

    private lateinit var allDiariesObserverJob: Job
    private lateinit var filteredDiariesObserverJob: Job

    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState = _homeUiState.asStateFlow()

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
            DiaryRepositoryImpl.getAllDiaries()
                    .collect { result ->
                        withContext(Dispatchers.Main) {
                            if (result is RequestState.Success) {
                                _homeUiState.update {
                                    it.copy(
                                            diaries = result.data
                                                    .map { diary ->
                                                        diary.toDiaryCard()
                                                    }
                                                    .groupBy { diaryCard ->
                                                        diaryCard.date.toLocalDate()
                                                    }
                                    )
                                }
                            }
                        }
                    }
        }
    }

    private fun observeFilteredDiaries(specificDateToShowDiaries: ZonedDateTime) {
        filteredDiariesObserverJob = viewModelScope.launch(Dispatchers.IO) {
            if (::allDiariesObserverJob.isInitialized) {
                allDiariesObserverJob.cancelAndJoin()
            }
            DiaryRepositoryImpl.getFilteredDiariesByDate(specificDateToShowDiaries)
                    .collect { result ->
                        withContext(Dispatchers.Main) {
                            if (result is RequestState.Success) {
                                _homeUiState.update {
                                    it.copy(
                                            diaries = result.data
                                                    .map { diary ->
                                                        diary.toDiaryCard()
                                                    }
                                                    .groupBy { diaryCard ->
                                                        diaryCard.date.toLocalDate()
                                                    }
                                    )
                                }
                            }
                        }
                    }
        }
    }


    fun deleteAllDiaries(
            onSuccess: () -> Unit,
            onError: () -> Unit
    ) {
        if (networkStatus == ConnectivityObserver.Status.AVAILABLE) {
            deleteAllImagesForAllDiaries(
                    onDeleteImageFail = { imageRemotePath ->
                        viewModelScope.launch(Dispatchers.IO) {
                            imageRepository.addImageToDelete(imageRemotePath)
                        }
                    },
                    onError = onError
            )
            viewModelScope.launch(Dispatchers.IO) {
                val diariesDeletedResult = DiaryRepositoryImpl.deleteAllDiaries()
                withContext(Dispatchers.Main) {
                    if (diariesDeletedResult is RequestState.Success) onSuccess()
                    else onError()
                }
            }
        } else {
            onError()
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
            val imagesUrl: List<String> = emptyList()
    )
}