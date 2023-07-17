package com.androiddevhispano.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.androiddevhispano.diaryapp.data.repository.ImageRepository
import com.androiddevhispano.diaryapp.navigation.Screen
import com.androiddevhispano.diaryapp.navigation.SetupNavGraph
import com.androiddevhispano.diaryapp.ui.theme.DiaryAppTheme
import com.androiddevhispano.diaryapp.utils.retryUploadImageToFirebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var imageRepository: ImageRepository

    private var keepSplashOpened = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        installSplashScreen().setKeepOnScreenCondition {
            keepSplashOpened
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            DiaryAppTheme {
                val navController = rememberNavController()

                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navHostController = navController,
                    onDataLoaded = {
                        keepSplashOpened = false
                    }
                )
            }
        }
        cleanupImages(
            scope = lifecycleScope,
            imageRepository = imageRepository
        )
    }
}

private fun cleanupImages(
    scope: CoroutineScope,
    imageRepository: ImageRepository
) {
    scope.launch(Dispatchers.IO) {
        imageRepository.getImagesToUpload().forEach {
            retryUploadImageToFirebase(
                image = it,
                onSuccess = {
                    scope.launch(Dispatchers.IO) {
                        imageRepository.cleanupImageToUpload(it.id)
                    }
                }
            )
        }
    }
}

private fun getStartDestination(): String {
    val user = App.create(BuildConfig.MONGO_APP_ID).currentUser
    val firebaseUser = Firebase.auth.currentUser
    return if (user != null && user.loggedIn && firebaseUser != null) Screen.Home.route
    else Screen.Authentication.route
}