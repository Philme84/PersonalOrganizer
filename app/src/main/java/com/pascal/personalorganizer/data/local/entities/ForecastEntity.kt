package com.pascal.personalorganizer.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.pascal.personalorganizer.data.local.typeconverters.DailyTypeConverter
import com.pascal.personalorganizer.data.local.typeconverters.TodayTypeConverter
import com.pascal.personalorganizer.domain.models.Weather
import com.pascal.personalorganizer.util.Constants.FORECAST_TABLE


@Entity(tableName = FORECAST_TABLE)
data class ForecastEntity (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "region") val region: String,
    @ColumnInfo(name = "currentTempF") val currentTempF: String,
    @ColumnInfo(name = "currentCondition") val currentCondition: String,
    @ColumnInfo(name = "currentConditionIconUrl") val currentConditionIconUrl: String,
    @TypeConverters(TodayTypeConverter::class) val todayForecast: List<TodayForecast>,
    @TypeConverters(DailyTypeConverter::class) val dayForecast: List<DailyForecast>,
)

data class DailyForecast (
    val condition: String,
    val iconUrl: String,
    val minTempF: String,
    val maxTempF: String
)

data class TodayForecast(
    val tempF: String,
    val iconUrl: String,
    val epoch: Long
)

/**
 * Wee need to map our Weather object to a Entity so it can be inserted to our room database
 */
fun Weather.toEntity() = ForecastEntity(
    name = name,
    region =  region,
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
    dayForecast = dailyForecast.map {
        DailyForecast(
            condition = it.condition,
            iconUrl = it.iconUrl,
            maxTempF = it.maxTempF,
            minTempF = it.minTempF
        )
    }
)

