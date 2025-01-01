package id.andra.foreblack.feature_main.presentation.ui.home

data class HomeState(
    val isRequestingPermission: Boolean = false
)

sealed class HomeEvent {
    data object OnLoad : HomeEvent()
    data object OnClickRequestPermission : HomeEvent()
}