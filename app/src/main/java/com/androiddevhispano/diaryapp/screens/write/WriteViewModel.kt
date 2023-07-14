package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.data.MongoDB
import com.androiddevhispano.diaryapp.models.Diary
import com.androiddevhispano.diaryapp.models.Mood
import com.androiddevhispano.diaryapp.navigation.Screen.Companion.DIARY_ID_ARGUMENT
import com.androiddevhispano.diaryapp.utils.RequestState
import com.androiddevhispano.diaryapp.utils.toInstant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mongodb.kbson.ObjectId
import java.time.Instant

class WriteViewModel(
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    var uiState by mutableStateOf(WriteUiState())
        private set

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
                    MongoDB.getDiaryById(diaryId = ObjectId.invoke(uiState.diaryId!!))
                if (diaryRequest is RequestState.Success) {
                    with(diaryRequest.data) {
                        setTitle(title)
                        setDescription(description)
                        setMood(Mood.valueOf(mood))
                        setDate(date.toInstant())
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

    fun setDate(date: Instant) {
        uiState = uiState.copy(date = date)
    }

    fun saveDiary(
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val diarySavedResult = MongoDB.insertDiary(
                Diary().apply {
                    title = uiState.title
                    description = uiState.description
                    mood = uiState.mood.name
                }
            )
            withContext(Dispatchers.Main){
                if (diarySavedResult is RequestState.Success) onSuccess()
                else onError()
            }
        }
    }
}


data class WriteUiState(
    val diaryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
    val date: Instant = Instant.now()
)
