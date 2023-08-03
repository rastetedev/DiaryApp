package com.androiddevhispano.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.androiddevhispano.diaryapp.data.repository.auth.AuthRepository
import com.androiddevhispano.diaryapp.data.repository.image.ImageRepository
import com.androiddevhispano.diaryapp.navigation.Screen
import com.androiddevhispano.diaryapp.navigation.SetupNavGraph
import com.androiddevhispano.diaryapp.ui.theme.DiaryAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val imageRepository: ImageRepository by inject()

    private val authRepository: AuthRepository by inject()

    private var keepSplashOpened = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiaryAppTheme {
                val navController = rememberNavController()

                SetupNavGraph(
                    startDestination = getStartDestination(authRepository),
                    navHostController = navController,
                    onDataLoaded = {
                        keepSplashOpened = false
                    }
                )
            }
        }
        retryUploadPendentImages(
            scope = lifecycleScope,
            imageRepository = imageRepository
        )
        retryDeletePendentImages(
            scope = lifecycleScope,
            imageRepository = imageRepository
        )
    }
}

private fun retryUploadPendentImages(
    scope: CoroutineScope,
    imageRepository: ImageRepository
) {
    scope.launch(Dispatchers.IO) {
        imageRepository.retryImagesToUpload()
    }
}

private fun retryDeletePendentImages(
    scope: CoroutineScope,
    imageRepository: ImageRepository
) {
    scope.launch(Dispatchers.IO) {
        imageRepository.retryImagesToDelete()
    }
}

private fun getStartDestination(authRepository: AuthRepository): String {
    return if (authRepository.isUserSignIn()) Screen.Home.route
    else Screen.Authentication.route
}