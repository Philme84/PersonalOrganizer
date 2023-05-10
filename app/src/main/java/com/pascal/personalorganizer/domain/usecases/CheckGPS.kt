package com.pascal.personalorganizer.domain.usecases


import com.pascal.personalorganizer.domain.repository.WeatherRepository
import javax.inject.Inject

class CheckGPS @Inject constructor(
    private val repository: WeatherRepository
) {

    fun check(): Boolean{
        return repository.isGPSEnabled()
    }

    fun getLocation(): String{
        return repository.getLocation()
    }

}