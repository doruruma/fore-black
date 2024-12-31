package id.andra.foreblack.feature_main.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import id.andra.foreblack.feature_main.presentation.ui.home.HomeScreen
import id.andra.foreblack.feature_main.util.Screen

@Composable
fun Navigation(
    navController: NavHostController
) {
    NavHost(
        modifier = Modifier.fillMaxWidth(),
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {
        composable(route = Screen.HomeScreen.route) {
            HomeScreen()
        }
    }
}