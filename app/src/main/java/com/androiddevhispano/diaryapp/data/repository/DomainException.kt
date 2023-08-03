package com.androiddevhispano.diaryapp.data.repository

sealed class DomainException(val message: String) {
    class UserNotAuthenticatedException : DomainException("User is not logged in")
    class DiaryNotExistException : DomainException("Queried Diary does not exist.")
    class GeneralException(message: String?) : DomainException(message ?: "Error" )
}
