package com.iti.fineweather.features.common.di

import android.content.Context
import androidx.room.Room
import com.google.gson.*
import com.iti.fineweather.features.common.services.local.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourcesModule {

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
}
