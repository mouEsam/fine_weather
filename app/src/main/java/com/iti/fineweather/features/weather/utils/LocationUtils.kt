package com.iti.fineweather.features.weather.utils

import com.iti.fineweather.features.settings.models.UserPreferences
import com.iti.fineweather.features.weather.models.WeatherLocation

fun UserPreferences.MapPlace.toWeatherLocation(): WeatherLocation {
    return WeatherLocation(name, location.latitude, location.longitude)
}
