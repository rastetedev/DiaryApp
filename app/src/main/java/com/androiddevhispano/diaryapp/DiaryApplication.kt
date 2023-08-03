package com.androiddevhispano.diaryapp

import android.app.Application
import com.androiddevhispano.diaryapp.di.authModule
import com.androiddevhispano.diaryapp.di.appModule
import com.androiddevhispano.diaryapp.di.homeModule
import com.androiddevhispano.diaryapp.di.writeModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class DiaryApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        startKoin {
            // Log Koin into Android logger
            androidLogger()
            // Reference Android context
            androidContext(this@DiaryApplication)
            // Load modules
            modules(appModule, authModule, homeModule, writeModule)
        }
    }
}