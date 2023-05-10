package com.pascal.personalorganizer.data.local.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pascal.personalorganizer.data.local.entities.ForecastEntity
import com.pascal.personalorganizer.util.Constants.FORECAST_TABLE

@Dao
interface ForecastDao {

    //This is self explanatory
    @Query("SELECT * FROM $FORECAST_TABLE")
    suspend fun getForecast(): ForecastEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertForecast(forecast: ForecastEntity)

    @Query("DELETE FROM $FORECAST_TABLE")
    suspend fun deleteForecast()
}