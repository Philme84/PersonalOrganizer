package com.pascal.personalorganizer.data.remote.response

data class ForecastResponse(
    val current: Current,
    val forecast: Forecast,
    val location: Location
)