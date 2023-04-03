package com.iti.fineweather.features.settings.di

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(ActivityRetainedComponent::class)
object SettingsModule {
    @Provides
    fun provideFusedLocation(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

}
