package com.pascal.personalorganizer.data.local.typeconverters

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.pascal.personalorganizer.data.local.entities.DailyForecast


class DailyTypeConverter {

    private val gson = GsonProvider.gson

    @TypeConverter
    fun fromJson(json: String): List<DailyForecast> {
        return gson.fromJson(json, object : TypeToken<List<DailyForecast>>() {}.type)
    }

    @TypeConverter
    fun toJson(dailyForecast: List<DailyForecast>): String {
        return gson.toJson(dailyForecast)
    }

}