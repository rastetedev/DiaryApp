package com.androiddevhispano.diaryapp.di

import androidx.room.Room
import com.androiddevhispano.diaryapp.data.datasource.local.DiaryDatabase
import com.androiddevhispano.diaryapp.data.repository.diary_manager.DiaryManager
import com.androiddevhispano.diaryapp.data.repository.diary_manager.DiaryManagerImpl
import com.androiddevhispano.diaryapp.data.repository.diary.DiaryRepository
import com.androiddevhispano.diaryapp.data.repository.diary.DiaryRepositoryImpl
import com.androiddevhispano.diaryapp.data.repository.image.ImageRepository
import com.androiddevhispano.diaryapp.data.repository.image.ImageRepositoryImpl
import com.androiddevhispano.diaryapp.ui.connectivity.ConnectivityObserver
import com.androiddevhispano.diaryapp.ui.connectivity.NetworkConnectivityObserver
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val appModule = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            DiaryDatabase::class.java,
            name = "diary_database.db"
        ).build()
    }

    single {
        val database = get<DiaryDatabase>()
        database.imageToDeleteDao()
    }

    single {
        val database = get<DiaryDatabase>()
        database.imageToUploadDao()
    }

    single<ConnectivityObserver> {
        NetworkConnectivityObserver(androidApplication())
    }

    single<ImageRepository> {
        ImageRepositoryImpl(get(), get())
    }

    single<DiaryRepository> {
        DiaryRepositoryImpl()
    }

    single<DiaryManager> {
        DiaryManagerImpl(get(), get())
    }
}
