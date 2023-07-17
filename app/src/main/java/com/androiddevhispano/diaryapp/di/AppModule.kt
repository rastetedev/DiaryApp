package com.androiddevhispano.diaryapp.di

import android.content.Context
import androidx.room.Room
import com.androiddevhispano.diaryapp.data.localdatabase.DiaryDatabase
import com.androiddevhispano.diaryapp.data.localdatabase.ImageToUploadDao
import com.androiddevhispano.diaryapp.data.repository.ImageRepository
import com.androiddevhispano.diaryapp.data.repository.ImageRepositoryImpl
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
    fun provideImageDao(database: DiaryDatabase) = database.imageDao()

    @Provides
    @Singleton
    fun provideImageRepository(imageToUploadDao: ImageToUploadDao) : ImageRepository {
        return ImageRepositoryImpl(imageToUploadDao)
    }
}