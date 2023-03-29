package com.iti.fineweather.features.weather.services.remote

import com.iti.fineweather.features.weather.models.RemoteWeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRemoteService {
    @GET("onecall")
    suspend fun getWeather(
        @Query(value = "lat") latitude: Double,
        @Query(value = "lon") longitude: Double,
        @Query(value = "units") units: String? = null,
        @Query(value = "lang") language: String? = null,
        @Query(value = "exclude") excludes: List<String>? = null,
    ): RemoteWeatherResponse
}