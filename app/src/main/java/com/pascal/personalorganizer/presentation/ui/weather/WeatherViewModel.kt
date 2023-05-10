package com.pascal.personalorganizer.presentation.ui.weather

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pascal.personalorganizer.domain.models.Weather
import com.pascal.personalorganizer.domain.usecases.CheckGPS
import com.pascal.personalorganizer.domain.usecases.GetWeather
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val getWeather: GetWeather,
    private val checkGPS: CheckGPS
) : ViewModel() {

    var forecast by mutableStateOf(Weather())
        private set

    var isReady by mutableStateOf("loading")
        private set

    var dayList = mutableStateListOf("Today", "Tomorrow", getDayInTwoDays())
        private set

    val visiblePermissionDialogQueue = mutableStateListOf<String>()


    init {
        checkGPS()
    }

    private fun checkGPS(){
        val gps = checkGPS.check()
        if (!gps){
            isReady = "GPSOff"
        }
    }

    fun dismissDialog() {
        visiblePermissionDialogQueue.removeFirst()
    }

    fun onPermissionResult(
        permission: String,
        isGranted: Boolean
    ) {
        if(!isGranted && !visiblePermissionDialogQueue.contains(permission)) {
            visiblePermissionDialogQueue.add(permission)
        }
    }

    fun getForecast(q: String){
        isReady = "loading"
        viewModelScope.launch {
            val weather = getWeather("Peterborough")
            forecast = weather
            isReady = "ready"
        }
    }

    private fun getDayInTwoDays(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 2)
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault()) ?: ""
    }
}