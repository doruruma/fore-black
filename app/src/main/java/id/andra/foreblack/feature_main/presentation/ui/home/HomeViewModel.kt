package id.andra.foreblack.feature_main.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000L),
        HomeState()
    )

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnLoad -> handleOnLoad()
            HomeEvent.OnClickRequestPermission -> handleClickRequestPermission()
        }
    }

    private fun handleOnLoad() {}

    private fun handleClickRequestPermission() {
        _state.update { state ->
            state.copy(
                isRequestingPermission = true
            )
        }
    }

}