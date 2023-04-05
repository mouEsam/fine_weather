package com.iti.fineweather.features.weather.repositories

import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.weather.models.RemoteWeatherResponse
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.hilt.android.scopes.ActivityRetainedScoped
import java.util.*
import javax.inject.Inject

@ActivityRetainedScoped
class WeatherRepository @Inject constructor(private val weatherRemoteService: WeatherRemoteService) {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        locale: Locale,
    ): Resource<RemoteWeatherResponse> {
        return try {
            val response = weatherRemoteService.getWeather(
                latitude = latitude,
                longitude = longitude,
                language = locale.language,
            )
            Resource.Success.Remote(response)
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
