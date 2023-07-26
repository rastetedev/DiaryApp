package com.androiddevhispano.diaryapp.di

import com.androiddevhispano.diaryapp.data.repository.auth.AuthRepository
import com.androiddevhispano.diaryapp.data.repository.auth.AuthRepositoryImpl
import com.androiddevhispano.diaryapp.datasource.remote.AuthRemoteDataSource
import com.androiddevhispano.diaryapp.datasource.remote.FirebaseAuthRemoteDataSourceImpl
import com.androiddevhispano.diaryapp.datasource.remote.MongoRemoteDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @JvmSuppressWildcards
    @Provides
    @Singleton
    fun provideAuthRepository(
        @FirebaseAuthRemoteSource authFirebaseSource: AuthRemoteDataSource,
        @MongoRemoteSource mongoSource: AuthRemoteDataSource
    ): AuthRepository = AuthRepositoryImpl(
        authFirebaseRemoteDataSource = authFirebaseSource,
        mongoRemoteDataSource = mongoSource
    )

    @Provides
    @Singleton
    @FirebaseAuthRemoteSource
    fun provideFirebaseAuthRemoteSource(): AuthRemoteDataSource {
        return FirebaseAuthRemoteDataSourceImpl()
    }

    @Provides
    @Singleton
    @MongoRemoteSource
    fun provideMongoRemoteSource(): AuthRemoteDataSource {
        return MongoRemoteDataSourceImpl()
    }
}

@Qualifier
annotation class FirebaseAuthRemoteSource

@Qualifier
annotation class MongoRemoteSource