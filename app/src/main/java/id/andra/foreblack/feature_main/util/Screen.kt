package id.andra.foreblack.feature_main.util

sealed class Screen(val route: String) {
    data object HomeScreen : Screen("home_screen")
    data object BlackScreen : Screen("black_screen")
}