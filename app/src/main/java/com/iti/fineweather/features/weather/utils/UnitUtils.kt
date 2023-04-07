package com.iti.fineweather.features.weather.utils

import androidx.annotation.StringRes
import com.iti.fineweather.R
import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.weather.helpers.Constants
import com.iti.fineweather.features.weather.models.Temperature

@get:StringRes
val UserPreferences.TemperatureUnit.unitRes: Int
    get() {
        return when (this) {
            UserPreferences.TemperatureUnit.CELSIUS,
            UserPreferences.TemperatureUnit.UNRECOGNIZED -> R.string.temperature_celsius

            UserPreferences.TemperatureUnit.KELVIN -> R.string.temperature_kelvin
            UserPreferences.TemperatureUnit.FAHRENHEIT -> R.string.temperature_fahrenheit
        }
    }

@get:StringRes
val UserPreferences.WindSpeedUnit.unitRes: Int
    get() {
        return when (this) {
            UserPreferences.WindSpeedUnit.METER_SEC,
            UserPreferences.WindSpeedUnit.UNRECOGNIZED -> R.string.wind_speed_ms

            UserPreferences.WindSpeedUnit.MILES_HOUR -> R.string.wind_speed_miles_hr
        }
    }

fun UserPreferences.WindSpeedUnit.convert(speed: Float): Float {
    return when(this) {
        UserPreferences.WindSpeedUnit.METER_SEC,
        UserPreferences.WindSpeedUnit.UNRECOGNIZED-> speed
        UserPreferences.WindSpeedUnit.MILES_HOUR -> speed * Constants.MILES_HOUR_IN_M_SEC
    }
}

fun UserPreferences.TemperatureUnit.convert(temp: Temperature): Temperature {
    return when (temp) {
        is Temperature.Average -> Temperature.Average(convert(temp.temp))
        is Temperature.DaySummery -> Temperature.DaySummery(
            day = convert(temp.day),
            night = convert(temp.night),
            evening = convert(temp.evening),
            morning = convert(temp.morning),
            min = temp.min?.let(::convert),
            max = temp.max?.let(::convert),
        )
    }
}

private fun UserPreferences.TemperatureUnit.convert(temp: Float): Float {
    return when (this) {
        UserPreferences.TemperatureUnit.KELVIN -> temp
        UserPreferences.TemperatureUnit.CELSIUS,
        UserPreferences.TemperatureUnit.UNRECOGNIZED -> (temp - Constants.ZERO_KELVIN_CELSIUS)

        UserPreferences.TemperatureUnit.FAHRENHEIT -> (temp - Constants.ZERO_KELVIN_CELSIUS) * (9.0f / 5.0f + 32.0f)
    }
}
