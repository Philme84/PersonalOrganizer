package com.pascal.personalorganizer.presentation.ui

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.data.remote.WeatherApiService
import com.pascal.personalorganizer.presentation.navigation.Screens
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(

) : ViewModel() {

    var currentScreen = mutableStateOf<Screens>(Screens.NotesScreen)
        private set

    var fabPress by mutableStateOf(false)
        private set

    var uris by mutableStateOf(Uri.EMPTY)
        private set

    var monthOffset by mutableStateOf(0)
        private set

    var monthIndex by mutableStateOf(-1)
        private set

    fun setCurrentScreen(screen: Screens) {
        currentScreen.value = screen
    }

    fun setUri(uri: Uri){
       uris = uri
    }
    fun addMonthOffset(offset: Int){
        monthOffset += offset
    }

    fun changeMonthIndex(index: Int){
        monthIndex = index
    }

    fun setFabStatus(status: Boolean){
        fabPress = status
    }



}