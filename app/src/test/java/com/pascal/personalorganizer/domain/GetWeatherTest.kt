package com.pascal.personalorganizer.domain

import com.pascal.personalorganizer.data.local.entities.DailyForecast
import com.pascal.personalorganizer.data.local.entities.TodayForecast
import com.pascal.personalorganizer.domain.models.Weather
import com.pascal.personalorganizer.domain.repository.WeatherRepository
import com.pascal.personalorganizer.domain.usecases.GetWeather
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class GetWeatherTest {

    @RelaxedMockK
    private lateinit var repository: WeatherRepository

    lateinit var getWeather: GetWeather

    @Before
    fun onBefore(){
        MockKAnnotations.init(this)
        getWeather = GetWeather(repository)
    }


    @Test
    fun whenApiReturnBlankObjectReturnDataFromDatabase() = runBlocking {
        //Given
        coEvery { repository.getWeatherFromApi("London") } returns Weather() // this is empty
        //When
        getWeather("London")
        //Then
        coVerify (exactly = 1) {repository.getWeatherFromLocalDB()}
    }

    @Test
    fun whenApiReturnAOKObjectUpdateTheDatabaseWithIt() = runBlocking {
        //Given
        val weather  =  Weather(name  = "MockData", region  = "MockData", currentTempF  = "MockData", currentCondition  = "MockData", currentConditionIconUrl  = "MockData", todayForecast = listOf(
            TodayForecast("MockData", "MockData", 1L)
        ), dailyForecast = listOf(
            DailyForecast("MockData", "MockData", "MockData", "MockData")
        ))
        coEvery { repository.getWeatherFromApi("London") }returns weather
        //When
        val response = getWeather("London")
        //Then
        coVerify (exactly = 1) { repository.deleteWeather() }
        coVerify (exactly = 1) { repository.insertWeather(any()) }
        coVerify (exactly = 0) {repository.getWeatherFromLocalDB()}
        assert(weather == response)
    }

}