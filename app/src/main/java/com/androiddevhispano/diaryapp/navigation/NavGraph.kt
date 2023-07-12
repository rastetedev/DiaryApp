package com.androiddevhispano.diaryapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.androiddevhispano.diaryapp.navigation.Screen.Companion.DIARY_ID_ARGUMENT
import com.androiddevhispano.diaryapp.screens.authentication.authenticationRoute
import com.androiddevhispano.diaryapp.screens.home.homeRoute

@Composable
fun SetupNavGraph(startDestination: String, navHostController: NavHostController) {
    NavHost(navController = navHostController, startDestination = startDestination) {
        authenticationRoute(
            navigateToHome = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Home.route)
            }
        )
        homeRoute(
            navigateToWrite = {
                navHostController.navigate(Screen.Write.route)
            },
            navigateToAuthentication = {
                navHostController.popBackStack()
                navHostController.navigate(Screen.Authentication.route)
            }
        )
        writeRoute()
    }
}

fun NavGraphBuilder.writeRoute() {
    composable(
        route = Screen.Write.route,
        arguments = listOf(
            navArgument(name = DIARY_ID_ARGUMENT) {
                type = NavType.StringType
                nullable = true
            }
        )
    ) {
    }
}