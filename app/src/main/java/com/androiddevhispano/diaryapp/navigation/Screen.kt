package com.androiddevhispano.diaryapp.navigation

sealed class Screen(val route: String) {

    object Authentication : Screen(route = "authentication_screen")
    object Home : Screen(route = "home_screen")
    object Write : Screen(route = "write_screen?$DIARY_ID_ARGUMENT={$DIARY_ID_ARGUMENT}") {

        fun navigateToDiary(diaryId: String): String {
            return this.route.replace(
                oldValue = "{$DIARY_ID_ARGUMENT}",
                newValue = diaryId
            )
        }
    }

    companion object {
        const val DIARY_ID_ARGUMENT = "diaryId"
    }
}
