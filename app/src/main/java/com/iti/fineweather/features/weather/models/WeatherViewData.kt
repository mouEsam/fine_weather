package com.iti.fineweather.features.weather.models

import androidx.annotation.StringRes

data class WeatherViewData(
    val location: WeatherLocation,
    val units: WeatherUnitData,
    val temp: Temperature,
    val pressure: Int,
    val weatherState: WeatherItem?,
)

data class WeatherLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double,
)

data class WeatherUnitData(
    @StringRes val temperature: Int,
    @StringRes val pressure: Int,
    @StringRes val speed: Int,
    @StringRes val length: Int,
    @StringRes val accumulation: Int,
)
