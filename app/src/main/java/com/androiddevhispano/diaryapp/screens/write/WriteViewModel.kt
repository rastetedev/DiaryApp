package com.androiddevhispano.diaryapp.screens.write

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.androiddevhispano.diaryapp.data.MongoDB
import com.androiddevhispano.diaryapp.models.Mood
import com.androiddevhispano.diaryapp.navigation.Screen.Companion.DIARY_ID_ARGUMENT
import com.androiddevhispano.diaryapp.utils.RequestState
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId

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
}


data class WriteUiState(
    val diaryId: String? = null,
    val title: String = "",
    val description: String = "",
    val mood: Mood = Mood.Neutral,
)
