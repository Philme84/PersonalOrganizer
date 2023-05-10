package com.pascal.personalorganizer.domain.repository

import com.pascal.personalorganizer.data.local.entities.ForecastEntity
import com.pascal.personalorganizer.domain.models.Weather


interface WeatherRepository {

    suspend fun getWeatherFromApi(q: String) : Weather
    suspend fun getWeatherFromLocalDB(): Weather
    suspend fun insertWeather(weather: ForecastEntity)
    suspend fun deleteWeather()
    fun isGPSEnabled() : Boolean
    fun getLocation() : String
}


