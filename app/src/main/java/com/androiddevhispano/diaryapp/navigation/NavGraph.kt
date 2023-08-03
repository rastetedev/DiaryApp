package com.androiddevhispano.diaryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.androiddevhispano.diaryapp.feature.authentication.authenticationRoute
import com.androiddevhispano.diaryapp.feature.home.homeRoute
import com.androiddevhispano.diaryapp.feature.write.writeRoute

@Composable
fun SetupNavGraph(
    startDestination: String,
    navHostController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(navController = navHostController, startDestination = startDestination) {
        authenticationRoute(
            navigateToHome = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        homeRoute(
            navigateToWrite = { diaryId ->
                if (diaryId != null)
                    navHostController.navigate(Screen.Write.navigateToDiary(diaryId))
                else
                    navHostController.navigate(Screen.Write.route)
            },
            navigateToAuthentication = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded
        )
        writeRoute(
            onBackPressed = {
                navHostController.popBackStack()
            }
        )
    }
}
