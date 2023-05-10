package com.pascal.personalorganizer.data.local.typeconverters

import androidx.room.TypeConverter
import com.google.gson.reflect.TypeToken
import com.pascal.personalorganizer.data.local.entities.TodayForecast

class TodayTypeConverter {

    private val gson = GsonProvider.gson

    @TypeConverter
    fun fromJson(json: String): List<TodayForecast> {
        return gson.fromJson(json, object : TypeToken<List<TodayForecast>>() {}.type)
    }

    @TypeConverter
    fun toJson(todayForecast: List<TodayForecast>): String {
        return gson.toJson(todayForecast)
    }

}