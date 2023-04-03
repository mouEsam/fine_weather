package com.iti.fineweather.features.map.di

import android.content.Context
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.gson.*
import com.iti.fineweather.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped

@Module
@InstallIn(ActivityRetainedComponent::class)
object MapModule {

    @Provides
    @MapApiKey
    fun provideMapApiKey(): String {
        return BuildConfig.MAPS_API_KEY
    }

    @Provides
    @ActivityRetainedScoped
    fun providePlacesClient(
        @MapApiKey apiKey: String,
        @ApplicationContext context: Context,
    ): PlacesClient {
        if (!Places.isInitialized()) {
            Places.initialize(context, apiKey)
        }
        return Places.createClient(context)
    }



}
