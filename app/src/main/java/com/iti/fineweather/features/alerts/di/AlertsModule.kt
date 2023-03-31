package com.iti.fineweather.features.alerts.di

import com.google.gson.*
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsDAO
import com.iti.fineweather.features.common.services.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlertsModule {
    @Provides
    @Singleton
    fun provideWeatherAlertsDAO(appDatabase: AppDatabase): WeatherAlertsDAO {
        return appDatabase.weatherAlertsDao()
    }
}
