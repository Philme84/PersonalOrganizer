package com.pascal.personalorganizer.domain.models

import com.pascal.personalorganizer.data.local.entities.DailyForecast
import com.pascal.personalorganizer.data.local.entities.ForecastEntity
import com.pascal.personalorganizer.data.local.entities.TodayForecast
import com.pascal.personalorganizer.data.remote.response.ForecastResponse

data class Weather (
    val name: String = "",
    val region: String = "",
    val currentTempF: String = "",
    val currentCondition: String = "",
    val currentConditionIconUrl: String = "",
    val todayForecast: List<TodayForecast> = emptyList(),
    val dailyForecast: List<DailyForecast> = emptyList(),
)

/**
 * We are getting a big object from the api with a lot of information.
 * With this we are mapping the ForecastResponse from the api
 * to a Weather object that will only hold the information
 * that we will need or use
 */
fun ForecastResponse.toDomain() = Weather(
    name = location.name,
    region = location.region,
    currentTempF = current.temp_f.toString(),
    currentCondition = current.condition.text,
    currentConditionIconUrl = "https:${current.condition.icon}".replace("64x64","128x128"), //constructing url, changing 64 to 128 for better img quality
    todayForecast = forecast.forecastday[0].hour.map {
        TodayForecast(
            tempF = it.temp_f.toString(),
            iconUrl = "https:${it.condition.icon}",
            epoch = it.time_epoch.toLong()
        )
    },
    dailyForecast = forecast.forecastday.map {
        DailyForecast(
            condition = it.day.condition.text,
            iconUrl = "https:${it.day.condition.icon}",
            maxTempF = it.day.maxtemp_f.toString(),
            minTempF = it.day.mintemp_f.toString()
        )
    }
)

/**
 * Every time we retrieve data from room database it will return a entity,
 * so we need to map the entity to our Weather object that is the one being
 * use by our presentation layer
 */
fun ForecastEntity.toDomain() = Weather(
    name = name,
    region = region,
    currentTempF = currentTempF,
    currentCondition = currentCondition,
    currentConditionIconUrl = currentConditionIconUrl,
    todayForecast = todayForecast.map {
        TodayForecast(
            tempF = it.tempF,
            iconUrl = it.iconUrl,
            epoch = it.epoch
        )
    },
    dailyForecast = dayForecast.map {
        DailyForecast(
            condition = it.condition,
            iconUrl = it.iconUrl,
            maxTempF = it.maxTempF,
            minTempF = it.minTempF
        )
    }
)