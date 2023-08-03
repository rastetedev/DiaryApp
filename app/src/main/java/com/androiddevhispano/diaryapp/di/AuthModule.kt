package com.androiddevhispano.diaryapp.di

import com.androiddevhispano.diaryapp.data.repository.auth.AuthRepository
import com.androiddevhispano.diaryapp.data.repository.auth.AuthRepositoryImpl
import com.androiddevhispano.diaryapp.data.datasource.remote.auth.AuthRemoteDataSource
import com.androiddevhispano.diaryapp.data.datasource.remote.auth.FirebaseAuthRemoteDataSourceImpl
import com.androiddevhispano.diaryapp.data.datasource.remote.auth.MongoRemoteDataSourceImpl
import com.androiddevhispano.diaryapp.feature.authentication.AuthenticationViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {

    single<AuthRemoteDataSource>(named("firebaseAuthRemoteSource")) {
        FirebaseAuthRemoteDataSourceImpl()
    }

    single<AuthRemoteDataSource>(named("mongoRemoteSource")) {
        MongoRemoteDataSourceImpl()
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            get(named("firebaseAuthRemoteSource")),
            get(named("mongoRemoteSource"))
        )
    }

    viewModel {
        AuthenticationViewModel(get())
    }

}
