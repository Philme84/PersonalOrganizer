package com.pascal.personalorganizer.data.remote

import com.pascal.personalorganizer.data.remote.response.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {

    /**
    We use this to call our api, here we past the endpoint and some requested parameters, (q) for the location
     and (days) for how many forecast days we want. The api free plan is limited to 3.
     */
    @GET("forecast.json")
    suspend fun getCurrentWeather(@Query("q") q: String, @Query("days") days: Int = 3): Result<ForecastResponse>


}