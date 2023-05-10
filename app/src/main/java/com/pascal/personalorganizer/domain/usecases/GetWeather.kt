package com.pascal.personalorganizer.domain.usecases


import com.pascal.personalorganizer.data.local.entities.toEntity
import com.pascal.personalorganizer.domain.models.Weather
import com.pascal.personalorganizer.domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeather @Inject constructor(
    private val repository: WeatherRepository
) {

    /**
     * We use this usecase to have to perform some logic with the data gathered.
     * in this case we always get the data from a api call and if for some reason
     * the api call fails we then get the data locally from the db. If the api call
     * contains valid data we wipe the weather data previously saved and put the new
     * updated weather data
     */
    suspend operator fun invoke(q: String): Weather{
        val forecast = repository.getWeatherFromApi(q)
        return if (forecast.name.isBlank()){
            repository.getWeatherFromLocalDB()
        } else {
            repository.deleteWeather()
            repository.insertWeather(forecast.toEntity())
            forecast
        }
    }

}