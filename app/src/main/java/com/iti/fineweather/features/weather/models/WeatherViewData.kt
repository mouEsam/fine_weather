package com.iti.fineweather.features.weather.models

import androidx.annotation.RawRes
import androidx.annotation.StringRes
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

data class WeatherViewData(
    val location: WeatherLocation,
    val units: WeatherUnitData,
    val timezone: TimeZone,
    val now: WeatherData,
    val hourly: SortedMap<LocalTime, WeatherData>,
    val daily: SortedMap<LocalDate, WeatherData>,
)

data class WeatherData(
    val tempObj: Temperature,
    val temperature: Float,
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Float,
    val clouds: Int,
    val weatherState: WeatherState,
)

data class WeatherLocation(
    val name: String,
    val city: String,
    val latitude: Double,
    val longitude: Double,
)

enum class WeatherType(val code: Long? = null, val codeRange: LongRange? = null) {
    Thunderstorm(codeRange = 200L until 300),
    Drizzle(codeRange = 300L until 400),
    Rain(codeRange = 500L until 600),
    Snow(codeRange = 600L until 700),
    Atmosphere(codeRange = 700L until 800),
    Clear(code = 800L),
    Clouds(codeRange = 800L..810),
}

data class WeatherState(
    val weatherType: WeatherType,
    val isDay: Boolean,
    val main: String,
    val description: String,
    val iconUrl: String,
    val iconUrlX2: String,
    @RawRes val dayIcon: Int,
    @RawRes val nightIcon: Int,
) {
    @get:RawRes
    val icon: Int
        get() = if (isDay) dayIcon else nightIcon
}

data class WeatherUnitData(
    @StringRes val temperature: Int,
    @StringRes val pressure: Int,
    @StringRes val speed: Int
)

