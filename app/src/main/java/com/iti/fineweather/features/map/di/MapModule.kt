package com.iti.fineweather.features.map.di

import android.content.Context
import android.location.Geocoder
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.*
import com.iti.fineweather.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MapModule {

    @Provides
    @Singleton
    @MapApiKey
    fun provideMapApiKey(): String {
        return BuildConfig.MAPS_API_KEY
    }

    @Provides
    @Singleton
    fun providePlacesClient(
        @MapApiKey apiKey: String,
        @ApplicationContext context: Context,
    ): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        return Places.createClient(context)
    }

    @Provides
    @Singleton
    fun provideGeocoder(
        @ApplicationContext context: Context,
    ): Geocoder {
        return Geocoder(context)
    }

}
