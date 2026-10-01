package com.sonusstudios.sonusedit.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sonusstudios.sonusedit.create.CreateProjectDraft
import com.sonusstudios.sonusedit.designsystem.SonusEditTheme
import com.sonusstudios.sonusedit.integration.IncomingIntentRouter
import com.sonusstudios.sonusedit.navigation.AppRoute
import kotlinx.coroutines.flow.StateFlow

@Composable
fun SonusEditApp(incomingRoute: StateFlow<IncomingIntentRouter.Route?>) {
    SonusEditTheme {
        val navController = rememberNavController()
        val route by incomingRoute.collectAsState()
        var activeDraft by remember { mutableStateOf<CreateProjectDraft?>(null) }

        LaunchedEffect(route) {
            when (route) {
                is IncomingIntentRouter.Route.SonusTrack,
                is IncomingIntentRouter.Route.SharedMedia -> navController.navigate(AppRoute.Create.route) {
                    launchSingleTop = true
                }
                else -> Unit
            }
        }

        NavHost(navController = navController, startDestination = AppRoute.Home.route) {
            composable(AppRoute.Home.route) {
                HomeScreen(onCreate = { navController.navigate(AppRoute.Create.route) })
            }
            composable(AppRoute.Create.route) {
                CreateFlowScreen(
                    incomingRoute = route,
                    onBack = { navController.popBackStack() },
                    onCreateDraft = { draft ->
                        activeDraft = draft
                        navController.navigate(AppRoute.Editor.route)
                    },
                )
            }
            composable(AppRoute.Editor.route) {
                EditorScreen(
                    incomingRoute = route,
                    draft = activeDraft,
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}
