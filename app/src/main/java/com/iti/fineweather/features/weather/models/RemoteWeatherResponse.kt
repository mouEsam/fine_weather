package com.iti.fineweather.features.weather.models

import com.google.gson.*
import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import com.iti.fineweather.features.weather.helpers.RainDeserializer

data class RemoteWeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    val timezoneOffset: Int,
    @SerializedName("current")
    val currentWeather: RemoteWeatherData,
    @SerializedName("hourly")
    val hourlyWeather: List<RemoteWeatherData>,
    @SerializedName("daily")
    val dailyWeather: List<RemoteWeatherData>,
    val alerts: List<WeatherAlert> = listOf(),
)

data class RemoteWeatherData(
    // TODO: serialize into [Timestamp]
    @SerializedName("dt")
    val timestamp: Long,
    // Current and daily only
    val sunrise: Long?,
    // Current and daily only
    val sunset: Long?,
    // Daily only
    val moonrise: Long?,
    // Daily only
    val moonset: Long?,
    val moonPhase: Float?,
    val temp: Temperature,
    val feelsLike: Temperature,
    val pressure: Int, // hPa
    val humidity: Int, // %
    val dewPoint: Float, // Kelvin
    @SerializedName("uvi")
    val uvIndex: Float,
    val clouds: Int, // %
    val visibility: Int, // Meters
    val windSpeed: Float, // Metre/Sec
    val windDeg: Float, // degrees
    val windGust: Float?, // metre/sec
    val pop: Float?,
    val snow: Float?, // mm/h
    @JsonAdapter(value = RainDeserializer::class)
    val rain: Float?, // mm/h
    val weather: List<WeatherItem>
)

data class WeatherItem(
    // Thunderstorm 2xx
    // Drizzle 3xx
    // Rain 5xx
    // Snow 6xx
    // Atmosphere 7xx
    // Clear 800
    // Clouds 80x
    val id: Long,
    // TODO: localize or make into an enum
    val main: String,
    val description: String,
    // Note: url -> https://openweathermap.org/img/wn/${icon}@2x.png
    val icon: String
)

sealed class Temperature {
    data class Average(val temp: Float): Temperature() {
        override fun getTemp(isDay: Boolean): Float = temp
    }

    data class DaySummery(
        val day: Float,
        val night: Float,
        @SerializedName("eve")
        val evening: Float,
        @SerializedName("morn")
        val morning : Float,
        val min: Float?,
        val max: Float?,
    ): Temperature() {
        override fun getTemp(isDay: Boolean): Float = if (isDay) day else night
    }

    abstract fun getTemp(isDay: Boolean): Float
}

data class WeatherAlert(
    val senderName: String,
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String> = listOf(),
)
