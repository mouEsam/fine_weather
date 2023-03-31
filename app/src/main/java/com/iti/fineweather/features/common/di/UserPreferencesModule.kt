package com.iti.fineweather.features.common.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.Serializer
import androidx.datastore.dataStoreFile
import com.google.gson.*
import com.iti.fineweather.features.common.helpers.UserPreferencesSerializer
import com.iti.fineweather.features.settings.models.UserPreferences
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface UserPreferencesModule {

    @Binds
    abstract fun provideUserPreferencesSerializer(serializer: UserPreferencesSerializer): Serializer<UserPreferences>

    companion object {
        @Provides
        @UserPreferencesStore
        fun provideUserPreferencesFileName(): String {
            return "user_preferences"
        }

        @Provides
        @Singleton
        fun providePreferencesStore(@ApplicationContext context: Context,
                                    coroutineScope: CoroutineScope,
                                    @UserPreferencesStore fileName: String,
                                    serializer: Serializer<UserPreferences>): DataStore<UserPreferences> {
            return DataStoreFactory.create(
                serializer = serializer,
                produceFile = { context.dataStoreFile(fileName) },
                corruptionHandler = null,
                migrations = listOf(),
                scope = coroutineScope
            )
        }
    }
}
