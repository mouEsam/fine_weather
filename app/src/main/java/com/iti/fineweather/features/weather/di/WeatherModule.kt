package com.iti.fineweather.features.weather.di

import com.google.gson.*
import com.iti.fineweather.BuildConfig
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Provides
    @Singleton
    @WeatherUrl
    fun provideWeatherUrl(): String {
        return "https://api.openweathermap.org/data/2.5/"
    }

    @Provides
    @Singleton
    @WeatherApiKey
    fun provideWeatherApiKey(): String {
        return BuildConfig.WEATHER_API_KEY
    }

    @Provides
    @Singleton
    @WeatherApiKey
    fun provideWeatherApiKeyInterceptor(@WeatherApiKey apiKey: String): Interceptor {
        return Interceptor { chain ->
            val newRequest = chain.request().run {
                newBuilder()
                    .url(url.newBuilder()
                        .addQueryParameter("appid", apiKey)
                        .build())
                    .build()
            }
            chain.proceed(newRequest)
        }
    }

    @Provides
    @WeatherApiKey
    fun provideOkHttpClient(
        @WeatherApiKey apiKeyInterceptor: Interceptor,
        interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        Timber.d(interceptors.toString())
        Timber.d(interceptors.toString())
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .apply {
                for (interceptor in interceptors) {
                    addInterceptor(interceptor)
                }
            }.build()
    }

    @Provides
    @Singleton
    @WeatherUrl
    fun provideRetrofit(
        @WeatherUrl baseUrl: String,
        gsonConverterFactory: GsonConverterFactory,
        @WeatherApiKey okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(@WeatherUrl retrofit: Retrofit): WeatherRemoteService {
        return retrofit.create(WeatherRemoteService::class.java)
    }
}
