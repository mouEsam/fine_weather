package com.iti.fineweather.features.weather.models

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.util.*

data class WeatherViewData(
    val location: WeatherLocation,
    val units: WeatherUnitData,
    val timezone: TimeZone,
    val now: WeatherData,
    val hourly: SortedMap<LocalTime, WeatherData>,
    val daily: SortedMap<LocalDate, WeatherData>,
    val alerts: List<WeatherAlertView>,
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
    Clouds(codeRange = 800L..810),;

    fun color(isDay: Boolean): Color {
        return when (this) {
            Clear -> if (isDay) Color(0xff99c738) else Color(0xff357eca)
            Thunderstorm -> if (isDay) Color(0xff78936c) else Color(0xff827b84)
            Drizzle -> if (isDay) Color(0xff93a659) else Color(0xff87629d)
            Rain -> if (isDay) Color(0xff679198) else Color(0xff906f72)
            Snow -> if (isDay) Color(0xff4ea6b1) else Color(0xff4a6cb5)
            Atmosphere -> if (isDay) Color(0xffa55a5d) else Color(0xffa24eb1)
            Clouds -> if (isDay) Color(0xff9c8a63) else Color(0xff574ab5)
        }
    }
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
    @DrawableRes val dayBG: Int,
    @DrawableRes val nightBG: Int,
) {
    @get:RawRes
    val icon: Int
        get() = if (isDay) dayIcon else nightIcon

    @get:DrawableRes
    val bg: Int
        get() = if (isDay) dayBG else nightBG

    val color: Color
        get() = weatherType.color(isDay)
}

data class WeatherUnitData(
    @StringRes val temperature: Int,
    @StringRes val pressure: Int,
    @StringRes val speed: Int
)

@Parcelize
data class WeatherAlertView(
    val senderName: String,
    val event: String,
    val start: ZonedDateTime,
    val end: ZonedDateTime,
    val description: String,
    val tags: List<String> = listOf(),
) : Parcelable

