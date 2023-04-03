package com.iti.fineweather.features.bookmarks.di

import com.google.gson.*
import com.iti.fineweather.features.bookmarks.services.local.PlaceBookmarksDAO
import com.iti.fineweather.features.common.services.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BookmarksModule {
    @Provides
    @Singleton
    fun provideBookmarksDAO(appDatabase: AppDatabase): PlaceBookmarksDAO {
        return appDatabase.placeBookmarksDao()
    }
}
