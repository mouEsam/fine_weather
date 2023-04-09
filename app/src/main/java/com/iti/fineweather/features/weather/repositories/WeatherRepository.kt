package com.iti.fineweather.features.weather.repositories

import com.iti.fineweather.R
import com.iti.fineweather.core.di.IODispatcher
import com.iti.fineweather.core.helpers.InternetFetchException
import com.iti.fineweather.core.helpers.Resource
import com.iti.fineweather.features.weather.models.RemoteWeatherResponse
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeatherRepository @Inject constructor(
    private val weatherRemoteService: WeatherRemoteService,
    @IODispatcher val ioDispatcher: CoroutineDispatcher,
) {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double,
        locale: Locale,
    ): Resource<RemoteWeatherResponse> {
        return try {
            val response = withContext(ioDispatcher) {
                weatherRemoteService.getWeather(
                    latitude = latitude,
                    longitude = longitude,
                    language = locale.language,
                )
            }
            Resource.Success.Remote(response)
        } catch (e: IOException) {
            Resource.Error(InternetFetchException(R.string.weather_data))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }
}
