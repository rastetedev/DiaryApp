package com.androiddevhispano.diaryapp.screens.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.data.repository.DiaryRepositoryImpl
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.utils.RequestState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class HomeViewModel : ViewModel() {

    var diaries: MutableState<RequestState<Map<LocalDate, List<Diary>>>> =
        mutableStateOf(RequestState.Idle)

    init {
        observeAllDiaries()
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
}