package com.androiddevhispano.diaryapp.di

import android.content.Context
import androidx.room.Room
import com.androiddevhispano.diaryapp.data.localdatabase.DiaryDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

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
}