package com.androiddevhispano.diaryapp.di

import com.androiddevhispano.diaryapp.feature.write.WriteViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val writeModule = module {

    viewModel {
        WriteViewModel(
            savedStateHandle = get(),
            diaryManager = get()
        )
    }
}