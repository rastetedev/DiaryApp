package com.androiddevhispano.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.androiddevhispano.diaryapp.navigation.Screen
import com.androiddevhispano.diaryapp.navigation.SetupNavGraph
import com.androiddevhispano.diaryapp.ui.theme.DiaryAppTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {

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
    }
}

private fun getStartDestination(): String {
    val user = App.create(BuildConfig.MONGO_APP_ID).currentUser
    val firebaseUser = Firebase.auth.currentUser
    return if (user != null && user.loggedIn && firebaseUser != null) Screen.Home.route
    else Screen.Authentication.route
}
