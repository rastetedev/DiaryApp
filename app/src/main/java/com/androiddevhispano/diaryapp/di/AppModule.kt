package com.androiddevhispano.diaryapp.di

import android.content.Context
import androidx.room.Room
import com.androiddevhispano.diaryapp.utils.connectivity.ConnectivityObserver
import com.androiddevhispano.diaryapp.utils.connectivity.NetworkConnectivityObserver
import com.androiddevhispano.diaryapp.data.localdatabase.DiaryDatabase
import com.androiddevhispano.diaryapp.data.localdatabase.dao.ImageToDeleteDao
import com.androiddevhispano.diaryapp.data.localdatabase.dao.ImageToUploadDao
import com.androiddevhispano.diaryapp.data.repository.image.ImageRepository
import com.androiddevhispano.diaryapp.data.repository.image.ImageRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): DiaryDatabase {
        return Room.databaseBuilder(
            context,
            DiaryDatabase::class.java,
            name = "diary_database.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }

    @Provides
    @Singleton
    fun provideImageToUploadDao(database: DiaryDatabase) = database.imageToUploadDao()

    @Provides
    @Singleton
    fun provideImageToDeleteDao(database: DiaryDatabase) = database.imageToDeleteDao()

    @Provides
    @Singleton
    fun provideImageRepository(
        imageToUploadDao: ImageToUploadDao,
        imageToDeleteDao: ImageToDeleteDao
    ): ImageRepository {
        return ImageRepositoryImpl(imageToUploadDao, imageToDeleteDao)
    }
}