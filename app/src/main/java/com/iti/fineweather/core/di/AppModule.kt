package com.iti.fineweather.core.di

import com.google.gson.*
import com.iti.fineweather.features.weather.helpers.TemperatureDeserializer
import com.iti.fineweather.features.weather.models.Temperature
import com.iti.fineweather.features.weather.services.remote.WeatherRemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


// TODO: refactor and split

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideGsonBuilder(): GsonBuilder {
        return GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .registerTypeAdapter(Temperature::class.java, TemperatureDeserializer())
    }

    @Provides
    @Singleton
    fun provideGson(builder: GsonBuilder): Gson {
        return builder.create()
    }

    @Provides
    @Singleton
    fun provideGsonConvertorFactory(gson: Gson): GsonConverterFactory {
        return GsonConverterFactory.create(gson)
    }

    @Provides
    @Singleton
    @BaseUrl
    fun provideWeatherUrl(): String {
        return "https://api.openweathermap.org/data/2.5/"
    }

    @Provides
    @Singleton
    @ApiKey
    fun provideWeatherApiKey(): String {
        return "4020f82d313de8db8252dc835398f48e"
    }

    @Provides
    @Singleton
    @IntoSet
    fun provideLoggingInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS)
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        return loggingInterceptor
    }

    @Provides
    @Singleton
    @IntoSet
    fun provideApiKeyInterceptor(@ApiKey apiKey: String): Interceptor {
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
    @Singleton
    fun provideOkHttpClient(interceptors: Set<@JvmSuppressWildcards Interceptor>): OkHttpClient {
        return OkHttpClient.Builder().apply {
                for (interceptor in interceptors) {
                    addInterceptor(interceptor)
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @BaseUrl baseUrl: String,
        gsonConverterFactory: GsonConverterFactory,
        okHttpClient: OkHttpClient,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(gsonConverterFactory)
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideWeatherService(retrofit: Retrofit): WeatherRemoteService {
        return retrofit.create(WeatherRemoteService::class.java)
    }
}
