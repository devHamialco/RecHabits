package com.rechabits.app.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rechabits.app.presentation.ui.screen.habitedit.HabitEditScreen
import com.rechabits.app.presentation.ui.screen.home.HomeScreen
import com.rechabits.app.presentation.ui.screen.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object HabitEdit : Screen("habit_edit/{habitId}") {
        fun createRoute(habitId: Long? = null) = "habit_edit/${habitId ?: -1}"
    }
    object Settings : Screen("settings")
}

@Composable
fun RechaBitsNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onAddHabit = {
                    navController.navigate(Screen.HabitEdit.createRoute())
                },
                onEditHabit = { habitId ->
                    navController.navigate(Screen.HabitEdit.createRoute(habitId))
                },
                onSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.HabitEdit.route,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType })
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: -1L
            HabitEditScreen(
                habitId = if (habitId > 0) habitId else null,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
