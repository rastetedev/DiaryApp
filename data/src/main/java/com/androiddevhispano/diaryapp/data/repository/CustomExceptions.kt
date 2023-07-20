package com.androiddevhispano.diaryapp.data.repository

class UserNotAuthenticatedException : Exception("User is not logged in")
class DiaryNotExistException : Exception("Queried Diary does not exist.")