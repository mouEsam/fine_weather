package com.iti.fineweather.features.alerts.di

import android.content.Context
import androidx.work.WorkManager
import com.google.gson.*
import com.iti.fineweather.features.alerts.services.local.WeatherAlertsDAO
import com.iti.fineweather.features.common.services.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AlertsModule {

    @Provides
    fun provideWorkManager(@ApplicationContext context: Context): WorkManager {
        return WorkManager.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideWeatherAlertsDAO(appDatabase: AppDatabase): WeatherAlertsDAO {
        return appDatabase.weatherAlertsDao()
    }
}
