package com.androiddevhispano.diaryapp.di

import com.androiddevhispano.diaryapp.feature.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {

    viewModel {
        HomeViewModel(
            connectivityObserver = get(),
            diaryManager = get()
        )
    }
}