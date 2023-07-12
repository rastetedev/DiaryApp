package com.androiddevhispano.diaryapp.screens.home

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.androiddevhispano.diaryapp.navigation.Screen

fun NavGraphBuilder.homeRoute(
    navigateToWrite: () -> Unit
) {
    composable(route = Screen.Home.route) {
        HomeScreen(
            onMenuClicked = {
                
            },
            navigateToWrite = navigateToWrite
        )

    }
}