package com.sonusstudios.sonusedit.navigation

sealed class AppRoute(val route: String) {
    data object Home : AppRoute("home")
    data object Create : AppRoute("create")
    data object Editor : AppRoute("editor")
}
