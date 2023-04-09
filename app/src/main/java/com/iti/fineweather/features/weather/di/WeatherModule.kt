package com.iti.fineweather.features.weather.di

import android.content.Context
import com.google.gson.*
import com.iti.fineweather.BuildConfig
import com.iti.fineweather.core.utils.ConnectionState
import com.iti.fineweather.core.utils.currentConnectivityState
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
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
                    .url(
                        url.newBuilder()
                            .addQueryParameter("appid", apiKey)
                            .build()
                    )
                    .build()
            }
            chain.proceed(newRequest)
        }
    }

    @Provides
    @Singleton
    @WeatherCache
    fun provideWeatherCacheInterceptor(@ApplicationContext context: Context): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            request = if (context.currentConnectivityState is ConnectionState.Available) {
                request.newBuilder().header("Cache-Control", "public, max-age=" + 1).build()
            } else {
                val now = LocalTime.now()
                val startOfDay = LocalDate.now().atStartOfDay().toLocalTime()
                val difference = Duration.between(startOfDay, now).toMillis() / 1000
                request.newBuilder().header(
                    "Cache-Control",
                    "public, only-if-cached, max-stale=$difference"
                ).build()
            }
            chain.proceed(request)
        }
    }

    @Provides
    @WeatherApiKey
    fun provideOkHttpClient(
        @ApplicationContext context: Context,
        @WeatherApiKey apiKeyInterceptor: Interceptor,
        @WeatherCache cacheInterceptor: Interceptor,
        interceptors: Set<@JvmSuppressWildcards Interceptor>
    ): OkHttpClient {
        val cacheSize = (5 * 1024 * 1024).toLong()
        val cache = Cache(context.cacheDir, cacheSize)
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(cacheInterceptor)
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
