package com.pascal.personalorganizer.data.repositoryImpl

import android.content.Context
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import com.pascal.personalorganizer.data.local.daos.ForecastDao
import com.pascal.personalorganizer.data.local.entities.ForecastEntity
import com.pascal.personalorganizer.data.remote.WeatherApiService
import com.pascal.personalorganizer.domain.models.Weather
import com.pascal.personalorganizer.domain.models.toDomain
import com.pascal.personalorganizer.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: WeatherApiService,
    private val dao: ForecastDao
): WeatherRepository {

    override suspend fun getWeatherFromApi(q: String): Weather {
        val response = apiService.getCurrentWeather(q)
        response.onSuccess {
            return it.toDomain() //we map our response to our domain object
        }
        response.onFailure {
            return Weather() //empty object
        }
        return Weather() //empty object
    }

    override suspend fun getWeatherFromLocalDB(): Weather {
        val response = dao.getForecast()
        return response.toDomain() //We are getting a entity from our db, so wee need to map it to a object.
    }

    override suspend fun insertWeather(weather: ForecastEntity) {
        dao.insertForecast(weather)
    }

    override fun getLocation(): String {
        // To get user latitude and longitude to perform api call
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        var location = ""
        return try {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                location = "${it.latitude},${it.longitude}"
            }
            location
        } catch (e: SecurityException){
            ""
        }
    }

    override suspend fun deleteWeather() {
        dao.deleteForecast()
    }

    override fun isGPSEnabled(): Boolean {
        // To know if GPS is enabled
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}