package com.androiddevhispano.diaryapp.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.connectivity.ConnectivityObserver
import com.androiddevhispano.diaryapp.data.repository.DiaryRepositoryImpl
import com.androiddevhispano.diaryapp.data.repository.ImageRepository
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.utils.RequestState
import com.androiddevhispano.diaryapp.utils.deleteAllImagesForAllDiaries
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val connectivityObserver: ConnectivityObserver,
    private val imageRepository: ImageRepository
) : ViewModel() {

    var diaries: MutableState<RequestState<Map<LocalDate, List<Diary>>>> =
        mutableStateOf(RequestState.Idle)

    private var networkStatus by mutableStateOf(ConnectivityObserver.Status.UNAVAILABLE)

    init {
        observeAllDiaries()
        observeNetwork()
    }

    private fun observeAllDiaries() {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.Main) {
                diaries.value = RequestState.Loading
            }
            DiaryRepositoryImpl.getAllDiaries().collect { result ->
                diaries.value = result
            }
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connectivityObserver.observe().collect {
                networkStatus = it
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
}