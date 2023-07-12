package com.androiddevhispano.diaryapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.androiddevhispano.diaryapp.navigation.Screen
import com.androiddevhispano.diaryapp.navigation.SetupNavGraph
import com.androiddevhispano.diaryapp.ui.theme.DiaryAppTheme
import io.realm.kotlin.mongodb.App

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            DiaryAppTheme {
                val navController = rememberNavController()

                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navHostController = navController
                )
            }
        }
    }
}

private fun getStartDestination(): String {
    val user = App.create(BuildConfig.MONGO_APP_ID).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}
