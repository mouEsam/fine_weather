package com.iti.fineweather.features.common.di

import android.content.Context
import android.location.Geocoder
import androidx.room.Room
import com.google.gson.*
import com.iti.fineweather.features.common.services.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Singleton

@Module
@InstallIn(ActivityRetainedComponent::class)
object LocationModule {

    @Provides
    @LocalDatabase
    fun provideDatabaseFileName(): String {
        return "weather_db"
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        @LocalDatabase fileName: String
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java, fileName
        ).build()
    }

    @Provides
    @ActivityRetainedScoped
    fun provideGeocoder(
        @ApplicationContext context: Context,
    ): Geocoder {
        return Geocoder(context)
    }
}
