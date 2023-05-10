package com.pascal.personalorganizer.util

import androidx.compose.material.SnackbarDuration
import com.pascal.personalorganizer.presentation.navigation.Screens

sealed class UiEvent {
    object PopBackStack: UiEvent()
    data class Navigate(val route: String): UiEvent()
    data class NavigateAndPop(val screen: Screens, val route: String, val pop: String): UiEvent()
    data class ShowSnackbar(val msg: String, val duration: SnackbarDuration): UiEvent()
}